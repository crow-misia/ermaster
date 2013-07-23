package org.insightech.er.editor.model.dbexport.html.page_generator.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.html.page_generator.AbstractHtmlReportPageGenerator;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;

public class CategoryHtmlReportPageGenerator extends
		AbstractHtmlReportPageGenerator<Category> {

	public CategoryHtmlReportPageGenerator(Map<Object, Integer> idMap) {
		super(idMap);
	}

	public String getType() {
		return "category";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Category> getObjectList(ERDiagram diagram) {
		return diagram.getDiagramContents().getSettings()
				.getCategorySetting().getSelectedCategories();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getContentArgs(ERDiagram diagram, Category category)
			throws IOException {
		List<TableView> usedTableList = category.getTableViewContents();

		String usedTableTable = this.generateUsedTableTable(usedTableList);

		return new String[] { usedTableTable };
	}

	public String getObjectName(Category category) {
		return category.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getObjectSummary(Category category) {
		return null;
	}

}
