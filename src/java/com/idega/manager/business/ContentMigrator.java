package com.idega.manager.business;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.jcr.RepositoryException;
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
	private Map<String, String> FILES_TO_IGNORE = new HashMap<String, String>();

	public ContentMigrator() {
		super();

		FILES_TO_IGNORE.put("1.2.3", "jquery");
	}

	@RemoteMethod
	public AdvancedProperty doImport(String inputFile) {
		IWResourceBundle iwrb = getResourceBundle(getBundle(ManagerViewManager.BUNDLE_IDENTIFIER));
		String success = iwrb.getLocalizedString("successfully_imported_repository", "Successfully imported repository");
		String error = iwrb.getLocalizedString("error_importing_repository", "Error importing repository");

		try {
			if (repository.doImportWorkspace("default", inputFile))
				return new AdvancedProperty(Boolean.TRUE.toString(), success);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		return new AdvancedProperty(Boolean.FALSE.toString(), error);
	}

	@RemoteMethod
	public AdvancedProperty doExport(String outputFile) {
		IWResourceBundle iwrb = getResourceBundle(getBundle(ManagerViewManager.BUNDLE_IDENTIFIER));
		String success = iwrb.getLocalizedString("successfully_exported_repository", "Successfully exported repository");
		String error = iwrb.getLocalizedString("error_exporting_repository", "Error exporting repository");

		try {
			if (repository.doExportWorkspace("default", outputFile))
				return new AdvancedProperty(Boolean.TRUE.toString(), success);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		return new AdvancedProperty(Boolean.FALSE.toString(), error);
	}

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

	private boolean isInvalid(String path, String name) {
		String pathPart = FILES_TO_IGNORE.get(name);
		if (!StringUtil.isEmpty(pathPart) && path.indexOf(pathPart) != -1)
			return true;

		String lastPartOfName = name.substring(name.lastIndexOf(CoreConstants.DOT));
		if (StringUtil.isEmpty(lastPartOfName) || CoreConstants.DOT.equals(lastPartOfName))
			return true;

		if (lastPartOfName.startsWith(".rwtheme"))
			return true;

		return false;
	}

	private InputStream getStreamToTheFinalVersion(String path, final String fileName, File file) throws Exception {
		if (!isVersionable(path, fileName))
			return new FileInputStream(file);

		File[] versions = file.getParentFile().listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith(fileName);
			}
		});

		if (ArrayUtil.isEmpty(versions) || versions.length == 1) {
			getLogger().warning("Expected to find versions for file '" + fileName + "' at '" + path + "', but no versions found");
			return new FileInputStream(file);
		}

		List<File> fileVersions = new ArrayList<File>(Arrays.asList(versions));
		Map<String, File> allVersions = new HashMap<String, File>();
		List<Integer> minorVersions = new ArrayList<Integer>(fileVersions.size());
		List<String> versionNumbers = new ArrayList<String>(fileVersions.size());
		Integer majorVersion = null;
		for (File fileVersion: fileVersions) {
			String name = fileVersion.getName();
			allVersions.put(name, fileVersion);

			String versionNumber = name.substring(name.lastIndexOf(CoreConstants.UNDER) + 1);
			versionNumbers.add(versionNumber);

			String tmpMajorVersion = versionNumber.substring(0, versionNumber.indexOf(CoreConstants.DOT));
			String minorVersion = versionNumber.substring(versionNumber.indexOf(CoreConstants.DOT) + 1);
			try {
				majorVersion = Integer.valueOf(tmpMajorVersion);
				minorVersions.add(Integer.valueOf(minorVersion));
			} catch (Exception e) {
				getLogger().warning("Failed to extract version from " + versionNumber);
			}
		}
		Collections.sort(minorVersions, new java.util.Comparator<Integer>() {
			@Override
			public int compare(Integer version1, Integer version2) {
				return -1 * (version1.compareTo(version2));
			}
		});
		Integer latestMinorVersionNumber = minorVersions.get(0);
		String latestVersionNumber = String.valueOf(majorVersion) + CoreConstants.DOT + String.valueOf(latestMinorVersionNumber);
		String latestVersionFileName = fileName + CoreConstants.UNDER + latestVersionNumber;
		File latestVersion = allVersions.get(latestVersionFileName);
		if (latestVersion == null) {
			latestVersion = fileVersions.get(fileVersions.size() - 1);
			getLogger().warning("Failed to get file " + latestVersionFileName + " from " + allVersions + ". Using " + latestVersion);
		} else
			getLogger().info("Using the latest version (" + latestVersionNumber + ") from all versions: " + versionNumbers + " for file " + fileName);

		return new FileInputStream(latestVersion);
	}

	private boolean isVersionable(String path, String fileName) {
		return	fileName.endsWith(".strings") ||									//	Localizations
				(fileName.endsWith(".xml") && path.indexOf("/pages/") != -1) ||		//	Page
				(fileName.endsWith(".xml") && path.indexOf(".article/") != -1);		//	Article
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
				if (length - newLength > 1) {
					if (getBundle(fileName) != null)
						continue;	//	Skipping bundle folders

					String[] children = tmp.list();
					if (!ArrayUtil.isEmpty(children)) {
						getLogger().info("Skipping file " + tmp + " that is actualy a directory");
						continue;
					}
				}

				String path = null;
				String progress = null;
				InputStream stream = null;
				try {
					path = tmp.getParent();
					int start = path.indexOf(CoreConstants.WEBDAV_SERVLET_URI);
					path = path.substring(start);

					if (isInvalid(path, fileName)) {
						getLogger().info("Skipping invalid file " + tmp);
						continue;
					}

					if (!path.endsWith(File.separator))
						path = path.concat(File.separator);

					if (progressInfo.isFileCopied(path + fileName))
						continue;

					//	Doing actual copy
					progress = path.concat(fileName);
					progressInfo.setProgress(progress);

					stream = getStreamToTheFinalVersion(path, fileName, tmp);

					if (isVersionable(path, fileName)) {
						success = repository.updateFileContents(path + fileName, stream, true) != null;
					} else {
						success = repository.uploadFile(path, fileName, null, stream);
					}
					if (success) {
						progressInfo.doMarkFileAsCopied(path + fileName);
					} else {
						progressInfo.addFailure(progress);
						getLogger().warning("Failed to import file '" + fileName + "' at '" + path + "'");
						return false;
					}
				} catch (ConstraintViolationException e) {
					getLogger().warning("Failed to import file '" + fileName + "' at '" + path + "'. Reason: " + e.getMessage());
					progressInfo.addFailure(progress);
				} catch (Exception e) {
					getLogger().log(Level.WARNING, "Failed to import file '" + fileName + "' at '" + path + "'.", e);
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