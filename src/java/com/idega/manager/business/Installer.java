/*
 * $Id: Installer.java,v 1.5 2005/01/07 11:03:35 thomas Exp $
 * Created on Dec 3, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.manager.business;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.idega.manager.data.Module;
import com.idega.manager.data.Pom;
import com.idega.manager.data.RealPom;
import com.idega.manager.util.IdegawebDirectoryStructure;
import com.idega.manager.util.ManagerConstants;
import com.idega.manager.util.ManagerUtils;
import com.idega.util.FacesConfigMerger;
import com.idega.util.FileUtil;


/**
 * 
 *  Last modified: $Date: 2005/01/07 11:03:35 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.5 $
 */
public class Installer {
	
	public static Installer getInstance(PomSorter pomSorter) {
		Installer installer = new Installer();
		installer.initialize(pomSorter);
		return installer;
	}
	
	private PomSorter pomSorter = null;
	private IdegawebDirectoryStructure idegawebDirectoryStructure= null;
	
	private Installer() {
		// use the class method
	}
	
	private void initialize(PomSorter pomSorter) {
		this.pomSorter = pomSorter;
		idegawebDirectoryStructure = ManagerUtils.getInstanceForCurrentContext().getIdegawebDirectoryStructure();
	}
	
	
	public void extractBundleArchives() throws IOException {
		Iterator iterator = pomSorter.getToBeInstalledPoms().values().iterator();
		while (iterator.hasNext()) {
			Module module = (Module) iterator.next();
			idegawebDirectoryStructure.getExtractedArchive(module);
		}
	}
	
	
	
	//from auxiliary folder
	public void mergeTagLibraries() throws IOException {
		File tagLibrary = idegawebDirectoryStructure.getTagLibrary();
		FileUtil.backup(tagLibrary);
		// delete all files that are not necessary
		//TODO: !!!!! does not work, because the tag libraries are not stored in the bundle folders !!!!!!
		// cleanTagLibrary(tagLibrary);
		// add the new missing files to the tag library
		Iterator moduleIterator = pomSorter.getToBeInstalledPoms().values().iterator();
		while (moduleIterator.hasNext()) {
			Module module = (Module) moduleIterator.next();
			File moduleTagLibrary = idegawebDirectoryStructure.getTagLibrary(module);
			FileUtil.copyDirectoryRecursively(moduleTagLibrary, tagLibrary);
		}
	}
	
	// !!!! does not work because the tag libraries are not stored in the bundle folders !!!!!
	private void cleanTagLibrary(File tagLibrary) {
		List existingTagLibraries = FileUtil.getFilesInDirectory(tagLibrary); 
		List filesToKeep = new ArrayList();
		Collection toBeInstalledArtifactIds = pomSorter.getToBeInstalledPoms().keySet();
		Map bundlesTagLibraries = pomSorter.getBundlesTagLibraries();
		Iterator iterator = bundlesTagLibraries.keySet().iterator();
		while (iterator.hasNext()) {
			String artifactId = (String) iterator.next();
			// do not keep the tag libraries from bundles that will be installed
			if (! toBeInstalledArtifactIds.contains(artifactId)) {
				List tlds = (List) bundlesTagLibraries.get(artifactId);
				if (tlds != null) {
					filesToKeep.addAll(tlds);
				}
			}
		}
		// delete files
		Iterator deleteIterator = existingTagLibraries.iterator();
		while (deleteIterator.hasNext()) {
			File file = (File) deleteIterator.next();
			if (! filesToKeep.contains(file)) {
				file.delete();
			}
		}
	}
	
	// from auxiliary folder
	public void mergeFacesConfiguration() throws IOException {
		FacesConfigMerger facesConfigMerger = new FacesConfigMerger();
		// do not remove existing modules!
		facesConfigMerger.setIfRemoveOlderModules(false);		
		File facesConfig = idegawebDirectoryStructure.getFacesConfig();
		FileUtil.backup(facesConfig);
		// set the target 
		facesConfigMerger.setOutputFile(facesConfig);
		Iterator moduleIterator = pomSorter.getToBeInstalledPoms().values().iterator();
		while (moduleIterator.hasNext()) {
			Module module = (Module) moduleIterator.next();
			File sourceFile = idegawebDirectoryStructure.getFacesConfig(module);
			// not every module has a faces-config.xml file
			if (sourceFile.exists()) {
				String artifactId = module.getArtifactId();
				String version = module.getCurrentVersion();
				facesConfigMerger.addMergeInSourceFile(sourceFile, artifactId, version);
			}
		}
		facesConfigMerger.process();
	}
	
	// from auxiliary folder
	public void mergeLibrary() throws IOException {
		File library = idegawebDirectoryStructure.getLibrary();
		FileUtil.backup(library);
		// delete all files that are not necessary
		cleanLibrary(library);
		// add the new missing jars to the library
		Iterator moduleIterator = pomSorter.getToBeInstalledPoms().values().iterator();
		while (moduleIterator.hasNext()) {
			Module module = (Module) moduleIterator.next();
			File moduleLibrary = idegawebDirectoryStructure.getLibrary(module);
			FileUtil.copyDirectoryRecursively(moduleLibrary, library);
		}
	}
	
	private void cleanLibrary(File library) throws IOException {
		// build a list of necessary file names...
		Map necessaryPomsMap = pomSorter.getNecessaryPoms();
		Collection necessaryPoms = necessaryPomsMap.values();
		List necessaryFileNames = new ArrayList(necessaryPoms.size());
		Iterator iterator = necessaryPoms.iterator();
		while (iterator.hasNext()) {
			Module module = (Module) iterator.next();
			String artifactId = module.getArtifactId();
			// this is a little bit tricky and complicated....
			//
			// If pom is not null, the module is a Dependency that refers to a bundle module
			// the version of the dependency could be "SNAPSHOT" but the real used one could have the version "1.0-SNAPSHOT".
			
			// In other words: 
			// If the dependency is a snapshot the method module.getCurrentVersion returns "SNAPSHOT" (and this is actually the value
			// that is used in the project file of the dependant) but the 
			// the method pom.getCurrentVersion() might either return null or "" or "1.0". 
			// Therefore to get the right file name ask the real one!
			//
			// If the version is not "SNAPSHOT" 
			// the method module.getCurrentVersion() and pom.getCurrentVersion() should return the same result.
			
			Pom pom = module.getPom();
			String version = (pom == null) ?  module.getCurrentVersion() : pom.getCurrentVersion();
			StringBuffer buffer = new StringBuffer(artifactId);
			// sometimes the version is not set
			
			if (version !=null && version.length() != 0) {
				buffer.append(ManagerConstants.ARTIFACT_ID_VERSION_SEPARATOR);
				buffer.append(version);
			}
			if (module.isSnapshot()) {
				buffer.append(ManagerConstants.ARTIFACT_ID_VERSION_SEPARATOR);
				buffer.append(RealPom.SNAPSHOT);
			}
			buffer.append('.');
			buffer.append(ManagerConstants.JAR_EXTENSION);
			if (module.isInstalled()) {
				necessaryFileNames.add(buffer.toString());
			}
		}
		List files = FileUtil.getFilesInDirectory(library);
		Iterator filesIterator = files.iterator();
		List deletedFiles = new ArrayList();
		while (filesIterator.hasNext()) {
			File file = (File) filesIterator.next();
			String fileName = file.getName();
			//fileName = RealPom.convertFileName(fileName);
			// if not necessary delete it
			if (! necessaryFileNames.contains(fileName)) {
				// delete jar file 
				file.delete();
				deletedFiles.add(fileName);
			}
		}
		deletedFiles.size();
	}
}
