/*
 * $Id: Module.java,v 1.4 2004/12/03 17:01:12 thomas Exp $
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


/**
 * 
 *  Last modified: $Date: 2004/12/03 17:01:12 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.4 $
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
	
	Pom getPom() throws IOException;
	
	File getBundleArchive();
}
