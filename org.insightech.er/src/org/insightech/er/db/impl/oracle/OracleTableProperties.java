package org.insightech.er.db.impl.oracle;

import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableProperties;

public class OracleTableProperties extends TableProperties {

	private static final long serialVersionUID = 2802345970023438938L;

	private String characterSet;

	public String getCharacterSet() {
		return characterSet;
	}

	public void setCharacterSet(final String characterSet) {
		this.characterSet = characterSet;
	}
}
