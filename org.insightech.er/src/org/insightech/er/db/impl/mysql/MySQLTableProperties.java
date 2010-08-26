package org.insightech.er.db.impl.mysql;

import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableProperties;

public class MySQLTableProperties extends TableProperties {

	private static final long serialVersionUID = 3126556935094407067L;

	private String storageEngine;

	private String characterSet;

	public String getStorageEngine() {
		return storageEngine;
	}

	public void setStorageEngine(String storageEngine) {
		this.storageEngine = storageEngine;
	}

	public String getCharacterSet() {
		return characterSet;
	}

	public void setCharacterSet(String characterSet) {
		this.characterSet = characterSet;
	}

}
