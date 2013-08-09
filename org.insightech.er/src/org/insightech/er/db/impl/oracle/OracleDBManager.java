package org.insightech.er.db.impl.oracle;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import org.insightech.er.db.DBManagerBase;
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

	private static final ResourceBundle CHARACTER_SET_RESOURCE = ResourceBundle
			.getBundle("oracle_characterset");
	
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

	public DDLCreator getDDLCreator(ERDiagram diagram, boolean semicolon) {
		return new OracleDDLCreator(diagram, semicolon);
	}

	public String[] getIndexTypeList(ERTable table) {
		return EMPTY_STRING_ARRAY;
	}

	@Override
	protected int[] getSupportItems() {
		return new int[] { SUPPORT_AUTO_INCREMENT, SUPPORT_SCHEMA,
				SUPPORT_SEQUENCE };
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

	@Override
	public Set<String> getSystemSchemaList() {
		final Set<String> list = new HashSet<String>();

		list.add("anonymous");
		list.add("ctxsys");
		list.add("dbsnmp");
		list.add("dip");
		list.add("flows_020100");
		list.add("flows_files");
		list.add("hr");
		list.add("mdsys");
		list.add("outln");
		list.add("sys");
		list.add("system");
		list.add("tsmsys");
		list.add("xdb");

		return list;
	}

	public BigDecimal getSequenceMaxValue() {
		return new BigDecimal("9999999999999999999999999999");
	}

	public static List<String> getCharacterSetList() {
		final List<String> list = new ArrayList<String>();

		final Enumeration<String> keys = CHARACTER_SET_RESOURCE.getKeys();

		while (keys.hasMoreElements()) {
			list.add(keys.nextElement());
		}

		return list;
	}
}
