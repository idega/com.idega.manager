/*
 * $Id: ManagerUtils.java,v 1.2 2004/11/19 17:05:58 thomas Exp $
 * Created on Nov 5, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.manager.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.manager.data.Pom;
import com.idega.presentation.IWContext;
import com.idega.util.FileUtil;
import com.idega.util.StringHandler;
import com.idega.util.xml.XMLData;


/**
 * 
 *  Last modified: $Date: 2004/11/19 17:05:58 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.2 $
 */
public class ManagerUtils {
	
	public static final String POM_FILE = "project.xml";
	
	public static final String BUNDLE_IDENTIFIER = "com.idega.manager";
	
	public static final String BASE_PROJECT_NAME = "com.idega.core.bundle";
	
	public static ManagerUtils getInstanceForCurrentContext() {
		return new ManagerUtils();
	}
	
	 /**
	  * Gets the default Logger. By default it uses the package and the class name to get the logger.<br>
	  * This behaviour can be overridden in subclasses.
	  * @return the default Logger
	  */
	static private Logger getLogger(){
		 return Logger.getLogger(ManagerUtils.class.getName());
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
	
	private void getBundleAndResourceBundle() {
		bundle = context.getIWMainApplication().getBundle(BUNDLE_IDENTIFIER);
		resourceBundle = bundle.getResourceBundle(context.getExternalContext().getRequestLocale());
	}
	
	public List  getPomOfInstalledModules() {
		String tempBundlesPath = getBundlesRealPath();
		List bundles = FileUtil.getDirectoriesInDirectory(tempBundlesPath);
		if (bundles == null) {
			return null;
		}
		List poms = new ArrayList(bundles.size());
		Iterator bundlesIterator = bundles.iterator();
		while (bundlesIterator.hasNext()) {
			File folder = (File) bundlesIterator.next();
			File projectFile = new File(folder, POM_FILE);
			if (projectFile.exists()) {
				try {
					Pom pom = Pom.getPom(projectFile);
					poms.add(pom);
					List list = pom.getDependencies();
					list.size();
				}
				catch (IOException ex) {
					Logger logger = getLogger();
					logger.log(Level.WARNING, "[ManagerUtils] POM file for module "+folder.getName()+" could not be parsed");
				}
			}
		}
		return poms;
	}


	
	public String getRealPathBaseProject() {
		return StringHandler.concat(getBundlesRealPath(), BASE_PROJECT_NAME);
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
