/*
 * Created on Mar 14, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.idega.manager.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;


/**
 * <p>
 * TODO thomas Describe Type VersionComparator
 * </p>
 *  Last modified: $Date: 2006/04/09 11:42:59 $ by $Author: laddi $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.2 $
 */
public class VersionComparator implements Comparator{
	
	private static Map pattern;
	
	private static final Integer MAIN_DELIMITER = new Integer(-4242);
	private static final Integer ZERO = new Integer(0);
	
	private static String[] patternKeys = { "rc", "m", "pre", "alpha", "beta", "gamma", "delta" };
	private static int[] patternValues = { -1, -2, -3, -10, -9, -8, -7 };
	
	static {
		pattern = new HashMap();
		for (int i = 0; i < patternKeys.length; i++) {
			String key = patternKeys[i];
			Integer value = new Integer(patternValues[i]);
			pattern.put( key, value);
		}
	}
	
	private Map convertedVersions;
	
//	// only for testing
//	public static void test(String[] arg) {
//		VersionComparator comparator = new VersionComparator();
//		List list1 = comparator.convertVersion("3.0RC2");
//		//result: [[3], [0, -2, 2]]
//		List list2 = comparator.convertVersion("3.001RC3");
//		//result: [[3], [1, -2, 3]]
//		List list3 = comparator.convertVersion("3.02.1234.123pre123");
//		// result: [[3], [2], [1234], [123, -3, 123]]
//		List list4 = comparator.convertVersion("123.02abcdz-pre");
//		// result: [[123], [2, -103, -102, -101, -100, -78], [-3]]
//		SortedSet map = new TreeSet(comparator);
//		map.add("3.01RC2");
//		map.add("3");
//		map.add("3.0RC2");
//		map.add("3.1RC4");
//		map.add("3.0m2");
//		map.add("3.0.0");
//		map.add("3.0.03");
//		map.add("3.M1");
//		Iterator iterator = map.iterator();
//		while (iterator.hasNext()) {
//			System.out.println(iterator.next());
//		}
//	}
	
	public int compare(String version1, String version2) {
		List list1 = getConvertedVersion(version1);
		List list2 = getConvertedVersion(version2);
		return compare(list1, list2);
	}
	
	
	public int compare(Object o1, Object o2) {
		return compare((String) o1, (String) o2);
	}
	

	
	public int compare(List convertedVersion1, List convertedVersion2) {
		int index = 0;
		int length1 = convertedVersion1.size();
		int length2 = convertedVersion2.size();
		while (true) {
			boolean indexLessThan1 = index < length1;
			boolean indexLessThan2 = index < length2;
			if ((! indexLessThan1) && (! indexLessThan2)) {
				return 0;
			}
			Integer integer1 = (indexLessThan1) ? (Integer) convertedVersion1.get(index) : ZERO;
			Integer integer2 = (indexLessThan2) ? (Integer) convertedVersion2.get(index) : ZERO;
			int result = integer1.compareTo(integer2);
			if (result != 0) {
				if (! ( ( ZERO.equals(integer1) && MAIN_DELIMITER.equals(integer2) ) ||
					( MAIN_DELIMITER.equals(integer1) && ZERO.equals(integer2) ) ) )  {
					return result;
				}
			}
			index++;
		}
	}
	
	private List getConvertedVersion(String version) {
		// caching and shortcut
		if (this.convertedVersions == null) {
			this.convertedVersions = new HashMap();
		}
		List list = null;
		if (this.convertedVersions.containsKey(version)) {
			list = (List) this.convertedVersions.get(version);
		}
		else {
			list = convertVersion(version);
			this.convertedVersions.put(version, list);
		}
		return list;
	}
		
	private List convertVersion(String version) {
		List resultList = new ArrayList();
		StringTokenizer tokenizer = new StringTokenizer(version, "-._");
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			int index = 0;
			StringBuffer buffer = new StringBuffer();
			boolean readingNumber = false;
			boolean readingString = false;
			while (index < token.length()) {
				char c = token.charAt(index);
				if (Character.isDigit(c)) {
					// reading number
					if (readingString) {
						addUnicodeValuesOfString(buffer, resultList);
						buffer = new StringBuffer();
						readingString = false;
					}
					readingNumber = true;
					buffer.append(c);
				}
				else {
					// reading string
					if (readingNumber) {
						resultList.add(new Integer(buffer.toString()));
						buffer = new StringBuffer();
						readingNumber = false;
					}
					readingString = true;
					buffer.append(c);
					// check for pattern
					String temp = buffer.toString();
					temp = temp.toLowerCase();
					if (pattern.containsKey(temp)) {
						resultList.add(pattern.get(temp));
						readingString = false;
						buffer = new StringBuffer();
					}
				}
				index++;
			}
			if (readingNumber) {
				resultList.add(new Integer(buffer.toString()));
			}
			else {
				addUnicodeValuesOfString(buffer, resultList);
			}
			resultList.add(MAIN_DELIMITER);
		}
		return resultList;
	}
	
	private void addUnicodeValuesOfString(StringBuffer buffer, List list) {
		for (int i=0; i < buffer.length(); i++) {
			char c = buffer.charAt(i);
			// shift the value to a negative number
			int k = c -200;
			Integer integerValue = new Integer(k);
			list.add(integerValue);
		}
	}
	


}
