package org.insightech.er.db.impl.oracle;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.view.dialog.element.table.tab.AdvancedComposite;
import org.insightech.er.util.Format;

public class OracleAdvancedComposite extends AdvancedComposite {

	private Combo characterSetCombo;

	public OracleAdvancedComposite(Composite parent) {
		super(parent);
	}

	@Override
	protected void initComposite() {
		super.initComposite();

		this.characterSetCombo = CompositeFactory.createCombo(dialog, this, "label.character.set", 1);
		this.characterSetCombo.setVisibleItemCount(20);
	}

	private void initCharacterSetCombo() {
		this.characterSetCombo.add("");

		for (final String characterSet : OracleDBManager.getCharacterSetList()) {
			this.characterSetCombo.add(characterSet);
		}
	}

	@Override
	protected void setData() {
		super.setData();

		this.initCharacterSetCombo();

		final String characterSet = ((OracleTableProperties) this.tableProperties)
				.getCharacterSet();

		this.characterSetCombo.setText(Format.toString(characterSet));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validate() throws InputException {
		super.validate();

		final String characterSet = this.characterSetCombo.getText();
		((OracleTableProperties) this.tableProperties)
				.setCharacterSet(characterSet);
	}
}
