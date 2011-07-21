package org.insightech.er.db.impl.hsqldb;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.insightech.er.editor.model.dbimport.ImportFromDBManagerBase;

public class HSQLDBTableImportManager extends ImportFromDBManagerBase {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getViewDefinitionSQL(String schema) {
		return null;
	}

	@Override
	protected ColumnData createColumnData(ResultSet columnSet)
			throws SQLException {
		ColumnData columnData = super.createColumnData(columnSet);
		String type = columnData.type.toLowerCase();

		if (type.startsWith("decimal")) {
			if (columnData.size == 128 && columnData.decimalDegits == 0) {
				columnData.size = 0;
			
			} else if (columnData.size == 646456993 && columnData.decimalDegits == 0) {
				columnData.size = 0;
			}

		} else if (type.startsWith("numeric")) {
			if (columnData.size == 128 && columnData.decimalDegits == 0) {
				columnData.size = 0;

			} else if (columnData.size == 646456993 && columnData.decimalDegits == 0) {
				columnData.size = 0;
			}

		} else if (type.startsWith("float")) {
			if (columnData.size == 17) {
				columnData.size = 0;

			} else if (columnData.size == 646456993 && columnData.decimalDegits == 0) {
				columnData.size = 0;
			}

		} else if (type.startsWith("clob")) {
			if (columnData.size == 16777216) {
				columnData.size = 0;
			}

		} else if (type.startsWith("blob")) {
			if (columnData.size == 16777216) {
				columnData.size = 0;
			}

		} else if (type.startsWith("varchar")) {
			if (columnData.size == 16777216) {
				columnData.size = 0;
				columnData.type = "longvarchar";
			}

		} else if (type.startsWith("varbinary")) {
			if (columnData.size == 16777216) {
				columnData.size = 0;
				columnData.type = "longvarbinary";
			}

		} else if (type.startsWith("timestamp")) {
			columnData.size = columnData.size - 20;

			if (columnData.size == 6) {
				columnData.size = 0;
			}

		}

		return columnData;
	}
}
