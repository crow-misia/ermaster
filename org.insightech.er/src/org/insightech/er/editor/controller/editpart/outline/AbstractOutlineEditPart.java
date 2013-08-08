package org.insightech.er.editor.controller.editpart.outline;

import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.insightech.er.editor.controller.editpart.element.ERDiagramEditPart;
import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;

public abstract class AbstractOutlineEditPart<T> extends AbstractTreeEditPart
		implements PropertyChangeListener {
	/** Cache Model Children */
	private List<T> modelChildren;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void activate() {
		super.activate();
		((AbstractModel) getModel()).addPropertyChangeListener(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deactivate() {
		((AbstractModel) getModel()).removePropertyChangeListener(this);
		super.deactivate();
	}

	@Override
	public void refresh() {
		if (ERDiagramEditPart.isUpdateable()) {
			refreshChildren();
			refreshVisuals();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final public void refreshVisuals() {
		if (ERDiagramEditPart.isUpdateable()) {
			this.refreshOutlineVisuals();

			for (Object child : this.getChildren()) {
				AbstractOutlineEditPart<?> part = (AbstractOutlineEditPart<?>) child;
				part.refreshVisuals();
			}
		}
	}

	protected ERDiagram getDiagram() {
		return (ERDiagram) this.getRoot().getContents().getModel();
	}

	protected Category getCurrentCategory() {
		return this.getDiagram().getCurrentCategory();
	}

	protected void execute(Command command) {
		this.getViewer().getEditDomain().getCommandStack().execute(command);
	}

	@Override
	protected void refreshChildren() {
		this.modelChildren = null;
		super.refreshChildren();
	}

	@Override
	protected final List<T> getModelChildren() {
		if (modelChildren == null) {
			modelChildren = getModelChildrenInternal();
		}
		return modelChildren;
	}

	protected abstract void refreshOutlineVisuals();

	@SuppressWarnings("unchecked")
	protected List<T> getModelChildrenInternal() {
		return super.getModelChildren();
	}
}
