package org.insightech.er.editor.model.testdata;

import org.insightech.er.ResourceString;

public class RepeatTestDataDef implements Cloneable {

	public static final String TYPE_FORMAT = ResourceString
			.getResourceString("label.testdata.repeat.type.format");

	public static final String TYPE_FOREIGNKEY = ResourceString
			.getResourceString("label.testdata.repeat.type.foreign.key");

	public static final String TYPE_ENUM = ResourceString
			.getResourceString("label.testdata.repeat.type.enum");

	public static final String TYPE_NULL = ResourceString
			.getResourceString("label.testdata.repeat.type.null");

	private String type;

	private int repeatNum;

	private String template;

	private int from;

	private int to;

	private int increment;

	private String[] selects;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getRepeatNum() {
		return repeatNum;
	}

	public void setRepeatNum(int repeatNum) {
		this.repeatNum = repeatNum;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public int getTo() {
		return to;
	}

	public void setTo(int to) {
		this.to = to;
	}

	public int getIncrement() {
		return increment;
	}

	public void setIncrement(int increment) {
		this.increment = increment;
	}

	public String[] getSelects() {
		return selects;
	}

	public void setSelects(String[] selects) {
		this.selects = selects;
	}

	@Override
	public RepeatTestDataDef clone() {
		try {
			RepeatTestDataDef clone = (RepeatTestDataDef) super.clone();

			if (this.selects != null) {
				clone.selects = new String[this.selects.length];
				for (int i = 0; i < clone.selects.length; i++) {
					clone.selects[i] = this.selects[i];
				}
			}

			return clone;

		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
