/*
 * $Id: PomValidator.java,v 1.5 2005/01/17 19:14:16 thomas Exp $
 * Created on Nov 24, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.manager.business;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import com.idega.idegaweb.IWResourceBundle;
import com.idega.manager.data.Module;


/**
 * 
 *  Last modified: $Date: 2005/01/17 19:14:16 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.5 $
 */
public class PomValidator {
	
	public void validateSelectedModuleNames(FacesContext context, UIComponent toValidate, Object value, IWResourceBundle resourceBundle) {
		// check if the user has chosen more than one version for the same artifact
		String[] selectedValues = (String[]) value;
		int length = selectedValues.length;
		// if nothing has been chosen, that is the lenght is zero, set an message
		if (length == 0) {
			String errorMessage = resourceBundle.getLocalizedString("man_val_choose_at_most_one_version_per_module","Choose at least one module");
			setErrorMessage(context, (UIInput) toValidate, errorMessage);
		}
	}

	
	public void validateSelectedModules(FacesContext context, UIComponent toValidate, Object value, PomSorter pomSorter, IWResourceBundle resourceBundle) {
		// check if the user has chosen more than one version for the same artifact
		String[] selectedValues = (String[]) value;
		int length = selectedValues.length;
		Map repositoryPoms = pomSorter.getRepositoryPoms();
		Set set = new HashSet(length);
		int i = 0;
		// if nothing has been chosen, that is the lenght is zero, set an message
		boolean go = (length == 0) ? false : true;
		while (( i < length) && go)  {
			String fileName = selectedValues[i++];
			Module module = (Module) repositoryPoms.get(fileName);
			String artifactId = module.getArtifactId();
			// go is not set to false as long as the artifactId is not already in the set
			go = set.add(artifactId);
		}
		if (! go) {
			String errorMessage = resourceBundle.getLocalizedString("man_val_choose_at_most_one_version_per_module","Choose at most one version per module");
			setErrorMessage(context, (UIInput) toValidate, errorMessage);
		}
	}
	
	private void setErrorMessage(FacesContext context, UIInput toValidate, String errorMessage) {
		toValidate.setValid(false);
		String id = toValidate.getClientId(context);
		// e.g. id = "form1:multiSelectListbox1"
		context.addMessage(id, new FacesMessage(errorMessage));
	}
}
