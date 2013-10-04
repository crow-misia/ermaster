package org.insightech.er.db.impl.mysql;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.dbimport.DBObject;
import org.insightech.er.editor.model.dbimport.ImportFromDBManagerBase;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableViewProperties;
import org.insightech.er.util.Format;

public class MySQLTableImportManager extends ImportFromDBManagerBase {

	private Map<String, MySQLTableProperties> tablePropertiesMap;

	public MySQLTableImportManager() {
		this.tablePropertiesMap = new HashMap<String, MySQLTableProperties>();
	}

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
	protected void cashOtherColumnData(String tableName, String schema,
			ColumnData columnData) throws SQLException {
		String tableNameWithSchema = this.dbSetting.getTableNameWithSchema(
				tableName, schema);

		SqlType sqlType = SqlType.valueOfId(columnData.type);

		if (sqlType != null && sqlType.doesNeedArgs()) {
			String restrictType = this.getRestrictType(tableNameWithSchema,
					columnData);

			Pattern p = Pattern.compile(columnData.type.toLowerCase()
					+ "\\((.*)\\)");
			Matcher m = p.matcher(restrictType);

			if (m.matches()) {
				columnData.enumData = m.group(1);
			}

		} else if (columnData.type.equals("year")) {
			String restrictType = this.getRestrictType(tableNameWithSchema,
					columnData);
			columnData.type = restrictType;
		}
	}

	private String getRestrictType(String tableNameWithSchema,
			ColumnData columnData) throws SQLException {
		String type = null;

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement("SHOW COLUMNS FROM `"
					+ tableNameWithSchema + "` LIKE ?");

			ps.setString(1, columnData.columnName);
			rs = ps.executeQuery();

			if (rs.next()) {
				type = rs.getString("Type");
			}

		} finally {
			if (rs != null) {
				rs.close();
			}
			if (ps != null) {
				ps.close();
			}
		}

		return type;
	}

	@Override
	protected ColumnData createColumnData(ResultSet columnSet)
			throws SQLException {
		ColumnData columnData = super.createColumnData(columnSet);
		String type = columnData.type.toLowerCase();

		if (type.startsWith("decimal")) {
			if (columnData.size == 10 && columnData.decimalDegits == 0) {
				columnData.size = 0;
			}

		} else if (type.startsWith("double")) {
			if (columnData.size == 22 && columnData.decimalDegits == 0) {
				columnData.size = 0;
			}

		} else if (type.startsWith("float")) {
			if (columnData.size == 12 && columnData.decimalDegits == 0) {
				columnData.size = 0;
			}

		}

		return columnData;
	}

	@Override
	protected void cashColumnData(List<DBObject> dbObjectList,
			IProgressMonitor monitor) throws SQLException, InterruptedException {
		super.cashColumnData(dbObjectList, monitor);

		PreparedStatement ps = null;
		ResultSet rs = null;

		String sql = "SELECT T.TABLE_NAME, T.TABLE_COLLATION, C.COLUMN_NAME, "
				+ "C.CHARACTER_SET_NAME, C.COLLATION_NAME, C.COLUMN_TYPE "
				+ "FROM INFORMATION_SCHEMA.TABLES T, INFORMATION_SCHEMA.COLUMNS C "
				+ "WHERE T.TABLE_SCHEMA = ? AND T.TABLE_NAME = C.TABLE_NAME";

		try {
			ps = con.prepareStatement(sql);

			ps.setString(1, this.dbSetting.getDatabase());
			rs = ps.executeQuery();

			while (rs.next()) {
				String tableName = rs.getString("TABLE_NAME");
				String columnName = rs.getString("COLUMN_NAME");
				String tableCollation = Format.null2blank(rs
						.getString("TABLE_COLLATION"));
				String tableCharacterSet = "";
				String characterSet = Format.null2blank(rs
						.getString("CHARACTER_SET_NAME"));
				String collation = Format.null2blank(rs
						.getString("COLLATION_NAME"));

				// String type = Format.null2blank(rs.getString("COLUMN_TYPE"));

				if (!tableCollation.isEmpty()) {
					int index = tableCollation.indexOf("_");
					if (index != -1) {
						tableCharacterSet = tableCollation.substring(0, index);
					}
				}

				MySQLTableProperties tableProperties = this.tablePropertiesMap
						.get(tableName);
				if (tableProperties == null) {
					tableProperties = new MySQLTableProperties();

					tableProperties.setCharacterSet(tableCharacterSet);
					tableProperties.setCollation(tableCollation);

					this.tablePropertiesMap.put(tableName, tableProperties);
				}

				Map<String, ColumnData> columnDataMap = this.columnDataCash
						.get(tableName);
				ColumnData columnData = columnDataMap.get(columnName);

				if (!tableCharacterSet.equals(characterSet)) {
					columnData.characterSet = characterSet;
				}

				if (!tableCollation.equals(collation)) {
					columnData.collation = collation;
				}
				
				if (collation.endsWith("_bin")) {
					columnData.isBinary = true;
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

	@Override
	protected void setTableViewProperties(String tableName,
			TableViewProperties tableViewProperties) {
		super.setTableViewProperties(tableName, tableViewProperties);

		MySQLTableProperties tableProperties = (MySQLTableProperties) tableViewProperties;

		MySQLTableProperties cachedTableProperties = this.tablePropertiesMap
				.get(tableName);

		if (cachedTableProperties != null) {
			tableProperties.setCharacterSet(cachedTableProperties
					.getCharacterSet());
			tableProperties.setCollation(cachedTableProperties.getCollation());
		}
	}

}
