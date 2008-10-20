/*
 * $Id: IWBundleStarter.java,v 1.5 2008/10/20 11:57:10 laddi Exp $
 * Created on 2.11.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.manager;

import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWBundleStartable;
import com.idega.manager.view.ManagerViewManager;


/**
 * 
 *  Last modified: $Date: 2008/10/20 11:57:10 $ by $Author: laddi $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.5 $
 */
public class IWBundleStarter implements IWBundleStartable {

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
