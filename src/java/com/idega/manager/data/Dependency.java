/*
 * $Id: Dependency.java,v 1.2 2004/11/26 17:19:09 thomas Exp $
 * Created on Nov 19, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.manager.data;

import com.idega.xml.XMLElement;


/**
 * 
 *  Last modified: $Date: 2004/11/26 17:19:09 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.2 $
 */
public class Dependency {
	
	private static final String GROUP_ID_BUNDLE = "bundles";
	
	private static final String GROUP_ID = "groupId";
	private static final String VERSION = "version";
	
	public static Dependency getInstanceForElement(RealPom dependentPom, XMLElement element) {
		Dependency dependency = new Dependency();
		dependency.setDependentPom(dependentPom);
		String tempGroupId = element.getTextTrim(GROUP_ID);
		String tempArtifactId = element.getTextTrim(RealPom.ARTIFACT_ID);
		String tempVersion = element.getTextTrim(VERSION);
		dependency.setGroupId(tempGroupId);
		dependency.setArtifactId(tempArtifactId);
		dependency.setVersion(tempVersion);
		return dependency;
	}
	
	String groupId = null;
	String artifactId = null;
	String version = null;
	
	RealPom dependentPom = null;
	
	public boolean isBundle() {
		return GROUP_ID_BUNDLE.equalsIgnoreCase(getGroupId());
	}
	
	public String getArtifactId() {
		return artifactId;
	}
	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}
	public RealPom getDependentPom() {
		return dependentPom;
	}
	public void setDependentPom(RealPom dependentPom) {
		this.dependentPom = dependentPom;
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
}
