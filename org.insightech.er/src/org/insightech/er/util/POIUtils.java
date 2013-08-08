package org.insightech.er.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

public class POIUtils {

	public static class CellLocation {
		public int r;

		public int c;

		private CellLocation(int r, short c) {
			this.r = r;
			this.c = c;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			String str = "(" + this.r + ", " + this.c + ")";

			return str;
		}
	}

	public static CellLocation findCell(Sheet sheet, String str) {
		return findCell(sheet, new String[] { str });
	}

	public static CellLocation findCell(Sheet sheet, String[] strs) {
		for (int rowNum = sheet.getFirstRowNum(); rowNum < sheet
				.getLastRowNum() + 1; rowNum++) {
			Row row = sheet.getRow(rowNum);
			if (row == null) {
				continue;
			}

			for (final String str : strs) {
				Integer colNum = findColumn(row, str);

				if (colNum != null) {
					return new CellLocation(rowNum, colNum.shortValue());
				}
			}
		}

		return null;
	}

	public static Integer findColumn(Row row, String str) {
		for (int colNum = row.getFirstCellNum(); colNum <= row.getLastCellNum(); colNum++) {
			Cell cell = row.getCell(colNum);

			if (cell == null) {
				continue;
			}

			if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
				String cellValue = cell.getStringCellValue();

				if (StringUtils.equals(str, cellValue)) {
					return Integer.valueOf(colNum);
				}
			}
		}

		return null;
	}

	public static CellLocation findMatchCell(Sheet sheet, String regexp) {
		for (int rowNum = sheet.getFirstRowNum(); rowNum < sheet
				.getLastRowNum() + 1; rowNum++) {
			Row row = sheet.getRow(rowNum);
			if (row == null) {
				continue;
			}

			Integer colNum = findMatchColumn(row, regexp);

			if (colNum != null) {
				return new CellLocation(rowNum, colNum.shortValue());
			}
		}

		return null;
	}

	public static Integer findMatchColumn(Row row, String str) {
		for (int colNum = row.getFirstCellNum(); colNum <= row.getLastCellNum(); colNum++) {
			Cell cell = row.getCell(colNum);

			if (cell == null) {
				continue;
			}

			if (cell.getCellType() != Cell.CELL_TYPE_STRING) {
				continue;
			}

			String cellValue = cell.getStringCellValue();

			if (cellValue.matches(str)) {
				return Integer.valueOf(colNum);
			}
		}

		return null;
	}

	public static CellLocation findCell(Sheet sheet, String str, int colNum) {
		for (int rowNum = sheet.getFirstRowNum(); rowNum < sheet
				.getLastRowNum() + 1; rowNum++) {
			Row row = sheet.getRow(rowNum);
			if (row == null) {
				continue;
			}

			Cell cell = row.getCell(colNum);

			if (cell == null) {
				continue;
			}
			String cellValue = cell.getStringCellValue();

			if (StringUtils.equals(cellValue, str)) {
				return new CellLocation(rowNum, (short) colNum);
			}
		}

		return null;
	}

	public static void replace(Sheet sheet, String keyword, String str) {
		CellLocation location = findCell(sheet, keyword);

		if (location == null) {
			return;
		}

		setCellValue(sheet, location, str);
	}

	public static String getCellValue(Sheet sheet, CellLocation location) {
		return getCellValue(sheet, location.r, location.c);
	}

	public static String getCellValue(Sheet sheet, int r, int c) {
		Row row = sheet.getRow(r);
		if (row == null) {
			return null;
		}
		
		Cell cell = row.getCell(c);
		if (cell == null) {
			return null;
		}
		
		return cell.getStringCellValue();
	}

	public static int getIntCellValue(Sheet sheet, int r, int c) {
		Row row = sheet.getRow(r);
		if (row == null) {
			return 0;
		}
		Cell cell = row.getCell(c);

		if (cell.getCellType() != Cell.CELL_TYPE_NUMERIC) {
			return 0;
		}

		return (int) cell.getNumericCellValue();
	}

	public static boolean getBooleanCellValue(Sheet sheet, int r, int c) {
		Row row = sheet.getRow(r);
		
		if (row == null) {
			return false;
		}
		
		Cell cell = row.getCell(c);
		
		if (cell == null) {
			return false;
		}
		
		return cell.getBooleanCellValue();
	}

	public static short getCellColor(Sheet sheet, int r, int c) {
		Row row = sheet.getRow(r);
		if (row == null) {
			return -1;
		}
		Cell cell = row.getCell(c);

		return cell.getCellStyle().getFillForegroundColor();
	}

	public static void setCellValue(Sheet sheet, CellLocation location,
			String value) {
		Row row = sheet.getRow(location.r);
		Cell cell = row.getCell(location.c);

		cell.setCellValue(value);
	}

	/**
	 * Excelファイルを開く
	 * 
	 * @param excelFile
	 * @return
	 * @throws IOException
	 */
	public static Workbook readExcelBook(File excelFile) throws IOException {
		FileInputStream fis = null;

		try {
			fis = new FileInputStream(excelFile);

			return readExcelBook(fis);

		} finally {
			Closer.close(fis);
		}
	}

	/**
	 * �G�N�Z���t�@�C���̓ǂݍ��݂�s���܂��B
	 * 
	 * @param excelFile
	 * @return
	 * @throws IOException
	 */
	public static Workbook readExcelBook(InputStream stream)
			throws IOException {
		BufferedInputStream bis = null;
		try {
			bis = new BufferedInputStream(stream);
			return new HSSFWorkbook(bis);

		} finally {
			Closer.close(bis);
		}
	}

	/**
	 * �G�N�Z���t�@�C���ɏ����o����s���܂��B
	 * 
	 * @param excelFile
	 * @param workbook
	 * @return
	 * @throws IOException
	 */
	public static void writeExcelFile(File excelFile, Workbook workbook)
			throws IOException {
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;

		try {
			fos = new FileOutputStream(excelFile);
			bos = new BufferedOutputStream(fos);
			workbook.write(bos);

		} finally {
			Closer.close(bos);
			Closer.close(fos);
		}
	}

	/**
	 * location�Ŏw�肵���s�́A�w�肵���񂩂�n�܂錋�����ꂽ�̈��擾���܂�
	 * 
	 * @param sheet
	 * @param location
	 * @return
	 */
	public static CellRangeAddress getMergedRegion(Sheet sheet,
			CellLocation location) {
		for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
			CellRangeAddress region = sheet.getMergedRegion(i);

			int rowFrom = region.getFirstRow();
			int rowTo = region.getLastRow();

			if (rowFrom == location.r && rowTo == location.r) {
				int colFrom = region.getFirstColumn();

				if (colFrom == location.c) {
					return region;
				}
			}
		}

		return null;
	}

	/**
	 * location�Ŏw�肵���s�́A�������ꂽ�̈�̈ꗗ��擾���܂�
	 * 
	 * @param sheet
	 * @param location
	 * @return
	 */
	public static List<CellRangeAddress> getMergedRegionList(Sheet sheet,
			int rowNum) {
		List<CellRangeAddress> regionList = new ArrayList<CellRangeAddress>();

		for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
			CellRangeAddress region = sheet.getMergedRegion(i);

			int rowFrom = region.getFirstRow();
			int rowTo = region.getLastRow();

			if (rowFrom == rowNum && rowTo == rowNum) {
				regionList.add(region);
			}
		}

		return regionList;
	}

	public static void copyRow(Sheet oldSheet, Sheet newSheet,
			int oldStartRowNum, int oldEndRowNum, int newStartRowNum) {
		Row oldAboveRow = oldSheet.getRow(oldStartRowNum - 1);

		int newRowNum = newStartRowNum;

		for (int oldRowNum = oldStartRowNum; oldRowNum <= oldEndRowNum; oldRowNum++) {
			POIUtils.copyRow(oldSheet, newSheet, oldRowNum, newRowNum++);
		}

		Row newTopRow = newSheet.getRow(newStartRowNum);

		if (oldAboveRow != null) {
			for (int colNum = newTopRow.getFirstCellNum(); colNum <= newTopRow
					.getLastCellNum(); colNum++) {
				Cell oldAboveCell = oldAboveRow.getCell(colNum);
				if (oldAboveCell != null) {
					Cell newTopCell = newTopRow.getCell(colNum);
					newTopCell.getCellStyle().setBorderTop(
							oldAboveCell.getCellStyle().getBorderBottom());
				}
			}
		}
	}

	public static void copyRow(Sheet oldSheet, Sheet newSheet,
			int oldRowNum, int newRowNum) {
		Row oldRow = oldSheet.getRow(oldRowNum);

		Row newRow = newSheet.createRow(newRowNum);

		if (oldRow == null) {
			return;
		}

		newRow.setHeight(oldRow.getHeight());

		if (oldRow.getFirstCellNum() == -1) {
			return;
		}

		for (int colNum = oldRow.getFirstCellNum(); colNum <= oldRow
				.getLastCellNum(); colNum++) {
			Cell oldCell = oldRow.getCell(colNum);
			Cell newCell = newRow.createCell(colNum);

			if (oldCell != null) {
				CellStyle style = oldCell.getCellStyle();
				newCell.setCellStyle(style);

				int cellType = oldCell.getCellType();
				newCell.setCellType(cellType);

				switch (cellType) {
				case Cell.CELL_TYPE_BOOLEAN:
					newCell.setCellValue(oldCell.getBooleanCellValue());
					break;

				case Cell.CELL_TYPE_FORMULA:
					newCell.setCellFormula(oldCell.getCellFormula());
					break;

				case Cell.CELL_TYPE_NUMERIC:
					newCell.setCellValue(oldCell.getNumericCellValue());
					break;

				case Cell.CELL_TYPE_STRING:
					newCell.setCellValue(oldCell.getRichStringCellValue());
					break;
				}
			}
		}

		POIUtils.copyMergedRegion(newSheet, getMergedRegionList(oldSheet,
				oldRowNum), newRowNum);
	}

	public static void copyMergedRegion(Sheet sheet,
			List<CellRangeAddress> regionList, int rowNum) {
		for (CellRangeAddress region : regionList) {
			CellRangeAddress address = new CellRangeAddress(rowNum, rowNum,
					region.getFirstColumn(), region.getLastColumn());
			sheet.addMergedRegion(address);
		}
	}

	public static List<CellStyle> copyCellStyle(Workbook workbook,
			Row row) {
		List<CellStyle> cellStyleList = new ArrayList<CellStyle>();

		for (int colNum = row.getFirstCellNum(); colNum <= row.getLastCellNum(); colNum++) {

			Cell cell = row.getCell(colNum);
			if (cell == null) {
				cellStyleList.add(null);
			} else {
				CellStyle style = cell.getCellStyle();
				CellStyle newCellStyle = copyCellStyle(workbook, style);
				cellStyleList.add(newCellStyle);
			}
		}

		return cellStyleList;
	}

	public static CellStyle copyCellStyle(Workbook workbook,
			CellStyle style) {

		CellStyle newCellStyle = workbook.createCellStyle();

		newCellStyle.setAlignment(style.getAlignment());
		newCellStyle.setBorderBottom(style.getBorderBottom());
		newCellStyle.setBorderLeft(style.getBorderLeft());
		newCellStyle.setBorderRight(style.getBorderRight());
		newCellStyle.setBorderTop(style.getBorderTop());
		newCellStyle.setBottomBorderColor(style.getBottomBorderColor());
		newCellStyle.setDataFormat(style.getDataFormat());
		newCellStyle.setFillBackgroundColor(style.getFillBackgroundColor());
		newCellStyle.setFillForegroundColor(style.getFillForegroundColor());
		newCellStyle.setFillPattern(style.getFillPattern());
		newCellStyle.setHidden(style.getHidden());
		newCellStyle.setIndention(style.getIndention());
		newCellStyle.setLeftBorderColor(style.getLeftBorderColor());
		newCellStyle.setLocked(style.getLocked());
		newCellStyle.setRightBorderColor(style.getRightBorderColor());
		newCellStyle.setRotation(style.getRotation());
		newCellStyle.setTopBorderColor(style.getTopBorderColor());
		newCellStyle.setVerticalAlignment(style.getVerticalAlignment());
		newCellStyle.setWrapText(style.getWrapText());

		Font font = workbook.getFontAt(style.getFontIndex());
		newCellStyle.setFont(font);

		return newCellStyle;
	}

	public static Font copyFont(Workbook workbook, Font font) {

		Font newFont = workbook.createFont();

		// newFont.setBoldweight(font.getBoldweight());
		// newFont.setCharSet(font.getCharSet());
		// newFont.setColor(font.getColor());
		// newFont.setFontHeight(font.getFontHeight());
		// newFont.setFontHeightInPoints(font.getFontHeightInPoints());
		// newFont.setFontName(font.getFontName());
		// newFont.setItalic(font.getItalic());
		// newFont.setStrikeout(font.getStrikeout());
		// newFont.setTypeOffset(font.getTypeOffset());
		// newFont.setUnderline(font.getUnderline());

		return newFont;
	}

	public static Row insertRow(Sheet sheet, int rowNum) {
		sheet.shiftRows(rowNum + 1, sheet.getLastRowNum(), 1);

		return sheet.getRow(rowNum);
	}
}
