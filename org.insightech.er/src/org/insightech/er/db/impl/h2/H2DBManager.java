package org.insightech.er.db.impl.h2;

import static org.insightech.er.db.SupportFunction.*;

import java.math.BigDecimal;

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

public class H2DBManager extends DBManagerBase {

	public static final String ID = "H2";

	public String getId() {
		return ID;
	}

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
	protected SupportFunction[] getSupportItems() {
		return new SupportFunction[] {
				AUTO_INCREMENT,
				AUTO_INCREMENT_SETTING,
				AUTO_INCREMENT_CACHE,
				SCHEMA,
				SEQUENCE,
				SEQUENCE_CACHE,
				DESC_INDEX,
		};
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

	public BigDecimal getSequenceMaxValue() {
		return null;
	}

	public int getCacheMinValue() {
		return 1;
	}
}
