/*
 * Created on Jan 5, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.idega.manager.util;


/**
 * @author thomas
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ManagerConstants {
	
	public static final String ARTIFACT_ID_VERSION_SEPARATOR = "-";
	
	public static final String JAR_EXTENSION = "jar";
	
	public static final String BUNDLE_EXTENSION = "bundle";
	
	// for testing use this one: user is "joeuser" pasword: "a.b.C.D"
	//public static final String IDEGA_REPOSITORY_URL =  "http://www.rahul.net/joeuser/";
	public static final String IDEGA_REPOSITORY_URL = "http://repository.idega.com/maven/";    
	
	// JSF input references
	public static final String JSF_COMPONENT_ID_MULTI_SELECT_1 = "form1:multiSelectListbox1";
	
	// for input login
	public static final String JSF_COMPONENT_ID_REPOSITORY = "textField1";
	public static final String JSF_COMPONENT_ID_REPOSITORY_PATH = "form1:" + JSF_COMPONENT_ID_REPOSITORY;
	public static final String JSF_COMPONENT_ID_USERNAME = "textField2";
	public static final String JSF_COMPONENT_ID_USERNAME_PATH = "form1:" + JSF_COMPONENT_ID_USERNAME;
	public static final String JSF_COMPONENT_ID_PASSWORD = "secretField1";
	public static final String JSF_COMPONENT_ID_PASSWORD_PATH = "form1:" + JSF_COMPONENT_ID_PASSWORD;
	
	// navigation rules
	public static final String ACTION_NEXT = "next";
	public static final String ACTION_BACK = "back";
	public static final String ACTION_CANCEL = "cancel";
	public static final String ACTION_INSTALL_NEW_MODULES = "installNewModules";
	public static final String ACTION_UPDATE_MODULES = "updateModules";
	public static final String ACTION_BACK_INSTALL = "backInstall";
	public static final String ACTION_BACK_UPDATE = "backUpdate";
	
	// class references 
	public static final String JSF_VALUE_REFERENCE_INSTALL_LIST_MANAGER = "#{InstallListManager}";
	
	public static final String JSF_VALUE_REFERENCE_UPDATE_LIST_MANAGER = "#{UpdateListManager}";
	
	public static final String JSF_VALUE_REFERENCE_MODULE_MANAGER = "#{ModuleManager}";
	
	public static final String JSF_VALUE_REFERENCE_LOGIN_MANAGER = "#{LoginManager}";
	
	public static final String JSF_VALUE_REFERENCE_INSTALL_OR_UPDATE_MANAGER = "#{InstallOrUpdateManager}";
	
	public static final String JSF_VALUE_REFERENCE_INSTALL_NEW_MODULE_LIST_MANAGER = "#{InstallNewModuleListManager}";
}
