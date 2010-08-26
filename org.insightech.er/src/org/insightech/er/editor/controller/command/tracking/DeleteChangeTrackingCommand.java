package org.insightech.er.editor.controller.command.tracking;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.tracking.ChangeTracking;

/**
 * �ύX�����폜�R�}���h
 */
public class DeleteChangeTrackingCommand extends AbstractCommand {

	private ERDiagram diagram;

	// �ύX����
	private ChangeTracking changeTracking;

	private int index;

	/**
	 * �ύX�����폜�R�}���h���쐬���܂��B
	 * 
	 * @param diagram
	 * @param index
	 */
	public DeleteChangeTrackingCommand(ERDiagram diagram, int index) {
		this.diagram = diagram;

		this.index = index;
		this.changeTracking = this.diagram.getChangeTrackingList().get(index);
	}

	/**
	 * �ύX�����폜���������s����
	 */
	@Override
	protected void doExecute() {
		this.diagram.getChangeTrackingList().removeChangeTracking(index);
	}

	/**
	 * �ύX�����폜���������ɖ߂�
	 */
	@Override
	protected void doUndo() {
		this.diagram.getChangeTrackingList().addChangeTracking(index,
				changeTracking);
	}

}
