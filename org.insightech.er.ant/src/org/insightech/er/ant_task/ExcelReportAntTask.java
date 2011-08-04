package org.insightech.er.ant_task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.tools.ant.BuildException;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.insightech.er.Activator;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.excel.ExportToExcelManager;
import org.insightech.er.editor.model.dbexport.image.ExportToImageManager;
import org.insightech.er.editor.view.action.dbexport.ExportToImageAction;
import org.insightech.er.preference.PreferenceInitializer;
import org.insightech.er.preference.template.TemplatePreferencePage;
import org.insightech.er.util.io.FileUtils;

public class ExcelReportAntTask extends ERMasterAntTaskBase {

	private String outputFile;

	private String outputImageFile;

	private String template;

	private boolean useLogicalNameAsSheetName;

	public void setUseLogicalNameAsSheetName(boolean useLogicalNameAsSheetName) {
		this.useLogicalNameAsSheetName = useLogicalNameAsSheetName;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public void setOutputImageFile(String outputImageFile) {
		this.outputImageFile = outputImageFile;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doTask(ERDiagram diagram) throws Exception {
		if (this.outputFile == null || this.outputFile.trim().equals("")) {
			throw new BuildException("outputFile attribute must be set!");
		}

		this.outputFile = this.getAbsolutePath(this.outputFile);

		this.log("Output to : " + this.outputFile);

		boolean outputImage = false;
		if (this.outputImageFile != null) {
			outputImage = true;
			this.outputImageFile = this.getAbsolutePath(this.outputImageFile);

			this.log("Use image : on");
			this.log("Output image to : " + this.outputImageFile);

		} else {
			this.log("Use image : off");
		}

		InputStream stream = null;

		try {
			byte[] imageBuffer = null;
			int excelPictureType = -1;

			if (outputImage) {
				Image img = null;
				GraphicalViewer viewer = null;

				try {
					viewer = Activator.createGraphicalViewer(diagram);

					int format = ExportToImageAction
							.getFormatType(outputImageFile);

					if (format == SWT.IMAGE_JPEG) {
						excelPictureType = HSSFWorkbook.PICTURE_TYPE_JPEG;

					} else if (format == SWT.IMAGE_PNG) {
						excelPictureType = HSSFWorkbook.PICTURE_TYPE_PNG;

					} else {
						throw new BuildException(
								ResourceString
										.getResourceString("dialog.message.export.image.not.supported.for.excel")
										+ " : " + outputImageFile);
					}

					img = Activator.createImage(viewer);

					ExportToImageManager exportToImageManager = new ExportToImageManager(
							img, format, outputImageFile);
					exportToImageManager.doProcess();

					imageBuffer = FileUtils.readFileToByteArray(new File(
							outputImageFile));

				} finally {
					if (viewer != null) {
						viewer.getContents().deactivate();
					}
					if (img != null) {
						img.dispose();
					}
				}
			}

			stream = this.getTemplate();

			AntConsoleProgressMonitor monitor = new AntConsoleProgressMonitor(
					this);

			this.log("Output excel beginning...");

			ExportToExcelManager manager = new ExportToExcelManager(outputFile,
					diagram, stream, this.useLogicalNameAsSheetName,
					imageBuffer, excelPictureType);
			manager.run(monitor);

			if (manager.getException() != null) {
				throw manager.getException();
			}

		} finally {
			if (stream != null) {
				stream.close();
			}
		}
	}

	private InputStream getTemplate() throws FileNotFoundException {
		if ("default_en".equals(template)) {
			return TemplatePreferencePage.getDefaultExcelTemplateEn();

		} else if ("default_ja".equals(template)) {
			return TemplatePreferencePage.getDefaultExcelTemplateJa();

		}

		File file = new File(PreferenceInitializer.getTemplatePath(template));

		return new FileInputStream(file);
	}

	@Override
	protected void logUsage() {
		this
				.log("<ermaster.excelReport> have these attributes. (the attribute with '*' must be set.) ");
		this.log("    * diagramFile     - The path of the input .erm file.");
		this.log("    * outputFile      - The path of the output excel file.");
		this
				.log("    * template        - The template of the output excel file.");
		this
				.log("                      - The available values are \"default_en\", \"default_ja\", or the file names of custom templates.");
		this
				.log("      outputImageFile - The path of the output image file. The png/jpg/jpeg format are supported.");
		this
				.log("                        When not specified, the image is not used in excel.");
		this
				.log("      useLogicalNameAsSheetName - Boolean. Whether the logical name is used for the seat name or not.");
	}
}
