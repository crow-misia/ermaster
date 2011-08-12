package org.insightech.er.editor.view.figure;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.insightech.er.Resources;

public class DropShadowRectangle extends RoundedRectangle {

	public static int SHADOW_INSET = 5;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void fillShape(Graphics graphics) {
		Rectangle f = Rectangle.SINGLETON.setBounds(getBounds());
		Insets shadowInset = new Insets(0, 0, SHADOW_INSET, SHADOW_INSET);
		f.shrink(shadowInset);
		this.drawShadow(f, graphics);
		graphics.fillRoundRectangle(f, getCornerDimensions().width,
				getCornerDimensions().height);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Insets getInsets() {
		return new Insets(1, 1, SHADOW_INSET + 1, SHADOW_INSET + 1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void outlineShape(Graphics graphics) {
		Rectangle f = Rectangle.SINGLETON.setBounds(getBounds());
		Insets shadowInset = new Insets(getLineWidth() / 2, getLineWidth() / 2, getLineWidth()
				+ SHADOW_INSET, getLineWidth() + SHADOW_INSET);
		f.shrink(shadowInset);
		graphics.drawRoundRectangle(f, getCornerDimensions().width, getCornerDimensions().height);
	}

	private void drawShadow(Rectangle rectangle, Graphics graphics) {
		int rgb = 255;
		int delta = 255 / SHADOW_INSET;

		for (int i = 0; i < SHADOW_INSET - 1; i++) {
			rgb -= delta;
			Color color = Resources.getColor(new int[] { rgb, rgb, rgb });
			this.drawShadowLayer(rectangle, graphics, SHADOW_INSET - 1 - i,
					color);
		}
	}

	private void drawShadowLayer(Rectangle rectangle, Graphics graphics,
			int offset, Color color) {

		// Save the state of the graphics object
		graphics.pushState();
		graphics.setLineWidth(0);
		graphics.setBackgroundColor(color);
		Rectangle shadowLayer = new Rectangle(rectangle);
		shadowLayer.x += offset;
		shadowLayer.y += offset;
		graphics.fillRoundRectangle(shadowLayer, getCornerDimensions().width,
				getCornerDimensions().height);
		// Restore the start of the graphics object
		graphics.popState();
	}

}
