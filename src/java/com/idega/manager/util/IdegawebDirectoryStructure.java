/*
 * Created on Jan 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.idega.manager.util;

import java.io.File;
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
	
	private static final String WORKING_DIRRECTORY = "auxiliaryManagerFolder";
	private static final String APPLICATION = "application";
	private static final String WEB_INF = "WEB_INF";
	private static final String LIBRARY = "library";
	private static final String TAG_LIBRARY ="tagLibrary";
	private static final String FACES_CONFIG_FILE ="facesConfig";
	private static final String DEPLOYMENT_DESCRIPTOR_FILE = "web";
	
	private static final String APPLICATION_SPECIAL = "applicationSpecial";
	private static final String BUNDLES = "bundles";

	private IWContext context = null;
	private Map pathes = null;

	public IdegawebDirectoryStructure(IWContext context) {
		this.context = context;
	}
	
	
	public File getFacesConfig() {
		return getPath(FACES_CONFIG_FILE);
	}
	
	public File getBundlesRealPath()	{
		return getPath(BUNDLES);
	}
	
	
	public File getLibrary() {
		return getPath(LIBRARY);
	}
	
	public File getTagLibrary()	{
		return getPath(TAG_LIBRARY);
	}
	
	public File getWorkingDirectory() {
		File workingDir = getPath(WORKING_DIRRECTORY);
		if (! workingDir.exists()) {
			workingDir.mkdir();
		}
		return workingDir;
	}	
	
	
	
	
	public File getFacesConfig(Module module) throws IOException {
		File webInf = getWebInf(module);
		return new File(webInf, "faces-config.xml");
	}
	
	public File getLibrary(Module module) throws IOException {
		File webInf = getWebInf(module);
		return new File(webInf, "lib");
	}
	
	public File getTagLibrary(Module module) throws IOException {
		File webInf = getWebInf(module);
		return getTagLibraryFromWebInf(webInf);
	}
	
	public File getTagLibrary(File bundleFolder) {
		File webInf = getWebInf(bundleFolder);
		return getTagLibraryFromWebInf(webInf);
	}
	
	private File getWebInf(Module module) throws IOException {
		File extractedArchive = getExtractedArchive(module);
		return getWebInf(extractedArchive);
	}
	
	private File getWebInf(File bundleFolder) {
		return new File(bundleFolder, "WEB-INF");
	}
	
	private File getTagLibraryFromWebInf(File webInf) {
		return new File(webInf, "tld");
	}
	
	public File getExtractedArchive(Module module) throws IOException {
		File temporaryInstallationFolder = module.getBundleArchive().getParentFile();
		String artifactId = module.getArtifactId();
		File extractedArchive = new File (temporaryInstallationFolder, artifactId);
		if (! extractedArchive.exists()) {
			extractedArchive.mkdir();
			File bundleArchive = module.getBundleArchive();
			ZipInstaller zipInstaller = new ZipInstaller();
			zipInstaller.extract(bundleArchive, extractedArchive);
		}
		return extractedArchive;
	}
	
	private File getPath(String key) {
		if (pathes == null) {
			initializePathes();
		}
		return (File) pathes.get(key);
	}
	
	private void initializePathes() {
		pathes = new HashMap();
		
		IWMainApplication mainApplication = context.getIWMainApplication();
		File application = new File(mainApplication.getApplicationRealPath());
		pathes.put(APPLICATION, application);
		
		File bundles = new File(mainApplication.getBundlesRealPath());
		pathes.put(BUNDLES, bundles);
		
		File applicationSpecial = new File(mainApplication.getApplicationSpecialRealPath());
		pathes.put(APPLICATION_SPECIAL, applicationSpecial);
		pathes.put(WORKING_DIRRECTORY, new File(applicationSpecial, "auxiliaryManagerFolder"));
		
		File webInf =  new File(application, "WEB-INF");
		pathes.put(WEB_INF, webInf);
		pathes.put(FACES_CONFIG_FILE, new File(webInf, "faces-config.xml"));
		pathes.put(DEPLOYMENT_DESCRIPTOR_FILE, new File(webInf, "web.xml"));
		pathes.put(LIBRARY, new File(webInf, "lib"));
		pathes.put(TAG_LIBRARY, new File(webInf, "tld"));
	}
}
