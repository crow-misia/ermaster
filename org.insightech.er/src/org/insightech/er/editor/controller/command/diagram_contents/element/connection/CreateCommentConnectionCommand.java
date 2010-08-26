package org.insightech.er.editor.controller.command.diagram_contents.element.connection;

import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.node.note.Note;

public class CreateCommentConnectionCommand extends CreateConnectionCommand {

	public CreateCommentConnectionCommand(ConnectionElement connection) {
		super(connection);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canExecute() {
		if (!super.canExecute()) {
			return false;
		}

		if (!(this.getSourceModel() instanceof Note)
				&& !(this.getTargetModel() instanceof Note)) {
			return false;
		}

		return true;
	}

}
