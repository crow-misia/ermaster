package org.insightech.er.db.impl.mysql;

import org.eclipse.swt.SWT;
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
	}

	/**
	 * This method initializes combo
	 * 
	 */
	public static Combo createEngineCombo(Composite parent,
			AbstractDialog dialog) {
		Combo combo = CompositeFactory.createReadOnlyCombo(dialog, parent,
				"label.storage.engine");

		initEngineCombo(combo);

		return combo;
	}

	/**
	 * This method initializes combo1
	 * 
	 */
	private void createCharacterSetCombo() {
		GridData gridData = new GridData();
		gridData.widthHint = 100;

		this.characterSetCombo = new Combo(this, SWT.NONE);
		this.characterSetCombo.setVisibleItemCount(20);
		this.characterSetCombo.setLayoutData(gridData);

		this.initCharacterSetCombo();
	}

	private static void initEngineCombo(Combo combo) {
		combo.add("MyISAM");
		combo.add("InnoDB");
		combo.add("NDBCluster");
	}

	private void initCharacterSetCombo() {
		this.characterSetCombo.add("ascii");
		this.characterSetCombo.add("big5");
		this.characterSetCombo.add("gbk");
		this.characterSetCombo.add("sjis");
		this.characterSetCombo.add("cp932");
		this.characterSetCombo.add("gb2312");
		this.characterSetCombo.add("ujis");
		this.characterSetCombo.add("euckr");
		this.characterSetCombo.add("latin1");
		this.characterSetCombo.add("latin2");
		this.characterSetCombo.add("greek");
		this.characterSetCombo.add("hebrew");
		this.characterSetCombo.add("cp866");
		this.characterSetCombo.add("tis620");
		this.characterSetCombo.add("cp866");
		this.characterSetCombo.add("cp1250");
		this.characterSetCombo.add("cp1251");
		this.characterSetCombo.add("cp1257");
		this.characterSetCombo.add("macroman");
		this.characterSetCombo.add("macce");
		this.characterSetCombo.add("utf8");
		this.characterSetCombo.add("ucs2");
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

		return true;
	}

}
