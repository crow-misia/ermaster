package org.insightech.er.db.impl.hsqldb;

import org.apache.commons.lang3.StringUtils;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;

public class HSQLDBDDLCreator extends DDLCreator {

	public HSQLDBDDLCreator(ERDiagram diagram) {
		super(diagram);
	}

	@Override
	protected String getDDL(Tablespace tablespace) {
		return null;
	}

	@Override
	public String getDDL(Sequence sequence) {
		StringBuilder ddl = new StringBuilder();

		String description = sequence.getDescription();
		if (this.semicolon && StringUtils.isNotBlank(description)
				&& this.ddlTarget.inlineTableComment) {
			ddl.append("-- ");
			ddl.append(StringUtils.replace(description, "\n", "\n-- "));
			ddl.append("\r\n");
		}

		ddl.append("CREATE SEQUENCE ");
		ddl.append(filter(this.getNameWithSchema(sequence.getSchema(), sequence
				.getName())));
		if (StringUtils.isNotBlank(sequence.getDataType())) {
			ddl.append(" AS ");
			String dataType = sequence.getDataType();
			ddl.append(dataType);
		}
		if (sequence.getIncrement() != null) {
			ddl.append(" INCREMENT BY ");
			ddl.append(sequence.getIncrement());
		}
		if (sequence.getMinValue() != null) {
			ddl.append(" MINVALUE ");
			ddl.append(sequence.getMinValue());
		}
		if (sequence.getMaxValue() != null) {
			ddl.append(" MAXVALUE ");
			ddl.append(sequence.getMaxValue());
		}
		if (sequence.getStart() != null) {
			ddl.append(" START WITH ");
			ddl.append(sequence.getStart());
		}
		if (sequence.isCycle()) {
			ddl.append(" CYCLE");
		}
		if (this.semicolon) {
			ddl.append(";");
		}

		return ddl.toString();
	}

	@Override
	public String getIfExistsOption(final Class<?> clazz) {
		return "IF EXISTS ";
	}

	@Override
	protected String getCreateViewSQL() {
		return "CREATE OR REPLACE VIEW ";
	}
}
