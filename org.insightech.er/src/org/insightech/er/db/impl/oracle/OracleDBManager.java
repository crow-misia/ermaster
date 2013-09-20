package org.insightech.er.db.impl.oracle;

import static org.insightech.er.db.SupportFunction.COLUMN_UNIT;
import static org.insightech.er.db.SupportFunction.INDEX_BITMAP;
import static org.insightech.er.db.SupportFunction.INDEX_DESC;
import static org.insightech.er.db.SupportFunction.MATERIALIZED_VIEW;
import static org.insightech.er.db.SupportFunction.MULTIPLE_INSERT;
import static org.insightech.er.db.SupportFunction.SCHEMA;
import static org.insightech.er.db.SupportFunction.SEQUENCE;
import static org.insightech.er.db.SupportFunction.SEQUENCE_CACHE;
import static org.insightech.er.db.SupportFunction.SEQUENCE_CYCLE;
import static org.insightech.er.db.SupportFunction.SEQUENCE_MAXVALUE;
import static org.insightech.er.db.SupportFunction.SEQUENCE_MINVALUE;
import static org.insightech.er.db.SupportFunction.SEQUENCE_ORDER;

import java.math.BigDecimal;

import org.apache.commons.lang3.ArrayUtils;
import org.insightech.er.db.DBManagerBase;
import org.insightech.er.db.SupportFunction;
import org.insightech.er.db.impl.oracle.tablespace.OracleTablespaceProperties;
import org.insightech.er.db.sqltype.SqlTypeManager;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.db.PreTableExportManager;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.dbimport.ImportFromDBManager;
import org.insightech.er.editor.model.dbimport.PreImportFromDBManager;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;

public class OracleDBManager extends DBManagerBase {

	public static final String ID = "Oracle";

	public String getId() {
		return ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDriverClassName() {
		return "oracle.jdbc.driver.OracleDriver";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getURL() {
		return "jdbc:oracle:thin:@<SERVER NAME>:<PORT>:<DB NAME>";
	}

	public int getDefaultPort() {
		return 1521;
	}

	public SqlTypeManager getSqlTypeManager() {
		return new OracleSqlTypeManager();
	}

	public TableProperties createTableProperties(TableProperties tableProperties) {
		if (tableProperties != null
				&& tableProperties instanceof OracleTableProperties) {
			return tableProperties;
		}

		return new OracleTableProperties();
	}

	public DDLCreator getDDLCreator(ERDiagram diagram) {
		return new OracleDDLCreator(diagram);
	}

	public String[] getIndexTypeList(ERTable table) {
		return ArrayUtils.EMPTY_STRING_ARRAY;
	}

	@Override
	protected SupportFunction[] getSupportItems() {
		return new SupportFunction[] {
				INDEX_DESC,
				INDEX_BITMAP,
				SCHEMA,
				SEQUENCE,
				SEQUENCE_MINVALUE,
				SEQUENCE_MAXVALUE,
				SEQUENCE_CACHE,
				SEQUENCE_CYCLE,
				SEQUENCE_ORDER,
				COLUMN_UNIT,
				MATERIALIZED_VIEW,
				MULTIPLE_INSERT,
		};
	}

	public ImportFromDBManager getTableImportManager() {
		return new OracleTableImportManager();
	}

	public PreImportFromDBManager getPreTableImportManager() {
		return new OraclePreTableImportManager();
	}

	public PreTableExportManager getPreTableExportManager() {
		return new OraclePreTableExportManager();
	}

	public TablespaceProperties createTablespaceProperties() {
		return new OracleTablespaceProperties();
	}

	public TablespaceProperties checkTablespaceProperties(
			TablespaceProperties tablespaceProperties) {

		if (!(tablespaceProperties instanceof OracleTablespaceProperties)) {
			return new OracleTablespaceProperties();
		}

		return tablespaceProperties;
	}

	public String[] getCurrentTimeValue() {
		return new String[] { "SYSDATE" };
	}

	public BigDecimal getSequenceMaxValue() {
		return new BigDecimal("9999999999999999999999999999");
	}

	public int getCacheMinValue() {
		return 1;
	}
}
