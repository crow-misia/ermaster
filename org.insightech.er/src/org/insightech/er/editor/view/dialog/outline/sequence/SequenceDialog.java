package org.insightech.er.editor.view.dialog.outline.sequence;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.Resources;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.util.Check;
import org.insightech.er.util.Format;

public class SequenceDialog extends AbstractDialog {

	private Text nameText;

	private Text schemaText;

	private Text incrementText;

	private Text minValueText;

	private Text maxValueText;

	private Text startText;

	private Text cacheText;

	private Button cycleCheckBox;

	private Text descriptionText;

	private Sequence sequence;

	private Sequence result;

	public SequenceDialog(Shell parentShell, Sequence sequence) {
		super(parentShell, 2);

		this.sequence = sequence;
	}

	@Override
	protected void initialize(Composite composite) {
		this.nameText = CompositeFactory.createText(this, composite,
				"label.sequence.name", false);
		this.schemaText = CompositeFactory.createText(this, composite,
				"label.schema", false);
		this.incrementText = CompositeFactory.createNumText(this, composite,
				"Increment");
		this.minValueText = CompositeFactory.createNumText(this, composite,
				"MinValue");
		this.maxValueText = CompositeFactory.createNumText(this, composite,
				"MaxValue");
		this.startText = CompositeFactory.createNumText(this, composite,
				"Start");
		this.cacheText = CompositeFactory.createNumText(this, composite,
				"Cache");
		this.cycleCheckBox = CompositeFactory.createCheckbox(this, composite,
				"Cycle", 2);
		this.descriptionText = CompositeFactory.createTextArea(this, composite,
				"label.description", Resources.DESCRIPTION_WIDTH, 100, 1, true);
	}

	@Override
	protected String getErrorMessage() {
		String text = nameText.getText().trim();
		if (text.equals("")) {
			return "error.sequence.name.empty";
		}

		if (!Check.isAlphabet(text)) {
			return "error.sequence.name.not.alphabet";
		}

		text = schemaText.getText();

		if (!Check.isAlphabet(text)) {
			return "error.schema.not.alphabet";
		}

		text = incrementText.getText();

		if (!text.equals("")) {
			try {
				Integer.parseInt(text);

			} catch (NumberFormatException e) {
				return "error.sequence.increment.degit";
			}
		}

		text = minValueText.getText();

		if (!text.equals("")) {
			try {
				Long.parseLong(text);

			} catch (NumberFormatException e) {
				return "error.sequence.minValue.degit";
			}
		}

		text = maxValueText.getText();

		if (!text.equals("")) {
			try {
				Long.parseLong(text);

			} catch (NumberFormatException e) {
				return "error.sequence.maxValue.degit";
			}
		}

		text = startText.getText();

		if (!text.equals("")) {
			try {
				Long.parseLong(text);

			} catch (NumberFormatException e) {
				return "error.sequence.start.degit";
			}
		}

		text = cacheText.getText();

		if (!text.equals("")) {
			try {
				Integer.parseInt(text);

			} catch (NumberFormatException e) {
				return "error.sequence.cache.degit";
			}
		}

		return null;
	}

	@Override
	protected String getTitle() {
		return "dialog.title.sequence";
	}

	@Override
	protected void perfomeOK() throws InputException {
		this.result = new Sequence();

		this.result.setName(this.nameText.getText().trim());
		this.result.setSchema(this.schemaText.getText().trim());

		Integer increment = null;
		Long minValue = null;
		Long maxValue = null;
		Long start = null;
		Integer cache = null;

		String text = incrementText.getText();
		if (!text.equals("")) {
			increment = Integer.valueOf(text);
		}

		text = minValueText.getText();
		if (!text.equals("")) {
			minValue = Long.valueOf(text);
		}

		text = maxValueText.getText();
		if (!text.equals("")) {
			maxValue = Long.valueOf(text);
		}

		text = startText.getText();
		if (!text.equals("")) {
			start = Long.valueOf(text);
		}

		text = cacheText.getText();
		if (!text.equals("")) {
			cache = Integer.valueOf(text);
		}

		this.result.setIncrement(increment);
		this.result.setMinValue(minValue);
		this.result.setMaxValue(maxValue);
		this.result.setStart(start);
		this.result.setCache(cache);
		this.result.setCycle(this.cycleCheckBox.getSelection());
		this.result.setDescription(this.descriptionText.getText().trim());
	}

	@Override
	protected void setData() {
		if (this.sequence != null) {
			this.nameText.setText(Format.toString(this.sequence.getName()));
			this.schemaText.setText(Format.toString(this.sequence.getSchema()));
			this.incrementText.setText(Format.toString(this.sequence
					.getIncrement()));
			this.minValueText.setText(Format.toString(this.sequence
					.getMinValue()));
			this.maxValueText.setText(Format.toString(this.sequence
					.getMaxValue()));
			this.startText.setText(Format.toString(this.sequence.getStart()));
			this.cacheText.setText(Format.toString(this.sequence.getCache()));
			this.cycleCheckBox.setSelection(this.sequence.isCycle());
			this.descriptionText.setText(Format.toString(this.sequence
					.getDescription()));
		}
	}

	public Sequence getResult() {
		return result;
	}

}
