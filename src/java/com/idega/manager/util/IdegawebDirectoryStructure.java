/*
 * Created on Jan 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.idega.manager.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.idega.idegaweb.IWMainApplication;
import com.idega.io.ZipInstaller;
import com.idega.manager.data.Module;
import com.idega.presentation.IWContext;

/**
 * @author thomas
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class IdegawebDirectoryStructure {
	
	// keys
	private static final String BACKUP_FOLDER_KEY = "backupFolderKey";
	private static final String WORKING_DIRECTORY_KEY= "auxiliaryManagerFolder";
	private static final String APPLICATION_KEY = "application";
	private static final String WEB_INF_KEY = "WEB_INF";
	private static final String LIBRARY_KEY = "library";
	private static final String TAG_LIBRARY_KEY ="tagLibrary";
	private static final String FACES_CONFIG_FILE_KEY ="facesConfig";
	private static final String DEPLOYMENT_DESCRIPTOR_FILE_KEY = "web";
	private static final String APPLICATION_SPECIAL_KEY = "applicationSpecial";
	private static final String BUNDLES_KEY = "bundles";
	
	// real existing folders or files
	private static final String BACKUP_FOLDER = "backupManager";
	private static final String WORKING_FOLDER = "auxiliaryManager";
	private static final String FACES_CONFIG_FILE = "faces-config.xml";
	private static final String WEB_DEPLOYMENT_FILE = "web.xml";
	private static final String WEB_INF_FOLDER = "WEB-INF";
	private static final String WEB_LIBRARY_FOLDER = "lib";
	private static final String WEB_TAG_LIBRARY_FOLDER = "tld";

	private IWContext context = null;
	private Map pathes = null;

	public IdegawebDirectoryStructure(IWContext context) {
		this.context = context;
	}
	
	public File getDeploymentDescriptor() {
		return getPath(DEPLOYMENT_DESCRIPTOR_FILE_KEY);
	}
	
	public File getFacesConfig() {
		return getPath(FACES_CONFIG_FILE_KEY);
	}
	
	public File getBundlesRealPath()	{
		return getPath(BUNDLES_KEY);
	}
	
	
	public File getLibrary() {
		return getPath(LIBRARY_KEY);
	}
	
	public File getTagLibrary()	{
		return getPath(TAG_LIBRARY_KEY);
	}
	
	public File getBackupDirectory() {
		File backupDirectory = getPath(BACKUP_FOLDER_KEY);
		if (! backupDirectory.exists()) {
			backupDirectory.mkdir();
		}
		return backupDirectory;
	}
	
	
	public File getWorkingDirectory() {
		File workingDir = getPath(WORKING_DIRECTORY_KEY);
		if (! workingDir.exists()) {
			workingDir.mkdir();
		}
		return workingDir;
	}	
	
	public File getDeploymentDescriptor(Module module) throws IOException {
		File webInf = getWebInf(module);
		return new File(webInf, WEB_DEPLOYMENT_FILE);
		
	}
	
	public File getFacesConfig(Module module) throws IOException {
		File webInf = getWebInf(module);
		return new File(webInf, FACES_CONFIG_FILE);
	}
	
	public File getLibrary(Module module) throws IOException {
		File webInf = getWebInf(module);
		return new File(webInf, WEB_LIBRARY_FOLDER);
	}
	
	public File getTagLibrary(Module module) throws IOException {
		File webInf = getWebInf(module);
		return getTagLibraryFromWebInf(webInf);
	}
	
	public File getTagLibrary(File bundleFolder) {
		File webInf = getWebInf(bundleFolder);
		return getTagLibraryFromWebInf(webInf);
	}
	
	public File getExtractedArchive(Module module) throws IOException {
		File bundleArchive = module.getBundleArchive();
		File temporaryInstallationFolder = bundleArchive.getParentFile();
		String artifactId = module.getArtifactId();
		File extractedArchive = new File (temporaryInstallationFolder, artifactId);
		if (! extractedArchive.exists()) {
			extractedArchive.mkdir();
			ZipInstaller zipInstaller = new ZipInstaller();
			zipInstaller.extract(bundleArchive, extractedArchive);
			String bundleArchiveName = bundleArchive.getName();
			// write the name of the source file into the origin file
			File originFile = new File(extractedArchive, ManagerConstants.ORIGIN_FILE);
			// check first if file exist to avoid an exception
			if (! originFile.exists()) {
				FileWriter writer = null;
				try {
					originFile.createNewFile();
					writer = new FileWriter(originFile);
					writer.write(bundleArchiveName);
				}
				finally {
					writer.close();
				}
			}
		}
		return extractedArchive;
	}
	
	public File getCorrespondingFileFromWebInf(Module module, File fileInWebInf) throws IOException {
		String name = fileInWebInf.getName();
		File webInf = getWebInf(module);
		return new File(webInf, name);
		
	}
	
	private File getWebInf(Module module) throws IOException {
		File extractedArchive = getExtractedArchive(module);
		return getWebInf(extractedArchive);
	}
	
	private File getWebInf(File bundleFolder) {
		return new File(bundleFolder, WEB_INF_FOLDER);
	}
	
	private File getTagLibraryFromWebInf(File webInf) {
		return new File(webInf, WEB_TAG_LIBRARY_FOLDER);
	}
	
	private File getPath(String key) {
		if (this.pathes == null) {
			initializePathes();
		}
		return (File) this.pathes.get(key);
	}
	
	private void initializePathes() {
		this.pathes = new HashMap();
		
		IWMainApplication mainApplication = this.context.getIWMainApplication();
		File application = new File(mainApplication.getApplicationRealPath());
		this.pathes.put(APPLICATION_KEY, application);
		
		File bundles = new File(mainApplication.getBundlesRealPath());
		this.pathes.put(BUNDLES_KEY, bundles);
		
		File applicationSpecial = new File(mainApplication.getApplicationSpecialRealPath());
		this.pathes.put(APPLICATION_SPECIAL_KEY, applicationSpecial);
		this.pathes.put(WORKING_DIRECTORY_KEY, new File(applicationSpecial, WORKING_FOLDER));
		this.pathes.put(BACKUP_FOLDER_KEY, new File(applicationSpecial, BACKUP_FOLDER));
		
		File webInf =  new File(application, WEB_INF_FOLDER);
		this.pathes.put(WEB_INF_KEY, webInf);
		this.pathes.put(FACES_CONFIG_FILE_KEY, new File(webInf, FACES_CONFIG_FILE));
		this.pathes.put(DEPLOYMENT_DESCRIPTOR_FILE_KEY, new File(webInf, WEB_DEPLOYMENT_FILE));
		this.pathes.put(LIBRARY_KEY, new File(webInf, WEB_LIBRARY_FOLDER));
		this.pathes.put(TAG_LIBRARY_KEY, new File(webInf, WEB_TAG_LIBRARY_FOLDER));
	}
}
