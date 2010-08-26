package org.insightech.er.editor.model.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Display;

public class PageSetting implements Serializable {

	private static final long serialVersionUID = 7520875865783223474L;

	private static final int DEFAULT_MARGIN = 30;

	private boolean directionHorizontal;

	private int scale;

	private String paperSize;

	private int topMargin;

	private int leftMargin;

	private int bottomMargin;

	private int rightMargin;

	public PageSetting() {
		this.directionHorizontal = true;
		this.scale = 100;
		this.paperSize = getAllPaperSize().get(0);
		this.topMargin = DEFAULT_MARGIN;
		this.rightMargin = DEFAULT_MARGIN;
		this.bottomMargin = DEFAULT_MARGIN;
		this.leftMargin = DEFAULT_MARGIN;
	}

	public PageSetting(boolean directionHorizontal, int scale,
			String paperSize, int topMargin, int rightMargin, int bottomMargin,
			int leftMargin) {
		this.directionHorizontal = directionHorizontal;
		this.scale = scale;
		this.paperSize = paperSize;
		this.topMargin = topMargin;
		this.rightMargin = rightMargin;
		this.bottomMargin = bottomMargin;
		this.leftMargin = leftMargin;
	}

	public static List<String> getAllPaperSize() {
		List<String> allPaperSize = new ArrayList<String>();

		allPaperSize.add("A4 210 x 297 mm");
		allPaperSize.add("B4 257 x 364 mm");
		allPaperSize.add("B5 182 x 257 mm");
		allPaperSize.add("A3 297 x 420 mm");

		return allPaperSize;
	}

	/**
	 * directionHorizontal ‚ðŽæ“¾‚µ‚Ü‚·.
	 * 
	 * @return directionHorizontal
	 */
	public boolean isDirectionHorizontal() {
		return directionHorizontal;
	}

	/**
	 * scale ‚ðŽæ“¾‚µ‚Ü‚·.
	 * 
	 * @return scale
	 */
	public int getScale() {
		return scale;
	}

	/**
	 * paperSize ‚ðŽæ“¾‚µ‚Ü‚·.
	 * 
	 * @return paperSize
	 */
	public String getPaperSize() {
		return paperSize;
	}

	/**
	 * topMargin ‚ðŽæ“¾‚µ‚Ü‚·.
	 * 
	 * @return topMargin
	 */
	public int getTopMargin() {
		return topMargin;
	}

	/**
	 * leftMargin ‚ðŽæ“¾‚µ‚Ü‚·.
	 * 
	 * @return leftMargin
	 */
	public int getLeftMargin() {
		return leftMargin;
	}

	/**
	 * bottomMargin ‚ðŽæ“¾‚µ‚Ü‚·.
	 * 
	 * @return bottomMargin
	 */
	public int getBottomMargin() {
		return bottomMargin;
	}

	/**
	 * rightMargin ‚ðŽæ“¾‚µ‚Ü‚·.
	 * 
	 * @return rightMargin
	 */
	public int getRightMargin() {
		return rightMargin;
	}

	public int getWidth() {
		return (int) ((this.getLength(this.directionHorizontal)
				- (this.leftMargin / 10) - (this.rightMargin / 10))
				* Display.getCurrent().getDPI().x / 25.4 * 100 / this.scale);
	}

	public int getHeight() {
		return (int) ((this.getLength(!this.directionHorizontal)
				- (this.topMargin / 10) - (this.bottomMargin / 10))
				* Display.getCurrent().getDPI().y / 25.4 * 100 / this.scale);
	}

	private int getLength(boolean horizontal) {
		if (horizontal) {
			if (this.paperSize.startsWith("B4")) {
				return 364;
			} else if (this.paperSize.startsWith("B5")) {
				return 257;
			} else if (this.paperSize.startsWith("A3")) {
				return 420;
			} else {
				return 297;
			}

		} else {
			if (this.paperSize.startsWith("B4")) {
				return 257;
			} else if (this.paperSize.startsWith("B5")) {
				return 182;
			} else if (this.paperSize.startsWith("A3")) {
				return 297;
			} else {
				return 210;
			}
		}
	}
}
