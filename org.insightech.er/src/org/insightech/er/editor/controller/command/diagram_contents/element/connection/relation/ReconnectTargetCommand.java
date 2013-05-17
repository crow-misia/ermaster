package org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;

public class ReconnectTargetCommand extends AbstractCommand {

	private final ConnectionElement connection;

	private final int xp;

	private final int yp;

	private final int oldXp;

	private final int oldYp;

	public ReconnectTargetCommand(ConnectionElement connection, int xp, int yp) {
		this.connection = connection;

		this.xp = xp;
		this.yp = yp;
		this.oldXp = connection.getTargetXp();
		this.oldYp = connection.getTargetYp();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		connection.setTargetLocationp(this.xp, this.yp);
		connection.setParentMove();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		connection.setTargetLocationp(this.oldXp, this.oldYp);
		connection.setParentMove();
	}
}
