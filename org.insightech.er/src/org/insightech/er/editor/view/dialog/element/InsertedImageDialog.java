package org.insightech.er.editor.view.dialog.element;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.model.diagram_contents.element.node.image.InsertedImage;

public class InsertedImageDialog extends AbstractDialog {

	private Scale brightnessScale;

	private Button fixRateCheckbox;

	private InsertedImage insertedImage;

	public InsertedImageDialog(Shell parentShell, InsertedImage insertedImage) {
		super(parentShell, 4);

		this.insertedImage = insertedImage;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize(Composite composite) {
		this.brightnessScale = CompositeFactory.createScale(this, composite,
				"label.brightness");
		this.brightnessScale.setMinimum(0);
		this.brightnessScale.setMaximum(100);
		this.brightnessScale.setPageIncrement(10);

		this.fixRateCheckbox = CompositeFactory.createCheckbox(this, composite,
				"label.fix.image.rate", 3);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getErrorMessage() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void perfomeOK() {

	}

	@Override
	protected String getTitle() {
		return "dialog.title.image.information";
	}

	@Override
	protected void setData() {

	}
}
