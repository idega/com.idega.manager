/*
 * $Id: RepositoryBrowser.java,v 1.2 2004/12/01 19:24:21 thomas Exp $
 * Created on Nov 16, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.manager.business;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.providers.http.LightweightHttpWagon;
import org.apache.maven.wagon.repository.Repository;
import org.doomdark.uuid.UUID;
import org.doomdark.uuid.UUIDGenerator;
import com.idega.manager.data.ProxyPom;
import com.idega.manager.data.RealPom;
import com.idega.manager.util.ManagerUtils;
import com.idega.util.FileUtil;
import com.idega.util.StringHandler;


/**
 * 
 *  Last modified: $Date: 2004/12/01 19:24:21 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.2 $
 */
public class RepositoryBrowser {
	
	private final static String SNAPSHOT_VERSION_SUFFIX = "snapshot-version";
	
	private final static char[] HTML_IWBAR_START_PATTERN = "iwbar\">".toCharArray();
	private final static char[] HTML_POM_START_PATTERN = "pom\">".toCharArray();
	private final static char HTML_LINK_END_PATTERN = '<';
	
	public static RepositoryBrowser getInstanceForIdegaRepository()	{
		return new RepositoryBrowser("http://repository.idega.com/maven");
	}
	
	static private Logger getLogger(){
		 return Logger.getLogger(ManagerUtils.class.getName());
	 }
	
	private String identifier;
	
	private File workingDirectory;
	
	private String repository;
	
	public RepositoryBrowser(String repository) {
		this.repository = repository;
	}

	public List getPoms()	{
		String urlAddress = getURL("bundles","poms");
		return getFileNames(urlAddress, HTML_POM_START_PATTERN);
	}
	
	
	/* if pomName is a snapshot like "com.idega.content-SNAPSHOT.pom"
	 * read the corresponding version from 
	 * "com.idega.content-snapshot-version"
	 * and download the corresponding file like
	 * "com.idega.content-20041117.164329.pom"
	 */
	public String convertPomNameIfNecessary(String pomName) {
		String urlAddress = getURL("bundles", "poms");
		return convertPomNameIfNecessary(urlAddress, pomName);
	}
	
	/* if pomName is a snapshot like "com.idega.content-SNAPSHOT.pom"
	 * read the corresponding version from 
	 * "com.idega.content-snapshot-version"
	 * and download the corresponding file like
	 * "com.idega.content-20041117.164329.pom"
	 */
	private String convertPomNameIfNecessary(String urlAddress, String pomName) {
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
			buffer.append(ProxyPom.EXTENSION);
			// e.g. buffer = urlAddress + com.idega.content-20041117.164329.pom
			pomName = buffer.toString();
		}
		return pomName;
	}
	
	public File getPom(String pomName) {
		String urlAddress = getURL("bundles", "poms");
		pomName = convertPomNameIfNecessary(urlAddress, pomName);
		return downloadFile(urlAddress, pomName);			
	}
	
	
	private String getURL(String group, String type)  {
		StringBuffer buffer = new StringBuffer(repository);
		if (! repository.endsWith("/")) {
			buffer.append('/');
		}
		buffer.append(group).append("/");
		buffer.append(type).append("/");
		return buffer.toString();
	}
	
	/** Use this only for very small content otherwise 
	 * it will throw an OutOfMemoryError.
	 *  !! Be careful !!
	 * 
	 * @param urlAddress
	 * @return
	 */ 
	private String getContent(String urlAddress) {
		InputStreamReader inputStreamReader = null;
		StringBuffer buffer = null;
		try {
			buffer = new StringBuffer();
			inputStreamReader = getReader(urlAddress);
			int charInt;
			while ((charInt = inputStreamReader.read()) != -1) {
				char c = (char) charInt;
				buffer.append(c);
			}
		}
		catch (MalformedURLException e) {
			getLogger().log(Level.WARNING, "[RepositoryBrowser] URL is malformed: "+ urlAddress , e);
			return null;
		}
		catch (IOException e) {
	       	getLogger().log(Level.WARNING, "[RepositoryBrowser] Could not open URL: "+ urlAddress , e);
	       	return null;
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
	
	
	
	private List getFileNames(String urlAddress, char[] startPatternChar) {
		InputStreamReader inputStreamReader = null;
		StringBuffer buffer = null;
		List poms = new ArrayList();
		try {
			inputStreamReader = getReader(urlAddress);
			int charInt;
			int startPatternLength = startPatternChar.length;
			int patternCounter = 0;
			char patternChar =startPatternChar[0];
			boolean read = false;
			while ((charInt = inputStreamReader.read()) != -1) {
				char c = (char) charInt;
				// are we reading a name of a file at the moment?
				if (read) {
					// yes, we do.....
					// is the end of the name reached?
					if (c == HTML_LINK_END_PATTERN) {
						// yes it is, store the name and reset the buffer and read variable
						String fileName = buffer.toString();
						ProxyPom pomProxy = ProxyPom.getInstanceOfGroupBundles(fileName, this);
						poms.add(pomProxy);
						buffer = null;
						read = false;
					}
					else {
						// append character of name
						buffer.append(c);
					}
				}
				// check if the start pattern fits
				else if (c == patternChar) {
					if (++patternCounter == startPatternLength) {
						// startpattern discovered
						buffer = new StringBuffer();
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
			return null;
		}
		catch (IOException e) {
	       	getLogger().log(Level.WARNING, "[RepositoryBrowser] Could not open URL: "+ urlAddress , e);
	       	return null;
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
	
	private InputStreamReader getReader(String urlAddress) throws MalformedURLException, IOException {
		URL url = new URL(urlAddress);
		URLConnection con = url.openConnection();
		InputStream inputStream = con.getInputStream();
		BufferedInputStream  buffInputStream = new BufferedInputStream(inputStream);
		return new InputStreamReader(buffInputStream, "8859_1");
	}
	
	private File downloadFile(String urlAddress, String fileName) {
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
		    localRepository.setUrl(urlAddress);
		    if ( !destination.exists() ) {
			        wagon.connect(localRepository);
			        wagon.get(fileName, destination );
		    }
		}
		catch (Exception e) {
			getLogger().log(Level.WARNING, "[RepositoryBrowser] URL is malformed: "+ urlAddress , e);
			//return null;
		}
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

