package org.insightech.er.db.impl.h2;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.insightech.er.editor.model.dbimport.ImportFromDBManagerBase;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;

public class H2TableImportManager extends ImportFromDBManagerBase {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getViewDefinitionSQL(String schema) {
		return "SELECT VIEW_DEFINITION FROM INFORMATION_SCHEMA.VIEWS WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? ";
	}

	@Override
	protected ColumnData createColumnData(ResultSet columnSet)
			throws SQLException {
		ColumnData columnData = super.createColumnData(columnSet);
		String type = columnData.type.toLowerCase();

		if (type.startsWith("decimal")) {
			if (columnData.size == 128 && columnData.decimalDegits == 0) {
				columnData.size = 0;

			} else if (columnData.size == 646456993
					&& columnData.decimalDegits == 0) {
				columnData.size = 0;
			}

		} else if (type.startsWith("numeric")) {
			if (columnData.size == 128 && columnData.decimalDegits == 0) {
				columnData.size = 0;

			} else if (columnData.size == 646456993
					&& columnData.decimalDegits == 0) {
				columnData.size = 0;
			}

		} else if (type.startsWith("float")) {
			if (columnData.size == 17) {
				columnData.size = 0;

			} else if (columnData.size == 646456993
					&& columnData.decimalDegits == 0) {
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

	@Override
	protected Sequence importSequence(String schema, String sequenceName)
			throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = con
					.prepareStatement("SELECT * FROM INFORMATION_SCHEMA.SEQUENCES WHERE SEQUENCE_SCHEMA = ? AND SEQUENCE_NAME = ?");
			stmt.setString(1, schema);
			stmt.setString(2, sequenceName);

			rs = stmt.executeQuery();

			if (rs.next()) {
				Sequence sequence = new Sequence();

				sequence.setName(sequenceName);
				sequence.setSchema(schema);
				sequence.setIncrement(rs.getInt("INCREMENT"));
				sequence.setCache(rs.getInt("CACHE"));

				return sequence;
			}

			return null;

		} finally {
			this.close(rs);
			this.close(stmt);
		}
	}
}
