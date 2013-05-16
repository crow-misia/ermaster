package org.insightech.er.editor.model.dbexport.ddl.validator.rule.view.impl;

import org.eclipse.core.resources.IMarker;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.dbexport.ddl.validator.ValidateResult;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.view.ViewRule;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.util.Check;

public class NoViewNameRule extends ViewRule {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean validate(View view) {
		if (Check.isBlank(view.getPhysicalName())) {
			ValidateResult validateResult = new ValidateResult();
			validateResult.setMessage(ResourceString
					.getResourceString("error.validate.no.view.name"));
			validateResult.setLocation(view.getLogicalName());
			validateResult.setSeverity(IMarker.SEVERITY_WARNING);
			validateResult.setObject(view);

			this.addError(validateResult);
		}

		return true;
	}
}
