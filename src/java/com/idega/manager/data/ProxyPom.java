/*
 * $Id: ProxyPom.java,v 1.4 2004/12/08 12:47:55 thomas Exp $
 * Created on Nov 22, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.manager.data;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.idega.manager.business.RepositoryBrowser;
import com.idega.util.IWTimestamp;
import com.idega.util.StringHandler;


/**
 * This class is a proxy for a real pom file.
 * It is initialised with a filename with arbitrary extension, that is it can be
 * initialised either
 * with a file name referring to a pom file like com.idega.block.article-20041109.112340.pom
 * or
 * with a file name referring to a iwbar file like com.idega.block.article-20041109.112340.iwbar
 * or
 * with a file name without an extension like
 * com.idega.block.article-20041109.112340
 * 
 * In any case the reference to the real subject is resolved by pointing to the real pom file.
 * 
 *  Last modified: $Date: 2004/12/08 12:47:55 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.4 $
 */
public class ProxyPom extends Pom {

	// see examples: 
	// com.idega.block.article-20041109.112340.pom
	// com.idega.core-1.9.1.pom 
	// com.idega.content-SNAPSHOT.pom
	private static final String ARTIFACT_ID_VERSION_SEPARATOR = "-";
	public static final String POM_EXTENSION = ".pom";
	public static final String IWBAR_EXTENSION = ".iwbar";
	public static final String POM_TIMESTAMP_FORMAT = "yyyyMMdd.HHmmss";
	
	private static SimpleDateFormat dateParser = null;
	
	private static SimpleDateFormat getDateParser() {
		if (dateParser == null) {
			dateParser = new SimpleDateFormat(POM_TIMESTAMP_FORMAT);
		}
		return dateParser;
	}
	
	static private Logger getLogger(){
		 return Logger.getLogger(ProxyPom.class.getName());
	 }
	
	private RealPom realSubject = null;
	private RepositoryBrowser repositoryBrowser = null;
	
	// Note : file name is stored without extension!
	private String fileName = null;
	
	private String groupId = null;
	private String artifactId = null;
	private String currentVersion = null;
	
	private boolean snapshot = false;
	private IWTimestamp timestamp = null;
	
	boolean isInstalled = false;
	
	
	public static ProxyPom getInstanceOfGroupBundles(String nameOfFile, RepositoryBrowser repositoryBrowser) {
		return new ProxyPom(RealPom.BUNDLES_GROUP_ID, nameOfFile, repositoryBrowser);
	}
	
	
	private ProxyPom(String groupId, String nameOfFile, RepositoryBrowser repositoryBrowser) {
		this.groupId = groupId;
		this.repositoryBrowser = repositoryBrowser;
		initialize(nameOfFile);
	}
		
		
	private void initialize(String nameOfFile) {
		this.fileName = StringHandler.cutExtension(nameOfFile);
		String[] partOfFileName = fileName.split(ARTIFACT_ID_VERSION_SEPARATOR);
		artifactId = partOfFileName[0];
		String tempVersion = null; 
		if (partOfFileName.length < 2) {
			tempVersion = "no version available";
		}
		else {
			tempVersion = partOfFileName[1];
		}
		// is it a snapshot?
		// com.idega.content-SNAPSHOT.pom
		if (RealPom.isSnapshot(tempVersion)) { 
			snapshot = true;
			currentVersion = tempVersion;
		}
		else {
			// is it a snapshot with a timestamp?
			// com.idega.block.article-20041109.112340.pom
			// parse timestamp
			SimpleDateFormat parser = ProxyPom.getDateParser();
			try {
				Date date = parser.parse(tempVersion);
				timestamp = new IWTimestamp(date);
				currentVersion = "";
				snapshot = true;
			}
			catch (ParseException ex) {
				// do nothing
				timestamp = null;
			}
		}
		// is it a version?  
		// com.idega.core-1.9.1.pom 
		if (timestamp == null) {
			currentVersion = tempVersion;
		}
	}
	
	public String getGroupId()	{
		return groupId;
	}
	
	public String getArtifactId() {
		return artifactId;
	}
	public String getCurrentVersion() {
		return currentVersion;
	}
	public boolean isSnapshot() {
		return snapshot;
	}
	public IWTimestamp getTimestamp() {
		return timestamp;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	private RealPom getRealSubject() {
		if (realSubject == null) {
			String fileNameWithPomExtension = StringHandler.concat(getFileName(), POM_EXTENSION);
			try {
				File pomFile = repositoryBrowser.getPom(fileNameWithPomExtension);
				realSubject = RealPom.getPom(pomFile);
			}
			catch (IOException ex) {
				getLogger().log(Level.WARNING, "[PomProxy] Could not download real subject: "+ getFileName() , ex);
				realSubject = null;
			}
		}
		return realSubject;
	}
	
	public Pom getPom(DependencyPomBundle dependency) throws IOException {
		StringBuffer buffer = constructFileName(dependency, POM_EXTENSION);
		String pomFileName = repositoryBrowser.convertPomNameIfNecessary(buffer.toString());
		return ProxyPom.getInstanceOfGroupBundles(pomFileName, repositoryBrowser);
	}
	
	public File getBundleArchive(DependencyPomBundle dependency) throws IOException  {
		StringBuffer buffer = constructFileName(dependency, IWBAR_EXTENSION);
		return repositoryBrowser.getBundleArchive(buffer.toString());
	}

	private StringBuffer constructFileName(DependencyPomBundle dependency, String useExtension) { 		
		String dependencyArtifactId = dependency.getArtifactId();
		StringBuffer buffer = new StringBuffer(dependencyArtifactId);
		buffer.append(ARTIFACT_ID_VERSION_SEPARATOR);
		String version = dependency.getVersion();
		version = RealPom.isSnapshot(version) ? RealPom.SNAPSHOT : version;
		return buffer.append(version).append(useExtension);
	}
	
	public List getDependencies() {
		RealPom pom = getRealSubject();
		return (pom == null) ? null : pom.getDependencies(this);
	}

	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(artifactId).append(" ").append(fileName);
		return buffer.toString();
	}
	
	public boolean isInstalled() {
		return isInstalled;
	}
	
	public void setIsInstalled(boolean isInstalled) {
		this.isInstalled = isInstalled;
	}
	
	public Pom getPom() {
		return this;
	}
	
	public File getBundleArchive() throws IOException {
		String fileNameWithBundleArchiveExtension = StringHandler.concat(getFileName(), IWBAR_EXTENSION);
		File bundleArchivFile = repositoryBrowser.getBundleArchive(fileNameWithBundleArchiveExtension);
		return bundleArchivFile;
	}
	
	public boolean isIncluded() {
		return false;
	}
	

	
	
}
