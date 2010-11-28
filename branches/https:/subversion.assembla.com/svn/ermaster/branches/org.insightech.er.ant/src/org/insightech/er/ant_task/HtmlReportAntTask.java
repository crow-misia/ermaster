package org.insightech.er.ant_task;

import java.io.File;
import java.util.Map;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.swt.graphics.Image;
import org.insightech.er.Activator;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.html.ExportToHtmlManager;
import org.insightech.er.editor.model.dbexport.image.ExportToImageManager;
import org.insightech.er.editor.model.diagram_contents.element.node.Location;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.view.action.dbexport.ExportToHtmlAction;
import org.insightech.er.editor.view.action.dbexport.ExportToImageAction;
import org.insightech.er.util.io.FileUtils;

public class HtmlReportAntTask extends ERMasterAntTaskBase {

	private String outputDir;

	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doTask(ERDiagram diagram) throws Exception {
		this.outputDir = getAbsolutePath(this.outputDir);
		this.outputDir = this.outputDir + "/dbdocs/";

		this.log("Output to : " + this.outputDir);

		// 出力ディレクトリの削除
		File dir = new File(outputDir);
		FileUtils.deleteDirectory(dir);

		dir = new File(outputDir + "image");
		dir.mkdirs();

		String outputImageFilePath = this.outputDir + "image/er.png";

		this.log("Output image to : " + outputImageFilePath);

		Image img = null;
		GraphicalViewer viewer = null;

		try {
			viewer = Activator.createGraphicalViewer(diagram);

			int format = ExportToImageAction.getFormatType(outputImageFilePath);

			img = Activator.createImage(viewer);

			ExportToImageManager exportToImageManager = new ExportToImageManager(
					img, format, outputImageFilePath);
			exportToImageManager.doProcess();

			Map<TableView, Location> tableLocationMap = ExportToHtmlAction
					.getTableLocationMap(viewer, diagram);

			ExportToHtmlManager reportManager = new ExportToHtmlManager(
					this.outputDir, diagram, tableLocationMap);

			this.log("Output html beginning...");

			reportManager.doProcess();

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
				.log("<ermaster.htmlReport> have these attributes. (the attribute with '*' must be set.) ");
		this.log("    * diagramFile - The path of the input .erm file.");
		this.log("      outputDir   - The path of the output directory.");
		this
				.log("                    The directory named 'dbdocs' is made under this directory.");
		this
				.log("                    When not specified, the project base directory is used.");
	}
}
