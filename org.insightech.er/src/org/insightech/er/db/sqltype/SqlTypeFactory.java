package org.insightech.er.db.sqltype;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.insightech.er.db.sqltype.SqlType.TypeKey;
import org.insightech.er.util.Closer;
import org.insightech.er.util.POIUtils;

public class SqlTypeFactory {

	public static void load() throws IOException, ClassNotFoundException {
		InputStream in = null;

		try {
			in = SqlTypeFactory.class
					.getResourceAsStream("/SqlType.xls");

			Workbook workBook = POIUtils.readExcelBook(in);

			Sheet sheet = workBook.getSheetAt(0);

			Map<String, Map<SqlType, String>> dbAliasMap = new HashMap<String, Map<SqlType, String>>();
			Map<String, Map<TypeKey, SqlType>> dbSqlTypeMap = new HashMap<String, Map<TypeKey, SqlType>>();

			Row headerRow = sheet.getRow(0);

			for (int colNum = 4, colEnd = headerRow.getLastCellNum(); colNum < colEnd; colNum++) {
				String dbId = POIUtils.getCellValue(sheet, 0, colNum);

				Map<SqlType, String> aliasMap = new LinkedHashMap<SqlType, String>();
				dbAliasMap.put(dbId, aliasMap);

				Map<TypeKey, SqlType> sqlTypeMap = new LinkedHashMap<TypeKey, SqlType>();
				dbSqlTypeMap.put(dbId, sqlTypeMap);
			}

			SqlType.setDBAliasMap(dbAliasMap, dbSqlTypeMap);

			for (int rowNum = 1, rowEnd = sheet.getLastRowNum(); rowNum <= rowEnd; rowNum++) {
				Row row = sheet.getRow(rowNum);

				String sqlTypeId = POIUtils.getCellValue(sheet, rowNum, 0);
				if (StringUtils.isEmpty(sqlTypeId)) {
					break;
				}
				Class<?> javaClass = Class.forName(POIUtils.getCellValue(sheet,
						rowNum, 1));
				boolean needArgs = POIUtils.getBooleanCellValue(sheet, rowNum,
						2);
				boolean fullTextIndexable = POIUtils.getBooleanCellValue(sheet,
						rowNum, 3);

				SqlType sqlType = new SqlType(sqlTypeId, javaClass, needArgs,
						fullTextIndexable);

				for (int colNum = 4, colEnd = row.getLastCellNum(); colNum < colEnd; colNum++) {

					String dbId = POIUtils.getCellValue(sheet, 0, colNum);

					if (StringUtils.isEmpty(dbId)) {
						dbId = POIUtils.getCellValue(sheet, 0, colNum - 1);
						String key = POIUtils.getCellValue(sheet, rowNum,
								colNum);
						if (StringUtils.isNotEmpty(key)) {
							sqlType.addToSqlTypeMap(key, dbId);
						}

					} else {
						Map<SqlType, String> aliasMap = dbAliasMap.get(dbId);

						if (POIUtils.getCellColor(sheet, rowNum, colNum) != HSSFColor.RED.index) {
							String alias = POIUtils.getCellValue(sheet, rowNum,
									colNum);

							if (StringUtils.isEmpty(alias)) {
								alias = sqlTypeId;
							}

							aliasMap.put(sqlType, alias);

							if (POIUtils.getCellColor(sheet, rowNum, colNum) == HSSFColor.SKY_BLUE.index) {
								sqlType.addToSqlTypeMap(alias, dbId);
							}
						}
					}
				}
			}

		} finally {
			Closer.close(in);
		}

	}

	public static void main(String[] args) {
		SqlType.main(ArrayUtils.EMPTY_STRING_ARRAY);
	}
}
