/*
 * $Id: DependencyMatrix.java,v 1.2 2004/12/01 19:24:21 thomas Exp $
 * Created on Nov 26, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.manager.business;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.idega.manager.data.Dependency;
import com.idega.manager.data.Module;
import com.idega.manager.data.Pom;
import com.idega.util.StringHandler;
import com.idega.util.datastructures.HashMatrix;


/**
 * 
 *  Last modified: $Date: 2004/12/01 19:24:21 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.2 $
 */
public class DependencyMatrix {
	
	private List errorMessages = new ArrayList();
	
	private HashMatrix moduleDependencies;
	
	public void addEntry(Pom pom) {
		HashMatrix matrix = getModuleDependencies();
		addEntry(pom, pom, matrix);
	}
	
	public void addEntry(Pom dependant, Pom source, HashMatrix matrix) {
		String dependantKey = getKeyForDependant(dependant).toString();
		String dependencyKeyForDependant = getKeyForDependency(dependant).toString();
		moduleDependencies.put(dependencyKeyForDependant, dependantKey, dependant);
		List dependencies = source.getDependencies();
		Iterator iterator = dependencies.iterator();
		while (iterator.hasNext()) {
			Dependency dependency = (Dependency) iterator.next();
			String dependencyKey = getKeyForDependency(dependency).toString();
			moduleDependencies.put(dependencyKey, dependantKey, dependency);
			Pom dependencyPom = null;
			try {
				dependencyPom  = dependency.getPom();
			}
			catch (IOException ex) {
				String errorMessage = "[DependencyMatrix] Could not get dependencies of " + dependency.getArtifactId();
				errorMessages.add(errorMessage);
			}
			if (dependencyPom != null) {
				// go further
				addEntry(dependant, dependencyPom, matrix);					
			}
		}
	}
	
	public List getListOfModulesToBeInstalled() {
		List moduleList = new ArrayList();
		// get a list of required modules
		Iterator iterator = moduleDependencies.firstKeySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			Map map = moduleDependencies.get(key);
			Iterator iteratorMap = map.keySet().iterator();
			Module toBeInstalled = null;
			while (iteratorMap.hasNext()) {
				String innerKey = (String) iteratorMap.next();
				Module module = (Module) map.get(innerKey);
				// tests
				if (module.getArtifactId().endsWith("article")) {
					System.out.println("Weserl");
				}
				if (toBeInstalled == null || (module.compare(toBeInstalled) > 0)) {
					toBeInstalled = module;
				}
			}
			// install only module that are not installed and not included in other modules
			if (! (toBeInstalled.isInstalled() || toBeInstalled.isIncluded())) {
				moduleList.add(toBeInstalled);
			}
		}
		return moduleList;
	}
	
	// e.g. returns "bundles_com.idega.block.article_installed"
	// e.g. returns "bundles_com.idega.block.article"
	private StringBuffer getKeyForDependant(Module module)	{
		StringBuffer buffer = getKeyForDependency(module);
		if (module.isInstalled()) {
			buffer.append("_installed");
		}
		return buffer;
	}
	
	// e.g. returns "bundles_com.idega.block.article"
	private StringBuffer getKeyForDependency(Module module) {
		String groupId = module.getGroupId();
		String artifactId = module.getArtifactId();
		StringBuffer buffer = new StringBuffer(groupId);
		buffer.append(" _").append(artifactId);
		return buffer;
	}
	
	private HashMatrix getModuleDependencies() {
		if (moduleDependencies == null) {
			moduleDependencies = new HashMatrix();
		}
		return moduleDependencies;
	}

}