/*
 * $Id: PomValidator.java,v 1.1 2004/11/26 17:19:09 thomas Exp $
 * Created on Nov 24, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.manager.business;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.manager.data.Pom;
import com.idega.manager.data.ProxyPom;


/**
 * 
 *  Last modified: $Date: 2004/11/26 17:19:09 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1 $
 */
public class PomValidator {
	
	public void validateSelectedModules(FacesContext context, UIComponent toValidate, Object value, PomSorter pomSorter, IWResourceBundle resourceBundle) {
		if (value == null ) {
			return;
		}
		String[] selectedValues = (String[]) value;
		if (selectedValues.length == 0) {
			return;
		}
		// check if the user has chosen more than one version for the same artifact
		Map repositoryPoms = pomSorter.getRepositoryPoms();
		Map selectedPoms = new HashMap();
		for (int i = 0; i < selectedValues.length; i++) {
			ProxyPom proxy = (ProxyPom) repositoryPoms.get(selectedValues[i]);
			String artifactId = proxy.getArtifactId();
			if (selectedPoms.containsKey(artifactId)) {
				((UIInput) toValidate).setValid(false);
				String errorMessage = resourceBundle.getLocalizedString("man_val_choose_at_most_one_version_per_module","Choose at most one version per module");
				String id = toValidate.getClientId(context);
				context.addMessage(id, new FacesMessage(errorMessage));
				return;
			}
			selectedPoms.put(artifactId, proxy);
		}
		// check dependencies
		DependencyMatrix matrix = new DependencyMatrix();
		Iterator iterator = selectedPoms.values().iterator();
		while (iterator.hasNext()) {
			Pom pom = (Pom) iterator.next();
			matrix.addEntry(pom);
		}
		Map installedPoms = pomSorter.getSortedInstalledPoms();
		Iterator installedPomsIterator = installedPoms.values().iterator();
		while (installedPomsIterator.hasNext()) {
			Pom pom = (Pom) installedPomsIterator.next();
			matrix.addEntry(pom);
		}

	}
}
