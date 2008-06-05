package com.idega.manager.data;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MavenModule {

	String groupId;
	String artifactId;
	String mostRecentVersion;
	List<MavenModuleVersion> versions;
	Date lastUpdated;
	private String artifactFolderUrl;
	
	static final String MAVEN_METADATA_DOC="maven-metadata.xml";
	
	public MavenModule(String artifactFolderUrl) {
		
		String mavenMetadataUrl = artifactFolderUrl+MAVEN_METADATA_DOC;
		this.artifactFolderUrl=artifactFolderUrl;
		parseMavenMetadata(mavenMetadataUrl);
		
	}
	/*
	<?xml version="1.0" encoding="UTF-8"?>
	<metadata>
	  <groupId>com.idega.webapp.custom</groupId>
	  <artifactId>felixclub</artifactId>
	  <version>4.0-SNAPSHOT</version>
	  <versioning>
	    <versions>
	      <version>4.0-SNAPSHOT</version>
	      <version>4.0.0</version>
	      <version>4.0.2</version>
	    </versions>
	    <lastUpdated>20080130004037</lastUpdated>
	  </versioning>
	</metadata>
	 */
	protected void parseMavenMetadata(String mavenMetadataUrl) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = dbf.newDocumentBuilder();
			Document document = docBuilder.parse(mavenMetadataUrl);
			Node root = document.getFirstChild();
			NodeList nodes = root.getChildNodes();
			Node childNode = root.getFirstChild(); 
			while (childNode != null) {
				childNode = childNode.getNextSibling();
				if(childNode instanceof Element){
					Element element = (Element)childNode;
					if(element.getNodeName().equals("groupId")){
						Node content = element.getFirstChild();
						String groupId = content.getNodeValue();
						setGroupId(groupId);
					}
					if(element.getNodeName().equals("artifactId")){
						Node content = element.getFirstChild();
						String artifactId = content.getNodeValue();
						setArtifactId(artifactId);
					}
					if(element.getNodeName().equals("version")){
						Node content = element.getFirstChild();
						String version = content.getNodeValue();
						setMostRecentVersion(version);
					}
					if(element.getNodeName().equals("versioning")){
						Node versionsNode = element.getFirstChild();
						if(versionsNode instanceof Element){
							Element versionsElement = (Element)versionsNode;
							parseVersions(versionsElement);
						}
					}
				}
			} 
			
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void parseVersions(Element element) {
		Node childNode = element.getFirstChild(); 
		while (childNode != null) {
			childNode = childNode.getNextSibling();
			if(childNode instanceof Element){
				if(element.getNodeName().equals("version")){
					Node content = element.getFirstChild();
					String version = content.getNodeValue();
					String metadataUrl = this.artifactFolderUrl+version+"/"+MAVEN_METADATA_DOC;
					MavenModuleVersion mVersion = new MavenModuleVersion(this,version,metadataUrl);
					getVersions().add(mVersion);
				}
				if(element.getNodeName().equals("lastUpdated")){
					Node content = element.getFirstChild();
					String lastUpdated = content.getNodeValue();
					if(lastUpdated!=null){
						DateFormat dateFormatyyyyMMddhhmmss = new SimpleDateFormat("yyyyMMddhhmmss");
						Date dateParsed;
						try {
							dateParsed = dateFormatyyyyMMddhhmmss.parse(lastUpdated);
							setLastUpdated(dateParsed);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
		
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getArtifactId() {
		return artifactId;
	}
	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}
	public String getMostRecentVersion() {
		return mostRecentVersion;
	}
	public void setMostRecentVersion(String mostRecentVersion) {
		this.mostRecentVersion = mostRecentVersion;
	}
	public List<MavenModuleVersion> getVersions() {
		if(versions==null){
			versions=new ArrayList<MavenModuleVersion>();
		}
		return versions;
	}
	public void setVersions(List<MavenModuleVersion> versions) {
		this.versions = versions;
	}
	public Date getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
}
