/*
 * $Id: Pom.java,v 1.11 2005/03/18 14:16:36 thomas Exp $
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
import com.idega.manager.util.VersionComparator;
import com.idega.util.IWTimestamp;


/**
 * 
 *  Last modified: $Date: 2005/03/18 14:16:36 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.11 $
 */
public abstract class Pom implements Module {
	 
	static protected int compareModules(Module module1, Module module2, VersionComparator versionComparator) {
		if (! ( module1.isSnapshot() || module2.isSnapshot()) ) {
			// Case 2: both are not snapshots !!!!
			String version1 =  module1.getCurrentVersion();
			String version2 = module2.getCurrentVersion();
			int result = versionComparator.compare(version1, version2);
			// if both are equal the installed one wins
			if (result == 0) {
				if ( module1.isInstalled() && module2.isInstalled()) {
					return 0;
				}
				if ( module1.isInstalled()) {
					return 1;
				}
				if (module2.isInstalled()) {
					return -1;
				}
			}
			return result;
		}
		
		// Case 3: only one of them are snapshots 
		//  -------------  that is you can not compare them ---------------------
		// or the result was zero
		
		// module that is not installed wins
		if (  module1.isInstalled() && ! module2.isInstalled()) {
			// that is: dependencyPomBundle is not installed
			return -1;
		}
		if (!  module1.isInstalled() && module2.isInstalled()) {
			// that is: this is not installed
			return 1;
		}
		return 0;
	}
	
	public abstract List getDependencies() throws IOException;
	
	public abstract Pom getPom(DependencyPomBundle dependency) throws IOException;
	
	public abstract File getBundleArchive(DependencyPomBundle dependency) throws IOException;
	
	public abstract boolean isSnapshot();
	
	public abstract IWTimestamp getTimestamp();
	
	public int compare(Dependency dependency, VersionComparator versionComparator) {
		// not supported, it has never the same group id
		return -1;
	}
	
	public int compare(Module module, VersionComparator versionComparator) throws IOException {
		// change the algebraic sign of the returned result
		return -(module.compare(this, versionComparator));
	}
	
	public int compare(DependencyPomBundle dependency, VersionComparator versionComparator) throws IOException {
		// change the algebraic sign of the returned result
		return -(dependency.compare(this, versionComparator));
	}
	
	public int compare(Pom aPom, VersionComparator versionComparator) {
		if (isSnapshot() && aPom.isSnapshot()) {
			IWTimestamp timestamp1 = getTimestamp();
			IWTimestamp timestamp2 = aPom.getTimestamp();
			return timestamp1.compareTo(timestamp2);
		}
		
		return Pom.compareModules(this, aPom, versionComparator);
	}
	

	
	
}
