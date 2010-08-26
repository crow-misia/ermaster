package org.insightech.er.db.impl.mysql;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.db.DBManagerBase;
import org.insightech.er.db.impl.mysql.tablespace.MySQLTablespaceProperties;
import org.insightech.er.db.sqltype.SqlTypeManager;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.db.PreTableExportManager;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.dbimport.ImportFromDBManager;
import org.insightech.er.editor.model.dbimport.PreImportFromDBManager;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;

public class MySQLDBManager extends DBManagerBase {

	public static final String ID = "MySQL";

	public String getId() {
		return ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDriverClassName() {
		return "com.mysql.jdbc.Driver";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getURL() {
		return "jdbc:mysql://<SERVER NAME>:<PORT>/<DB NAME>";
	}

	public int getDefaultPort() {
		return 3306;
	}

	public SqlTypeManager getSqlTypeManager() {
		return new MySQLSqlTypeManager();
	}

	public TableProperties createTableProperties(TableProperties tableProperties) {
		if (tableProperties != null
				&& tableProperties instanceof MySQLTableProperties) {
			return tableProperties;
		}

		return new MySQLTableProperties();
	}

	public DDLCreator getDDLCreator(ERDiagram diagram, boolean semicolon) {
		return new MySQLDDLCreator(diagram, semicolon);
	}

	public List<String> getIndexTypeList(ERTable table) {
		List<String> list = new ArrayList<String>();

		list.add("BTREE");

		return list;
	}

	@Override
	protected int[] getSupportItems() {
		return new int[] { SUPPORT_AUTO_INCREMENT,
				SUPPORT_AUTO_INCREMENT_SETTING, SUPPORT_DESC_INDEX,
				SUPPORT_FULLTEXT_INDEX, SUPPORT_SCHEMA };
	}

	public ImportFromDBManager getTableImportManager() {
		return new MySQLTableImportManager();
	}

	public PreImportFromDBManager getPreTableImportManager() {
		return new MySQLPreTableImportManager();
	}

	public PreTableExportManager getPreTableExportManager() {
		return new MySQLPreTableExportManager();
	}

	public TablespaceProperties createTablespaceProperties() {
		return new MySQLTablespaceProperties();
	}

	public TablespaceProperties checkTablespaceProperties(
			TablespaceProperties tablespaceProperties) {

		if (!(tablespaceProperties instanceof MySQLTablespaceProperties)) {
			return new MySQLTablespaceProperties();
		}

		return tablespaceProperties;
	}

	public String[] getCurrentTimeValue() {
		return new String[] {"NOW(), SYSDATE()"};
	}
}
