package com.idega.manager.data;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MavenModuleVersion {

	
	MavenModule module;
	String versionId;
	boolean isSnapshot=false;
	String mavenMetadataUrl;
	
	String lastSnapshotTimestamp;
	String lastBuildNumber;
	String lastUpdated;
	private boolean loaded=false;
	
	public MavenModuleVersion(MavenModule module,String versionId, String mavenMetadataUrl) {
		
		setModule(module);
		setVersionId(versionId);
		this.mavenMetadataUrl=mavenMetadataUrl;
		
		//Load lazily:
		//parseMavenMetadata(mavenMetadataUrl);
		
	}
	/*
		<?xml version="1.0" encoding="UTF-8"?>
		<metadata>
		  <groupId>com.idega.block.platform</groupId>
		  <artifactId>com.idega.documentmanager</artifactId>
		  <version>4.0-SNAPSHOT</version>
		  <versioning>
		    <snapshot>
		      <timestamp>20070629.125230</timestamp>
		      <buildNumber>6</buildNumber>
		    </snapshot>
		    <lastUpdated>20070629125803</lastUpdated>
		  </versioning>
		</metadata>
	 */
	
	protected void loadDocument(){
		if(!loaded){
			parseMavenMetadata(mavenMetadataUrl);
			loaded=true;
		}
	}
	
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
					/*if(element.getNodeName().equals("groupId")){
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
					}*/
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
				/*if(element.getNodeName().equals("version")){
					Node content = element.getFirstChild();
					String version = content.getNodeValue();
					getVersions().add(version);
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
				*/
			}
		}
		
	}

	public MavenModule getModule() {
		return module;
	}
	public void setModule(MavenModule module) {
		this.module = module;
	}
	public String getVersionId() {
		return versionId;
	}
	public void setVersionId(String versionId) {
		if(versionId.toLowerCase().endsWith("snapshot")){
			setSnapshot(true);
		}
		this.versionId = versionId;
	}
	public boolean isSnapshot() {
		return isSnapshot;
	}
	public void setSnapshot(boolean isSnapshot) {
		this.isSnapshot = isSnapshot;
	}

	public String getLastSnapshotTimestamp() {
		loadDocument();
		return lastSnapshotTimestamp;
	}

	public void setLastSnapshotTimestamp(String lastSnapshotTimestamp) {
		this.lastSnapshotTimestamp = lastSnapshotTimestamp;
	}

	public String getLastBuildNumber() {
		loadDocument();
		return lastBuildNumber;
	}

	public void setLastBuildNumber(String lastBuildNumber) {
		this.lastBuildNumber = lastBuildNumber;
	}

	public String getLastUpdated() {
		loadDocument();
		return lastUpdated;
	}

	public void setLastUpdated(String lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
}
