/*
 * $Id: ModuleManager.java,v 1.1 2004/11/29 18:10:52 thomas Exp $
 * Created on Nov 10, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.manager.bean;

import java.util.ArrayList;
import java.util.List;
import javax.faces.application.Application;
import javax.faces.component.UIColumn;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.component.html.HtmlForm;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.model.ListDataModel;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.manager.business.PomSorter;
import com.idega.manager.business.PomValidator;
import com.idega.manager.util.ManagerUtils;


/**
 * 
 *  Last modified: $Date: 2004/11/29 18:10:52 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1 $
 */
public class ModuleManager {
	
	private IWResourceBundle resourceBundle;
	private FacesContext context = null;
	private Application application = null;
	private PomValidator pomValidator = null;
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
		initializeOutputText();
		initializeSubmitButtons();
		initializeDataTable1();
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
		
		String[] names = { "weser1", "weser2", "weser3"};
		String[] names2 = { "hallo1", "hallo2", "hallo3"};
		List list = new ArrayList();
		list.add(names);
		list.add(names2);
		dataTable1Model = new ListDataModel(list);
		initializeHtmlDataTable();
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
    	
    private void  initializeHtmlDataTable() {
    	try {
    		// First we remove columns from table
    		List list = dataTable1.getChildren();
    		list.clear();

			
			
    		UpdateListManager manager = (UpdateListManager) getValue("#{UpdateListManager}");
    		manager.getClass();



    
     
//    		// This is data obtained in "executeQuery" method
//    		ResultSet resultSet = (ResultSet)data.getWrappedData();
//     
//    		columnNames = new String[colNumber];
    		UIColumn column;
    		HtmlOutputText outText;
//    		HtmlOutputText headerText;
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
//    			headerText = new HtmlOutputText();
//    			headerText.setTitle(columnNames[i]);
//    			headerText.setValue(columnNames[i]);
//     
    			outText = new HtmlOutputText();
     
    			//String vblExpression = 	"#{" + hdt.getVar() + "." + metaData.getColumnName(i + 1) + "}";
    			String vblExpression = "#{" + dataTable1.getVar() + "[" + Integer.toString(i) + "]}";
    			ValueBinding vb = application.createValueBinding(vblExpression);
    			outText.setValueBinding("value", vb);
     
    			column = new UIColumn();
     
    			column.getChildren().add(outText);
    			//column.setHeader(headerText);
    			// ... we add column to list of table's components
    			list.add(column);
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	// ...and finally, this is our table
    	this.dataTable1 = dataTable1;
    }

	private Object getValue(String valueRef) {
		ValueBinding binding = application.createValueBinding(valueRef);
		return binding.getValue(context);
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
}
