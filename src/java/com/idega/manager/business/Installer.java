/*
 * $Id: Installer.java,v 1.4 2004/12/08 12:47:55 thomas Exp $
 * Created on Dec 3, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.manager.business;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import com.idega.io.ZipInstaller;
import com.idega.manager.data.Module;


/**
 * 
 *  Last modified: $Date: 2004/12/08 12:47:55 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.4 $
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
	
	
	public void getBundleArchives() throws IOException {
		Iterator iterator = toBeInstalled.iterator();
		while (iterator.hasNext()) {
			Module module = (Module) iterator.next();
			getBundleArchive(module);
		}
	}
	
	public void getBundleArchive(Module module) throws IOException  {
		File bundleArchive = module.getBundleArchive();
		String folderName = module.getArtifactId();
		File folder = new File(bundleArchive.getParentFile(), folderName);
		folder.mkdir();
		ZipInstaller zipInstaller = new ZipInstaller();
		zipInstaller.extract(bundleArchive, folder);
		bundleArchive.exists();
	}
	
	
	public Collection getToBeInstalled() {
		return toBeInstalled;
	}
	
	public void setToBeInstalled(Collection toBeInstalled) {
		this.toBeInstalled = toBeInstalled;
	}
}
