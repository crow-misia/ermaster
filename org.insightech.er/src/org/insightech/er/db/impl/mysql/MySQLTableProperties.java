package org.insightech.er.db.impl.mysql;

import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableProperties;

public class MySQLTableProperties extends TableProperties {

	private static final long serialVersionUID = 3126556935094407067L;

	private String storageEngine;

	private Integer primaryKeyLengthOfText;

	public String getStorageEngine() {
		return storageEngine;
	}

	public void setStorageEngine(String storageEngine) {
		this.storageEngine = storageEngine;
	}

	public Integer getPrimaryKeyLengthOfText() {
		return primaryKeyLengthOfText;
	}

	public void setPrimaryKeyLengthOfText(Integer primaryKeyLengthOfText) {
		this.primaryKeyLengthOfText = primaryKeyLengthOfText;
	}

}
