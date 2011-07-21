package org.insightech.er.db.impl.db2;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.insightech.er.editor.model.dbimport.ImportFromDBManagerBase;

public class DB2TableImportManager extends ImportFromDBManagerBase {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getViewDefinitionSQL(String schema) {
		return "SELECT TEXT FROM SYSCAT.VIEWS WHERE VIEWSCHEMA = ? AND VIEWNAME = ?";
	}

	@Override
	protected ColumnData createColumnData(ResultSet columnSet)
			throws SQLException {
		ColumnData columnData = super.createColumnData(columnSet);
		String type = columnData.type.toLowerCase();

		if (type.equals("decimal")) {
			if (columnData.size == 5 && columnData.decimalDegits == 0) {
				columnData.size = 0;
			}

		} else if (type.equals("clob")) {
			if (columnData.size == 1048576) {
				columnData.size = 0;
			}

		} else if (type.equals("blob")) {
			if (columnData.size == 1048576) {
				columnData.size = 0;
			}

		} else if (type.equals("dbclob")) {
			if (columnData.size == 2097152) {
				columnData.size = 0;
			}
			
		} else if (type.equals("decfloat")) {
			if (columnData.size == 34) {
				columnData.size = 0;
			}
			
		}

		return columnData;
	}
}
