package org.insightech.er.db;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.Activator;
import org.insightech.er.db.impl.standard_sql.StandardSQLDBManager;
import org.insightech.er.preference.PreferenceInitializer;
import org.insightech.er.preference.jdbc.JDBCPathDialog;
import org.insightech.er.util.Format;

public abstract class DBManagerBase implements DBManager {

	private Set<String> reservedWords = new HashSet<String>();

	public DBManagerBase() {
		DBManagerFactory.addDB(this);

		this.reservedWords = this.getReservedWords();
	}

	public String getURL(String serverName, String dbName, int port) {
		String temp = serverName.replaceAll("\\\\", "\\\\\\\\");
		String url = this.getURL().replaceAll("<SERVER NAME>", temp);
		url = url.replaceAll("<PORT>", String.valueOf(port));

		temp = dbName.replaceAll("\\\\", "\\\\\\\\");
		url = url.replaceAll("<DB NAME>", temp);

		return url;
	}

	public Class getDriverClass(String driverClassName) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		Class clazz = null;

		try {
			String path = PreferenceInitializer
					.getJDBCLibraryPath(this.getId());

			if (StandardSQLDBManager.ID.equals(this.getId())) {

				int num = store
						.getInt(PreferenceInitializer.JDBC_DRIVER_CLASS_NAME_LIST_NUM);

				for (int i = 0; i < num; i++) {
					if (driverClassName
							.equals(store
									.getString(PreferenceInitializer.JDBC_DRIVER_CLASS_NAME_PREFIX
											+ i))) {
						path = store
								.getString(PreferenceInitializer.JDBC_DRIVER_PATH_PREFIX
										+ i);
						break;
					}
				}
			}

			ClassLoader loader = this.getClassLoader(path);
			clazz = loader.loadClass(driverClassName);

		} catch (Exception e) {
			JDBCPathDialog dialog = new JDBCPathDialog(PlatformUI
					.getWorkbench().getActiveWorkbenchWindow().getShell(), this
					.getId(), driverClassName, PreferenceInitializer
					.getJDBCLibraryPath(this.getId()), false);

			if (dialog.open() == IDialogConstants.OK_ID) {

				String path = dialog.getPath();

				if (StandardSQLDBManager.ID.equals(this.getId())) {
					int num = store
							.getInt(PreferenceInitializer.JDBC_DRIVER_CLASS_NAME_LIST_NUM);

					store.setValue(
							PreferenceInitializer.JDBC_DRIVER_CLASS_NAME_PREFIX
									+ num, Format.null2blank(dialog
									.getDriverClassName()));
					store
							.setValue(
									PreferenceInitializer.JDBC_DRIVER_PATH_PREFIX
											+ num, Format.null2blank(path));
					num++;

					store
							.setValue(
									PreferenceInitializer.JDBC_DRIVER_CLASS_NAME_LIST_NUM,
									num);

				} else {
					store.setValue(
							PreferenceInitializer.JDBC_DRIVER_PATH_PREFIX
									+ this.getId(), Format.null2blank(path));
				}

				clazz = this.getDriverClass(dialog.getDriverClassName());
			}
		}

		return clazz;
	}

	private ClassLoader getClassLoader(String uri) throws SQLException,
			MalformedURLException {

		StringTokenizer tokenizer = new StringTokenizer(uri, ";");
		int count = tokenizer.countTokens();

		URL[] urls = new URL[count];

		for (int i = 0; i < urls.length; i++) {
			urls[i] = new URL("file", "", tokenizer.nextToken());
		}

		URLClassLoader loader = new URLClassLoader(urls, this.getClass()
				.getClassLoader());

		return loader;
	}

	abstract protected String getURL();

	abstract public String getDriverClassName();

	protected Set<String> getReservedWords() {
		Set<String> reservedWords = new HashSet<String>();

		ResourceBundle bundle = ResourceBundle.getBundle(this.getClass()
				.getPackage().getName()
				+ ".reserved_word");

		Enumeration<String> keys = bundle.getKeys();

		while (keys.hasMoreElements()) {
			reservedWords.add(keys.nextElement().toUpperCase());
		}

		return reservedWords;
	}

	public boolean isReservedWord(String str) {
		return reservedWords.contains(str.toUpperCase());
	}

	public boolean isSupported(int supportItem) {
		int[] supportItems = this.getSupportItems();

		for (int i = 0; i < supportItems.length; i++) {
			if (supportItems[i] == supportItem) {
				return true;
			}
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean doesNeedURLDatabaseName() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean doesNeedURLServerName() {
		return true;
	}

	abstract protected int[] getSupportItems();

	public List<String> getImportSchemaList(Connection con) throws SQLException {
		List<String> schemaList = new ArrayList<String>();

		DatabaseMetaData metaData = con.getMetaData();
		ResultSet rs = metaData.getSchemas();

		while (rs.next()) {
			schemaList.add(rs.getString(1));
		}

		return schemaList;
	}

	public List<String> getSystemSchemaList() {
		List<String> list = new ArrayList<String>();

		return list;
	}

}
