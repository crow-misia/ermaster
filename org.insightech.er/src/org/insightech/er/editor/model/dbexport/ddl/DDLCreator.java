package org.insightech.er.editor.model.dbexport.ddl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.insightech.er.ResourceString;
import org.insightech.er.db.DBManager;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.db.SupportFunction;
import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableViewProperties;
import org.insightech.er.editor.model.diagram_contents.element.node.table.unique_key.ComplexUniqueKey;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.insightech.er.editor.model.settings.Environment;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.util.Format;

public abstract class DDLCreator {

	private final ERDiagram diagram;

	protected boolean semicolon;

	protected String newline;

	protected Environment environment;

	protected DDLTarget ddlTarget;

	public DDLCreator(ERDiagram diagram) {
		this.diagram = diagram;
	}

	public final void init(Environment environment, DDLTarget ddlTarget, boolean semicolon, String newline) {
		this.environment = environment;
		this.ddlTarget = ddlTarget;
		this.semicolon = semicolon;
		this.newline = newline;
	}

	public String getDropDDL(ERDiagram diagram) {
		StringBuilder ddl = new StringBuilder();

		if (this.ddlTarget.dropIndex) {
			ddl.append(this.getDropIndexes(diagram));
		}
		if (this.ddlTarget.dropView) {
			ddl.append(this.getDropViews(diagram));
		}
		if (this.ddlTarget.dropTrigger) {
			ddl.append(this.getDropTriggers(diagram));
		}
		if (this.ddlTarget.dropTable) {
			ddl.append(this.getDropTables(diagram));
		}
		if (this.ddlTarget.dropSequence
				&& DBManagerFactory.getDBManager(diagram).isSupported(
						SupportFunction.SEQUENCE)) {
			ddl.append(this.getDropSequences(diagram));
		}
		if (this.ddlTarget.dropTablespace) {
			ddl.append(this.getDropTablespaces(diagram));
		}

		return ddl.toString();
	}

	private String getDropTablespaces(ERDiagram diagram) {
		StringBuilder ddl = new StringBuilder();

		String comment = "\r\n-- Drop Tablespaces\r\n\r\n";
		if (this.getDBManager().createTablespaceProperties() != null) {
			for (Tablespace tablespace : diagram.getDiagramContents()
					.getTablespaceSet()) {
				ddl.append(comment);
				comment = "";

				ddl.append(this.getDropDDL(tablespace));
				ddl.append("\r\n");
				ddl.append("\r\n");
				ddl.append("\r\n");
			}
		}

		return ddl.toString();
	}

	private String getDropSequences(ERDiagram diagram) {
		StringBuilder ddl = new StringBuilder();

		boolean first = true;

		for (Sequence sequence : diagram.getDiagramContents().getSequenceSet()) {
			if (first) {
				ddl.append("\r\n-- Drop Sequences\r\n\r\n");
				first = false;
			}
			ddl.append(this.getDropDDL(sequence));
			ddl.append("\r\n");
		}

		if (!first) {
			ddl.append("\r\n");
			ddl.append("\r\n");
		}

		return ddl.toString();
	}

	private String getDropViews(ERDiagram diagram) {
		StringBuilder ddl = new StringBuilder();

		boolean first = true;

		for (View view : diagram.getDiagramContents().getContents()
				.getViewSet()) {
			if (first) {
				ddl.append("\r\n-- Drop Views\r\n\r\n");
				first = false;
			}
			ddl.append(this.getDropDDL(view));
			ddl.append("\r\n");
		}

		if (!first) {
			ddl.append("\r\n");
			ddl.append("\r\n");
		}

		return ddl.toString();
	}

	private String getDropTriggers(ERDiagram diagram) {
		StringBuilder ddl = new StringBuilder();

		boolean first = true;

		for (Trigger trigger : diagram.getDiagramContents().getTriggerSet()) {
			if (first) {
				ddl.append("\r\n-- Drop Triggers\r\n\r\n");
				first = false;
			}
			ddl.append(this.getDropDDL(trigger));
			ddl.append("\r\n");
		}

		if (!first) {
			ddl.append("\r\n");
			ddl.append("\r\n");
		}

		return ddl.toString();
	}

	private String getDropIndexes(ERDiagram diagram) {
		StringBuilder ddl = new StringBuilder();

		boolean first = true;

		for (ERTable table : diagram.getDiagramContents().getContents()
				.getTableSet()) {

			if (diagram.getCurrentCategory() != null
					&& !diagram.getCurrentCategory().contains(table)) {
				continue;
			}

			for (Index index : table.getIndexes()) {
				if (first) {
					ddl.append("\r\n-- Drop Indexes\r\n\r\n");
					first = false;
				}
				ddl.append(this.getDropDDL(index, table));
				ddl.append("\r\n");
			}
		}

		if (!first) {
			ddl.append("\r\n");
			ddl.append("\r\n");
		}

		return ddl.toString();
	}

	private String getDropTables(ERDiagram diagram) {
		StringBuilder ddl = new StringBuilder();

		Set<TableView> doneTables = new HashSet<TableView>();

		boolean first = true;

		for (ERTable table : diagram.getDiagramContents().getContents()
				.getTableSet()) {

			if (diagram.getCurrentCategory() != null
					&& !diagram.getCurrentCategory().contains(table)) {
				continue;
			}

			if (first) {
				ddl.append("\r\n-- Drop Tables\r\n\r\n");
				first = false;
			}

			if (!doneTables.contains(table)) {
				ddl.append(this.getDropDDL(table, doneTables));
			}
		}

		if (!first) {
			ddl.append("\r\n");
			ddl.append("\r\n");
		}

		return ddl.toString();
	}

	public String getCreateDDL(ERDiagram diagram) {
		StringBuilder ddl = new StringBuilder();

		if (this.ddlTarget.createTablespace) {
			ddl.append(this.getCreateTablespaces(diagram));
		}
		if (this.ddlTarget.createSequence
				&& DBManagerFactory.getDBManager(diagram).isSupported(
						SupportFunction.SEQUENCE)) {
			ddl.append(this.getCreateSequences(diagram));
		}
		if (this.ddlTarget.createTable) {
			ddl.append(this.getCreateTables(diagram));
		}
		if (this.ddlTarget.createForeignKey) {
			ddl.append(this.getCreateForeignKeys(diagram));
		}
		if (this.ddlTarget.createTrigger) {
			ddl.append(this.getCreateTriggers(diagram));
		}
		if (this.ddlTarget.createView) {
			ddl.append(this.getCreateViews(diagram));
		}
		if (this.ddlTarget.createIndex) {
			ddl.append(this.getCreateIndexes(diagram));
		}
		if (this.ddlTarget.createComment) {
			ddl.append(this.getCreateComment(diagram));
		}
		return ddl.toString();
	}

	private String getCreateTablespaces(ERDiagram diagram) {
		StringBuilder ddl = new StringBuilder();

		boolean first = true;

		if (this.getDBManager().createTablespaceProperties() != null) {
			for (Tablespace tablespace : diagram.getDiagramContents()
					.getTablespaceSet()) {
				if (first) {
					ddl.append("\r\n-- Create Tablespaces\r\n\r\n");
					first = false;
				}

				String description = tablespace.getDescription();
				if (this.semicolon && StringUtils.isNotBlank(description)
						&& this.ddlTarget.inlineTableComment) {
					ddl.append("-- ");
					ddl.append(description.replaceAll("\n", "\n-- "));
					ddl.append("\r\n");
				}

				ddl.append(this.getDDL(tablespace));
				ddl.append("\r\n");
				ddl.append("\r\n");
				ddl.append("\r\n");
			}
		}

		return ddl.toString();
	}

	protected abstract String getDDL(Tablespace object);

	protected Iterable<ERTable> getTablesForCreateDDL() {
		return diagram.getDiagramContents().getContents().getTableSet();
	}

	private String getCreateTables(ERDiagram diagram) {
		StringBuilder ddl = new StringBuilder();

		boolean first = true;

		for (ERTable table : this.getTablesForCreateDDL()) {

			if (diagram.getCurrentCategory() != null
					&& !diagram.getCurrentCategory().contains(table)) {
				continue;
			}

			if (first) {
				ddl.append("\r\n-- Create Tables\r\n\r\n");
				first = false;
			}

			ddl.append(this.getDDL(table));
			ddl.append("\r\n");
			ddl.append("\r\n");
			ddl.append("\r\n");
			ddl.append(this.getTableSettingDDL(table));
		}

		return ddl.toString();
	}

	protected String getCreateForeignKeys(ERDiagram diagram) {
		StringBuilder ddl = new StringBuilder();

		boolean first = true;

		for (ERTable table : diagram.getDiagramContents().getContents()
				.getTableSet()) {

			if (diagram.getCurrentCategory() != null
					&& !diagram.getCurrentCategory().contains(table)) {
				continue;
			}

			for (Relation relation : table.getOutgoingRelations()) {
				if (first) {
					ddl.append("\r\n-- Create Foreign Keys\r\n\r\n");
					first = false;
				}
				ddl.append(this.getDDL(relation));
				ddl.append("\r\n");
				ddl.append("\r\n");
				ddl.append("\r\n");
			}
		}

		return ddl.toString();
	}

	private String getCreateIndexes(ERDiagram diagram) {
		StringBuilder ddl = new StringBuilder();

		boolean first = true;

		for (ERTable table : diagram.getDiagramContents().getContents()
				.getTableSet()) {

			if (diagram.getCurrentCategory() != null
					&& !diagram.getCurrentCategory().contains(table)) {
				continue;
			}

			for (Index index : table.getIndexes()) {
				if (first) {
					ddl.append("\r\n-- Create Indexes\r\n\r\n");
					first = false;
				}
				ddl.append(this.getDDL(index, table));
				ddl.append("\r\n");
			}
		}

		if (!first) {
			ddl.append("\r\n");
			ddl.append("\r\n");
		}

		return ddl.toString();
	}

	private String getCreateViews(ERDiagram diagram) {
		StringBuilder ddl = new StringBuilder();

		boolean first = true;

		for (View view : diagram.getDiagramContents().getContents()
				.getViewSet()) {

			if (first) {
				ddl.append("\r\n-- Create Views\r\n\r\n");
				first = false;
			}
			ddl.append(this.getDDL(view));
			ddl.append("\r\n");
		}

		if (!first) {
			ddl.append("\r\n");
			ddl.append("\r\n");
		}

		return ddl.toString();
	}

	private String getCreateTriggers(ERDiagram diagram) {
		StringBuilder ddl = new StringBuilder();

		boolean first = true;

		for (Trigger trigger : diagram.getDiagramContents().getTriggerSet()) {

			if (first) {
				ddl.append("\r\n-- Create Triggers\r\n\r\n");
				first = false;
			}
			ddl.append(this.getDDL(trigger));
			ddl.append("\r\n");
		}

		if (!first) {
			ddl.append("\r\n");
			ddl.append("\r\n");
		}

		return ddl.toString();
	}

	private String getCreateSequences(ERDiagram diagram) {
		StringBuilder ddl = new StringBuilder();

		boolean first = true;

		List<String> autoSequenceNames = diagram.getDiagramContents()
				.getContents().getTableSet()
				.getAutoSequenceNames(getDatabase());

		for (Sequence sequence : diagram.getDiagramContents().getSequenceSet()) {
			String sequenceName = this.getNameWithSchema(sequence.getSchema(),
					sequence.getName()).toUpperCase();
			if (autoSequenceNames.contains(sequenceName)) {
				continue;
			}

			if (first) {
				ddl.append("\r\n-- Create Sequences\r\n\r\n");
				first = false;
			}
			ddl.append(this.getDDL(sequence));
			ddl.append("\r\n");
		}

		if (!first) {
			ddl.append("\r\n");
			ddl.append("\r\n");
		}

		return ddl.toString();
	}

	private String getCreateComment(ERDiagram diagram) {
		StringBuilder ddl = new StringBuilder();

		boolean first = true;

		for (ERTable table : diagram.getDiagramContents().getContents()
				.getTableSet()) {

			if (diagram.getCurrentCategory() != null
					&& !diagram.getCurrentCategory().contains(table)) {
				continue;
			}
			List<String> commentDDLList = this.getCommentDDL(table);

			if (!commentDDLList.isEmpty()) {
				if (first) {
					ddl.append("\r\n-- Comments\r\n\r\n");
					first = false;
				}

				for (String commentDDL : commentDDLList) {
					ddl.append(commentDDL);
					ddl.append("\r\n");
				}
			}
		}

		if (!first) {
			ddl.append("\r\n");
			ddl.append("\r\n");
		}

		return ddl.toString();
	}

	public String getDDL(ERTable table) {
		StringBuilder ddl = new StringBuilder();

		String tableDescription = table.getDescription();
		if (this.semicolon && StringUtils.isNotBlank(tableDescription)
				&& this.ddlTarget.inlineTableComment) {
			ddl.append("-- ");
			ddl.append(tableDescription.replaceAll("\n", "\n-- "));
			ddl.append("\r\n");
		}
		ddl.append("CREATE TABLE ");
		ddl.append(filter(table.getNameWithSchema(getDatabase())));
		ddl.append("\r\n(\r\n");

		boolean first = true;

		for (Column column : table.getColumns()) {
			if (column instanceof NormalColumn) {
				NormalColumn normalColumn = (NormalColumn) column;

				if (!first) {
					ddl.append(",\r\n");
				}

				ddl.append(this.getColulmnDDL(normalColumn));

				first = false;

			} else {
				ColumnGroup columnGroup = (ColumnGroup) column;

				for (NormalColumn normalColumn : columnGroup.getColumns()) {
					if (!first) {
						ddl.append(",\r\n");
					}

					ddl.append(this.getColulmnDDL(normalColumn));

					first = false;
				}
			}
		}

		ddl.append(this.getPrimaryKeyDDL(table));

		List<ComplexUniqueKey> complexUniqueKeyList = table
				.getComplexUniqueKeyList();
		for (ComplexUniqueKey complexUniqueKey : complexUniqueKeyList) {
			ddl.append(",\r\n");
			ddl.append("\t");
			if (StringUtils.isNotBlank(complexUniqueKey.getUniqueKeyName())) {
				ddl.append("CONSTRAINT ");
				ddl.append(complexUniqueKey.getUniqueKeyName());
				ddl.append(" ");
			}

			ddl.append("UNIQUE (");

			first = true;
			for (NormalColumn column : complexUniqueKey.getColumnList()) {
				if (!first) {
					ddl.append(", ");
				}
				ddl.append(filter(column.getPhysicalName()));
				first = false;
			}

			ddl.append(")");
		}

		String constraint = Format.null2blank(table.getConstraint()).trim();
		if (StringUtils.isNotBlank(constraint)) {
			constraint = constraint.replaceAll("\r\n", "\r\n\t");

			ddl.append(",\r\n");
			ddl.append("\t");
			ddl.append(constraint);
		}

		ddl.append("\r\n");
		ddl.append(")");

		ddl.append(this.getPostDDL(table));

		String option = Format.null2blank(table.getOption()).trim();
		if (StringUtils.isNotBlank(option)) {
			ddl.append("\r\n");
			ddl.append(option);
		}

		if (this.semicolon) {
			ddl.append(';');
		}

		return ddl.toString();
	}

	protected String getPrimaryKeyDDL(ERTable table) {
		StringBuilder ddl = new StringBuilder();

		List<NormalColumn> primaryKeys = table.getPrimaryKeys();

		if (primaryKeys.size() != 0) {
			ddl.append(",\r\n");
			ddl.append("\t");
			if (StringUtils.isNotBlank(table.getPrimaryKeyName())) {
				ddl.append("CONSTRAINT ");
				ddl.append(table.getPrimaryKeyName());
				ddl.append(" ");
			}
			ddl.append("PRIMARY KEY (");

			boolean first = true;
			for (NormalColumn primaryKey : primaryKeys) {
				if (!first) {
					ddl.append(", ");
				}
				ddl.append(filter(primaryKey.getPhysicalName()));
				ddl.append(this.getPrimaryKeyLength(table, primaryKey));
				first = false;
			}

			ddl.append(")");
		}

		return ddl.toString();
	}

	protected String getPrimaryKeyLength(ERTable table, NormalColumn primaryKey) {
		return "";
	}

	protected String getTableSettingDDL(ERTable table) {
		return "";
	}

	protected String getColulmnDDL(NormalColumn normalColumn) {
		StringBuilder ddl = new StringBuilder();

		String description = normalColumn.getDescription();
		if (this.semicolon && StringUtils.isNotBlank(description)
				&& this.ddlTarget.inlineColumnComment) {
			ddl.append("\t-- ");
			ddl.append(description.replaceAll("\n", "\n\t-- "));
			ddl.append("\r\n");
		}

		ddl.append("\t");
		ddl.append(filter(normalColumn.getPhysicalName()));
		ddl.append(" ");

		ddl.append(filter(Format.formatType(normalColumn.getType(),
				normalColumn.getTypeData(), getDatabase())));

		if (StringUtils.isNotEmpty(normalColumn.getDefaultValue())) {
			String defaultValue = normalColumn.getDefaultValue();
			if (ResourceString.getResourceString("label.current.date.time")
					.equals(defaultValue)) {
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

		if (normalColumn.isUniqueKey()) {
			if (StringUtils.isNotBlank(normalColumn.getUniqueKeyName())) {
				ddl.append(" CONSTRAINT ");
				ddl.append(normalColumn.getUniqueKeyName());
			}
			ddl.append(" UNIQUE");
		}

		String constraint = Format.null2blank(normalColumn.getConstraint());
		if (StringUtils.isNotBlank(constraint)) {
			ddl.append(" ");
			ddl.append(constraint);
		}

		return ddl.toString();
	}

	protected boolean doesNeedQuoteDefaultValue(NormalColumn normalColumn) {
		final SqlType type = normalColumn.getType();

		if (type.isNumber()) {
			return false;
		}

		if (type.isTimestamp()) {
			if (!Character
					.isDigit(normalColumn.getDefaultValue().toCharArray()[0])) {
				return false;
			}
		}

		if (Boolean.class.equals(type.getJavaClass())) {
			return false;
		}

		return true;
	}

	public List<String> getCommentDDL(ERTable table) {
		return new ArrayList<String>();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getPostDDL(ERTable table) {
		TableViewProperties commonTableProperties = (TableViewProperties) this
				.getDiagram().getDiagramContents().getSettings()
				.getTableViewProperties();

		TableProperties tableProperties = (TableProperties) table
				.getTableViewProperties();

		Tablespace tableSpace = tableProperties.getTableSpace();
		if (tableSpace == null) {
			tableSpace = commonTableProperties.getTableSpace();
		}

		StringBuilder postDDL = new StringBuilder();

		if (tableSpace != null) {
			postDDL.append(" TABLESPACE ");
			postDDL.append(tableSpace.getName());
		}

		return postDDL.toString();
	}

	public String getDDL(Index index, ERTable table) {
		StringBuilder ddl = new StringBuilder();

		String description = index.getDescription();
		if (this.semicolon && StringUtils.isNotBlank(description)
				&& this.ddlTarget.inlineTableComment) {
			ddl.append("-- ");
			ddl.append(description.replaceAll("\n", "\n-- "));
			ddl.append("\r\n");
		}

		ddl.append("CREATE ");
		if (!index.isNonUnique()) {
			ddl.append("UNIQUE ");
		}
		if (!index.isBitmap()) {
			ddl.append("BITMAP ");
		}
		ddl.append("INDEX ");
		ddl.append(filter(index.getName()));
		ddl.append(" ON ");
		ddl.append(filter(table.getNameWithSchema(getDatabase())));

		if (StringUtils.isNotBlank(index.getType())) {
			ddl.append(" USING ");
			ddl.append(index.getType().trim());
		}

		ddl.append(" (");
		boolean first = true;

		int i = 0;
		List<Boolean> descs = index.getDescs();

		for (NormalColumn column : index.getColumns()) {
			if (!first) {
				ddl.append(", ");

			}

			ddl.append(filter(column.getPhysicalName()));

			if (this.getDBManager().isSupported(SupportFunction.INDEX_DESC)) {
				if (descs.size() > i) {
					Boolean desc = descs.get(i);
					if (Boolean.TRUE.equals(desc)) {
						ddl.append(" DESC");
					} else {
						ddl.append(" ASC");
					}
				}
			}

			first = false;
			i++;
		}

		ddl.append(")");

		if (this.semicolon) {
			ddl.append(';');
		}

		return ddl.toString();
	}

	public String getDDL(Relation relation) {
		StringBuilder ddl = new StringBuilder();

		ddl.append("ALTER TABLE ");
		ddl.append(filter(relation.getTargetTableView().getNameWithSchema(
				getDatabase())));
		ddl.append("\r\n");
		ddl.append("\tADD ");
		if (StringUtils.isNotBlank(relation.getName())) {
			ddl.append("CONSTRAINT ");
			ddl.append(filter(relation.getName()));
			ddl.append(" ");
		}
		ddl.append("FOREIGN KEY (");

		boolean first = true;

		for (NormalColumn column : relation.getForeignKeyColumns()) {
			if (!first) {
				ddl.append(", ");

			}
			ddl.append(filter(column.getPhysicalName()));
			first = false;
		}

		ddl.append(")\r\n");
		ddl.append("\tREFERENCES ");
		ddl.append(filter(relation.getSourceTableView().getNameWithSchema(
				getDatabase())));
		ddl.append(" (");

		first = true;

		for (NormalColumn foreignKeyColumn : relation.getForeignKeyColumns()) {
			if (!first) {
				ddl.append(", ");

			}

			ddl.append(filter(foreignKeyColumn.getReferencedColumn(relation)
					.getPhysicalName()));
			first = false;
		}

		ddl.append(")\r\n");
		ddl.append("\tON UPDATE ");
		ddl.append(filter(relation.getOnUpdateAction()));
		ddl.append("\r\n");
		ddl.append("\tON DELETE ");
		ddl.append(filter(relation.getOnDeleteAction()));
		ddl.append("\r\n");

		if (this.semicolon) {
			ddl.append(';');
		}

		return ddl.toString();
	}

	public String getDDL(View view) {
		StringBuilder ddl = new StringBuilder();

		String description = view.getDescription();
		if (this.semicolon && StringUtils.isNotBlank(description)
				&& this.ddlTarget.inlineTableComment) {
			ddl.append("-- ");
			ddl.append(description.replaceAll("\n", "\n-- "));
			ddl.append("\r\n");
		}

		ddl.append(getCreateViewSQL());
		ddl.append(filter(this.getNameWithSchema(view.getTableViewProperties()
				.getSchema(), view.getPhysicalName())));
		ddl.append(" AS ");
		String sql = filter(view.getSql());
		if (sql.endsWith(";")) {
			sql = sql.substring(0, sql.length() - 1);
		}
		ddl.append(sql);

		if (this.semicolon) {
			ddl.append(';');
		}

		return ddl.toString();
	}

	protected String getCreateViewSQL() {
		return "CREATE VIEW ";
	}

	public String getDDL(Trigger trigger) {
		StringBuilder ddl = new StringBuilder();

		String description = trigger.getDescription();
		if (this.semicolon && StringUtils.isNotBlank(description)
				&& this.ddlTarget.inlineTableComment) {
			ddl.append("-- ");
			ddl.append(description.replaceAll("\n", "\n-- "));
			ddl.append("\r\n");
		}

		ddl.append("CREATE TRIGGER ");
		ddl.append(filter(this.getNameWithSchema(trigger.getSchema(),
				trigger.getName())));
		ddl.append(" ");
		ddl.append(filter(trigger.getSql()));

		if (this.semicolon) {
			ddl.append(';');
		}

		return ddl.toString();
	}

	public String getDDL(Sequence sequence) {
		StringBuilder ddl = new StringBuilder();

		String description = sequence.getDescription();
		if (this.semicolon && StringUtils.isNotBlank(description)
				&& this.ddlTarget.inlineTableComment) {
			ddl.append("-- ");
			ddl.append(description.replaceAll("\n", "\n-- "));
			ddl.append("\r\n");
		}

		ddl.append("CREATE SEQUENCE ");
		ddl.append(filter(this.getNameWithSchema(sequence.getSchema(),
				sequence.getName())));
		if (sequence.getIncrement() != null) {
			ddl.append(" INCREMENT ");
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
			ddl.append(" START ");
			ddl.append(sequence.getStart());
		}
		if (sequence.getCache() != null) {
			ddl.append(" CACHE ");
			ddl.append(sequence.getCache());
		}
		if (sequence.isCycle()) {
			ddl.append(" CYCLE");
		}
		if (this.semicolon) {
			ddl.append(';');
		}

		return ddl.toString();
	}

	public String getDropDDL(Index index, ERTable table) {
		StringBuilder ddl = new StringBuilder();

		ddl.append("DROP INDEX ");
		ddl.append(this.getIfExistsOption(Index.class));
		ddl.append(filter(index.getName()));
		if (this.semicolon) {
			ddl.append(';');
		}

		return ddl.toString();
	}

	public String getDropDDL(TableView table, Set<TableView> doneTables) {
		StringBuilder ddl = new StringBuilder();

		doneTables.add(table);

		for (Relation relation : table.getOutgoingRelations()) {
			TableView targetTableView = relation.getTargetTableView();
			if (!doneTables.contains(targetTableView)) {
				doneTables.add(targetTableView);
				String targetTableDDL = this.getDropDDL(targetTableView,
						doneTables);
				ddl.append(targetTableDDL);
			}
		}

		ddl.append("DROP TABLE ");
		ddl.append(this.getIfExistsOption(TableView.class));
		ddl.append(filter(table.getNameWithSchema(getDatabase())));

		if (this.semicolon) {
			ddl.append(';');
		}

		ddl.append("\r\n");

		return ddl.toString();
	}

	public String getDropDDL(View view) {
		StringBuilder ddl = new StringBuilder();

		ddl.append("DROP VIEW ");
		ddl.append(this.getIfExistsOption(View.class));
		ddl.append(filter(this.getNameWithSchema(view.getTableViewProperties()
				.getSchema(), view.getPhysicalName())));
		if (this.semicolon) {
			ddl.append(';');
		}

		return ddl.toString();
	}

	public String getDropDDL(Trigger trigger) {
		StringBuilder ddl = new StringBuilder();

		ddl.append("DROP TRIGGER ");
		ddl.append(this.getIfExistsOption(Trigger.class));
		ddl.append(filter(trigger.getName()));
		if (this.semicolon) {
			ddl.append(';');
		}

		return ddl.toString();
	}

	public String getDropDDL(Tablespace tablespace) {
		StringBuilder ddl = new StringBuilder();

		ddl.append("DROP TABLESPACE ");
		ddl.append(this.getIfExistsOption(Tablespace.class));
		ddl.append(filter(tablespace.getName()));
		if (this.semicolon) {
			ddl.append(';');
		}

		return ddl.toString();
	}

	public String getDropDDL(Sequence sequence) {
		StringBuilder ddl = new StringBuilder();

		ddl.append("DROP SEQUENCE ");
		ddl.append(this.getIfExistsOption(Sequence.class));
		ddl.append(filter(this.getNameWithSchema(sequence.getSchema(),
				sequence.getName())));
		if (this.semicolon) {
			ddl.append(';');
		}

		return ddl.toString();
	}

	protected String filter(String str) {
		if (str == null) {
			return "";
		}

		Settings settings = diagram.getDiagramContents().getSettings();

		if (settings.isCapital()) {
			return str.toUpperCase();
		}

		return str;
	}

	protected DBManager getDBManager() {
		return DBManagerFactory.getDBManager(this.diagram);
	}

	protected ERDiagram getDiagram() {
		return diagram;
	}

	protected String getNameWithSchema(String schema, String name) {
		StringBuilder sb = new StringBuilder();

		if (StringUtils.isEmpty(schema)) {
			schema = this.getDiagram().getDiagramContents().getSettings()
					.getTableViewProperties().getSchema();
		}

		if (StringUtils.isNotEmpty(schema)) {
			sb.append(schema);
			sb.append('.');
		}

		sb.append(name);

		return sb.toString();
	}

	public String getIfExistsOption(final Class<?> clazz) {
		return "";
	}

	protected String filterComment(String logicalName, String description,
			boolean column) {
		String comment = null;

		if (this.ddlTarget.commentValueLogicalNameDescription) {
			comment = Format.null2blank(logicalName);

			if (StringUtils.isNotBlank(description)) {
				comment = comment + " : " + Format.null2blank(description);
			}

		} else if (this.ddlTarget.commentValueLogicalName) {
			comment = Format.null2blank(logicalName);

		} else {
			comment = Format.null2blank(description);

		}

		if (ddlTarget.commentReplaceLineFeed) {
			comment = comment.replaceAll("\r\n",
					Format.null2blank(ddlTarget.commentReplaceString));
			comment = comment.replaceAll("\r",
					Format.null2blank(ddlTarget.commentReplaceString));
			comment = comment.replaceAll("\n",
					Format.null2blank(ddlTarget.commentReplaceString));
		}

		return comment;
	}

	protected final String getDatabase() {
		return this.diagram.getDatabase();
	}
}
