package org.insightech.er.editor.controller.editpart.outline.group;

import java.util.Collections;
import java.util.List;

import org.insightech.er.Activator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GroupSet;

public class GroupSetOutlineEditPart extends AbstractOutlineEditPart {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List getModelChildren() {
		GroupSet columnGroupSet = (GroupSet) this.getModel();

		List<ColumnGroup> columnGroupList = columnGroupSet.getGroupList();

		Collections.sort(columnGroupList);

		return columnGroupList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void refreshOutlineVisuals() {
		this.setWidgetText(ResourceString
				.getResourceString("label.column.group")
				+ " ("
				+ this.getModelChildren().size() + ")");
		this.setWidgetImage(Activator.getImage(ImageKey.DICTIONARY));
	}

}
