/*
 * $Id: UpdateListManager.java,v 1.9 2005/01/07 11:03:35 thomas Exp $
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
import javax.faces.component.UISelectItems;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlForm;
import javax.faces.component.html.HtmlOutputText;
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
 *  Last modified: $Date: 2005/01/07 11:03:35 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.9 $
 */
public class UpdateListManager {
	
	private static final String ACTION_MODULE_MANAGER = "moduleManager";
	
	private IWResourceBundle resourceBundle;
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
		resourceBundle = ManagerUtils.getInstanceForCurrentContext().getResourceBundle();
		initializeOutputText();
		initializeSubmitButtons();
		initializeList();
	}
	
	private void initializeOutputText() {
		outputText1Value = resourceBundle.getLocalizedString("man_manager_header", "Manager");
		outputText2Value = resourceBundle.getLocalizedString("man_manager_choose","Choose one option");
	}

	private void initializeSubmitButtons() {
		button1Label = resourceBundle.getLocalizedString("man_manager_back","Back");
		button2Label = resourceBundle.getLocalizedString("man_manager_next","Next");
		button3Label = resourceBundle.getLocalizedString("man_manager_cancel","Cancel");
	}
	
	private void initializeList() {
		 multiSelectListbox1DefaultItems = new ArrayList();
		 pomSorter = new PomSorter();
		 try {
		 	pomSorter.initializeInstalledPomsAndAvailableUpdates();
		 }
		 catch (IOException ex) {
		 	String errorMessage = resourceBundle.getLocalizedString("man_manager_no_connection", "Problems connecting to remote repository occurred");
		 	SelectItemGroup errorGroup = new SelectItemGroup(errorMessage, null, true,  new SelectItem[0]);
		 	multiSelectListbox1DefaultItems.add(errorGroup);	
		 	return;
		 }
		 SortedMap sortedInstalledPom = pomSorter.getSortedInstalledPoms();
		 Map repositoryPom = pomSorter.getSortedRepositoryPoms();
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
		if (dependencyMatrix.hasErrors()) {
			pomSorter.setErrorMessages(dependencyMatrix.getErrorMessages());
		}
		pomSorter.setNecessaryPoms(necessaryModules);
	}
 
	
	public void validateSelectedModules(FacesContext context, UIComponent toValidate, Object value) {
		if (pomValidator == null) {
			pomValidator = new PomValidator();
		}
		PomSorter tempPomSorter = getPomSorter();
		pomValidator.validateSelectedModules(context, toValidate, value, tempPomSorter , resourceBundle);
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
    
    public String button2_action() {
    	return ACTION_MODULE_MANAGER;
    }
    
	public PomSorter getPomSorter() {
		return pomSorter;
	}
}
