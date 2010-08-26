package org.insightech.er.editor.controller.command.edit;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.controller.editpart.element.ERDiagramEditPart;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.DiagramContents;

/**
 * DiagramContents �̒u���R�}���h
 */
public class EditAllAttributesCommand extends AbstractCommand {

	private ERDiagram diagram;

	private DiagramContents oldDiagramContents;

	private DiagramContents newDiagramContents;

	/**
	 * �u���R�}���h���쐬���܂��B
	 * 
	 * @param diagram
	 * @param nodeElements
	 * @param columnGroups
	 */
	public EditAllAttributesCommand(ERDiagram diagram,
			DiagramContents newDiagramContents) {
		this.diagram = diagram;

		this.oldDiagramContents = this.diagram.getDiagramContents();
		this.newDiagramContents = newDiagramContents;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		// �`��X�V���Ƃ߂܂��B
		ERDiagramEditPart.setUpdateable(false);

		this.diagram.replaceContents(newDiagramContents);

		// �`��X�V���ĊJ���܂��B
		ERDiagramEditPart.setUpdateable(true);

		this.diagram.changeAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		// �`��X�V���Ƃ߂܂��B
		ERDiagramEditPart.setUpdateable(false);

		this.diagram.replaceContents(oldDiagramContents);

		// �`��X�V���ĊJ���܂��B
		ERDiagramEditPart.setUpdateable(true);

		this.diagram.changeAll();
	}

}
