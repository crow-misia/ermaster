package org.insightech.er.editor.model.dbexport.ddl.validator.rule.table.impl;

import org.eclipse.core.resources.IMarker;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.dbexport.ddl.validator.ValidateResult;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.table.TableRule;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.util.Check;

public class NoTableNameRule extends TableRule {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean validate(ERTable table) {
		if (Check.isBlank(table.getPhysicalName())) {
			ValidateResult validateResult = new ValidateResult();
			validateResult.setMessage(ResourceString
					.getResourceString("error.validate.no.table.name"));
			validateResult.setLocation(table.getLogicalName());
			validateResult.setSeverity(IMarker.SEVERITY_WARNING);
			validateResult.setObject(table);

			this.addError(validateResult);
		}

		return true;
	}
}
