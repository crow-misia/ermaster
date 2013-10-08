package org.insightech.er.editor.model.dbexport.ddl.validator;

public class ValidateResult {

	private String message;

	private String location;

	private int severity;
	
	private Object object;

	/**
	 * object を取得します.
	 *
	 * @return object
	 */
	public Object getObject() {
		return object;
	}

	/**
	 * object を設定します.
	 *
	 * @param object object
	 */
	public void setObject(Object object) {
		this.object = object;
	}

	/**
	 * message を取得します.
	 *
	 * @return message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * message を設定します.
	 *
	 * @param message message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * location を取得します.
	 *
	 * @return location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * location を設定します.
	 *
	 * @param location location
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * severity を取得します.
	 *
	 * @return severity
	 */
	public int getSeverity() {
		return severity;
	}

	/**
	 * severity を設定します.
	 *
	 * @param severity severity
	 */
	public void setSeverity(int severity) {
		this.severity = severity;
	}

}
