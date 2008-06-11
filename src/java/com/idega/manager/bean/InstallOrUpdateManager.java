/*
 * $Id: InstallOrUpdateManager.java,v 1.7 2008/06/11 21:10:01 tryggvil Exp $
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
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.component.html.HtmlSelectOneRadio;
import javax.faces.model.SelectItem;

import com.idega.idegaweb.IWResourceBundle;
import com.idega.manager.maven1.business.PomSorter;
import com.idega.manager.maven1.util.ManagerConstants;
import com.idega.manager.maven1.util.ManagerUtils;


/**
 * 
 *  Last modified: $Date: 2008/06/11 21:10:01 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.7 $
 */
public class InstallOrUpdateManager {
	
	private IWResourceBundle resourceBundle = null;
	
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
		this.resourceBundle = ManagerUtils.getInstanceForCurrentContext().getResourceBundle();
		initializePomSorter();
		initializeOutputText();
		initializeSubmitButtons();
		initializeRadioButtons();
	}
	
	private void initializePomSorter() {
		this.pomSorter = new PomSorter();
	}
	
	private void initializeOutputText() {
		this.outputText1Value = this.resourceBundle.getLocalizedString("man_manager_header", "Manager");
		this.outputText2Value = this.resourceBundle.getLocalizedString("man_mamager_choose","Choose one option");
	}

	private void initializeSubmitButtons() {
		this.button1Label = this.resourceBundle.getLocalizedString("man_manager_back","Back");
		this.button2Label = this.resourceBundle.getLocalizedString("man_manager_next","Next");
		this.button3Label = this.resourceBundle.getLocalizedString("man_manager_cancel","Cancel");
	}
	
	private void initializeRadioButtons() {
		String installNewModules = this.resourceBundle.getLocalizedString("man_install_new_modules", "Install new modules");
		String updateModules = this.resourceBundle.getLocalizedString("man_update_installed_modules","Update installed modules");
		this.radioButtonList1DefaultItems = new ArrayList(2);
		this.radioButtonList1DefaultItems.add(new SelectItem(ManagerConstants.ACTION_INSTALL_NEW_MODULES, installNewModules));
		this.radioButtonList1DefaultItems.add( new SelectItem(ManagerConstants.ACTION_UPDATE_MODULES, updateModules));
		this.radioButtonList1.setValue(ManagerConstants.ACTION_INSTALL_NEW_MODULES);
	}
	
	public PomSorter getPomSorter() {
		return this.pomSorter;
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

    private HtmlPanelGroup groupPanel1 = new HtmlPanelGroup();

    public HtmlPanelGroup getGroupPanel1() {
        return this.groupPanel1;
    }

    public void setGroupPanel1(HtmlPanelGroup hpg) {
        this.groupPanel1 = hpg;
    }

    private HtmlSelectOneRadio radioButtonList1 = new HtmlSelectOneRadio();

    public HtmlSelectOneRadio getRadioButtonList1() {
        return this.radioButtonList1;
    }

    public void setRadioButtonList1(HtmlSelectOneRadio hsor) {
        this.radioButtonList1 = hsor;
    }

    private List radioButtonList1DefaultItems = new ArrayList();

    public List getRadioButtonList1DefaultItems() {
        return this.radioButtonList1DefaultItems;
    }

    public void setRadioButtonList1DefaultItems(List dsia) {
        this.radioButtonList1DefaultItems = dsia;
    }

    private UISelectItems radioButtonList1SelectItems = new UISelectItems();

    public UISelectItems getRadioButtonList1SelectItems() {
        return this.radioButtonList1SelectItems;
    }

    public void setRadioButtonList1SelectItems(UISelectItems uisi) {
        this.radioButtonList1SelectItems = uisi;
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
    	
    public String button2_action() {
    	String action = (String) this.radioButtonList1.getValue();
    	if (ManagerConstants.ACTION_INSTALL_NEW_MODULES.equals(action)) {
    		InstallListManager installListManager = ManagerUtils.getInstallListManager();
    		if (installListManager != null) {
    			installListManager.initializeDynamicContent();
    		}
    	}
    	else if (ManagerConstants.ACTION_UPDATE_MODULES.equals(action)) {
    		UpdateListManager updateListManager = ManagerUtils.getUpdateListManager();
    		if (updateListManager != null) {
    			updateListManager.initializeDynamicContent();
    		}
    	}
    	return action;
    }
    
    public String button3_action() {
    	return ManagerConstants.ACTION_CANCEL;
    }
}
