/*
 * $Id: ManagerUtils.java,v 1.10 2006/04/09 11:42:59 laddi Exp $
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
import com.idega.manager.bean.InstallListManager;
import com.idega.manager.bean.InstallNewModuleListManager;
import com.idega.manager.bean.InstallOrUpdateManager;
import com.idega.manager.bean.LoginManager;
import com.idega.manager.bean.ModuleManager;
import com.idega.manager.bean.UpdateListManager;
import com.idega.manager.business.PomSorter;
import com.idega.manager.data.RepositoryLogin;
import com.idega.presentation.IWContext;


/**
 * 
 *  Last modified: $Date: 2006/04/09 11:42:59 $ by $Author: laddi $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.10 $
 */
public class ManagerUtils {
	

	public static final String BUNDLE_IDENTIFIER = "com.idega.manager";
	
	public static ManagerUtils getInstanceForCurrentContext() {
		return new ManagerUtils();
	}
	
	public static InstallListManager getInstallListManager() {
		return (InstallListManager) getInstanceForCurrentContext().getValue(ManagerConstants.JSF_VALUE_REFERENCE_INSTALL_LIST_MANAGER);
	}
	
	public static UpdateListManager getUpdateListManager() {
		return (UpdateListManager) getInstanceForCurrentContext().getValue(ManagerConstants.JSF_VALUE_REFERENCE_UPDATE_LIST_MANAGER);
	}
	
	public static InstallNewModuleListManager getInstallNewModuleListManager() {
		return (InstallNewModuleListManager) getInstanceForCurrentContext().getValue(ManagerConstants.JSF_VALUE_REFERENCE_INSTALL_NEW_MODULE_LIST_MANAGER);
	}
	
	public static ModuleManager getModuleManager() 	{
		return (ModuleManager) getInstanceForCurrentContext().getValue(ManagerConstants.JSF_VALUE_REFERENCE_MODULE_MANAGER);
	}
	
	public static PomSorter getPomSorter() {
		InstallOrUpdateManager installOrUpdateManager = (InstallOrUpdateManager) getInstanceForCurrentContext().getValue(ManagerConstants.JSF_VALUE_REFERENCE_INSTALL_OR_UPDATE_MANAGER);
		return installOrUpdateManager.getPomSorter();
	}
	
	public static RepositoryLogin getRepositoryLogin() {
		LoginManager loginManager = (LoginManager) getInstanceForCurrentContext().getValue(ManagerConstants.JSF_VALUE_REFERENCE_LOGIN_MANAGER);
		return loginManager.getRepositoryLogin();
	}
	
	private FacesContext facesContext = null;
	private Application application = null;
	private IWContext context = null;
	private IWBundle bundle = null;
	private IWResourceBundle resourceBundle = null;
	private IdegawebDirectoryStructure idegawebDirectoryStructure;

	
	private ManagerUtils() {
		this.facesContext = FacesContext.getCurrentInstance();
		this.application = this.facesContext.getApplication();
		this.context = IWContext.getIWContext(this.facesContext);
		this.idegawebDirectoryStructure = new IdegawebDirectoryStructure(this.context);
	}
	
	public FacesContext getFacesContext() {
		return this.facesContext;
	}
	
	public IWContext getIWContext() {
		return this.context;
	}
	
	public Application getApplication() {
		return this.application;
	}
	
	public IWBundle getBundle() {
		if (this.bundle == null) {
			getBundleAndResourceBundle();
		}
		return this.bundle;
	}
	
	public IWResourceBundle getResourceBundle(){
		if (this.resourceBundle == null) {
			getBundleAndResourceBundle();
		}
		return this.resourceBundle;
	}
	
	public IdegawebDirectoryStructure getIdegawebDirectoryStructure() {
		return this.idegawebDirectoryStructure;
	}
	
	public File getBundlesRealPath() {
		return this.idegawebDirectoryStructure.getBundlesRealPath();
	}
	
	public File getWorkingDirectory() {
		return this.idegawebDirectoryStructure.getWorkingDirectory();
	}
	
	public Object getValue(String valueRef) {
		ValueBinding binding = this.application.createValueBinding(valueRef);
		return binding.getValue(this.context);
	}
	
	private void getBundleAndResourceBundle() {
		this.bundle = this.context.getIWMainApplication().getBundle(BUNDLE_IDENTIFIER);
		this.resourceBundle = this.bundle.getResourceBundle(this.context.getExternalContext().getRequestLocale());
	}


}
