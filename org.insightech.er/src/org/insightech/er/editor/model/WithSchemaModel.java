package org.insightech.er.editor.model;

import org.apache.commons.lang3.StringUtils;
import org.insightech.er.db.DBManager;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.db.SupportFunction;
import org.insightech.er.util.Format;

public abstract class WithSchemaModel extends AbstractModel implements
		Comparable<WithSchemaModel> {

	private static final long serialVersionUID = -7450893485538582071L;

	private String schema;

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getNameWithSchema(String database) {
		final DBManager dbManager = DBManagerFactory.getDBManager(database);

		if (dbManager.isSupported(SupportFunction.SCHEMA) &&
			StringUtils.isNotBlank(schema)) {
			return this.schema + "." + Format.null2blank(this.name);
		}
		return Format.null2blank(this.name);
	}

	public int compareTo(WithSchemaModel other) {
		int compareTo = 0;

		compareTo = Format.null2blank(this.schema).toUpperCase().compareTo(
				Format.null2blank(other.schema).toUpperCase());

		if (compareTo != 0) {
			return compareTo;
		}

		compareTo = Format.null2blank(this.name).toUpperCase().compareTo(
				Format.null2blank(other.name).toUpperCase());

		if (compareTo != 0) {
			return compareTo;
		}

		return compareTo;
	}
}
