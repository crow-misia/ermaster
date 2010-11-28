package org.insightech.er.editor.controller.command.tracking;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.tracking.ChangeTracking;

/**
 * �ύX�����X�V�R�}���h
 */
public class UpdateChangeTrackingCommand extends AbstractCommand {

	// �ύX����
	private ChangeTracking changeTracking;

	private String oldComment;

	private String newComment;

	/**
	 * �ύX�����X�V�R�}���h���쐬���܂��B
	 * 
	 * @param changeTracking
	 * @param comment
	 */
	public UpdateChangeTrackingCommand(ChangeTracking changeTracking,
			String comment) {
		this.changeTracking = changeTracking;

		this.oldComment = changeTracking.getComment();
		this.newComment = comment;
	}

	/**
	 * �ύX�����X�V���������s����
	 */
	@Override
	protected void doExecute() {
		this.changeTracking.setComment(newComment);
	}

	/**
	 * �ύX�����X�V���������ɖ߂�
	 */
	@Override
	protected void doUndo() {
		this.changeTracking.setComment(oldComment);
	}

}
