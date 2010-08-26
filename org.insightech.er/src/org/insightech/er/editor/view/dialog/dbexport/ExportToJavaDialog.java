package org.insightech.er.editor.view.dialog.dbexport;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.Activator;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.common.widgets.FileText;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.java.ExportToJavaWithProgressManager;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.settings.ExportSetting;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.util.Format;

public class ExportToJavaDialog extends AbstractDialog {

	private FileText outputDirText;

	private Text packageText;

	private ERDiagram diagram;

	private IEditorPart editorPart;

	private ExportSetting exportSetting;

	public ExportToJavaDialog(Shell parentShell, ERDiagram diagram,
			IEditorPart editorPart) {
		super(parentShell, 3);

		this.diagram = diagram;
		this.editorPart = editorPart;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initLayout(GridLayout layout) {
		super.initLayout(layout);

		layout.verticalSpacing = 15;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize(Composite parent) {
		GridData gridData = new GridData();
		gridData.widthHint = 200;

		CompositeFactory.createLabel(parent, "label.output.dir");

		this.outputDirText = new FileText(parent, SWT.BORDER, ".xls");
		this.outputDirText.setLayoutData(gridData);

		this.outputDirText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validate();
			}
		});

	}

	@Override
	protected String getErrorMessage() {
		if (this.outputDirText.isBlank()) {
			return "error.output.dir.is.empty";
		}

		return null;
	}

	@Override
	protected void perfomeOK() throws InputException {
		Category currentCategory = this.diagram.getCurrentCategory();
		int currentCategoryIndex = this.diagram.getCurrentCategoryIndex();

		InputStream stream = null;

		try {
			ProgressMonitorDialog monitor = new ProgressMonitorDialog(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getShell());

			String outputDirPath = this.outputDirText.getFilePath();
			String packageName = this.packageText.getText();

			ExportToJavaWithProgressManager manager = new ExportToJavaWithProgressManager(
					outputDirPath, packageName, diagram);
			monitor.run(true, true, manager);

			this.exportSetting = new ExportSetting();

			this.exportSetting.setJavaOutput(outputDirPath);
			this.exportSetting.setPackageName(packageName);

			if (manager.getException() != null) {
				throw manager.getException();
			}

		} catch (IOException e) {
			Activator.showMessageDialog(e.getMessage());

		} catch (InterruptedException e) {

		} catch (Exception e) {
			Activator.showExceptionDialog(e);

		} finally {
			this.diagram.setCurrentCategory(currentCategory,
					currentCategoryIndex);

			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					Activator.showExceptionDialog(e);
				}
			}
		}
	}

	public ExportSetting getExportSetting() {
		return this.exportSetting;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setData() {
		Settings settings = this.diagram.getDiagramContents().getSettings();
		ExportSetting exportSetting = settings.getExportSetting();

		String outputDir = Format.null2blank(exportSetting.getJavaOutput());

		if ("".equals(outputDir)) {
			IFile file = ((IFileEditorInput) editorPart.getEditorInput())
					.getFile();
			outputDir = file.getLocation().toOSString();
		}
		outputDir = outputDir.substring(0, outputDir.lastIndexOf(".")) + ".xls";

		this.outputDirText.setText(outputDir);

		this.packageText.setText(Format.null2blank(exportSetting
				.getPackageName()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getTitle() {
		return "dialog.title.export.java";
	}
}