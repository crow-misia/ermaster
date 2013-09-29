package org.insightech.er.editor.model.dbexport.html.page_generator.impl;

import java.util.List;
import java.util.Map;

import org.insightech.er.db.DBManager;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.html.page_generator.AbstractHtmlReportPageGenerator;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.util.Format;

public class SequenceHtmlReportPageGenerator extends
		AbstractHtmlReportPageGenerator {

	public SequenceHtmlReportPageGenerator(Map<Object, Integer> idMap) {
		super(idMap);
	}

	public String getType() {
		return "sequence";
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Object> getObjectList(ERDiagram diagram) {
		List list = diagram.getDiagramContents().getSequenceSet()
				.getSequenceList();

		return list;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getContentArgs(ERDiagram diagram, Object object) {
		Sequence sequence = (Sequence) object;

		String cache = Format.toString(sequence.getCache());
		
		if (DBManagerFactory.getDBManager(diagram).isSupported(DBManager.SUPPORT_SEQUENCE_NOCACHE)) {
			if (sequence.isNocache()) {
				cache = "NO CACHE";
			}
		}
		
		return new String[] { Format.null2blank(sequence.getDescription()),
				this.getValue(sequence.getIncrement()),
				this.getValue(sequence.getMinValue()),
				this.getValue(sequence.getMaxValue()),
				this.getValue(sequence.getStart()),
				cache,
				String.valueOf(sequence.isCycle()).toUpperCase() };
	}

	private String getValue(Number value) {
		if (value == null) {
			return "";
		} else {
			return String.valueOf(value);
		}
	}

	public String getObjectName(Object object) {
		Sequence sequence = (Sequence) object;

		return sequence.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getObjectSummary(Object object) {
		Sequence sequence = (Sequence) object;

		return sequence.getDescription();
	}
}
