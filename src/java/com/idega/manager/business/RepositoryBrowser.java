/*
 * $Id: RepositoryBrowser.java,v 1.7 2005/02/23 18:02:17 thomas Exp $
 * Created on Nov 16, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.manager.business;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.maven.wagon.ConnectionException;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.authentication.AuthenticationInfo;
import org.apache.maven.wagon.authorization.AuthorizationException;
import org.apache.maven.wagon.providers.http.LightweightHttpWagon;
import org.apache.maven.wagon.repository.Repository;
import org.doomdark.uuid.UUID;
import org.doomdark.uuid.UUIDGenerator;
import com.idega.manager.data.ProxyPom;
import com.idega.manager.data.RealPom;
import com.idega.manager.data.RepositoryLogin;
import com.idega.manager.util.ManagerUtils;
import com.idega.util.FileUtil;
import com.idega.util.StringHandler;


/**
 * 
 *  Last modified: $Date: 2005/02/23 18:02:17 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.7 $
 */
public class RepositoryBrowser {
	
	private final static String SNAPSHOT_VERSION_SUFFIX = "snapshot-version";
	
	private final static char[] HTML_IWBAR_START_PATTERN = "iwbar\">".toCharArray();
	private final static char[] HTML_POM_START_PATTERN = "pom\">".toCharArray();
	private final static char HTML_LINK_END_PATTERN = '<';
	
	public static RepositoryBrowser getInstanceForIdegaRepository(RepositoryLogin repositoryLogin)	{
		return new RepositoryBrowser(repositoryLogin);
	}
	
	static private Logger getLogger(){
		 return Logger.getLogger(ManagerUtils.class.getName());
	 }
	
	private String identifier;
	
	private File workingDirectory;
	
	private RepositoryLogin repositoryLogin;
	
	private String cachedBundlesPomsURL = null;
	private String cachedBundlesIwbarsURL = null;
	// map of file names and files
	private Map cachedDownloadedFiles = null;
	
	public RepositoryBrowser(RepositoryLogin repositoryLogin) {
		this.repositoryLogin = repositoryLogin;
	}
	
	/** Returns names of poms that have a corresponding iwbar file.
	 * 	Sometimes only the pom file exist but not the corresponding iwbar file and vice versa. 
	 * 
	 * @return
	 * @throws IOException
	 */
	public List getPomsSynchronizingBundleArchivesFolderAndPomsFolder() throws IOException {
		List iwbars = getPomsScanningBundleArchivesFolder();
		List pomFiles = getPomsScanningPomsFolder();
		List poms = new ArrayList(iwbars.size());
		Iterator iterator = iwbars.iterator();
		while (iterator.hasNext()) {
			String fileNameWithoutExtension = (String) iterator.next();
			if (pomFiles.contains(fileNameWithoutExtension)) {
				// pom and corresponding jar file exist
				ProxyPom pomProxy = ProxyPom.getInstanceOfGroupBundlesWithoutFileExtension(fileNameWithoutExtension, this);
				if (! pomProxy.shouldBeIgnored()) {
					poms.add(pomProxy);
				}
			}
		}
		return poms;
	}
	
	private List getPomsScanningBundleArchivesFolder() throws IOException {
		String urlAddress = getURLForBundlesArchives();
		return getPomProxies(urlAddress, HTML_IWBAR_START_PATTERN);
	}

	private List getPomsScanningPomsFolder()	throws IOException {
		String urlAddress = getURLForBundlesPoms();
		return getPomProxies(urlAddress, HTML_POM_START_PATTERN);
	}
	
	
	/* if pomName is a snapshot like "com.idega.content-SNAPSHOT.pom"
	 * read the corresponding version from 
	 * "com.idega.content-snapshot-version"
	 * and download the corresponding file like
	 * "com.idega.content-20041117.164329.pom"
	 */
	public String convertPomNameIfNecessary(String pomName) throws IOException {
		String urlAddress = getURLForBundlesPoms();
		return convertPomNameIfNecessary(urlAddress, pomName);
	}
	
	
	/* if pomName is a snapshot like "com.idega.content-SNAPSHOT.pom"
	 * read the corresponding version from 
	 * "com.idega.content-snapshot-version"
	 * and download the corresponding file like
	 * "com.idega.content-20041117.164329.pom"
	 */
	private String convertPomNameIfNecessary(String urlAddress, String pomName) throws IOException {
		return convertNameIfNecessary(urlAddress, pomName, ProxyPom.POM_EXTENSION); 
	}
	
	private String convertBundleArchiveNameIfNecessary(String urlAddress, String archiveName) throws IOException {
		return convertNameIfNecessary(urlAddress, archiveName, ProxyPom.IWBAR_EXTENSION);
	}
	
	
	private String convertNameIfNecessary(String urlAddress, String pomName, String useExtension) throws IOException {
		int index = pomName.indexOf(RealPom.SNAPSHOT);
		// if pomName is a snapshot like "com.idega.content-SNAPSHOT.pom"
		// read the corresponding version from 
		// "com.idega.content-snapshot-version"
		// and download the corresponding file like
		// "com.idega.content-20041117.164329.pom"
		if (index != -1) {
			// e.g. pomName = com.idega.content-SNAPSHOT.pom
			String fileName = pomName.substring(0,index);
			// e.g. fileName = com.idega.content-
			StringBuffer buffer = new StringBuffer(urlAddress);
			buffer.append(fileName);
			buffer.append(SNAPSHOT_VERSION_SUFFIX);
			// e.g. buffer = urlAddress + com.idega.content-snapshot-version
			String version = getContent(buffer.toString());
			// e.g. version = 20041117.164329
			// create new file name
			buffer = new StringBuffer(fileName);
			buffer.append(version);
			buffer.append(useExtension);
			// e.g. buffer = com.idega.content-20041117.164329.pom
			pomName = buffer.toString();
		}
		return pomName;
	}
	
	public File getPom(String pomName) throws IOException {
		String urlAddress = getURLForBundlesPoms();
		pomName = convertPomNameIfNecessary(urlAddress, pomName);
		return downloadFile(urlAddress, pomName);			
	}
	
	public File getBundleArchive(String bundleArchiveName) throws IOException {
		String urlAddress = getURLForBundlesArchives();
		bundleArchiveName = convertBundleArchiveNameIfNecessary(urlAddress, bundleArchiveName);
		return downloadFile(urlAddress, bundleArchiveName);
		
	}
	
	private String getURL(String group, String type)  {
		String repository = repositoryLogin.getRepository();
		StringBuffer buffer = new StringBuffer(repository);
		if (! repository.endsWith("/")) {
			buffer.append('/');
		}
		buffer.append(group).append("/");
		buffer.append(type).append("/");
		return buffer.toString();
	}
	
	private String getURLForBundlesArchives() {
		if (cachedBundlesIwbarsURL == null) {
			cachedBundlesIwbarsURL = getURL("bundles", "iwbars");
		}
		return cachedBundlesIwbarsURL;
	}
	
	private String getURLForBundlesPoms() {
		if (cachedBundlesPomsURL == null ) {
			cachedBundlesPomsURL = getURL("bundles", "poms");
		}
		return cachedBundlesPomsURL;
	}
	
	/** Use this only for very small content otherwise 
	 * it will throw an OutOfMemoryError.
	 *  !! Be careful !!
	 * 
	 * @param urlAddress
	 * @return
	 * @throws IOException
	 */ 
	private String getContent(String urlAddress) throws IOException {
		InputStreamReader inputStreamReader = null;
		StringBuffer buffer = null;
		try {
			buffer = new StringBuffer();
			PasswordAuthentication passwordAuthentication = repositoryLogin.getPasswordAuthentication();
			inputStreamReader = URLReadConnection.getReaderForURLWithAuthentication(urlAddress, passwordAuthentication);
			int charInt;
			while ((charInt = inputStreamReader.read()) != -1) {
				char c = (char) charInt;
				buffer.append(c);
			}
		}
		catch (MalformedURLException e) {
			getLogger().log(Level.WARNING, "[RepositoryBrowser] URL is malformed: "+ urlAddress , e);
			throw new IOException("[RepositoryBrowser] Could not connect to repository, most likely the URL is wrong");
		}
		catch (IOException e) {
	       	getLogger().log(Level.WARNING, "[RepositoryBrowser] Could not open URL: "+ urlAddress , e);
	       	throw new IOException("[RepositoryBorwser] Could not connect to repository, most likely the repository server is down");
		}
		finally  {
			try {
				if (inputStreamReader != null) {
					inputStreamReader.close();
				}
		    }
		    catch (IOException e) {
		    	// do not hide existing exception
		    }
		}
		return buffer.toString();
	}
	
	
	private List getPomProxies(String urlAddress, char[] startPatternChar) throws IOException {
		InputStreamReader inputStreamReader = null;
		StringBuffer nameBuffer = null;
		List poms = new ArrayList();
		try {
			PasswordAuthentication passwordAuthentication = repositoryLogin.getPasswordAuthentication();
			inputStreamReader = URLReadConnection.getReaderForURLWithAuthentication(urlAddress, passwordAuthentication);
			int charInt;
			int startPatternLength = startPatternChar.length;
			int patternCounter = 0;
			char patternChar =startPatternChar[0];
			boolean read = false;
			int readIndex = -1;
			int dotIndex = 0;
			while ((charInt = inputStreamReader.read()) != -1) {
				char c = (char) charInt;
				// are we reading a name of a file at the moment?
				if (read) {
					readIndex++;
					// yes, we do.....
					// is the end of the name reached?
					if (c == HTML_LINK_END_PATTERN) {
						// yes it is, store the name and reset the buffer and read variable
						String fileName = nameBuffer.substring(0, dotIndex);
						poms.add(fileName);
						nameBuffer = null;
						read = false;
						dotIndex = 0;
						readIndex = -1;
					}
					else {
						// do not append the extension of the name to the nameBuffer:
						// store the very last index of a dot
						if (c == '.') {
							dotIndex = readIndex;
						}
						// append character of name to dotBuffer
						nameBuffer.append(c);
					}
				}
				// check if the start pattern fits
				else if (c == patternChar) {
					if (++patternCounter == startPatternLength) {
						// startpattern discovered
						nameBuffer = new StringBuffer();
						read = true;
						patternCounter = 0;
					}
					// search for the next character
					patternChar = startPatternChar[patternCounter];
				}
				else if (patternCounter != 0) {
					// start again from the beginning
					patternChar = startPatternChar[0];
					patternCounter = 0;
				}
			}
		}
		catch (MalformedURLException e) {
			getLogger().log(Level.WARNING, "[RepositoryBrowser] URL is malformed: "+ urlAddress , e);
			throw new IOException("[RepositoryBrowser] Could not connect to repository, most likely the URL is wrong");
		}
		catch (IOException e) {
	       	getLogger().log(Level.WARNING, "[RepositoryBrowser] Could not open URL: "+ urlAddress , e);
	       	throw new IOException("[RepositoryBorwser] Could not connect to repository, most likely the repository server is down");
		}
		finally  {
			try {
				if (inputStreamReader != null) {
					inputStreamReader.close();
				}
		    }
		    catch (IOException e) {
		    	// do not hide existing exception
		    }
		}
		return poms;
	}
	
	private File downloadFile(String urlAddress, String fileName) throws IOException {
		// caching of already downloaded files 
		// (the calling objects are also caching the file but sometimes two different objects are asking for the
		// same file)
		if (cachedDownloadedFiles == null) {
			cachedDownloadedFiles = new HashMap();
		}
		if (cachedDownloadedFiles.containsKey(fileName)) {
			return (File) cachedDownloadedFiles.get(fileName);
		}
		File tempWorkingDirectory = getWorkingDirectory();
		// clean the working directory
		// set time to 60 minutes
		FileUtil.deleteAllFilesAndFolderInFolderOlderThan(tempWorkingDirectory, 3600000);
		// set folder for this instance of repositoryBrowser
		// folder gets the name of the identifier
		File myFolder = new File(tempWorkingDirectory, getIdentifer());
		if (! myFolder.exists()) {
			myFolder.mkdir();
		}
	    File destination = new File(myFolder, fileName);
		try {
			Wagon wagon = new LightweightHttpWagon();
		    Repository localRepository = new Repository();
		    AuthenticationInfo authenticationInfo = repositoryLogin.getAuthenticationInfo();
		    localRepository.setAuthenticationInfo(authenticationInfo);
		    localRepository.setUrl(urlAddress);
		    if ( !destination.exists() ) {
			        wagon.connect(localRepository);
			        wagon.get(fileName, destination );
		    }
		}
		catch (TransferFailedException ex) {
			getLogger().log(Level.WARNING, "[RepositoryBrowser] Transfer failed: "+ urlAddress + fileName , ex);
			throw new IOException("[RepositoryBrowser] Could not download file. " + fileName);
		}
		catch (ConnectionException ex) {
			getLogger().log(Level.WARNING, "[RepositoryBrowser] Connection problems: "+ urlAddress + fileName , ex);
			throw new IOException("[RepositoryBrowser] Could not download file. " + fileName);
		}
		catch (AuthenticationException ex) {
			getLogger().log(Level.WARNING, "[RepositoryBrowser] Authentication problems: "+ urlAddress + fileName , ex);
			throw new IOException("[RepositoryBrowser] Could not download file. " + fileName);
		}
		catch (ResourceDoesNotExistException ex) {
			getLogger().log(Level.WARNING, "[RepositoryBrowser] File does not exist: "+ urlAddress + fileName , ex);
			throw new IOException("[RepositoryBrowser] Could not download file. " + fileName);
		}
		catch (AuthorizationException ex) {
			getLogger().log(Level.WARNING, "[RepositoryBrowser] Authorization problems: "+ urlAddress + fileName , ex);
			throw new IOException("[RepositoryBrowser] Could not download file. " + fileName);
		}
		cachedDownloadedFiles.put(fileName, destination);
		return destination;
	}
	

	
	private File getWorkingDirectory() {
		if (workingDirectory == null) {
			workingDirectory = ManagerUtils.getInstanceForCurrentContext().getWorkingDirectory();
		}
		return workingDirectory;
	}
	
	
	
	private String getIdentifer() {
		if (identifier == null) {
			UUIDGenerator generator = UUIDGenerator.getInstance();
			UUID uuid = generator.generateRandomBasedUUID();
			identifier = uuid.toString();
			identifier = StringHandler.remove(identifier, "-");
		}
		return identifier;
	}
	
	
}
	
	




	
	
//	public XMLDocument getDocument() {
//
//        BufferedReader in = null;
//        try {
//        	in = new BufferedReader(new InputStreamReader(new URL(getURL()).openStream()));
//        	
//        	String inputLine;
//
//        	while ((inputLine = in.readLine()) != null)
//        	    System.out.println(inputLine);
//
//        	in.close();
        	
//        	org.w3c.dom.Document documentW3c = getTidy().parseDOM(in, null);
//        	Document document  = new DOMReader().read(documentW3c);
//        	Element element = document.getRootElement();
//        	List list = element.elements();
//        	list.size();
//        	NodeList list = documentW3c.getChildNodes();
//        	int length = list.getLength();
//        	List values = new ArrayList(length);
//        	for (int i = 0; i < length; i++) {
//        		Node node = list.item(i);
//        		String value = node.getNodeValue();
//        		values.add(value);
//        	}
//        	values.size();
        	// changing to XMLDocument
 //       	return null; //XMLDocument.valueOf(documentW3c);
//	    }
//        catch (MalformedURLException ex) {
//        	getLogger().log(Level.WARNING, "[RepositoryBrowser] URL is malformed: "+ getURL(),ex);
//        }
//		catch (IOException e) {
//        	getLogger().log(Level.WARNING, "[RepositoryBrowser] Could not open URL: "+ getURL(), e);
//		}
//	    finally
//	    {
//	    	try {
//	    		in.close();
//	    	}
//	    	catch (IOException ex) {
//	    		// do not hide existing exception
//	    	}
//	    }
//	    return null;
//	}

