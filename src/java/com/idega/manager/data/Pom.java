/*
 * $Id: Pom.java,v 1.1 2004/11/19 17:05:42 thomas Exp $
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
import java.util.List;
import org.apache.maven.wagon.util.FileUtils;
import com.idega.util.StringHandler;
import com.idega.util.xml.XMLData;
import com.idega.xml.XMLElement;


/**
 * 
 *  Last modified: $Date: 2004/11/19 17:05:42 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1 $
 */
public class Pom {
	
	private final static	String  EXTEND = "extend";
	private final static String ARTIFACT_ID = "artifactId";
	private final static String CURRENT_VERSION = "currentVersion";
	private final static String DEPENDENCY = "dependency";
	
	private final static String BASE_PROJECT_DIR = "../com.idega.core.bundle";
	private final static String BASE_PROJECT_VARIABLE = "${base.project.dir}";
	
	public static Pom getPom(File projectFile) throws IOException {
		XMLData pomData = createXMLData(projectFile);
		return new Pom(pomData);
	}
	
	public static XMLData createXMLData(File projectFile) throws IOException {
		XMLData pomData = XMLData.getInstanceForFile(projectFile);
		// handle extensions
		String superPom = pomData.getDocument().getRootElement().getText(EXTEND);
		//TODO thi: where can I fetch the value of the variable?
		if (superPom != null) {
			// replace if variable exists
			superPom = StringHandler.replace(superPom, BASE_PROJECT_VARIABLE, BASE_PROJECT_DIR);
			// get the path (works also for relative paths!)
			String filePath =projectFile.getPath();
			// should also work on windows....
			filePath = filePath.replace(File.separatorChar, '/');
			// catPath works only with unix file separator
			String pathToSuperPom = FileUtils.catPath(filePath, superPom);
			// should also works on windows
			pathToSuperPom = pathToSuperPom.replace('/',File.separatorChar);
			File superPomFile = new File(pathToSuperPom);
			XMLData superPomData = createXMLData(superPomFile);
			// concat pomData and superPomData
			pomData.addFirst(superPomData);
		}
		return pomData;
	}
	
	private XMLData xmlData = null;
	private XMLElement root = null;
	
	private List dependencies = null;
	
	public Pom(XMLData data) {
		xmlData = data;
	}
	
	
	public String getArtifactId() {
		return getRoot().getText(ARTIFACT_ID);
	}
	
	public String getCurrentVersion() {
		return getRoot().getText(CURRENT_VERSION);
	}
	
	private XMLElement  getRoot() {
		if (root == null) {
			root = xmlData.getDocument().getRootElement();
		}
		return root;
	}

	public List getDependencies() {
		if (dependencies == null) {
			dependencies = getRoot().getChildrenRecursive(DEPENDENCY);
		}
		return dependencies;
	}

}
