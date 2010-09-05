package org.insightech.er.editor.controller.editpart.element.node;

import java.io.ByteArrayInputStream;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.EditPolicy;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.insightech.er.editor.controller.editpolicy.element.node.NodeElementComponentEditPolicy;
import org.insightech.er.editor.model.diagram_contents.element.node.image.InsertedImage;
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

}