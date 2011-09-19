package org.insightech.er.db.impl.sqlite;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;

public class SQLiteDDLCreator extends DDLCreator {

	public SQLiteDDLCreator(ERDiagram diagram, boolean semicolon) {
		super(diagram, semicolon);
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
			ddl.append(" AUTOINCREMENT");
		}
		
		return ddl.toString();
	}

}
