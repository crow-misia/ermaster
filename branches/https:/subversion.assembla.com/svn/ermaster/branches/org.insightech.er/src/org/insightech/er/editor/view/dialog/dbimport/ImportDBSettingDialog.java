package org.insightech.er.editor.view.dialog.dbimport;

import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.insightech.er.Activator;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.db.DBManager;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.db.impl.standard_sql.StandardSQLDBManager;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.DBSetting;
import org.insightech.er.editor.view.dialog.common.AbstractDBSettingDialog;
import org.insightech.er.editor.view.dialog.common.DBSettingListDialog;

public class ImportDBSettingDialog extends AbstractDBSettingDialog {

	public ImportDBSettingDialog(Shell parentShell, ERDiagram diagram) {
		super(parentShell, diagram);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize(Composite parent) {
		super.initialize(parent);
		this.dbSetting = DBSettingListDialog.getDBSetting(0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void perfomeOK() throws InputException {
		this.setCurrentSetting();

		Connection con = null;

		try {
			con = this.dbSetting.connect();

		} catch (InputException e) {
			throw e;

		} catch (Exception e) {
			Activator.log(e);
			Throwable cause = e.getCause();

			if (cause instanceof UnknownHostException) {
				throw new InputException("error.server.not.found");
			}

			Activator.showMessageDialog(e.getMessage());
			throw new InputException("error.database.not.found");

		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					Activator.showExceptionDialog(e);
				}
			}
		}
	}

	public static Connection connect(DBSetting dbSetting)
			throws InputException, InstantiationException,
			IllegalAccessException, SQLException {
		String db = dbSetting.getDbsystem();

		DBManager manager = DBManagerFactory.getDBManager(db);

		String driverClassName = manager.getDriverClassName();

		if (StandardSQLDBManager.ID.equals(manager.getId())) {
			driverClassName = dbSetting.getDriverClassName();
		}

		Class driverClass = manager.getDriverClass(driverClassName);

		if (driverClass == null) {
			throw new InputException("error.jdbc.driver.not.found");
		}

		Driver driver = (Driver) driverClass.newInstance();

		String url = dbSetting.getUrl();

		if (!StandardSQLDBManager.ID.equals(db)) {
			url = manager.getURL(dbSetting.getServer(),
					dbSetting.getDatabase(), dbSetting.getPort());
		}

		Properties info = new Properties();
		if (dbSetting.getUser() != null) {
			info.put("user", dbSetting.getUser());
		}
		if (dbSetting.getPassword() != null) {
			info.put("password", dbSetting.getPassword());
		}

		return driver.connect(url, info);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getTitle() {
		return "dialog.title.import.tables";
	}

}
