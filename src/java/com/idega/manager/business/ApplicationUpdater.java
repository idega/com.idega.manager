/*
 * Created on Jan 20, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.idega.manager.business;

import java.io.IOException;


/**
 * @author thomas
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ApplicationUpdater {
	
	private PomSorter pomSorter = null;
	
	private String errorMessage = null;
	
	public ApplicationUpdater(PomSorter pomSorter) {
		this.pomSorter = pomSorter;
	}
	
	public boolean installModules() {
		
		Installer installer = Installer.getInstance(pomSorter);
		try {
			installer.extractBundleArchives();
		 	installer.mergeFacesConfiguration();
			installer.mergeLibrary();
			installer.mergeTagLibraries();
			installer.mergeBundles();
			installer.mergeWebConfiguration(); 
		}
		catch (IOException ex) {
			errorMessage = ex.getMessage();
			return false;
		}
		return true;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
}
