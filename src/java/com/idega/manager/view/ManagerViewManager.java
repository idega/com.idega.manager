/*
 * Created on Dec 29, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.idega.manager.view;

import java.util.ArrayList;
import java.util.Collection;
import javax.faces.context.FacesContext;
import com.idega.core.accesscontrol.business.StandardRoles;
import com.idega.core.view.ApplicationViewNode;
import com.idega.core.view.DefaultViewNode;
import com.idega.core.view.ViewManager;
import com.idega.core.view.ViewNode;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.repository.data.Instantiator;
import com.idega.repository.data.Singleton;
import com.idega.repository.data.SingletonRepository;

/**
 * @author thomas
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ManagerViewManager implements Singleton {
	
	private static Instantiator instantiator = new Instantiator() {
		public Object getInstance(Object parameter) {
			IWMainApplication iwma = null;
			if (parameter instanceof FacesContext) {
				iwma = IWMainApplication.getIWMainApplication((FacesContext) parameter);
			}
			else {
				iwma = (IWMainApplication) parameter;
			}
			return new ManagerViewManager(iwma);
		}
	};
	
	private static final String MANAGER_ID="manager";
	private static final String BUNDLE_IDENTIFIER="com.idega.manager";
	
	private ViewNode managerRootNode;
	private IWMainApplication iwma;
	
	private ManagerViewManager(IWMainApplication iwma){
		this.iwma=iwma;
	}
	
	public static ManagerViewManager getInstance(IWMainApplication iwma){
		return (ManagerViewManager) SingletonRepository.getRepository().getInstance(ManagerViewManager.class, instantiator, iwma);
	}
	
	public static ManagerViewManager getInstance(FacesContext context){
		return (ManagerViewManager) SingletonRepository.getRepository().getInstance(ManagerViewManager.class, instantiator, context);
	}
	
	public ViewManager getViewManager(){
		return ViewManager.getInstance(iwma);
	}
	
	
	public ViewNode getContentNode(){
		IWBundle iwb = iwma.getBundle(BUNDLE_IDENTIFIER);
		//ViewNode content = root.getChild(CONTENT_ID);
		if(managerRootNode==null){
			managerRootNode = initalizeManagerNode(iwb);
		}
		return managerRootNode;
	}
	
	public ViewNode initalizeManagerNode(IWBundle contentBundle){
		ViewNode root = getViewManager().getWorkspaceRoot();
		DefaultViewNode managerNode = new ApplicationViewNode(MANAGER_ID,root);
		Collection roles = new ArrayList();
		roles.add(StandardRoles.ROLE_KEY_ADMIN);
		managerNode.setAuthorizedRoles(roles);
		managerRootNode = managerNode;
		return managerRootNode;
	}
	
	
	public void initializeStandardNodes(IWBundle bundle){
		ViewNode contentNode = initalizeManagerNode(bundle);
		
		// login manager / step 1   
		DefaultViewNode loginNode = new DefaultViewNode("Install", contentNode);
		loginNode.setJspUri(bundle.getJSPURI("LoginManager.jsp"));
		
		// update and install /step 2
		DefaultViewNode installOrUpdateNode = new DefaultViewNode("InstallUpdate", loginNode);
		installOrUpdateNode.setJspUri(bundle.getJSPURI("InstallOrUpdateManager.jsp"));
		
		// update/step 3
		DefaultViewNode updateNode = new DefaultViewNode("Update", loginNode);
		updateNode.setJspUri(bundle.getJSPURI("UpdateListManager.jsp"));
		
		// install/step 3.1
		DefaultViewNode newModulesNode = new DefaultViewNode("NewModule", loginNode);
		newModulesNode.setJspUri(bundle.getJSPURI("InstallListManager.jsp"));
		
		// install/step 3.2
		DefaultViewNode newModuleVersionNode = new DefaultViewNode("NewModuleVersion", loginNode);
		newModuleVersionNode.setJspUri(bundle.getJSPURI("InstallNewModuleListManager.jsp"));
		
		// update and install /step 4
		DefaultViewNode commitNode = new DefaultViewNode("Commit", loginNode);
		commitNode.setJspUri(bundle.getJSPURI("ModuleManager.jsp"));
	}
}



