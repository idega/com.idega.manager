/*
 * $Id: Installer.java,v 1.2 2004/12/03 17:36:49 thomas Exp $
 * Created on Dec 3, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.manager.business;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import com.idega.manager.data.Module;


/**
 * 
 *  Last modified: $Date: 2004/12/03 17:36:49 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.2 $
 */
public class Installer {
	
	public static Installer getInstance(Collection toBeInstalledPom) {
		Installer installer = new Installer();
		installer.setToBeInstalled(toBeInstalledPom);
		return installer;
	}
	
	private Collection toBeInstalled = null;
	
	private Installer() {
		// use the class method
	}
	
	
	public void getBundleArchives() {
		Iterator iterator = toBeInstalled.iterator();
		while (iterator.hasNext()) {
			Module module = (Module) iterator.next();
			getBundleArchive(module);
		}
	}
	
	public void getBundleArchive(Module module)  {
		File bundleArchive = module.getBundleArchive();
		bundleArchive.exists();
	}
	
	
	public Collection getToBeInstalled() {
		return toBeInstalled;
	}
	
	public void setToBeInstalled(Collection toBeInstalled) {
		this.toBeInstalled = toBeInstalled;
	}
}
