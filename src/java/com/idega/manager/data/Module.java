/*
 * $Id: Module.java,v 1.7 2005/03/16 17:49:40 thomas Exp $
 * Created on Nov 30, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.manager.data;

import java.io.File;
import java.io.IOException;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.manager.util.VersionComparator;


/**
 * 
 *  Last modified: $Date: 2005/03/16 17:49:40 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.7 $
 */
public interface Module {
	
	int compare(Module module, VersionComparator versionComparator) throws IOException;
	
	int compare(Dependency dependency, VersionComparator versionComparator) throws IOException;
	
	int compare(DependencyPomBundle dependencyPomBundle, VersionComparator versionComparator) throws IOException;
	
	int compare(Pom pom, VersionComparator versionComparator) throws IOException;
	
	boolean isIncluded();

	boolean isInstalled();
	
	void setIsInstalled(boolean isInstalled);
	
	boolean isSnapshot();
	
	String getGroupId();
	
	String getArtifactId();

	String getCurrentVersion();
	
	// returns the unchanged original version string
	String getCurrentVersionForLabel(IWResourceBundle resourceBundle);
	
	String getNameForLabel(IWResourceBundle resourceBundle);
	
	Pom getPom() throws IOException;
	
	File getBundleArchive() throws IOException;
}
