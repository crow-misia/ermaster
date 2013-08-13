package org.insightech.er.db;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.editor.model.settings.JDBCDriverSetting;
import org.insightech.er.preference.PreferenceInitializer;
import org.insightech.er.preference.jdbc.JDBCPathDialog;

public abstract class DBManagerBase implements DBManager {
	private final Set<String> reservedWords;

	private final Map<String, ClassLoader> loaderMap;

	private final boolean[] supportFunctions;

	public DBManagerBase() {
		this.reservedWords = this.getReservedWords();

		this.loaderMap = new HashMap<String, ClassLoader>();

		this.supportFunctions = createSupportFunctionArray(this.getSupportItems());
	}

	private static boolean[] createSupportFunctionArray(final SupportFunction[] functions) {
		final SupportFunction[] funcs = SupportFunction.values();
		final int n = funcs.length;
		final boolean[] retval = new boolean[funcs.length];

		for (int i = 0; i < n; i++) {
			retval[i] = false;
		}
		for (final SupportFunction func : functions) {
			retval[func.ordinal()] = true;
		}
		return retval;
	}

	public String getURL(String serverName, String dbName, int port) {
		String url = StringUtils.replace(this.getURL(), "<SERVER NAME>", serverName);
		url = StringUtils.replace(url, "<PORT>", String.valueOf(port));
		return StringUtils.replace(url, "<DB NAME>", dbName);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class<Driver> getDriverClass(String driverClassName) {
		String path = null;
		Class clazz = null;

		do {
		try {
			if (driverClassName.equals("sun.jdbc.odbc.JdbcOdbcDriver")) {
				return (Class<Driver>) Class
						.forName("sun.jdbc.odbc.JdbcOdbcDriver");

			} else {
				path = PreferenceInitializer.getJDBCDriverPath(this.getId(),
						driverClassName);

				// Cash the class loader to map.
				// Because if I use the another loader with the driver using native library(.dll)
				// next error occur.
				// 
				// java.lang.UnsatisfiedLinkError: Native Library xxx.dll already loaded in another classloader
				//
				ClassLoader loader = this.loaderMap.get(path);
				if (loader == null) {
					loader = this.getClassLoader(path);
					this.loaderMap.put(path, loader);
				}
				
				clazz = loader.loadClass(driverClassName);
			}

		} catch (Exception e) {
			JDBCPathDialog dialog = new JDBCPathDialog(PlatformUI
					.getWorkbench().getActiveWorkbenchWindow().getShell(), this
					.getId(), driverClassName, path,
					Collections.<JDBCDriverSetting>emptySet(), false);

			if (dialog.open() == IDialogConstants.OK_ID) {
				JDBCDriverSetting newDriverSetting = new JDBCDriverSetting(this
						.getId(), dialog.getDriverClassName(), dialog.getPath());

				Set<JDBCDriverSetting> driverSettingList = PreferenceInitializer
						.getJDBCDriverSettingList();

				if (driverSettingList.contains(newDriverSetting)) {
					driverSettingList.remove(newDriverSetting);
				}
				driverSettingList.add(newDriverSetting);

				PreferenceInitializer
						.saveJDBCDriverSettingList(driverSettingList);
			}
		}
		} while (clazz == null);

		return clazz;
	}

	private ClassLoader getClassLoader(String uri) throws SQLException,
			MalformedURLException {

		StringTokenizer tokenizer = new StringTokenizer(uri, ";");
		int count = tokenizer.countTokens();

		URL[] urls = new URL[count];

		for (int i = 0, n = urls.length; i < n; i++) {
			urls[i] = new URL("file", "", tokenizer.nextToken());
		}

		return new URLClassLoader(urls, this.getClass().getClassLoader());
	}

	protected abstract  String getURL();

	public abstract String getDriverClassName();

	protected Set<String> getReservedWords() {
		final ResourceBundle bundle = ResourceBundle.getBundle(this.getClass()
				.getPackage().getName()
				+ ".reserved_word");

		return new HashSet<String>(bundle.keySet());
	}

	public boolean isReservedWord(String str) {
		return reservedWords.contains(str.toUpperCase());
	}

	public boolean isSupported(SupportFunction function) {
		return this.supportFunctions[function.ordinal()];
	}

	@Override
	public boolean doesNeedURLDatabaseName() {
		return true;
	}

	@Override
	public boolean doesNeedURLServerName() {
		return true;
	}

	@Override
	public List<String> getImportSchemaList(Connection con) throws SQLException {
		List<String> schemaList = new ArrayList<String>();

		DatabaseMetaData metaData = con.getMetaData();
		try {
			ResultSet rs = metaData.getSchemas();
			while (rs.next()) {
				schemaList.add(rs.getString(1));
			}

		} catch (SQLException e) {
			// when schema is not supported
		}

		return schemaList;
	}

	@Override
	public final Set<String> getSystemSchemaList() {
		try {
			final ResourceBundle bundle = ResourceBundle.getBundle(this.getClass()
					.getPackage().getName()
					+ ".system_schema");

			return new HashSet<String>(bundle.keySet());
		} catch (final MissingResourceException e) {
			return Collections.emptySet();
		}
	}

	protected abstract SupportFunction[] getSupportItems();
}
