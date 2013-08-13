package org.insightech.er.db.impl.db2;

import static org.insightech.er.db.SupportFunction.*;

import java.math.BigDecimal;

import org.apache.commons.lang3.ArrayUtils;
import org.insightech.er.db.DBManagerBase;
import org.insightech.er.db.SupportFunction;
import org.insightech.er.db.impl.db2.tablespace.DB2TablespaceProperties;
import org.insightech.er.db.sqltype.SqlTypeManager;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.db.PreTableExportManager;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.dbimport.ImportFromDBManager;
import org.insightech.er.editor.model.dbimport.PreImportFromDBManager;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;

public class DB2DBManager extends DBManagerBase {

	public static final String ID = "DB2";

	public String getId() {
		return ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDriverClassName() {
		return "com.ibm.db2.jcc.DB2Driver";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getURL() {
		return "jdbc:db2://<SERVER NAME>:<PORT>/<DB NAME>";
	}

	public int getDefaultPort() {
		return 50000;
	}

	public SqlTypeManager getSqlTypeManager() {
		return new DB2SqlTypeManager();
	}

	public TableProperties createTableProperties(TableProperties tableProperties) {
		if (tableProperties != null
				&& tableProperties instanceof DB2TableProperties) {
			return tableProperties;
		}

		return new DB2TableProperties();
	}

	public DDLCreator getDDLCreator(ERDiagram diagram) {
		return new DB2DDLCreator(diagram);
	}

	public String[] getIndexTypeList(ERTable table) {
		return ArrayUtils.EMPTY_STRING_ARRAY;
	}

	@Override
	protected SupportFunction[] getSupportItems() {
		return new SupportFunction[] {
				AUTO_INCREMENT,
				AUTO_INCREMENT_CACHE,
				SCHEMA,
				SEQUENCE,
				SEQUENCE_MINVALUE,
				SEQUENCE_MAXVALUE,
				SEQUENCE_CACHE,
				SEQUENCE_CYCLE,
				SEQUENCE_ORDER,
		};
	}

	public ImportFromDBManager getTableImportManager() {
		return new DB2TableImportManager();
	}

	public PreImportFromDBManager getPreTableImportManager() {
		return new DB2PreTableImportManager();
	}

	public PreTableExportManager getPreTableExportManager() {
		return new DB2PreTableExportManager();
	}

	public TablespaceProperties createTablespaceProperties() {
		return new DB2TablespaceProperties();
	}

	public TablespaceProperties checkTablespaceProperties(
			TablespaceProperties tablespaceProperties) {

		if (!(tablespaceProperties instanceof DB2TablespaceProperties)) {
			return new DB2TablespaceProperties();
		}

		return tablespaceProperties;
	}

	public String[] getCurrentTimeValue() {
		return new String[] { "CURRENT TIMESTAMP" };
	}

	public BigDecimal getSequenceMaxValue() {
		return null;
	}

}
