package org.insightech.er.editor.controller.editpart.element.connection;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.ConnectionEndpointLocator;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation.ChangeRelationPropertyCommand;
import org.insightech.er.editor.controller.editpolicy.element.connection.RelationBendpointEditPolicy;
import org.insightech.er.editor.controller.editpolicy.element.connection.RelationEditPolicy;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.view.dialog.element.relation.RelationDialog;
import org.insightech.er.editor.view.figure.connection.ERDiagramConnection;
import org.insightech.er.editor.view.figure.connection.decoration.DecorationFactory;
import org.insightech.er.editor.view.figure.connection.decoration.DecorationFactory.Decoration;

public class RelationEditPart extends ERDiagramConnectionEditPart {

	private Label targetLabel;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure createFigure() {
		boolean bezier = this.getDiagram().getDiagramContents().getSettings()
				.isUseBezierCurve();
		PolylineConnection connection = new ERDiagramConnection(bezier);
		connection.setConnectionRouter(new BendpointConnectionRouter());

		ConnectionEndpointLocator targetLocator = new ConnectionEndpointLocator(
				connection, true);
		this.targetLabel = new Label("");
		connection.add(targetLabel, targetLocator);

		return connection;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createEditPolicies() {
		this.installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE,
				new ConnectionEndpointEditPolicy());
		this.installEditPolicy(EditPolicy.CONNECTION_ROLE,
				new RelationEditPolicy());
		this.installEditPolicy(EditPolicy.CONNECTION_BENDPOINTS_ROLE,
				new RelationBendpointEditPolicy());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void refreshVisuals() {
		super.refreshVisuals();

		ERDiagram diagram = this.getDiagram();

		if (diagram != null) {
			Relation relation = (Relation) this.getModel();

			PolylineConnection connection = (PolylineConnection) this
					.getConnectionFigure();

			String notation = diagram.getDiagramContents().getSettings()
					.getNotation();

			Decoration decoration = DecorationFactory.getDecoration(notation,
					relation.getParentCardinality(), relation
							.getChildCardinality());

			connection.setSourceDecoration(decoration.getSourceDecoration());
			connection.setTargetDecoration(decoration.getTargetDecoration());
			targetLabel.setText(StringUtils.defaultString(decoration.getTargetLabel()));
		}

		this.refreshBendpoints();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void performRequest(Request request) {
		Relation relation = (Relation) this.getModel();

		if (request.getType().equals(RequestConstants.REQ_OPEN)) {
			Relation copy = relation.copy();

			RelationDialog dialog = new RelationDialog(PlatformUI
					.getWorkbench().getActiveWorkbenchWindow().getShell(), copy);

			if (dialog.open() == IDialogConstants.OK_ID) {
				ChangeRelationPropertyCommand command = new ChangeRelationPropertyCommand(
						relation, copy);
				this.getViewer().getEditDomain().getCommandStack().execute(
						command);
			}
		}

		super.performRequest(request);
	}

}
