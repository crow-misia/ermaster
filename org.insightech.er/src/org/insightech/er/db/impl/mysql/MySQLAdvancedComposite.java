package org.insightech.er.db.impl.mysql;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.view.dialog.element.table.tab.AdvancedComposite;
import org.insightech.er.util.Format;

public class MySQLAdvancedComposite extends AdvancedComposite {

	private Combo engineCombo;

	private Text primaryKeyLengthOfText;

	public MySQLAdvancedComposite(Composite parent) {
		super(parent);
	}

	@Override
	protected void initComposite() {
		super.initComposite();

		this.engineCombo = createEngineCombo(this, this.dialog);

		this.primaryKeyLengthOfText = CompositeFactory.createNumText(
				this.dialog, this, "label.primary.key.length.of.text", 30);
	}

	public static Combo createEngineCombo(Composite parent,
			AbstractDialog dialog) {
		Combo combo = CompositeFactory.createCombo(dialog, parent,
				"label.storage.engine");
		combo.setVisibleItemCount(20);

		initEngineCombo(combo);

		return combo;
	}

	private static void initEngineCombo(Combo combo) {
		combo.add("");
		combo.add("InnoDB");
		combo.add("MyISAM");
		combo.add("ndbcluster");
		combo.add("MEMORY");
		combo.add("EXAMPLE");
		combo.add("FEDERATED");
		combo.add("ARCHIVE");
		combo.add("CSV");
		combo.add("BLACKHOLE");
		combo.add("MERGE");
	}

	@Override
	protected void setData() {
		super.setData();

		this.engineCombo.setText(Format
				.toString(((MySQLTableProperties) this.tableProperties)
						.getStorageEngine()));

		this.primaryKeyLengthOfText.setText(Format
				.toString(((MySQLTableProperties) this.tableProperties)
						.getPrimaryKeyLengthOfText()));
	}

	@Override
	public void validate() throws InputException {
		super.validate();

		String engine = this.engineCombo.getText();
		((MySQLTableProperties) this.tableProperties).setStorageEngine(engine);

		String str = this.primaryKeyLengthOfText.getText();
		Integer length = null;

		try {
			if (StringUtils.isNotBlank(str)) {
				length = Integer.valueOf(str);
			}
		} catch (Exception e) {
			throw new InputException("error.column.length.digit");
		}

		((MySQLTableProperties) this.tableProperties)
				.setPrimaryKeyLengthOfText(length);

		if (this.table != null) {
			for (NormalColumn primaryKey : this.table.getPrimaryKeys()) {
				SqlType type = primaryKey.getType();

				if (type != null && type.isFullTextIndexable()
						&& !type.isNeedLength(this.diagram.getDatabase())) {
					if (length == null || length == 0) {
						throw new InputException(
								"error.primary.key.length.empty");
					}
				}
			}
		}
	}
}
