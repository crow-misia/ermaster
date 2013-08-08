package org.insightech.er.editor.model.dbexport.excel.sheet_generator;

import java.util.Map;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.eclipse.core.runtime.IProgressMonitor;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.ObjectModel;
import org.insightech.er.editor.model.dbexport.excel.ExportToExcelManager.LoopDefinition;
import org.insightech.er.util.POIUtils;
import org.insightech.er.util.POIUtils.CellLocation;

public class SheetIndexSheetGenerator extends AbstractSheetGenerator {

	// シートタイプ
	private static final String KEYWORD_SHEET_TYPE = "$SHTT";

	// 名前
	private static final String KEYWORD_NAME = "$NAM";

	// 説明
	private static final String KEYWORD_DESCRIPTION = "$DSC";

	private static final String[] FIND_KEYWORDS_LIST = { KEYWORD_SHEET_TYPE,
			KEYWORD_NAME, KEYWORD_DESCRIPTION };

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void generate(IProgressMonitor monitor, Workbook workbook,
			int sheetNo, boolean useLogicalNameAsSheetName,
			Map<String, Integer> sheetNameMap,
			Map<String, ObjectModel> sheetObjectMap, ERDiagram diagram,
			Map<String, LoopDefinition> loopDefinitionMap) {

		Sheet sheet = workbook.getSheetAt(sheetNo);

		this.setSheetListData(workbook, sheet, sheetObjectMap, diagram);
		monitor.worked(1);
	}

	public void setSheetListData(Workbook workbook, Sheet sheet,
			Map<String, ObjectModel> sheetObjectMap, ERDiagram diagram) {
		CellLocation cellLocation = POIUtils
				.findCell(sheet, FIND_KEYWORDS_LIST);

		if (cellLocation != null) {
			int rowNum = cellLocation.r;
			Row templateRow = sheet.getRow(rowNum);

			ColumnTemplate columnTemplate = this.loadColumnTemplate(workbook,
					sheet, cellLocation);
			int order = 1;

			Font linkCellFont = null;
			int linkCol = -1;

			for (Map.Entry<String, ObjectModel> entry : sheetObjectMap
					.entrySet()) {
				String sheetName = entry.getKey();
				ObjectModel objectModel = entry.getValue();

				Row row = POIUtils.insertRow(sheet, rowNum++);

				for (int columnNum : columnTemplate.columnTemplateMap.keySet()) {
					Cell cell = row.createCell(columnNum);
					String template = columnTemplate.columnTemplateMap
							.get(columnNum);

					String value = null;
					if (KEYWORD_ORDER.equals(template)) {
						value = String.valueOf(order);

					} else {
						if (KEYWORD_SHEET_TYPE.equals(template)) {
							value = ResourceString
									.getResourceString("label.object.type."
											+ objectModel.getObjectType());

						} else if (KEYWORD_NAME.equals(template)) {
							value = sheetName;
							Hyperlink link = workbook.getCreationHelper().createHyperlink(Hyperlink.LINK_DOCUMENT);
							link.setAddress("'" + sheetName + "'!A1");
							cell.setHyperlink(link);

							if (linkCellFont == null) {
								linkCol = columnNum;

								linkCellFont = workbook.createFont();

								linkCellFont.setColor(HSSFColor.BLUE.index);
								linkCellFont.setUnderline(Font.U_SINGLE);
							}

						} else if (KEYWORD_DESCRIPTION.equals(template)) {
							value = objectModel.getDescription();
						}

						cell.setCellValue(value);
					}

					order++;
				}
			}

			this.setCellStyle(columnTemplate, sheet, cellLocation.r, rowNum
					- cellLocation.r, templateRow.getFirstCellNum());

			if (linkCol != -1) {
				for (int row = cellLocation.r; row < rowNum; row++) {
					Cell cell = sheet.getRow(row).getCell(linkCol);
					cell.getCellStyle().setFont(linkCellFont);
				}
			}
		}
	}

	public String getSheetName() {
		String name = this.keywordsValueMap.get(KEYWORD_SHEET_NAME);

		if (name == null) {
			name = "List of sheets";
		}

		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTemplateSheetName() {
		return "sheet_index_template";
	}

	@Override
	public String[] getKeywords() {
		return new String[] { KEYWORD_SHEET_TYPE, KEYWORD_NAME,
				KEYWORD_DESCRIPTION, KEYWORD_ORDER, KEYWORD_SHEET_NAME };
	}

	@Override
	public int getKeywordsColumnNo() {
		return 24;
	}

	@Override
	public int count(ERDiagram diagram) {
		return 1;
	}
}
