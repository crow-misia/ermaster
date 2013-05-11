package org.insightech.er.db.impl.oracle;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.insightech.er.editor.model.dbexport.db.PreTableExportManager;
import org.insightech.er.util.Closer;

public class OraclePreTableExportManager extends PreTableExportManager {

	@Override
	protected void checkTableExist(Connection con, String tableNameWithSchema)
			throws SQLException {
		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery("SELECT 1 FROM " + tableNameWithSchema);

		} finally {
			Closer.close(rs);
			Closer.close(stmt);
		}
	}
}
