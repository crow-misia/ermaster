package org.insightech.er.editor.controller.command.tracking;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.tracking.ChangeTrackingList;

/**
 * �ύX�����v�Z�R�}���h
 */
public class ResetChangeTrackingCommand extends AbstractCommand {

	private ERDiagram diagram;
	private ChangeTrackingList changeTrackingList;

	private boolean oldCalculated;

	public ResetChangeTrackingCommand(ERDiagram diagram) {
		this.diagram = diagram;
		this.changeTrackingList = this.diagram.getChangeTrackingList();
		this.oldCalculated = this.changeTrackingList.isCalculated();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		this.changeTrackingList.setCalculated(false);
		this.diagram.changeAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		this.changeTrackingList.setCalculated(oldCalculated);
		this.diagram.changeAll();
	}
}
