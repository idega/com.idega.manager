/*
 * Created on Mar 30, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.idega.manager.data;

import java.io.File;
import java.io.IOException;
import com.idega.util.FileUtil;
import com.idega.util.xml.XMLData;


/**
 * <p>
 * TODO thomas Describe Type ApplicationRealPom
 * </p>
 *  Last modified: $Date: 2005/03/31 15:48:00 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1 $
 */
public class ApplicationRealPom extends RealPom {
	

	
	public static ApplicationRealPom getPomForApplication(File projectFile) throws IOException {
		XMLData pomData =RealPom.createXMLData(projectFile);
		return new ApplicationRealPom(pomData, projectFile);
	}
	
	protected ApplicationRealPom(XMLData xmlData, File projectFile) throws IOException {
		super(xmlData, projectFile);
	}
	
	protected File getBundlesFolder(File startFile) {
		if (isEclipseProject()) {
			return FileUtil.getFileRelativeToFile(startFile, "../..");
		}
		return FileUtil.getFileRelativeToFile(startFile, "./idegaweb/bundles");
	}
	
}
