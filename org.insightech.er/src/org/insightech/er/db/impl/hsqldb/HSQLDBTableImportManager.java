package org.insightech.er.db.impl.hsqldb;

import java.sql.SQLException;

import org.insightech.er.editor.model.dbimport.ImportFromDBManagerBase;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;

public class HSQLDBTableImportManager extends ImportFromDBManagerBase {

	@Override
	protected View importView(String schema, String viewName)
			throws SQLException {
		return null;
	}
}
