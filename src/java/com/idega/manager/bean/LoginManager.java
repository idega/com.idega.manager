/*
 * $Id: LoginManager.java,v 1.3 2005/03/09 15:19:04 thomas Exp $
 * Created on Nov 3, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.manager.bean;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UIViewRoot;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlInputSecret;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.manager.business.UserPasswordValidator;
import com.idega.manager.data.RepositoryLogin;
import com.idega.manager.util.ManagerConstants;
import com.idega.manager.util.ManagerUtils;


/**
 * 
 * Start of the install and update wizard
 * 
 * 1. LoginManager
 * 2. InstallOrUpdateManager
 * 
 * then either
 * 
 * 3.1 InstallListManager
 * 3.2 InstallNewModuleListManager
 * 
 * or
 * 
 * 3.1UpdateListManager
 * 
 * then
 * 
 * 4. ModuleManager
 * 
 * 
 *  Last modified: $Date: 2005/03/09 15:19:04 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.3 $
 */
public class LoginManager {
	
	private IWResourceBundle resourceBundle = null;
	
	private String outputText1Value;
	private String outputText2Value;
	private String outputText3Value;
	private String outputText4Value;
	private String outputText5Value;
	private String button1Label;
	private String button2Label;
	private String button3Label;
	
	private UserPasswordValidator userPasswordValidator = null;
	
	RepositoryLogin repositoryLogin = null;
	
	public LoginManager() {
		initialize();
	}
	
	private void initialize() {
		resourceBundle = ManagerUtils.getInstanceForCurrentContext().getResourceBundle();
		repositoryLogin = null;
		initializeOutputText();
		initializeInputFields();
		initializeSubmitButtons();
	}
	
	private void initializeOutputText() {
		outputText1Value = resourceBundle.getLocalizedString("man_manager_header", "Module Manager");
		outputText2Value = resourceBundle.getLocalizedString("man_manager_login","Login");
		outputText3Value = resourceBundle.getLocalizedString("man_repository", "Repository");
		outputText4Value = resourceBundle.getLocalizedString("man_user_name", "User");
		outputText5Value = resourceBundle.getLocalizedString("man_password","Password");
	}

	private void initializeSubmitButtons() {
		button1Label = resourceBundle.getLocalizedString("man_manager_back","Back");
		button2Label = resourceBundle.getLocalizedString("man_manager_next","Next");
		button3Label = resourceBundle.getLocalizedString("man_manager_cancel","Cancel");
	}
	
	private void initializeInputFields() {
		textField1.setValue(ManagerConstants.IDEGA_REPOSITORY_URL);
	}
	
	public RepositoryLogin getRepositoryLogin() {
		return repositoryLogin;
	}
	
	public void submitForm(ActionEvent event) {
		UIComponent component = event.getComponent();
		UIComponent parentForm = component.getParent();
		UIInput repositoryURLInput = (UIInput) parentForm.findComponent(ManagerConstants.JSF_COMPONENT_ID_REPOSITORY);
		UIInput usernameInput = (UIInput) parentForm.findComponent(ManagerConstants.JSF_COMPONENT_ID_USERNAME);
		UIInput passwordInput = (UIInput) parentForm.findComponent(ManagerConstants.JSF_COMPONENT_ID_PASSWORD);
		String repositoryURL = (String) repositoryURLInput.getValue();
		String userName = (String) usernameInput.getValue();
		String password = (String) passwordInput.getValue();
		repositoryLogin = RepositoryLogin.getInstanceWithAuthentication(repositoryURL, userName, password);
	}
		
		

	
	public void validateUserPassword(FacesContext context, UIComponent toValidate, Object value) {
		// the value of a hidden input is validated because only in this way this method is called even if nothing has been selected.
		// We could use the attribute "required" but this causes problems with the localization of the corresponding error message.
		// get the value of the component we are really interested in....
		UIViewRoot viewRoot = context.getViewRoot();
		UIComponent componentRepositoryURL = viewRoot.findComponent(ManagerConstants.JSF_COMPONENT_ID_REPOSITORY_PATH);
		UIComponent componentUser = viewRoot.findComponent(ManagerConstants.JSF_COMPONENT_ID_USERNAME_PATH);
		UIComponent componentPassword = viewRoot.findComponent(ManagerConstants.JSF_COMPONENT_ID_PASSWORD_PATH);
		Object componentRepositoryURLValue = ((UIInput) componentRepositoryURL).getValue();
		Object componentUserValue = ((UIInput) componentUser).getValue();
		Object componentPasswordValue = ((UIInput) componentPassword).getValue();
		if (userPasswordValidator == null) {
			userPasswordValidator = new UserPasswordValidator(); 
		}
		userPasswordValidator.validateUserPassword(context, toValidate, componentRepositoryURLValue, componentUserValue , componentPasswordValue, resourceBundle);
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

    private HtmlOutputText outputText3 = new HtmlOutputText();

    public HtmlOutputText getOutputText3() {
        return outputText3;
    }

    public void setOutputText3(HtmlOutputText hot) {
        this.outputText3 = hot;
    }

    private HtmlOutputText outputText4 = new HtmlOutputText();

    public HtmlOutputText getOutputText4() {
        return outputText4;
    }

    public void setOutputText4(HtmlOutputText hot) {
        this.outputText4 = hot;
    }

    private HtmlOutputText outputText5 = new HtmlOutputText();

    public HtmlOutputText getOutputText5() {
        return outputText5;
    }

    public void setOutputText5(HtmlOutputText hot) {
        this.outputText5 = hot;
    }

    
    private HtmlInputText textField1 = new HtmlInputText();

    public HtmlInputText getTextField1() {
        return textField1;
    }

    public void setTextField1(HtmlInputText hit) {
        this.textField1 = hit;
    }
    
    private HtmlInputText textField2 = new HtmlInputText();

    public HtmlInputText getTextField2() {
        return textField2;
    }

    public void setTextField2(HtmlInputText hit) {
        this.textField2 = hit;
    }


    private HtmlInputSecret secretField1 = new HtmlInputSecret();

    public HtmlInputSecret getSecretField1() {
        return secretField1;
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
    
    public String getOutputText3Value() {
    	return outputText3Value;
    }
 
    public String getOutputText4Value() {
    	return outputText4Value;
    }

    public String getOutputText5Value() {
    	return outputText5Value;
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
    	return ManagerConstants.ACTION_NEXT;
    }
    
    public String button3_action() {
    	return ManagerConstants.ACTION_CANCEL;
    }
 }
