/*
 * Created on Feb 15, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.idega.manager.data;

import java.net.PasswordAuthentication;
import org.apache.maven.wagon.authentication.AuthenticationInfo;


/**
 * @author thomas
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RepositoryLogin {
	
	public static RepositoryLogin getInstanceWithoutAuthentication(String repository) {
		RepositoryLogin login = new RepositoryLogin();
		login.initialize(repository);
		return login;
	}
	
	public static RepositoryLogin getInstanceWithAuthentication(String repository, String userName, String password) {
		RepositoryLogin login = new RepositoryLogin();
		login.initialize(repository, userName,password);
		return login;
	}
	
	private RepositoryLogin() {
		// empty
	}
	
	private String repository = null;
	
	private PasswordAuthentication passwordAuthentication = null;
	
	private AuthenticationInfo authenticationInfo = null;
	
	private void initialize(String repository) {
		this.repository = repository;
	}
	
	private void initialize(String repository, String userName, String password) {
		initialize(repository);
		this.passwordAuthentication = new PasswordAuthentication(userName, password.toCharArray());
		this.authenticationInfo = new AuthenticationInfo();
		this.authenticationInfo.setUserName(userName);
		this.authenticationInfo.setPassword(password);
	}
	
	/**
	 * @return Returns the passwordAuthentication.
	 */
	public PasswordAuthentication getPasswordAuthentication() {
		return this.passwordAuthentication;
	}

	public AuthenticationInfo getAuthenticationInfo()	{
		return this.authenticationInfo;
	}
	
	/**
	 * @return Returns the repository.
	 */
	public String getRepository() {
		return this.repository;
	}

}
