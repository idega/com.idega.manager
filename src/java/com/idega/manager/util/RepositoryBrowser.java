/*
 * $Id: RepositoryBrowser.java,v 1.1 2004/11/19 17:05:42 thomas Exp $
 * Created on Nov 16, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.manager.util;

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


/**
 * 
 *  Last modified: $Date: 2004/11/19 17:05:42 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1 $
 */
public class RepositoryBrowser {
	
	private final static char[] HTML_IWBAR_START_PATTERN = "iwbar\">".toCharArray();
	private final static char[] HTML_POM_START_PATTERN = "pom\">".toCharArray();
	private final static char HTML_LINK_END_PATTERN = '<';
	
	static private Logger getLogger(){
		 return Logger.getLogger(ManagerUtils.class.getName());
	 }
	
	private String repository;
	
	public RepositoryBrowser(String repository) {
		this.repository = repository;
	}

	public List getPoms()	{
		String urlAddress = getURL("bundles","poms");
		return getFileNames(urlAddress, HTML_POM_START_PATTERN);
	}
	
	public void getPom(String pomName) {
		String urlAddress = getURL("bundles", "poms");
		downloadFile(urlAddress, pomName);
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
	
	private List getFileNames(String urlAddress, char[] startPatternChar) {
		InputStreamReader inputStreamReader = null;
		StringBuffer buffer = null;
		List fileNames = new ArrayList();
		try {
			URL url = new URL(urlAddress);
			URLConnection con = url.openConnection();
			InputStream inputStream = con.getInputStream();
			BufferedInputStream  buffInputStream = new BufferedInputStream(inputStream);
			inputStreamReader = new InputStreamReader(buffInputStream, "8859_1");
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
						fileNames.add(buffer.toString());
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
		return fileNames;
	}
	
	private void downloadFile(String urlAddress, String fileName) {
		try {
			Wagon wagon = new LightweightHttpWagon();
		    File destination = new File("/home/thomas/download/test");
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
//		catch (IOException e) {
//		   	getLogger().log(Level.WARNING, "[RepositoryBrowser] Could not open URL: "+ urlAddress , e);
//		   	//return null;
//		}	
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

