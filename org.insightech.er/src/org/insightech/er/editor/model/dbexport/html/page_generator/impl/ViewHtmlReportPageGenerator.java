package org.insightech.er.editor.model.dbexport.html.page_generator.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.html.page_generator.AbstractHtmlReportPageGenerator;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.util.Format;

public class ViewHtmlReportPageGenerator extends
		AbstractHtmlReportPageGenerator<View> {

	public ViewHtmlReportPageGenerator(Map<Object, Integer> idMap) {
		super(idMap);
	}

	public String getType() {
		return "view";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<View> getObjectList(ERDiagram diagram) {
		return diagram.getDiagramContents().getContents().getViewSet()
				.getList();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws IOException
	 */
	@Override
	public String[] getContentArgs(ERDiagram diagram, View view)
			throws IOException {		String description = Format.null2blank(view.getDescription());

		List<NormalColumn> normalColumnList = view.getExpandedColumns();

		String attributeTable = this.generateAttributeTable(diagram,
				normalColumnList);

		List<NormalColumn> foreignKeyList = new ArrayList<NormalColumn>();
		for (NormalColumn normalColumn : normalColumnList) {
			if (normalColumn.isForeignKey()) {
				foreignKeyList.add(normalColumn);
			}
		}

		String foreignKeyTable = this.generateForeignKeyTable(foreignKeyList);

		String attributeDetailTable = this.generateAttributeDetailTable(
				diagram, normalColumnList);

		return new String[] { Format.null2blank(description),
				Format.null2blank(view.getPhysicalName()),
				Format.null2blank(view.getSql()), attributeTable,
				foreignKeyTable, attributeDetailTable };
	}

	public String getObjectName(View view) {
		return view.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getObjectSummary(View view) {
		return view.getDescription();
	}
}
