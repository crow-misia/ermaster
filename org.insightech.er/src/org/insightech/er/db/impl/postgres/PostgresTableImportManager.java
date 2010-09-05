package org.insightech.er.db.impl.postgres;

import org.insightech.er.editor.model.dbimport.ImportFromDBManagerBase;

public class PostgresTableImportManager extends ImportFromDBManagerBase {

	@Override
	protected String getTableNameWithSchema(String schema, String tableName) {
		return this.dbSetting.getTableNameWithSchema("\"" + tableName + "\"",
				schema);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getViewDefinitionSQL(String schema) {
		if (schema != null) {
			return "SELECT definition FROM pg_views WHERE schemaname = ? and viewname = ? ";

		} else {
			return "SELECT definition FROM pg_views WHERE viewname = ? ";

		}
	}

}
