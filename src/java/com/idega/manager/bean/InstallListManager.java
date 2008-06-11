/*
 * $Id: InstallListManager.java,v 1.8 2008/06/11 21:10:01 tryggvil Exp $
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UISelectItems;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.component.html.HtmlSelectManyListbox;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.idega.idegaweb.IWResourceBundle;
import com.idega.manager.maven1.business.PomSorter;
import com.idega.manager.maven1.business.PomValidator;
import com.idega.manager.maven1.data.ProxyPom;
import com.idega.manager.maven1.data.RepositoryLogin;
import com.idega.manager.maven1.data.SimpleProxyPomList;
import com.idega.manager.maven1.util.ManagerConstants;
import com.idega.manager.maven1.util.ManagerUtils;
import com.idega.util.datastructures.SortedByValueMap;


/**
 * 
 *  Last modified: $Date: 2008/06/11 21:10:01 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.8 $
 */
public class InstallListManager {
	
	private IWResourceBundle resourceBundle;
	private PomValidator pomValidator = null;
	private PomSorter pomSorter = null;
	
	private String outputText1Value;
	private String outputText2Value;
	private String button1Label;
	private String button2Label;
	private String button3Label;
	
	public InstallListManager() {
		initialize();
	}
	
	private void initialize() {
		this.resourceBundle = ManagerUtils.getInstanceForCurrentContext().getResourceBundle();
		initializePomSorter();
		initializeOutputText();
		initializeSubmitButtons();
	}
	
	private void initializePomSorter() {
		if (this.pomSorter == null) {
			this.pomSorter = ManagerUtils.getPomSorter();
		}
	}
	
	
	private void initializeOutputText() {
		this.outputText1Value = this.resourceBundle.getLocalizedString("man_manager_header", "Manager");
		this.outputText2Value = this.resourceBundle.getLocalizedString("man_manager_select _new_modules","Select new modules you wish to install");
	}

	private void initializeSubmitButtons() {
		this.button1Label = this.resourceBundle.getLocalizedString("man_manager_back","Back");
		this.button2Label = this.resourceBundle.getLocalizedString("man_manager_next","Next");
		this.button3Label = this.resourceBundle.getLocalizedString("man_manager_cancel","Cancel");
	}
	
	private void initializeList() {
		 this.multiSelectListbox1DefaultItems = new ArrayList();
		 String errorMessage = null;
			RepositoryLogin repositoryLogin = ManagerUtils.getRepositoryLogin();
		 try {
		 	this.pomSorter.initializeInstalledPomsAndAvailableNewModules(repositoryLogin); 
		 }
		 catch (IOException ex) {
		 	errorMessage = this.resourceBundle.getLocalizedString("man_manager_no_connection", "Problems connecting to remote repository occurred");
		 }
		HtmlPanelGroup group = getGroupPanel1();
		List list = group.getChildren();
		list.clear();
		this.button2.setDisabled(false);
		if (errorMessage != null) {
			this.button2.setDisabled(true);
			errorMessage = errorMessage + " <br/>";
			HtmlOutputText error = new HtmlOutputText();
			error.setValue(errorMessage);
			error.setStyle("color: red");
			error.setEscape(false);
			list.add(error);	
			return;
		}
		 
		 Map repositoryPom = this.pomSorter.getSortedSimpleProxyList();
		 Map listItems = new HashMap();
		 Iterator iterator = repositoryPom.keySet().iterator();
		 while (iterator.hasNext()) {
		 	String artifactId = (String) iterator.next();
		 	SimpleProxyPomList simpleProxyPomList = (SimpleProxyPomList) repositoryPom.get(artifactId);
		 	if (! simpleProxyPomList.isEmpty()) {
		 		ProxyPom proxyPom = simpleProxyPomList.getRepresentative();
		 		String name = proxyPom.getNameForLabel(this.resourceBundle);
			 	SelectItem item = new SelectItem(artifactId, name);
		 	 	listItems.put(item, name);
		 	}
	 	}
		Locale locale = this.resourceBundle.getLocale();
		SortedByValueMap sortedMap = new SortedByValueMap(listItems, locale);
		Iterator valueIterator = sortedMap.keySet().iterator();
		while (valueIterator.hasNext()) {
			SelectItem item = (SelectItem) valueIterator.next();
			 this.multiSelectListbox1DefaultItems.add(item);	
		}
	}
	
	public void submitForm(ActionEvent event) {
		UIComponent component = event.getComponent();
		UIComponent parentForm = component.getParent();
		HtmlSelectManyListbox selectManyList = (HtmlSelectManyListbox) parentForm.findComponent("multiSelectListbox1");
		Object[] selectedValues = selectManyList.getSelectedValues();
		List selectedModules = Arrays.asList(selectedValues);
		InstallNewModuleListManager installNewModuleListManager = ManagerUtils.getInstallNewModuleListManager();
		if (installNewModuleListManager != null) {
			installNewModuleListManager.initializeDynamicContent(selectedModules);
		}
	}

	public void validateSelectedModules(FacesContext context, UIComponent toValidate, Object value) {
		// the value of a hidden input is validated because only in this way this method is called even if nothing has been selected.
		// We could use the attribute "required" but this causes problems with the localization of the corresponding error message.
		// get the value of the component we are really interested in....
		UIComponent component = context.getViewRoot().findComponent(ManagerConstants.JSF_COMPONENT_ID_MULTI_SELECT_1);
		Object componentValue = ((UIInput) component).getValue();
		if (this.pomValidator == null) {
			this.pomValidator = new PomValidator();
		}
		this.pomValidator.validateSelectedModuleNames(context, toValidate, componentValue, this.resourceBundle);
	}
	
	public void initializeDynamicContent() {
		initializeList();
	}

    private HtmlSelectManyListbox multiSelectListbox1 = new HtmlSelectManyListbox();

    public HtmlSelectManyListbox getMultiSelectListbox1() {
        return this.multiSelectListbox1;
    }

    public void setMultiSelectListbox1(HtmlSelectManyListbox hsml) {
        this.multiSelectListbox1 = hsml;
    }

    private List multiSelectListbox1DefaultItems = new ArrayList();

    public List getMultiSelectListbox1DefaultItems() {
        return this.multiSelectListbox1DefaultItems;
    }

    public void setMultiSelectListbox1DefaultItems(List dsia) {
        this.multiSelectListbox1DefaultItems = dsia;
    }

    private UISelectItems multiSelectListbox1SelectItems = new UISelectItems();

    public UISelectItems getMultiSelectListbox1SelectItems() {
        return this.multiSelectListbox1SelectItems;
    }

    public void setMultiSelectListbox1SelectItems(UISelectItems uisi) {
        this.multiSelectListbox1SelectItems = uisi;
    }

    private HtmlCommandButton button1 = new HtmlCommandButton();

    public HtmlCommandButton getButton1() {
        return this.button1;
    }

    public void setButton1(HtmlCommandButton hcb) {
        this.button1 = hcb;
    }

    private HtmlCommandButton button2 = new HtmlCommandButton();

    public HtmlCommandButton getButton2() {
        return this.button2;
    }

    public void setButton2(HtmlCommandButton hcb) {
        this.button2 = hcb;
    }

    private HtmlCommandButton button3 = new HtmlCommandButton();

    public HtmlCommandButton getButton3() {
        return this.button3;
    }

    public void setButton3(HtmlCommandButton hcb) {
        this.button3 = hcb;
    }    
    
    private HtmlOutputText outputText1 = new HtmlOutputText();

    public HtmlOutputText getOutputText1() {
        return this.outputText1;
    }

    public void setOutputText1(HtmlOutputText hot) {
        this.outputText1 = hot;
    }

    private HtmlOutputText outputText2 = new HtmlOutputText();

    public HtmlOutputText getOutputText2() {
        return this.outputText2;
    }

    public void setOutputText2(HtmlOutputText hot) {
        this.outputText2 = hot;
    }

    public String getOutputText1Value() {
    	return this.outputText1Value;
    }
 
    public String getOutputText2Value() {
    	return this.outputText2Value;
    }    
    
    public String getButton1Label() {
    	return this.button1Label;
    }
    
    public String getButton2Label() {
    	return this.button2Label;
    }
    public String getButton3Label() {
    	return this.button3Label;
    }
    
    public String button1_action() {
    	return ManagerConstants.ACTION_BACK;
    }
    
    public String button2_action() {
    	return ManagerConstants.ACTION_NEXT;
    }    
    
    
    public String button3_action() {
    	return ManagerConstants.ACTION_CANCEL;
    }

    private HtmlPanelGroup groupPanel1 = new HtmlPanelGroup();

    public HtmlPanelGroup getGroupPanel1() {
        return this.groupPanel1;
    }

    public void setGroupPanel1(HtmlPanelGroup hpg) {
        this.groupPanel1 = hpg;
    }


}
