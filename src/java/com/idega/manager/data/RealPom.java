/*
 * $Id: RealPom.java,v 1.1 2004/11/26 17:19:09 thomas Exp $
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
 *  Last modified: $Date: 2004/11/26 17:19:09 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1 $
 */
public class RealPom implements Pom {
	

	public static final String SNAPSHOT = "SNAPSHOT";
	
	public static final String BUNDLE_SUFFIX = ".bundle";
	
	public static final String POM_FILE = "project.xml";
	
	private static final String EXTEND = "extend";
	static final String ARTIFACT_ID = "artifactId";
	private static final String CURRENT_VERSION = "currentVersion";
	private static final String DEPENDENCY = "dependency";
	
	
	private static final String MANIFEST_PATH = "./META-INF/MANIFEST.MF";
	private static final String BASE_PROJECT_PATH = "../com.idega.core.bundle";
	private static final String BASE_PROJECT_VARIABLE = "${base.project.dir}";
	
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
	
	private File projectFile = null;
	
	private XMLData xmlData = null;
	private XMLElement root = null;
	
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
	
	public String getArtifactId() {
		if (artifactId == null) {
			artifactId = getRoot().getText(ARTIFACT_ID);
		}
		return artifactId;
	}
	
	public boolean isSnapshot() {
		return snapshot;
	}
	
	public String getCurrentVersion() {
		if (currentVersion == null) { 
			currentVersion = getRoot().getText(CURRENT_VERSION);
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
		if (dependencies == null) {
			dependencies = new ArrayList();
			List elements = getRoot().getChildrenRecursive(DEPENDENCY);
			Iterator iterator = elements.iterator();
			while (iterator.hasNext()) {
				XMLElement element = (XMLElement) iterator.next();
				Dependency dependency = Dependency.getInstanceForElement(this, element);
				dependencies.add(dependency);
			}
		}
		return dependencies;
	}
	
	public Pom getPom(Dependency dependency) throws IOException {
		if (! dependency.isBundle()) {
			throw new IOException("Dependency does not belong to group bundles");
		}
		String dependencyArtifactId = dependency.getArtifactId();
		String moduleName = StringHandler.concat(dependencyArtifactId, BUNDLE_SUFFIX);
		File bundlesFolder = projectFile.getParentFile().getParentFile();
		File module = new File(bundlesFolder, moduleName);
		File dependencyProjectFile = new File(module, RealPom.POM_FILE);
		return RealPom.getPom(dependencyProjectFile);
	}

	public IWTimestamp getTimesstamp() {
		return timesstamp;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getArtifactId()).append(" ").append(currentVersion).append(" ").append(timesstamp);
		return buffer.toString();
	}
}
