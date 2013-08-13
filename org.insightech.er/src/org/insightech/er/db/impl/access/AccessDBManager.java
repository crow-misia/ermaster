package org.insightech.er.db.impl.access;

import static org.insightech.er.db.SupportFunction.*;

import java.math.BigDecimal;

import org.apache.commons.lang3.ArrayUtils;
import org.insightech.er.db.DBManagerBase;
import org.insightech.er.db.SupportFunction;
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
		return ArrayUtils.EMPTY_STRING_ARRAY;
	}

	@Override
	protected SupportFunction[] getSupportItems() {
		return new SupportFunction[] {
				AUTO_INCREMENT,
				AUTO_INCREMENT_SETTING,
		};
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

	public BigDecimal getSequenceMaxValue() {
		return null;
	}

	public int getCacheMinValue() {
		return 1;
	}

	public TablespaceProperties checkTablespaceProperties(
			TablespaceProperties tablespaceProperties) {
		return null;
	}

	public TablespaceProperties createTablespaceProperties() {
		return null;
	}

	@Override
	public boolean doesNeedURLServerName() {
		return false;
	}

}
