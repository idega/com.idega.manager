/*
 * $Id: ManagerUtils.java,v 1.1 2004/11/08 10:52:38 thomas Exp $
 * Created on Nov 5, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.manager.util;

import javax.faces.context.FacesContext;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;


/**
 * 
 *  Last modified: $Date: 2004/11/08 10:52:38 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1 $
 */
public class ManagerUtils {
	
	public static final String BUNDLE_IDENTIFIER = "com.idega.manager";
	
	public static ManagerUtils getInstanceForCurrentContext() {
		return new ManagerUtils();
	}
	
	private IWBundle bundle = null;
	private IWResourceBundle resourceBundle = null;
	
	private ManagerUtils() {
		// use class method
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
	
	private void getBundleAndResourceBundle() {
		FacesContext context = FacesContext.getCurrentInstance();
		bundle = IWContext.getIWContext(context).getIWMainApplication().getBundle(BUNDLE_IDENTIFIER);
		resourceBundle = bundle.getResourceBundle(context.getExternalContext().getRequestLocale());
	}
}
