/*
 * $Id: ModuleManager.java,v 1.6 2004/12/08 17:36:53 thomas Exp $
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
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIColumn;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.component.html.HtmlForm;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.ActionEvent;
import javax.faces.model.ListDataModel;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.manager.business.Installer;
import com.idega.manager.business.PomSorter;
import com.idega.manager.data.Module;
import com.idega.manager.util.ManagerUtils;


/**
 * 
 *  Last modified: $Date: 2004/12/08 17:36:53 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.6 $
 */
public class ModuleManager {
	
	private static final String ACTION_INSTALL_MANAGER = "installManager";
	
	private static final String JSF_VALUE_REFERENCE_UPDATE_LIST_MANAGER = "#{UpdateListManager}";
	private static final String JSF_ID_REFERENCE_FORM1 = "form1";
	
	private IWResourceBundle resourceBundle;
	private FacesContext context = null;
	private Application application = null;
	private PomSorter pomSorter = null;
	
	private String outputText1Value;
	private String outputText2Value;
	private String button1Label;
	private String button2Label;
	private String button3Label;
	
	public ModuleManager() {
		initialize();
	}
	
	private void initialize() {
		context  = FacesContext.getCurrentInstance();
		application = context.getApplication();
		resourceBundle = ManagerUtils.getInstanceForCurrentContext().getResourceBundle();
		initializePomSorter();
		initializeOutputText();
		initializeSubmitButtons();
		initializeDataTable1();
	}
	
	private void initializePomSorter() {
		if (pomSorter == null) {
			UpdateListManager updateListManager = (UpdateListManager) ManagerUtils.getInstanceForCurrentContext().getValue(application, JSF_VALUE_REFERENCE_UPDATE_LIST_MANAGER);
			if (updateListManager != null) {
				pomSorter = updateListManager.getPomSorter();
				initializeErrorMessages(context, JSF_ID_REFERENCE_FORM1, pomSorter);
			}
		}
	}
	
	private void initializeOutputText() {
		outputText1Value = resourceBundle.getLocalizedString("man_manager_header", "Manager");
		outputText2Value = resourceBundle.getLocalizedString("man_mamager_choose","Choose one option");
	}

	private void initializeSubmitButtons() {
		button1Label = resourceBundle.getLocalizedString("man_manager_back","Back");
		button2Label = resourceBundle.getLocalizedString("man_manager_next","Next");
		button3Label = resourceBundle.getLocalizedString("man_manager_cancel","Cancel");
	}
	
	private void initializeDataTable1() {
		String noPreviousVersionInstalled = resourceBundle.getLocalizedString("man_manager_no_previous_version_installed","No previous version installed");
		String snapshot = resourceBundle.getLocalizedString("man_manager_snapshot", "Snapshot");
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
			Iterator iterator = toBeInstalled.values().iterator();
			while (iterator.hasNext()) {
				Module module = (Module) iterator.next();
				String artifactId = module.getArtifactId();
				String version = null;
				if (module.isSnapshot()) {
					version = snapshot;
				}
				else {
					version = module.getCurrentVersion();
				}
				Module oldPom = (Module) sortedInstalledMap.get(artifactId);
				String oldVersion = (oldPom == null) ? noPreviousVersionInstalled : oldPom.getCurrentVersion();
				String[] row = {artifactId, version, oldVersion};
				rows.add(row);
			}
		}
		dataTable1Model = new ListDataModel(rows);
		// initialize columnNames
		String module = resourceBundle.getLocalizedString("man_manager_module", "Module");
		String version = resourceBundle.getLocalizedString("man_manager_module", "Version");
		String oldVersion = resourceBundle.getLocalizedString("man_manager_old_version","Old version");
		String[] columnNames = {module, version, oldVersion};
		initializeHtmlDataTable(columnNames);
	}	

	private void initializeErrorMessages(FacesContext tempContext, String id, PomSorter tempPomSorter) {
		List errorMessages = tempPomSorter.getErrorMessages();
		if (errorMessages == null) {
			return;
		}
		Iterator iterator = errorMessages.iterator();
		while (iterator.hasNext()) {
			String errorMessage = (String) iterator.next();
			tempContext.addMessage(id, new FacesMessage(errorMessage));
		}
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
     
    			outText = new HtmlOutputText();
     
    			//String vblExpression = 	"#{" + hdt.getVar() + "." + metaData.getColumnName(i + 1) + "}";
    			String vblExpression = "#{" + dataTable1.getVar() + "[" + Integer.toString(i) + "]}";
    			ValueBinding vb = application.createValueBinding(vblExpression);
    			outText.setValueBinding("value", vb);
     
    			column = new UIColumn();
     
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
			if (pomSorter.getErrorMessages() == null) {
				Collection toBeInstalled = pomSorter.getToBeInstalledPoms().values();
				Installer installer = Installer.getInstance(toBeInstalled);
				try {
					installer.getBundleArchives();
				}
				catch (IOException ex) {
					// what next?
				}
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
    
    public String button2_action() {
    	return ACTION_INSTALL_MANAGER;
    }
}
