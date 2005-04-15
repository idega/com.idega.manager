/*
 * Created on Feb 14, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.idega.manager.business;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import com.idega.idegaweb.IWResourceBundle;


/**
 * @author thomas
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class UserPasswordValidator {
	
	static private Logger getLogger(){
		 return Logger.getLogger(UserPasswordValidator.class.getName());
	 }

	
	public void validateUserPassword(FacesContext context, UIComponent toValidate, Object componentRepositoryURLValue, Object componentUserValue , Object componentPasswordValue, IWResourceBundle resourceBundle) {
		String repositoryURLValueString = (String) componentRepositoryURLValue;
		String errorMessage = null;
		try {
			URL repositoryURL = new URL(repositoryURLValueString);
			String user = (String) ((componentUserValue == null) ? "" : componentUserValue);
			String password = (String) ((componentPasswordValue == null) ? "" : componentPasswordValue);
			PasswordAuthentication userPassword = new PasswordAuthentication(user, password.toCharArray());
			String error = URLReadConnection.authenticationValid(repositoryURL , userPassword);
			if (error == null) {
				// everything is fine
				return;
			}
			if (URLReadConnection.NOT_FOUND_ERROR.equals(error)) {
				errorMessage = resourceBundle.getLocalizedString("man_URL_could_not_be_found","Could not found URL");
			}
			else if (URLReadConnection.UNAUTHORIZED_ERROR.equals(error)) {
				errorMessage = resourceBundle.getLocalizedString("man_username_or_password_is_wrong","Username or password is wrong");
			}
			else if (URLReadConnection.FORBIDDEN_ERROR.equals(error)) {
				errorMessage = resourceBundle.getLocalizedString("man_forbidden","Access to forbidden");
			}
			else if (URLReadConnection.NO_CONTENT_ERROR.equals(error)) {
				errorMessage = resourceBundle.getLocalizedString("man_no_content","URL does not point to any content");
			}
			else {
				errorMessage = resourceBundle.getLocalizedString("man_unknown_problem_occurred","An unknown problem occurred when trying to login ");
			}
		}
		catch (MalformedURLException e) {
	       	getLogger().log(Level.WARNING, "[UserPasswordValidator] MalformedURLException", e);
	       	errorMessage = resourceBundle.getLocalizedString("man_url_malformed","URL is wrong");
		}
		catch (IOException e) {
	       	getLogger().log(Level.WARNING, "[UserPasswordValidator] IOException", e);
	       	errorMessage = resourceBundle.getLocalizedString("man_unknown_problem_occurred","An unknown problem occurred when connecting to repository");
		}
		setErrorMessage(context, (UIInput) toValidate, errorMessage);
	}

	
	private void setErrorMessage(FacesContext context, UIInput toValidate, String errorMessage) {
		toValidate.setValid(false);
		String id = toValidate.getClientId(context);
		// e.g. id = "form1:multiSelectListbox1"
		context.addMessage(id, new FacesMessage(errorMessage));
	}
}
