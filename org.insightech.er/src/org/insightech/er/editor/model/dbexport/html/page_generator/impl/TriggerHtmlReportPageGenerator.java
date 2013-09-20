package org.insightech.er.editor.model.dbexport.html.page_generator.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.html.page_generator.AbstractHtmlReportPageGenerator;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.Trigger;

public class TriggerHtmlReportPageGenerator extends
		AbstractHtmlReportPageGenerator<Trigger> {

	public TriggerHtmlReportPageGenerator(Map<Object, Integer> idMap) {
		super(idMap);
	}

	public String getType() {
		return "trigger";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Trigger> getObjectList(ERDiagram diagram) {
		return diagram.getDiagramContents().getTriggerSet()
				.getTriggerList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getContentArgs(ERDiagram diagram, Trigger trigger) {
		String description = StringUtils.defaultString(trigger.getDescription());
		String sql = StringUtils.defaultString(trigger.getSql());

		return new String[] { description, sql };
	}

	public String getObjectName(Trigger trigger) {
		return trigger.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getObjectSummary(Trigger trigger) {
		return trigger.getDescription();
	}
}
