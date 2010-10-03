package org.insightech.er.editor.controller.editpart.element.connection;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.settings.CategorySetting;

public abstract class ERDiagramConnectionEditPart extends
		AbstractConnectionEditPart implements PropertyChangeListener {

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

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(
				ConnectionElement.PROPERTY_CHANGE_BEND_POINT)) {
			this.refreshBendpoints();

		} else if (event.getPropertyName().equals(
				ConnectionElement.PROPERTY_CHANGE_CONNECTION_ATTRIBUTE)) {
			this.refreshVisuals();
		}
	}

	protected ERDiagram getDiagram() {
		return (ERDiagram) this.getRoot().getContents().getModel();
	}

	protected Category getCurrentCategory() {
		return this.getDiagram().getCurrentCategory();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void refreshVisuals() {
		EditPart sourceEditPart = this.getSource();
		EditPart targetEditPart = this.getTarget();

		ERDiagram diagram = this.getDiagram();

		if (diagram != null) {
			this.figure.setVisible(false);

			Category category = this.getCurrentCategory();

			if (category != null) {
				CategorySetting categorySettings = this.getDiagram()
						.getDiagramContents().getSettings()
						.getCategorySetting();

				if (sourceEditPart != null && targetEditPart != null) {
					NodeElement sourceModel = (NodeElement) sourceEditPart
							.getModel();
					NodeElement targetModel = (NodeElement) targetEditPart
							.getModel();

					boolean containsSource = false;

					if (category.contains(sourceModel)) {
						containsSource = true;

					} else if (categorySettings.isShowReferredTables()) {
						for (NodeElement referringElement : sourceModel
								.getReferringElementList()) {
							if (category.contains(referringElement)) {
								containsSource = true;
								break;
							}
						}
					}

					if (containsSource) {
						if (category.contains(targetModel)) {
							this.figure.setVisible(true);

						} else if (categorySettings.isShowReferredTables()) {
							for (NodeElement referringElement : targetModel
									.getReferringElementList()) {
								if (category.contains(referringElement)) {
									this.figure.setVisible(true);
									break;
								}
							}
						}
					}
				}

			} else {
				this.figure.setVisible(true);
			}
		}
	}

	abstract protected void refreshBendpoints();
}
