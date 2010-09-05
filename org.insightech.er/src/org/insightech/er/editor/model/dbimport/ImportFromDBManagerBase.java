package org.insightech.er.editor.model.dbimport;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.insightech.er.Activator;
import org.insightech.er.ResourceString;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.TranslationResources;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableViewProperties;
import org.insightech.er.editor.model.diagram_contents.element.node.table.unique_key.ComplexUniqueKey;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.TypeData;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.UniqueWord;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.insightech.er.editor.model.settings.DBSetting;
import org.insightech.er.util.Check;
import org.insightech.er.util.Format;

public abstract class ImportFromDBManagerBase implements ImportFromDBManager,
		IRunnableWithProgress {

	private static Logger logger = Logger
			.getLogger(ImportFromDBManagerBase.class.getName());

	protected Connection con;

	private DatabaseMetaData metaData;

	protected DBSetting dbSetting;

	private ERDiagram diagram;

	private List<DBObject> dbObjectList;

	private Map<String, ERTable> tableMap;

	protected Map<String, String> tableCommentMap;

	protected Map<String, Map<String, ColumnData>> columnDataCash;

	private Map<String, List<ForeignKeyData>> tableForeignKeyDataMap;

	private Map<UniqueWord, Word> dictionary;

	private List<ERTable> importedTables;

	private List<Sequence> importedSequences;

	private List<Trigger> importedTriggers;

	private List<Tablespace> importedTablespaces;

	private List<View> importedViews;

	private Exception exception;

	protected TranslationResources translationResources;

	private boolean useCommentAsLogicalName;

	private boolean mergeWord;

	protected static class ColumnData {
		public String columnName;

		public String type;

		public int size;

		public int decimalDegits;

		public int nullable;

		public String defaultValue;

		public String description;

		public String constraint;
	}

	private static class ForeignKeyData {
		private String name;

		private String sourceTableName;

		private String sourceSchemaName;

		private String sourceColumnName;

		private String targetTableName;

		private String targetSchemaName;

		private String targetColumnName;
	}

	protected static class PrimaryKeyData {
		private String columnName;

		private String constraintName;
	}

	public ImportFromDBManagerBase() {
		this.tableMap = new HashMap<String, ERTable>();
		this.tableCommentMap = new HashMap<String, String>();
		this.columnDataCash = new HashMap<String, Map<String, ColumnData>>();
		this.tableForeignKeyDataMap = new HashMap<String, List<ForeignKeyData>>();
		this.dictionary = new HashMap<UniqueWord, Word>();
	}

	public void init(Connection con, DBSetting dbSetting, ERDiagram diagram,
			List<DBObject> dbObjectList, boolean useCommentAsLogicalName,
			boolean mergeWord) throws SQLException {
		this.con = con;
		this.dbSetting = dbSetting;
		this.diagram = diagram;
		this.dbObjectList = dbObjectList;
		this.useCommentAsLogicalName = useCommentAsLogicalName;
		this.mergeWord = mergeWord;

		this.metaData = con.getMetaData();
		this.translationResources = new TranslationResources(diagram
				.getDiagramContents().getSettings().getTranslationSetting());

		if (this.mergeWord) {
			for (Word word : this.diagram.getDiagramContents().getDictionary()
					.getWordList()) {
				this.dictionary.put(new UniqueWord(word), word);
			}
		}
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {

		try {
			monitor.beginTask(ResourceString
					.getResourceString("dialog.message.import.table"),
					this.dbObjectList.size());

			this.importedSequences = this.importSequences(this.dbObjectList);
			this.importedTriggers = this.importTriggers(this.dbObjectList);
			this.importedTablespaces = this
					.importTablespaces(this.dbObjectList);
			this.importedTables = this.importTables(this.dbObjectList, monitor);
			this.importedTables.addAll(this.importSynonyms());

			this.setForeignKeys(this.importedTables);

			this.importedViews = this.importViews(this.dbObjectList);

		} catch (InterruptedException e) {
			throw e;

		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
			this.exception = e;
		}

		monitor.done();
	}

	protected void cashColumnData(List<DBObject> dbObjectList,
			IProgressMonitor monitor) throws SQLException, InterruptedException {
		this.cashColumnDataX(null, dbObjectList, monitor);
	}

	protected void cashColumnDataX(String tableName,
			List<DBObject> dbObjectList, IProgressMonitor monitor)
			throws SQLException, InterruptedException {
		ResultSet columnSet = null;

		try {
			columnSet = metaData.getColumns(null, null, tableName, null);

			while (columnSet.next()) {
				tableName = columnSet.getString("TABLE_NAME");
				String schema = columnSet.getString("TABLE_SCHEM");

				tableName = this.dbSetting.getTableNameWithSchema(tableName,
						schema);

				if (monitor != null) {
					monitor.subTask("reading : " + tableName);
				}

				Map<String, ColumnData> cash = this.columnDataCash
						.get(tableName);
				if (cash == null) {
					cash = new LinkedHashMap<String, ColumnData>();
					this.columnDataCash.put(tableName, cash);
				}

				ColumnData columnData = new ColumnData();
				columnData.columnName = columnSet.getString("COLUMN_NAME");
				columnData.type = columnSet.getString("TYPE_NAME");
				columnData.size = columnSet.getInt("COLUMN_SIZE");
				columnData.decimalDegits = columnSet.getInt("DECIMAL_DIGITS");
				columnData.nullable = columnSet.getInt("NULLABLE");
				columnData.defaultValue = columnSet.getString("COLUMN_DEF");
				columnData.description = columnSet.getString("REMARKS");

				cash.put(columnData.columnName, columnData);

				if (monitor != null && monitor.isCanceled()) {
					throw new InterruptedException("Cancel has been requested.");
				}
			}

		} finally {
			if (columnSet != null) {
				columnSet.close();
			}
		}
	}

	protected void cashTableComment(IProgressMonitor monitor)
			throws SQLException, InterruptedException {
	}

	private List<Sequence> importSequences(List<DBObject> dbObjectList)
			throws SQLException {
		List<Sequence> list = new ArrayList<Sequence>();

		for (DBObject dbObject : dbObjectList) {
			if (DBObject.TYPE_SEQUENCE.equals(dbObject.getType())) {
				String schema = dbObject.getSchema();
				String name = dbObject.getName();

				Sequence sequence = this.importSequence(schema, name);

				if (sequence != null) {
					list.add(sequence);
				}
			}
		}

		return list;
	}

	protected Sequence importSequence(String schema, String sequenceName)
			throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;

		String sequenceNameWithSchema = this.getTableNameWithSchema(schema,
				sequenceName);

		try {
			logger.info("SELECT * FROM " + sequenceNameWithSchema);
			stmt = this.con.prepareStatement("SELECT * FROM "
					+ sequenceNameWithSchema);
			rs = stmt.executeQuery();

			if (rs.next()) {
				Sequence sequence = new Sequence();

				sequence.setName(sequenceName);
				sequence.setSchema(schema);
				sequence.setIncrement(rs.getInt("INCREMENT_BY"));
				sequence.setMinValue(rs.getLong("MIN_VALUE"));
				sequence.setMaxValue(rs.getLong("MAX_VALUE"));
				sequence.setStart(rs.getLong("LAST_VALUE"));
				sequence.setCache(rs.getInt("CACHE_VALUE"));
				sequence.setCycle(rs.getBoolean("IS_CYCLED"));

				return sequence;
			}

			return null;

		} finally {
			if (rs != null) {
				rs.close();
			}

			if (stmt != null) {
				stmt.close();
			}
		}
	}

	private List<Trigger> importTriggers(List<DBObject> dbObjectList)
			throws SQLException {
		List<Trigger> list = new ArrayList<Trigger>();

		for (DBObject dbObject : dbObjectList) {
			if (DBObject.TYPE_TRIGGER.equals(dbObject.getType())) {
				String schema = dbObject.getSchema();
				String name = dbObject.getName();

				Trigger trigger = this.importTrigger(schema, name);

				if (trigger != null) {
					list.add(trigger);
				}
			}
		}

		return list;
	}

	protected Trigger importTrigger(String schema, String triggerName)
			throws SQLException {
		//		
		return null;
	}

	protected List<ERTable> importTables(List<DBObject> dbObjectList,
			IProgressMonitor monitor) throws SQLException, InterruptedException {
		List<ERTable> list = new ArrayList<ERTable>();

		this.cashTableComment(monitor);
		this.cashColumnData(dbObjectList, monitor);

		int i = 0;

		for (DBObject dbObject : dbObjectList) {
			if (DBObject.TYPE_TABLE.equals(dbObject.getType())) {
				i++;

				String tableName = dbObject.getName();
				String schema = dbObject.getSchema();
				String tableNameWithSchema = this.dbSetting
						.getTableNameWithSchema(tableName, schema);

				monitor.subTask("(" + i + "/" + this.dbObjectList.size() + ") "
						+ tableNameWithSchema);
				monitor.worked(1);

				ERTable table = this.importTable(tableNameWithSchema,
						tableName, schema);

				if (table != null) {
					list.add(table);
				}
			}

			if (monitor.isCanceled()) {
				throw new InterruptedException("Cancel has been requested.");
			}
		}

		return list;
	}

	protected List<ERTable> importSynonyms() throws SQLException,
			InterruptedException {
		return new ArrayList<ERTable>();
	}

	protected String getConstraintName(PrimaryKeyData data) {
		return data.constraintName;
	}

	protected ERTable importTable(String tableNameWithSchema, String tableName,
			String schema) throws SQLException, InterruptedException {
		String autoIncrementColumnName = null;
		try {
			autoIncrementColumnName = this.getAutoIncrementColumnName(con, this
					.getTableNameWithSchema(schema, tableName));
		} catch (SQLException e) {
			// �e�[�u����񂪎擾�ł��Ȃ��ꍇ�i���̃��[�U�̏��L���Ȃǂ̏ꍇ�j�A
			// ���̃e�[�u���͎g�p���Ȃ��B
			return null;
		}

		ERTable table = new ERTable();
		TableViewProperties tableProperties = table
				.getTableViewProperties(this.dbSetting.getDbsystem());
		tableProperties.setSchema(schema);

		table.setPhysicalName(tableName);
		table.setLogicalName(this.translationResources.translate(tableName));

		table.setDescription(this.tableCommentMap.get(tableNameWithSchema));

		List<PrimaryKeyData> primaryKeys = this.getPrimaryKeys(table, metaData);
		if (!primaryKeys.isEmpty()) {
			table.setPrimaryKeyName(getConstraintName(primaryKeys.get(0)));
		}

		List<Index> indexes = this.getIndexes(table, metaData, primaryKeys);

		List<Column> columns = this.getColumns(tableNameWithSchema, tableName,
				schema, indexes, primaryKeys, autoIncrementColumnName);

		table.setColumns(columns);
		table.setIndexes(indexes);

		this.tableMap.put(tableNameWithSchema, table);

		for (Index index : indexes) {
			this.setIndexColumn(table, index);
		}

		return table;
	}

	protected String getTableNameWithSchema(String schema, String tableName) {
		return this.dbSetting.getTableNameWithSchema(tableName, schema);
	}

	protected void setForeignKeys(List<ERTable> list) throws SQLException {
		this.cashForeignKeyData();

		for (ERTable target : list) {
			if (this.tableForeignKeyDataMap != null) {
				this.setForeignKeysUsingCash(target);
			} else {
				this.setForeignKeys(target);
			}
		}
	}

	private String getAutoIncrementColumnName(Connection con,
			String tableNameWithSchema) throws SQLException {
		String autoIncrementColumnName = null;

		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = con.createStatement();

			rs = stmt.executeQuery("SELECT 1 FROM " + tableNameWithSchema);
			ResultSetMetaData md = rs.getMetaData();

			for (int i = 0; i < md.getColumnCount(); i++) {
				if (md.isAutoIncrement(i + 1)) {
					autoIncrementColumnName = md.getColumnName(i + 1);
					break;
				}
			}

		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}

		}

		return autoIncrementColumnName;
	}

	protected List<Index> getIndexes(ERTable table, DatabaseMetaData metaData,
			List<PrimaryKeyData> primaryKeys) throws SQLException {

		List<Index> indexes = new ArrayList<Index>();

		Map<String, Index> indexMap = new HashMap<String, Index>();

		ResultSet indexSet = null;

		try {
			// getIndexInfo �� table �w��Ȃ��ł͎擾�ł��Ȃ����߁A
			// �e�[�u�����ƂɎ擾����K�v������܂��B
			indexSet = metaData.getIndexInfo(null, table
					.getTableViewProperties(this.dbSetting.getDbsystem())
					.getSchema(), table.getPhysicalName(), false, true);

			while (indexSet.next()) {
				String name = indexSet.getString("INDEX_NAME");
				if (name == null) {
					continue;
				}

				Index index = indexMap.get(name);

				if (index == null) {
					boolean nonUnique = indexSet.getBoolean("NON_UNIQUE");
					String type = null;
					short indexType = indexSet.getShort("TYPE");
					if (indexType == DatabaseMetaData.tableIndexOther) {
						type = "BTREE";
					}

					// DatabaseMetaData.tableIndexClustered
					// DatabaseMetaData.tableIndexOther
					// DatabaseMetaData.tableIndexStatistic

					index = new Index(table, name, nonUnique, type, null);

					indexMap.put(name, index);
					indexes.add(index);
				}

				String columnName = indexSet.getString("COLUMN_NAME");
				String ascDesc = indexSet.getString("ASC_OR_DESC");

				if (columnName.startsWith("\"") && columnName.endsWith("\"")) {
					columnName = columnName.substring(1,
							columnName.length() - 1);
				}

				Boolean desc = null;

				if ("A".equals(ascDesc)) {
					desc = Boolean.FALSE;
				} else if ("D".equals(ascDesc)) {
					desc = Boolean.TRUE;
				}

				index.addColumnName(columnName, desc);
			}

		} catch (SQLException e) {
			throw e;
		} finally {
			if (indexSet != null) {
				indexSet.close();
			}
		}

		for (Iterator<Index> iter = indexes.iterator(); iter.hasNext();) {
			Index index = iter.next();
			List<String> indexColumns = index.getColumnNames();

			if (indexColumns.size() == primaryKeys.size()) {
				boolean equals = true;

				for (int i = 0; i < indexColumns.size(); i++) {
					if (!indexColumns.get(i).equals(
							primaryKeys.get(i).columnName)) {
						equals = false;
						break;
					}
				}

				if (equals) {
					iter.remove();
				}
			}
		}

		return indexes;
	}

	private void setIndexColumn(ERTable erTable, Index index) {
		for (String columnName : index.getColumnNames()) {
			for (Column column : erTable.getColumns()) {
				if (column instanceof NormalColumn) {
					NormalColumn normalColumn = (NormalColumn) column;

					if (normalColumn.getPhysicalName().equals(columnName)) {
						index.addColumn(normalColumn);
						break;
					}
				}
			}
		}
	}

	private List<PrimaryKeyData> getPrimaryKeys(ERTable table,
			DatabaseMetaData metaData) throws SQLException {
		List<PrimaryKeyData> primaryKeys = new ArrayList<PrimaryKeyData>();

		ResultSet primaryKeySet = null;

		try {
			primaryKeySet = metaData.getPrimaryKeys(null, table
					.getTableViewProperties(this.dbSetting.getDbsystem())
					.getSchema(), table.getPhysicalName());
			while (primaryKeySet.next()) {
				PrimaryKeyData data = new PrimaryKeyData();

				data.columnName = primaryKeySet.getString("COLUMN_NAME");
				data.constraintName = primaryKeySet.getString("PK_NAME");

				primaryKeys.add(data);
			}

		} finally {
			if (primaryKeySet != null) {
				primaryKeySet.close();
			}
		}

		return primaryKeys;
	}

	protected Map<String, ColumnData> getColumnDataMap(
			String tableNameWithSchema, String tableName, String schema)
			throws SQLException, InterruptedException {
		return this.columnDataCash.get(tableNameWithSchema);
	}

	private List<Column> getColumns(String tableNameWithSchema,
			String tableName, String schema, List<Index> indexes,
			List<PrimaryKeyData> primaryKeys, String autoIncrementColumnName)
			throws SQLException, InterruptedException {
		List<Column> columns = new ArrayList<Column>();

		Map<String, ColumnData> columnDataMap = this.getColumnDataMap(
				tableNameWithSchema, tableName, schema);
		if (columnDataMap == null) {
			return new ArrayList<Column>();
		}

		Collection<ColumnData> columnSet = columnDataMap.values();

		for (ColumnData columnData : columnSet) {
			String columnName = columnData.columnName;
			String type = columnData.type;

			boolean array = false;
			Integer arrayDimension = null;
			boolean unsigned = false;

			int unsignedIndex = type.indexOf(" UNSIGNED");
			if (unsignedIndex != -1) {
				unsigned = true;
				type = type.substring(0, unsignedIndex);
			}

			int arrayStartIndex = type.indexOf("[");
			if (arrayStartIndex != -1) {
				array = true;
				String str = type.substring(arrayStartIndex + 1, type
						.indexOf("]"));
				arrayDimension = Integer.parseInt(str);
				type = type.substring(0, arrayStartIndex);
			}

			int size = this.getLength(type, columnData.size);
			Integer length = new Integer(size);

			SqlType sqlType = SqlType.valueOf(this.dbSetting.getDbsystem(),
					type, size);

			if (sqlType == null) {
				logger.info(columnName + ": " + type + ", " + size);
			}

			int decimalDegits = columnData.decimalDegits;
			Integer decimal = new Integer(decimalDegits);

			boolean notNull = false;
			if (columnData.nullable == DatabaseMetaData.columnNoNulls) {
				notNull = true;
			}

			String defaultValue = Format.null2blank(columnData.defaultValue);
			if (SqlType.SERIAL.equals(sqlType)
					|| SqlType.BIG_SERIAL.equals(sqlType)) {
				defaultValue = "";
			}

			String description = Format.null2blank(columnData.description);
			String constraint = Format.null2blank(columnData.constraint);

			boolean primaryKey = false;

			for (PrimaryKeyData primaryKeyData : primaryKeys) {
				if (columnName.equals(primaryKeyData.columnName)) {
					primaryKey = true;
					break;
				}
			}

			boolean uniqueKey = this.isUniqueKey(columnName, indexes,
					primaryKeys);

			boolean autoIncrement = columnName
					.equalsIgnoreCase(autoIncrementColumnName);

			String logicalName = null;
			if (this.useCommentAsLogicalName && !Check.isEmpty(description)) {
				logicalName = description.replaceAll("[\r\n]", "");
			}
			if (Check.isEmpty(logicalName)) {
				logicalName = this.translationResources.translate(columnName);
			}

			String args = null;

			TypeData typeData = new TypeData(length, decimal, array,
					arrayDimension, unsigned, args);

			Word word = new Word(columnName, logicalName, sqlType, typeData,
					description, this.diagram.getDatabase());
			UniqueWord uniqueWord = new UniqueWord(word);

			if (this.dictionary.get(uniqueWord) != null) {
				word = this.dictionary.get(uniqueWord);
			} else {
				this.dictionary.put(uniqueWord, word);
			}

			// TODO UNIQUE KEY �̐��񖼂��擾�ł��Ă��Ȃ�

			NormalColumn column = new NormalColumn(word, notNull, primaryKey,
					uniqueKey, autoIncrement, defaultValue, constraint, null);

			columns.add(column);
		}

		return columns;
	}

	private boolean isUniqueKey(String columnName, List<Index> indexes,
			List<PrimaryKeyData> primaryKeys) {
		String primaryKey = null;

		if (primaryKeys.size() == 1) {
			primaryKey = primaryKeys.get(0).columnName;
		}

		if (columnName == null) {
			return false;
		}

		for (Index index : indexes) {
			List<String> columnNames = index.getColumnNames();
			if (columnNames.size() == 1) {
				String indexColumnName = columnNames.get(0);
				if (columnName.equals(indexColumnName)) {
					if (!index.isNonUnique()) {
						if (!columnName.equals(primaryKey)) {
							indexes.remove(index);
							return true;
						}
						return false;
					}
				}
			}
		}

		return false;
	}

	private boolean isCyclicForeignKye(ForeignKeyData foreignKeyData) {
		if (foreignKeyData.sourceSchemaName == null) {
			if (foreignKeyData.targetSchemaName != null) {
				return false;
			}

		} else if (!foreignKeyData.sourceSchemaName
				.equals(foreignKeyData.targetSchemaName)) {
			return false;
		}

		if (!foreignKeyData.sourceTableName
				.equals(foreignKeyData.targetTableName)) {
			return false;
		}

		if (!foreignKeyData.sourceColumnName
				.equals(foreignKeyData.targetColumnName)) {
			return false;
		}

		return true;
	}

	private void cashForeignKeyData() throws SQLException {
		ResultSet foreignKeySet = null;
		try {
			foreignKeySet = metaData.getImportedKeys(null, null, null);

			while (foreignKeySet.next()) {
				ForeignKeyData foreignKeyData = new ForeignKeyData();

				foreignKeyData.name = foreignKeySet.getString("FK_NAME");
				foreignKeyData.sourceSchemaName = foreignKeySet
						.getString("PKTABLE_SCHEM");
				foreignKeyData.sourceTableName = foreignKeySet
						.getString("PKTABLE_NAME");
				foreignKeyData.sourceColumnName = foreignKeySet
						.getString("PKCOLUMN_NAME");
				foreignKeyData.targetSchemaName = foreignKeySet
						.getString("FKTABLE_SCHEM");
				foreignKeyData.targetTableName = foreignKeySet
						.getString("FKTABLE_NAME");
				foreignKeyData.targetColumnName = foreignKeySet
						.getString("FKCOLUMN_NAME");

				if (this.isCyclicForeignKye(foreignKeyData)) {
					continue;
				}

				String key = this.dbSetting.getTableNameWithSchema(
						foreignKeyData.targetTableName,
						foreignKeyData.targetSchemaName);

				List<ForeignKeyData> foreignKeyDataList = tableForeignKeyDataMap
						.get(key);

				if (foreignKeyDataList == null) {
					foreignKeyDataList = new ArrayList<ForeignKeyData>();
					tableForeignKeyDataMap.put(key, foreignKeyDataList);
				}

				foreignKeyDataList.add(foreignKeyData);
			}
		} catch (SQLException e) {
			tableForeignKeyDataMap = null;

		} finally {
			if (foreignKeySet != null) {
				foreignKeySet.close();
			}
		}
	}

	private void setForeignKeysUsingCash(ERTable target) throws SQLException {
		String tableName = target.getPhysicalName();
		String schema = target.getTableViewProperties(
				this.dbSetting.getDbsystem()).getSchema();

		tableName = this.dbSetting.getTableNameWithSchema(tableName, schema);

		List<ForeignKeyData> foreignKeyList = this.tableForeignKeyDataMap
				.get(tableName);

		if (foreignKeyList == null) {
			return;
		}

		Map<String, List<ForeignKeyData>> sameNameForeignKeyDataMap = this
				.collectSameNameForeignKeyData(foreignKeyList);

		for (Map.Entry<String, List<ForeignKeyData>> entry : sameNameForeignKeyDataMap
				.entrySet()) {
			this.createRelation(target, entry.getValue());
		}
	}

	private void setForeignKeys(ERTable target) throws SQLException {
		String tableName = target.getPhysicalName();
		String schemaName = target.getTableViewProperties(
				this.dbSetting.getDbsystem()).getSchema();

		ResultSet foreignKeySet = null;

		try {
			foreignKeySet = this.metaData.getImportedKeys(null, schemaName,
					tableName);

			List<ForeignKeyData> foreignKeyList = new ArrayList<ForeignKeyData>();

			while (foreignKeySet.next()) {
				ForeignKeyData foreignKeyData = new ForeignKeyData();

				foreignKeyData.name = foreignKeySet.getString("FK_NAME");
				foreignKeyData.sourceTableName = foreignKeySet
						.getString("PKTABLE_NAME");
				foreignKeyData.sourceSchemaName = foreignKeySet
						.getString("PKTABLE_SCHEM");
				foreignKeyData.sourceColumnName = foreignKeySet
						.getString("PKCOLUMN_NAME");
				foreignKeyData.targetSchemaName = foreignKeySet
						.getString("FKTABLE_SCHEM");
				foreignKeyData.targetColumnName = foreignKeySet
						.getString("FKCOLUMN_NAME");

				foreignKeyList.add(foreignKeyData);
			}

			if (foreignKeyList.isEmpty()) {
				return;
			}

			Map<String, List<ForeignKeyData>> sameNameForeignKeyDataMap = this
					.collectSameNameForeignKeyData(foreignKeyList);

			for (Map.Entry<String, List<ForeignKeyData>> entry : sameNameForeignKeyDataMap
					.entrySet()) {
				this.createRelation(target, entry.getValue());
			}

		} finally {
			if (foreignKeySet != null) {
				foreignKeySet.close();
			}
		}
	}

	private Map<String, List<ForeignKeyData>> collectSameNameForeignKeyData(
			List<ForeignKeyData> foreignKeyList) {
		Map<String, List<ForeignKeyData>> map = new HashMap<String, List<ForeignKeyData>>();

		for (ForeignKeyData foreignKyeData : foreignKeyList) {
			List<ForeignKeyData> list = map.get(foreignKyeData.name);
			if (list == null) {
				list = new ArrayList<ForeignKeyData>();
				map.put(foreignKyeData.name, list);
			}

			list.add(foreignKyeData);
		}

		return map;
	}

	private Relation createRelation(ERTable target,
			List<ForeignKeyData> foreignKeyDataList) {
		ForeignKeyData representativeData = foreignKeyDataList.get(0);

		String sourceTableName = representativeData.sourceTableName;
		String sourceSchemaName = representativeData.sourceSchemaName;

		sourceTableName = this.dbSetting.getTableNameWithSchema(
				sourceTableName, sourceSchemaName);

		ERTable source = this.tableMap.get(sourceTableName);
		if (source == null) {
			return null;
		}

		boolean referenceForPK = true;

		List<NormalColumn> primaryKeys = source.getPrimaryKeys();
		if (primaryKeys.size() != foreignKeyDataList.size()) {
			referenceForPK = false;
		}

		Map<NormalColumn, NormalColumn> referenceMap = new HashMap<NormalColumn, NormalColumn>();

		for (ForeignKeyData foreignKeyData : foreignKeyDataList) {
			NormalColumn sourceColumn = null;

			for (NormalColumn normalColumn : source.getNormalColumns()) {
				if (normalColumn.getPhysicalName().equals(
						foreignKeyData.sourceColumnName)) {
					sourceColumn = normalColumn;
					break;
				}
			}

			if (sourceColumn == null) {
				return null;
			}

			if (!sourceColumn.isPrimaryKey()) {
				referenceForPK = false;
			}

			NormalColumn targetColumn = null;

			for (NormalColumn normalColumn : target.getNormalColumns()) {
				if (normalColumn.getPhysicalName().equals(
						foreignKeyData.targetColumnName)) {
					targetColumn = normalColumn;
					break;
				}
			}

			if (targetColumn == null) {
				return null;
			}

			referenceMap.put(sourceColumn, targetColumn);
		}

		ComplexUniqueKey referencedComplexUniqueKey = null;
		NormalColumn referencedColumn = null;

		if (!referenceForPK) {
			if (referenceMap.size() > 1) {
				// TODO ������ӃL�[�̐��񖼂𕜌��ł��Ă��Ȃ�
				referencedComplexUniqueKey = new ComplexUniqueKey("");
				for (NormalColumn column : referenceMap.keySet()) {
					referencedComplexUniqueKey.addColumn(column);
				}
				// TODO �����ŕ�����ӃL�[��ǉ�����̂ł͂Ȃ��Aindex ���烆�j�[�N�L�[�����Ƃ���ł���H
				source.getComplexUniqueKeyList()
						.add(referencedComplexUniqueKey);

			} else {
				referencedColumn = referenceMap.keySet().iterator().next();
			}
		}

		Relation relation = new Relation(referenceForPK,
				referencedComplexUniqueKey, referencedColumn);
		relation.setName(representativeData.name);
		relation.setSource(source);
		relation.setTargetWithoutForeignKey(target);

		for (Map.Entry<NormalColumn, NormalColumn> entry : referenceMap
				.entrySet()) {
			entry.getValue().addReference(entry.getKey(), relation);
		}

		return relation;
	}

	public List<ERTable> getImportedTables() {
		return importedTables;
	}

	public List<Sequence> getImportedSequences() {
		return importedSequences;
	}

	public List<View> getImportedViews() {
		return importedViews;
	}

	private List<View> importViews(List<DBObject> dbObjectList)
			throws SQLException {
		List<View> list = new ArrayList<View>();

		for (DBObject dbObject : dbObjectList) {
			if (DBObject.TYPE_VIEW.equals(dbObject.getType())) {
				String schema = dbObject.getSchema();
				String name = dbObject.getName();

				View view = this.importView(schema, name);

				if (view != null) {
					list.add(view);
				}
			}
		}

		return list;
	}

	protected View importView(String schema, String viewName)
			throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;

		String sql = getViewDefinitionSQL(schema);
		if (sql == null) {
			return null;
		}

		try {
			stmt = this.con.prepareStatement(sql);

			if (schema != null) {
				stmt.setString(1, schema);
				stmt.setString(2, viewName);

			} else {
				stmt.setString(1, viewName);

			}

			rs = stmt.executeQuery();

			if (rs.next()) {
				View view = new View();

				view.setPhysicalName(viewName);
				view.setLogicalName(this.translationResources
						.translate(viewName));
				String definitionSQL = rs.getString(1);
				view.setSql(definitionSQL);
				view.getTableViewProperties().setSchema(schema);

				List<Column> columnList = this.getViewColumnList(definitionSQL);
				view.setColumns(columnList);

				return view;
			}

			return null;

		} finally {
			this.close(rs);
			this.close(stmt);
		}
	}

	protected abstract String getViewDefinitionSQL(String schema);

	protected List<Column> getViewColumnList(String sql) {
		List<Column> columnList = new ArrayList<Column>();

		String upperSql = sql.toUpperCase();
		int selectIndex = upperSql.indexOf("SELECT ");
		int fromIndex = upperSql.indexOf(" FROM ");

		if (selectIndex == -1 || fromIndex == -1) {
			return null;
		}

		String columnsPart = sql.substring(selectIndex + "SELECT ".length(),
				fromIndex);
		String fromPart = sql.substring(fromIndex + " FROM ".length());

		int whereIndex = fromPart.toUpperCase().indexOf(" WHERE ");

		if (whereIndex != -1) {
			fromPart = fromPart.substring(0, whereIndex);
		}

		Map<String, String> aliasTableMap = new HashMap<String, String>();

		StringTokenizer fromTokenizer = new StringTokenizer(fromPart, ",");

		while (fromTokenizer.hasMoreTokens()) {
			String tableName = fromTokenizer.nextToken().trim();

			tableName.replaceAll(" AS", "");
			tableName.replaceAll(" as", "");
			tableName.replaceAll(" As", "");
			tableName.replaceAll(" aS", "");

			String tableAlias = null;

			int asIndex = tableName.toUpperCase().indexOf(" ");
			if (asIndex != -1) {
				tableAlias = tableName.substring(asIndex + 1).trim();
				tableName = tableName.substring(0, asIndex).trim();

				aliasTableMap.put(tableAlias, tableName);
			}
		}

		StringTokenizer columnTokenizer = new StringTokenizer(columnsPart, ",");

		while (columnTokenizer.hasMoreTokens()) {
			String columnName = columnTokenizer.nextToken().trim();

			columnName = columnName.replaceAll(" AS", "");
			columnName = columnName.replaceAll(" as", "");
			columnName = columnName.replaceAll(" As", "");
			columnName = columnName.replaceAll(" aS", "");

			String columnAlias = null;

			int asIndex = columnName.toUpperCase().indexOf(" ");
			if (asIndex != -1) {
				columnAlias = columnName.substring(asIndex + 1).trim();
				columnName = columnName.substring(0, asIndex).trim();

			}

			int dotIndex = columnName.indexOf(".");

			String tableName = null;

			if (dotIndex != -1) {
				String aliasTableName = columnName.substring(0, dotIndex);
				columnName = columnName.substring(dotIndex + 1);

				tableName = aliasTableMap.get(aliasTableName);

				if (tableName == null) {
					tableName = aliasTableName;
				}
			}

			if (columnAlias == null) {
				columnAlias = columnName;
			}

			NormalColumn targetColumn = null;

			if (columnName != null) {
				if (tableName != null) {
					tableName = tableName.toLowerCase();
				}
				columnName = columnName.toLowerCase();

				for (ERTable table : this.importedTables) {
					if (tableName == null
							|| (table.getPhysicalName() != null && tableName
									.equals(table.getPhysicalName()
											.toLowerCase()))) {
						for (NormalColumn column : table.getExpandedColumns()) {
							if (column.getPhysicalName() != null
									&& columnName.equals(column
											.getPhysicalName().toLowerCase())) {
								targetColumn = column;

								break;
							}
						}

						if (targetColumn != null) {
							break;
						}
					}

				}
			}

			Word word = null;

			if (targetColumn != null) {
				word = new Word(targetColumn.getWord());
				word.setPhysicalName(columnAlias);

			} else {
				word = new Word(columnAlias, this.translationResources
						.translate(columnAlias), null, new TypeData(null, null,
						false, null, false, null), null, null);

			}

			UniqueWord uniqueWord = new UniqueWord(word);

			if (this.dictionary.get(uniqueWord) != null) {
				word = this.dictionary.get(uniqueWord);
			} else {
				this.dictionary.put(uniqueWord, word);
			}

			NormalColumn column = new NormalColumn(word, false, false, false,
					false, null, null, null);
			columnList.add(column);
		}

		return columnList;
	}

	public List<Tablespace> getImportedTablespaces() {
		return importedTablespaces;
	}

	private List<Tablespace> importTablespaces(List<DBObject> dbObjectList)
			throws SQLException {
		List<Tablespace> list = new ArrayList<Tablespace>();

		for (DBObject dbObject : dbObjectList) {
			if (DBObject.TYPE_TABLESPACE.equals(dbObject.getType())) {
				String name = dbObject.getName();

				Tablespace tablespace = this.importTablespace(name);

				if (tablespace != null) {
					list.add(tablespace);
				}
			}
		}

		return list;
	}

	public List<Trigger> getImportedTriggers() {
		return importedTriggers;
	}

	protected Tablespace importTablespace(String tablespaceName)
			throws SQLException {
		// TODO �e�[�u���X�y�[�X�̃C���|�[�g
		return null;
	}

	public Exception getException() {
		return exception;
	}

	protected int getLength(String type, int size) {
		return size;
	}

	public static void main(String[] args) throws InputException,
			InstantiationException, IllegalAccessException, SQLException {
		new Activator();

		DBSetting setting = new DBSetting("Oracle", "localhost", 1521, "XE",
				"nakajima", "nakajima", null, null);

		Connection con = null;
		try {
			con = setting.connect();
			DatabaseMetaData metaData = con.getMetaData();

			metaData.getIndexInfo(null, "SYS", "ALERT_QT", false, false);

		} finally {
			if (con != null) {
				con.close();
			}
		}
	}

	protected void close(ResultSet rs) throws SQLException {
		if (rs != null) {
			rs.close();
		}
	}

	protected void close(Statement stmt) throws SQLException {
		if (stmt != null) {
			stmt.close();
		}
	}

}
