/*
 * $Id: UpdateListManager.java,v 1.13 2005/01/19 18:24:29 thomas Exp $
 * Created on Nov 10, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.manager.bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UISelectItems;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.component.html.HtmlForm;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.component.html.HtmlSelectManyListbox;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.manager.business.DependencyMatrix;
import com.idega.manager.business.PomSorter;
import com.idega.manager.business.PomValidator;
import com.idega.manager.data.Pom;
import com.idega.manager.data.ProxyPom;
import com.idega.manager.data.RealPom;
import com.idega.manager.util.ManagerUtils;
import com.idega.util.IWTimestamp;


/**
 * 
 *  Last modified: $Date: 2005/01/19 18:24:29 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.13 $
 */
public class UpdateListManager {
	
	private static final String JSF_VALUE_REFERENCE_INSTALL_OR_UPDATE_MANAGER = "#{InstallOrUpdateManager}";
	private static final String JSF_VALUE_REFERENCE_MODULE_MANAGER = "#{ModuleManager}";
	private static final String JSF_COMPONENT_ID = "form1:multiSelectListbox1";
	
	private static final String ACTION_NEXT = "next";
	private static final String ACTION_BACK = "back";
	private static final String ACTION_CANCEL = "cancel";
	
	private ManagerUtils managerUtils;
	private PomValidator pomValidator = null;
	private PomSorter pomSorter = null;
	
	private String outputText1Value;
	private String outputText2Value;
	private String button1Label;
	private String button2Label;
	private String button3Label;
	
	public UpdateListManager() {
		initialize();
	}
	
	private void initialize() {
		managerUtils = ManagerUtils.getInstanceForCurrentContext();
		initializePomSorter();
		initializeOutputText();
		initializeSubmitButtons();
	}

	private void initializePomSorter() {
		if (pomSorter == null) {
			InstallOrUpdateManager installOrUpdateManager = (InstallOrUpdateManager) managerUtils.getValue(JSF_VALUE_REFERENCE_INSTALL_OR_UPDATE_MANAGER);
			if (installOrUpdateManager != null) {
				pomSorter = installOrUpdateManager.getPomSorter();
			}
		}
	}
	
	private void initializeOutputText() {
		IWResourceBundle resourceBundle = managerUtils.getResourceBundle();
		outputText1Value = resourceBundle.getLocalizedString("man_manager_header", "Manager");
		outputText2Value = resourceBundle.getLocalizedString("man_manager_select _updates","Select updates");
	}

	private void initializeSubmitButtons() {
		IWResourceBundle resourceBundle = managerUtils.getResourceBundle();
		button1Label = resourceBundle.getLocalizedString("man_manager_back","Back");
		button2Label = resourceBundle.getLocalizedString("man_manager_next","Next");
		button3Label = resourceBundle.getLocalizedString("man_manager_cancel","Cancel");
	}
	
	private void initializeList() {
		IWResourceBundle resourceBundle = managerUtils.getResourceBundle();
		multiSelectListbox1DefaultItems = new ArrayList();
		String errorMessage = null;
		try {
			pomSorter.initializeInstalledPomsAndAvailableUpdates();
		}
		catch (IOException ex) {
			errorMessage = resourceBundle.getLocalizedString("man_manager_no_connection", "Problems connecting to remote repository occurred");
		}
		HtmlPanelGroup group = getGroupPanel1();
		List list = group.getChildren();
		list.clear();
		button2.setDisabled(false);
		if (errorMessage != null) {
			button2.setDisabled(true);
			errorMessage = errorMessage + " <br/>";
			HtmlOutputText error = new HtmlOutputText();
			error.setValue(errorMessage);
			error.setStyle("color: red");
			error.setEscape(false);
			list.add(error);		
			return;
		}
	 	
		SortedMap sortedInstalledPom = pomSorter.getSortedInstalledPoms();
		Map repositoryPom = pomSorter.getSortedRepositoryPomsOfAvailableUpdates();
		Iterator iterator = sortedInstalledPom.keySet().iterator();
		while (iterator.hasNext()) {
		 	String artifactId = (String) iterator.next();
		 	SortedSet pomProxies = (SortedSet) repositoryPom.get(artifactId);
		 	SelectItem[] items = null;
		 	if (pomProxies == null) {
		 		items = new SelectItem[0];
		 	}
		 	else {
		 		Iterator pomProxiesIterator = pomProxies.iterator();
		 		items = new SelectItem[pomProxies.size()];
			 	int i = 0;
			 	while (pomProxiesIterator.hasNext()) {
			 		ProxyPom proxy = (ProxyPom) pomProxiesIterator.next();
			 		// file is used as identifier
			 		String fileName = proxy.getFileName();
			 		IWTimestamp timestamp = proxy.getTimestamp();
			 		String label = (timestamp == null) ? proxy.getCurrentVersion() : timestamp.toString(true);
			 		items[i++] = new SelectItem(fileName, label);
			 	}
		 	}
		 	RealPom pom = (RealPom) sortedInstalledPom.get(artifactId);
		 	String currentVersion = pom.getCurrentVersion();
		 	StringBuffer buffer = new StringBuffer();
		 	buffer.append(artifactId).append(" ").append(currentVersion);
		 	multiSelectListbox1DefaultItems.add(new SelectItemGroup(buffer.toString(), null, true, items));		 	
		}
	}
	
	public void submitForm(ActionEvent event) {
		IWResourceBundle resourceBundle = managerUtils.getResourceBundle();
		UIComponent component = event.getComponent();
		UIComponent parentForm = component.getParent();
		HtmlSelectManyListbox selectManyList = (HtmlSelectManyListbox) parentForm.findComponent("multiSelectListbox1");
		Object[] selectedValues = selectManyList.getSelectedValues();
		Map repositoryPoms = pomSorter.getRepositoryPoms();
		Map selectedPoms = new HashMap();
		for (int i = 0; i < selectedValues.length; i++) {
			Pom pom = (Pom) repositoryPoms.get(selectedValues[i]);
			String artifactId = pom.getArtifactId();
			selectedPoms.put(artifactId, pom);
		}
		Map installedPoms = pomSorter.getSortedInstalledPoms();
		Collection installedModules = installedPoms.values();
		Collection notInstalledModules = selectedPoms.values();
		DependencyMatrix dependencyMatrix = DependencyMatrix.getInstance(notInstalledModules, installedModules, resourceBundle);
		List necessaryModules = dependencyMatrix.getListOfNecessaryModules();
		// it is important to reset the error messages - that is to set to null - if there are not any
		List errorMessages = dependencyMatrix.hasErrors() ? dependencyMatrix.getErrorMessages() : null;
		pomSorter.setErrorMessages(errorMessages);
		pomSorter.setNecessaryPoms(necessaryModules);
		ModuleManager moduleManager = (ModuleManager) ManagerUtils.getInstanceForCurrentContext().getValue(JSF_VALUE_REFERENCE_MODULE_MANAGER);
		if (moduleManager != null) {
			moduleManager.initializeDynamicContent();
			moduleManager.setActionBackToUpdateModules();
		}
	}
 
	
	public void validateSelectedModules(FacesContext context, UIComponent toValidate, Object value) {
		// the value of a hidden input is validated because only in this way this method is called even if nothing has been selected.
		// We could use the attribute "required" but this causes problems with the localization of the corresponding error message.
		IWResourceBundle resourceBundle = managerUtils.getResourceBundle();
		// get the value of the component we are really interested in....
		UIComponent component = context.getViewRoot().findComponent(JSF_COMPONENT_ID);
		Object componentValue = ((UIInput) component).getValue();
		if (pomValidator == null) {
			pomValidator = new PomValidator();
		}
		pomValidator.validateSelectedModules(context, toValidate, componentValue, pomSorter , resourceBundle);
	}

	public void initializeDynamicContent() {
		initializeList();
	}	
	
   private HtmlForm form1 = new HtmlForm();

    public HtmlForm getForm1() {
        return form1;
    }

    public void setForm1(HtmlForm hf) {
        this.form1 = hf;
    }

    private HtmlSelectManyListbox multiSelectListbox1 = new HtmlSelectManyListbox();

    public HtmlSelectManyListbox getMultiSelectListbox1() {
        return multiSelectListbox1;
    }

    public void setMultiSelectListbox1(HtmlSelectManyListbox hsml) {
        this.multiSelectListbox1 = hsml;
    }

    private List multiSelectListbox1DefaultItems = new ArrayList();

    public List getMultiSelectListbox1DefaultItems() {
        return multiSelectListbox1DefaultItems;
    }

    public void setMultiSelectListbox1DefaultItems(List dsia) {
        this.multiSelectListbox1DefaultItems = dsia;
    }

    private UISelectItems multiSelectListbox1SelectItems = new UISelectItems();

    public UISelectItems getMultiSelectListbox1SelectItems() {
        return multiSelectListbox1SelectItems;
    }

    public void setMultiSelectListbox1SelectItems(UISelectItems uisi) {
        this.multiSelectListbox1SelectItems = uisi;
    }

    private HtmlCommandButton button1 = new HtmlCommandButton();

    public HtmlCommandButton getButton1() {
        return button1;
    }

    public void setButton1(HtmlCommandButton hcb) {
        this.button1 = hcb;
    }

    private HtmlCommandButton button2 = new HtmlCommandButton();

    public HtmlCommandButton getButton2() {
        return button2;
    }

    public void setButton2(HtmlCommandButton hcb) {
        this.button2 = hcb;
    }

    private HtmlCommandButton button3 = new HtmlCommandButton();

    public HtmlCommandButton getButton3() {
        return button3;
    }

    public void setButton3(HtmlCommandButton hcb) {
        this.button3 = hcb;
    }    
    
    private HtmlOutputText outputText1 = new HtmlOutputText();

    public HtmlOutputText getOutputText1() {
        return outputText1;
    }

    public void setOutputText1(HtmlOutputText hot) {
        this.outputText1 = hot;
    }

    private HtmlOutputText outputText2 = new HtmlOutputText();

    public HtmlOutputText getOutputText2() {
        return outputText2;
    }

    public void setOutputText2(HtmlOutputText hot) {
        this.outputText2 = hot;
    }

    public String getOutputText1Value() {
    	return outputText1Value;
    }
 
    public String getOutputText2Value() {
    	return outputText2Value;
    }    
    
    public String getButton1Label() {
    	return button1Label;
    }
    
    public String getButton2Label() {
    	return button2Label;
    }
    public String getButton3Label() {
    	return button3Label;
    }
    
    public String button1_action() {
    	return ACTION_BACK;
    }
    
    public String button2_action() {
    	return ACTION_NEXT;
    }    
    
    
    public String button3_action() {
    	return ACTION_CANCEL;
    }

    private HtmlPanelGroup groupPanel1 = new HtmlPanelGroup();

    public HtmlPanelGroup getGroupPanel1() {
        return groupPanel1;
    }

    public void setGroupPanel1(HtmlPanelGroup hpg) {
        this.groupPanel1 = hpg;
    }


}
