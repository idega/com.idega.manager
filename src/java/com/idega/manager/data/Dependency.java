/*
 * $Id: Dependency.java,v 1.13 2005/03/31 15:47:38 thomas Exp $
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
import com.idega.idegaweb.IWResourceBundle;
import com.idega.manager.util.VersionComparator;
import com.idega.xml.XMLElement;


/**
 * 
 *  Last modified: $Date: 2005/03/31 15:47:38 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.13 $
 */
public class Dependency extends ModulePomImpl  {
	
	private static final String GROUP_ID = "groupId";
	private static final String VERSION = "version";
	private static final String JAR = "jar";
	private static final String PROPERTIES = "properties";
	private static final String INCLUDED_IN_BUNDLE = "idegaweb.bundle.include";
	
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
		// sometimes version is not set, set version to an empty string
		if (tempVersion == null) {
			tempVersion = ""; 
		}
		String tempJarFileName = element.getTextTrim(JAR);
		// note: in most cases this string will be null
		XMLElement propertiesElement = element.getChild(PROPERTIES);
		if (propertiesElement != null) {
			String tempIncludedInBundle = propertiesElement.getTextTrim(INCLUDED_IN_BUNDLE);
			if (tempIncludedInBundle != null) {
				// note: Boolean.getBoolean(String) does not work properly
				dependency.setIncludedInBundle((new Boolean(tempIncludedInBundle)).booleanValue());
			}
		}
		dependency.setJarFileName(tempJarFileName);
		dependency.setDependantPom(dependant);
		dependency.setIsInstalled(dependant.isInstalled());
		dependency.setGroupId(tempGroupId);
		dependency.setArtifactId(tempArtifactId);
		dependency.setCurrentVersion(tempVersion);
		return dependency;
	}
	
	String groupId = null;
	String artifactId = null;
	String version = null;
	String jarFileName = null;
	
	boolean includedInBundle = false;
	
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
	public String getCurrentVersion() {
		return version;
	}
	public void setCurrentVersion(String version) {
		this.version = version;
	}

	public boolean isInstalled() {
		return isInstalled;
	}

	public void setIsInstalled(boolean isInstalled) {
		this.isInstalled = isInstalled;
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

	public int compare(Pom pom, VersionComparator versionComparator)	throws IOException{
		// not supported, it has never the same group id
		return -1;
	}
	
	public int compare(DependencyPomBundle dependencyPomBundle, VersionComparator versionComparator) throws IOException {
		// not supported, it has never the same group id
		return -1;
	}
	
	public int compare(Dependency dependency, VersionComparator versionComparator) {
		String version1 = getCurrentVersion();
		String version2 = dependency.getCurrentVersion();
		int result = versionComparator.compare(version1, version2);
		// if both are equal the installed one wins
		if (result == 0) {
			if (isInstalled() && dependency.isInstalled()) {
				return 0;
			}
			if (isInstalled()) {
				return 1;
			}
			if (dependency.isInstalled()) {
				return -1;
			}
		}
		return result;
		
	}
	
	// you can only compare a dependency with another dependency
	public int compare(Module module, VersionComparator versionComparator) throws IOException {
		// change algebraic sign of returned result
		return - (module.compare(this, versionComparator));
	}
	
	public String getCurrentVersionForLabel(IWResourceBundle resourcreBundle) {
		return getCurrentVersion();
	}
	
	public String getNameForLabel(IWResourceBundle resourceBundle) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getArtifactId());
		buffer.append(" (");
		buffer.append(getGroupId());
		buffer.append(")");
		return buffer.toString();
	}
	public String getJarFileName() {
		return jarFileName;
	}
	public void setJarFileName(String jarFileName) {
		this.jarFileName = jarFileName;
	}
	public static String getINCLUDED_IN_BUNDLE() {
		return INCLUDED_IN_BUNDLE;
	}
	public boolean isIncludedInBundle() {
		return includedInBundle;
	}
	public void setIncludedInBundle(boolean includedInBundle) {
		this.includedInBundle = includedInBundle;
	}
}
