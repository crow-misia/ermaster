package org.insightech.er.editor.model.dbexport.excel.sheet_generator;

import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.eclipse.core.runtime.IProgressMonitor;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.ObjectModel;
import org.insightech.er.editor.model.dbexport.excel.ExportToExcelManager.LoopDefinition;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeSet;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.util.POIUtils;

public class AllIndicesSheetGenerator extends IndexSheetGenerator {

	@Override
	public void generate(IProgressMonitor monitor, HSSFWorkbook workbook,
			int sheetNo, boolean useLogicalNameAsSheetName,
			Map<String, Integer> sheetNameMap,
			Map<String, ObjectModel> sheetObjectMap, ERDiagram diagram,
			Map<String, LoopDefinition> loopDefinitionMap) {

		LoopDefinition loopDefinition = loopDefinitionMap.get(this
				.getTemplateSheetName());

		HSSFSheet newSheet = this.createNewSheet(workbook, sheetNo,
				loopDefinition.sheetName, sheetNameMap);

		sheetObjectMap.put(workbook.getSheetName(workbook
				.getSheetIndex(newSheet)), diagram.getDiagramContents()
				.getIndexSet());

		HSSFSheet oldSheet = workbook.getSheetAt(sheetNo);

		NodeSet nodeSet = null;

		if (diagram.getCurrentCategory() != null) {
			nodeSet = diagram.getCurrentCategory().getContentsAsNodeSet();
		} else {
			nodeSet = diagram.getDiagramContents().getContents();
		}

		boolean first = true;

		for (NodeElement nodeElement : nodeSet) {

			if (nodeElement instanceof ERTable) {

				ERTable table = (ERTable) nodeElement;

				for (Index index : table.getIndexes()) {
					if (first) {
						first = false;

					} else {
						POIUtils.copyRow(oldSheet, newSheet,
								loopDefinition.startLine - 1, oldSheet
										.getLastRowNum(), newSheet
										.getLastRowNum()
										+ loopDefinition.spaceLine + 1);
					}

					this.setIndexData(workbook, newSheet, index);

					newSheet.setRowBreak(newSheet.getLastRowNum()
							+ loopDefinition.spaceLine);

					monitor.worked(1);
				}
			}
		}

		if (first) {
			for (int i = loopDefinition.startLine - 1; i <= newSheet
					.getLastRowNum(); i++) {
				HSSFRow row = newSheet.getRow(i);
				if (row != null) {
					newSheet.removeRow(row);
				}
			}
		}
	}

	@Override
	public String getTemplateSheetName() {
		return "all_indices_template";
	}

}
