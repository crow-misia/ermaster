package org.insightech.er.db.impl.postgres;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.insightech.er.editor.model.dbimport.ImportFromDBManagerBase;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;

public class PostgresTableImportManager extends ImportFromDBManagerBase {

	@Override
	protected String getTableNameWithSchema(String schema, String tableName) {
		return this.dbSetting.getTableNameWithSchema("\"" + tableName + "\"",
				schema);
	}

	@Override
	protected View importView(String schema, String viewName)
			throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			if (schema != null) {
				stmt = this.con
						.prepareStatement("SELECT definition FROM pg_views WHERE schemaname = ? and viewname = ? ");
				stmt.setString(1, schema);
				stmt.setString(2, viewName);

			} else {
				stmt = this.con
						.prepareStatement("SELECT definition FROM pg_views WHERE viewname = ? ");
				stmt.setString(1, viewName);

			}

			rs = stmt.executeQuery();

			if (rs.next()) {
				View view = new View();

				view.setPhysicalName(viewName);
				view.setLogicalName(this.translationResources
						.translate(viewName));
				
				String sql = rs.getString("definition");
				view.setSql(sql);
				view.getTableViewProperties().setSchema(schema);

				List<Column> columnList = this.getViewColumnList(sql);
				view.setColumns(columnList);
				
				return view;
			}

			return null;

		} finally {
			this.close(rs);
			this.close(stmt);
		}
	}

}
