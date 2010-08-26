package org.insightech.er.editor.controller.command.tracking;

import java.util.List;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeSet;
import org.insightech.er.editor.model.tracking.ChangeTrackingList;
import org.insightech.er.editor.model.tracking.RemovedNodeElement;
import org.insightech.er.editor.model.tracking.UpdatedNodeElement;

/**
 * �ύX�����v�Z�R�}���h
 */
public class CalculateChangeTrackingCommand extends AbstractCommand {

	private ERDiagram diagram;

	private NodeSet comparison;

	private ChangeTrackingList changeTrackingList;

	private List<NodeElement> oldAddedNodeElements;

	private List<UpdatedNodeElement> oldUpdatedNodeElements;

	private List<RemovedNodeElement> oldRemovedNodeElements;

	/**
	 * �ύX�����v�Z�R�}���h���쐬���܂��B
	 * 
	 * @param diagram
	 * @param comparison
	 */
	public CalculateChangeTrackingCommand(ERDiagram diagram, NodeSet comparison) {
		this.diagram = diagram;
		this.comparison = comparison;

		this.changeTrackingList = this.diagram.getChangeTrackingList();

		this.oldAddedNodeElements = this.changeTrackingList
				.getAddedNodeElementSet();
		this.oldUpdatedNodeElements = this.changeTrackingList
				.getUpdatedNodeElementSet();
		this.oldRemovedNodeElements = this.changeTrackingList
				.getRemovedNodeElementSet();
	}

	/**
	 * �ύX�����v�Z���������s����
	 */
	@Override
	protected void doExecute() {
		this.changeTrackingList.calculateUpdatedNodeElementSet(this.comparison,
				this.diagram.getDiagramContents().getContents());
		this.diagram.changeAll();
	}

	/**
	 * �ύX�����v�Z���������ɖ߂�
	 */
	@Override
	protected void doUndo() {
		this.changeTrackingList.restore(this.oldAddedNodeElements,
				this.oldUpdatedNodeElements, this.oldRemovedNodeElements);
		this.diagram.changeAll();
	}
}
