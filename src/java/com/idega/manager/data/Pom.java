/*
 * $Id: Pom.java,v 1.9 2005/02/23 18:02:17 thomas Exp $
 * Created on Nov 26, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.manager.data;

import java.io.File;
import java.io.IOException;
import java.util.List;
import com.idega.util.IWTimestamp;
import com.idega.util.StringHandler;


/**
 * 
 *  Last modified: $Date: 2005/02/23 18:02:17 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.9 $
 */
public abstract class Pom implements Module {
	
	public abstract List getDependencies() throws IOException;
	
	public abstract Pom getPom(DependencyPomBundle dependency) throws IOException;
	
	public abstract File getBundleArchive(DependencyPomBundle dependency) throws IOException;
	
	public abstract boolean isSnapshot();
	
	public abstract IWTimestamp getTimestamp();
	
	public int compare(Dependency dependency) {
		// not supported, it has never the same group id
		return -1;
	}
	
	public int compare(Module module) {
		// change the algebraic sign of the returned result
		return -(module.compare(this));
	}
	
	public int compare(DependencyPomBundle dependency) {
		// change the algebraic sign of the returned result
		return -(dependency.compare(this));
	}
	
	public int compare(Pom aPom) {
		if (isSnapshot() && aPom.isSnapshot()) {
			IWTimestamp timestamp1 = getTimestamp();
			IWTimestamp timestamp2 = aPom.getTimestamp();
			return timestamp1.compareTo(timestamp2);
		}
		if (isSnapshot()) {
			return 1;
		}
		if (aPom.isSnapshot()) {
				return -1;
		}
		String version1 = getCurrentVersion();
		String version2 = aPom.getCurrentVersion();
		int result = StringHandler.compareVersions(version1, version2);
		// if both are equal the installed one wins
		if (result == 0) {
			if (isInstalled() && aPom.isInstalled()) {
				return 0;
			}
			if (isInstalled()) {
				return 1;
			}
			if (aPom.isInstalled()) {
				return -1;
			}
		}
		return result;
	}
	
	
	
}
