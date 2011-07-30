package org.insightech.er.db.impl.mysql;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.insightech.er.ResourceString;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.view.dialog.element.table.tab.AdvancedComposite;
import org.insightech.er.util.Format;

public class MySQLAdvancedComposite extends AdvancedComposite {

	private Combo engineCombo;

	private Combo characterSetCombo;

	private Combo collationCombo;

	public MySQLAdvancedComposite(Composite parent) {
		super(parent);
	}

	@Override
	protected void initComposite() {
		super.initComposite();

		this.engineCombo = createEngineCombo(this, null);

		Label label = new Label(this, SWT.NONE);
		label.setText(ResourceString.getResourceString("label.character.set"));
		createCharacterSetCombo();

		label = new Label(this, SWT.NONE);
		label.setText(ResourceString.getResourceString("label.collation"));

		GridData gridData = new GridData();
		gridData.widthHint = 150;

		this.collationCombo = new Combo(this, SWT.NONE);
		this.collationCombo.setVisibleItemCount(20);
		this.collationCombo.setLayoutData(gridData);

		this.characterSetCombo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				String selectedCollation = collationCombo.getText();

				collationCombo.removeAll();
				collationCombo.add("");

				for (String collation : MySQLDBManager
						.getCollationList(characterSetCombo.getText())) {
					collationCombo.add(collation);
				}

				int index = collationCombo.indexOf(selectedCollation);

				collationCombo.select(index);
			}
		});

	}

	/**
	 * This method initializes combo
	 * 
	 */
	public static Combo createEngineCombo(Composite parent,
			AbstractDialog dialog) {
		Combo combo = CompositeFactory.createCombo(dialog, parent,
				"label.storage.engine", 1);
		combo.setVisibleItemCount(20);

		initEngineCombo(combo);

		return combo;
	}

	/**
	 * This method initializes combo1
	 * 
	 */
	private void createCharacterSetCombo() {
		GridData gridData = new GridData();
		gridData.widthHint = 150;

		this.characterSetCombo = new Combo(this, SWT.NONE);
		this.characterSetCombo.setVisibleItemCount(20);
		this.characterSetCombo.setLayoutData(gridData);

		this.initCharacterSetCombo();
	}

	private static void initEngineCombo(Combo combo) {
		combo.add("");
		combo.add("MyISAM");
		combo.add("InnoDB");
		combo.add("Memory");
		combo.add("Merge");
		combo.add("Archive");
		combo.add("Federated");
		combo.add("NDB");
		combo.add("CSV");
		combo.add("Blackhole");
		combo.add("CSV");
	}

	private void initCharacterSetCombo() {
		this.characterSetCombo.add("");

		for (String characterSet : MySQLDBManager.getCharacterSetList()) {
			this.characterSetCombo.add(characterSet);
		}
	}

	@Override
	protected void setData() {
		super.setData();

		String engine = ((MySQLTableProperties) this.tableProperties)
				.getStorageEngine();

		this.engineCombo.setText(Format.toString(engine));

		String characterSet = ((MySQLTableProperties) this.tableProperties)
				.getCharacterSet();

		this.characterSetCombo.setText(Format.toString(characterSet));

		this.collationCombo.add("");

		for (String collation : MySQLDBManager.getCollationList(Format
				.toString(characterSet))) {
			this.collationCombo.add(collation);
		}

		String collation = ((MySQLTableProperties) this.tableProperties)
				.getCollation();

		this.collationCombo.setText(Format.toString(collation));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean validate() {
		super.validate();

		String engine = this.engineCombo.getText();
		((MySQLTableProperties) this.tableProperties).setStorageEngine(engine);

		String characterSet = this.characterSetCombo.getText();
		((MySQLTableProperties) this.tableProperties)
				.setCharacterSet(characterSet);

		String collation = this.collationCombo.getText();
		((MySQLTableProperties) this.tableProperties).setCollation(collation);

		return true;
	}

}
