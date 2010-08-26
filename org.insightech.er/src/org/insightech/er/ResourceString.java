package org.insightech.er;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.TreeMap;

public class ResourceString {

	private static ResourceBundle resource = ResourceBundle
			.getBundle("org.insightech.er.ERDiagram");;

	/**
	 * ERDiagram.properties �̎w�肳�ꂽ�L�[�ɑΉ�����l��Ԃ��܂�
	 * 
	 * @param key
	 *            ERDiagram.properties �Œ�`���ꂽ�L�[
	 * @return ERDiagram.properties �̎w�肳�ꂽ�L�[�ɑΉ�����l
	 */
	public static String getResourceString(String key) {
		try {
			return resource.getString(key);
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
