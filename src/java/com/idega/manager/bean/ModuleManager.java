/*
 * $Id: ModuleManager.java,v 1.17 2005/03/02 16:50:02 thomas Exp $
 * Created on Nov 10, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.manager.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import javax.faces.application.Application;
import javax.faces.component.UIColumn;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.component.html.HtmlForm;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.el.ValueBinding;
import javax.faces.event.ActionEvent;
import javax.faces.model.ListDataModel;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.manager.business.ApplicationUpdater;
import com.idega.manager.business.PomSorter;
import com.idega.manager.data.Module;
import com.idega.manager.util.ManagerConstants;
import com.idega.manager.util.ManagerUtils;
import com.idega.util.datastructures.SortedByValueMap;


/**
 * 
 *  Last modified: $Date: 2005/03/02 16:50:02 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.17 $
 */
public class ModuleManager {
	
	private IWResourceBundle resourceBundle = null;
	private PomSorter pomSorter = null;
	
	private String outputText1Value;
	private String outputText2Value;
	private String button1Label;
	private String button2Label;
	private String button3Label;
	
	private String actionBack = ManagerConstants.ACTION_BACK_UPDATE;
	private String actionNext = "";
	private String actionNextChangeToNewValue = null;
	
	public ModuleManager() {
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
		outputText1Value = resourceBundle.getLocalizedString("man_manager_header", "Module Manager");
		outputText2Value = resourceBundle.getLocalizedString("man_manager_do_you_want_to_install_modules","Do you want to install the following modules?");
	}

	private void initializeSubmitButtons() {
		button1Label = resourceBundle.getLocalizedString("man_manager_back","Back");
		button2Label = resourceBundle.getLocalizedString("man_manager_next","Install");
		button3Label = resourceBundle.getLocalizedString("man_manager_cancel","Cancel");
	}
	
	private void initializeDataTable1() {
		String noPreviousVersionInstalled = resourceBundle.getLocalizedString("man_manager_no_previous_version_installed","No previous version installed");
		List rows = new ArrayList();
		SortedMap toBeInstalled = null;
		SortedMap sortedInstalledMap = null;
		if (pomSorter != null) {
			toBeInstalled = pomSorter.getToBeInstalledPoms();
			sortedInstalledMap =pomSorter.getSortedInstalledPoms();
		}
		if (toBeInstalled == null || toBeInstalled.isEmpty()) {
			String noModulesNeedToBeInstalled = resourceBundle.getLocalizedString("man_manager_no_modules_need_to_be_installed","No modules need to be installed");
			String[] firstRow = { noModulesNeedToBeInstalled };
			rows.add(firstRow);
		}
		else {
			Map tableRows = new HashMap();
			Iterator iterator = toBeInstalled.values().iterator();
			while (iterator.hasNext()) {
				Module module = (Module) iterator.next();
				String name = module.getNameForLabel(resourceBundle);
				String version = module.getCurrentVersionForLabel(resourceBundle);
				String artifactId = module.getArtifactId();
				Module oldPom = (Module) sortedInstalledMap.get(artifactId);
				String oldVersion = (oldPom == null) ? noPreviousVersionInstalled : oldPom.getCurrentVersionForLabel(resourceBundle);
				String[] row = {name, version, oldVersion};
				tableRows.put(row, name);
			}
			Locale locale = resourceBundle.getLocale();
			SortedByValueMap sortedMap = new SortedByValueMap(tableRows, locale);
			Iterator valueIterator = sortedMap.keySet().iterator();
			while (valueIterator.hasNext()) {
				String[] row = (String[]) valueIterator.next();
				rows.add(row);	
			}
		}			
		dataTable1Model = new ListDataModel(rows);
		// initialize columnNames
		String module = resourceBundle.getLocalizedString("man_manager_module", "Module");
		String version = resourceBundle.getLocalizedString("man_manager_module", "New Version");
		String oldVersion = resourceBundle.getLocalizedString("man_manager_old_version","Old version");
		String[] columnNames = {module, version, oldVersion};
		initializeHtmlDataTable(columnNames);
	}	

	private void initializeErrorMessages() {
		List errorMessages = pomSorter.getErrorMessages();
		initializeErrorMessages(errorMessages);
	}
		
	private void initializeErrorMessages(List errorMessages) {
		HtmlPanelGroup group = getGroupPanel1();
		List list = group.getChildren();
		list.clear();
		button2.setDisabled(false);
		button2Label = resourceBundle.getLocalizedString("man_manager_next","Install");
		outputText2Value = resourceBundle.getLocalizedString("man_manager_do_you_want_to_install_modules","Do you want to install the following modules?");
		if (errorMessages != null) {
			outputText2Value = resourceBundle.getLocalizedString("man_manager_successl","Problems occurred, you can not proceed");
			button2.setDisabled(true);
			Iterator iterator = errorMessages.iterator();
			while (iterator.hasNext()) {
				String errorMessage = (String) iterator.next();
				errorMessage = errorMessage + " <br/>";
				HtmlOutputText error = new HtmlOutputText();
				error.setValue(errorMessage);
				error.setStyle("color: red");
				error.setEscape(false);
				list.add(error);
			}
		}
	}
	
	public void initializeDynamicContent() {
		initializeDataTable1();
		initializeErrorMessages();
	}
	
	private HtmlForm form1 = new HtmlForm();

    public HtmlForm getForm1() {
        return form1;
    }

    public void setForm1(HtmlForm hf) {
        this.form1 = hf;
    }

    private HtmlDataTable dataTable1 = new HtmlDataTable();

    public HtmlDataTable getDataTable1() {
    	return dataTable1;
    }

    public void setDataTable1(HtmlDataTable dataTable1) {
    	this.dataTable1 = dataTable1;
    }
    	
    private void  initializeHtmlDataTable(String[] columnNames) {
    	Application application = ManagerUtils.getInstanceForCurrentContext().getApplication();
    	try {
    		// First we remove columns from table
    		List list = dataTable1.getChildren();
    		list.clear();
//    		// This is data obtained in "executeQuery" method
//    		ResultSet resultSet = (ResultSet)data.getWrappedData();
//     
//    		columnNames = new String[colNumber];
    		UIColumn column;
    		HtmlOutputText outText;
    		HtmlOutputText headerText;
//     
//    		ResultSetMetaData metaData = resultSet.getMetaData();
    		dataTable1.setVar("currentRow");
    		dataTable1.setColumnClasses("moduleManagerBigColumnClass, moduleManagerColumnClass, moduleManagerColumnClass");
//			dataTable1.setHeaderClass("moduleManagerHeaderClass");
    		
    		// "currentRow" must be set in the corresponding JSP page!
    		// this variable is used above by the method call dataTable1.getVar()
     
    		// In this loop we are going to add columns to table,
    		// and to make sure that all column are populated (table.getVar is taking care of that).
    		// As you can se we will add "headerText" and "outText" to "column",
    		// same way we would add "<f:facet name="header">" and "<h:outputtext>" to "<h:column>"
    		// Notice that "var" is previously set to "dbTable" and now we are using it.
    		for (int i = 0; i < 3; i++) {
//    			columnNames[i] = metaData.getColumnName(i + 1);
//     
    			headerText = new HtmlOutputText();
    			headerText.setTitle(columnNames[i]);
    			headerText.setValue(columnNames[i]);
    			//headerText.setStyleClass("moduleManagerHeaderClass");
     
    			outText = new HtmlOutputText();
     
    			//String vblExpression = 	"#{" + hdt.getVar() + "." + metaData.getColumnName(i + 1) + "}";
    			String vblExpression = "#{" + dataTable1.getVar() + "[" + Integer.toString(i) + "]}";
    			ValueBinding vb = application.createValueBinding(vblExpression);
    			outText.setValueBinding("value", vb);
     
    			column = new UIColumn();


    			//map.put("width","444px");
     
    			column.getChildren().add(outText);
    			column.setHeader(headerText);
    			// ... we add column to list of table's components
    			list.add(column);
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }

	public void submitForm(ActionEvent event) {
		if (pomSorter != null) {
			ApplicationUpdater updater = new ApplicationUpdater(pomSorter);
			if (! updater.installModules()) { 
				String errorMessage = updater.getErrorMessage();
				List errorMessages = new ArrayList(1);
				errorMessages.add(errorMessage);
				initializeErrorMessages(errorMessages);
			}
			else {
				outputText2Value = resourceBundle.getLocalizedString("man_manager_success","Modules have been successfully installed");
				button1.setDisabled(true);
				button2.setDisabled(true);
				button3.setDisabled(true);
				pomSorter = null;
//				button2Label = resourceBundle.getLocalizedString("man_manager_finish","Finish");
//				actionNextChangeToNewValue = ManagerConstants.ACTION_CANCEL;
			}
		}
	}
    
    
    private ListDataModel dataTable1Model = new ListDataModel();

    public ListDataModel getDataTable1Model() {
        return dataTable1Model;
    }

    public void setDataTable1Model(ListDataModel dtdm) {
        this.dataTable1Model = dtdm;
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
    
    public void setActionBack(String actionBack) {
    	this.actionBack = actionBack;
    }
    
    public String button1_action() {
    	return actionBack;
    }
    
    // first submitForm is called then this method is invoked 
    // change the value after returning the old value!
    public String button2_action() {
		String returnValue = actionNext;
    	if (actionNextChangeToNewValue != null) {
    		actionNext = actionNextChangeToNewValue;
    		actionNextChangeToNewValue = null;
    	}
    	return returnValue;
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
