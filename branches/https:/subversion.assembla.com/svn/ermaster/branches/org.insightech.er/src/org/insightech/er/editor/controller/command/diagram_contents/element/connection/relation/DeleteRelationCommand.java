package org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.insightech.er.Activator;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.DeleteConnectionCommand;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Dictionary;

public class DeleteRelationCommand extends DeleteConnectionCommand {

	private TableView oldTargetCopyTable;

	private TableView oldTargetTable;

	private Relation relation;

	private Boolean removeForeignKey;

	private Map<NormalColumn, NormalColumn> referencedColumnMap;

	public DeleteRelationCommand(Relation relation, Boolean removeForeignKey) {
		super(relation);

		this.relation = relation;
		this.oldTargetTable = relation.getTargetTableView();

		this.removeForeignKey = removeForeignKey;

		this.referencedColumnMap = new HashMap<NormalColumn, NormalColumn>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		if (this.oldTargetCopyTable == null) {
			for (NormalColumn foreignKey : relation.getForeignKeyColumns()) {
				NormalColumn referencedColumn = foreignKey
						.getReferencedColumn(relation);

				this.referencedColumnMap.put(foreignKey, referencedColumn);
			}

			this.oldTargetCopyTable = this.oldTargetTable.copyData();
		}

		Dictionary dictionary = this.oldTargetTable.getDiagram()
				.getDiagramContents().getDictionary();

		this.relation.delete(this.removeForeignKey, dictionary);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		super.doUndo();

		for (NormalColumn foreignKey : this.referencedColumnMap.keySet()) {
			if (!this.removeForeignKey) {
				Dictionary dictionary = this.oldTargetTable.getDiagram()
						.getDiagramContents().getDictionary();
				dictionary.remove(foreignKey);
			}

			foreignKey.addReference(this.referencedColumnMap.get(foreignKey),
					this.relation);
		}

		this.oldTargetCopyTable.restructureData(this.oldTargetTable);
	}

	@Override
	public boolean canExecute() {
		if (this.removeForeignKey == null) {
			if (this.relation.isReferedStrictly()) {
				if (this.isReferencedByMultiRelations()) {
					Activator
							.showErrorDialog("dialog.message.referenced.by.multi.foreign.key");
					return false;
				}

				this.removeForeignKey = false;

				this.referencedColumnMap = new HashMap<NormalColumn, NormalColumn>();

				for (NormalColumn foreignKey : relation.getForeignKeyColumns()) {
					NormalColumn referencedColumn = foreignKey
							.getReferencedColumn(relation);

					this.referencedColumnMap.put(foreignKey, referencedColumn);
				}

				return true;
			}

			if (Activator.showConfirmDialog(
					"dialog.message.confirm.remove.foreign.key", SWT.YES,
					SWT.NO)) {
				this.removeForeignKey = true;

			} else {
				this.removeForeignKey = false;

				this.referencedColumnMap = new HashMap<NormalColumn, NormalColumn>();

				for (NormalColumn foreignKey : relation.getForeignKeyColumns()) {
					NormalColumn referencedColumn = foreignKey
							.getReferencedColumn(relation);

					this.referencedColumnMap.put(foreignKey, referencedColumn);
				}
			}
		}

		return true;
	}

	private boolean isReferencedByMultiRelations() {
		for (NormalColumn foreignKeyColumn : relation.getForeignKeyColumns()) {
			for (NormalColumn childForeignKeyColumn : foreignKeyColumn
					.getForeignKeyList()) {
				if (childForeignKeyColumn.getRelationList().size() >= 2) {
					Set<TableView> referencedTables = new HashSet<TableView>();

					for (Relation relation : childForeignKeyColumn
							.getRelationList()) {
						referencedTables.add(relation.getSourceTableView());
					}

					if (referencedTables.size() >= 2) {
						return true;
					}
				}
			}
		}

		return false;
	}

}
