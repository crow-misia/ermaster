package org.insightech.er.editor.controller.command.tracking;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.tracking.ChangeTracking;

/**
 * �ύX����ǉ��R�}���h
 */
public class AddChangeTrackingCommand extends AbstractCommand {

	private ERDiagram diagram;

	// �ύX����
	private ChangeTracking changeTracking;

	/**
	 * �ύX����ǉ��R�}���h���쐬���܂��B
	 * 
	 * @param diagram
	 * @param nodeElements
	 */
	public AddChangeTrackingCommand(ERDiagram diagram,
			ChangeTracking changeTracking) {
		this.diagram = diagram;

		this.changeTracking = changeTracking;
	}

	/**
	 * �ύX����ǉ����������s����
	 */
	@Override
	protected void doExecute() {
		this.diagram.getChangeTrackingList().addChangeTracking(changeTracking);
	}

	/**
	 * �ύX����ǉ����������ɖ߂�
	 */
	@Override
	protected void doUndo() {
		this.diagram.getChangeTrackingList().removeChangeTracking(
				changeTracking);
	}

}
