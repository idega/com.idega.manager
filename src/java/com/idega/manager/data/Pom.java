/*
 * $Id: Pom.java,v 1.2 2004/11/26 17:19:09 thomas Exp $
 * Created on Nov 26, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.manager.data;

import java.io.IOException;
import java.util.List;


/**
 * 
 *  Last modified: $Date: 2004/11/26 17:19:09 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.2 $
 */
public interface Pom {
	
	String getArtifactId();

	String getCurrentVersion();
	
	List getDependencies();
	
	Pom getPom(Dependency dependency) throws IOException;
}
