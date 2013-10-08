package org.insightech.er.editor.model.dbimport;

import org.insightech.er.editor.model.AbstractModel;

public class DBObject {

	public static final String TYPE_TABLE = "table";

	public static final String TYPE_SEQUENCE = "sequence";

	public static final String TYPE_VIEW = "view";

	public static final String TYPE_TRIGGER = "trigger";

	public static final String TYPE_TABLESPACE = "tablespace";

	public static final String TYPE_NOTE = "note";

	public static final String TYPE_GROUP = "group";

	public static final String[] ALL_TYPES = { TYPE_TABLE, TYPE_VIEW,
			TYPE_SEQUENCE, TYPE_TRIGGER };

	private String schema;
	private String name;
	private String type;

	private AbstractModel model;

	public DBObject(String schema, String name, String type) {
		this.schema = schema;
		this.name = name;
		this.type = type;
	}

	public void setModel(AbstractModel model) {
		this.model = model;
	}

	public AbstractModel getModel() {
		return model;
	}

	/**
	 * schema を取得します.
	 * 
	 * @return schema
	 */
	public String getSchema() {
		return schema;
	}

	/**
	 * schema を設定します.
	 * 
	 * @param schema
	 *            schema
	 */
	public void setSchema(String schema) {
		this.schema = schema;
	}

	/**
	 * name を取得します.
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * name を設定します.
	 * 
	 * @param name
	 *            name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * type を取得します.
	 * 
	 * @return type
	 */
	public String getType() {
		return type;
	}

	/**
	 * type を設定します.
	 * 
	 * @param type
	 *            type
	 */
	public void setType(String type) {
		this.type = type;
	}
}
