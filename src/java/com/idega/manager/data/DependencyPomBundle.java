/*
 * $Id: DependencyPomBundle.java,v 1.3 2004/12/02 18:06:57 thomas Exp $
 * Created on Dec 1, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.manager.data;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.idega.manager.util.ManagerUtils;
import com.idega.util.StringHandler;


/**
 * 
 *  Last modified: $Date: 2004/12/02 18:06:57 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.3 $
 */
public class DependencyPomBundle extends Dependency {
	
	public static final String GROUP_ID_BUNDLE = "bundles";
	
	static private Logger getLogger(){
		 return Logger.getLogger(ManagerUtils.class.getName());
	 }
	
	Boolean isSnapshot = null;
	
	Pom pom = null;
	
	DependencyPomBundle() {
		// use the class method of Dependency
	}
	
	public Pom getPom() throws IOException {
		if (pom == null) {
			pom = getDependantPom().getPom(this);
		}
		return pom;
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
			return 0;
		}
		else if (isSnapshot()) {
			return 1;
		}
		else if (dependencyPomBundle.isSnapshot()) {
			return -1;
		}
		String version1 = getCurrentVersion();
		String version2 = dependencyPomBundle.getCurrentVersion();
		return StringHandler.compareVersions(version1, version2);
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
		return StringHandler.compareVersions(version1, version2);
	}
	
	
	public boolean isSnapshot() {
		if (isSnapshot == null) {
			String tempVersion = getVersion();
			isSnapshot = new Boolean(RealPom.isSnapshot(tempVersion));
		}
		return isSnapshot.booleanValue();
	}
	
}
