package org.insightech.er.db.impl.mysql;

import static org.insightech.er.db.SupportFunction.*;

import java.math.BigDecimal;

import org.insightech.er.db.DBManagerBase;
import org.insightech.er.db.SupportFunction;
import org.insightech.er.db.impl.mysql.tablespace.MySQLTablespaceProperties;
import org.insightech.er.db.sqltype.SqlTypeManager;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.db.PreTableExportManager;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.dbimport.ImportFromDBManager;
import org.insightech.er.editor.model.dbimport.PreImportFromDBManager;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;

public class MySQLDBManager extends DBManagerBase {

	public static final String ID = "MySQL";

	public String getId() {
		return ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDriverClassName() {
		return "com.mysql.jdbc.Driver";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getURL() {
		return "jdbc:mysql://<SERVER NAME>:<PORT>/<DB NAME>";
	}

	public int getDefaultPort() {
		return 3306;
	}

	public SqlTypeManager getSqlTypeManager() {
		return new MySQLSqlTypeManager();
	}

	public TableProperties createTableProperties(TableProperties tableProperties) {
		if (tableProperties != null
				&& tableProperties instanceof MySQLTableProperties) {
			return tableProperties;
		}

		return new MySQLTableProperties();
	}

	public DDLCreator getDDLCreator(ERDiagram diagram) {
		return new MySQLDDLCreator(diagram);
	}

	public String[] getIndexTypeList(ERTable table) {
		return new String[] { "BTREE", "RTREE", "HASH", };
	}

	@Override
	protected SupportFunction[] getSupportItems() {
		return new SupportFunction[] {
				AUTO_INCREMENT,
				AUTO_INCREMENT_SETTING,
				INDEX_DESC,
				INDEX_FULLTEXT,
				SCHEMA,
				COLUMN_CHARSET,
				MULTIPLE_INSERT,
		};
	}

	public ImportFromDBManager getTableImportManager() {
		return new MySQLTableImportManager();
	}

	public PreImportFromDBManager getPreTableImportManager() {
		return new MySQLPreTableImportManager();
	}

	public PreTableExportManager getPreTableExportManager() {
		return new MySQLPreTableExportManager();
	}

	public TablespaceProperties createTablespaceProperties() {
		return new MySQLTablespaceProperties();
	}

	public TablespaceProperties checkTablespaceProperties(
			TablespaceProperties tablespaceProperties) {

		if (!(tablespaceProperties instanceof MySQLTablespaceProperties)) {
			return new MySQLTablespaceProperties();
		}

		return tablespaceProperties;
	}

	public String[] getCurrentTimeValue() {
		return new String[] { "NOW()", "SYSDATE()", };
	}

	public BigDecimal getSequenceMaxValue() {
		return null;
	}

	public int getCacheMinValue() {
		return 1;
	}
}
