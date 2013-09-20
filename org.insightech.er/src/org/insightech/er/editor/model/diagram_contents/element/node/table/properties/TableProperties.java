package org.insightech.er.editor.model.diagram_contents.element.node.table.properties;

public abstract class TableProperties extends TableViewProperties {

	private static final long serialVersionUID = -4482559358342532447L;

	private String characterSet;

	private String collation;

	public String getCharacterSet() {
		return characterSet;
	}

	public void setCharacterSet(String characterSet) {
		this.characterSet = characterSet;
	}

	public String getCollation() {
		return collation;
	}

	public void setCollation(String collation) {
		this.collation = collation;
	}

}
