package org.insightech.er.editor.model.dbexport.html.page_generator.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.html.page_generator.AbstractHtmlReportPageGenerator;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;

public class SequenceHtmlReportPageGenerator extends
		AbstractHtmlReportPageGenerator<Sequence> {

	public SequenceHtmlReportPageGenerator(Map<Object, Integer> idMap) {
		super(idMap);
	}

	public String getType() {
		return "sequence";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Sequence> getObjectList(ERDiagram diagram) {
		return diagram.getDiagramContents().getSequenceSet()
				.getSequenceList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getContentArgs(ERDiagram diagram, Sequence sequence) {
		return new String[] { StringUtils.defaultString(sequence.getDescription()),
				this.getValue(sequence.getIncrement()),
				this.getValue(sequence.getMinValue()),
				this.getValue(sequence.getMaxValue()),
				this.getValue(sequence.getStart()),
				this.getValue(sequence.getCache()),
				String.valueOf(sequence.isCycle()).toUpperCase() };
	}

	private String getValue(Number value) {
		if (value == null) {
			return "";
		} else {
			return String.valueOf(value);
		}
	}

	public String getObjectName(Sequence sequence) {
		return sequence.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getObjectSummary(Sequence sequence) {
		return sequence.getDescription();
	}
}
