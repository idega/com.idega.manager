/*
 * $Id: IWBundleStarter.java,v 1.1 2005/01/07 11:03:35 thomas Exp $
 * Created on 2.11.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.manager;

import com.idega.core.view.ViewNode;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWBundleStartable;
import com.idega.manager.view.ManagerViewManager;


/**
 * 
 *  Last modified: $Date: 2005/01/07 11:03:35 $ by $Author: thomas $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.1 $
 */
public class IWBundleStarter implements IWBundleStartable {
	private static final String STYLE_SHEET_URL = "/style/webfacestyle.css";
	private static final String BUNDLE_IDENTIFIER="com.idega.manager";

	/**
	 * 
	 */
	public IWBundleStarter() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWBundleStartable#start(com.idega.idegaweb.IWBundle)
	 */
	public void start(IWBundle starterBundle) {
		addManagerViews(starterBundle);
		//Add the stylesheet:
		//GlobalIncludeManager.getInstance().addBundleStyleSheet(BUNDLE_IDENTIFIER,STYLE_SHEET_URL);
	}

	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWBundleStartable#stop(com.idega.idegaweb.IWBundle)
	 */
	public void stop(IWBundle starterBundle) {
		// TODO Auto-generated method stub	
	}
	
	public void addManagerViews(IWBundle bundle){

		ManagerViewManager managerViewManager = ManagerViewManager.getInstance(bundle.getApplication());
		managerViewManager.initializeStandardNodes(bundle);
		
//		DefaultViewNode articleNode = new DefaultViewNode("article",contentNode);
//		articleNode.setJspUri(bundle.getJSPURI("articles.jsp"));
//		DefaultViewNode createNewArticleNode = new DefaultViewNode("create",articleNode);
//		//createNewArticleNode.setJspUri("/idegaweb/bundles/com.idega.webface.bundle/jsp/createarticle.jsp");
//		String jspUri = bundle.getJSPURI("createarticle.jsp");
//		createNewArticleNode.setJspUri(jspUri);
//		
//		DefaultViewNode previewArticlesNode = new DefaultViewNode("preview",articleNode);
//		//previewArticlesNode.setJspUri("/idegaweb/bundles/com.idega.webface.bundle/jsp/previewarticle.jsp");
//		previewArticlesNode.setJspUri(bundle.getJSPURI("previewarticle.jsp"));
//		//DefaultViewNode listArticlesNode = new ApplicationViewNode("listarticles",articleNode);
//		
//		DefaultViewNode listArticlesNode = new DefaultViewNode("list",articleNode);
//		//previewArticlesNode.setJspUri("/idegaweb/bundles/com.idega.webface.bundle/jsp/previewarticle.jsp");
//		listArticlesNode.setJspUri(bundle.getJSPURI("listarticles.jsp"));
//		//DefaultViewNode listArticlesNode = new ApplicationViewNode("listarticles",articleNode);		
//
//		DefaultViewNode searchArticlesNode = new DefaultViewNode("search",articleNode);
//		//previewArticlesNode.setJspUri("/idegaweb/bundles/com.idega.webface.bundle/jsp/previewarticle.jsp");
//		searchArticlesNode.setJspUri(bundle.getJSPURI("searcharticle.jsp"));
//		//DefaultViewNode listArticlesNode = new ApplicationViewNode("listarticles",articleNode);		
//				
		

	}
}
