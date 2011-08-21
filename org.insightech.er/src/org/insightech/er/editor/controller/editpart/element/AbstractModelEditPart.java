package org.insightech.er.editor.controller.editpart.element;

import java.beans.PropertyChangeListener;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;

public abstract class AbstractModelEditPart extends AbstractGraphicalEditPart
		implements PropertyChangeListener {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void activate() {
		super.activate();

		AbstractModel model = (AbstractModel) this.getModel();
		model.addPropertyChangeListener(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deactivate() {
		AbstractModel model = (AbstractModel) this.getModel();
		model.removePropertyChangeListener(this);

		super.deactivate();
	}

	protected ERDiagram getDiagram() {
		return (ERDiagram) this.getRoot().getContents().getModel();
	}

	protected Category getCurrentCategory() {
		return this.getDiagram().getCurrentCategory();
	}

	protected void executeCommand(Command command) {
		this.getViewer().getEditDomain().getCommandStack().execute(command);
	}
}
