package org.insightech.er.db.impl.hsqldb;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;

public class HSQLDBDDLCreator extends DDLCreator {

	public HSQLDBDDLCreator(ERDiagram diagram, boolean semicolon) {
		super(diagram, semicolon);
	}

	@Override
	protected String getDDL(Tablespace tablespace) {
		return null;
	}

}
