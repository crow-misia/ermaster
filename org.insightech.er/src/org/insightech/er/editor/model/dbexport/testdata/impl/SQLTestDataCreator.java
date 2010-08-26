package org.insightech.er.editor.model.dbexport.testdata.impl;

import java.util.Map;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.testdata.TestDataCreator;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.testdata.RepeatTestData;
import org.insightech.er.editor.model.testdata.RepeatTestDataDef;

public class SQLTestDataCreator extends TestDataCreator {

	@Override
	protected String getDirectTestData(ERTable table,
			Map<NormalColumn, String> data, String database) {
		StringBuilder sb = new StringBuilder();

		sb.append("INSERT INTO ");
		sb.append(table.getNameWithSchema(database));
		sb.append(" (");

		StringBuilder valueSb = new StringBuilder();

		boolean first = true;
		for (NormalColumn column : table.getExpandedColumns()) {
			if (!first) {
				sb.append(", ");
				valueSb.append(", ");
			}

			sb.append(column.getPhysicalName());
			valueSb.append("'");
			valueSb.append(data.get(column));
			valueSb.append("'");

			first = false;
		}

		sb.append(") VALUES (");
		sb.append(valueSb.toString());

		sb.append(");\r\n");

		return sb.toString();
	}

	@Override
	protected String getRepeatTestData(ERTable table,
			RepeatTestData repeatTestData, String database) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < repeatTestData.getTestDataNum(); i++) {
			sb.append("INSERT INTO ");
			sb.append(table.getNameWithSchema(database));
			sb.append(" (");

			StringBuilder valueSb = new StringBuilder();

			boolean first = true;
			for (NormalColumn column : table.getExpandedColumns()) {
				if (!first) {
					sb.append(", ");
					valueSb.append(", ");
				}

				sb.append(column.getPhysicalName());

				RepeatTestDataDef repeatTestDataDef = repeatTestData
						.getDataDef(column);

				String value = this.getRepeatTestDataValue(i,
						repeatTestDataDef, column);

				if (value != null) {
					valueSb.append("'");
					valueSb.append(value);
					valueSb.append("'");
				} else {
					valueSb.append("null");
				}

				first = false;
			}

			sb.append(") VALUES (");
			sb.append(valueSb.toString());

			sb.append(");\r\n");
		}

		return sb.toString();
	}

	@Override
	protected String getFooter() {
		return "";
	}

	@Override
	protected String getHeader() {
		return "";
	}

	@Override
	protected String getTableHeader(ERDiagram diagram, ERTable table) {
		StringBuilder sb = new StringBuilder();

		sb.append("-- ");
		sb.append(table.getLogicalName());
		sb.append("\r\n");

		return sb.toString();
	}

	@Override
	protected String getTableFooter(ERTable table) {
		StringBuilder sb = new StringBuilder();

		sb.append("\r\n");
		sb.append("\r\n");

		return sb.toString();
	}

}
