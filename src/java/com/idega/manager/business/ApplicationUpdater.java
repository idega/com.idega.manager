/*
 * Created on Jan 20, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.idega.manager.business;

import java.io.IOException;
import java.util.logging.Level;
import com.idega.util.logging.LogFile;


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
		LogFile logFile = null;
		try {
			logFile = installer.getLogFile(); 
		}
		catch (IOException ex) {
			errorMessage = ex.getMessage();
			return false;
		}
		try {
			logFile.log(Level.INFO,  "Extracting bundle archives...");
			installer.extractBundleArchives();
			logFile.log(Level.INFO, "...extracting bundle archives finished");
		 	// not needed: installer.mergeFacesConfiguration();
			logFile.log(Level.INFO, "Merging library...");
			installer.mergeLibrary();
			logFile.log(Level.INFO, "...merging library  finished");
			// not needed: installer.mergeTagLibraries();
			logFile.log(Level.INFO,  "Merging bundles...");
			installer.mergeBundles();
			logFile.log(Level.INFO, "...merging bundles finished");
			logFile.log(Level.INFO,  "Merging web configuration...");
			installer.mergeWebConfiguration(); 
			logFile.log(Level.INFO, "...merging web configuration finished");
		}
		catch (IOException ex) {
			errorMessage = ex.getMessage();
			return false;
		}
		finally {
			logFile. close();
		}
		return true;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
}
