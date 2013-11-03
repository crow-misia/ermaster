package org.insightech.er.editor.model;

import java.util.Locale;

import org.eclipse.draw2d.geometry.Point;
import org.insightech.er.editor.ERDiagramMultiPageEditor;
import org.insightech.er.editor.model.diagram_contents.DiagramContents;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GlobalGroupSet;
import org.insightech.er.editor.model.settings.DBSetting;
import org.insightech.er.editor.model.settings.PageSetting;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.model.tracking.ChangeTrackingList;

public class ERDiagram extends ViewableModel {

	private static final long serialVersionUID = 8729319470770699498L;

	private ChangeTrackingList changeTrackingList;

	private DiagramContents diagramContents;

	private ERDiagramMultiPageEditor editor;

	private int[] defaultColor;

	private boolean tooltip;

	private boolean disableSelectColumn;

	private boolean snapToGrid;

	private Category currentCategory;

	private int currentCategoryIndex;

	private double zoom = 1.0d;

	private int x;

	private int y;

	private DBSetting dbSetting;

	private PageSetting pageSetting;

	public Point mousePoint = new Point();

	public ERDiagram(String database) {
		this.diagramContents = new DiagramContents();
		this.diagramContents.getSettings().setDatabase(database);
		this.pageSetting = new PageSetting();

		this.setDefaultColor(128, 128, 192);
		this.setColor(255, 255, 255);
	}

	public void clear() {
		this.diagramContents.clear();
		this.changeTrackingList.clear();

		this.diagramContents.setColumnGroups(GlobalGroupSet.load());
	}

	public void init() {
		this.diagramContents.setColumnGroups(GlobalGroupSet.load());

		Settings settings = this.getDiagramContents().getSettings();

		if (Locale.JAPANESE.getLanguage().equals(
				Locale.getDefault().getLanguage())) {
			settings.getTranslationSetting().setUse(true);
			settings.getTranslationSetting().selectDefault();
		}

		settings.getModelProperties().init();
	}

	public void addNewContent(NodeElement element) {
		element.setColor(this.defaultColor[0], this.defaultColor[1],
				this.defaultColor[2]);
		element.setFontName(this.getFontName());
		element.setFontSize(this.getFontSize());

		this.addContent(element);
	}

	public void addContent(NodeElement element) {
		element.setDiagram(this);

		this.diagramContents.getContents().addNodeElement(element);

		if (this.editor != null) {
			Category category = this.editor.getCurrentPageCategory();
			if (category != null) {
				category.getContents().add(element);
			}
		}

		if (element instanceof TableView) {
			for (NormalColumn normalColumn : ((TableView) element)
					.getNormalColumns()) {
				this.getDiagramContents().getDictionary().add(normalColumn);
			}
		}

	}

	public void removeContent(NodeElement element) {
		this.diagramContents.getContents().remove(element);

		if (element instanceof TableView) {
			this.diagramContents.getDictionary().remove((TableView) element);
		}

		for (Category category : this.diagramContents.getSettings()
				.getCategorySetting().getAllCategories()) {
			category.getContents().remove(element);
		}
	}

	public void replaceContents(DiagramContents newDiagramContents) {
		this.diagramContents = newDiagramContents;
	}

	public String getDatabase() {
		return this.getDiagramContents().getSettings().getDatabase();
	}

	public void setSettings(Settings settings) {
		this.getDiagramContents().setSettings(settings);
		this.editor.initCategoryPages();
	}

	public void setCurrentCategoryPageName() {
		this.editor.setCurrentCategoryPageName();
	}

	public void addCategory(Category category) {
		category.setColor(this.defaultColor[0], this.defaultColor[1],
				this.defaultColor[2]);
		this.getDiagramContents().getSettings().getCategorySetting()
				.addCategoryAsSelected(category);
		this.editor.initCategoryPages();
	}

	public void removeCategory(Category category) {
		this.getDiagramContents().getSettings().getCategorySetting()
				.removeCategory(category);
		this.editor.initCategoryPages();
	}

	public void restoreCategories() {
		this.editor.initCategoryPages();
	}

	public void change() {
	}

	public ChangeTrackingList getChangeTrackingList() {
		if (this.changeTrackingList == null) {
			this.changeTrackingList = new ChangeTrackingList();
		}
		return changeTrackingList;
	}

	public DiagramContents getDiagramContents() {
		return this.diagramContents;
	}

	public void setEditor(ERDiagramMultiPageEditor editor) {
		this.editor = editor;
	}

	public int[] getDefaultColor() {
		return defaultColor;
	}

	public void setDefaultColor(int red, int green, int blue) {
		this.defaultColor = new int[3];
		this.defaultColor[0] = red;
		this.defaultColor[1] = green;
		this.defaultColor[2] = blue;
	}

	public void setCurrentCategory(Category currentCategory,
			int currentCategoryIndex) {
		this.currentCategory = currentCategory;
		this.currentCategoryIndex = currentCategoryIndex;
	}

	public Category getCurrentCategory() {
		return currentCategory;
	}

	public int getCurrentCategoryIndex() {
		return currentCategoryIndex;
	}

	public boolean isTooltip() {
		return tooltip;
	}

	public void setTooltip(boolean tooltip) {
		this.tooltip = tooltip;
	}

	public double getZoom() {
		return zoom;
	}

	public void setZoom(double zoom) {
		this.zoom = zoom;
	}

	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	/**
	 * dbSetting ���擾���܂�.
	 * 
	 * @return dbSetting
	 */
	public DBSetting getDbSetting() {
		return dbSetting;
	}

	/**
	 * dbSetting ��ݒ肵�܂�.
	 * 
	 * @param dbSetting
	 *            dbSetting
	 */
	public void setDbSetting(DBSetting dbSetting) {
		this.dbSetting = dbSetting;
	}

	/**
	 * pageSetting ���擾���܂�.
	 * 
	 * @return pageSetting
	 */
	public PageSetting getPageSetting() {
		return pageSetting;
	}

	/**
	 * pageSetting ��ݒ肵�܂�.
	 * 
	 * @param pageSetting
	 *            pageSetting
	 */
	public void setPageSetting(PageSetting pageSetting) {
		this.pageSetting = pageSetting;
	}

	/**
	 * editor ���擾���܂�.
	 * 
	 * @return editor
	 */
	public ERDiagramMultiPageEditor getEditor() {
		return editor;
	}

	public String filter(String str) {
		if (str == null) {
			return str;
		}

		Settings settings = this.getDiagramContents().getSettings();

		if (settings.isCapital()) {
			return str.toUpperCase();
		}

		return str;
	}

	/**
	 * disableSelectColumn ���擾���܂�.
	 * 
	 * @return disableSelectColumn
	 */
	public boolean isDisableSelectColumn() {
		return disableSelectColumn;
	}

	/**
	 * disableSelectColumn ��ݒ肵�܂�.
	 * 
	 * @param disableSelectColumn
	 *            disableSelectColumn
	 */
	public void setDisableSelectColumn(boolean disableSelectColumn) {
		this.disableSelectColumn = disableSelectColumn;
	}

	public boolean isSnapToGrid() {
		return snapToGrid;
	}

	public void setSnapToGrid(boolean snapToGrid) {
		this.snapToGrid = snapToGrid;
	}

	public void refreshChildren() {
		if (isUpdateable()) {
			this.firePropertyChange("refreshChildren", null, null);
		}
	}

	public void refreshConnection() {
		if (isUpdateable()) {
			this.firePropertyChange("refreshConnection", null, null);
		}
	}

	public void refreshOutline() {
		if (isUpdateable()) {
			this.firePropertyChange("refreshOutline", null, null);
		}
	}

	public void refreshSettings() {
		if (isUpdateable()) {
			this.firePropertyChange("refreshSettings", null, null);
		}
	}

	public void refreshWithConnection() {
		if (isUpdateable()) {
			this.firePropertyChange("refreshWithConnection", null, null);
		}
	}

}
