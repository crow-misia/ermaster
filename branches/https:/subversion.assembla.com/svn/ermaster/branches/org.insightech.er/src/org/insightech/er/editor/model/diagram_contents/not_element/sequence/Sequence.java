package org.insightech.er.editor.model.diagram_contents.not_element.sequence;

import org.insightech.er.editor.model.ObjectModel;
import org.insightech.er.editor.model.WithSchemaModel;

public class Sequence extends WithSchemaModel implements ObjectModel {

	private static final long serialVersionUID = -4492787972500741281L;

	private String description;

	private Integer increment;

	private Long minValue;

	private Long maxValue;

	private Long start;

	private Integer cache;

	private boolean cycle;

	public Integer getCache() {
		return cache;
	}

	public void setCache(Integer cache) {
		this.cache = cache;
	}

	public boolean isCycle() {
		return cycle;
	}

	public void setCycle(boolean cycle) {
		this.cycle = cycle;
	}

	public Integer getIncrement() {
		return increment;
	}

	public void setIncrement(Integer increment) {
		this.increment = increment;
	}

	public Long getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Long maxValue) {
		this.maxValue = maxValue;
	}

	public Long getMinValue() {
		return minValue;
	}

	public void setMinValue(Long minValue) {
		this.minValue = minValue;
	}

	public Long getStart() {
		return start;
	}

	public void setStart(Long start) {
		this.start = start;
	}

	/**
	 * description ÇéÊìæÇµÇ‹Ç∑.
	 * 
	 * @return description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * description Çê›íËÇµÇ‹Ç∑.
	 * 
	 * @param description
	 *            description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public String getObjectType() {
		return "sequence";
	}
}
