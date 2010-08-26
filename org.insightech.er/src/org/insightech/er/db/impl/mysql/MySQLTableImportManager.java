package org.insightech.er.db.impl.mysql;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import org.insightech.er.editor.model.dbimport.ImportFromDBManagerBase;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;

public class MySQLTableImportManager extends ImportFromDBManagerBase {

	@Override
	protected View importView(String schema, String viewName)
			throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			if (schema != null) {
				stmt = this.con
						.prepareStatement("SELECT view_definition FROM information_schema.views WHERE table_schema = ? AND table_name = ?");
				stmt.setString(1, schema);
				stmt.setString(2, viewName);

			} else {
				stmt = this.con
						.prepareStatement("SELECT view_definition FROM information_schema.views WHERE table_name = ?");
				stmt.setString(1, viewName);

			}

			rs = stmt.executeQuery();

			if (rs.next()) {
				View view = new View();

				view.setPhysicalName(viewName);
				view.setLogicalName(this.translationResources
						.translate(viewName));
				view.setSql(rs.getString("view_definition"));

				return view;
			}

		} finally {
			this.close(rs);
			this.close(stmt);
		}

		return null;
	}

	@Override
	protected List<Index> getIndexes(ERTable table, DatabaseMetaData metaData,
			List<PrimaryKeyData> primaryKeys) throws SQLException {

		List<Index> indexes = super.getIndexes(table, metaData, primaryKeys);

		for (Iterator<Index> iter = indexes.iterator(); iter.hasNext();) {
			Index index = iter.next();

			if ("PRIMARY".equalsIgnoreCase(index.getName())) {
				iter.remove();
			}
		}

		return indexes;
	}

	@Override
	protected String getConstraintName(PrimaryKeyData data) {
		return null;
	}
}
