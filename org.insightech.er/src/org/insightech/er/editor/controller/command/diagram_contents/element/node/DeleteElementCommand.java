package org.insightech.er.editor.controller.command.diagram_contents.element.node;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;

public class DeleteElementCommand extends AbstractCommand {

	private ERDiagram diagram;

	private NodeElement element;

	public DeleteElementCommand(ERDiagram diagram, NodeElement element) {
		this.diagram = diagram;
		this.element = element;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		this.diagram.removeContent(this.element);
		this.diagram.refreshChildren();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		this.diagram.addContent(this.element);
		this.diagram.refreshChildren();
	}
}
