package org.insightech.er.editor.controller.command.diagram_contents.element.connection;

import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;

public class CreateConnectionCommand extends AbstractCreateConnectionCommand {

	private ConnectionElement connection;

	public CreateConnectionCommand(ConnectionElement connection) {
		super();
		this.connection = connection;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		connection.setSource((NodeElement) source.getModel());
		connection.setTarget((NodeElement) target.getModel());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		connection.setSource(null);
		connection.setTarget(null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String validate() {
		return null;
	}

}
