/*
 * $Id: ManagerUtils.java,v 1.3 2004/11/26 17:19:09 thomas Exp $
 * Created on Nov 5, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.manager.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.faces.context.FacesContext;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.manager.business.RepositoryBrowser;
import com.idega.manager.data.RealPom;
import com.idega.presentation.IWContext;


/**
 * 
 *  Last modified: $Date: 2004/11/26 17:19:09 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.3 $
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


	
	
	private void getBundleAndResourceBundle() {
		bundle = context.getIWMainApplication().getBundle(BUNDLE_IDENTIFIER);
		resourceBundle = bundle.getResourceBundle(context.getExternalContext().getRequestLocale());
	}
	

	
	public List getList() {
		RepositoryBrowser repositoryBrowser = new RepositoryBrowser("http://repository.idega.com/maven");
		List pomNames = repositoryBrowser.getPoms();
		pomNames.size();
		repositoryBrowser.getPom("com.idega.block.albumcollection-20040730.132348.pom");
		return null;
	}
//	Group group = new Group("bundles",repository);
//	Type type = new Type("iwbar", group);
//	TypeBrowser browser = new TypeBrowser(type);
//	//getInstalledModules();
//		//RepositoryBrowser browser = new RepositoryBrowser(repository);
//		try {
//			return Arrays.asList(browser.collect());
//		}
//		catch (RepositoryBrowsingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return null;
//	}
}
