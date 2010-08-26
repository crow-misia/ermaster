package org.insightech.er.editor.model.diagram_contents.element.node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.diagram_contents.element.node.note.Note;
import org.insightech.er.editor.model.diagram_contents.element.node.note.NoteSet;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableSet;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.model.diagram_contents.element.node.view.ViewSet;

public class NodeSet extends AbstractModel implements Iterable<NodeElement> {

	private static final long serialVersionUID = -120487815554383179L;

	public static final String PROPERTY_CHANGE_CONTENTS = "contents";

	private NoteSet noteSet;

	private TableSet tableSet;

	private ViewSet viewSet;

	public NodeSet() {
		this.tableSet = new TableSet();
		this.viewSet = new ViewSet();
		this.noteSet = new NoteSet();
	}

	public void addNodeElement(NodeElement nodeElement) {
		if (nodeElement instanceof ERTable) {
			this.tableSet.add((ERTable) nodeElement);

		} else if (nodeElement instanceof View) {
			this.viewSet.add((View) nodeElement);

		} else if (nodeElement instanceof Note) {
			this.noteSet.add((Note) nodeElement);
		}

		this.firePropertyChange(PROPERTY_CHANGE_CONTENTS, null, null);
	}

	public void remove(NodeElement nodeElement) {
		if (nodeElement instanceof ERTable) {
			this.tableSet.remove((ERTable) nodeElement);

		} else if (nodeElement instanceof View) {
			this.viewSet.remove((View) nodeElement);

		} else if (nodeElement instanceof Note) {
			this.noteSet.remove((Note) nodeElement);
		}

		this.firePropertyChange(PROPERTY_CHANGE_CONTENTS, null, null);
	}

	public boolean contains(NodeElement nodeElement) {
		if (nodeElement instanceof ERTable) {
			return this.tableSet.getList().contains((ERTable) nodeElement);

		} else if (nodeElement instanceof View) {
			return this.viewSet.getList().contains((View) nodeElement);

		} else if (nodeElement instanceof Note) {
			return this.noteSet.getList().contains((Note) nodeElement);
		}

		return false;
	}

	public void clear() {
		this.tableSet.getList().clear();
		this.viewSet.getList().clear();
		this.noteSet.getList().clear();
	}

	public boolean isEmpty() {
		return this.tableSet.getList().isEmpty()
				&& this.viewSet.getList().isEmpty()
				&& this.noteSet.getList().isEmpty();
	}

	public List<NodeElement> getNodeElementList() {
		List<NodeElement> nodeElementList = new ArrayList<NodeElement>();

		nodeElementList.addAll(this.tableSet.getList());
		nodeElementList.addAll(this.viewSet.getList());
		nodeElementList.addAll(this.noteSet.getList());

		return nodeElementList;
	}

	public List<TableView> getTableViewList() {
		List<TableView> nodeElementList = new ArrayList<TableView>();

		nodeElementList.addAll(this.tableSet.getList());
		nodeElementList.addAll(this.viewSet.getList());

		return nodeElementList;
	}
	
	public Iterator<NodeElement> iterator() {
		return this.getNodeElementList().iterator();
	}

	public ViewSet getViewSet() {
		return viewSet;
	}

	public NoteSet getNoteSet() {
		return noteSet;
	}

	public TableSet getTableSet() {
		return tableSet;
	}

}
