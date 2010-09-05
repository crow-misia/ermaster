package org.insightech.er.db.impl.hsqldb;

import org.insightech.er.editor.model.dbimport.ImportFromDBManagerBase;

public class HSQLDBTableImportManager extends ImportFromDBManagerBase {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getViewDefinitionSQL(String schema) {
		return null;
	}
}
