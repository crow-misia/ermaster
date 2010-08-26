package org.insightech.er.editor.controller.editpart.element.node;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.Activator;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.table_view.ChangeTableViewPropertyCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.view.dialog.element.view.ViewDialog;
import org.insightech.er.editor.view.figure.view.ViewFigure;

public class ViewEditPart extends TableViewEditPart {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure createFigure() {
		ERDiagram diagram = this.getDiagram();
		Settings settings = diagram.getDiagramContents().getSettings();

		ViewFigure figure = new ViewFigure(settings.getTableStyle());

		this.changeFont(figure);

		return figure;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void performRequest(Request request) {
		try {
			View view = (View) this.getModel();
			ERDiagram diagram = this.getDiagram();

			if (request.getType().equals(RequestConstants.REQ_OPEN)) {
				View copyView = view.copyData();

				ViewDialog dialog = new ViewDialog(PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getShell(), this
						.getViewer(), copyView, diagram.getDiagramContents()
						.getGroups());

				if (dialog.open() == IDialogConstants.OK_ID) {
					CompoundCommand command = createChangeViewPropertyCommand(
							diagram, view, copyView);

					this.getViewer().getEditDomain().getCommandStack().execute(
							command.unwrap());
				}
			}
		} catch (Exception e) {
			Activator.showExceptionDialog(e);
		}
		super.performRequest(request);
	}

	public static CompoundCommand createChangeViewPropertyCommand(
			ERDiagram diagram, View view, View copyView) {
		CompoundCommand command = new CompoundCommand();

		ChangeTableViewPropertyCommand changeViewPropertyCommand = new ChangeTableViewPropertyCommand(
				view, copyView);
		command.add(changeViewPropertyCommand);

		return command;
	}

}
