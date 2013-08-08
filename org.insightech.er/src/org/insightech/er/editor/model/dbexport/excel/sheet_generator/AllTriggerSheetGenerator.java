package org.insightech.er.editor.model.dbexport.excel.sheet_generator;

import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.eclipse.core.runtime.IProgressMonitor;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.ObjectModel;
import org.insightech.er.editor.model.dbexport.excel.ExportToExcelManager.LoopDefinition;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.insightech.er.util.POIUtils;

public class AllTriggerSheetGenerator extends TriggerSheetGenerator {

	@Override
	public void generate(IProgressMonitor monitor, Workbook workbook,
			int sheetNo, boolean useLogicalNameAsSheetName,
			Map<String, Integer> sheetNameMap,
			Map<String, ObjectModel> sheetObjectMap, ERDiagram diagram,
			Map<String, LoopDefinition> loopDefinitionMap) {

		LoopDefinition loopDefinition = loopDefinitionMap.get(this
				.getTemplateSheetName());

		Sheet newSheet = createNewSheet(workbook, sheetNo,
				loopDefinition.sheetName, sheetNameMap);

		sheetObjectMap.put(workbook.getSheetName(workbook
				.getSheetIndex(newSheet)), diagram.getDiagramContents()
				.getTriggerSet());

		Sheet oldSheet = workbook.getSheetAt(sheetNo);

		boolean first = true;

		for (Trigger trigger : diagram.getDiagramContents().getTriggerSet()) {
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

			this.setTriggerData(workbook, newSheet, trigger);

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
		return "all_trigger_template";
	}

}
