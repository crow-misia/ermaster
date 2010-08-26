package org.insightech.er.editor.model.dbexport.excel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.ObjectModel;
import org.insightech.er.editor.model.StringObjectModel;
import org.insightech.er.editor.model.dbexport.excel.sheet_generator.AbstractSheetGenerator;
import org.insightech.er.editor.model.dbexport.excel.sheet_generator.AllIndicesSheetGenerator;
import org.insightech.er.editor.model.dbexport.excel.sheet_generator.AllSequencesSheetGenerator;
import org.insightech.er.editor.model.dbexport.excel.sheet_generator.AllTablesSheetGenerator;
import org.insightech.er.editor.model.dbexport.excel.sheet_generator.AllTriggerSheetGenerator;
import org.insightech.er.editor.model.dbexport.excel.sheet_generator.AllViewSheetGenerator;
import org.insightech.er.editor.model.dbexport.excel.sheet_generator.CategorySheetGenerator;
import org.insightech.er.editor.model.dbexport.excel.sheet_generator.ColumnSheetGenerator;
import org.insightech.er.editor.model.dbexport.excel.sheet_generator.IndexSheetGenerator;
import org.insightech.er.editor.model.dbexport.excel.sheet_generator.PictureSheetGenerator;
import org.insightech.er.editor.model.dbexport.excel.sheet_generator.SequenceSheetGenerator;
import org.insightech.er.editor.model.dbexport.excel.sheet_generator.SheetIndexSheetGenerator;
import org.insightech.er.editor.model.dbexport.excel.sheet_generator.TableSheetGenerator;
import org.insightech.er.editor.model.dbexport.excel.sheet_generator.TriggerSheetGenerator;
import org.insightech.er.editor.model.dbexport.excel.sheet_generator.ViewSheetGenerator;
import org.insightech.er.util.POIUtils;
import org.insightech.er.util.io.FileUtils;

public class ExportToExcelManager implements IRunnableWithProgress {

	private static final String WORDS_SHEET_NAME = "words";

	private static final String LOOPS_SHEET_NAME = "loops";

	private Map<String, Integer> sheetNameMap;

	private Map<String, ObjectModel> sheetObjectMap;

	private static final List<AbstractSheetGenerator> SHHET_GENERATOR_LIST = new ArrayList<AbstractSheetGenerator>();

	static {
		SHHET_GENERATOR_LIST.add(new TableSheetGenerator());
		SHHET_GENERATOR_LIST.add(new IndexSheetGenerator());
		SHHET_GENERATOR_LIST.add(new SequenceSheetGenerator());
		SHHET_GENERATOR_LIST.add(new ViewSheetGenerator());
		SHHET_GENERATOR_LIST.add(new TriggerSheetGenerator());
		SHHET_GENERATOR_LIST.add(new ColumnSheetGenerator());
		SHHET_GENERATOR_LIST.add(new AllTablesSheetGenerator());
		SHHET_GENERATOR_LIST.add(new AllIndicesSheetGenerator());
		SHHET_GENERATOR_LIST.add(new AllSequencesSheetGenerator());
		SHHET_GENERATOR_LIST.add(new AllViewSheetGenerator());
		SHHET_GENERATOR_LIST.add(new AllTriggerSheetGenerator());
		SHHET_GENERATOR_LIST.add(new CategorySheetGenerator());
	}

	public static class LoopDefinition {

		public int startLine;

		public int spaceLine;

		public String sheetName;

		public LoopDefinition(int startLine, int spaceLine, String sheetName) {
			this.startLine = startLine;
			this.spaceLine = spaceLine;
			this.sheetName = sheetName;
		}
	}

	private PictureSheetGenerator pictureSheetGenerator;

	private SheetIndexSheetGenerator sheetIndexSheetGenerator;

	private Map<String, LoopDefinition> loopDefinitionMap;

	private String saveFilePath;

	private ERDiagram diagram;

	private InputStream template;

	private boolean useLogicalNameAsSheetName;

	private byte[] imageBuffer;

	private int excelPictureType;

	private Exception exception;

	public ExportToExcelManager(String saveFilePath, ERDiagram diagram,
			InputStream template, boolean useLogicalNameAsSheetName,
			byte[] imageBuffer, int excelPictureType) {
		super();

		this.saveFilePath = saveFilePath;
		this.diagram = diagram;
		this.template = template;
		this.useLogicalNameAsSheetName = useLogicalNameAsSheetName;
		this.imageBuffer = imageBuffer;
		this.excelPictureType = excelPictureType;

		this.sheetNameMap = new HashMap<String, Integer>();
		this.sheetObjectMap = new LinkedHashMap<String, ObjectModel>();

		this.loopDefinitionMap = new HashMap<String, LoopDefinition>();
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {

		try {
			save(monitor);

		} catch (InterruptedException e) {
			throw e;

		} catch (Exception e) {
			this.exception = e;
		}

		monitor.done();
	}

	/**
	 * データベース定義をエクセルに書き出します。
	 * 
	 * @param editorPart
	 * @param viewer
	 * @param saveFilePath
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void save(IProgressMonitor monitor) throws IOException,
			InterruptedException {
		File excelFile = new File(this.saveFilePath);

		this.backup(excelFile, true);

		HSSFWorkbook workbook = this.loadTemplateWorkbook(this.template,
				this.diagram);

		int count = this.countSheetFromTemplate(workbook, this.diagram);
		monitor.beginTask(ResourceString
				.getResourceString("dialog.message.export.excel"), count);

		this.pictureSheetGenerator = new PictureSheetGenerator(workbook,
				imageBuffer, excelPictureType);

		this.createSheetFromTemplate(monitor, workbook, diagram,
				useLogicalNameAsSheetName);

		for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
			workbook.getSheetAt(i).setSelected(false);
		}

		if (workbook.getNumberOfSheets() > 0) {
			workbook.getSheetAt(0).setSelected(true);
			workbook.setActiveSheet(0);
		}

		POIUtils.writeExcelFile(excelFile, workbook);
	}

	private HSSFWorkbook loadTemplateWorkbook(InputStream template,
			ERDiagram diagram) throws IOException {

		HSSFWorkbook workbook = POIUtils.readExcelBook(template);

		if (workbook == null) {
			throw new IOException(ResourceString
					.getResourceString("error.read.file"));
		}

		HSSFSheet wordsSheet = workbook.getSheet(WORDS_SHEET_NAME);

		if (wordsSheet == null) {
			throw new IOException(ResourceString
					.getResourceString("error.not.found.words.sheet"));
		}

		HSSFSheet loopsSheet = workbook.getSheet(LOOPS_SHEET_NAME);

		if (loopsSheet == null) {
			throw new IOException(ResourceString
					.getResourceString("error.not.found.loops.sheet"));
		}

		this.initLoopDefinitionMap(loopsSheet);

		for (AbstractSheetGenerator sheetGenerator : SHHET_GENERATOR_LIST) {
			sheetGenerator.init(wordsSheet);
		}

		this.sheetIndexSheetGenerator = new SheetIndexSheetGenerator();
		this.sheetIndexSheetGenerator.init(wordsSheet);

		return workbook;
	}

	private void initLoopDefinitionMap(HSSFSheet loopsSheet) {
		for (int i = 2; i <= loopsSheet.getLastRowNum(); i++) {
			String templateSheetName = POIUtils.getCellValue(loopsSheet, i, 0);
			if (templateSheetName == null) {
				break;
			}

			int firstLine = POIUtils.getIntCellValue(loopsSheet, i, 1);
			int spaceLine = POIUtils.getIntCellValue(loopsSheet, i, 2);
			String sheetName = POIUtils.getCellValue(loopsSheet, i, 3);

			this.loopDefinitionMap.put(templateSheetName, new LoopDefinition(
					firstLine, spaceLine, sheetName));
		}
	}

	private AbstractSheetGenerator getSheetGenerator(String templateSheetName) {
		for (AbstractSheetGenerator sheetGenerator : SHHET_GENERATOR_LIST) {
			if (sheetGenerator.getTemplateSheetName().equals(templateSheetName)) {
				return sheetGenerator;
			}
		}

		return null;
	}

	private void createSheetFromTemplate(IProgressMonitor monitor,
			HSSFWorkbook workbook, ERDiagram diagram,
			boolean useLogicalNameAsSheetName) throws InterruptedException {
		int originalSheetNum = workbook.getNumberOfSheets();

		int sheetNo = 0;
		int sheetIndexSheetNo = -1;

		while (sheetNo < originalSheetNum) {
			String templateSheetName = workbook.getSheetName(sheetNo);

			AbstractSheetGenerator sheetGenerator = this
					.getSheetGenerator(templateSheetName);

			if (sheetGenerator != null) {
				sheetGenerator.generate(monitor, workbook, sheetNo,
						useLogicalNameAsSheetName, this.sheetNameMap,
						this.sheetObjectMap, diagram, loopDefinitionMap);
				workbook.removeSheetAt(sheetNo);
				originalSheetNum--;

			} else {
				// ER図の貼り付け
				HSSFSheet sheet = workbook.getSheetAt(sheetNo);
				this.pictureSheetGenerator.setImage(workbook, sheet);

				if (this.sheetIndexSheetGenerator.getTemplateSheetName()
						.equals(templateSheetName)) {
					sheetIndexSheetNo = sheetNo;

					String name = this.sheetIndexSheetGenerator.getSheetName();

					workbook.setSheetName(sheetNo,
							this.sheetIndexSheetGenerator.decideSheetName(name,
									sheetNameMap));

					sheetObjectMap.put(workbook.getSheetName(sheetNo),
							new StringObjectModel(workbook
									.getSheetName(sheetNo)));

				} else if (!isExcludeTarget(templateSheetName)) {
					this.sheetObjectMap.put(workbook.getSheetName(sheetNo),
							new StringObjectModel(workbook
									.getSheetName(sheetNo)));
				}

				sheetNo++;

				monitor.worked(1);
			}

			if (monitor.isCanceled()) {
				throw new InterruptedException("Cancel has been requested.");
			}
		}

		// 必要なくなったシートの削除
		workbook.removeSheetAt(workbook.getSheetIndex(WORDS_SHEET_NAME));
		workbook.removeSheetAt(workbook.getSheetIndex(LOOPS_SHEET_NAME));

		// シート一覧の作成
		if (sheetIndexSheetNo != -1) {
			this.sheetIndexSheetGenerator.generate(monitor, workbook,
					sheetIndexSheetNo, useLogicalNameAsSheetName,
					this.sheetNameMap, this.sheetObjectMap, diagram,
					loopDefinitionMap);
		}
	}

	private int countSheetFromTemplate(HSSFWorkbook workbook, ERDiagram diagram) {
		int count = 0;

		for (int sheetNo = 0; sheetNo < workbook.getNumberOfSheets(); sheetNo++) {
			String templateSheetName = workbook.getSheetName(sheetNo);

			AbstractSheetGenerator sheetGenerator = this
					.getSheetGenerator(templateSheetName);

			if (sheetGenerator != null) {
				count += sheetGenerator.count(diagram);

			} else {
				count++;
			}
		}

		return count;
	}

	/**
	 * バックアップファイルを作成します。
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private boolean backup(File file, boolean isBackupEnable)
			throws IOException {
		if (!isBackupEnable || !file.exists()) {
			return true;
		}

		String path;
		try {
			path = file.getCanonicalPath();

			String backupFilePath = path.substring(0, path.indexOf(".xls"))
					+ "_back.xls";
			File backupFile = new File(backupFilePath);
			FileUtils.copyFile(file, backupFile);

		} catch (IOException e) {
			throw new IOException(ResourceString
					.getResourceString("error.backup.excel.file"));
		}

		return true;
	}

	private boolean isExcludeTarget(String templateSheetName) {
		if (WORDS_SHEET_NAME.equals(templateSheetName)
				|| LOOPS_SHEET_NAME.equals(templateSheetName)) {
			return true;
		}

		return false;
	}

	public Exception getException() {
		return exception;
	}
}
