/*
 * $Id: ManagerUtils.java,v 1.4 2004/12/02 18:06:57 thomas Exp $
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
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;


/**
 * 
 *  Last modified: $Date: 2004/12/02 18:06:57 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.4 $
 */
public class ManagerUtils {
	
	public static final String WORKING_DIRRECTORY = "auxiliaryManagerFolder";
	public static final String BUNDLE_IDENTIFIER = "com.idega.manager";
	
	public static ManagerUtils getInstanceForCurrentContext() {
		return new ManagerUtils();
	}
	

	
	private IWContext context;
	private IWBundle bundle = null;
	private IWResourceBundle resourceBundle = null;
	private String bundlesRealPath = null;
	
	private ManagerUtils() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		context = IWContext.getIWContext(facesContext);
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
	
	public String getBundlesRealPath()	{
		if (bundlesRealPath == null) {
			IWMainApplication mainApplication = context.getIWMainApplication();
			bundlesRealPath = mainApplication.getBundlesRealPath();
		}
		return bundlesRealPath;
	}
	
	public File getWorkingDirectory() {
		IWMainApplication mainApplication = context.getIWMainApplication();
		// get the idegaweb folder
		String specialRealPath = mainApplication.getApplicationSpecialRealPath();
		File  idegawebFolder = new File(specialRealPath);
		File workingDir = new File(idegawebFolder, WORKING_DIRRECTORY);
		if (! workingDir.exists()) {
			workingDir.mkdir();
		}
		return workingDir;
		
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
