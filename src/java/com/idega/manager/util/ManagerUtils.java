/*
 * $Id: ManagerUtils.java,v 1.8 2005/01/20 13:04:40 thomas Exp $
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
 *  Last modified: $Date: 2005/01/20 13:04:40 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.8 $
 */
public class ManagerUtils {
	

	public static final String BUNDLE_IDENTIFIER = "com.idega.manager";
	
	public static ManagerUtils getInstanceForCurrentContext() {
		return new ManagerUtils();
	}
	
	private FacesContext facesContext = null;
	private Application application = null;
	private IWContext context = null;
	private IWBundle bundle = null;
	private IWResourceBundle resourceBundle = null;
	private IdegawebDirectoryStructure idegawebDirectoryStructure;

	
	private ManagerUtils() {
		facesContext = FacesContext.getCurrentInstance();
		application = facesContext.getApplication();
		context = IWContext.getIWContext(facesContext);
		idegawebDirectoryStructure = new IdegawebDirectoryStructure(context);
	}
	
	public FacesContext getFacesContext() {
		return facesContext;
	}
	
	public IWContext getIWContext() {
		return context;
	}
	
	public Application getApplication() {
		return application;
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
	
	public Object getValue(String valueRef) {
		ValueBinding binding = application.createValueBinding(valueRef);
		return binding.getValue(context);
	}
	
	private void getBundleAndResourceBundle() {
		bundle = context.getIWMainApplication().getBundle(BUNDLE_IDENTIFIER);
		resourceBundle = bundle.getResourceBundle(context.getExternalContext().getRequestLocale());
	}
	


}
