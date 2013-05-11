package org.insightech.er.db.impl.h2;

import java.io.Closeable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.model.dbimport.DBObject;
import org.insightech.er.editor.model.dbimport.PreImportFromDBManager;
import org.insightech.er.util.Closer;

public class H2PreTableImportManager extends PreImportFromDBManager {

	@Override
	protected List<DBObject> importSequences() throws SQLException {
		List<DBObject> list = new ArrayList<DBObject>();

		if (this.schemaList.isEmpty()) {
			this.schemaList.add(null);
		}

		for (String schemaPattern : this.schemaList) {
			ResultSet resultSet = null;
			PreparedStatement stmt = null;

			try {
				if (schemaPattern == null) {
					stmt = con
							.prepareStatement("SELECT SEQUENCE_SCHEMA, SEQUENCE_NAME FROM INFORMATION_SCHEMA.SEQUENCES");

				} else {
					stmt = con
							.prepareStatement("SELECT SEQUENCE_SCHEMA, SEQUENCE_NAME FROM INFORMATION_SCHEMA.SEQUENCES WHERE SEQUENCE_SCHEMA = ?");
					stmt.setString(1, schemaPattern);
				}

				resultSet = stmt.executeQuery();

				while (resultSet.next()) {
					String schema = resultSet.getString("SEQUENCE_SCHEMA");
					String name = resultSet.getString("SEQUENCE_NAME");

					DBObject dbObject = new DBObject(schema, name,
							DBObject.TYPE_SEQUENCE);
					list.add(dbObject);
				}

			} finally {
				Closer.close(resultSet);
				Closer.close(stmt);
			}
		}

		return list;

	}
}
