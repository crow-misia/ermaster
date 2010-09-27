package org.insightech.er.ant_task;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.export.ExportTestDataSetting;
import org.insightech.er.editor.model.testdata.TestData;
import org.insightech.er.editor.view.dialog.dbexport.ExportToTestDataDialog;

public class TestDataAntTask extends ERMasterAntTaskBase {

	private String outputDir;

	private String encoding;

	private String format;

	private List<TestDataElement> testDataList;

	@Override
	public void init() throws BuildException {
		super.init();

		this.testDataList = new ArrayList<TestDataElement>();
	}

	public void addTestData(TestDataElement testData) {
		this.testDataList.add(testData);
	}

	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doTask(ERDiagram diagram) throws Exception {
		ExportTestDataSetting setting = new ExportTestDataSetting();

		if (this.outputDir == null || this.outputDir.trim().equals("")) {
			throw new BuildException("outputDir attribute must be set!");

		} else {
			this.outputDir = this.getAbsolutePath(this.outputDir);
			setting.setExportFilePath(this.outputDir);
			this.log("Output Dir : " + this.outputDir);
		}

		int formatNo = -1;
		try {
			formatNo = Integer.parseInt(this.format);

		} catch (Exception e) {
		}

		if (formatNo == 0) {
			this.log("Format : SQL");

		} else if (formatNo == 1) {
			this.log("Format : DBUnit XML");

		} else if (formatNo == 2) {
			this.log("Format : DBUnit Flat XML");

		} else if (formatNo == 3) {
			this.log("Format : DBUnit Excel");

		} else {
			throw new BuildException(
					"format attribute must be 0(SQL) or 1(DBUnit XML) or 2(DBUnit Flat XML) or 3(DBUnit Excel)!");
		}
		if (this.testDataList.isEmpty()) {
			throw new BuildException(
					"At least one <testdata> element must be specified!");
		}

		setting.setExportFormat(formatNo);

		if (this.encoding == null) {
			this.encoding = Charset.defaultCharset().name();
		}

		this.log("Encoding : " + this.encoding);

		setting.setExportFileEncoding(this.encoding);

		for (TestDataElement testDataElement : this.testDataList) {
			boolean exist = false;

			for (TestData testData : diagram.getDiagramContents()
					.getTestDataList()) {
				if (testDataElement.getName().equals(testData.getName())) {
					ExportToTestDataDialog.exportTestData(diagram, setting,
							testData);
					exist = true;
					this.log("Test Data (" + testDataElement.getName()
							+ ") was output.");

					break;
				}
			}

			if (!exist) {
				this.log("Test Data (" + testDataElement.getName()
						+ ") was not found.");

			}
		}

	}

	@Override
	protected void logUsage() {
		this
				.log("<ermaster.testdata> have these attributes. (the attribute with '*' must be set.) ");
		this.log("    * diagramFile - The path of the input .erm file.");
		this.log("    * outputDir   - The path of the output directory.");
		this
				.log("    * format      - 0(SQL) or 1(DBUnit XML) or 2(DBUnit Flat XML) or 3(DBUnit Excel).");
		this.log("      encoding    - The encoding of the output file.");
		this
				.log("<ermaster.testdata> have these sub elements. (the element with '*' must be set.) ");
		this
				.log("    * <testdata>  - The element which specifies the testdata being output.");
		this
				.log("<testdata> have these attributes. (the attribute with '*' must be set.) ");
		this.log("    * name        - The name of the testdata.");

	}

	public static class TestDataElement {

		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}
}
