/*
 * Created on Apr 13, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.idega.manager.data;

import java.util.ArrayList;
import java.util.List;
import com.idega.manager.business.RepositoryBrowser;


/**
 * <p>
 * TODO thomas Describe Type SimpleProxyPomList
 * </p>
 *  Last modified: $Date: 2006/04/09 11:42:59 $ by $Author: laddi $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.2 $
 */
public class SimpleProxyPomList {
	
	private List simplePomProxies = null;
	
	private RepositoryBrowser repositoryBrowser = null;
	
	private ProxyPom representative = null;
	
	public SimpleProxyPomList(RepositoryBrowser repositoryBrowser) {
		this.repositoryBrowser = repositoryBrowser;
		this.simplePomProxies = new ArrayList();
	}
	
	public void add(String[] simpleProxyPom) {
		if (this.representative == null) {
			ProxyPom proxyPom = ProxyPom.getInstanceOfGroupBundlesForSimpleProxyPom(simpleProxyPom, this.repositoryBrowser);
			if (proxyPom.shouldBeIgnored()) {
				return;
			}
			this.representative = proxyPom;
		}
		this.simplePomProxies.add(simpleProxyPom);
	}
	
	public boolean isEmpty() {
		return this.simplePomProxies.isEmpty();
	}
	

	public ProxyPom getRepresentative() {
		return this.representative;
	}
	
	public List getSimpleProxies() {
		return this.simplePomProxies;
	}
	
	
	public RepositoryBrowser getRepositoryBrowser() {
		return this.repositoryBrowser;
	}
}
