package org.insightech.er.editor.model.dbexport.excel.sheet_generator;

import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.eclipse.core.runtime.IProgressMonitor;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.ObjectModel;
import org.insightech.er.editor.model.dbexport.excel.ExportToExcelManager.LoopDefinition;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.util.POIUtils;

public class AllViewSheetGenerator extends ViewSheetGenerator {

	@Override
	public void generate(IProgressMonitor monitor, Workbook workbook,
			int sheetNo, boolean useLogicalNameAsSheetName,
			Map<String, Integer> sheetNameMap,
			Map<String, ObjectModel> sheetObjectMap, ERDiagram diagram,
			Map<String, LoopDefinition> loopDefinitionMap) {
		this.clear();
		
		LoopDefinition loopDefinition = loopDefinitionMap.get(this
				.getTemplateSheetName());

		Sheet newSheet = createNewSheet(workbook, sheetNo,
				loopDefinition.sheetName, sheetNameMap);

		sheetObjectMap.put(workbook.getSheetName(workbook
				.getSheetIndex(newSheet)), diagram.getDiagramContents()
				.getContents().getViewSet());

		Sheet oldSheet = workbook.getSheetAt(sheetNo);

		boolean first = true;

		for (View view : diagram.getDiagramContents().getContents()
				.getViewSet()) {
			if (first) {
				first = false;

			} else {
				POIUtils
						.copyRow(oldSheet, newSheet,
								loopDefinition.startLine - 1, oldSheet
										.getLastRowNum(), newSheet
										.getLastRowNum()
										+ loopDefinition.spaceLine + 1);
			}

			this.setViewData(workbook, newSheet, view);

			newSheet.setRowBreak(newSheet.getLastRowNum()
					+ loopDefinition.spaceLine);

			monitor.worked(1);
		}

		if (first) {
			for (int i = loopDefinition.startLine - 1, n = newSheet
					.getLastRowNum(); i <= n; i++) {
				Row row = newSheet.getRow(i);
				if (row != null) {
					newSheet.removeRow(row);
				}
			}
		}
	}

	@Override
	public String getTemplateSheetName() {
		return "all_view_template";
	}

}
