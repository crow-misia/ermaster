package org.insightech.er.db.impl.mysql;

import java.util.List;

import org.insightech.er.db.DBManager;
import org.insightech.er.db.impl.mysql.tablespace.MySQLTablespaceProperties;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.util.Check;
import org.insightech.er.util.Format;

public class MySQLDDLCreator extends DDLCreator {

	public MySQLDDLCreator(ERDiagram diagram, boolean semicolon) {
		super(diagram, semicolon);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPostDDL(ERTable table) {
		MySQLTableProperties commonTableProperties = (MySQLTableProperties) this
				.getDiagram().getDiagramContents().getSettings()
				.getTableViewProperties();

		MySQLTableProperties tableProperties = (MySQLTableProperties) table
				.getTableViewProperties();

		String engine = tableProperties.getStorageEngine();
		if (engine == null || engine.equals("")) {
			engine = commonTableProperties.getStorageEngine();
		}
		String characterSet = tableProperties.getCharacterSet();
		if (characterSet == null || characterSet.equals("")) {
			characterSet = commonTableProperties.getCharacterSet();
		}

		StringBuffer postDDL = new StringBuffer();
		if (engine != null && !engine.equals("")) {
			postDDL.append(" ENGINE = ");
			postDDL.append(engine);
		}

		if (this.ddlTarget.createComment) {
			String comment = this.filterComment(table.getLogicalName(), table
					.getDescription(), false);

			if (!Check.isEmpty(comment)) {
				postDDL.append(" COMMENT = '");
				postDDL.append(comment.replaceAll("'", "''"));
				postDDL.append("'");
			}
		}

		if (characterSet != null && !characterSet.equals("")) {
			postDDL.append(" DEFAULT CHARACTER SET ");
			postDDL.append(characterSet);
		}

		postDDL.append(super.getPostDDL(table));

		return postDDL.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getColulmnDDL(NormalColumn normalColumn) {
		StringBuffer ddl = new StringBuffer();

		ddl.append(super.getColulmnDDL(normalColumn));

		if (normalColumn.isAutoIncrement()) {
			ddl.append(" AUTO_INCREMENT");
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

		return ddl.toString();
	}

	@Override
	protected boolean doesNeedQuoteDefaultValue(NormalColumn normalColumn) {
		if (!super.doesNeedQuoteDefaultValue(normalColumn)) {
			return false;
		}

		if ("CURRENT_TIMESTAMP".equalsIgnoreCase(normalColumn.getDefaultValue()
				.trim())) {
			return false;
		}

		return true;
	}

	@Override
	protected String getDDL(Tablespace tablespace) {
		MySQLTablespaceProperties tablespaceProperties = (MySQLTablespaceProperties) tablespace
				.getProperties(this.environment, this.getDiagram());

		StringBuffer ddl = new StringBuffer();

		ddl.append("CREATE TABLESPACE ");
		ddl.append(filter(tablespace.getName()));
		ddl.append("\r\n");
		ddl.append(" ADD DATAFILE '");
		ddl.append(tablespaceProperties.getDataFile());
		ddl.append("'\r\n");
		ddl.append(" USE LOGFILE GROUP ");
		ddl.append(tablespaceProperties.getLogFileGroup());
		ddl.append("\r\n");

		if (!Check.isEmpty(tablespaceProperties.getExtentSize())) {
			ddl.append(" EXTENT_SIZE ");
			ddl.append(tablespaceProperties.getExtentSize());
			ddl.append("\r\n");
		}

		ddl.append(" INITIAL_SIZE ");
		ddl.append(tablespaceProperties.getInitialSize());
		ddl.append("\r\n");
		ddl.append(" ENGINE ");
		ddl.append(tablespaceProperties.getEngine());
		ddl.append("\r\n");

		if (this.semicolon) {
			ddl.append(";");
		}

		return ddl.toString();
	}

	@Override
	protected String filterComment(String logicalName, String description,
			boolean column) {
		String comment = null;

		if (this.ddlTarget.commentValueLogicalNameDescription) {
			comment = Format.null2blank(logicalName);

			if (!Check.isEmpty(description)) {
				comment = comment + " : " + Format.null2blank(description);
			}

		} else if (this.ddlTarget.commentValueLogicalName) {
			comment = Format.null2blank(logicalName);

		} else {
			comment = Format.null2blank(description);

		}

		if (ddlTarget.commentReplaceLineFeed) {
			comment = comment.replaceAll("\r\n", Format
					.null2blank(ddlTarget.commentReplaceString));
			comment = comment.replaceAll("\r", Format
					.null2blank(ddlTarget.commentReplaceString));
			comment = comment.replaceAll("\n", Format
					.null2blank(ddlTarget.commentReplaceString));
		}

		int maxLength = 60;

		if (column) {
			maxLength = 255;
		}

		if (comment.length() > maxLength) {
			comment = comment.substring(0, maxLength);
		}

		return comment;
	}

	@Override
	public String getDDL(Index index, ERTable table) {
		StringBuffer ddl = new StringBuffer();

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
		ddl.append("INDEX ");
		ddl.append(filter(index.getName()));

		if (index.getType() != null && !index.getType().trim().equals("")) {
			ddl.append(" USING ");
			ddl.append(index.getType().trim());
		}

		ddl.append(" ON ");
		ddl.append(filter(table.getNameWithSchema(this.getDiagram()
				.getDatabase())));

		ddl.append(" (");
		boolean first = true;

		int i = 0;
		List<Boolean> descs = index.getDescs();

		for (NormalColumn column : index.getColumns()) {
			if (!first) {
				ddl.append(", ");

			}

			ddl.append(filter(column.getPhysicalName()));

			if (this.getDBManager().isSupported(DBManager.SUPPORT_DESC_INDEX)) {
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
			ddl.append(";");
		}

		return ddl.toString();
	}
	
	@Override
	public String getDropDDL(Index index, ERTable table) {
		StringBuffer ddl = new StringBuffer();

		ddl.append("DROP INDEX ");
		ddl.append(this.getIfExistsOption());
		ddl.append(filter(index.getName()));
		ddl.append(" ON ");
		ddl.append(filter(table.getNameWithSchema(this.getDiagram()
				.getDatabase())));
		
		if (this.semicolon) {
			ddl.append(";");
		}

		return ddl.toString();
	}

}