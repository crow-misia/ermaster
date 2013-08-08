package org.insightech.er.editor.controller.editpart.element.connection;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.draw2d.AbsoluteBendpoint;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.RelativeBendpoint;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.insightech.er.Activator;
import org.insightech.er.editor.controller.editpart.element.node.NodeElementEditPart;
import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.connection.Bendpoint;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.settings.CategorySetting;
import org.insightech.er.editor.view.figure.anchor.XYChopboxAnchor;

public abstract class ERDiagramConnectionEditPart extends
		AbstractConnectionEditPart implements PropertyChangeListener {

	private static Logger logger = Logger
			.getLogger(ERDiagramConnectionEditPart.class.getName());

	private static final boolean DEBUG = false;

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
		try {
			if (DEBUG) {
				logger.log(Level.INFO, this.getClass().getName() + ":"
						+ event.getPropertyName() + ":" + event.toString());
			}

			if (event.getPropertyName().equals(
					ConnectionElement.PROPERTY_CHANGE_BEND_POINT)) {
				this.refreshBendpoints();

			} else if (event.getPropertyName().equals(
					ConnectionElement.PROPERTY_CHANGE_CONNECTION_ATTRIBUTE)) {
				this.refreshVisuals();
			}

		} catch (Exception e) {
			Activator.showExceptionDialog(e);
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

	protected void refreshBendpoints() {
		this.calculateAnchorLocation();
		
		// ベンド・ポイントの位置情報の取得
		final ConnectionElement connection = (ConnectionElement) this.getModel();
		
		// 実際のベンド・ポイントのリスト
		List<org.eclipse.draw2d.Bendpoint> constraint = new ArrayList<org.eclipse.draw2d.Bendpoint>();
		for (Bendpoint bendPoint : connection.getBendpoints()) {
			if (bendPoint.isRelative()) {
				NodeElementEditPart editPart = (NodeElementEditPart) this
						.getSource();
				if (editPart != null) {
					Rectangle bounds = editPart.getFigure()
							.getBounds();
					int width = bounds.width;
					int height = bounds.height;

					if (width == 0) {
						bounds = editPart.getFigure().getBounds();
						width = bounds.width;
						height = bounds.height;
					}
					RelativeBendpoint point = new RelativeBendpoint();

					int xp = connection.getTargetXp();
					int x;

					if (xp == -1) {
						x = bounds.x + bounds.width;
					} else {
						x = bounds.x + (bounds.width * xp / 100);
					}

					point.setRelativeDimensions(new Dimension(width
							* bendPoint.getX() / 100 - bounds.x
							- bounds.width + x, 0), new Dimension(width
							* bendPoint.getX() / 100 - bounds.x
							- bounds.width + x, 0));
					point.setWeight(0);
					point.setConnection(this.getConnectionFigure());

					constraint.add(point);

					point = new RelativeBendpoint();
					point.setRelativeDimensions(new Dimension(width
							* bendPoint.getX() / 100 - bounds.x
							- bounds.width + x, height * bendPoint.getY()
							/ 100), new Dimension(width * bendPoint.getX()
							/ 100 - bounds.x - bounds.width + x, height
							* bendPoint.getY() / 100));
					point.setWeight(0);
					point.setConnection(this.getConnectionFigure());

					constraint.add(point);

					point = new RelativeBendpoint();
					point.setRelativeDimensions(
							new Dimension(x - bounds.x - bounds.width,
									height * bendPoint.getY() / 100),
							new Dimension(x - bounds.x - bounds.width,
									height * bendPoint.getY() / 100));
					point.setWeight(0);
					point.setConnection(this.getConnectionFigure());

					constraint.add(point);
				}

			} else {
				constraint.add(new AbsoluteBendpoint(bendPoint.getX(),
						bendPoint.getY()));
			}
		}

		this.getConnectionFigure().setRoutingConstraint(constraint);
	}

	private void calculateAnchorLocation() {
		ConnectionElement connection = (ConnectionElement) this.getModel();

		NodeElementEditPart sourceEditPart = (NodeElementEditPart) this.getSource();

		Point sourcePoint = null;
		Point targetPoint = null;

		if (sourceEditPart != null && connection.getSourceXp() != -1
				&& connection.getSourceYp() != -1) {
			Rectangle bounds = sourceEditPart.getFigure().getBounds();
			sourcePoint = new Point(bounds.x
					+ (bounds.width * connection.getSourceXp() / 100), bounds.y
					+ (bounds.height * connection.getSourceYp() / 100));
		}

		NodeElementEditPart targetEditPart = (NodeElementEditPart) this.getTarget();

		if (targetEditPart != null && connection.getTargetXp() != -1
				&& connection.getTargetYp() != -1) {
			Rectangle bounds = targetEditPart.getFigure().getBounds();
			targetPoint = new Point(bounds.x
					+ (bounds.width * connection.getTargetXp() / 100), bounds.y
					+ (bounds.height * connection.getTargetYp() / 100));
		}

		ConnectionAnchor sourceAnchor = this.getConnectionFigure()
				.getSourceAnchor();

		if (sourceAnchor instanceof XYChopboxAnchor) {
			((XYChopboxAnchor) sourceAnchor).setLocation(sourcePoint);
		}

		ConnectionAnchor targetAnchor = this.getConnectionFigure()
				.getTargetAnchor();

		if (targetAnchor instanceof XYChopboxAnchor) {
			((XYChopboxAnchor) targetAnchor).setLocation(targetPoint);
		}
	}

}
