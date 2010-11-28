package org.insightech.er.db.impl.mysql;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.dbimport.ImportFromDBManagerBase;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;

public class MySQLTableImportManager extends ImportFromDBManagerBase {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getViewDefinitionSQL(String schema) {
		if (schema != null) {
			return "SELECT view_definition FROM information_schema.views WHERE table_schema = ? AND table_name = ?";

		} else {
			return "SELECT view_definition FROM information_schema.views WHERE table_name = ?";

		}
	}

	@Override
	protected List<Index> getIndexes(ERTable table, DatabaseMetaData metaData,
			List<PrimaryKeyData> primaryKeys) throws SQLException {

		List<Index> indexes = super.getIndexes(table, metaData, primaryKeys);

		for (Iterator<Index> iter = indexes.iterator(); iter.hasNext();) {
			Index index = iter.next();

			if ("PRIMARY".equalsIgnoreCase(index.getName())) {
				iter.remove();
			}
		}

		return indexes;
	}

	@Override
	protected String getConstraintName(PrimaryKeyData data) {
		return null;
	}

	@Override
	protected void cashOtherColumnData(String tableName, ColumnData columnData)
			throws SQLException {
		if ((SqlType.ENUM.getId().equalsIgnoreCase(columnData.type) || SqlType.SET
				.getId().equalsIgnoreCase(columnData.type))) {

			String type = columnData.type.toLowerCase();
			PreparedStatement ps = null;
			ResultSet rs = null;

			try {
				ps = con.prepareStatement("SHOW COLUMNS FROM `" + tableName
						+ "` LIKE ?");

				ps.setString(1, columnData.columnName);
				rs = ps.executeQuery();

				if (rs.next()) {
					Pattern p = Pattern.compile(type + "\\((.*)\\)");
					Matcher m = p.matcher(rs.getString("Type"));
					if (m.matches()) {
						columnData.enumData = m.group(1);
					}
				}
			} finally {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
			}
		}
	}
}
