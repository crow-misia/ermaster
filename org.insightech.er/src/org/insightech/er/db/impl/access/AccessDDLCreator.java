package org.insightech.er.db.impl.access;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;

public class AccessDDLCreator extends DDLCreator {

	public AccessDDLCreator(ERDiagram diagram, boolean semicolon) {
		super(diagram, semicolon);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getColulmnDDL(NormalColumn normalColumn) {
		StringBuilder ddl = new StringBuilder();

		ddl.append(super.getColulmnDDL(normalColumn));

		if (normalColumn.isAutoIncrement()) {
			ddl.append(" IDENTITY ");

			Sequence sequence = normalColumn.getAutoIncrementSetting();

			if (sequence.getIncrement() != null || sequence.getStart() != null) {
				ddl.append("(");
				if (sequence.getStart() != null) {
					ddl.append(sequence.getStart());

				} else {
					ddl.append("1");
				}

				if (sequence.getIncrement() != null) {
					ddl.append(", ");
					ddl.append(sequence.getIncrement());
				}

				ddl.append(")");
			}
		}

		return ddl.toString();
	}

	@Override
	protected String getDDL(Tablespace object) {
		return null;
	}

}
