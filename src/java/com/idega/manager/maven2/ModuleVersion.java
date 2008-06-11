package com.idega.manager.maven2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.artifact.repository.metadata.Metadata;

public class ModuleVersion {

	Metadata module;
	String version;
	
	public static String SNAPSHOT_SUFFIX="SNAPSHOT";
	
	ModuleVersion(Metadata module,String version){
		this.module=module;
		this.version=version;
	}

	public Metadata getModule() {
		return module;
	}

	public void setModule(Metadata module) {
		this.module = module;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	public static String getMostRecentVersionAsString(Metadata module,boolean includeSnapshot){
		return getMostRecentVersionAsString(module, module.getVersioning().getVersions(), includeSnapshot);
	}
	
	public static ModuleVersion getMostRecentVersion(Metadata module,boolean includeSnapshot){
		return getMostRecentVersion(module, module.getVersioning().getVersions(), includeSnapshot);
	}
	
	public static String getMostRecentVersionAsString(Metadata module, List versionListString,boolean includeSnapshot){
		ModuleVersion version = getMostRecentVersion(module, versionListString, includeSnapshot);
		return version.getVersion();
	}
	
	public static ModuleVersion getMostRecentVersion(Metadata module, List versionListString,boolean includeSnapshot){
		List<ModuleVersion> modules = getVersionList(module,versionListString,includeSnapshot);
		ModuleVersionComparator comparator = new ModuleVersionComparator();
		Collections.sort(modules, comparator);
		
		return modules.get(modules.size()-1);
		
	}
	
	public static List<ModuleVersion> getVersionList(Metadata module, List versionListString,boolean includeSnapshot){
		List<ModuleVersion> list = new ArrayList<ModuleVersion>();
		for (Iterator iterator = versionListString.iterator(); iterator.hasNext();) {
			
			String stringVersion = (String) iterator.next();
			ModuleVersion moduleVersion = new ModuleVersion(module,stringVersion);
			if(moduleVersion.isSnapshot()){
				if(includeSnapshot){
					list.add(moduleVersion);
				}
			}
			else{
				list.add(moduleVersion);
			}
		}
		return list;
	}

	private boolean isSnapshot() {
		if(getVersion()!=null){
			if(getVersion().endsWith(SNAPSHOT_SUFFIX)){
				return true;
			}
		}
		return false;
	}
	
	public boolean isStable(){
		return !isSnapshot();
	}
	
	public String toString(){
		return this.version;
	}
}
