package org.insightech.er.editor.controller.editpart.element.node;

import java.io.ByteArrayInputStream;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.Activator;
import org.insightech.er.editor.controller.editpolicy.element.node.NodeElementComponentEditPolicy;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.image.InsertedImage;
import org.insightech.er.editor.view.dialog.element.InsertedImageDialog;
import org.insightech.er.editor.view.figure.InsertedImageFigure;

public class InsertedImageEditPart extends NodeElementEditPart implements
		IResizable {

	private Image image;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure createFigure() {
		InsertedImage model = (InsertedImage) this.getModel();

		byte[] data = Base64.decodeBase64((model.getBase64EncodedData()
				.getBytes()));
		ByteArrayInputStream in = new ByteArrayInputStream(data);

		this.image = new Image(Display.getCurrent(), in);

		InsertedImageFigure figure = new InsertedImageFigure(image);
		figure.setMinimumSize(new Dimension(1, 1));

		return figure;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void disposeFont() {
		if (this.image != null) {
			this.image.dispose();
		}
		super.disposeFont();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createEditPolicies() {
		this.installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new NodeElementComponentEditPolicy());

		super.createEditPolicies();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void performRequest(Request request) {
		try {
			InsertedImage insertedImage = (InsertedImage) this.getModel();
			ERDiagram diagram = this.getDiagram();

			if (request.getType().equals(RequestConstants.REQ_OPEN)) {
				InsertedImageDialog dialog = new InsertedImageDialog(PlatformUI
						.getWorkbench().getActiveWorkbenchWindow().getShell(),
						insertedImage);

				if (dialog.open() == IDialogConstants.OK_ID) {
//					CompoundCommand command = createChangeTablePropertyCommand(
//							diagram, table, copyTable);
//
//					this.getViewer().getEditDomain().getCommandStack().execute(
//							command.unwrap());
				}
			}
		} catch (Exception e) {
			Activator.showExceptionDialog(e);
		}
		
		super.performRequest(request);
	}
}
