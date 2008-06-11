/*
 * $Id: RealPom.java,v 1.1 2008/06/11 21:10:01 tryggvil Exp $
 * Created on Nov 15, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.manager.maven1.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.idega.idegaweb.IWResourceBundle;
import com.idega.manager.maven1.util.ManagerConstants;
import com.idega.util.FileUtil;
import com.idega.util.IWTimestamp;
import com.idega.util.StringHandler;
import com.idega.util.xml.XMLData;
import com.idega.xml.XMLElement;


/**
 * 
 *  Last modified: $Date: 2008/06/11 21:10:01 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1 $
 */
public class RealPom extends Pom {
	
	public static final String BUNDLES_GROUP_ID = "bundles";	

	public static final String SNAPSHOT = "SNAPSHOT";
	
	public static final String BUNDLE_SUFFIX = ".bundle";
	
	public static final String POM_FILE = "project.xml";
	
	private static final String EXTEND = "extend";
	private static final String GROUP_ID = "groupId";
	private static final String NAME = "name";
	static final String ARTIFACT_ID = "artifactId";
	private static final String CURRENT_VERSION = "currentVersion";
	private static final String DEPENDENCY = "dependency";
	
	
	private static final String MANIFEST_PATH = "./META-INF/MANIFEST.MF";
	private static final String BASE_PROJECT_PATH = "../com.idega.core.bundle";
	private static final String BASE_PROJECT_VARIABLE = "${base.project.dir}";
	
	
//	public static String convertFileName(String fileName) {
//		// e.g. com.idega.manager-1.0.1-SNAPSHOT.jar
//		if (isSnapshot(fileName)) {
//			String[] partOfFileName = fileName.split(ManagerConstants.ARTIFACT_ID_VERSION_SEPARATOR);
//			StringBuffer buffer = new StringBuffer(partOfFileName[0]);
//			buffer.append(ManagerConstants.ARTIFACT_ID_VERSION_SEPARATOR);
//			buffer.append(SNAPSHOT);
//			buffer.append('.');
//			buffer.append(ManagerConstants.JAR_EXTENSION);
//			// e.g. com.idega.manager-SNAPSHOT.jar
//			return buffer.toString();
//		}
//		return fileName;
//	}
	
	public static RealPom getInstalledPomOfGroupBundles(File projectFile) throws IOException {
		RealPom pom = getPom(projectFile);
		pom.setIsInstalled(true);
		// save time....
		pom.groupId = BUNDLES_GROUP_ID;	
		return pom;
	}
	
	public static RealPom getPom(File projectFile) throws IOException {
		XMLData pomData = createXMLData(projectFile);
		return new RealPom(pomData, projectFile);
	}
	
	public static XMLData createXMLData(File projectFile) throws IOException {
		XMLData pomData = XMLData.getInstanceForFile(projectFile);
		// handle extensions
		String superPom = pomData.getDocument().getRootElement().getTextTrim(EXTEND);
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
	
	// are we navigating in an eclipse project or on a web server?
	private boolean isEclipseProject = false;
	
	boolean isInstalled = false;
	
	protected File projectFile = null;
	
	private XMLData xmlData = null;
	private XMLElement root = null;
	
	private String groupId = null;
	private String artifactId = null;
	private String currentVersion = null;
	
	private boolean snapshot = false;
	private IWTimestamp timesstamp = null;
	
	private List dependencies = null;
	
	protected RealPom(XMLData xmlData, File projectFile) throws IOException {
		this.projectFile = projectFile;
		this.xmlData =xmlData ;
		initialize();
	}

	private void initialize() throws IOException {
		// initilaize snapshot variable
		getCurrentVersion();
		initializeTimestamp();
	}	
	
	private void initializeTimestamp() throws IOException {
		IWTimestamp timestamp = null;
		// try to get timestamp from origin file
		File originFile = FileUtil.getFileRelativeToFile(this.projectFile, ManagerConstants.ORIGIN_FILE);
		if (originFile.exists()) {
			BufferedReader fileReader = null;
			try {
				fileReader = new BufferedReader(new FileReader(originFile));
				String orignFileName = fileReader.readLine();
				timestamp = getTimestampFromFileName(orignFileName);
			}
			finally {
			  	try {
			  		if (fileReader != null) {
			  			fileReader.close();
			  		}
			  	}
			  	catch (IOException io) {
					// do not hide an existing exception
			  	}
			}
		}
		if (timestamp == null) {
			// failed?  try to get timestamp from corresponding MANIFEST_FILE  
			File manifestFile = FileUtil.getFileRelativeToFile(this.projectFile, MANIFEST_PATH);
			if (manifestFile.exists()) {
				long dateValue = manifestFile.lastModified();
				Date date = new Date(dateValue);
				timestamp = new IWTimestamp(date);
			}
		}
		setTimestamp(timestamp);
	}
	

	
	public void setTimestamp(IWTimestamp timestamp) {
		this.timesstamp = timestamp;
	}
	
	public String getGroupId() {
		if (this.groupId == null) {
			this.groupId = getRoot().getTextTrim(GROUP_ID);
		}
		return this.groupId;
	}
	
	public String getArtifactId() {
		if (this.artifactId == null) {
			this.artifactId = getRoot().getTextTrim(ARTIFACT_ID);
		}
		return this.artifactId;
	}
	
	public boolean isSnapshot() {
		return this.snapshot;
	}
	
	public String getCurrentVersion() {
		if (this.currentVersion == null) { 
			this.currentVersion = getRoot().getTextTrim(CURRENT_VERSION);
			// sometimes a version is not set
			if (this.currentVersion == null) {
				this.currentVersion = "";
			}
			this.snapshot = RealPom.isSnapshot(this.currentVersion);
			if (this.snapshot) {
				this.currentVersion = StringHandler.remove(this.currentVersion, SNAPSHOT);
				this.currentVersion = StringHandler.remove(this.currentVersion, "-");
			}
		}
		return this.currentVersion;
	}
	
	private XMLElement  getRoot() {
		if (this.root == null) {
			this.root = this.xmlData.getDocument().getRootElement();
		}
		return this.root;
	}

	public List getDependencies()  {
		return getDependencies(this);
	}
	
	
	public List getDependencies(Pom dependant) {
		if (this.dependencies == null) {
			this.dependencies = new ArrayList();
			List elements = getRoot().getChildrenRecursive(DEPENDENCY);
			Iterator iterator = elements.iterator();
			while (iterator.hasNext()) {
				XMLElement element = (XMLElement) iterator.next();
				Dependency dependency = Dependency.getInstanceForElement(dependant, element);
				this.dependencies.add(dependency);
			}
		}
		return this.dependencies;
	}
	
	public Pom getPom(DependencyPomBundle dependency) throws IOException {
		String dependencyArtifactId = dependency.getArtifactId();
		String moduleName = (isEclipseProject()) ? dependencyArtifactId : StringHandler.concat(dependencyArtifactId, BUNDLE_SUFFIX);
		File bundlesFolder = getBundlesFolder(this.projectFile);
		File module = new File(bundlesFolder, moduleName);
		File dependencyProjectFile = new File(module, RealPom.POM_FILE);
		RealPom pom = RealPom.getPom(dependencyProjectFile);
		pom.setEclipseProject(isEclipseProject());
		pom.setIsInstalled(isInstalled());
		return pom;
	}
	
	protected File getBundlesFolder(File startFile) {
		return startFile.getParentFile().getParentFile();
	}
	
	public File getBundleArchive(DependencyPomBundle dependency) {
		return null;
	}

	public IWTimestamp getTimestamp() {
		return this.timesstamp;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getArtifactId()).append(" ").append(this.currentVersion).append(" ").append(this.timesstamp);
		return buffer.toString();
	}
	
	public boolean isInstalled() {
		return this.isInstalled;
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
	
	public String getNameForLabel(IWResourceBundle resourceBundle) {
		String tempName = getRoot().getTextTrim(NAME);
		String tempArtifactId = getRoot().getTextTrim(ARTIFACT_ID);
		StringBuffer buffer = new StringBuffer();
		buffer.append(tempName).append(" '");
		buffer.append(tempArtifactId).append("'");
		return buffer.toString();
	}
	
	public String getCurrentVersionForLabel(IWResourceBundle resourceBundle) {
		String tempCurrentVersion = getRoot().getTextTrim(CURRENT_VERSION);
		String version = resourceBundle.getLocalizedString("man_manager_version","Version");
		StringBuffer buffer = new StringBuffer();
		buffer.append(version).append(" ").append(tempCurrentVersion);
		return buffer.toString();
	}
	
	public boolean isEclipseProject() {
		return this.isEclipseProject;
	}
	public void setEclipseProject(boolean isEclipseProject) {
		this.isEclipseProject = isEclipseProject;
	}
}
