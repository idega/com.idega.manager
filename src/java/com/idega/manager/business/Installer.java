/*
 * $Id: Installer.java,v 1.11 2005/04/05 16:14:29 thomas Exp $
 * Created on Dec 3, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.manager.business;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.doomdark.uuid.UUID;
import org.doomdark.uuid.UUIDGenerator;
import com.idega.manager.data.Module;
import com.idega.manager.data.Pom;
import com.idega.manager.data.RealPom;
import com.idega.manager.util.IdegawebDirectoryStructure;
import com.idega.manager.util.ManagerConstants;
import com.idega.manager.util.ManagerUtils;
import com.idega.util.BundleFileMerger;
import com.idega.util.FacesConfigMerger;
import com.idega.util.FileUtil;
import com.idega.util.StringHandler;
import com.idega.util.WebXmlMerger;
import com.idega.util.logging.LogFile;


/**
 * 
 *  Last modified: $Date: 2005/04/05 16:14:29 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.11 $
 */
public class Installer {
	
	private static final int WAIT_PERIOD = 10000;
	
	private LogFile logFile = null;
	private File backupFolder = null;
	
	public static Installer getInstance(PomSorter pomSorter){
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
			String artifactId = module.getArtifactId();
			writeToLogger("Extracting bundle ", artifactId, " finished");
		}
	}
	
	// from auxiliary folder
	public void mergeBundles() throws IOException {
		File bundlesFolder = idegawebDirectoryStructure.getBundlesRealPath();
		Collection toBeInstalledModules = pomSorter.getToBeInstalledPoms().values();
		Iterator moduleIterator = toBeInstalledModules.iterator();
		while (moduleIterator.hasNext()) {
			Module module = (Module) moduleIterator.next();
			File moduleArchive = idegawebDirectoryStructure.getExtractedArchive(module);
			String artifactId = module.getArtifactId();
			StringBuffer buffer = new StringBuffer(artifactId);
			buffer.append('.').append(ManagerConstants.BUNDLE_EXTENSION);
			String bundleFolderName = buffer.toString();
			// does an old version of the bundle exist?
			File target = new File(bundlesFolder, bundleFolderName);
			if (target.exists()) {
				FileUtil.backupToFolder(target, getBackupDirectory());
				FileUtil.deleteContentOfFolder(target);
			}
			FileUtil.copyDirectoryRecursivelyKeepTimestamps(moduleArchive, target);
			writeToLogger("Merging bundle ", artifactId, " finished");
		}
	}
	
	
	
	//from auxiliary folder
	public void mergeTagLibraries() throws IOException {
		File tagLibrary = idegawebDirectoryStructure.getTagLibrary();
		FileUtil.backupToFolder(tagLibrary, getBackupDirectory());
		// delete all files that are not necessary
		//TODO: !!!!! does not work, because the tag libraries are not stored in the bundle folders !!!!!!
		// cleanTagLibrary(tagLibrary);
		// add the new missing files to the tag library
		Iterator moduleIterator = pomSorter.getToBeInstalledPoms().values().iterator();
		while (moduleIterator.hasNext()) {
			Module module = (Module) moduleIterator.next();
			File moduleTagLibrary = idegawebDirectoryStructure.getTagLibrary(module);
			FileUtil.copyDirectoryRecursivelyKeepTimestamps(moduleTagLibrary, tagLibrary);
			String artifactId = module.getArtifactId();
			writeToLogger("Merging tag library ", artifactId, " finished");
		}
	}
	
//	// do not use this method
//	// !!!! does not work because the tag libraries are not stored in the bundle folders !!!!!
//	private void cleanTagLibrary(File tagLibrary) {
//		List existingTagLibraries = FileUtil.getFilesInDirectory(tagLibrary); 
//		List filesToKeep = new ArrayList();
//		Collection toBeInstalledArtifactIds = pomSorter.getToBeInstalledPoms().keySet();
//		Map bundlesTagLibraries = pomSorter.getBundlesTagLibraries();
//		Iterator iterator = bundlesTagLibraries.keySet().iterator();
//		while (iterator.hasNext()) {
//			String artifactId = (String) iterator.next();
//			// do not keep the tag libraries from bundles that will be installed
//			if (! toBeInstalledArtifactIds.contains(artifactId)) {
//				List tlds = (List) bundlesTagLibraries.get(artifactId);
//				if (tlds != null) {
//					filesToKeep.addAll(tlds);
//				}
//			}
//		}
//		// delete files
//		Iterator deleteIterator = existingTagLibraries.iterator();
//		while (deleteIterator.hasNext()) {
//			File file = (File) deleteIterator.next();
//			if (! filesToKeep.contains(file)) {
//				file.delete();
//			}
//		}
//	}
	
	
	//from auxiliary folder
	public void mergeWebConfiguration() throws IOException {
		File webXml = idegawebDirectoryStructure.getDeploymentDescriptor();
		BundleFileMerger merger = new WebXmlMerger();
		mergeConfiguration(merger, webXml, true);
		writeToLogger("Merging web configuration finished", null, null);
	}
	
	
	
	public void mergeFacesConfiguration() throws IOException {
		File facesConfig = idegawebDirectoryStructure.getFacesConfig();
		BundleFileMerger merger = new FacesConfigMerger();
		mergeConfiguration(merger, facesConfig, false);
		writeToLogger("Merging faces configuration finished", null, null);
	}
	
	
	// from auxiliary folder
	private void mergeConfiguration(final BundleFileMerger merger, final File fileInWebInf, boolean useExternalThread ) throws IOException {
		// do not remove existing modules!
		merger.setIfRemoveOlderModules(false);		
		FileUtil.backupToFolder(fileInWebInf, getBackupDirectory());
		// set the target 
		if (! useExternalThread) {
			// the simple way: write directly to the file
			mergeConfiguration(merger, fileInWebInf, fileInWebInf);
		}
		else 	{
			// the complicated way: write to a temp file then copy the file to the original one but wait 20 seconds
			// in this way, the application has enough time to finish the response
			String mergedFileName = fileInWebInf.getName();
			mergedFileName = StringHandler.concat(mergedFileName, "_merged");
			final File mergedFile = new File(getBackupDirectory(), mergedFileName);
			// seems to be a bug in merger (or is it a feature ?), source and output has to be the same
			FileUtil.copyFile(fileInWebInf, mergedFile);
			mergeConfiguration(merger, mergedFile, mergedFile);
			Thread thread = new Thread() {
				public void run() {
			          try { 
			          	System.out.println("[Installer] Start sleeping....");
			          	sleep(WAIT_PERIOD);
			          	System.out.println("[Installer] Stop sleeping...application will restart pretty soon");
			          	try {
			          		FileUtil.copyFile(mergedFile, fileInWebInf);
			          	}
			          	catch (FileNotFoundException ex) {
			          		// do nothing
			          		System.err.println("[[Installer] Could not copy new web.xml" + ex.getMessage());
			          	}
			          	catch (IOException ex) {
			          		// do nothing
			          		System.err.println("[[Installer] Could not copy new web.xml" + ex.getMessage());
			          	}
			          }
			          catch ( InterruptedException e ) {
			          	// do nothing
			          }
			    }
			};
			thread.start();	
		}
	}
			
	private void mergeConfiguration(BundleFileMerger merger, File sourceFile, File outputFile) throws IOException {
		merger.setOutputFile(outputFile);
		Iterator moduleIterator = pomSorter.getToBeInstalledPoms().values().iterator();
		while (moduleIterator.hasNext()) {
			Module module = (Module) moduleIterator.next();
			File tempSourceFile = idegawebDirectoryStructure.getCorrespondingFileFromWebInf(module, sourceFile);
			// not every module has a config file
			if (tempSourceFile.exists()) {
				String artifactId = module.getArtifactId();
				String version = module.getCurrentVersion();
				merger.addMergeInSourceFile(tempSourceFile, artifactId, version);
			}
		}
		merger.process();

	}

	// from auxiliary folder
	public void mergeLibrary() throws IOException {
		File library = idegawebDirectoryStructure.getLibrary();
		FileUtil.backupToFolder(library, getBackupDirectory());
		// delete all files that are not necessary
		cleanLibrary(library);
		// add the new missing jars to the library
		Iterator moduleIterator = pomSorter.getToBeInstalledPoms().values().iterator();
		while (moduleIterator.hasNext()) {
			Module module = (Module) moduleIterator.next();
			File moduleLibrary = idegawebDirectoryStructure.getLibrary(module);
			FileUtil.copyDirectoryRecursivelyKeepTimestamps(moduleLibrary, library);
			String artifactId = module.getArtifactId();
			writeToLogger("Merging library ", artifactId, " finished");
		}
	}
	
	private void cleanLibrary(File library) throws IOException {
		// build a list of necessary file names...
		Map necessaryPomsMap = pomSorter.getNecessaryPoms();
		Collection necessaryPoms = necessaryPomsMap.values();
		Map necessaryFileNames = new HashMap(necessaryPoms.size());
		List notInstalledYet = new ArrayList();
		List containedFileNames = new ArrayList();
		Iterator iterator = necessaryPoms.iterator();
		while (iterator.hasNext()) {
			Module module = (Module) iterator.next();
			// check if the name of the jar file is explicit defined
			String fileName = module.getJarFileName();
			if (fileName == null) {
				fileName = getJarFileName(module);
			}
			if (module.isInstalled()) {
				necessaryFileNames.put(fileName, module.getArtifactId());
			}
			else {
				// only for debugging
				notInstalledYet.add(fileName);
			}
		}
		List files = FileUtil.getFilesInDirectory(library);
		Iterator filesIterator = files.iterator();
		List toBeDeletedFiles = new ArrayList();
		while (filesIterator.hasNext()) {
			File file = (File) filesIterator.next();
			String fileName = file.getName();
			// if not necessary delete it
			if (! necessaryFileNames.containsKey(fileName)) {
				// delete jar file 
				toBeDeletedFiles.add(file);
			}
			else {
				// only for debugging
				containedFileNames.add(fileName);
				necessaryFileNames.remove(fileName);
			}
		}
		// necessary files should be empty!
		// but sometimes 
		Iterator toBeDeletedIterator = toBeDeletedFiles.iterator();
		while (toBeDeletedIterator.hasNext()) {
			File file = (File) toBeDeletedIterator.next();
			String fileName = file.getName();
			String artifact = StringHandler.getFirstToken(fileName, "_-") ;
			if (! necessaryFileNames.containsValue(artifact)) {
				file.delete();
			}
		}
			
		// only for debugging
			
		containedFileNames.size();
		notInstalledYet.size();
		toBeDeletedFiles.size();
	}
	
	private String getJarFileName(Module module) throws IOException {
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
		
		// if the version is equal to SNAPSHOT do not add the current version (avoiding file names like com.idega.block.article-SNAPSHOT-SNAPSHOT)
		if (version !=null && version.length() != 0 && ! version.equals(RealPom.SNAPSHOT)) {
			buffer.append(ManagerConstants.ARTIFACT_ID_VERSION_SEPARATOR);
			buffer.append(version);
		}
		if (module.isSnapshot()) {
			buffer.append(ManagerConstants.ARTIFACT_ID_VERSION_SEPARATOR);
			buffer.append(RealPom.SNAPSHOT);
		}
		buffer.append('.');
		buffer.append(ManagerConstants.JAR_EXTENSION);
		return buffer.toString();
	}
	
	public LogFile getLogFile() throws IOException {
		if (logFile == null) {
			File tempBackupFolder = getBackupDirectory();
			File logDestination = new File(tempBackupFolder, "install.log");
			logFile = new LogFile(logDestination);
		}
		return logFile;
	}
	
	
	private File getBackupDirectory() {
		if (backupFolder == null) {
			File folder = idegawebDirectoryStructure.getBackupDirectory();
			backupFolder = new File(folder, getIdentifier());
		}
		return backupFolder;
	}
	
	private void writeToLogger(String prefix, String main, String suffix) throws IOException {
		LogFile tempLogFile = getLogFile();
		StringBuffer logBuffer = new StringBuffer(prefix);
		if (main != null) {
			logBuffer.append(main);
		}
		if (suffix != null) {
			logBuffer.append(suffix);
		}
		tempLogFile.logInfo(logBuffer.toString());
	}
	
	private String getIdentifier() {
		UUIDGenerator generator = UUIDGenerator.getInstance();
		UUID uuid = generator.generateTimeBasedUUID();
		String identifier = uuid.toString();
		return StringHandler.remove(identifier, "-");
	}
}
