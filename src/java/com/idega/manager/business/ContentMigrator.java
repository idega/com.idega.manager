package com.idega.manager.business;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import javax.jcr.nodetype.ConstraintViolationException;
import javax.servlet.http.HttpSession;

import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.builder.bean.AdvancedProperty;
import com.idega.core.business.DefaultSpringBean;
import com.idega.dwr.business.DWRAnnotationPersistance;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.manager.bean.MigrationProgress;
import com.idega.manager.view.ManagerViewManager;
import com.idega.repository.RepositoryService;
import com.idega.util.ArrayUtil;
import com.idega.util.CoreConstants;
import com.idega.util.IOUtil;
import com.idega.util.StringHandler;
import com.idega.util.StringUtil;

@Service(ContentMigrator.BEAN_NAME)
@Scope(BeanDefinition.SCOPE_SINGLETON)
@RemoteProxy(creator=SpringCreator.class, creatorParams={
	@Param(name="beanName", value=ContentMigrator.BEAN_NAME),
	@Param(name="javascript", value=ContentMigrator.DWR_OBJECT)
}, name=ContentMigrator.DWR_OBJECT)
public class ContentMigrator extends DefaultSpringBean implements DWRAnnotationPersistance {

	public static final String	BEAN_NAME = "contentMigrator",
								DWR_OBJECT = "ContentMigrator";

	@Autowired
	private RepositoryService repository;

	private Map<String, MigrationProgress> progress = new HashMap<String, MigrationProgress>();

	@RemoteMethod
	public AdvancedProperty doMigrate(String path) {
		IWResourceBundle iwrb = getResourceBundle(getBundle(ManagerViewManager.BUNDLE_IDENTIFIER));
		AdvancedProperty result = new AdvancedProperty(Boolean.FALSE.toString(), iwrb.getLocalizedString("path_not_provided", "Path is not provided"));
		if (StringUtil.isEmpty(path))
			return result;

		String sessionId = null;
		try {
			sessionId = getSession().getId();

			File oldRepository = new File(path);
			if (!oldRepository.exists()) {
				result.setValue(iwrb.getLocalizedString("repository_can_not_be_found", "Repository can not be found"));
				return result;
			}
			if (!oldRepository.canRead()) {
				result.setValue(iwrb.getLocalizedString("unable_to_read_from_repository", "Unable to read from repository"));
				return result;
			}

			String[] filesAndFolders = null;
			if ("store".equals(oldRepository.getName())) {
				File tmp = new File(oldRepository.getPath() + File.separator + "content");
				oldRepository = tmp;
			}
			if ("content".equals(oldRepository.getName()))
				filesAndFolders = oldRepository.list();

			boolean migrationResult = doMigrateFilesAndFolders(sessionId, oldRepository.getAbsolutePath(), filesAndFolders);
			if (!migrationResult) {
				result.setValue(iwrb.getLocalizedString("some_error_occurred_while_migrating", "Some error occured while migrating"));
				return result;
			}
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error migrating from repository: " + path, e);
			result.setValue(iwrb.getLocalizedString("unable_to_read_from_repository", "Unable to read from repository"));
			return result;
		} finally {
			progress.remove(sessionId);
		}

		result.setId(Boolean.TRUE.toString());
		result.setValue(iwrb.getLocalizedString("succesfully_migrated", "Successfully migrated"));
		return result;
	}

	@RemoteMethod
	public MigrationProgress getMigrationProgress(HttpSession session) {
		return progress.get(session.getId());
	}

	@RemoteMethod
	public void doClearMigrationFailures(HttpSession session) {
		MigrationProgress progress = this.progress.get(session.getId());
		if (progress != null)
			progress.doClearFailures();
	}

	private boolean doMigrateFilesAndFolders(String sessionId, String parentPath, String[] filesAndFolders) {
		if (ArrayUtil.isEmpty(filesAndFolders))
			return true;

		boolean success = true;
		MigrationProgress progressInfo = progress.get(sessionId);
		if (progressInfo == null) {
			progressInfo = new MigrationProgress();
			progress.put(sessionId, progressInfo);
		}
		for (String fileOrFolder: filesAndFolders) {
			File tmp = new File(parentPath + File.separator + fileOrFolder);
			if (!tmp.exists())
				continue;

			if (tmp.isDirectory())
				if (!doMigrateFilesAndFolders(sessionId, tmp.getAbsolutePath(), tmp.list()))
					return false;

			if (tmp.isFile()) {
				String name = tmp.getName();
				if (name.indexOf(CoreConstants.DOT) == -1) {
					getLogger().warning("Skipping file " + name);
					continue;
				}
				if (name.equals(".DS_Store"))
					continue;

				int lastUnderline = name.lastIndexOf(CoreConstants.UNDER);
				if (lastUnderline != -1)
					name = name.substring(0, lastUnderline);
				if (name.indexOf(CoreConstants.DOT) == -1 || name.endsWith(".article") || name.endsWith(".comment") || name.endsWith(".bundle")
						|| name.endsWith(".post") || name.endsWith(".rwtheme"))
					continue;

				String fileName = new StringBuilder(name).toString();
				int length = fileName.length();
				int newLength = StringHandler.removeCharacters(fileName, CoreConstants.DOT, CoreConstants.EMPTY).length();
				if (length - newLength > 1 && getBundle(fileName) != null) {
					continue;	//	Skipping bundle folders
				}

				String progress = null;
				InputStream stream = null;
				try {
					String path = tmp.getParent();
					int start = path.indexOf(CoreConstants.WEBDAV_SERVLET_URI);
					path = path.substring(start);

					progress = path.concat(File.separator).concat(fileName);
					progressInfo.setProgress(progress);

					stream = new FileInputStream(tmp);

					if (fileName.endsWith(".strings") ||									//	Localizations
						(fileName.endsWith(".xml") && path.indexOf("/pages/") != -1) ||		//	Page
						(fileName.endsWith(".xml") && path.indexOf(".article/") != -1))	{	//	Article
						if (!path.endsWith(CoreConstants.SLASH))
							path = path.concat(CoreConstants.SLASH);
						success = repository.updateFileContents(path + fileName, stream, true) != null;
					} else {
						success = repository.uploadFile(path, fileName, null, stream);
					}
					if (!success) {
						progressInfo.addFailure(progress);
						getLogger().warning("Failed to import file " + progress);
						return false;
					}
				} catch (ConstraintViolationException e) {
					getLogger().warning("Failed to import " + progress + ". Reason: " + e.getMessage());
					progressInfo.addFailure(progress);
				} catch (Exception e) {
					getLogger().log(Level.WARNING, "Error importing file: " + progress, e);
					progressInfo.addFailure(progress);
					return false;
				} finally {
					IOUtil.close(stream);
				}
			}
		}

		return success;
	}
}