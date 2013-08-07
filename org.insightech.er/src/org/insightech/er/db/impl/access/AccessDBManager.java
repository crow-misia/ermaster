package org.insightech.er.db.impl.access;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;

import org.insightech.er.db.DBManagerBase;
import org.insightech.er.db.sqltype.SqlTypeManager;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.db.PreTableExportManager;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.dbimport.ImportFromDBManager;
import org.insightech.er.editor.model.dbimport.PreImportFromDBManager;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;

public class AccessDBManager extends DBManagerBase {

	public static final String ID = "MSAccess";

	public String getId() {
		return ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDriverClassName() {
		return "sun.jdbc.odbc.JdbcOdbcDriver";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getURL() {
		return "jdbc:odbc:<DB NAME>";
	}

	public int getDefaultPort() {
		return 0;
	}

	public SqlTypeManager getSqlTypeManager() {
		return new AccessSqlTypeManager();
	}

	public TableProperties createTableProperties(TableProperties tableProperties) {
		if (tableProperties != null
				&& tableProperties instanceof AccessTableProperties) {
			return tableProperties;
		}

		return new AccessTableProperties();
	}

	public DDLCreator getDDLCreator(ERDiagram diagram) {
		return new AccessDDLCreator(diagram);
	}

	public String[] getIndexTypeList(ERTable table) {
		return EMPTY_STRING_ARRAY;
	}

	@Override
	protected int[] getSupportItems() {
		return new int[] { SUPPORT_AUTO_INCREMENT,
				SUPPORT_AUTO_INCREMENT_SETTING };
	}

	public ImportFromDBManager getTableImportManager() {
		return new AccessTableImportManager();
	}

	public PreImportFromDBManager getPreTableImportManager() {
		return new AccessPreTableImportManager();
	}

	public PreTableExportManager getPreTableExportManager() {
		return new AccessPreTableExportManager();
	}

	public String[] getCurrentTimeValue() {
		return new String[] { "GETDATE()", "CURRENT_TIMESTAMP" };
	}

	@Override
	public Set<String> getSystemSchemaList() {
		return Collections.emptySet();
	}

	public BigDecimal getSequenceMaxValue() {
		return null;
	}

	public TablespaceProperties checkTablespaceProperties(
			TablespaceProperties tablespaceProperties) {
		return null;
	}

	public TablespaceProperties createTablespaceProperties() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean doesNeedURLServerName() {
		return false;
	}

}
