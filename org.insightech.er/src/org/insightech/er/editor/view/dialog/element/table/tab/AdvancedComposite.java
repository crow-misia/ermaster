package org.insightech.er.editor.view.dialog.element.table.tab;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.db.DBManager;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.util.Format;

public abstract class AdvancedComposite extends Composite {

	private Combo tableSpaceCombo;

	private Combo characterSetCombo;

	private Combo collationCombo;

	private Text schemaText;

	protected TableProperties tableProperties;

	protected ERDiagram diagram;

	protected AbstractDialog dialog;

	protected ERTable table;

	protected DBManager dbManager;

	public AdvancedComposite(Composite parent) {
		super(parent, SWT.NONE);
	}

	public final void initialize(AbstractDialog dialog, String database,
			TableProperties tableProperties, ERDiagram diagram, ERTable table) {
		this.dialog = dialog;
		this.tableProperties = tableProperties;
		this.diagram = diagram;
		this.table = table;
		this.dbManager = DBManagerFactory.getDBManager(database);
		
		this.initComposite();
		this.addListener();
		this.setData();
	}

	protected void initComposite() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;

		this.setLayout(gridLayout);

		this.tableSpaceCombo = CompositeFactory.createReadOnlyCombo(null, this,
				"label.tablespace");
		this.schemaText = CompositeFactory.createText(null, this,
				"label.schema", 1, 120, false);

		this.characterSetCombo = CompositeFactory.createReadOnlyCombo(dialog, this,
				"label.character.set");
		this.characterSetCombo.setVisibleItemCount(20);

		this.collationCombo = CompositeFactory.createReadOnlyCombo(this.dialog, this,
				"label.collation");
		this.collationCombo.setVisibleItemCount(20);

		this.initTablespaceCombo();
		this.initCharacterSetCombo();
	}

	protected void addListener() {
		this.characterSetCombo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				final String selectedCollation = characterSetCombo.getText();

				collationCombo.removeAll();
				collationCombo.add("");

				for (final String collation : dbManager.getCollationList(selectedCollation)) {
					collationCombo.add(collation);
				}
				
				collationCombo.setVisible(collationCombo.getItemCount() > 1);

				final int index = collationCombo.indexOf(selectedCollation);

				collationCombo.select(index);
			}
		});
	}

	private void initTablespaceCombo() {
		this.tableSpaceCombo.add("");

		for (Tablespace tablespace : this.diagram.getDiagramContents()
				.getTablespaceSet()) {
			this.tableSpaceCombo.add(tablespace.getName());
		}
	}

	private void initCharacterSetCombo() {
		final List<String> characterSetList = dbManager.getCharacterSetList();
		if (characterSetList.isEmpty()) {
			this.characterSetCombo.setVisible(false);
		} else {
			this.characterSetCombo.setVisible(true);

			this.characterSetCombo.add("");
			for (final String characterSet : characterSetList) {
				this.characterSetCombo.add(characterSet);
			}
		}
	}

	protected void setData() {
		Tablespace tablespace = this.tableProperties.getTableSpace();

		if (tablespace != null) {
			int index = this.diagram.getDiagramContents().getTablespaceSet()
					.getTablespaceList().indexOf(tablespace);
			this.tableSpaceCombo.select(index + 1);
		}

		if (this.tableProperties.getSchema() != null && this.schemaText != null) {
			this.schemaText.setText(this.tableProperties.getSchema());
		}

		final String characterSet = this.tableProperties.getCharacterSet();
		final List<String> collations = dbManager.getCollationList(Format.toString(characterSet));
		if (collations.isEmpty()) {
			this.collationCombo.setVisible(false);
		} else {
			this.collationCombo.setVisible(true);
			this.collationCombo.add("");
			for (final String collation : collations) {
				this.collationCombo.add(collation);
			}
		}
		this.characterSetCombo.setText(Format.toString(characterSet));
		this.collationCombo.setText(Format.toString(this.tableProperties.getCollation()));
	}

	public void validate() throws InputException {
		if (this.tableSpaceCombo != null) {
			int tablespaceIndex = this.tableSpaceCombo.getSelectionIndex();
			if (tablespaceIndex > 0) {
				Tablespace tablespace = this.diagram.getDiagramContents()
						.getTablespaceSet().getTablespaceList()
						.get(tablespaceIndex - 1);
				this.tableProperties.setTableSpace(tablespace);

			} else {
				this.tableProperties.setTableSpace(null);
			}
		}

		if (this.schemaText != null) {
			this.tableProperties.setSchema(this.schemaText.getText());
		}

		String characterSet = this.characterSetCombo.getText();
		this.tableProperties.setCharacterSet(characterSet);

		String collation = this.collationCombo.getText();
		this.tableProperties.setCollation(collation);
	}

	public void setInitFocus() {
		this.tableSpaceCombo.setFocus();
	}
}
