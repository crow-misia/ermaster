package org.insightech.er.editor.model.dbexport.excel.sheet_generator;

import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.insightech.er.util.POIUtils;
import org.insightech.er.util.POIUtils.CellLocation;

public class PictureSheetGenerator {

	private static final String KEYWORD_ER = "$ER";

	private int pictureIndex;

	private int excelPictureType;

	private boolean existPicture;

	public PictureSheetGenerator(Workbook workbook, byte[] imageBuffer,
			int excelPictureType) {
		this.excelPictureType = excelPictureType;

		this.existPicture = imageBuffer != null;
		if (this.existPicture) {
			this.pictureIndex = workbook.addPicture(imageBuffer,
					this.excelPictureType);
		}
	}

	public void setImage(Workbook workbook, Sheet sheet) {
		CellLocation cellLocation = POIUtils.findMatchCell(sheet, "\\"
				+ KEYWORD_ER + ".*");
		if (cellLocation != null) {
			int width = -1;
			int height = -1;

			String value = POIUtils.getCellValue(sheet, cellLocation);

			int startIndex = value.indexOf("(");
			if (startIndex != -1) {
				int middleIndex = value.indexOf(",", startIndex + 1);
				if (middleIndex != -1) {
					width = Integer.parseInt(value.substring(startIndex + 1,
							middleIndex).trim());
					height = Integer.parseInt(value.substring(middleIndex + 1,
							value.length() - 1).trim());
				}
			}

			this.setImage(workbook, sheet, cellLocation, width, height);
		}
	}

	private void setImage(Workbook workbook, Sheet sheet,
			CellLocation cellLocation, int width, int height) {
		POIUtils.setCellValue(sheet, cellLocation, "");

		if (this.existPicture) {
			Drawing patriarch = sheet.createDrawingPatriarch();
			ClientAnchor anchor = patriarch.createAnchor(0, 0, 0, 0, cellLocation.c, cellLocation.r, cellLocation.c, cellLocation.r);

			Picture picture = patriarch.createPicture(anchor, this.pictureIndex);
			picture.resize();
		}
	}
}
