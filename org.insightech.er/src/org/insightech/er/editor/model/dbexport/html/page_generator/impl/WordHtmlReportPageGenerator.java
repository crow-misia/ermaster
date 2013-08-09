package org.insightech.er.editor.model.dbexport.html.page_generator.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.html.page_generator.AbstractHtmlReportPageGenerator;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.ColumnHolder;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.util.Format;

public class WordHtmlReportPageGenerator extends
		AbstractHtmlReportPageGenerator<Word> {

	public WordHtmlReportPageGenerator(Map<Object, Integer> idMap) {
		super(idMap);
	}

	public String getType() {
		return "word";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Word> getObjectList(ERDiagram diagram) {
		return diagram.getDiagramContents().getDictionary().getWordList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getContentArgs(ERDiagram diagram, Word word)
			throws IOException {
		String logicalName = word.getLogicalName();
		String physicalName = word.getPhysicalName();
		String type = "";
		if (word.getType() != null) {
			type = Format.formatType(word.getType(), word.getTypeData(),
					diagram.getDatabase());
		}

		String description = word.getDescription();

		List<TableView> usedTableList = new ArrayList<TableView>();

		List<NormalColumn> normalColumnList = diagram.getDiagramContents()
				.getDictionary().getColumnList(word);
		for (NormalColumn normalColumn : normalColumnList) {
			ColumnHolder columnHolder = normalColumn.getColumnHolder();
			if (columnHolder instanceof TableView) {
				usedTableList.add((TableView) columnHolder);

			} else {
				ColumnGroup columnGroup = (ColumnGroup) columnHolder;
				usedTableList.addAll(columnGroup.getUsedTableList(diagram));
			}
		}

		String usedTableTable = this.generateUsedTableTable(usedTableList);

		return new String[] { logicalName, physicalName, type, description,
				usedTableTable };
	}

	public String getObjectName(Word word) {
		return word.getLogicalName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getObjectSummary(Word word) {
		return null;
	}
}
