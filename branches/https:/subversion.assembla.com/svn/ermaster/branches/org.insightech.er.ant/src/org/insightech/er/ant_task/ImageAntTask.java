package org.insightech.er.ant_task;

import org.apache.tools.ant.BuildException;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.swt.graphics.Image;
import org.insightech.er.Activator;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.image.ExportToImageManager;
import org.insightech.er.editor.view.action.dbexport.ExportToImageAction;

public class ImageAntTask extends ERMasterAntTaskBase {

	private String outputFile;

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doTask(ERDiagram diagram) throws Exception {
		if (this.outputFile == null || this.outputFile.trim().equals("")) {
			throw new BuildException("outputFile attribute must be set!");
		}

		this.outputFile = this.getAbsolutePath(this.outputFile);

		this.log("Output to : " + this.outputFile);

		Image img = null;
		GraphicalViewer viewer = null;

		try {
			viewer = Activator.createGraphicalViewer(diagram);

			int format = ExportToImageAction.getFormatType(outputFile);

			if (format == -1) {
				throw new BuildException(
						ResourceString
								.getResourceString("dialog.message.export.image.not.supported")
								+ " : " + outputFile);
			}

			img = Activator.createImage(viewer);

			ExportToImageManager exportToImageManager = new ExportToImageManager(
					img, format, outputFile);
			exportToImageManager.doProcess();

		} finally {
			if (viewer != null) {
				viewer.getContents().deactivate();
			}
			if (img != null) {
				img.dispose();
			}
		}
	}

	@Override
	protected void logUsage() {
		this
				.log("<ermaster.image> have these attributes. (the attribute with '*' must be set.) ");
		this.log("    * diagramFile - The path of the input .erm file.");
		this
				.log("    * outputFile  - The path of the output image file. The png/jpg/jpeg/bmp format are supported.");
	}
}
