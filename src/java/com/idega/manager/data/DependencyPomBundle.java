/*
 * $Id: DependencyPomBundle.java,v 1.8 2005/02/23 18:02:17 thomas Exp $
 * Created on Dec 1, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.manager.data;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.manager.util.ManagerUtils;
import com.idega.util.StringHandler;


/**
 * 
 *  Last modified: $Date: 2005/02/23 18:02:17 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.8 $
 */
public class DependencyPomBundle extends Dependency {
	
	public static final String GROUP_ID_BUNDLE = "bundles";
	
	static private Logger getLogger(){
		 return Logger.getLogger(ManagerUtils.class.getName());
	 }
	
	Boolean isSnapshot = null;
	
	Pom pom = null;
	
	File bundleArchive = null;
	
	DependencyPomBundle() {
		// use the class method of Dependency
	}
	
	public Pom getPom() throws IOException {
		if (pom == null) {
			pom = getDependantPom().getPom(this);
		}
		return pom;
	}
	
	public File getBundleArchive() throws IOException {
		if (bundleArchive == null) {
			bundleArchive = getDependantPom().getBundleArchive(this);
		}
		return bundleArchive;
	}
	
	
	public boolean isIncluded() {
		return false;
	}
	
	public int compare(Module module) {
		// change algebraic sign of returned result
		return - (module.compare(this));
	}
	
	public int compare(Dependency dependency) {
		// not supported, it has never the same group id
		return -1;
	}
	
	public int compare(DependencyPomBundle dependencyPomBundle) {
		if (isSnapshot() && dependencyPomBundle.isSnapshot()) {
			// snapShot that is not installed wins
			if (isInstalled() && dependencyPomBundle.isInstalled()) {
				return 0;
			}
			if (isInstalled()) {
				// that is dependencyPomBundle is not installed
				return -1;
			}
			// that is this is not installed
			return 1;
		}
		else if (isSnapshot()) {
			return 1;
		}
		else if (dependencyPomBundle.isSnapshot()) {
			return -1;
		}
		String version1 = getCurrentVersion();
		String version2 = dependencyPomBundle.getCurrentVersion();
		int result = StringHandler.compareVersions(version1, version2);
		// if both are equal the installed one wins
		if (result == 0) {
			if (isInstalled() && dependencyPomBundle.isInstalled()) {
				return 0;
			}
			if (isInstalled()) {
				return 1;
			}
			if (dependencyPomBundle.isInstalled()) {
				return -1;
			}
		}
		return result;
	}
	
	public int compare(Pom aPom) {
		if (isSnapshot() || aPom.isSnapshot()) {
			Pom tempPom = null;
			try {
				tempPom = getPom();
			}
			catch (IOException e) {
		       	getLogger().log(Level.WARNING, "[RepositoryBrowser] Could not get Pom of " + getArtifactId() , e);
		       	return 0;
			}
			return tempPom.compare(aPom);
		}
		String version1 = getCurrentVersion();
		String version2 = aPom.getCurrentVersion();
		int result = StringHandler.compareVersions(version1, version2);
		// if both are equal the installed one wins
		if (result == 0) {
			if (isInstalled() && aPom.isInstalled()) {
				return 0;
			}
			if (isInstalled()) {
				return 1;
			}
			if (aPom.isInstalled()) {
				return -1;
			}
		}
		return result;
			
	}
	
	
	public boolean isSnapshot() {
		if (isSnapshot == null) {
			String tempVersion = getCurrentVersion();
			isSnapshot = new Boolean(RealPom.isSnapshot(tempVersion));
		}
		return isSnapshot.booleanValue();
	}

	public String getCurrentVersionForLabel(IWResourceBundle resourceBundle) {
		Pom tempPom = null;
		try {
			tempPom = getPom();
		}
		catch (IOException ex) {
			String problem = resourceBundle.getLocalizedString("man_manager_could_not_ figure_out_version","Could not figure out version");
			StringBuffer buffer = new StringBuffer();
			buffer.append(problem);
			buffer.append(" ");
			buffer.append(getCurrentVersion());
			return buffer.toString();
		}
		return tempPom.getCurrentVersionForLabel(resourceBundle);
	}
	
	public String getNameForLabel(IWResourceBundle resourceBundle) {
		Pom tempPom = null;
		try {
			tempPom = getPom();
		}
		catch (IOException ex) {
			String problem = resourceBundle.getLocalizedString("man_manager_could_not_ figure_out_name_for","Could not figure out name for");
			StringBuffer buffer = new StringBuffer();
			buffer.append(problem);
			buffer.append(" ");
			buffer.append(getArtifactId());
			buffer.append(" (");
			buffer.append(getGroupId());
			buffer.append(")");
			return buffer.toString();
		}
		return tempPom.getNameForLabel(resourceBundle);
	}

}
