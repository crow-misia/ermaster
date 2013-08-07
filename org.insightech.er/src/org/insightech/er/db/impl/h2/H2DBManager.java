package org.insightech.er.db.impl.h2;

import java.math.BigDecimal;
import java.util.HashSet;
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

public class H2DBManager extends DBManagerBase {

	public static final String ID = "H2";

	public String getId() {
		return ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDriverClassName() {
		return "org.h2.Driver";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getURL() {
		return "jdbc:h2:tcp://<SERVER NAME>:<PORT>/<DB NAME>";
	}

	public int getDefaultPort() {
		return 9101;
	}

	public SqlTypeManager getSqlTypeManager() {
		return new H2SqlTypeManager();
	}

	public TableProperties createTableProperties(TableProperties tableProperties) {
		if (tableProperties != null
				&& tableProperties instanceof H2TableProperties) {
			return tableProperties;
		}

		return new H2TableProperties();
	}

	public DDLCreator getDDLCreator(ERDiagram diagram) {
		return new H2DDLCreator(diagram);
	}

	public String[] getIndexTypeList(ERTable table) {
		return new String[] { "HASH", };
	}

	@Override
	protected int[] getSupportItems() {
		return new int[] { SUPPORT_AUTO_INCREMENT, SUPPORT_AUTO_INCREMENT_SETTING,
				SUPPORT_SCHEMA, SUPPORT_SEQUENCE,
				SUPPORT_DESC_INDEX, };
	}

	public ImportFromDBManager getTableImportManager() {
		return new H2TableImportManager();
	}

	public PreImportFromDBManager getPreTableImportManager() {
		return new H2PreTableImportManager();
	}

	public PreTableExportManager getPreTableExportManager() {
		return new H2PreTableExportManager();
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

	@Override
	public Set<String> getSystemSchemaList() {
		final Set<String> list = new HashSet<String>();

		list.add("information_schema");
		list.add("system_lobs");

		return list;
	}

	public BigDecimal getSequenceMaxValue() {
		return null;
	}

}
