/*
 * $Id: Dependency.java,v 1.1 2004/11/19 17:05:42 thomas Exp $
 * Created on Nov 19, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.manager.data;


/**
 * 
 *  Last modified: $Date: 2004/11/19 17:05:42 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1 $
 */
public class Dependency {
	
	String groupId = null;
	String artifactId = null;
	String version = null;
	
	Pom dependentPom = null;
	
	
	
	public String getArtifactId() {
		return artifactId;
	}
	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}
	public Pom getDependentPom() {
		return dependentPom;
	}
	public void setDependentPom(Pom dependentPom) {
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
