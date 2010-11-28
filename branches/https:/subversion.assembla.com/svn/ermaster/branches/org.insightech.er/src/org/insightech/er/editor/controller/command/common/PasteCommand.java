package org.insightech.er.editor.controller.command.common;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.controller.editpart.element.ERDiagramEditPart;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.Location;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeSet;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GroupSet;

public class PasteCommand extends AbstractCommand {

	private ERDiagram diagram;

	private GraphicalViewer viewer;

	// �\��t���Ώۂ̈ꗗ
	private NodeSet nodeElements;

	// �\��t�����ɒǉ�����O���[�v��̈ꗗ
	private GroupSet columnGroups;

	/**
	 * �\��t���R�}���h���쐬���܂��B
	 * 
	 * @param editor
	 * @param nodeElements
	 */
	public PasteCommand(ERDiagramEditor editor, NodeSet nodeElements, int x,
			int y) {
		this.viewer = editor.getGraphicalViewer();
		this.diagram = (ERDiagram) viewer.getContents().getModel();

		this.nodeElements = nodeElements;

		this.columnGroups = new GroupSet();

		// �\��t���Ώۂɑ΂��ď������J��Ԃ��܂�
		for (NodeElement nodeElement : nodeElements) {
			nodeElement.setLocation(new Location(nodeElement.getX() + x,
					nodeElement.getY() + y, nodeElement.getWidth(), nodeElement
							.getHeight()));

			// �\��t���Ώۂ��e�[�u���̏ꍇ
			if (nodeElement instanceof ERTable) {

				ERTable table = (ERTable) nodeElement;

				// ��ɑ΂��ď������J��Ԃ��܂�
				for (Column column : table.getColumns()) {

					// �񂪃O���[�v��̏ꍇ
					if (column instanceof ColumnGroup) {
						ColumnGroup group = (ColumnGroup) column;

						// ���̐}�̃O���[�v��łȂ��ꍇ
						if (!diagram.getDiagramContents().getGroups().contains(
								group)) {
							// �Ώۂ̃O���[�v��ɒǉ����܂��B
							columnGroups.add(group);
						}
					}
				}
			}
		}
	}

	/**
	 * �\��t�����������s����
	 */
	@Override
	protected void doExecute() {
		// �`��X�V���Ƃ߂܂��B
		ERDiagramEditPart.setUpdateable(false);

		GroupSet columnGroupSet = this.diagram.getDiagramContents().getGroups();

		// �}�Ƀm�[�h��ǉ����܂��B
		for (NodeElement nodeElement : this.nodeElements) {
			this.diagram.addContent(nodeElement);
		}

		// �O���[�v���ǉ����܂��B
		for (ColumnGroup columnGroup : this.columnGroups) {
			columnGroupSet.add(columnGroup);
		}

		// �`��X�V���ĊJ���܂��B
		ERDiagramEditPart.setUpdateable(true);

		this.diagram.changeAll();

		// �\��t����ꂽ�e�[�u����I����Ԃɂ��܂��B
		this.setFocus();
	}

	/**
	 * �\��t�����������ɖ߂�
	 */
	@Override
	protected void doUndo() {
		// �`��X�V���Ƃ߂܂��B
		ERDiagramEditPart.setUpdateable(false);

		GroupSet columnGroupSet = this.diagram.getDiagramContents().getGroups();

		// �}����m�[�h���폜���܂��B
		for (NodeElement nodeElement : this.nodeElements) {
			this.diagram.removeContent(nodeElement);
		}

		// �O���[�v����폜���܂��B
		for (ColumnGroup columnGroup : this.columnGroups) {
			columnGroupSet.remove(columnGroup);
		}

		// �`��X�V���ĊJ���܂��B
		ERDiagramEditPart.setUpdateable(true);

		this.diagram.changeAll();
	}

	/**
	 * �\��t����ꂽ�e�[�u����I����Ԃɂ��܂��B
	 */
	private void setFocus() {
		// �\��t����ꂽ�e�[�u����I����Ԃɂ��܂��B
		for (NodeElement nodeElement : this.nodeElements) {
			EditPart editPart = (EditPart) viewer.getEditPartRegistry().get(
					nodeElement);

			this.viewer.getSelectionManager().appendSelection(editPart);
		}
	}
}
