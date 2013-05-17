package org.insightech.er.editor.controller.command.diagram_contents.element.connection;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.connection.Bendpoint;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;

public class DefaultLineCommand extends AbstractCommand {

	private final int sourceXp;

	private final int sourceYp;

	private final int targetXp;

	private final int targetYp;

	private ConnectionElement connection;

	private List<Bendpoint> oldBendpointList;

	public DefaultLineCommand(ERDiagram diagram, ConnectionElement connection) {
		this.sourceXp = connection.getSourceXp();
		this.sourceYp = connection.getSourceYp();
		this.targetXp = connection.getTargetXp();
		this.targetYp = connection.getTargetYp();

		this.connection = connection;
		this.oldBendpointList = this.connection.getBendpoints();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		this.connection.setBendpoints(new ArrayList<Bendpoint>());

		this.connection.setSourceLocationp(-1, -1);
		this.connection.setTargetLocationp(-1, -1);
		this.connection.setParentMove();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		this.connection.setBendpoints(this.oldBendpointList);

		this.connection.setSourceLocationp(this.sourceXp, this.sourceYp);
		this.connection.setTargetLocationp(this.targetXp, this.targetYp);
		this.connection.setParentMove();
	}
}
