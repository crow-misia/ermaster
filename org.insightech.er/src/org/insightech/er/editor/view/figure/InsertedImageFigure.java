package org.insightech.er.editor.view.figure;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;

public class InsertedImageFigure extends Figure {

	private Image img;

	// private Dimension imageSize;

	/**
	 * Constructor
	 * 
	 * @param image
	 *            The Image to be displayed
	 */
	public InsertedImageFigure(Image image) {
		// setImage(image);
		this.img = image;
	}

	/**
	 * @return The Image that this Figure displays
	 */
	public Image getImage() {
		return img;
	}

	@Override
	protected void paintFigure(Graphics graphics) {
		super.paintFigure(graphics);

		if (getImage() == null)
			return;

		Rectangle area = getClientArea();
		// Rectangle destination = new Rectangle();
		//
		// double dw = (double) imageSize.width / (double) area.width;
		// double dh = (double) imageSize.height / (double) area.height;
		//
		// if (dw < 1 && dh < 1) {
		// // image is smaller than the figure bounds
		// destination.width = imageSize.width;
		// destination.height = imageSize.height;
		// } else if (dw > dh) {
		// // we must limit the size by the width
		// destination.width = area.width;
		// destination.height = (int) (imageSize.height / dw);
		// } else {
		// // we must limit the size by the height
		// destination.width = (int) (imageSize.width / dh);
		// destination.height = area.height;
		// }
		// destination.x = (area.width - destination.width) / 2 + area.x;
		// destination.y = (area.height - destination.height) / 2 + area.y;

		graphics.drawImage(getImage(), new Rectangle(getImage().getBounds()),
				area);

	}

	// /**
	// * Sets the Image that this ImageFigure displays.
	// * <p>
	// * IMPORTANT: Note that it is the client's responsibility to dispose the
	// * given image.
	// *
	// * @param image
	// * The Image to be displayed. It can be <code>null</code>.
	// */
	// public void setImage(Image image) {
	// if (img == image)
	// return;
	// img = image;
	// if (img != null) {
	// imageSize = new Dimension(getImage().getBounds().width, getImage()
	// .getBounds().height);
	// } else {
	// imageSize = null;
	// }
	// revalidate();
	// repaint();
	// }

}
