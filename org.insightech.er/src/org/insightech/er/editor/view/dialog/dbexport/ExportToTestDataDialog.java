package org.insightech.er.editor.view.dialog.dbexport;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.insightech.er.Activator;
import org.insightech.er.ResourceString;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.common.widgets.FileText;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.testdata.TestDataCreator;
import org.insightech.er.editor.model.dbexport.testdata.impl.DBUnitTestDataCreator;
import org.insightech.er.editor.model.dbexport.testdata.impl.SQLTestDataCreator;
import org.insightech.er.editor.model.testdata.TestData;
import org.insightech.er.util.Check;

public class ExportToTestDataDialog extends AbstractDialog {

	private Button formatSqlRadio;

	private Button formatDBUnitRadio;

	private FileText outputFileText;

	private Combo fileEncodingCombo;

	private TestData testData;

	private ERDiagram diagram;

	private IEditorPart editorPart;

	public ExportToTestDataDialog(Shell parentShell, IEditorPart editorPart,
			ERDiagram diagram, TestData testData) {
		super(parentShell, 3);

		this.testData = testData;
		this.editorPart = editorPart;
		this.diagram = diagram;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize(Composite parent) {
		GridData groupGridData = new GridData();
		groupGridData.horizontalAlignment = GridData.FILL;
		groupGridData.grabExcessHorizontalSpace = true;
		groupGridData.horizontalSpan = 3;

		GridLayout groupLayout = new GridLayout();
		groupLayout.marginWidth = 15;
		groupLayout.marginHeight = 15;

		Group group = new Group(parent, SWT.NONE);
		group.setText(ResourceString.getResourceString("label.format"));
		group.setLayoutData(groupGridData);
		group.setLayout(groupLayout);

		this.formatSqlRadio = CompositeFactory.createRadio(this, group,
				"label.sql");
		this.formatDBUnitRadio = CompositeFactory.createRadio(this, group,
				"label.dbunit");

		CompositeFactory.createLabel(parent, "label.output.file");

		GridData gridData = new GridData();
		gridData.widthHint = 200;

		this.outputFileText = new FileText(parent, SWT.BORDER, "");
		this.outputFileText.setLayoutData(gridData);

		this.fileEncodingCombo = CompositeFactory.createFileEncodingCombo(this,
				parent, "label.output.file.encoding", 1);
		CompositeFactory.filler(parent, 1);

		CompositeFactory.filler(parent, 3);
	}

	@Override
	protected void addListener() {
		super.addListener();

		this.formatSqlRadio.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				outputFileText.setFilterExtension(".sql");
				
				String path = outputFileText.getFilePath();

				if (path != null && path.endsWith(".xml")) {
					path = path.substring(0, path.length() - ".xml".length());
					path += ".sql";
					outputFileText.setText(path);
				}
			}

		});

		this.formatDBUnitRadio.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				outputFileText.setFilterExtension(".xml");
				
				String path = outputFileText.getFilePath();

				if (path != null && path.endsWith(".sql")) {
					path = path.substring(0, path.length() - ".sql".length());
					path += ".xml";
					outputFileText.setText(path);
				}
			}

		});

	}

	@Override
	protected String getErrorMessage() {
		if (this.outputFileText.isBlank()) {
			return "error.output.file.is.empty";
		}

		return null;
	}

	@Override
	protected void perfomeOK() throws InputException {
		if (this.formatSqlRadio.getSelection()) {
			this.testData.setExportFormat(TestData.EXPORT_FORMT_SQL);

		} else if (this.formatDBUnitRadio.getSelection()) {
			this.testData.setExportFormat(TestData.EXPORT_FORMT_DBUNIT);

		}

		this.testData.setExportFilePath(this.outputFileText.getFilePath());
		this.testData.setExportFileEncoding(this.fileEncodingCombo.getText());

		this.exportTestData();

		this.refreshProject();
	}

	private void exportTestData() {
		PrintWriter out = null;

		try {
			TestDataCreator testDataCreator = null;

			if (this.testData.getExportFormat() == TestData.EXPORT_FORMT_DBUNIT) {
				testDataCreator = new DBUnitTestDataCreator(this.testData
						.getExportFileEncoding());

			} else {
				testDataCreator = new SQLTestDataCreator();

			}

			testDataCreator.init(this.testData);

			File file = new File(this.testData.getExportFilePath());
			file.getParentFile().mkdirs();

			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(this.testData.getExportFilePath()),
					this.testData.getExportFileEncoding())));

			out.println(testDataCreator.getTestData(this.diagram));

			Activator.showMessageDialog("dialog.message.export.finish");

		} catch (Exception e) {
			Activator.showExceptionDialog(e);

		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setData() {
		String extension = null;

		if (this.testData.getExportFormat() == TestData.EXPORT_FORMT_DBUNIT) {
			this.formatDBUnitRadio.setSelection(true);
			extension = ".xml";

		} else {
			this.formatSqlRadio.setSelection(true);
			extension = ".sql";

		}

		this.outputFileText.setFilterExtension(extension);

		String outputFilePath = this.testData.getExportFilePath();

		if (Check.isEmpty(outputFilePath)) {
			IFile file = ((IFileEditorInput) editorPart.getEditorInput())
					.getFile();
			outputFilePath = file.getLocation().toOSString();

			outputFilePath = outputFilePath.substring(0, outputFilePath
					.lastIndexOf(File.separator))
					+ File.separator
					+ "testdata"
					+ File.separator
					+ this.testData.getName() + extension;
		}

		this.outputFileText.setText(outputFilePath);

		String outputFileEncoding = this.testData.getExportFileEncoding();

		if (Check.isEmpty(outputFileEncoding)) {
			outputFileEncoding = "UTF-8";
		}

		this.fileEncodingCombo.setText(outputFileEncoding);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		this.createButton(parent, IDialogConstants.OK_ID, ResourceString
				.getResourceString("label.button.export"), true);
		this.createButton(parent, IDialogConstants.CLOSE_ID,
				IDialogConstants.CLOSE_LABEL, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getTitle() {
		return "dialog.title.export.testdata";
	}

	private void refreshProject() {
		IFile file = ((IFileEditorInput) editorPart.getEditorInput()).getFile();
		IProject project = file.getProject();

		try {
			project.refreshLocal(IResource.DEPTH_INFINITE, null);

		} catch (CoreException e) {
			Activator.showExceptionDialog(e);
		}
	}
}
