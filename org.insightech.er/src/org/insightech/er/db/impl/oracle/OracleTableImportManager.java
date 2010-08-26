package org.insightech.er.db.impl.oracle;

import java.math.BigDecimal;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.insightech.er.editor.model.dbimport.DBObject;
import org.insightech.er.editor.model.dbimport.ImportFromDBManagerBase;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.Trigger;

public class OracleTableImportManager extends ImportFromDBManagerBase {

	private static Logger logger = Logger
			.getLogger(OracleTableImportManager.class.getName());

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void cashColumnData(List<DBObject> dbObjectList,
			IProgressMonitor monitor) throws SQLException, InterruptedException {
		super.cashColumnData(dbObjectList, monitor);

		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = this.con
					.prepareStatement("SELECT OWNER, TABLE_NAME, COLUMN_NAME, COMMENTS FROM SYS.ALL_COL_COMMENTS WHERE COMMENTS IS NOT NULL");
			rs = stmt.executeQuery();

			while (rs.next()) {
				String tableName = rs.getString("TABLE_NAME");
				String schema = rs.getString("OWNER");

				String columnName = rs.getString("COLUMN_NAME");
				String comments = rs.getString("COMMENTS");

				tableName = this.dbSetting.getTableNameWithSchema(tableName,
						schema);

				Map<String, ColumnData> cash = this.columnDataCash
						.get(tableName);
				if (cash != null) {
					ColumnData columnData = cash.get(columnName);
					if (columnData != null) {
						columnData.description = comments;
					}
				}
			}

		} finally {
			this.close(rs);
			this.close(stmt);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void cashTableComment(IProgressMonitor monitor)
			throws SQLException, InterruptedException {
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = this.con
					.prepareStatement("SELECT OWNER, TABLE_NAME, COMMENTS FROM SYS.ALL_TAB_COMMENTS WHERE COMMENTS IS NOT NULL");
			rs = stmt.executeQuery();

			while (rs.next()) {
				String tableName = rs.getString("TABLE_NAME");

				String schema = rs.getString("OWNER");
				String comments = rs.getString("COMMENTS");

				tableName = this.dbSetting.getTableNameWithSchema(tableName,
						schema);

				this.tableCommentMap.put(tableName, comments);
			}

		} finally {
			this.close(rs);
			this.close(stmt);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected View importView(String schema, String viewName)
			throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			if (schema != null) {
				stmt = this.con
						.prepareStatement("SELECT TEXT FROM ALL_VIEWS WHERE OWNER = ? AND VIEW_NAME = ?");
				stmt.setString(1, schema);
				stmt.setString(2, viewName);

			} else {
				stmt = this.con
						.prepareStatement("SELECT TEXT FROM ALL_VIEWS WHERE VIEW_NAME = ?");
				stmt.setString(1, viewName);

			}

			rs = stmt.executeQuery();

			if (rs.next()) {
				View view = new View();

				view.setPhysicalName(viewName);
				view.setLogicalName(this.translationResources
						.translate(viewName));
				view.setSql(rs.getString("TEXT"));
				view.getTableViewProperties().setSchema(schema);

				return view;
			}

			return null;

		} finally {
			this.close(rs);
			this.close(stmt);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Sequence importSequence(String schema, String sequenceName)
			throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			if (schema != null) {
				stmt = this.con
						.prepareStatement("SELECT * FROM SYS.ALL_SEQUENCES WHERE SEQUENCE_OWNER = ? AND SEQUENCE_NAME = ?");
				stmt.setString(1, schema);
				stmt.setString(2, sequenceName);

			} else {
				stmt = this.con
						.prepareStatement("SELECT * FROM SYS.ALL_SEQUENCES WHERE SEQUENCE_NAME = ?");
				stmt.setString(1, sequenceName);

			}

			rs = stmt.executeQuery();

			if (rs.next()) {
				Sequence sequence = new Sequence();

				sequence.setName(sequenceName);
				sequence.setSchema(schema);
				sequence.setIncrement(rs.getInt("INCREMENT_BY"));
				BigDecimal minValue = rs.getBigDecimal("MIN_VALUE");
				sequence.setMinValue(minValue.longValue());
				BigDecimal maxValue = rs.getBigDecimal("MAX_VALUE");
				sequence.setMaxValue(maxValue.longValue());
				BigDecimal lastNumber = rs.getBigDecimal("LAST_NUMBER");
				sequence.setStart(lastNumber.longValue());
				sequence.setCache(rs.getInt("CACHE_SIZE"));

				String cycle = rs.getString("CYCLE_FLAG").toLowerCase();
				if ("y".equals(cycle)) {
					sequence.setCycle(true);
				} else {
					sequence.setCycle(false);
				}

				return sequence;
			}

			return null;

		} finally {
			this.close(rs);
			this.close(stmt);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Trigger importTrigger(String schema, String name)
			throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			if (schema != null) {
				stmt = this.con
						.prepareStatement("SELECT * FROM SYS.ALL_TRIGGERS WHERE OWNER = ? AND TRIGGER_NAME = ?");
				stmt.setString(1, schema);
				stmt.setString(2, name);

			} else {
				stmt = this.con
						.prepareStatement("SELECT * FROM SYS.ALL_TRIGGERS WHERE TRIGGER_NAME = ?");
				stmt.setString(1, name);

			}

			rs = stmt.executeQuery();

			if (rs.next()) {
				Trigger trigger = new Trigger();

				trigger.setName(name);
				trigger.setSchema(schema);
				trigger.setDescription(rs.getString("DESCRIPTION"));
				trigger.setSql(rs.getString("TRIGGER_BODY"));

				return trigger;
			}

			return null;

		} finally {
			this.close(rs);
			this.close(stmt);
		}
	}

	public static boolean isValidObjectName(String s) {
		return s.matches("([a-zA-Z]{1}\\w*(\\$|\\#)*\\w*)|(\".*)");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Index> getIndexes(ERTable table, DatabaseMetaData metaData,
			List<PrimaryKeyData> primaryKeys) throws SQLException {
		if (!isValidObjectName(table.getPhysicalName())) {
			logger
					.info("is not valid object name : "
							+ table.getPhysicalName());
			return new ArrayList<Index>();
		}

		try {
			return super.getIndexes(table, metaData, primaryKeys);

		} catch (SQLException e) {
			if (e.getErrorCode() == 38029) {
				logger.info(table.getPhysicalName() + " : " + e.getMessage());
				return new ArrayList<Index>();
			}

			throw e;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int getLength(String type, int size) {
		int startIndex = type.indexOf("(");

		if (startIndex > 0) {
			int endIndex = type.indexOf(")", startIndex + 1);
			if (endIndex != -1) {
				String str = type.substring(startIndex + 1, endIndex);
				return Integer.parseInt(str);
			}
		}

		return size;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<ERTable> importSynonyms() throws SQLException,
			InterruptedException {
		List<ERTable> list = new ArrayList<ERTable>();

		// if (this.isOnlyUserTable()) {
		// PreparedStatement stmt = null;
		// ResultSet rs = null;
		//
		// try {
		// String sql =
		// "SELECT SYNONYM_NAME, TABLE_OWNER, TABLE_NAME FROM USER_SYNONYMS";
		//
		// stmt = this.con.prepareStatement(sql);
		// rs = stmt.executeQuery();
		//
		// while (rs.next()) {
		// String tableName = rs.getString("TABLE_NAME");
		// String schema = rs.getString("TABLE_OWNER");
		// String tableNameWithSchema = DBSetting
		// .getTableNameWithSchema(tableName, schema);
		//
		// if (!this.dbSetting.getUser().equalsIgnoreCase(schema)) {
		// this.cashColumnData(schema, null, null);
		//
		// ERTable table = this.importTable(tableNameWithSchema
		// , tableName, schema);
		//
		// if (table != null) {
		// list.add(table);
		// }
		// }
		// }
		//
		// } finally {
		// this.close(rs);
		// this.close(stmt);
		// }
		// }

		return list;
	}

}
