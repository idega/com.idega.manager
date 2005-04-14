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
 *  Last modified: $Date: 2005/04/14 14:01:01 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1 $
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
		if (representative == null) {
			ProxyPom proxyPom = ProxyPom.getInstanceOfGroupBundlesForSimpleProxyPom(simpleProxyPom, repositoryBrowser);
			if (proxyPom.shouldBeIgnored()) {
				return;
			}
			representative = proxyPom;
		}
		simplePomProxies.add(simpleProxyPom);
	}
	
	public boolean isEmpty() {
		return simplePomProxies.isEmpty();
	}
	

	public ProxyPom getRepresentative() {
		return representative;
	}
	
	public List getSimpleProxies() {
		return simplePomProxies;
	}
	
	
	public RepositoryBrowser getRepositoryBrowser() {
		return repositoryBrowser;
	}
}
