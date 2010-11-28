package org.insightech.er.preference;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.insightech.er.Activator;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.util.Check;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	public static final String TEMPLATE_DIR = "template";

	public static final String TRANSLATION_DIR = "translation";

	public static final String TEMPLATE_FILE_LIST = "template_file_list";

	public static final String TRANSLATION_FILE_LIST = "translation_file_list";

	public static final String DEFAULT_TEMPLATE_FILE_EN = "template_en.xls";

	public static final String DEFAULT_TEMPLATE_FILE_JA = "template_ja.xls";

	public static final String JDBC_DRIVER_PATH_PREFIX = "jdbc.driver.path.";

	public static final String JDBC_DRIVER_CLASS_NAME_PREFIX = "jdbc.driver.class.name.";

	public static final String JDBC_DRIVER_CLASS_NAME_LIST_NUM = "jdbc.driver.class.name.list.num";

	public static final String DB_SETTING_LIST_NUM = "db.setting.list.num";

	public static final String DB_SETTING_CURRENT_NUM = "db.setting.list.current.num";

	public static final String DB_SETTING_DBSYSTEM = "db.setting.dbsystem.";

	public static final String DB_SETTING_SERVER = "db.setting.server.";

	public static final String DB_SETTING_PORT = "db.setting.port.";

	public static final String DB_SETTING_DATABASE = "db.setting.database.";

	public static final String DB_SETTING_USER = "db.setting.user.";

	public static final String DB_SETTING_URL = "db.setting.url.";

	public static final String DB_SETTING_DRIVER_CLASS_NAME = "db.setting.driver.class.name.";

	public static final String DB_SETTING_PASSWORD = "setting.password.";

	public PreferenceInitializer() {
	}

	@Override
	public void initializeDefaultPreferences() {
	}

	public static String getTemplatePath(String fileName) {
		IPath dataLocation = Activator.getDefault().getStateLocation();
		String path = dataLocation.append(PreferenceInitializer.TEMPLATE_DIR)
				.append(fileName).toOSString();

		return path;
	}

	public static String getTranslationPath(String fileName) {
		IPath dataLocation = Activator.getDefault().getStateLocation();
		String path = dataLocation
				.append(PreferenceInitializer.TRANSLATION_DIR).append(fileName)
				.toOSString();

		return path;
	}

	public static String getJDBCLibraryPath(String database) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String path = store
				.getString(PreferenceInitializer.JDBC_DRIVER_PATH_PREFIX
						+ database);

		if (path == null) {
			path = "";
		}

		return path;
	}

	public static Map<String, String> getJDBCDriverMap() {
		Map<String, String> map = new LinkedHashMap<String, String>();

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		int num = store
				.getInt(PreferenceInitializer.JDBC_DRIVER_CLASS_NAME_LIST_NUM);

		for (int i = 0; i < num; i++) {
			String driverClassName = store
					.getString(PreferenceInitializer.JDBC_DRIVER_CLASS_NAME_PREFIX
							+ i);
			if (!Check.isEmpty(driverClassName)) {
				String path = store
						.getString(PreferenceInitializer.JDBC_DRIVER_PATH_PREFIX
								+ i);

				if (path == null) {
					path = "";
				}

				map.put(driverClassName, path);
			}

		}

		return map;
	}

	public static void clearJDBCDriverInfo() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		int num = store
				.getInt(PreferenceInitializer.JDBC_DRIVER_CLASS_NAME_LIST_NUM);

		for (int i = 0; i < num; i++) {
			store.setValue(PreferenceInitializer.JDBC_DRIVER_CLASS_NAME_PREFIX
					+ i, "");
			store.setValue(PreferenceInitializer.JDBC_DRIVER_PATH_PREFIX + i,
					"");
		}

		store.setValue(PreferenceInitializer.JDBC_DRIVER_CLASS_NAME_LIST_NUM,
				0);

		for (String db : DBManagerFactory.getAllDBList()) {
			store.setValue(PreferenceInitializer.JDBC_DRIVER_CLASS_NAME_PREFIX
					+ db, "");
			store.setValue(PreferenceInitializer.JDBC_DRIVER_PATH_PREFIX + db,
					"");
		}
	}
}
