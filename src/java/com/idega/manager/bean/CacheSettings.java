/*
 * $Id: CacheSettings.java,v 1.2 2006/04/09 11:42:59 laddi Exp $
 * Created on 29.7.2005 in project com.idega.manager
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.manager.bean;

import com.idega.idegaweb.IWMainApplication;
import com.idega.servlet.filter.CacheFilter;


/**
 * <p>
 * Managed bean to use to manipulate server properties
 * </p>
 *  Last modified: $Date: 2006/04/09 11:42:59 $ by $Author: laddi $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2 $
 */
public class CacheSettings {

	private IWMainApplication iwma;
	private boolean cacheFilterEnabled;
	
	/**
	 * 
	 */
	public CacheSettings() {
		super();
		setIwma(IWMainApplication.getDefaultIWMainApplication());
		loadData();
	}

	/**
	 * <p>
	 * TODO tryggvil describe method setIwma
	 * </p>
	 * @param defaultIWMainApplication
	 */
	private void setIwma(IWMainApplication defaultIWMainApplication) {
		this.iwma=defaultIWMainApplication;
	}

	/**
	 * <p>
	 * Load the data from persistence
	 * </p>
	 */
	private void loadData() {
		IWMainApplication iwma = getIwma();
		String propCacheFilterEnabled = iwma.getSettings().getProperty(CacheFilter.PROPERTY_CACHE_FILTER_ENABLED);
		boolean cacheFilterEnabled = Boolean.valueOf(propCacheFilterEnabled).booleanValue();
		setCacheFilterEnabled(cacheFilterEnabled);
	}

	
	public void store(){
		IWMainApplication iwma = getIwma();
		String propCacheFilterEnabled = Boolean.toString(isCacheFilterEnabled());
		iwma.getSettings().setProperty(CacheFilter.PROPERTY_CACHE_FILTER_ENABLED,propCacheFilterEnabled);
		CacheFilter.reload();
	}

	/**
	 * <p>
	 * TODO tryggvil describe method getIwma
	 * </p>
	 * @return
	 */
	private IWMainApplication getIwma() {
		return this.iwma;
	}

	
	/**
	 * @return Returns the cacheFilterEnabled.
	 */
	public boolean isCacheFilterEnabled() {
		return this.cacheFilterEnabled;
	}

	
	/**
	 * @param cacheFilterEnabled The cacheFilterEnabled to set.
	 */
	public void setCacheFilterEnabled(boolean cacheFilterEnabled) {
		this.cacheFilterEnabled = cacheFilterEnabled;
	}
	
}
