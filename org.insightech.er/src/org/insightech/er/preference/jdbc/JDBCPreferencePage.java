package org.insightech.er.preference.jdbc;

import java.util.Map;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.Activator;
import org.insightech.er.ResourceString;
import org.insightech.er.Resources;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.db.impl.standard_sql.StandardSQLDBManager;
import org.insightech.er.preference.PreferenceInitializer;
import org.insightech.er.util.Format;

public class JDBCPreferencePage extends
		org.eclipse.jface.preference.PreferencePage implements
		IWorkbenchPreferencePage {

	private Table table;

	private Button addButton;

	private Button editButton;

	private Button deleteButton;

	public void init(IWorkbench workbench) {
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;

		composite.setLayout(gridLayout);

		this.initTable(composite);
		this.createButton(composite);
		this.addListener();

		return composite;
	}

	private void initTable(Composite parent) {
		this.table = new Table(parent, SWT.SINGLE | SWT.BORDER
				| SWT.FULL_SELECTION);

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.heightHint = 200;
		gridData.horizontalSpan = 3;

		this.table.setLayoutData(gridData);

		this.table.setLinesVisible(true);
		this.table.setHeaderVisible(true);

		TableColumn nameColumn = new TableColumn(table, SWT.NONE);
		nameColumn.setText(ResourceString.getResourceString("label.database"));
		nameColumn.setWidth(200);

		TableColumn driverClassNameColumn = new TableColumn(table, SWT.NONE);
		driverClassNameColumn.setText(ResourceString
				.getResourceString("label.driver.class.name"));
		driverClassNameColumn.setWidth(200);

		TableColumn pathColumn = new TableColumn(table, SWT.NONE);
		pathColumn.setText(ResourceString.getResourceString("label.path"));
		pathColumn.setWidth(200);

		this.setData();
	}

	private void createButton(Composite parent) {
		GridData buttonGridData = new GridData();
		buttonGridData.widthHint = Resources.BUTTON_WIDTH;

		this.addButton = new Button(parent, SWT.NONE);
		this.addButton.setLayoutData(buttonGridData);
		this.addButton.setText(ResourceString
				.getResourceString("label.button.add"));

		this.editButton = new Button(parent, SWT.NONE);
		this.editButton.setLayoutData(buttonGridData);
		this.editButton.setText(ResourceString
				.getResourceString("label.button.edit"));

		this.deleteButton = new Button(parent, SWT.NONE);
		this.deleteButton.setLayoutData(buttonGridData);
		this.deleteButton.setText(ResourceString
				.getResourceString("label.button.delete"));
		this.deleteButton.setEnabled(false);
	}

	private void setData() {
		this.table.removeAll();

		for (String db : DBManagerFactory.getAllDBList()) {
			if (StandardSQLDBManager.ID.equals(db)) {
				Map<String, String> driverMap = PreferenceInitializer
						.getJDBCDriverMap();

				for (Map.Entry<String, String> entry : driverMap.entrySet()) {
					TableItem tableItem = new TableItem(this.table, SWT.NONE);
					tableItem.setBackground(ColorConstants.white);
					tableItem.setText(0, db);
					tableItem.setText(1, entry.getKey());
					tableItem.setText(2, entry.getValue());
				}

			} else {
				TableItem tableItem = new TableItem(this.table, SWT.NONE);
				tableItem.setBackground(ColorConstants.white);
				tableItem.setText(0, db);
				String driverClassName = DBManagerFactory.getDBManager(db)
						.getDriverClassName();
				tableItem.setText(1, driverClassName);
				tableItem.setText(2, PreferenceInitializer
						.getJDBCLibraryPath(db));
			}
		}
	}

	@Override
	protected void performDefaults() {
		PreferenceInitializer.clearJDBCDriverInfo();

		setData();

		super.performDefaults();
	}

	@Override
	public boolean performOk() {
		PreferenceInitializer.clearJDBCDriverInfo();

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		int num = 0;

		for (int i = 0; i < this.table.getItemCount(); i++) {
			TableItem tableItem = this.table.getItem(i);

			String db = tableItem.getText(0);

			String driverClassName = tableItem.getText(1);
			String path = tableItem.getText(2);

			if (StandardSQLDBManager.ID.equals(db)) {
				store.setValue(
						PreferenceInitializer.JDBC_DRIVER_CLASS_NAME_PREFIX
								+ num, Format.null2blank(driverClassName));
				store.setValue(PreferenceInitializer.JDBC_DRIVER_PATH_PREFIX
						+ num, Format.null2blank(path));
				num++;

			} else {
				store.setValue(PreferenceInitializer.JDBC_DRIVER_PATH_PREFIX
						+ db, Format.null2blank(path));
			}
		}

		store.setValue(PreferenceInitializer.JDBC_DRIVER_CLASS_NAME_LIST_NUM,
				num);

		return super.performOk();
	}

	private void addListener() {
		this.table.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent selectionevent) {
				int index = table.getSelectionIndex();
				if (index == -1) {
					return;
				}

				TableItem item = table.getItem(index);

				if (StandardSQLDBManager.ID.equals(item.getText(0))) {
					deleteButton.setEnabled(true);

				} else {
					deleteButton.setEnabled(false);
				}
			}

		});

		this.table.addMouseListener(new MouseAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				int index = table.getSelectionIndex();
				if (index == -1) {
					return;
				}

				TableItem item = table.getItem(index);

				boolean editDriverClassName = false;

				if (StandardSQLDBManager.ID.equals(item.getText(0))) {
					editDriverClassName = true;
				}

				JDBCPathDialog dialog = new JDBCPathDialog(PlatformUI
						.getWorkbench().getActiveWorkbenchWindow().getShell(),
						item.getText(0), item.getText(1), item.getText(2),
						editDriverClassName);

				if (dialog.open() == IDialogConstants.OK_ID) {
					item.setText(1, dialog.getDriverClassName());
					item.setText(2, dialog.getPath());
				}
			}
		});

		this.addButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				JDBCPathDialog dialog = new JDBCPathDialog(PlatformUI
						.getWorkbench().getActiveWorkbenchWindow().getShell(),
						StandardSQLDBManager.ID, null, null, true);

				if (dialog.open() == IDialogConstants.OK_ID) {
					IPreferenceStore store = Activator.getDefault()
							.getPreferenceStore();

					int num = store
							.getInt(PreferenceInitializer.JDBC_DRIVER_CLASS_NAME_LIST_NUM);

					store.setValue(
							PreferenceInitializer.JDBC_DRIVER_CLASS_NAME_PREFIX
									+ num, Format.null2blank(dialog
									.getDriverClassName()));
					store
							.setValue(
									PreferenceInitializer.JDBC_DRIVER_PATH_PREFIX
											+ num, Format.null2blank(dialog
											.getPath()));

					num++;

					store
							.setValue(
									PreferenceInitializer.JDBC_DRIVER_CLASS_NAME_LIST_NUM,
									num);

					setData();
				}
			}

		});

		this.editButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = table.getSelectionIndex();
				if (index == -1) {
					return;
				}

				TableItem item = table.getItem(index);

				boolean editDriverClassName = false;

				if (StandardSQLDBManager.ID.equals(item.getText(0))) {
					editDriverClassName = true;
				}

				JDBCPathDialog dialog = new JDBCPathDialog(PlatformUI
						.getWorkbench().getActiveWorkbenchWindow().getShell(),
						item.getText(0), item.getText(1), item.getText(2),
						editDriverClassName);

				if (dialog.open() == IDialogConstants.OK_ID) {
					item.setText(1, dialog.getDriverClassName());
					item.setText(2, dialog.getPath());
				}
			}

		});

		this.deleteButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent event) {
				try {
					int index = table.getSelectionIndex();

					if (index == -1) {
						return;
					}

					TableItem item = table.getItem(index);
					if (StandardSQLDBManager.ID.equals(item.getText(0))) {
						table.remove(index);
					}

				} catch (Exception e) {
					Activator.showExceptionDialog(e);
				}
			}
		});

	}
}
