/*
 * $Id: PomSorter.java,v 1.2 2004/12/01 19:24:21 thomas Exp $
 * Created on Nov 22, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.manager.business;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import com.idega.manager.data.Pom;
import com.idega.manager.data.ProxyPom;
import com.idega.manager.data.RealPom;


/**
 * 
 *  Last modified: $Date: 2004/12/01 19:24:21 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.2 $
 */
public class PomSorter {
	
	// key: artifactId String value: Pom 
	SortedMap sortedInstalledPom = null;
	// key: artifactId String value (TreeSet of PomProxy) 
	Map sortedRepositoryPom = null;
	// key: fileName String value: PomProxy
	Map fileNameRepositoryPom = null;
	
	public void initializeInstalledPomsAndAvailableUpdates() {
		RepositoryBrowser repositoryBrowser = RepositoryBrowser.getInstanceForIdegaRepository();
		sortedInstalledPom = new TreeMap();
		sortedRepositoryPom = new HashMap();
		LocalBundlesBrowser localBrowser = new LocalBundlesBrowser();
		List installedPoms = localBrowser.getPomOfInstalledModules();
		Iterator installedPomsIterator = installedPoms.iterator();
		while (installedPomsIterator.hasNext()) {
			RealPom pom = (RealPom) installedPomsIterator.next();
			String artifactId = pom.getArtifactId();
			sortedInstalledPom.put(artifactId, pom);
		}
		List allPoms = repositoryBrowser.getPoms();
		Iterator allPomsIterator = allPoms.iterator();
		while (allPomsIterator.hasNext()) {
			ProxyPom proxy = (ProxyPom) allPomsIterator.next();
			String artifactId = proxy.getArtifactId();
			if (sortedInstalledPom.containsKey(artifactId)) {
				RealPom pom = (RealPom) sortedInstalledPom.get(artifactId);
				if (proxy.compare(pom) > 0) {
					putPom(artifactId, proxy);
				}
//				if (proxy.isSnapshot()) {
//					IWTimestamp installedTimestamp = pom.getTimestamp();
//					IWTimestamp repositoryTimestamp = proxy.getTimestamp();
//					if (repositoryTimestamp == null || installedTimestamp.compareTo(repositoryTimestamp) < 0) {
//						putPom(artifactId, proxy);
//						int k = proxy.compare(pom);
//						k++;
//					}
//				}
//				else {
//					String installedVersion = pom.getCurrentVersion();
//					String repositoryVersion = proxy.getCurrentVersion();
//					if (StringHandler.compareVersions(installedVersion, repositoryVersion) < 0) {
//						putPom(artifactId, proxy);
//						int k = proxy.compare(pom);
//						k++;
//					}
//				}
			}
		}
	}
	
	private void putPom(String key, ProxyPom value) {
		// first store in fileNameMap
		if (fileNameRepositoryPom == null) {
			fileNameRepositoryPom = new HashMap();
		}
		String fileName = value.getFileName();
		fileNameRepositoryPom.put(fileName, value);
		// second store in sorted map
		SortedSet pomSet = (SortedSet) sortedRepositoryPom.get(key);
		if (pomSet == null) {
			Comparator comparator = new Comparator() {
				
				public int compare(Object proxy1, Object proxy2) {
					Pom pom1 = (Pom) proxy1;
					Pom pom2 = (Pom) proxy2;
					return pom1.compare(pom2);
				}
			};
			 pomSet = new TreeSet(comparator);
			 sortedRepositoryPom.put(key, pomSet);
		}
		pomSet.add(value);
	}
		
	public Map getRepositoryPoms() {
		return fileNameRepositoryPom;
	}
		
	public SortedMap getSortedInstalledPoms() {
		return sortedInstalledPom;
	}
	public Map getSortedRepositoryPoms() {
		return sortedRepositoryPom;
	}
}
