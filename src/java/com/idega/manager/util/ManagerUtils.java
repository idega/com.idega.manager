/*
 * $Id: ManagerUtils.java,v 1.6 2005/01/07 11:03:35 thomas Exp $
 * Created on Nov 5, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.manager.util;

import java.io.File;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;


/**
 * 
 *  Last modified: $Date: 2005/01/07 11:03:35 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.6 $
 */
public class ManagerUtils {
	

	public static final String BUNDLE_IDENTIFIER = "com.idega.manager";
	
	public static ManagerUtils getInstanceForCurrentContext() {
		return new ManagerUtils();
	}

	private IWContext context = null;
	private IWBundle bundle = null;
	private IWResourceBundle resourceBundle = null;
	private IdegawebDirectoryStructure idegawebDirectoryStructure;

	
	private ManagerUtils() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		context = IWContext.getIWContext(facesContext);
		idegawebDirectoryStructure = new IdegawebDirectoryStructure(context);
	}
	
	public IWBundle getBundle() {
		if (bundle == null) {
			getBundleAndResourceBundle();
		}
		return bundle;
	}
	
	public IWResourceBundle getResourceBundle(){
		if (resourceBundle == null) {
			getBundleAndResourceBundle();
		}
		return resourceBundle;
	}
	
	public IdegawebDirectoryStructure getIdegawebDirectoryStructure() {
		return idegawebDirectoryStructure;
	}
	
	public File getBundlesRealPath() {
		return idegawebDirectoryStructure.getBundlesRealPath();
	}
	
	public File getWorkingDirectory() {
		return idegawebDirectoryStructure.getWorkingDirectory();
	}
	
	public Object getValue(Application application, String valueRef) {
		ValueBinding binding = application.createValueBinding(valueRef);
		return binding.getValue(context);
	}
	
	private void getBundleAndResourceBundle() {
		bundle = context.getIWMainApplication().getBundle(BUNDLE_IDENTIFIER);
		resourceBundle = bundle.getResourceBundle(context.getExternalContext().getRequestLocale());
	}
	


}
