package org.insightech.er.editor.model.dbexport.html.page_generator.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.html.page_generator.AbstractHtmlReportPageGenerator;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;

public class TableHtmlReportPageGenerator extends
		AbstractHtmlReportPageGenerator<ERTable> {

	public TableHtmlReportPageGenerator(Map<Object, Integer> idMap) {
		super(idMap);
	}

	public String getType() {
		return "table";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ERTable> getObjectList(ERDiagram diagram) {
		return diagram.getDiagramContents().getContents().getTableSet()
				.getList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getContentArgs(ERDiagram diagram, ERTable table)
			throws IOException {
		String description = table.getDescription();

		List<NormalColumn> normalColumnList = table.getExpandedColumns();

		String attributeTable = this.generateAttributeTable(diagram,
				normalColumnList);

		List<NormalColumn> foreignKeyList = new ArrayList<NormalColumn>();
		for (NormalColumn normalColumn : normalColumnList) {
			if (normalColumn.isForeignKey()) {
				foreignKeyList.add(normalColumn);
			}
		}

		String foreignKeyTable = this.generateForeignKeyTable(foreignKeyList);

		List<NormalColumn> referencedKeyList = new ArrayList<NormalColumn>();
		for (Relation relation : table.getOutgoingRelations()) {
			referencedKeyList.addAll(relation.getForeignKeyColumns());
		}

		String referencedKeyTable = this
				.generateReferenceKeyTable(referencedKeyList);

		String complexUniqueKeyMatrix = this.generateComplexUniqueKeyMatrix(
				table.getComplexUniqueKeyList(), normalColumnList);

		List<Index> indexList = table.getIndexes();

		String indexSummaryTable = this.generateIndexSummaryTable(indexList);

		String indexMatrix = this.generateIndexMatrix(indexList,
				normalColumnList);

		String attributeDetailTable = this.generateAttributeDetailTable(
				diagram, normalColumnList);

		return new String[] { StringUtils.defaultString(description),
				StringUtils.defaultString(table.getPhysicalName()),
				StringUtils.defaultString(table.getConstraint()), attributeTable,
				foreignKeyTable, referencedKeyTable, complexUniqueKeyMatrix,
				indexSummaryTable, indexMatrix, attributeDetailTable };
	}

	public String getObjectName(ERTable table) {
		return table.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getObjectSummary(ERTable table) {
		return table.getDescription();
	}
}
