package org.insightech.er.editor.controller.editpart.element;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.SelectionManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.Activator;
import org.insightech.er.ResourceString;
import org.insightech.er.Resources;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.controller.editpart.element.node.NodeElementEditPart;
import org.insightech.er.editor.controller.editpolicy.ERDiagramLayoutEditPolicy;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.ViewableModel;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeSet;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.view.property_source.ERDiagramPropertySource;

public class ERDiagramEditPart extends AbstractModelEditPart {

	private static boolean updateable = true;

	public static void setUpdateable(boolean enabled) {
		updateable = enabled;
	}

	public static boolean isUpdateable() {
		return updateable;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deactivate() {
		try {
		super.deactivate();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure createFigure() {
		FreeformLayer layer = new FreeformLayer();
		layer.setLayoutManager(new FreeformLayout());

		return layer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createEditPolicies() {
		this.installEditPolicy(EditPolicy.LAYOUT_ROLE,
				new ERDiagramLayoutEditPolicy());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List getModelChildren() {
		List<Object> modelChildren = new ArrayList<Object>();

		ERDiagram diagram = (ERDiagram) this.getModel();

		modelChildren.addAll(diagram.getDiagramContents().getSettings()
				.getCategorySetting().getSelectedCategories());

		modelChildren.add(diagram.getDiagramContents().getSettings()
				.getModelProperties());
		modelChildren.addAll(diagram.getDiagramContents().getContents()
				.getNodeElementList());

		if (diagram.getChangeTrackingList().isCalculated()) {
			modelChildren.addAll(diagram.getChangeTrackingList()
					.getRemovedNodeElementSet());
		}

		return modelChildren;
	}

	@SuppressWarnings("unchecked")
	public void propertyChange(PropertyChangeEvent event) {
		try {
			if ("consumed".equals(event.getPropagationId())) {
				return;
			}

			if (event.getPropertyName()
					.equals(NodeSet.PROPERTY_CHANGE_CONTENTS)) {
				this.refreshChildren();

			} else if (event.getPropertyName().equals(
					ERDiagram.PROPERTY_CHANGE_ALL)) {
				this.refresh();
				this.refreshRelations();

				List<NodeElement> nodeElementList = (List<NodeElement>) event
						.getNewValue();

				if (nodeElementList != null) {
					this.getViewer().deselectAll();
					SelectionManager selectionManager = this.getViewer()
							.getSelectionManager();

					Map<NodeElement, EditPart> modelToEditPart = getModelToEditPart();

					for (NodeElement nodeElement : nodeElementList) {
						selectionManager.appendSelection(modelToEditPart
								.get(nodeElement));
					}
				}

			} else if (event.getPropertyName().equals(
					ViewableModel.PROPERTY_CHANGE_COLOR)) {
				this.refreshVisuals();

			} else if (event.getPropertyName().equals(
					ERDiagram.PROPERTY_CHANGE_DATABASE)) {
				this.changeDatabase(event);

			} else if (event.getPropertyName().equals(
					ERDiagramPropertySource.PROPERTY_INIT_DATABASE)) {
				ERDiagram diagram = (ERDiagram) this.getModel();
				diagram.restoreDatabase(DBManagerFactory.getAllDBList().get(0));

			} else if (event.getPropertyName().equals(
					ERDiagram.PROPERTY_CHANGE_SETTINGS)) {
				this.changeSettings();
			}
			
		} catch (Exception e) {
			Activator.showExceptionDialog(e);
		}
	}

	public void refreshRelations() {
		for (Object child : this.getChildren()) {
			if (child instanceof NodeElementEditPart) {
				NodeElementEditPart part = (NodeElementEditPart) child;
				part.refreshConnections();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refreshVisuals() {
		ERDiagram element = (ERDiagram) this.getModel();

		int[] color = element.getColor();

		if (color != null) {
			Color bgColor = Resources.getColor(color);
			this.getViewer().getControl().setBackground(bgColor);
		}

		for (Object child : this.getChildren()) {
			if (child instanceof NodeElementEditPart) {
				NodeElementEditPart part = (NodeElementEditPart) child;
				part.refreshVisuals();
			}
		}
	}

	private void changeSettings() {
		ERDiagram diagram = (ERDiagram) this.getModel();
		Settings settings = diagram.getDiagramContents().getSettings();

		for (Object child : this.getChildren()) {
			if (child instanceof NodeElementEditPart) {
				NodeElementEditPart part = (NodeElementEditPart) child;
				part.changeSettings(settings);
			}
		}
	}

	private void changeDatabase(PropertyChangeEvent event) {

		MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), SWT.ICON_QUESTION
				| SWT.OK | SWT.CANCEL);
		messageBox.setText(ResourceString
				.getResourceString("dialog.title.change.database"));
		messageBox.setMessage(ResourceString
				.getResourceString("dialog.message.change.database"));

		if (messageBox.open() == SWT.OK) {
			event.setPropagationId("consumed");

		} else {
			ERDiagram diagram = (ERDiagram) this.getModel();

			diagram.restoreDatabase(String.valueOf(event.getOldValue()));
		}
	}

	private Map<NodeElement, EditPart> getModelToEditPart() {
		Map<NodeElement, EditPart> modelToEditPart = new HashMap<NodeElement, EditPart>();
		List children = getChildren();

		for (int i = 0; i < children.size(); i++) {
			EditPart editPart = (EditPart) children.get(i);
			modelToEditPart.put((NodeElement) editPart.getModel(), editPart);
		}

		return modelToEditPart;
	}
}
