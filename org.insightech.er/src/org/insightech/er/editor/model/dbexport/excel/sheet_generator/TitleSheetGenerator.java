package org.insightech.er.editor.model.dbexport.excel.sheet_generator;

import java.text.SimpleDateFormat;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.eclipse.core.runtime.IProgressMonitor;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.ObjectModel;
import org.insightech.er.editor.model.StringObjectModel;
import org.insightech.er.editor.model.dbexport.excel.ExportToExcelManager.LoopDefinition;
import org.insightech.er.editor.model.diagram_contents.element.node.model_properties.ModelProperties;
import org.insightech.er.util.POIUtils;

public class TitleSheetGenerator extends AbstractSheetGenerator {

	// プロジェクト名
	private static final String KEYWORD_PROJECT_NAME = "$PRJN";

	// モデル名
	private static final String KEYWORD_MODEL_NAME = "$MDLN";

	// バージョン
	private static final String KEYWORD_VERSION = "$VER";

	// 会社名
	private static final String KEYWORD_COMPANY_NAME = "$CMPN";

	// 部署名
	private static final String KEYWORD_DEPARTMENT_NAME = "$DEPN";

	// 作成者
	private static final String KEYWORD_AUTHOR = "$AUTH";

	// 作成日時
	private static final String KEYWORD_CREATE_DATE = "$CRDT";

	// 更新者
	private static final String KEYWORD_UPDATER = "$UPDR";

	// 作成日時
	private static final String KEYWORD_UPDATE_DATE = "$UPDT";

	// 日付フォーマット
	private static final String KEYWORD_DATE_FORMAT = "$FMT";

	@Override
	public void generate(IProgressMonitor monitor, HSSFWorkbook workbook,
			int sheetNo, boolean useLogicalNameAsSheetName,
			Map<String, Integer> sheetNameMap,
			Map<String, ObjectModel> sheetObjectMap, ERDiagram diagram,
			Map<String, LoopDefinition> loopDefinitionMap) {

		String sheetName = this.getSheetName();

		HSSFSheet newSheet = createNewSheet(workbook, sheetNo, sheetName,
				sheetNameMap);

		sheetObjectMap.put(workbook.getSheetName(workbook
				.getSheetIndex(newSheet)), new StringObjectModel(sheetName));

		this.setPropertyListData(workbook, newSheet, sheetObjectMap, diagram);
		monitor.worked(1);
	}

	public void setPropertyListData(HSSFWorkbook workbook, HSSFSheet sheet,
			Map<String, ObjectModel> sheetObjectMap, ERDiagram diagram) {
		final ModelProperties properties = diagram.getDiagramContents()
				.getSettings().getModelProperties();
		final Map<String, String> map = properties.getMap();

		final SimpleDateFormat format = new SimpleDateFormat(
				this.keywordsValueMap.get(KEYWORD_DATE_FORMAT));

		POIUtils.replace(sheet, KEYWORD_PROJECT_NAME,
				map.get(ModelProperties.KEY_PROJECT_NAME));

		POIUtils.replace(sheet, KEYWORD_MODEL_NAME,
				map.get(ModelProperties.KEY_MODEL_NAME));

		POIUtils.replace(sheet, KEYWORD_VERSION,
				map.get(ModelProperties.KEY_VERSION));

		POIUtils.replace(sheet, KEYWORD_COMPANY_NAME,
				map.get(ModelProperties.KEY_COMPANY_NAME));

		POIUtils.replace(sheet, KEYWORD_DEPARTMENT_NAME,
				map.get(ModelProperties.KEY_DEPARTMENT_NAME));

		POIUtils.replace(sheet, KEYWORD_AUTHOR,
				map.get(ModelProperties.KEY_AUTHOR));

		POIUtils.replace(sheet, KEYWORD_UPDATER,
				map.get(ModelProperties.KEY_UPDATER));

		POIUtils.replace(sheet, KEYWORD_CREATE_DATE,
				getDateString(format, properties.getCreationDate()));

        POIUtils.replace(sheet, KEYWORD_UPDATE_DATE,
                getDateString(format, properties.getUpdatedDate()));
	}

	public String getSheetName() {
		String name = this.keywordsValueMap.get(KEYWORD_SHEET_NAME);

		if (name == null) {
			name = "Title";
		}

		return name;
	}

	@Override
	public String getTemplateSheetName() {
		return "title_template";
	}

	@Override
	public String[] getKeywords() {
		return new String[] {
		        KEYWORD_PROJECT_NAME, KEYWORD_MODEL_NAME, KEYWORD_VERSION,
		        KEYWORD_COMPANY_NAME, KEYWORD_DEPARTMENT_NAME,
		        KEYWORD_AUTHOR, KEYWORD_CREATE_DATE,
		        KEYWORD_UPDATER, KEYWORD_UPDATE_DATE,
				KEYWORD_DATE_FORMAT, KEYWORD_SHEET_NAME };
	}

	@Override
	public int getKeywordsColumnNo() {
		return 32;
	}

	@Override
	public int count(ERDiagram diagram) {
		return 1;
	}
}
