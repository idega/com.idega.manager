/*
 * $Id: RealPom.java,v 1.3 2004/12/03 17:01:12 thomas Exp $
 * Created on Nov 15, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.manager.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import com.idega.util.FileUtil;
import com.idega.util.IWTimestamp;
import com.idega.util.StringHandler;
import com.idega.util.xml.XMLData;
import com.idega.xml.XMLElement;


/**
 * 
 *  Last modified: $Date: 2004/12/03 17:01:12 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.3 $
 */
public class RealPom extends Pom {
	
	public static final String BUNDLES_GROUP_ID = "bundles";	

	public static final String SNAPSHOT = "SNAPSHOT";
	
	public static final String BUNDLE_SUFFIX = ".bundle";
	
	public static final String POM_FILE = "project.xml";
	
	private static final String EXTEND = "extend";
	private static final String GROUP_ID = "groupId";
	static final String ARTIFACT_ID = "artifactId";
	private static final String CURRENT_VERSION = "currentVersion";
	private static final String DEPENDENCY = "dependency";
	
	
	private static final String MANIFEST_PATH = "./META-INF/MANIFEST.MF";
	private static final String BASE_PROJECT_PATH = "../com.idega.core.bundle";
	private static final String BASE_PROJECT_VARIABLE = "${base.project.dir}";
	
	public static RealPom getInstalledPomOfGroupBundles(File projectFile) throws IOException {
		RealPom pom = getPom(projectFile);
		pom.setIsInstalled(true);
		// save time....
		pom.groupId = BUNDLES_GROUP_ID;	
		return pom;
	}
	
	public static RealPom getPom(File projectFile) throws IOException {
		XMLData pomData = createXMLData(projectFile);
		RealPom pom =  new RealPom(pomData, projectFile);
		// get timestamp from corresponding MANIFEST_FILE  
		File manifestFile = FileUtil.getFileRelativeToFile(projectFile, MANIFEST_PATH);
		if (manifestFile.exists()) {
			long dateValue = manifestFile.lastModified();
			Date date = new Date(dateValue);
			IWTimestamp timestamp = new IWTimestamp(date);
			pom.setTimestamp(timestamp);
		}
		return pom;
	}
	
	public static XMLData createXMLData(File projectFile) throws IOException {
		XMLData pomData = XMLData.getInstanceForFile(projectFile);
		// handle extensions
		String superPom = pomData.getDocument().getRootElement().getText(EXTEND);
		//TODO thi: where can I fetch the value of the variable?
		if (superPom != null) {
			// replace if variable exists
			superPom = StringHandler.replace(superPom, BASE_PROJECT_VARIABLE, BASE_PROJECT_PATH);
			File superPomFile = FileUtil.getFileRelativeToFile(projectFile, superPom);
			if (superPomFile.exists()) {
				XMLData superPomData = createXMLData(superPomFile);
				// concat pomData and superPomData
				pomData.add(superPomData);
			}
		}
		return pomData;
	}
	
	public static boolean isSnapshot(String version) {
		return StringHandler.contains(version.toUpperCase(), SNAPSHOT);
	}
	
	boolean isInstalled = false;
	
	private File projectFile = null;
	
	private XMLData xmlData = null;
	private XMLElement root = null;
	
	private String groupId = null;
	private String artifactId = null;
	private String currentVersion = null;
	
	private boolean snapshot = false;
	private IWTimestamp timesstamp = null;
	
	private List dependencies = null;
	
	private RealPom(XMLData xmlData, File projectFile) {
		this.projectFile = projectFile;
		this.xmlData =xmlData ;
		initialize();
	}
	
	private void initialize() {
		// initilaize snapshot variable
		getCurrentVersion();
	}
	
	public void setTimestamp(IWTimestamp timestamp) {
		this.timesstamp = timestamp;
	}
	
	public String getGroupId() {
		if (groupId == null) {
			groupId = getRoot().getTextTrim(GROUP_ID);
		}
		return groupId;
	}
	
	public String getArtifactId() {
		if (artifactId == null) {
			artifactId = getRoot().getTextTrim(ARTIFACT_ID);
		}
		return artifactId;
	}
	
	public boolean isSnapshot() {
		return snapshot;
	}
	
	public String getCurrentVersion() {
		if (currentVersion == null) { 
			currentVersion = getRoot().getTextTrim(CURRENT_VERSION);
			snapshot = RealPom.isSnapshot(currentVersion);
			if (snapshot) {
				currentVersion = StringHandler.remove(currentVersion, SNAPSHOT);
				currentVersion = StringHandler.remove(currentVersion, "-");
			}
		}
		return currentVersion;
	}
	
	private XMLElement  getRoot() {
		if (root == null) {
			root = xmlData.getDocument().getRootElement();
		}
		return root;
	}

	public List getDependencies() {
		return getDependencies(this);
	}
	
	
	public List getDependencies(Pom dependant) {
		if (dependencies == null) {
			dependencies = new ArrayList();
			List elements = getRoot().getChildrenRecursive(DEPENDENCY);
			Iterator iterator = elements.iterator();
			while (iterator.hasNext()) {
				XMLElement element = (XMLElement) iterator.next();
				Dependency dependency = Dependency.getInstanceForElement(dependant, element);
				dependencies.add(dependency);
			}
		}
		return dependencies;
	}
	
	public Pom getPom(DependencyPomBundle dependency) throws IOException {
		String dependencyArtifactId = dependency.getArtifactId();
		String moduleName = StringHandler.concat(dependencyArtifactId, BUNDLE_SUFFIX);
		File bundlesFolder = projectFile.getParentFile().getParentFile();
		File module = new File(bundlesFolder, moduleName);
		File dependencyProjectFile = new File(module, RealPom.POM_FILE);
		Pom pom = RealPom.getPom(dependencyProjectFile);
		pom.setIsInstalled(isInstalled());
		return pom;
	}
	
	public File getBundleArchive(DependencyPomBundle dependency) {
		return null;
	}

	public IWTimestamp getTimestamp() {
		return timesstamp;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getArtifactId()).append(" ").append(currentVersion).append(" ").append(timesstamp);
		return buffer.toString();
	}
	
	public boolean isInstalled() {
		return isInstalled;
	}
	
	public void setIsInstalled(boolean isInstalled) {
		this.isInstalled = isInstalled;
	}
	
	public boolean isBundle() {
		return BUNDLES_GROUP_ID.equalsIgnoreCase(getGroupId());
	}

	public Pom getPom() {
		return this;
	}
	
	public File getBundleArchive() {
		return null;	
	}
	
	public boolean isIncluded() {
		return false;
	}
}
