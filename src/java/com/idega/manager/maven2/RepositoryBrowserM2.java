package com.idega.manager.maven2;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.Snapshot;
import org.apache.maven.artifact.repository.metadata.Versioning;
import org.apache.maven.artifact.repository.metadata.io.xpp3.MetadataXpp3Reader;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import com.idega.manager.maven1.business.RepositoryBrowser;
import com.idega.manager.maven1.data.RepositoryLogin;

public class RepositoryBrowserM2 extends RepositoryBrowser{

	public static final String MAVEN_METADATA_FILE_NAME = "maven-metadata.xml";
	
	public static RepositoryBrowserM2 getInstanceForIdegaRepository(RepositoryLogin repositoryLogin)	{
		return new RepositoryBrowserM2(repositoryLogin);
	}
	
	public RepositoryBrowserM2(RepositoryLogin repositoryLogin) {
		super(repositoryLogin);
	}
	
	/*public List<MavenModule> getModulesInGroup(String groupId){
		List<MavenModule> modules = new ArrayList<MavenModule>();
		
		String groupUrl=getURLForGroupFolder(groupId);
		List<StringBuffer> artifactIds = getArtifactIdsUnder(groupUrl);
		for(StringBuffer bufferArtifactId: artifactIds){
			String artifactId = bufferArtifactId.toString();
			//String mavenMetadataUrl = getURLForMavenMetadataFile(groupId, artifactId);
			String artifactUrl = getURLForArtifactFolder(groupId, artifactId);
			MavenModule module = new MavenModule(artifactUrl);
			//module.setGroupId(groupId);
			//module.setArtifactId(artifactId);
			modules.add(module);
		}
		
		//for(MavenModule module: modules){
		//	System.out.println("Artifact:"+module.getArtifactId());
		//}
		
		return modules;
	}*/

	public Metadata getModuleWithGroupAndArtifactId(String groupId,String artifactId){
		String artifactUrl = getURLForArtifactMetadata(groupId, artifactId);
		Metadata metadata=null;
		try {
			metadata = getMetadataFromUrl(artifactUrl);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return metadata;
		
	}
	
	public List<Metadata> getModulesInGroup(String groupId){
		List<Metadata> modules = new ArrayList<Metadata>();
		
		String groupUrl=getURLForGroupFolder(groupId);
		List<StringBuffer> artifactIds = getArtifactIdsUnder(groupUrl);
		for(StringBuffer bufferArtifactId: artifactIds){
			String artifactId = bufferArtifactId.toString();
			//String mavenMetadataUrl = getURLForMavenMetadataFile(groupId, artifactId);
			String artifactUrl = getURLForArtifactMetadata(groupId, artifactId);
			
			try {
				Metadata metadata = getMetadataFromUrl(artifactUrl);

				//module.setGroupId(groupId);
				//module.setArtifactId(artifactId);
				modules.add(metadata);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//for(MavenModule module: modules){
		//	System.out.println("Artifact:"+module.getArtifactId());
		//}
		
		return modules;
	}

	public String getArtifactUrlForMostRecent(String groupId, String artifactId){
		return getArtifactUrlForMostRecent(groupId,artifactId,true);
	}
	
	public String getArtifactUrlForMostRecent(String groupId, String artifactId,boolean snapshot){
		Metadata metadata = getModuleWithGroupAndArtifactId(groupId,artifactId);
		return getArtifactUrlForMostRecent(metadata,snapshot);
	}
	
	public String getArtifactUrlForMostRecent(Metadata metadata){
		return getArtifactUrlForMostRecent(metadata,true);
	}
	
	public String getArtifactUrlForMostRecent(Metadata metadata,boolean snapshot){
		Model pom =  getModuleForMostRecent(metadata,snapshot);
		String artifactType = pom.getPackaging();
		if(artifactType.equals("iwwar")){
			artifactType="war";
		}
		
		String artifactUrl = getURLForArtifactFile(metadata,snapshot,artifactType);
		return artifactUrl;
	}
	
	public Model getModuleForMostRecent(Metadata metadata){
		return getModuleForMostRecent(metadata,true);
	}
	
	
	
	public Model getModuleForMostRecent(Metadata metadata,boolean snapshot){
		//String groupId = metadata.getGroupId();
		//String artifactId = metadata.getArtifactId();
		//String groupUrl = getURLForGroupFolder(metadata.getGroupId());
		//List<StringBuffer> artifactIds = getArtifactIdsUnder(groupUrl);
		//for(StringBuffer bufferArtifactId: artifactIds){
		//	String artifactId = bufferArtifactId.toString();
			//String mavenMetadataUrl = getURLForMavenMetadataFile(groupId, artifactId);
			//String versionId = metadata.getVersion();
			
			String artifactPomUrl = getURLForArtifactPom(metadata,snapshot);
			
			URL uRepositoryUrl;
			try {
				uRepositoryUrl = new URL(artifactPomUrl);
				URLConnection conn = uRepositoryUrl.openConnection();
				Object content = conn.getContent();
				InputStream input = (InputStream)content;
				Reader reader = new InputStreamReader(input);
				
				MavenXpp3Reader mavenReader = new MavenXpp3Reader();
				Model module = mavenReader.read(reader);

				//module.setGroupId(groupId);
				//module.setArtifactId(artifactId);
				return module;
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		//}
			return null;
	}

	private List<StringBuffer> getArtifactIdsUnder(String groupUrl) {
		// TODO Auto-generated method stub
		try {
			List<StringBuffer> folders = getFolderList(groupUrl);
			return folders;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public String getURLForArtifactFolder(String groupId, String artifactId) {
		String groupUrl = getURLForGroupFolder(groupId);
		String newUrl = groupUrl+artifactId+"/";
		return newUrl;
	}

	public String getURLForArtifactMetadata(String groupId, String artifactId) {
		String folderUrl = getURLForArtifactFolder(groupId,artifactId);
		return folderUrl+"maven-metadata.xml";
	}

	public String getURLForArtifactPom(Metadata metadata, boolean snapshot) {
		return getURLForArtifactFile(metadata,snapshot,"pom");
	}

	public String getURLForArtifactFile(Metadata metadata, boolean snapshot, String type) {
		
		String groupId = metadata.getGroupId();
		String artifactId = metadata.getArtifactId();
		
		String folderPath = getURLForArtifactFolder(groupId,artifactId);
		String fileUrl = folderPath;
		Versioning versioning = metadata.getVersioning();
		String mostRecentVersion = ModuleVersion.getMostRecentVersionAsString(metadata, versioning.getVersions(), snapshot);
		
		if(!snapshot){
			//String releaseVersion = versioning.getRelease();
			String releaseVersion = mostRecentVersion;
			fileUrl+=releaseVersion+"/"+artifactId+"-"+releaseVersion+"."+type;
		}
		else{
			//String version = metadata.getVersion();
			String version = mostRecentVersion;
			String snapshotFolderUrl = folderPath+version;
			String snapshotMetadataFileUrl = snapshotFolderUrl+"/maven-metadata.xml";
			
			Metadata snapshotMetadata;
			try {
				snapshotMetadata = getMetadataFromUrl(snapshotMetadataFileUrl);
	
				Versioning snapshotVersioning = snapshotMetadata.getVersioning();
				
				Snapshot snap = snapshotVersioning.getSnapshot();
				
				String snapshotVersion = snapshotMetadata.getVersion();
				String tstamp = snap.getTimestamp();
				int buildNumber = snap.getBuildNumber();
				String snapshotVersionMinusSnapshot = snapshotVersion.substring(0,snapshotVersion.indexOf("SNAPSHOT")-1);
				
				fileUrl+=snapshotVersion+"/"+artifactId+"-"+snapshotVersionMinusSnapshot+"-"+tstamp+"-"+buildNumber+"."+type;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return fileUrl;
		
	}

	protected Metadata getMetadataFromUrl(String artifactUrl) throws IOException,
			XmlPullParserException {
				URL uRepositoryUrl = new URL(artifactUrl);
			
				//HttpClient client = HttpClient.New(uRepositoryUrl);
				//client.getInputStream();
				//reader = new   FileReader("file://");
				//Reader reader = new InputStreamReader(client.getInputStream());
				URLConnection conn = uRepositoryUrl.openConnection();
				Object content = conn.getContent();
				InputStream input = (InputStream)content;
				Reader reader = new InputStreamReader(input);
				
				MetadataXpp3Reader mReader = new MetadataXpp3Reader();
				Metadata metadata = mReader.read(reader);
				return metadata;
			}

	public static void main(String[] args){
		
		
		String repositoryUrl = "http://repository.idega.com/maven2/";
		RepositoryLogin login = RepositoryLogin.getInstanceWithoutAuthentication(repositoryUrl);
		//login.getAuthenticationInfo().setUserName("idegaweb");
		//login.getAuthenticationInfo().setPassword("pl4tf0rm");
		RepositoryBrowserM2 browser = new RepositoryBrowserM2(login);
		
		/*MetadataXpp3Reader mReader = new MetadataXpp3Reader();
		Reader reader;
		MavenXpp3Reader mavenReader = new MavenXpp3Reader();
		
		
		try {
			URL uRepositoryUrl = new URL(repositoryUrl);
			HttpClient client = HttpClient.New(uRepositoryUrl);
			//client.getInputStream();
			//reader = new   FileReader("file://");
			reader = new InputStreamReader(client.getInputStream());
			Metadata metadata = mReader.read(reader);
			
			metadata.getVersioning().getSnapshot().getBuildNumber();
			Model model = mavenReader.read(reader);
			
			model.getDependencies();
			
			model.getVersion();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} */
		
		String appsGroupId = "com.idega.webapp.custom";
		//String artifactId = null;
		String artifactId = "felixclub";
		boolean includeSnapshot=true;
		
		if(artifactId==null){
			List<Metadata> poms = browser.getModulesInGroup(appsGroupId);
			for (Iterator iterator = poms.iterator(); iterator.hasNext();) {
				Metadata module = (Metadata) iterator.next();
				String mostRecent = ModuleVersion.getMostRecentVersionAsString(module, includeSnapshot);
				//System.out.println("Module found: "+module.getArtifactId()+" with version:"+module.getMostRecentVersion()+", last updated: "+module.getLastUpdated());
				System.out.println("Module metadata with: "+module.getArtifactId()+" with most recent version: "+mostRecent+", lastupdated: "+module.getVersioning().getLastUpdated());
				//Model model = browser.getModuleForMostRecent(module);
				System.out.println("Module: "+module.getArtifactId()+" has url: "+browser.getArtifactUrlForMostRecent(module,includeSnapshot));
				
			}
		}
		else{
			Metadata module = browser.getModuleWithGroupAndArtifactId(appsGroupId, artifactId);
			String mostRecent = ModuleVersion.getMostRecentVersionAsString(module, includeSnapshot);
			//System.out.println("Module found: "+module.getArtifactId()+" with version:"+module.getMostRecentVersion()+", last updated: "+module.getLastUpdated());
			System.out.println("Module metadata with: "+module.getArtifactId()+" with most recent version: "+mostRecent+", lastupdated: "+module.getVersioning().getLastUpdated());
			
			//Model model = browser.getModuleForMostRecent(module);
			System.out.println("Module: "+module.getArtifactId()+" has url: "+browser.getArtifactUrlForMostRecent(module,includeSnapshot));
		}
		
	}
	
}
