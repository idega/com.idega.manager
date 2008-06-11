package com.idega.manager.maven2;

import java.util.Comparator;

public class ModuleVersionComparator implements Comparator<ModuleVersion> {

	public int compare(ModuleVersion o1, ModuleVersion o2) {
        String s1 = o1.getVersion();
        String s2 = o2.getVersion();
        return s1.compareTo(s2);
	}

}
