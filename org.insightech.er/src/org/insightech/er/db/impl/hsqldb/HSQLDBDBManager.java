package org.insightech.er.db.impl.hsqldb;

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

public class HSQLDBDBManager extends DBManagerBase {

	public static final String ID = "HSQLDB";

	public String getId() {
		return ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDriverClassName() {
		return "org.hsqldb.jdbcDriver";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getURL() {
		return "jdbc:hsqldb:hsql://<SERVER NAME>:<PORT>/<DB NAME>";
	}

	public int getDefaultPort() {
		return 9001;
	}

	public SqlTypeManager getSqlTypeManager() {
		return new HSQLDBSqlTypeManager();
	}

	public TableProperties createTableProperties(TableProperties tableProperties) {
		if (tableProperties != null
				&& tableProperties instanceof HSQLDBTableProperties) {
			return tableProperties;
		}

		return new HSQLDBTableProperties();
	}

	public DDLCreator getDDLCreator(ERDiagram diagram) {
		return new HSQLDBDDLCreator(diagram);
	}

	public String[] getIndexTypeList(ERTable table) {
		return ArrayUtils.EMPTY_STRING_ARRAY;	}

	@Override
	protected SupportFunction[] getSupportItems() {
		return new SupportFunction[] {
				SCHEMA,
				SEQUENCE,
				SEQUENCE_MINVALUE,
				SEQUENCE_MAXVALUE,
				SEQUENCE_CYCLE,
		};
	}

	public ImportFromDBManager getTableImportManager() {
		return new HSQLDBTableImportManager();
	}

	public PreImportFromDBManager getPreTableImportManager() {
		return new HSQLDBPreTableImportManager();
	}

	public PreTableExportManager getPreTableExportManager() {
		return new HSQLDBPreTableExportManager();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean doesNeedURLDatabaseName() {
		return false;
	}

	public TablespaceProperties createTablespaceProperties() {
		return null;
	}

	public TablespaceProperties checkTablespaceProperties(
			TablespaceProperties tablespaceProperties) {
		return null;
	}

	public String[] getCurrentTimeValue() {
		return new String[] { "CURRENT_TIMESTAMP" };
	}

	public BigDecimal getSequenceMaxValue() {
		return null;
	}

}
