/*
 * $Id: DependencyMatrix.java,v 1.1 2004/11/26 17:19:09 thomas Exp $
 * Created on Nov 26, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.manager.business;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import com.idega.manager.data.Dependency;
import com.idega.manager.data.Pom;
import com.idega.util.datastructures.HashMatrix;


/**
 * 
 *  Last modified: $Date: 2004/11/26 17:19:09 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1 $
 */
public class DependencyMatrix {
	
	private HashMatrix moduleDependencies;
	
	public void addEntry(Pom pom) {
		addEntry(pom, null);
	}
	
	public void addEntry(Pom pom, HashMatrix matrix) {
		List dependencies = pom.getDependencies();
		Iterator iterator = dependencies.iterator();
		while (iterator.hasNext()) {
			Dependency dependency = (Dependency) iterator.next(); 
			// is it a bundle?
			if (dependency.isBundle()) {
				// get pom
				try {
					Pom dependencyPom = pom.getPom(dependency);
					addEntry(dependencyPom, matrix);
					System.out.println(dependencyPom);
				}
				catch (IOException ex) {
					System.out.println("Weser");
				}
				
			}
			
		}
	}
}
