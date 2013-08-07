package org.insightech.er.db.impl.h2;

import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.util.Check;
import org.insightech.er.util.Format;

public class H2DDLCreator extends DDLCreator {

	public H2DDLCreator(ERDiagram diagram) {
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
		if (this.semicolon && !Check.isEmpty(description)
				&& this.ddlTarget.inlineTableComment) {
			ddl.append("-- ");
			ddl.append(description.replaceAll("\n", "\n-- "));
			ddl.append("\r\n");
		}

		ddl.append("CREATE ");
		ddl.append("SEQUENCE ");
		ddl.append(filter(this.getNameWithSchema(sequence.getSchema(), sequence
				.getName())));
		if (!Check.isEmpty(sequence.getDataType())) {
			ddl.append(" AS ");
			String dataType = sequence.getDataType();
			ddl.append(dataType);
		}
		if (sequence.getStart() != null) {
			ddl.append(" START WITH ");
			ddl.append(sequence.getStart());
		}
		if (sequence.getIncrement() != null) {
			ddl.append(" INCREMENT BY ");
			ddl.append(sequence.getIncrement());
		}
		if (sequence.getCache() != null) {
			ddl.append(" CACHE ");
			ddl.append(sequence.getCache());
		}
		if (this.semicolon) {
			ddl.append(";");
		}

		return ddl.toString();

	}

	@Override
    public String getDDL(Index index, ERTable table) {
		StringBuilder ddl = new StringBuilder();

		String description = index.getDescription();
		if (this.semicolon && !Check.isEmpty(description)
				&& this.ddlTarget.inlineTableComment) {
			ddl.append("-- ");
			ddl.append(description.replaceAll("\n", "\n-- "));
			ddl.append("\r\n");
		}

		ddl.append("CREATE ");
		if (!index.isNonUnique()) {
			ddl.append("UNIQUE ");
		}
		if (Check.isNotBlank(index.getType())) {
			ddl.append(index.getType().trim());
			ddl.append(' ');
		}
		ddl.append("INDEX ");
		ddl.append(filter(index.getName()));
		ddl.append(" ON ");
		ddl.append(filter(table.getNameWithSchema(getDatabase())));

		ddl.append(" (");

		String comma = "";
		for (NormalColumn column : index.getColumns()) {
			ddl.append(comma);

			ddl.append(filter(column.getPhysicalName()));

			comma = ", ";
		}

		ddl.append(")");

		if (this.semicolon) {
			ddl.append(";");
		}

		return ddl.toString();
	}

	@Override
	protected String getColulmnDDL(NormalColumn normalColumn) {
		StringBuilder ddl = new StringBuilder();

		String description = normalColumn.getDescription();
		if (this.semicolon && !Check.isEmpty(description)
				&& this.ddlTarget.inlineColumnComment) {
			ddl.append("\t-- ");
			ddl.append(description.replaceAll("\n", "\n-- "));
			ddl.append("\r\n");
		}

		ddl.append("\t");
		ddl.append(filter(normalColumn.getPhysicalName()));
		ddl.append(" ");

		ddl.append(filter(Format.formatType(normalColumn.getType(),
				normalColumn.getTypeData(), getDatabase())));

		if (!Check.isEmpty(normalColumn.getDefaultValue())) {
			String defaultValue = normalColumn.getDefaultValue();
			if (Check.equals(ResourceString.getResourceString("label.current.date.time"), defaultValue)) {
				defaultValue = this.getDBManager().getCurrentTimeValue()[0];
			}

			ddl.append(" DEFAULT ");
			if (this.doesNeedQuoteDefaultValue(normalColumn)) {
				ddl.append("'");
				ddl.append(Format.escapeSQL(defaultValue));
				ddl.append("'");

			} else {
				ddl.append(defaultValue);
			}
		}

		if (normalColumn.isNotNull()) {
			ddl.append(" NOT NULL");
		}

		if (normalColumn.isAutoIncrement()) {
			ddl.append(" AUTO_INCREMENT");
			final Sequence sequence = normalColumn.getAutoIncrementSetting();
			if (sequence.getStart() != null) {
				ddl.append('(');
				ddl.append(sequence.getStart());

				if (sequence.getIncrement() != null) {
					ddl.append(", ");
					ddl.append(sequence.getIncrement());
				}
				ddl.append(')');
			}
		}

		if (this.ddlTarget.createComment) {
			String comment = this.filterComment(normalColumn.getLogicalName(),
					normalColumn.getDescription(), true);

			if (!Check.isEmpty(comment)) {
				ddl.append(" COMMENT '");
				ddl.append(comment.replaceAll("'", "''"));
				ddl.append("'");
			}
		}

		if (normalColumn.isUniqueKey()) {
			ddl.append(" UNIQUE");
		}

		String constraint = Format.null2blank(normalColumn.getConstraint());
		if (Check.isNotBlank(constraint)) {
			ddl.append(" CHECK ");
			ddl.append(constraint);
		}

		return ddl.toString();
	}

	@Override
	public String getIfExistsOption(final Class<?> clazz) {
		return "IF EXISTS ";
	}
}
