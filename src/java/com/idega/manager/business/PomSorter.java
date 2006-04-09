/*
 * $Id: PomSorter.java,v 1.15 2006/04/09 11:42:59 laddi Exp $
 * Created on Nov 22, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.manager.business;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import com.idega.manager.data.Module;
import com.idega.manager.data.Pom;
import com.idega.manager.data.ProxyPom;
import com.idega.manager.data.RealPom;
import com.idega.manager.data.RepositoryLogin;
import com.idega.manager.data.SimpleProxyPomList;
import com.idega.manager.util.VersionComparator;


/**
 * 
 *  Last modified: $Date: 2006/04/09 11:42:59 $ by $Author: laddi $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.15 $
 */
public class PomSorter {

	// map of installed bundles
	// key: artifactId (String) value: Pom (RealPom)
	SortedMap sortedInstalledPom = null;
		
	// map of sets of available updates for already installed modules
	// read from the iwbar folder in the repository
	// key: artifactId (String) value: SortedSet of Pom (TreeSet of ProxyPom) 
	Map sortedRepositoryPomAvailableUpdates = null;
	
	// map of lists of available new modules (not yet installed)
	// read from the iwbar folder in the repository
	// for performance reasons "simple PomProxies" are stored, that are just String[2] arrays. 
	// key: artifactId (String) value: List of simple ProxyPom (SimpleProxyList, that contains simple ProxyPoms])
	Map sortedSimpleProxyList = null;
	
	// this list is used by the isValid() method.
	// A valid pom has an existing pom.file in the pom folder in the repository and
	// also an existing iwbar file in the iwbar folder in the repository.
	// for performance reason StringBuffer, not Strings, are stored
	// value: file name of a pom from the pom folder in the repository (StringBuffer)
	List pomFileNames = null;
	
	// this look up list is used by the beans to get the corresponding ProxyPom 
	// by using the file name as identifier
	// key: fileName (String) value: pom (ProxyPom)
	Map fileNameRepositoryPom = null;
	
	// this list stores the necessary poms.
	// PomSorter works just as a container. The necessary poms are
	// not calculated within this class.
	// values: modules (Module)
	Map necessaryPoms = null;
	
	// this list is a subset of the list "necessaryPoms". It contains the modules that
	// have to be downloaded from the repository. The rest of the modules in the list
	// "necessaryPoms" are either already installed or included in other modules.
	SortedMap toBeInstalledPoms = null;
	
	// just a list of error messages
	List errorMessages = null;
	
	// version comparator works with caches, that is 
	// a reused comparator works faster when dealing with the same versions again
	VersionComparator usedVersionComparator = null;
	
//  Not used at the moment
//	//key: artifactId String value: List of Files
//	Map bundlesTagLibraries = null;
	
	public void initializeInstalledPomsAndAvailableUpdates(RepositoryLogin repositoryLogin) throws IOException {
		VersionComparator versionComparator = getUsedVersionComparator();
		findInstalledPoms();
		findAvailableUpdates(repositoryLogin, versionComparator);
	}
	
	public void initializeInstalledPomsAndAvailableNewModules(RepositoryLogin repositoryLogin) throws IOException {
		findInstalledPoms();
		findAvailableNewModules(repositoryLogin);
	}
	
	public void addSimpleProxyPomList(String key, SimpleProxyPomList simpleProxyPomList, Map pomMap) {
		VersionComparator versionComparator = getUsedVersionComparator();
		// iterator over list
		Iterator iterator = simpleProxyPomList.getSimpleProxies().iterator();
		RepositoryBrowser repositoryBrowser = simpleProxyPomList.getRepositoryBrowser(); 
		while (iterator.hasNext()) {
			String[] simpleProxyPom = (String[]) iterator.next();
			ProxyPom proxyPom = ProxyPom.getInstanceOfGroupBundlesForSimpleProxyPom(simpleProxyPom, repositoryBrowser);
			if (isValid(proxyPom)) {
				putPom(key, proxyPom, pomMap, versionComparator);
			}
		}
	}
	
	private void findInstalledPoms() {
		LocalBundlesBrowser localBrowser = new LocalBundlesBrowser();
		// not used at the moment
		//bundlesTagLibraries = localBrowser.getTagLibrariesOfInstalledModules();
		List installedPoms = localBrowser.getPomOfInstalledModules();
		this.sortedInstalledPom = new TreeMap();
		Iterator installedPomsIterator = installedPoms.iterator();
		while (installedPomsIterator.hasNext()) {
			RealPom pom = (RealPom) installedPomsIterator.next();
			String artifactId = pom.getArtifactId();
			this.sortedInstalledPom.put(artifactId, pom);
		}
	}
		
	private void findAvailableUpdates(RepositoryLogin repositoryLogin, VersionComparator versionComparator) throws IOException {
		RepositoryBrowser repositoryBrowser = RepositoryBrowser.getInstanceForIdegaRepository(repositoryLogin);
		List allPoms = getAllPomsFromRepository(repositoryBrowser);
		this.sortedRepositoryPomAvailableUpdates = new HashMap();
		Iterator allPomsIterator = allPoms.iterator();
		while (allPomsIterator.hasNext()) {
			String[] simpleProxyPom = (String[]) allPomsIterator.next();
			// simpleProxyPom[0] contains the artifactId
			if (this.sortedInstalledPom.containsKey(simpleProxyPom[0])) {
				RealPom pom = (RealPom) this.sortedInstalledPom.get(simpleProxyPom[0]);
				// fetch only poms that are newer than the installed ones
				// and fetch additionally versions if the installed one is a snapshot
				// convert to a ProxyPom
				ProxyPom proxy = ProxyPom.getInstanceOfGroupBundlesForSimpleProxyPom(simpleProxyPom, repositoryBrowser);
				if ( (isValid(proxy)) &&
					(proxy.compare(pom, versionComparator) > 0 || (pom.isSnapshot() && ! proxy.isSnapshot()))) {
					putPom(simpleProxyPom[0], proxy, this.sortedRepositoryPomAvailableUpdates, versionComparator);
				}
			}
		}
	}
	
	
	private void findAvailableNewModules(RepositoryLogin repositoryLogin) throws IOException {
		RepositoryBrowser repositoryBrowser = RepositoryBrowser.getInstanceForIdegaRepository(repositoryLogin);
		List allPoms= getAllPomsFromRepository(repositoryBrowser);
		this.sortedSimpleProxyList = new TreeMap();
		Iterator allPomsIterator = allPoms.iterator();
		while (allPomsIterator.hasNext()) {
			String[] simpleProxyPom = (String[]) allPomsIterator.next();
			// simpleProxyPom[0] contains the artifactId
			if (! this.sortedInstalledPom.containsKey(simpleProxyPom[0])) {
				putPrimitiveProxyPom(simpleProxyPom[0], simpleProxyPom, this.sortedSimpleProxyList, repositoryBrowser);
			}
		}
	}
	
	private boolean isValid(ProxyPom proxyPom) {
		if (proxyPom.shouldBeIgnored()) {
			return false;
		}
		String fileName = proxyPom.getFileName();
		Iterator iterator = this.pomFileNames.iterator();
		while (iterator.hasNext()) {
			StringBuffer buffer = (StringBuffer) iterator.next();
			if (fileName.contentEquals(buffer)) {
				return true;
			}
		}
		return false;
	}
	
	private List getAllPomsFromRepository(RepositoryBrowser repositoryBrowser) throws IOException {
		this.pomFileNames = repositoryBrowser.getPomsScanningPomsFolder();
		return repositoryBrowser.getSimplePomProxiesFromBundleArchivesFolder();
	}
	
	private void putPrimitiveProxyPom(String artifactId, String[] simpleProxyPom, Map aMap, RepositoryBrowser repositoryBrowser) {
		SimpleProxyPomList simpleProxyPomList = (SimpleProxyPomList) aMap.get(artifactId);
		if (simpleProxyPomList == null) {
			simpleProxyPomList = new SimpleProxyPomList(repositoryBrowser);
			aMap.put(artifactId, simpleProxyPomList);
		}
		simpleProxyPomList.add(simpleProxyPom);
	}
	
	private void putPom(String key, ProxyPom value, Map pomMap, final VersionComparator versionComparator) {
		// first store in fileNameMap
		if (this.fileNameRepositoryPom == null) {
			this.fileNameRepositoryPom = new HashMap();
		}
		String fileName = value.getFileName();
		this.fileNameRepositoryPom.put(fileName, value);
		// second store in sorted map
		SortedSet pomSet = (SortedSet) pomMap.get(key);
		if (pomSet == null) {
			Comparator comparator = new Comparator() {
				
				public int compare(Object proxy1, Object proxy2) {
					Pom pom1 = (Pom) proxy1;
					Pom pom2 = (Pom) proxy2;
					return pom1.compare(pom2, versionComparator);
				}
			};
			 pomSet = new TreeSet(comparator);
			 pomMap.put(key, pomSet);
		}
		pomSet.add(value);
	}
	
//	Not used at the moment	
//	public Map getBundlesTagLibraries() {
//		return bundlesTagLibraries;
//	}
	
	public Map getRepositoryPoms() {
		return this.fileNameRepositoryPom;
	}
		
	public SortedMap getSortedInstalledPoms() {
		return this.sortedInstalledPom;
	}
	
	public Map getSortedRepositoryPomsOfAvailableUpdates() {
		return this.sortedRepositoryPomAvailableUpdates;
	}
	
	
	public Map getSortedSimpleProxyList() {
		return this.sortedSimpleProxyList;
	}
	
	public SortedMap getToBeInstalledPoms() {
		return this.toBeInstalledPoms;
	}
	
	public Map getNecessaryPoms() {
		return this.necessaryPoms;
	}
	
	public void setNecessaryPoms(List necessaryPoms) {
		this.toBeInstalledPoms = new TreeMap();
		this.necessaryPoms = new HashMap();
		Iterator iterator = necessaryPoms.iterator();
		while (iterator.hasNext()) {
			Module module = (Module) iterator.next();
			String key = module.getArtifactId();
			if (! (module.isInstalled() || module.isIncluded())) { 
				this.toBeInstalledPoms.put(key, module);
			}
			this.necessaryPoms.put(key, module);
		}
	}

	public List getErrorMessages() {
		return this.errorMessages;
	}
	public void setErrorMessages(List errorMessages) {
		this.errorMessages = errorMessages;
	}
	
	/**
	 * 
	 * <p>
	 * 	A version comparator works with a cache, that is a 
	*  	reused comparator works faster 
	*  when dealing with the same versions again
	 * </p>
	 * @return
	 */
	public VersionComparator getUsedVersionComparator() {
		if (this.usedVersionComparator == null) {
			this.usedVersionComparator = new VersionComparator();
		}
		return this.usedVersionComparator;
	}
}
