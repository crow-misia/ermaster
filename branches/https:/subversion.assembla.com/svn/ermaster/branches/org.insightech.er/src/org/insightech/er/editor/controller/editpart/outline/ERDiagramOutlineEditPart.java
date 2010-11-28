package org.insightech.er.editor.controller.editpart.outline;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.DiagramContents;

public class ERDiagramOutlineEditPart extends AbstractOutlineEditPart {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List getModelChildren() {
		List<AbstractModel> modelChildren = new ArrayList<AbstractModel>();
		ERDiagram diagram = (ERDiagram) this.getModel();
		DiagramContents diagramContents = diagram.getDiagramContents();

		modelChildren.add(diagramContents.getDictionary());
		modelChildren.add(diagramContents.getGroups());
		modelChildren.add(diagramContents.getContents().getTableSet());
		modelChildren.add(diagramContents.getContents().getViewSet());
		modelChildren.add(diagramContents.getTriggerSet());
		modelChildren.add(diagramContents.getSequenceSet());
		modelChildren.add(diagramContents.getIndexSet());
		modelChildren.add(diagramContents.getTablespaceSet());

		return modelChildren;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(ERDiagram.PROPERTY_CHANGE_ALL)) {
			refresh();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void refreshOutlineVisuals() {
		for (Object child : this.getChildren()) {
			EditPart part = (EditPart) child;
			part.refresh();
		}
	}
}
