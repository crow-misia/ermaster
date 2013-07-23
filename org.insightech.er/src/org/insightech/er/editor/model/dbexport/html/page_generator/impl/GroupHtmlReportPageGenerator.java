package org.insightech.er.editor.model.dbexport.html.page_generator.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.html.page_generator.AbstractHtmlReportPageGenerator;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;

public class GroupHtmlReportPageGenerator extends
		AbstractHtmlReportPageGenerator<ColumnGroup> {

	public GroupHtmlReportPageGenerator(Map<Object, Integer> idMap) {
		super(idMap);
	}

	public String getType() {
		return "group";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ColumnGroup> getObjectList(ERDiagram diagram) {
		return diagram.getDiagramContents().getGroups().getGroupList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getContentArgs(ERDiagram diagram, ColumnGroup columnGroup)
			throws IOException {
		List<NormalColumn> normalColumnList = columnGroup.getColumns();

		String attributeTable = this.generateAttributeTable(diagram,
				normalColumnList);

		List<TableView> usedTableList = columnGroup.getUsedTableList(diagram);

		String usedTableTable = this.generateUsedTableTable(usedTableList);

		String attributeDetailTable = this.generateAttributeDetailTable(
				diagram, normalColumnList);

		return new String[] { attributeTable, usedTableTable,
				attributeDetailTable };
	}

	public String getObjectName(ColumnGroup columnGroup) {
		return columnGroup.getGroupName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getObjectSummary(ColumnGroup object) {
		return null;
	}

}
