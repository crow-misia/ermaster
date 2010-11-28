package org.insightech.er.preference.jdbc;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.ResourceString;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.preference.MultiFileFieldEditor;
import org.insightech.er.util.Format;

public class JDBCPathDialog extends AbstractDialog {

	private MultiFileFieldEditor fileFieldEditor;

	private Text driverClassNameText;

	private String database;

	private String driverClassName;

	private String path;

	private boolean editDriverClassName;

	public JDBCPathDialog(Shell parentShell, String database,
			String driverClassName, String path, boolean editDriverClassName) {
		super(parentShell, 3);

		this.database = database;
		this.driverClassName = driverClassName;
		this.path = path;
		this.editDriverClassName = editDriverClassName;
	}

	@Override
	protected Object createLayoutData() {
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.widthHint = 600;
		gridData.heightHint = 180;
		gridData.horizontalIndent = 10;
		gridData.horizontalSpan = 10;

		return gridData;
	}

	@Override
	protected void initialize(Composite composite) {
		GridData gridData = new GridData();
		gridData.horizontalSpan = 3;
		gridData.heightHint = 50;

		Label label = new Label(composite, SWT.NONE);
		label.setLayoutData(gridData);
		label.setText(ResourceString
				.getResourceString("label.jdbc.driver.message"));

		label = new Label(composite, SWT.NONE);
		label.setText(ResourceString.getResourceString("label.database"));

		GridData gridData2 = new GridData();
		gridData2.horizontalSpan = 2;

		label = new Label(composite, SWT.NONE);
		label.setLayoutData(gridData2);
		label.setText(database);

		this.driverClassNameText = CompositeFactory.createText(null, composite,
				"label.driver.class.name", 2, -1, SWT.BORDER | SWT.READ_ONLY,
				false);

		this.driverClassNameText.setEditable(this.editDriverClassName);

		this.fileFieldEditor = new MultiFileFieldEditor("", ResourceString
				.getResourceString("label.path"), composite);
		this.fileFieldEditor.setMultiple(true);
		
		this.fileFieldEditor.setFocus();
	}

	@Override
	protected String getTitle() {
		return "label.path";
	}

	@Override
	protected String getErrorMessage() {
		return null;
	}

	@Override
	protected void perfomeOK() throws InputException {
		this.path = fileFieldEditor.getStringValue();
		this.driverClassName = this.driverClassNameText.getText();
	}

	@Override
	protected void setData() {
		this.fileFieldEditor.setStringValue(this.path);
		this.driverClassNameText.setText(Format
				.null2blank(this.driverClassName));
	}

	public String getPath() {
		return this.path;
	}

	public String getDriverClassName() {
		return this.driverClassName;
	}
	
}
