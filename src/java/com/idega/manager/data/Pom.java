/*
 * $Id: Pom.java,v 1.13 2005/04/14 14:01:01 thomas Exp $
 * Created on Nov 26, 2004
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
import com.idega.manager.util.ManagerConstants;
import com.idega.manager.util.VersionComparator;
import com.idega.util.IWTimestamp;
import com.idega.util.StringHandler;


/**
 * 
 *  Last modified: $Date: 2005/04/14 14:01:01 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.13 $
 */
public abstract class Pom extends ModulePomImpl {
	
	public static final String POM_TIMESTAMP_FORMAT = "yyyyMMdd.HHmmss";
	
	private static SimpleDateFormat dateParser = null;
	
	private static SimpleDateFormat getDateParser() {
		if (dateParser == null) {
			dateParser = new SimpleDateFormat(POM_TIMESTAMP_FORMAT);
		}
		return dateParser;
	}

	public abstract List getDependencies() throws IOException;
	
	public abstract Pom getPom(DependencyPomBundle dependency) throws IOException;
	
	public abstract File getBundleArchive(DependencyPomBundle dependency) throws IOException;
	
	public abstract boolean isSnapshot();
	
	public abstract IWTimestamp getTimestamp();
	
	public int compare(Dependency dependency, VersionComparator versionComparator) {
		// not supported, it has never the same group id
		return -1;
	}
	
	public int compare(Module module, VersionComparator versionComparator) throws IOException {
		// change the algebraic sign of the returned result
		return -(module.compare(this, versionComparator));
	}
	
	public int compare(DependencyPomBundle dependency, VersionComparator versionComparator) throws IOException {
		// change the algebraic sign of the returned result
		return -(dependency.compare(this, versionComparator));
	}
	
	public int compare(Pom aPom, VersionComparator versionComparator) {
		if (isSnapshot() && aPom.isSnapshot()) {
			IWTimestamp timestamp1 = getTimestamp();
			IWTimestamp timestamp2 = aPom.getTimestamp();
			return timestamp1.compareTo(timestamp2);
		}
		
		return compareModules(aPom, versionComparator);
	}

	/**
	 * Returns timestamp from filename else null
	 * 
	 */
	protected IWTimestamp getTimestampFromFileName(String fileNameWithExtension) {
		String nameWithoutExtension = StringHandler.cutExtension(fileNameWithExtension);
		String[] partOfFileName = splitFileName(nameWithoutExtension);
		try {
			String version = partOfFileName[1];
			if (RealPom.isSnapshot(version)) {
				//	like:  com.idega.content-SNAPSHOT.pom
				return null;
			}
			// like: com.idega.block.article-20041109.112340.pom   
			return parseVersion(version);
		}
		// usually does not happen
		catch (ArrayIndexOutOfBoundsException ex) {
			return null;
		}
	}
	
	protected static String[] splitFileName(StringBuffer fileNameWithoutExtension) {
		// myfaces-1.0.5 -> myfaces, 1.0.5
		// jaxen-1.0-FCS-full -> jaxen, 1.0-FCS-full
		int index = fileNameWithoutExtension.indexOf(ManagerConstants.ARTIFACT_ID_VERSION_SEPARATOR);
		try {
			String name = fileNameWithoutExtension.substring(0, index);
			index++;
			String version = fileNameWithoutExtension.substring(index, fileNameWithoutExtension.length());
			String[] result = {name, version} ;
			return result;
		}
		// usually does not happen
		catch (StringIndexOutOfBoundsException ex) {
			String[] result = {fileNameWithoutExtension.toString()};
			return result;
		}
	}
	
	protected String[] splitFileName(String fileNameWithoutExtension) {
		return Pom.splitFileName(new StringBuffer(fileNameWithoutExtension));
	}
	
	protected static IWTimestamp parseVersion(String version) {
		SimpleDateFormat parser = Pom.getDateParser();
		try {
			Date date = parser.parse(version);
			IWTimestamp timestamp = new IWTimestamp(date);
			return timestamp;
		}
		catch (ParseException ex) {
			// do nothing
			return null;
		}
	}	
	
}
