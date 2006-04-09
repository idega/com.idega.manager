/*
 * Created on Feb 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.idega.manager.business;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import sun.misc.BASE64Encoder;
import com.idega.util.StringHandler;


/**
 * @author thomas
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class URLReadConnection {
	
	public static final String UNKNOWN_CONNECTION_ERROR = "unknown_connection_error";
	public static final String NOT_FOUND_ERROR = "not_found_error";
	public static final String FORBIDDEN_ERROR = "forbidden_error";
	public static final String UNAUTHORIZED_ERROR = "unauthorized_error";
	public static final String NO_CONTENT_ERROR = "no_content_error";
	
	public static String authenticationValid(URL url, PasswordAuthentication passwordAuthentication) throws MalformedURLException, IOException {
		URLReadConnection conn = new URLReadConnection();
		conn.url = url;
		conn.passwordAuthentication = passwordAuthentication;
		return conn.authenticationValid();
	}
	
	public static  InputStreamReader getReaderForURLWithoutAuthentication(URL url) throws MalformedURLException, IOException {
		return getReaderForURLWithAuthentication(url, null);
	}
	
	public static InputStreamReader getReaderForURLWithAuthentication(String url, PasswordAuthentication passwordAuthentication) throws MalformedURLException, IOException {
		return getReaderForURLWithAuthentication(new URL(url), passwordAuthentication);
	}
	
	public static  InputStreamReader getReaderForURLWithAuthentication(URL url, PasswordAuthentication passwordAuthentication) throws IOException  {
		URLReadConnection conn = new URLReadConnection();
		conn.url = url;
		conn.passwordAuthentication = passwordAuthentication;
		return conn.getReader();
	}

	private URL url = null;
	
	private PasswordAuthentication passwordAuthentication = null;
	
	private URLConnection connect() throws IOException  {
		URLConnection con = this.url.openConnection();
		if (this.passwordAuthentication != null) {
	  	    con.setDoInput( true );
	  	    String base64UserPassword = getBase64UserPassword();
	  	    con.setRequestProperty( "Authorization", base64UserPassword);
	  	    con.connect();
		}
		return con;
	}
	
	private InputStreamReader getReader() throws IOException  {
		URLConnection con = connect();
		InputStream inputStream = con.getInputStream();
		BufferedInputStream  buffInputStream = new BufferedInputStream(inputStream);
		return new InputStreamReader(buffInputStream, "8859_1");
	}
	
	private String authenticationValid() throws IOException  {
		// check the most common errors
		URLConnection con = connect();
		String state = con.getHeaderField(null);
		if (state == null) {
			return UNKNOWN_CONNECTION_ERROR; 
		}
		state = state.toUpperCase();
		if (StringHandler.contains(state, "200")) {
			// okay
			return null;
		}
		if (StringHandler.contains(state, "404")) {
			return NOT_FOUND_ERROR;
		}
		if (StringHandler.contains(state, "403")) {
			return FORBIDDEN_ERROR;
		}
		if (StringHandler.contains(state, "401")) {
			return UNAUTHORIZED_ERROR;
		}
		if (StringHandler.contains(state, "204")) {
			return NO_CONTENT_ERROR;
		}
		return UNKNOWN_CONNECTION_ERROR;
	}
	
	
	
	
	private String getBase64UserPassword() {
		String userName = this.passwordAuthentication.getUserName();
		char[] password = this.passwordAuthentication.getPassword();
		StringBuffer buffer = new StringBuffer();
		buffer.append(userName).append(":").append(password);
		Charset charset = Charset.forName("ISO-8859-1");
		ByteBuffer byteBuffer = charset.encode(buffer.toString());
		byte[] byteArray = byteBuffer.array();
		BASE64Encoder base64Encoder = new BASE64Encoder();
		String encodedUser = base64Encoder.encode(byteArray);
		// put Basic at the beginning
		buffer = new StringBuffer("Basic ");
		buffer.append(encodedUser);
		return buffer.toString();
	}

}
