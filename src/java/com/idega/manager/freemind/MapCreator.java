/*
 * Created on Mar 29, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.idega.manager.freemind;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import com.idega.manager.data.ApplicationRealPom;
import com.idega.manager.data.Dependency;
import com.idega.manager.data.Module;
import com.idega.manager.data.Pom;
import com.idega.manager.data.RealPom;
import com.idega.util.xml.XMLData;
import com.idega.xml.XMLDocument;
import com.idega.xml.XMLElement;


/**
 * <p>
 * TODO thomas Describe Type MapCreator
 * </p>
 *  Last modified: $Date: 2005/04/06 16:08:32 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.2 $
 */
public class MapCreator {
	
	private int counter = 0;
	
	private Map artifactCounter = null;
	private Map artifactVersion = null;
	private Map artifactNode = null;
	private List artifactPom = null;

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.show();
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("../"));
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.showOpenDialog(frame);
		File workspace = chooser.getSelectedFile();
		if (workspace != null) {
			File application = workspace; //new File(workspace, "applications/eplatform");
			//File webApplication = new File(application, "target/eplatform");
			
			MapCreator mapCreator = new MapCreator();
			mapCreator.createMindMap(application, workspace, false, "webApp.mm");
			//mapCreator.createMindMap(application, workspace, true, "workspace.mm");
		}
		frame.dispose();
	}
	
	private void createMindMap(File application, File workspace, boolean eclipseProject, String outputName) {
		// reset counter and map
		counter = 0;
		artifactCounter = new HashMap();
		artifactVersion = new HashMap();
		artifactNode = new HashMap();
		artifactPom = new ArrayList();
		File projectFile = new File(application, RealPom.POM_FILE);
		if (projectFile.exists()) {
			try {
				RealPom pom = ApplicationRealPom.getPomForApplication(projectFile);
				pom.setEclipseProject(eclipseProject);
				String artifactId = pom.getArtifactId();
				XMLData data = XMLData.getInstanceWithoutExistingFileSetNameSetRootName("weser", "map");
				XMLDocument document = data.getDocument();
				XMLElement root = document.getRootElement();
				root.setAttribute("version","0.7.1");
				XMLElement node = new XMLElement("node");
				node.setAttribute("TEXT", artifactId);
				// root is set
				createBranches(node, pom);
				root.addContent(node);
				File output = new File(workspace, outputName);
				output.createNewFile();
				data.writeToFile(output);
			}
			catch (IOException ex) {
				System.err.println("Could not open "+ projectFile.getName());
			}
		}
	}
	
	private void createBranches(XMLElement parentNode, Pom pom) throws IOException {
		List dependencies = pom.getDependencies();
		Comparator comparator = new Comparator() {
			public int compare(Object o1, Object o2) {
				String artifactId1= ((Module) o1).getArtifactId();
				String artifactId2 = ((Module) o2).getArtifactId();
				return artifactId1.compareTo(artifactId2);
			}
		};
		Collections.sort(dependencies, comparator);
		Iterator iterator = dependencies.iterator();
		while (iterator.hasNext()) {
			String currentId = Integer.toString(counter++);
			Dependency dependency = (Dependency) iterator.next();
			String dependencyArtifactId = dependency.getArtifactId();
			String version = dependency.getCurrentVersion();
			StringBuffer buffer = new StringBuffer(dependencyArtifactId).append(" ").append(version);
			if (dependency.isIncludedInBundle()) {
				buffer.append(" INCLUDED");
			}
			if (artifactPom.contains(dependencyArtifactId)) {
				buffer.append(" -->");
			}
			XMLElement dependencyNode = new XMLElement("node");
			dependencyNode.setAttribute("POSITION", "RIGHT");
			dependencyNode.setAttribute("ID", currentId);
			parentNode.addContent(dependencyNode);
			// update map
			if (artifactCounter.containsKey(dependencyArtifactId)) {
				// create a link to the existing node
				String destination = (String) artifactCounter.get(dependencyArtifactId);
				XMLElement arrow = new XMLElement("arrowlink");
				arrow.setAttribute("ENDARROW","Default");
				arrow.setAttribute("DESTINATION", destination);
				arrow.setAttribute("STARTARROW", "None");
				// change color if there is an inconsistency
				String otherVersion = (String) artifactVersion.get(dependencyArtifactId);
				if (! version.equalsIgnoreCase(otherVersion) ) {
					XMLElement originNode = (XMLElement) artifactNode.get(dependencyArtifactId);
					originNode.setAttribute("COLOR", "#0000cc");
					dependencyNode.setAttribute("COLOR","#ff0000");
					arrow.setAttribute("COLOR","#ff0000");
				}
				dependencyNode.addContent(arrow);
			}
			else {
				artifactCounter.put(dependencyArtifactId, currentId);
				artifactVersion.put(dependencyArtifactId, version);
				artifactNode.put(dependencyArtifactId, dependencyNode);
				Pom dependencyPom = dependency.getPom();
				if (dependencyPom != null) {
					artifactPom.add(dependencyArtifactId);
					dependencyNode.setAttribute("FOLDED", "true");
					dependencyNode.setAttribute("COLOR", "#990099");
					createBranches(dependencyNode, dependencyPom);
				}
			}
			dependencyNode.setAttribute("TEXT", buffer.toString());
		}
	}

}
