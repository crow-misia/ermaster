package org.insightech.er.editor.controller.command.common;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ViewableModel;

public class ChangeFontCommand extends AbstractCommand {

	private ViewableModel viewableModel;

	private String oldFontName;

	private String newFontName;

	private int oldFontSize;

	private int newFontSize;

	public ChangeFontCommand(ViewableModel viewableModel, String fontName,
			int fontSize) {
		this.viewableModel = viewableModel;

		this.oldFontName = viewableModel.getFontName();
		this.oldFontSize = viewableModel.getFontSize();

		this.newFontName = fontName;
		this.newFontSize = fontSize;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		this.viewableModel.setFontName(this.newFontName);
		this.viewableModel.setFontSize(this.newFontSize);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		this.viewableModel.setFontName(this.oldFontName);
		this.viewableModel.setFontSize(this.oldFontSize);
	}
}
