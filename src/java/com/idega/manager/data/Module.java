/*
 * $Id: Module.java,v 1.6 2005/02/23 18:02:17 thomas Exp $
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


/**
 * 
 *  Last modified: $Date: 2005/02/23 18:02:17 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.6 $
 */
public interface Module {
	
	int compare(Module module);
	
	int compare(Dependency dependency);
	
	int compare(DependencyPomBundle dependencyPomBundle);
	
	int compare(Pom pom);
	
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
