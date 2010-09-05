package org.insightech.er.db.impl.sqlserver;

import org.insightech.er.editor.model.dbimport.ImportFromDBManagerBase;

public class SqlServerTableImportManager extends ImportFromDBManagerBase {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getViewDefinitionSQL(String schema) {
		return "SELECT VIEW_DEFINITION FROM INFORMATION_SCHEMA.VIEWS WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?";
	}
}
