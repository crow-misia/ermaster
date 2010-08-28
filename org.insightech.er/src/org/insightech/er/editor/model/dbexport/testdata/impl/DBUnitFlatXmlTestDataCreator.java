package org.insightech.er.editor.model.dbexport.testdata.impl;

import java.util.Map;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.testdata.TestDataCreator;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.testdata.RepeatTestData;
import org.insightech.er.editor.model.testdata.RepeatTestDataDef;
import org.insightech.er.editor.persistent.impl.PersistentXmlImpl;

public class DBUnitFlatXmlTestDataCreator extends TestDataCreator {

	private String encoding;

	public DBUnitFlatXmlTestDataCreator(String encoding) {
		this.encoding = encoding;
	}

	@Override
	protected String getDirectTestData(ERTable table,
			Map<NormalColumn, String> data, String database) {
		StringBuilder sb = new StringBuilder();

		sb.append("\t<");
		sb.append(table.getNameWithSchema(database));

		for (NormalColumn column : table.getExpandedColumns()) {
			sb.append(" ");
			sb.append(column.getPhysicalName());
			sb.append("=\"");
			sb.append(PersistentXmlImpl.escape(data.get(column)));
			sb.append("\"");
		}

		sb.append(">\r\n");

		return sb.toString();
	}

	@Override
	protected String getRepeatTestData(ERTable table,
			RepeatTestData repeatTestData, String database) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < repeatTestData.getTestDataNum(); i++) {
			sb.append("\t<");
			sb.append(table.getNameWithSchema(database));

			for (NormalColumn column : table.getExpandedColumns()) {
				sb.append(" ");
				sb.append(column.getPhysicalName());
				sb.append("=\"");

				RepeatTestDataDef repeatTestDataDef = repeatTestData
						.getDataDef(column);

				String value = this.getRepeatTestDataValue(i,
						repeatTestDataDef, column);

				sb.append(PersistentXmlImpl.escape(value));
				sb.append("\"");
			}

			sb.append(">\r\n");
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
		return "";
	}

	@Override
	protected String getTableFooter(ERTable table) {
		return "";
	}

}
