package org.insightech.er.editor.controller.editpart.outline.table;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.insightech.er.Activator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableSet;

public class TableSetOutlineEditPart extends AbstractOutlineEditPart {

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(TableSet.PROPERTY_CHANGE_TABLE_SET)) {
			refresh();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List getModelChildren() {
		TableSet tableSet = (TableSet) this.getModel();

		List<ERTable> modelChildren = new ArrayList<ERTable>();

		Category category = this.getCurrentCategory();
		for (ERTable table : tableSet) {
			if (category == null || category.contains(table)) {
				modelChildren.add(table);
			}
		}

		Collections.sort(modelChildren);

		return modelChildren;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void refreshOutlineVisuals() {
		this.setWidgetText(ResourceString.getResourceString("label.table"));
		this.setWidgetImage(Activator.getImage(ImageKey.DICTIONARY));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void refreshChildren() {
		super.refreshChildren();

		for (Object child : this.getChildren()) {
			EditPart part = (EditPart) child;
			part.refresh();
		}
	}

}
