/*
 * $Id: PomValidator.java,v 1.3 2004/12/08 17:36:53 thomas Exp $
 * Created on Nov 24, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.manager.business;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import com.idega.idegaweb.IWResourceBundle;


/**
 * 
 *  Last modified: $Date: 2004/12/08 17:36:53 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.3 $
 */
public class PomValidator {
	
	public void validateSelectedModules(FacesContext context, UIComponent toValidate, Object value, IWResourceBundle resourceBundle) {
		if (value == null ) {
			return;
		}
		String[] selectedValues = (String[]) value;
		if (selectedValues.length == 0) {
			return;
		}
		// check if the user has chosen more than one version for the same artifact
		List selectedValuesAsList = Arrays.asList(selectedValues);
		Set set = new HashSet(selectedValuesAsList);
		if (set.size() != selectedValuesAsList.size()) {
			((UIInput) toValidate).setValid(false);
			String errorMessage = resourceBundle.getLocalizedString("man_val_choose_at_most_one_version_per_module","Choose at most one version per module");
			String id = toValidate.getClientId(context);
			// e.g. id = "form1:multiSelectListbox1"
			context.addMessage(id, new FacesMessage(errorMessage));
		}
	}
}
