/*
 * $Id: DependencyMatrix.java,v 1.6 2004/12/08 17:36:53 thomas Exp $
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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.manager.data.Dependency;
import com.idega.manager.data.Module;
import com.idega.manager.data.Pom;
import com.idega.util.datastructures.HashMatrix;


/**
 * 
 *  Last modified: $Date: 2004/12/08 17:36:53 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.6 $
 */
public class DependencyMatrix {
	
	private IWResourceBundle resourceBundle = null;
	
	private List errorMessages = null;
	
	private HashMatrix moduleDependencies = null;
	
	private Collection notInstalledModules = null;
	private Collection installedModules = null;
	
	
	public static DependencyMatrix getInstance(Collection notInstalledModules, Collection installedModules, IWResourceBundle resourceBundle) {
		DependencyMatrix dependencyMatrix = new DependencyMatrix();
		dependencyMatrix.notInstalledModules = notInstalledModules;
		dependencyMatrix.installedModules = installedModules;
		dependencyMatrix.resourceBundle = resourceBundle;
		return dependencyMatrix;
	}
	
	public List getListOfModulesToBeInstalled() {
		Collection tempNotInstalled = notInstalledModules;
		Collection tempInstalled = installedModules;
		List tempToBeInstalled = null;
		boolean go = true;
		while (go) {
			initializeMatrix(tempNotInstalled, tempInstalled);
			tempToBeInstalled = tryGetListOfModulesToBeInstalled();
			go = tempNotInstalled.retainAll(tempToBeInstalled);
		}
		return tempToBeInstalled;
	}
			
	public boolean hasErrors() {
		return ! (errorMessages == null || errorMessages.isEmpty());
	}
	
	public List getErrorMessages() {
		return errorMessages;
	}
	
	
	private List tryGetListOfModulesToBeInstalled() {
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
	
	private void initializeMatrix(Collection tempNotInstalledModules, Collection tempInstalledModules) {
		errorMessages = null;
		addEntries(tempInstalledModules);
		addEntries(tempNotInstalledModules);
	}
	
	private void addEntries(Collection poms) {
		Iterator iterator = poms.iterator();
		while (iterator.hasNext()) {
			Pom pom = (Pom) iterator.next();
			addEntry(pom);
		}
	}
	
	private void addEntry(Pom pom) {
		HashMatrix matrix = getModuleDependencies();
		addEntry(pom, pom, matrix);
	}
	
	private void addEntry(Pom dependant, Pom source, HashMatrix matrix) {
		String dependantKey = getKeyForDependant(dependant).toString();
		String dependencyKeyForDependant = getKeyForDependency(dependant).toString();
		moduleDependencies.put(dependencyKeyForDependant, dependantKey, dependant);
		List dependencies = null;
		try {
			 dependencies = source.getDependencies();
		}
		catch (IOException ex) {
			String errorMessage = resourceBundle.getLocalizedString("man_manager_could_not_get_dependencies","Could not figure out dependencies of ") + source.getArtifactId();
			addErrorMessage(errorMessage);
			return;
		}
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
				String errorMessage = resourceBundle.getLocalizedString("man_manager_could_not_get_dependencies","Could not figure out dependencies of ") + dependency.getArtifactId();
				addErrorMessage(errorMessage);

			}
			if (dependencyPom != null) {
				// go further
				addEntry(dependant, dependencyPom, matrix);					
			}
		}
	}
	
	private void addErrorMessage(String errorMessage) {
		if (errorMessages == null) {
			errorMessages = new ArrayList();
		}
		errorMessages.add(errorMessage);
	}
	
	

}