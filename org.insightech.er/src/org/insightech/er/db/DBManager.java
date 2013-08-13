package org.insightech.er.db;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import org.insightech.er.db.sqltype.SqlTypeManager;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.db.PreTableExportManager;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.dbimport.ImportFromDBManager;
import org.insightech.er.editor.model.dbimport.PreImportFromDBManager;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;

public interface DBManager {
	String getId();

	String getURL(String serverName, String dbName, int port);

	int getDefaultPort();

	String getDriverClassName();

	Class<Driver> getDriverClass(String driverClassName);

	SqlTypeManager getSqlTypeManager();

	TableProperties createTableProperties(TableProperties tableProperties);

	TablespaceProperties createTablespaceProperties();

	TablespaceProperties checkTablespaceProperties(
			TablespaceProperties tablespaceProperties);

	DDLCreator getDDLCreator(ERDiagram diagram);

	boolean isSupported(SupportFunction function);

	boolean doesNeedURLDatabaseName();

	boolean doesNeedURLServerName();

	boolean isReservedWord(String str);

	String[] getIndexTypeList(ERTable table);

	PreImportFromDBManager getPreTableImportManager();

	ImportFromDBManager getTableImportManager();

	PreTableExportManager getPreTableExportManager();

	String[] getCurrentTimeValue();

	List<String> getImportSchemaList(Connection con) throws SQLException;

	Set<String> getSystemSchemaList();

	BigDecimal getSequenceMaxValue();

	int getCacheMinValue();
}
