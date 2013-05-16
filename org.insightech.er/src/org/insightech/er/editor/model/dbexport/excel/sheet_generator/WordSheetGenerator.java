package org.insightech.er.editor.model.dbexport.excel.sheet_generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.eclipse.core.runtime.IProgressMonitor;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.ObjectModel;
import org.insightech.er.editor.model.dbexport.excel.ExportToExcelManager.LoopDefinition;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.ColumnSet;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.util.POIUtils;
import org.insightech.er.util.POIUtils.CellLocation;

public class WordSheetGenerator extends AbstractSheetGenerator {

	private static final String[] KEYWORDS_OF_COLUMN = {
		KEYWORD_ORDER,
		KEYWORD_LOGICAL_COLUMN_NAME, KEYWORD_PHYSICAL_COLUMN_NAME,
		KEYWORD_TYPE, KEYWORD_LENGTH, KEYWORD_DECIMAL,
		KEYWORD_DESCRIPTION,
	};

	private ColumnTemplate columnTemplate;

	private void clear() {
		this.columnTemplate = null;
	}

	public void setAllColumnsData(HSSFWorkbook workbook, HSSFSheet sheet,
			ERDiagram diagram) {
		this.clear();

		CellLocation cellLocation = POIUtils.findCell(sheet,
				FIND_KEYWORDS_OF_COLUMN);

		if (cellLocation != null) {
			int rowNum = cellLocation.r;
			HSSFRow templateRow = sheet.getRow(rowNum);

			if (this.columnTemplate == null) {
				this.columnTemplate = loadColumnTemplate(workbook, sheet,
						cellLocation);
			}

			int order = 1;

			// 選択されたカテゴリに属する単語のみを抽出する
			List<Word> wordList;
			if (diagram.getCurrentCategory() == null) {
				wordList = diagram.getDiagramContents().getDictionary().getWordList();
			} else {
				final Set<Word> check = new HashSet<Word>();

				for (ERTable table : diagram.getDiagramContents().getContents()
						.getTableSet()) {

					if (diagram.getCurrentCategory() != null
							&& !diagram.getCurrentCategory().contains(table)) {
						continue;
					}
	
					for (NormalColumn normalColumn : table.getExpandedColumns()) {
						check.add(normalColumn.getWord());
					}
				}

				wordList = new ArrayList<Word>(check);
			}
			
			// 物理名でソートする
			Collections.sort(wordList, Word.PHYSICAL_NAME_COMPARATOR);

			final String database = diagram.getDatabase();
			for (final Word word : wordList) {
				HSSFRow row = POIUtils.insertRow(sheet, rowNum++);
				setColumnData(this.keywordsValueMap, columnTemplate,
						row, word, database, order);
				order++;
			}

			setCellStyle(columnTemplate, sheet, cellLocation.r, rowNum
					- cellLocation.r, templateRow.getFirstCellNum());
		}
	}

	protected static void setColumnData(Map<String, String> keywordsValueMap,
			ColumnTemplate columnTemplate, HSSFRow row,
			Word word, String database, int order) {

		for (int columnNum : columnTemplate.columnTemplateMap.keySet()) {
			HSSFCell cell = row.createCell(columnNum);
			String template = columnTemplate.columnTemplateMap.get(columnNum);

			String value = null;
			if (KEYWORD_ORDER.equals(template)) {
				value = String.valueOf(order);

			} else {
				value = getColumnValue(keywordsValueMap, word, database, template);
			}

			try {
				double num = Double.parseDouble(value);
				cell.setCellValue(num);

			} catch (NumberFormatException e) {
				HSSFRichTextString text = new HSSFRichTextString(value);
				cell.setCellValue(text);
			}
		}
	}

	private static String getColumnValue(Map<String, String> keywordsValueMap,
			Word word, String database, String template) {
		String str = template;

		for (String keyword : KEYWORDS_OF_COLUMN) {
			str = str.replaceAll("\\" + keyword, getKeywordValue(
					keywordsValueMap, word, database, keyword));
		}

		return str;
	}

	protected static String getKeywordValue(Map<String, String> keywordsValueMap,
			Word word, String database, String keyword) {
		Object obj = null;

		if (KEYWORD_LOGICAL_COLUMN_NAME.equals(keyword)) {
			obj = word.getLogicalName();

		} else if (KEYWORD_PHYSICAL_COLUMN_NAME.equals(keyword)) {
			obj = word.getPhysicalName();

		} else if (KEYWORD_TYPE.equals(keyword) &&
			word.getType() != null) {
			obj = word.getType().getAlias(database);
		} else if (KEYWORD_LENGTH.equals(keyword)) {
			obj = word.getTypeData().getLength();

		} else if (KEYWORD_DECIMAL.equals(keyword)) {
			obj = word.getTypeData().getDecimal();

		} else if (KEYWORD_DESCRIPTION.equals(keyword)) {
			obj = word.getDescription();

		}

		return getValue(keywordsValueMap, keyword, obj);
	}

	public String getSheetName() {
		String name = this.keywordsValueMap.get(KEYWORD_SHEET_NAME);

		if (name == null) {
			name = "Word";
		}

		return name;
	}

	@Override
	public void generate(IProgressMonitor monitor, HSSFWorkbook workbook,
			int sheetNo, boolean useLogicalNameAsSheetName,
			Map<String, Integer> sheetNameMap,
			Map<String, ObjectModel> sheetObjectMap, ERDiagram diagram,
			Map<String, LoopDefinition> loopDefinitionMap) {
		String name = this.getSheetName();
		HSSFSheet newSheet = createNewSheet(workbook, sheetNo, name,
				sheetNameMap);

		sheetObjectMap.put(workbook.getSheetName(workbook
				.getSheetIndex(newSheet)), new ColumnSet());

		this.setAllColumnsData(workbook, newSheet, diagram);
		monitor.worked(1);
	}

	@Override
	public String getTemplateSheetName() {
		return "word_template";
	}

	@Override
	public int getKeywordsColumnNo() {
		return 36;
	}

	@Override
	public String[] getKeywords() {
		return new String[] {
				KEYWORD_ORDER, KEYWORD_LOGICAL_COLUMN_NAME,
				KEYWORD_PHYSICAL_COLUMN_NAME, KEYWORD_TYPE, KEYWORD_LENGTH,
				KEYWORD_DECIMAL,
				KEYWORD_DESCRIPTION, KEYWORD_SHEET_NAME };
	}

	@Override
	public int count(ERDiagram diagram) {
		return 1;
	}

}
