package org.insightech.er.db.impl.mysql;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import org.insightech.er.editor.model.dbimport.ImportFromDBManagerBase;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;

public class MySQLTableImportManager extends ImportFromDBManagerBase {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getViewDefinitionSQL(String schema) {
		if (schema != null) {
			return "SELECT view_definition FROM information_schema.views WHERE table_schema = ? AND table_name = ?";

		} else {
			return "SELECT view_definition FROM information_schema.views WHERE table_name = ?";

		}
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
