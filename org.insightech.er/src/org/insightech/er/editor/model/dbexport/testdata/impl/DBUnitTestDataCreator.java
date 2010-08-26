package org.insightech.er.editor.model.dbexport.testdata.impl;

import java.util.Map;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.testdata.TestDataCreator;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.testdata.RepeatTestData;
import org.insightech.er.editor.model.testdata.RepeatTestDataDef;

public class DBUnitTestDataCreator extends TestDataCreator {

	private String encoding;

	public DBUnitTestDataCreator(String encoding) {
		this.encoding = encoding;
	}

	@Override
	protected String getDirectTestData(ERTable table,
			Map<NormalColumn, String> data, String database) {
		StringBuilder sb = new StringBuilder();

		sb.append("\t\t<row>\r\n");

		for (NormalColumn column : table.getExpandedColumns()) {
			sb.append("\t\t\t<value>");
			sb.append(data.get(column));
			sb.append("</value>\r\n");
		}

		sb.append("\t\t</row>\r\n");

		return sb.toString();
	}

	@Override
	protected String getRepeatTestData(ERTable table,
			RepeatTestData repeatTestData, String database) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < repeatTestData.getTestDataNum(); i++) {
			sb.append("\t\t<row>\r\n");

			for (NormalColumn column : table.getExpandedColumns()) {

				RepeatTestDataDef repeatTestDataDef = repeatTestData
						.getDataDef(column);

				String value = this.getRepeatTestDataValue(i,
						repeatTestDataDef, column);

				sb.append("\t\t\t<value>");
				sb.append(value);
				sb.append("</value>\r\n");
			}

			sb.append("\t\t</row>\r\n");
		}

		return sb.toString();
	}

	@Override
	protected String getHeader() {
		StringBuilder sb = new StringBuilder();

		sb.append("<?xml version=\"1.0\" encoding=\"");
		sb.append(this.encoding);
		sb.append("\" ?>\r\n<dataset>\r\n");

		return sb.toString();
	}

	@Override
	protected String getFooter() {
		return "</dataset>";
	}

	@Override
	protected String getTableHeader(ERDiagram diagram, ERTable table) {
		StringBuilder sb = new StringBuilder();

		sb.append("\t<table name=\"");
		sb.append(table.getNameWithSchema(diagram.getDatabase()));
		sb.append("\">\r\n");

		for (NormalColumn column : table.getExpandedColumns()) {
			sb.append("\t\t<column>");
			sb.append(column.getPhysicalName());
			sb.append("</column>\r\n");
		}

		return sb.toString();
	}

	@Override
	protected String getTableFooter(ERTable table) {
		return "\t</table>\r\n";
	}

}
