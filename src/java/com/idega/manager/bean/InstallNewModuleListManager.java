/*
 * $Id: InstallNewModuleListManager.java,v 1.6 2005/04/14 14:01:01 thomas Exp $
 * Created on Nov 10, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.manager.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.faces.event.ActionEvent;
import com.idega.manager.data.Module;
import com.idega.manager.data.SimpleProxyPomList;
import com.idega.manager.util.ManagerConstants;


/**
 * 
 *  Last modified: $Date: 2005/04/14 14:01:01 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.6 $
 */
public class InstallNewModuleListManager extends UpdateListManager {
	
	// String list of artifactIds
	private List selectedModules = null;

	public InstallNewModuleListManager() {
		initialize();
	}
	
	public String getTitle() {
       	return resourceBundle.getLocalizedString("man_manager_install_new_modules","Install new modules");
	}
	
	protected void initializeList() {
		if (selectedModules == null) {
		 	// nothing to initialize
		 	return;
		 }
		 multiSelectListbox1DefaultItems = new ArrayList();
		 Map repositoryPom = new HashMap();
		 Map sortedSimpleProxyList = pomSorter.getSortedSimpleProxyList();
		 Iterator iterator = selectedModules.iterator();
		 while (iterator.hasNext()) {
		 	String artifactId = (String) iterator.next();
		 	SimpleProxyPomList simpleProxyPomList = (SimpleProxyPomList) sortedSimpleProxyList.get(artifactId);
		 	pomSorter.addSimpleProxyPomList(artifactId, simpleProxyPomList, repositoryPom);
		 }
		 // sort
		 Collections.sort(selectedModules);
		 fillList(selectedModules, repositoryPom, repositoryPom);
	}
	
	protected Module getModuleGroupItem(Map repositoryPom, String artifactId) {
	 	Set pomSet = (Set) repositoryPom.get(artifactId);
	 	return (Module) pomSet.iterator().next();
	}
	
	protected String getLabelForItemGroup(Module groupModule) {
	 	return groupModule.getNameForLabel(resourceBundle);
	}
	
	protected String getLabelForItem(Module groupModule, Module itemModule) {
 		String label = itemModule.getCurrentVersionForLabel(resourceBundle);
		// if the suggested version is a snapshot do not recommend to install it
 		if (itemModule.isSnapshot()) {
 			String notRecommended = resourceBundle.getLocalizedString("man_manager_not_recommended", "not recommended");
 			StringBuffer buffer = new StringBuffer(label);
 			buffer.append(" - ").append(notRecommended);
 			return buffer.toString();
 		}
 		return label;
	}

		
	public void submitForm(ActionEvent event) {
		submitAndSetModuleManager(event, ManagerConstants.ACTION_BACK_INSTALL);
	}
		
	public void initializeDynamicContent(List modules) {
		this.selectedModules = modules;
		initializeList();
	}

}
