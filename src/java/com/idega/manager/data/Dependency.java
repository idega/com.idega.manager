/*
 * $Id: Dependency.java,v 1.7 2004/12/08 12:47:55 thomas Exp $
 * Created on Nov 19, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.manager.data;

import java.io.File;
import java.io.IOException;
import com.idega.util.StringHandler;
import com.idega.xml.XMLElement;


/**
 * 
 *  Last modified: $Date: 2004/12/08 12:47:55 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.7 $
 */
public class Dependency implements Module  {
	
	private static final String GROUP_ID = "groupId";
	private static final String VERSION = "version";
	
	public static Dependency getInstanceForElement(Pom dependant, XMLElement element) {
		String tempGroupId = element.getTextTrim(GROUP_ID);
		Dependency dependency = null;
		if (DependencyPomBundle.GROUP_ID_BUNDLE.equalsIgnoreCase(tempGroupId))	{
			dependency = new DependencyPomBundle();
		}
		else {
			dependency = new Dependency();
		}
		String tempArtifactId = element.getTextTrim(RealPom.ARTIFACT_ID);
		String tempVersion = element.getTextTrim(VERSION);
		dependency.setDependantPom(dependant);
		dependency.setIsInstalled(dependant.isInstalled());
		dependency.setGroupId(tempGroupId);
		dependency.setArtifactId(tempArtifactId);
		dependency.setVersion(tempVersion);
		return dependency;
	}
	
	String groupId = null;
	String artifactId = null;
	String version = null;
	
	boolean isInstalled = false;
	
	Pom dependantPom = null;
	
	public String getArtifactId() {
		return artifactId;
	}
	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}
	public Pom getDependantPom() {
		return dependantPom;
	}
	public void setDependantPom(Pom dependantPom) {
		this.dependantPom = dependantPom;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}

	public boolean isInstalled() {
		return isInstalled;
	}

	public void setIsInstalled(boolean isInstalled) {
		this.isInstalled = isInstalled;
	}

	public String getCurrentVersion() {
		return getVersion();
	}
	
	public Pom getPom() throws IOException {
		return null;
	}
	
	public File getBundleArchive() throws IOException  {
		return null;
	}
	
	public boolean isIncluded() {
		return true;
	}
	
	public boolean isSnapshot() {
		return false;
	}
	
	
	public int compare(Pom pom)	{
		// not supported, it has never the same group id
		return -1;
	}
	
	public int compare(DependencyPomBundle dependencyPomBundle) {
		// not supported, it has never the same group id
		return -1;
	}
	
	public int compare(Dependency dependency)	{
		String version1 = getCurrentVersion();
		String version2 = dependency.getCurrentVersion();
		return StringHandler.compareVersions(version1, version2);
	}
	
	// you can only compare a dependency with another dependency
	public int compare(Module module) {
		// change algebraic sign of returned result
		return - (module.compare(this));
	}
}
