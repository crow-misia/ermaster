package org.insightech.er.db.impl.sqlserver;

import static org.insightech.er.db.SupportFunction.*;

import java.math.BigDecimal;

import org.apache.commons.lang3.ArrayUtils;
import org.insightech.er.db.DBManagerBase;
import org.insightech.er.db.SupportFunction;
import org.insightech.er.db.impl.sqlserver.tablespace.SqlServerTablespaceProperties;
import org.insightech.er.db.sqltype.SqlTypeManager;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.db.PreTableExportManager;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.dbimport.ImportFromDBManager;
import org.insightech.er.editor.model.dbimport.PreImportFromDBManager;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;

public class SqlServerDBManager extends DBManagerBase {

	public static final String ID = "SQLServer";

	public String getId() {
		return ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDriverClassName() {
		return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getURL() {
		return "jdbc:sqlserver://<SERVER NAME>:<PORT>;database=<DB NAME>";
	}

	public int getDefaultPort() {
		return 1433;
	}

	public SqlTypeManager getSqlTypeManager() {
		return new SqlServerSqlTypeManager();
	}

	public TableProperties createTableProperties(TableProperties tableProperties) {
		if (tableProperties != null
				&& tableProperties instanceof SqlServerTableProperties) {
			return tableProperties;
		}

		return new SqlServerTableProperties();
	}

	public DDLCreator getDDLCreator(ERDiagram diagram) {
		return new SqlServerDDLCreator(diagram);
	}

	public String[] getIndexTypeList(ERTable table) {
		return ArrayUtils.EMPTY_STRING_ARRAY;
	}

	@Override
	protected SupportFunction[] getSupportItems() {
		return new SupportFunction[] {
				AUTO_INCREMENT,
				AUTO_INCREMENT_SETTING,
				SCHEMA,
		};
	}

	public ImportFromDBManager getTableImportManager() {
		return new SqlServerTableImportManager();
	}

	public PreImportFromDBManager getPreTableImportManager() {
		return new SqlServerPreTableImportManager();
	}

	public PreTableExportManager getPreTableExportManager() {
		return new SqlServerPreTableExportManager();
	}

	public TablespaceProperties createTablespaceProperties() {
		return new SqlServerTablespaceProperties();
	}

	public TablespaceProperties checkTablespaceProperties(
			TablespaceProperties tablespaceProperties) {

		if (!(tablespaceProperties instanceof SqlServerTablespaceProperties)) {
			return new SqlServerTablespaceProperties();
		}

		return tablespaceProperties;
	}

	public String[] getCurrentTimeValue() {
		return new String[] { "GETDATE()", "CURRENT_TIMESTAMP" };
	}

	public BigDecimal getSequenceMaxValue() {
		return null;
	}

	public int getCacheMinValue() {
		return 1;
	}
}
