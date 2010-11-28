package org.insightech.er.editor.view.dialog.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.insightech.er.Activator;
import org.insightech.er.ResourceString;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.editor.model.settings.DBSetting;
import org.insightech.er.preference.PreferenceInitializer;
import org.insightech.er.util.Format;

public class DBSettingListDialog extends AbstractDialog {

	private Table settingTable;

	private List<DBSetting> dbSettingList;

	private DBSetting result;

	private String database;

	public DBSettingListDialog(Shell parentShell, String database) {
		super(parentShell);

		this.database = database;
		this.dbSettingList = new ArrayList<DBSetting>();
	}

	@Override
	protected void initialize(Composite composite) {
		GridData gridData = new GridData();
		gridData.heightHint = 150;

		this.settingTable = new Table(composite, SWT.FULL_SELECTION
				| SWT.BORDER);
		this.settingTable.setHeaderVisible(true);
		this.settingTable.setLayoutData(gridData);
		this.settingTable.setLinesVisible(false);

		TableColumn dbsystemColumn = new TableColumn(this.settingTable,
				SWT.LEFT);
		dbsystemColumn.setWidth(100);
		dbsystemColumn.setText(ResourceString
				.getResourceString("label.database"));

		TableColumn serverColumn = new TableColumn(this.settingTable, SWT.LEFT);
		serverColumn.setWidth(100);
		serverColumn.setText(ResourceString
				.getResourceString("label.server.name"));

		TableColumn portColumn = new TableColumn(this.settingTable, SWT.RIGHT);
		portColumn.setWidth(80);
		portColumn.setText(ResourceString.getResourceString("label.port"));

		TableColumn databaseColumn = new TableColumn(this.settingTable,
				SWT.LEFT);
		databaseColumn.setWidth(100);
		databaseColumn.setText(ResourceString
				.getResourceString("label.database.name"));

		TableColumn userNameColumn = new TableColumn(this.settingTable,
				SWT.LEFT);
		userNameColumn.setWidth(100);
		userNameColumn.setText(ResourceString
				.getResourceString("label.user.name"));

		TableColumn urlTableColumn = new TableColumn(this.settingTable,
				SWT.LEFT);
		urlTableColumn.setWidth(130);
		urlTableColumn.setText(ResourceString.getResourceString("label.url"));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addListener() {
		super.addListener();

		this.settingTable.addMouseListener(new MouseAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				buttonPressed(IDialogConstants.OK_ID);
			}
		});

		this.settingTable.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				validate();

				int index = settingTable.getSelectionIndex();
				if (index == -1) {
					return;
				}

				selectTable(index);
			}
		});

	}

	private void saveSetting() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		store.setValue(PreferenceInitializer.DB_SETTING_LIST_NUM,
				this.dbSettingList.size());

		for (int i = 0; i < this.dbSettingList.size(); i++) {
			DBSetting dbSetting = this.dbSettingList.get(i);
			saveSetting(i + 1, dbSetting);
		}
	}

	public static void saveSetting(int no, DBSetting dbSetting) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		store.setValue(PreferenceInitializer.DB_SETTING_DBSYSTEM + no, Format
				.null2blank(dbSetting.getDbsystem()));
		store.setValue(PreferenceInitializer.DB_SETTING_SERVER + no, Format
				.null2blank(dbSetting.getServer()));
		store.setValue(PreferenceInitializer.DB_SETTING_PORT + no, dbSetting
				.getPort());
		store.setValue(PreferenceInitializer.DB_SETTING_DATABASE + no, Format
				.null2blank(dbSetting.getDatabase()));
		store.setValue(PreferenceInitializer.DB_SETTING_USER + no, Format
				.null2blank(dbSetting.getUser()));
		store.setValue(PreferenceInitializer.DB_SETTING_PASSWORD + no, Format
				.null2blank(dbSetting.getPassword()));
		store.setValue(PreferenceInitializer.DB_SETTING_URL + no, Format
				.null2blank(dbSetting.getUrl()));
		store.setValue(PreferenceInitializer.DB_SETTING_DRIVER_CLASS_NAME + no,
				Format.null2blank(dbSetting.getDriverClassName()));
	}

	@Override
	protected void perfomeOK() throws InputException {
		int index = settingTable.getSelectionIndex();
		this.result = this.dbSettingList.get(index);
	}

	public DBSetting getResult() {
		return this.result;
	}

	public int getResultIndex() {
		return this.dbSettingList.indexOf(this.result);
	}

	@Override
	protected void setData() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		int num = store.getInt(PreferenceInitializer.DB_SETTING_LIST_NUM);

		for (int i = 1; i <= num; i++) {
			DBSetting dbSetting = getDBSetting(i);
			if (this.database != null
					&& !dbSetting.getDbsystem().equals(this.database)) {
				continue;
			}
			this.dbSettingList.add(dbSetting);
		}

		Collections.sort(this.dbSettingList);

		for (DBSetting dbSetting : this.dbSettingList) {
			TableItem item = new TableItem(this.settingTable, SWT.NONE);
			item.setText(0, dbSetting.getDbsystem());
			item.setText(1, dbSetting.getServer());
			if (dbSetting.getPort() != 0) {
				item.setText(2, String.valueOf(dbSetting.getPort()));
			}
			item.setText(3, dbSetting.getDatabase());
			item.setText(4, dbSetting.getUser());
			item.setText(5, Format.null2blank(dbSetting.getUrl()));
		}

		this.setButtonEnabled(false);
	}

	public static DBSetting getDBSetting(int no) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		String dbsystem = store
				.getString(PreferenceInitializer.DB_SETTING_DBSYSTEM + no);
		String server = store.getString(PreferenceInitializer.DB_SETTING_SERVER
				+ no);
		int portNo = store.getInt(PreferenceInitializer.DB_SETTING_PORT + no);

		String database = store
				.getString(PreferenceInitializer.DB_SETTING_DATABASE + no);
		String user = store.getString(PreferenceInitializer.DB_SETTING_USER
				+ no);
		String password = store
				.getString(PreferenceInitializer.DB_SETTING_PASSWORD + no);
		String url = store.getString(PreferenceInitializer.DB_SETTING_URL + no);
		String driverClassName = store
				.getString(PreferenceInitializer.DB_SETTING_DRIVER_CLASS_NAME
						+ no);

		return new DBSetting(dbsystem, server, portNo, database, user,
				password, url, driverClassName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, ResourceString
				.getResourceString("label.load.setting"), true);
		createButton(parent, IDialogConstants.STOP_ID, ResourceString
				.getResourceString("label.delete"), false);
		createButton(parent, IDialogConstants.CLOSE_ID,
				IDialogConstants.CLOSE_LABEL, false);

		setButtonEnabled(false);
	}

	private void setButtonEnabled(boolean enabled) {
		Button okButton = this.getButton(IDialogConstants.OK_ID);
		if (okButton != null) {
			okButton.setEnabled(enabled);
		}

		Button deleteButton = this.getButton(IDialogConstants.STOP_ID);
		if (deleteButton != null) {
			deleteButton.setEnabled(enabled);
		}
	}

	private void selectTable(int index) {
		this.settingTable.select(index);

		if (index >= 0) {
			this.setButtonEnabled(true);
		} else {
			this.setButtonEnabled(false);
		}
	}

	@Override
	protected String getErrorMessage() {
		int index = settingTable.getSelectionIndex();
		if (index == -1) {
			return "dialog.message.load.db.setting";
		}

		return null;
	}

	@Override
	protected String getTitle() {
		return "label.load.database.setting";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.STOP_ID) {
			int index = this.settingTable.getSelectionIndex();

			if (index != -1) {
				this.settingTable.remove(index);
				this.dbSettingList.remove(index);

				this.saveSetting();

				if (index >= this.settingTable.getItemCount()) {
					index = this.settingTable.getItemCount() - 1;
				}

				this.selectTable(index);
			}
		}

		super.buttonPressed(buttonId);
	}

}
