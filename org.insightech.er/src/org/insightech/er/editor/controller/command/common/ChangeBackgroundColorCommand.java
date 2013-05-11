package org.insightech.er.editor.controller.command.common;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ViewableModel;

public class ChangeBackgroundColorCommand extends AbstractCommand {

	private ViewableModel model;

	private final int[] newColor;

	private final int[] oldColor;

	public ChangeBackgroundColorCommand(ViewableModel model, int red,
			int green, int blue) {
		this.model = model;
		this.oldColor = this.model.getColor();
		this.newColor = new int[] { red, green, blue, };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		this.model.setColor(newColor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		if (this.oldColor == null) {
			this.model.setColor(255, 255, 255);
		} else {
			this.model.setColor(this.oldColor);
		}
	}
}
