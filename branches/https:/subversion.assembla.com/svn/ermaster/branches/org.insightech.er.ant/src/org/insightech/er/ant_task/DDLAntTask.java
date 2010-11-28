package org.insightech.er.ant_task;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import org.apache.tools.ant.BuildException;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.dbexport.ddl.DDLTarget;
import org.insightech.er.editor.model.settings.Environment;

public class DDLAntTask extends ERMasterAntTaskBase {

	private String outputFile;

	private String encoding;

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
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

		PrintWriter out = null;

		try {
			DDLCreator ddlCreator = DBManagerFactory.getDBManager(diagram)
					.getDDLCreator(diagram, true);

			// TODO
			Environment environment = diagram.getDiagramContents()
					.getSettings().getEnvironmentSetting().getEnvironments()
					.get(0);

			DDLTarget ddlTarget = diagram.getDiagramContents().getSettings()
					.getExportSetting().getDdlTarget();

			ddlCreator.init(environment, ddlTarget);

			if (this.encoding == null) {
				this.encoding = Charset.defaultCharset().name();
			}

			this.log("Encoding : " + this.encoding);

			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(this.outputFile), this.encoding)));

			out.println(ddlCreator.getDropDDL(diagram));
			out.println(ddlCreator.getCreateDDL(diagram));

		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	@Override
	protected void logUsage() {
		this
				.log("<ermaster.ddl> have these attributes. (the attribute with '*' must be set.) ");
		this.log("    * diagramFile - The path of the input .erm file.");
		this.log("    * outputFile  - The path of the output ddl file.");
		this.log("      encoding    - The encoding of the output ddl file.");
	}

}
