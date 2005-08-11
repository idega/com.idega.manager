/*
 * $Id: SystemSettings.java,v 1.1 2005/08/11 18:56:43 tryggvil Exp $
 * Created on 29.7.2005 in project com.idega.manager
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.manager.bean;

import com.idega.core.builder.data.ICDomain;
import com.idega.core.builder.data.ICDomainHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.idegaweb.IWMainApplication;


/**
 * <p>
 * Managed bean to use to manipulate server properties
 * </p>
 *  Last modified: $Date: 2005/08/11 18:56:43 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class SystemSettings {

	private String mainDomainName;
	private String mainDomainUrl;
	private IWMainApplication iwma;
	
	/**
	 * 
	 */
	public SystemSettings() {
		super();
		setIwma(IWMainApplication.getDefaultIWMainApplication());
		loadData();
	}

	
	/**
	 * <p>
	 * Load the data from persistence
	 * </p>
	 */
	private void loadData() {
		ICDomain domain = getIwma().getIWApplicationContext().getDomain();
		setMainDomainName(domain.getDomainName());
		setMainDomainUrl(domain.getURL());
	}
	
	protected ICDomainHome getDomainHome(){
		try {
			return (ICDomainHome) IDOLookup.getHome(ICDomain.class);
		}
		catch (IDOLookupException e) {
			throw new RuntimeException(e);
		}
	}


	/**
	 * @return Returns the iwma.
	 */
	public IWMainApplication getIwma() {
		return iwma;
	}

	
	/**
	 * @param iwma The iwma to set.
	 */
	public void setIwma(IWMainApplication iwma) {
		this.iwma = iwma;
	}

	
	/**
	 * @return Returns the mainDomainName.
	 */
	public String getMainDomainName() {
		return mainDomainName;
	}

	
	/**
	 * @param mainDomainName The mainDomainName to set.
	 */
	public void setMainDomainName(String mainDomainName) {
		this.mainDomainName = mainDomainName;
	}

	
	/**
	 * @return Returns the mainDomainUrl.
	 */
	public String getMainDomainUrl() {
		return mainDomainUrl;
	}

	
	/**
	 * @param mainDomainUrl The mainDomainUrl to set.
	 */
	public void setMainDomainUrl(String mainDomainUrl) {
		this.mainDomainUrl = mainDomainUrl;
	}
	
	public void store(){
		ICDomain domain = getIwma().getIWApplicationContext().getDomain();
		domain.setDomainName(getMainDomainName());
		domain.setURL(getMainDomainUrl());
		domain.store();
	}
	
}
