/*
 * $Id: InstallListManager.java,v 1.3 2005/02/23 18:02:17 thomas Exp $
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
import java.util.Set;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UISelectItems;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlForm;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.component.html.HtmlSelectManyListbox;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.manager.business.PomSorter;
import com.idega.manager.business.PomValidator;
import com.idega.manager.data.ProxyPom;
import com.idega.manager.data.RepositoryLogin;
import com.idega.manager.util.ManagerConstants;
import com.idega.manager.util.ManagerUtils;
import com.idega.util.datastructures.SortedByValueMap;


/**
 * 
 *  Last modified: $Date: 2005/02/23 18:02:17 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.3 $
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
		resourceBundle = ManagerUtils.getInstanceForCurrentContext().getResourceBundle();
		initializePomSorter();
		initializeOutputText();
		initializeSubmitButtons();
	}
	
	private void initializePomSorter() {
		if (pomSorter == null) {
			pomSorter = ManagerUtils.getPomSorter();
		}
	}
	
	
	private void initializeOutputText() {
		outputText1Value = resourceBundle.getLocalizedString("man_manager_header", "Manager");
		outputText2Value = resourceBundle.getLocalizedString("man_manager_select _new_modules","Select new modules you wish to install");
	}

	private void initializeSubmitButtons() {
		button1Label = resourceBundle.getLocalizedString("man_manager_back","Back");
		button2Label = resourceBundle.getLocalizedString("man_manager_next","Next");
		button3Label = resourceBundle.getLocalizedString("man_manager_cancel","Cancel");
	}
	
	private void initializeList() {
		 multiSelectListbox1DefaultItems = new ArrayList();
		 String errorMessage = null;
			RepositoryLogin repositoryLogin = ManagerUtils.getRepositoryLogin();
		 try {
		 	pomSorter.initializeInstalledPomsAndAvailableNewModules(repositoryLogin); 
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
		 
		 Map repositoryPom = pomSorter.getSortedRepositoryPomsOfAvailableNewModules();
		 Map listItems = new HashMap();
		 Iterator iterator = repositoryPom.keySet().iterator();
		 while (iterator.hasNext()) {
		 	String artifactId = (String) iterator.next();
		 	Set pomSet = (Set) repositoryPom.get(artifactId);
		 	ProxyPom proxyPom = (ProxyPom) pomSet.iterator().next();
		 	String name = proxyPom.getNameForLabel(resourceBundle);
		 	SelectItem item = new SelectItem(artifactId, name);
	 	 	listItems.put(item, name);
	 	}
		Locale locale = resourceBundle.getLocale();
		SortedByValueMap sortedMap = new SortedByValueMap(listItems, locale);
		Iterator valueIterator = sortedMap.keySet().iterator();
		while (valueIterator.hasNext()) {
			SelectItem item = (SelectItem) valueIterator.next();
			 multiSelectListbox1DefaultItems.add(item);	
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
		if (pomValidator == null) {
			pomValidator = new PomValidator();
		}
		pomValidator.validateSelectedModuleNames(context, toValidate, componentValue, resourceBundle);
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
        return groupPanel1;
    }

    public void setGroupPanel1(HtmlPanelGroup hpg) {
        this.groupPanel1 = hpg;
    }


}
