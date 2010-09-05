package org.insightech.er.db.impl.db2;

import org.insightech.er.editor.model.dbimport.ImportFromDBManagerBase;

public class DB2TableImportManager extends ImportFromDBManagerBase {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getViewDefinitionSQL(String schema) {
		return "SELECT TEXT FROM SYSCAT.VIEWS WHERE VIEWSCHEMA = ? AND VIEWNAME = ?";
	}
}
