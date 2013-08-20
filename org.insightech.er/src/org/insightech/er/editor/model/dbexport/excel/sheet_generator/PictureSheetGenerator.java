package org.insightech.er.editor.model.dbexport.excel.sheet_generator;

import java.awt.Dimension;
import java.io.ByteArrayInputStream;

import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.ImageUtils;
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
			getPreferredSize(sheet, anchor, picture.getPictureData(), width, height, true);
		}
	}

	/**
	 * POIのresizeがバグっているため、POIの内部コードを修正したコードを此処に配置
	 */
    /**
     * width of 1px in columns with default width in units of 1/256 of a character width
     */
    private static final float PX_DEFAULT = 32.00f;
    /**
     * width of 1px in columns with overridden width in units of 1/256 of a character width
     */
    private static final float PX_MODIFIED = 36.56f;

    /**
     * Height of 1px of a row
     */
    private static final int PX_ROW = 15;

    private ClientAnchor getPreferredSize(final Sheet sheet, final ClientAnchor anchor, final PictureData pictureData, final double width, final double height, final boolean aspectLock) {
        double scaledWidth;
        double scaledHeight;
        if (aspectLock) {
            final Dimension size = getImageDimension(pictureData);
            final double aspect = size.getWidth() / size.getHeight();
            if (width > height) {
                scaledHeight = width / aspect;
                scaledWidth = scaledHeight * aspect;
            } else {
                scaledWidth = height * aspect;
                scaledHeight = scaledWidth / aspect;
            }
        } else {
            scaledWidth = width;
            scaledHeight = height;
        }

        //space in the leftmost cell
        float w = getColumnWidthInPixels(sheet, anchor.getCol1())*(1 - (float)anchor.getDx1()/1024);
        short col2 = (short)(anchor.getCol1() + 1);
        int dx2 = 0;

        while(w < scaledWidth){
            w += getColumnWidthInPixels(sheet, col2++);
        }

        if(w > scaledWidth) {
            //calculate dx2, offset in the rightmost cell
            col2--;
            final double cw = getColumnWidthInPixels(sheet, col2);
            final double delta = w - scaledWidth;
            dx2 = (int)((cw - delta) / cw * 1024);
        }
        anchor.setCol2(col2);
        anchor.setDx2(dx2);

        float h = 0;
        h += (1 - (float)anchor.getDy1()/256)* getRowHeightInPixels(sheet, anchor.getRow1());
        int row2 = anchor.getRow1() + 1;
        int dy2 = 0;

        while(h < scaledHeight){
            h += getRowHeightInPixels(sheet, row2++);
        }
        if(h > scaledHeight) {
            row2--;
            final double ch = getRowHeightInPixels(sheet, row2);
            final double delta = h - scaledHeight;
            dy2 = (int)((ch-delta)/ch*256);
        }
        anchor.setRow2(row2);
        anchor.setDy2(dy2);

        return anchor;
    }

    private float getColumnWidthInPixels(final Sheet sheet, final int column){

        int cw = sheet.getColumnWidth(column);
        float px = getPixelWidth(sheet, column);

        return cw/px;
    }

    private float getRowHeightInPixels(final Sheet sheet, final int i){

        Row row = sheet.getRow(i);
        float height;
        if(row != null) height = row.getHeight();
        else height = sheet.getDefaultRowHeight();

        return height/PX_ROW;
    }

    private float getPixelWidth(final Sheet sheet, final int column){

        int def = sheet.getDefaultColumnWidth()*256;
        int cw = sheet.getColumnWidth(column);

        return cw == def ? PX_DEFAULT : PX_MODIFIED;
    }

    /**
     * Return the dimension of this image
     *
     * @return image dimension
     */
    protected Dimension getImageDimension(final PictureData pictureData){
        byte[] data = pictureData.getData();
        return ImageUtils.getImageDimension(new ByteArrayInputStream(data), excelPictureType);
    }
}
