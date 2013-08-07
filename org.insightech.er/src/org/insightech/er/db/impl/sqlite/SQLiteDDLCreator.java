package org.insightech.er.db.impl.sqlite;

import java.util.LinkedHashSet;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;

public class SQLiteDDLCreator extends DDLCreator {

	public SQLiteDDLCreator(ERDiagram diagram) {
		super(diagram);
	}

	@Override
	protected String getDDL(Tablespace tablespace) {
		return null;
	}

	@Override
	protected String getColulmnDDL(NormalColumn normalColumn) {
		StringBuilder ddl = new StringBuilder();

		ddl.append(super.getColulmnDDL(normalColumn));

		if (normalColumn.isAutoIncrement()) {
			ddl.append(" PRIMARY KEY AUTOINCREMENT");
		}

		return ddl.toString();
	}

	@Override
	protected String getPrimaryKeyDDL(ERTable table) {
		StringBuilder ddl = new StringBuilder(super.getPrimaryKeyDDL(table));

		for (Relation relation : table.getIncomingRelations()) {
			ddl.append(",\r\n\tFOREIGN KEY (");

			String comma = "";
			for (NormalColumn column : relation.getForeignKeyColumns()) {
				ddl.append(comma);
				ddl.append(filter(column.getPhysicalName()));
				comma = ", ";
			}

			ddl.append(")\r\n");
			ddl.append("\tREFERENCES ");
			ddl.append(filter(relation.getSourceTableView().getNameWithSchema(getDatabase())));
			ddl.append(" (");

			comma = "";
			for (NormalColumn foreignKeyColumn : relation
					.getForeignKeyColumns()) {
				ddl.append(comma);
				ddl.append(filter(foreignKeyColumn
						.getReferencedColumn(relation).getPhysicalName()));
				comma = ", ";
			}

			ddl.append(")");
		}

		return ddl.toString();
	}

	@Override
	protected Iterable<ERTable> getTablesForCreateDDL() {
		LinkedHashSet<ERTable> results = new LinkedHashSet<ERTable>();

		for (ERTable table : this.getDiagram().getDiagramContents()
				.getContents().getTableSet()) {
			if (!results.contains(table)) {
				this.getReferedTables(results, table);
				results.add(table);
			}
		}

		return results;
	}

	private void getReferedTables(LinkedHashSet<ERTable> referedTables,
			ERTable table) {
		for (NodeElement nodeElement : table.getReferedElementList()) {
			if (nodeElement instanceof ERTable) {
				if (nodeElement != table) {
					ERTable referedTable = (ERTable) nodeElement;
					if (!referedTables.contains(referedTable)) {
						this.getReferedTables(referedTables, referedTable);
						referedTables.add(referedTable);
					}
				}
			}
		}
	}

	@Override
	protected String getCreateForeignKeys(ERDiagram diagram) {
		return "";
	}
	
	
}
