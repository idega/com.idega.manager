/*
 * $Id: ProxyPom.java,v 1.1 2008/06/11 21:10:01 tryggvil Exp $
 * Created on Nov 22, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.manager.maven1.data;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.idega.idegaweb.IWResourceBundle;
import com.idega.manager.maven1.business.RepositoryBrowser;
import com.idega.manager.maven1.util.ManagerConstants;
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
 *  Last modified: $Date: 2008/06/11 21:10:01 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1 $
 */
public class ProxyPom extends Pom {

	// see examples: 
	// com.idega.block.article-20041109.112340.pom
	// com.idega.core-1.9.1.pom 
	// com.idega.content-SNAPSHOT.pom

	public static final String POM_EXTENSION = ".pom";
	public static final String IWBAR_EXTENSION = ".iwbar";
	
	private static Logger getLogger(){
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
	
	private File bundleArchive = null;
	
	// better performance
	public static String[] getSimpleProxyPom(StringBuffer nameOfFileWithoutExtension) {
		return Pom.splitFileName(nameOfFileWithoutExtension);
	}
	
	public static ProxyPom getInstanceOfGroupBundlesForSimpleProxyPom(String[] simpleProxyPom, RepositoryBrowser repositoryBrowser) {
		return new ProxyPom(RealPom.BUNDLES_GROUP_ID, simpleProxyPom, repositoryBrowser);
	}
	
	public static ProxyPom getInstanceOfGroupBundlesWithoutFileExtension(String nameOfFileWithoutExtension, RepositoryBrowser repositoryBrowser) {
		return new ProxyPom(RealPom.BUNDLES_GROUP_ID, nameOfFileWithoutExtension, repositoryBrowser);
	}
	
	public static ProxyPom getInstanceOfGroupBundlesWithFileExtension(String nameOfFile, RepositoryBrowser repositoryBrowser) {
		String nameOfFileWithoutExtension = StringHandler.cutExtension(nameOfFile);
		return getInstanceOfGroupBundlesWithoutFileExtension(nameOfFileWithoutExtension, repositoryBrowser);
	}
	
	private ProxyPom(String groupId, String[] primitiveProxyPom, RepositoryBrowser repositoryBrowser) {
		this.groupId = groupId;
		this.repositoryBrowser = repositoryBrowser;
		initialize(primitiveProxyPom);
	}
	
	
	private ProxyPom(String groupId, String nameOfFileWithoutExtension, RepositoryBrowser repositoryBrowser) {
		this.groupId = groupId;
		this.repositoryBrowser = repositoryBrowser;
		this.fileName = nameOfFileWithoutExtension;
		String[] primitiveProxyPom = splitFileName(nameOfFileWithoutExtension);
		initialize(primitiveProxyPom);
	}
		
	private void initialize(String[] primitiveProxyPom) {
		this.artifactId = primitiveProxyPom[0];
		String tempVersion = null; 
		try {
			tempVersion = primitiveProxyPom[1];
			if (this.fileName == null) {
				StringBuffer buffer = new StringBuffer(primitiveProxyPom[0]);
				buffer.append(ManagerConstants.ARTIFACT_ID_VERSION_SEPARATOR);
				buffer.append(primitiveProxyPom[1]);
				this.fileName = buffer.toString();
			}
		}
		// usually does not happen
		catch (ArrayIndexOutOfBoundsException ex) {
			tempVersion = "no version available";
			if (this.fileName == null) {
				this.fileName = primitiveProxyPom[0];
			}
		}
		// is it a snapshot?
		// com.idega.content-SNAPSHOT.pom
		if (RealPom.isSnapshot(tempVersion)) {
			this.snapshot = true;
			this.currentVersion = tempVersion;
		}
		else {
			// is it a snapshot with a timestamp?
			// com.idega.block.article-20041109.112340.pom
			// parse timestamp
			this.timestamp = parseVersion(tempVersion);
			if (this.timestamp != null) {
				this.currentVersion = "";
				this.snapshot = true;
			}
		}
		// is it a version?  
		// com.idega.core-1.9.1.pom 
		if (this.timestamp == null) {
			this.currentVersion = tempVersion;
		}
	}
	
	public String getGroupId()	{
		return this.groupId;
	}
	
	public String getArtifactId() {
		return this.artifactId;
	}
	public String getCurrentVersion() {
		return this.currentVersion;
	}
	public boolean isSnapshot() {
		return this.snapshot;
	}
	public IWTimestamp getTimestamp() {
		return this.timestamp;
	}
	
	public String getFileName() {
		return this.fileName;
	}
	
	private RealPom getRealSubject() {
		if (this.realSubject == null) {
			String fileNameWithPomExtension = StringHandler.concat(getFileName(), POM_EXTENSION);
			try {
				File pomFile = this.repositoryBrowser.getPom(fileNameWithPomExtension);
				this.realSubject = RealPom.getPom(pomFile);
			}
			catch (IOException ex) {
				this.realSubject = null;
				getLogger().log(Level.WARNING, "[PomProxy] Could not download real subject: "+ getFileName() , ex);
			}
		}
		return this.realSubject;
	}
	
	public Pom getPom(DependencyPomBundle dependency) throws IOException {
		StringBuffer buffer = constructFileName(dependency, POM_EXTENSION);
		String pomFileName = this.repositoryBrowser.convertPomNameIfNecessary(buffer.toString());
		return ProxyPom.getInstanceOfGroupBundlesWithFileExtension(pomFileName, this.repositoryBrowser);
	}
	
	public File getBundleArchive(DependencyPomBundle dependency) throws IOException  {
		StringBuffer buffer = constructFileName(dependency, IWBAR_EXTENSION);
		return this.repositoryBrowser.getBundleArchive(buffer.toString());
	}

	private StringBuffer constructFileName(DependencyPomBundle dependency, String useExtension) { 		
		String dependencyArtifactId = dependency.getArtifactId();
		StringBuffer buffer = new StringBuffer(dependencyArtifactId);
		buffer.append(ManagerConstants.ARTIFACT_ID_VERSION_SEPARATOR);
		String version = dependency.getCurrentVersion();
		version = RealPom.isSnapshot(version) ? RealPom.SNAPSHOT : version;
		return buffer.append(version).append(useExtension);
	}
	
	public List getDependencies() throws IOException {
		RealPom pom = getRealSubject();
		if (pom == null) { 
			// in this case we really need the real subject
			throw new IOException("[ProxyPom] Could not get my real subject. " + this.toString());
		}	
		return pom.getDependencies(this);
	}

	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.artifactId).append(" ").append(this.fileName);
		return buffer.toString();
	}
	
	public boolean isInstalled() {
		return this.isInstalled;
	}
	
	public void setIsInstalled(boolean isInstalled) {
		this.isInstalled = isInstalled;
	}
	
	public Pom getPom() {
		return this;
	}
	
	public File getBundleArchive() throws IOException {
		if (this.bundleArchive == null) {
			String fileNameWithBundleArchiveExtension = StringHandler.concat(getFileName(), IWBAR_EXTENSION);
			File bundleArchivFile = this.repositoryBrowser.getBundleArchive(fileNameWithBundleArchiveExtension);
			this.bundleArchive = bundleArchivFile;
		}
		return this.bundleArchive;
	}
	
	public boolean isIncluded() {
		return false;
	}
	
	// a proxy that points to a file
	// like 
	// com.idega.content-SNAPSHOT.pom
	// should be ignored because there is
	// a corresponding identical file with a
	// a name that contains the timestamp.
	public boolean shouldBeIgnored() {
		return (this.snapshot && this.timestamp == null);
	}
	
	public String getNameForLabel(IWResourceBundle resourceBundle) {
		RealPom pom = getRealSubject();
		if (pom == null) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("'");
			buffer.append(getArtifactId()).append("'");
			return buffer.toString();
		}
		return pom.getNameForLabel(resourceBundle);
	}
	
	public String getCurrentVersionForLabel(IWResourceBundle resourceBundle) {
		Locale locale = resourceBundle.getLocale();
		StringBuffer buffer = new StringBuffer();
		if (isSnapshot()) {
			DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM , DateFormat.MEDIUM , locale);
			buffer.append(resourceBundle.getLocalizedString("man_manager_build", "Build"));
			buffer.append(" ");
			buffer.append(dateFormat.format(this.timestamp.getDate()));
		}
		else {
			buffer.append(resourceBundle.getLocalizedString("man_manager_version", "Version"));
			buffer.append(" ");
			buffer.append(getCurrentVersion());
		}
		return buffer.toString();
	}
}
