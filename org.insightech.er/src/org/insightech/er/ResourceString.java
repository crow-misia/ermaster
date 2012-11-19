package org.insightech.er;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.TreeMap;

public class ResourceString {

	private static ResourceBundle resource = ResourceBundle
			.getBundle("org.insightech.er.ERDiagram");;

	public static String getResourceString(String key) {
		return getResourceString(key, null);
	}
	
	public static String getResourceString(String key, String[] args) {
		try {
			String string = resource.getString(key);
			string = MessageFormat.format(string, args);
			
			return string;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	public static Map getResources(String prefix) {
		Map<String, String> props = new TreeMap<String, String>(Collections
				.reverseOrder());
		Enumeration keys = resource.getKeys();

		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			if (key.startsWith(prefix)) {
				props.put(key, resource.getString(key));
			}
		}

		return props;
	}
}
