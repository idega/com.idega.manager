/*
 * Created on Mar 23, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.idega.manager.data;

import com.idega.manager.util.VersionComparator;


/**
 * <p>
 * TODO thomas Describe Type ModulePomImpl
 * </p>
 *  Last modified: $Date: 2005/03/23 15:31:07 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1 $
 */
public abstract class ModulePomImpl implements Module {
	
	public String getJarFileName() {
		return null;
	}
	
	protected int compareModules(Module module, VersionComparator versionComparator) {
		if (! ( isSnapshot() || module.isSnapshot()) ) {
			// Case 2: both are not snapshots !!!!
			String version1 =  getCurrentVersion();
			String version2 = module.getCurrentVersion();
			int result = versionComparator.compare(version1, version2);
			// if both are equal the installed one wins
			if (result == 0) {
				if ( isInstalled() && module.isInstalled()) {
					return 0;
				}
				if ( isInstalled()) {
					return 1;
				}
				if (module.isInstalled()) {
					return -1;
				}
			}
			return result;
		}
		
		// Case 3: only one of them are snapshots 
		//  -------------  that is you can not compare them ---------------------
		// or the result was zero
		
		// module that is not installed wins
		if (  isInstalled() && ! module.isInstalled()) {
			// that is: dependencyPomBundle is not installed
			return -1;
		}
		if (!  isInstalled() && module.isInstalled()) {
			// that is: this is not installed
			return 1;
		}
		return 0;
	}
}
