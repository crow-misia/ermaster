package org.insightech.er.db.impl.postgres;

import static org.insightech.er.db.SupportFunction.*;

import java.math.BigDecimal;

import org.insightech.er.db.DBManagerBase;
import org.insightech.er.db.SupportFunction;
import org.insightech.er.db.impl.postgres.tablespace.PostgresTablespaceProperties;
import org.insightech.er.db.sqltype.SqlTypeManager;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.db.PreTableExportManager;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.dbimport.ImportFromDBManager;
import org.insightech.er.editor.model.dbimport.PreImportFromDBManager;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;

public class PostgresDBManager extends DBManagerBase {

	public static final String ID = "PostgreSQL";

	public String getId() {
		return ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDriverClassName() {
		return "org.postgresql.Driver";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getURL() {
		return "jdbc:postgresql://<SERVER NAME>:<PORT>/<DB NAME>";
	}

	public int getDefaultPort() {
		return 5432;
	}

	public SqlTypeManager getSqlTypeManager() {
		return new PostgresSqlTypeManager();
	}

	public TableProperties createTableProperties(TableProperties tableProperties) {
		if (tableProperties != null
				&& tableProperties instanceof PostgresTableProperties) {
			return tableProperties;
		}

		return new PostgresTableProperties();
	}

	public DDLCreator getDDLCreator(ERDiagram diagram) {
		return new PostgresDDLCreator(diagram);
	}

	public String[] getIndexTypeList(ERTable table) {
		return new String[] { "BTREE", "RTREE", "HASH", };
	}

	@Override
	protected SupportFunction[] getSupportItems() {
		return new SupportFunction[] {
				AUTO_INCREMENT_SETTING,
				AUTO_INCREMENT_MINVALUE,
				AUTO_INCREMENT_MAXVALUE,
				AUTO_INCREMENT_CACHE,
				AUTO_INCREMENT_CYCLE,
				SCHEMA,
				SEQUENCE,
				SEQUENCE_MINVALUE,
				SEQUENCE_MAXVALUE,
				SEQUENCE_CYCLE,
				ARRAY_TYPE,
		};
	}

	public ImportFromDBManager getTableImportManager() {
		return new PostgresTableImportManager();
	}

	public PreImportFromDBManager getPreTableImportManager() {
		return new PostgresPreTableImportManager();
	}

	public PreTableExportManager getPreTableExportManager() {
		return new PostgresPreTableExportManager();
	}

	public TablespaceProperties createTablespaceProperties() {
		return new PostgresTablespaceProperties();
	}

	public TablespaceProperties checkTablespaceProperties(
			TablespaceProperties tablespaceProperties) {

		if (!(tablespaceProperties instanceof PostgresTablespaceProperties)) {
			return new PostgresTablespaceProperties();
		}

		return tablespaceProperties;
	}

	public String[] getCurrentTimeValue() {
		return new String[] { "CURRENT_TIMESTAMP", "now()" };
	}

	public BigDecimal getSequenceMaxValue() {
		return BigDecimal.valueOf(Long.MAX_VALUE);
	}
}
