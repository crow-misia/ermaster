package org.insightech.er.db.impl.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.insightech.er.editor.model.dbexport.db.PreTableExportManager;

public class MySQLPreTableExportManager extends PreTableExportManager {

	@Override
	protected String dropForeignKeys() throws SQLException {
		StringBuffer ddl = new StringBuffer();

		ResultSet tableSet = null;

		try {
			tableSet = metaData.getTables(null, null, null,
					new String[] { "TABLE" });

			while (tableSet.next()) {
				String tableName = tableSet.getString("TABLE_NAME");
				String schema = tableSet.getString("TABLE_SCHEM");
				tableName = this.dbSetting.getTableNameWithSchema(tableName,
						schema);

				String sql = "ALTER TABLE " + tableName + " DROP FOREIGN KEY;";

				ddl.append(sql);
				ddl.append("\r\n");
			}

		} finally {
			if (tableSet != null) {
				tableSet.close();
			}
		}

		return ddl.toString();
	}

	@Override
	protected void prepareNewNames() {
	}

}
