/*
 * $Id: InstallOrUpdateManager.java,v 1.3 2005/01/19 18:24:29 thomas Exp $
 * Created on Nov 3, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.manager.bean;

import java.util.ArrayList;
import java.util.List;
import javax.faces.component.UISelectItems;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlForm;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.component.html.HtmlSelectOneRadio;
import javax.faces.model.SelectItem;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.manager.business.PomSorter;
import com.idega.manager.util.ManagerUtils;


/**
 * 
 *  Last modified: $Date: 2005/01/19 18:24:29 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.3 $
 */
public class InstallOrUpdateManager {
	
	private static final String JSF_VALUE_REFERENCE_INSTALL_LIST_MANAGER = "#{InstallListManager}";
	private static final String JSF_VALUE_REFERENCE_UPDATE_LIST_MANAGER = "#{UpdateListManager}";
	private static final String INSTALL_NEW_MODULES = "installNewModules";
	private static final String UPDATE_MODULES = "updateModules";
	
	private ManagerUtils managerUtils = null;
	
	private String outputText1Value;
	private String outputText2Value;
	private String button1Label;
	private String button2Label;
	private String button3Label;
	
	private PomSorter pomSorter = null;
	
	public InstallOrUpdateManager() {
		initialize();
	}
	
	private void initialize() {
		managerUtils = ManagerUtils.getInstanceForCurrentContext();
		initializePomSorter();
		initializeOutputText();
		initializeSubmitButtons();
		initializeRadioButtons();
	}
	
	private void initializePomSorter() {
		pomSorter = new PomSorter();
	}
	
	private void initializeOutputText() {
		IWResourceBundle resourceBundle = managerUtils.getResourceBundle();
		outputText1Value = resourceBundle.getLocalizedString("man_manager_header", "Manager");
		outputText2Value = resourceBundle.getLocalizedString("man_mamager_choose","Choose one option");
	}

	private void initializeSubmitButtons() {
		IWResourceBundle resourceBundle = managerUtils.getResourceBundle();
		button1Label = resourceBundle.getLocalizedString("man_manager_back","Back");
		button2Label = resourceBundle.getLocalizedString("man_manager_next","Next");
		button3Label = resourceBundle.getLocalizedString("man_manager_cancel","Cancel");
	}
	
	
	private void initializeRadioButtons() {
		IWResourceBundle resourceBundle = managerUtils.getResourceBundle();
		String installNewModules = resourceBundle.getLocalizedString("man_install_new_modules", "Install new modules");
		String updateModules = resourceBundle.getLocalizedString("man_update_installed_modules","Update installed modules");
		radioButtonList1DefaultItems = new ArrayList(2);
		radioButtonList1DefaultItems.add(new SelectItem(INSTALL_NEW_MODULES, installNewModules));
		radioButtonList1DefaultItems.add( new SelectItem(UPDATE_MODULES, updateModules));
		radioButtonList1.setValue(INSTALL_NEW_MODULES);
	}
	
	public PomSorter getPomSorter() {
		return pomSorter;
	}
	
    private HtmlForm form1 = new HtmlForm();

    public HtmlForm getForm1() {
        return form1;
    }

    public void setForm1(HtmlForm hf) {
        this.form1 = hf;
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

    private HtmlPanelGroup groupPanel1 = new HtmlPanelGroup();

    public HtmlPanelGroup getGroupPanel1() {
        return groupPanel1;
    }

    public void setGroupPanel1(HtmlPanelGroup hpg) {
        this.groupPanel1 = hpg;
    }

    private HtmlSelectOneRadio radioButtonList1 = new HtmlSelectOneRadio();

    public HtmlSelectOneRadio getRadioButtonList1() {
        return radioButtonList1;
    }

    public void setRadioButtonList1(HtmlSelectOneRadio hsor) {
        this.radioButtonList1 = hsor;
    }

    private List radioButtonList1DefaultItems = new ArrayList();

    public List getRadioButtonList1DefaultItems() {
        return radioButtonList1DefaultItems;
    }

    public void setRadioButtonList1DefaultItems(List dsia) {
        this.radioButtonList1DefaultItems = dsia;
    }

    private UISelectItems radioButtonList1SelectItems = new UISelectItems();

    public UISelectItems getRadioButtonList1SelectItems() {
        return radioButtonList1SelectItems;
    }

    public void setRadioButtonList1SelectItems(UISelectItems uisi) {
        this.radioButtonList1SelectItems = uisi;
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
    	String action = (String) radioButtonList1.getValue();
    	if (INSTALL_NEW_MODULES.equals(action)) {
    		InstallListManager installListManager = (InstallListManager) ManagerUtils.getInstanceForCurrentContext().getValue(JSF_VALUE_REFERENCE_INSTALL_LIST_MANAGER);
    		if (installListManager != null) {
    			installListManager.initializeDynamicContent();
    		}
    	}
    	else if (UPDATE_MODULES.equals(action)) {
    		UpdateListManager updateListManager = (UpdateListManager) ManagerUtils.getInstanceForCurrentContext().getValue(JSF_VALUE_REFERENCE_UPDATE_LIST_MANAGER);
    		if (updateListManager != null) {
    			updateListManager.initializeDynamicContent();
    		}
    	}
    	return action;
    }
}
