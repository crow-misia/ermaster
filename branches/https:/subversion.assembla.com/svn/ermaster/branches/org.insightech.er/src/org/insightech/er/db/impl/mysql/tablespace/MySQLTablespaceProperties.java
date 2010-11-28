package org.insightech.er.db.impl.mysql.tablespace;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;
import org.insightech.er.util.Check;

public class MySQLTablespaceProperties implements TablespaceProperties {

	private static final long serialVersionUID = 7900101196638704362L;

	private String dataFile;

	private String logFileGroup;

	private String extentSize;

	private String initialSize;

	private String engine;

	/**
	 * dataFile ���擾���܂�.
	 * 
	 * @return dataFile
	 */
	public String getDataFile() {
		return dataFile;
	}

	/**
	 * dataFile ��ݒ肵�܂�.
	 * 
	 * @param dataFile
	 *            dataFile
	 */
	public void setDataFile(String dataFile) {
		this.dataFile = dataFile;
	}

	/**
	 * logFileGroup ���擾���܂�.
	 * 
	 * @return logFileGroup
	 */
	public String getLogFileGroup() {
		return logFileGroup;
	}

	/**
	 * logFileGroup ��ݒ肵�܂�.
	 * 
	 * @param logFileGroup
	 *            logFileGroup
	 */
	public void setLogFileGroup(String logFileGroup) {
		this.logFileGroup = logFileGroup;
	}

	/**
	 * extentSize ���擾���܂�.
	 * 
	 * @return extentSize
	 */
	public String getExtentSize() {
		return extentSize;
	}

	/**
	 * extentSize ��ݒ肵�܂�.
	 * 
	 * @param extentSize
	 *            extentSize
	 */
	public void setExtentSize(String extentSize) {
		this.extentSize = extentSize;
	}

	/**
	 * initialSize ���擾���܂�.
	 * 
	 * @return initialSize
	 */
	public String getInitialSize() {
		return initialSize;
	}

	/**
	 * initialSize ��ݒ肵�܂�.
	 * 
	 * @param initialSize
	 *            initialSize
	 */
	public void setInitialSize(String initialSize) {
		this.initialSize = initialSize;
	}

	/**
	 * engine ���擾���܂�.
	 * 
	 * @return engine
	 */
	public String getEngine() {
		return engine;
	}

	/**
	 * engine ��ݒ肵�܂�.
	 * 
	 * @param engine
	 *            engine
	 */
	public void setEngine(String engine) {
		this.engine = engine;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TablespaceProperties clone() {
		MySQLTablespaceProperties properties = new MySQLTablespaceProperties();

		properties.dataFile = this.dataFile;
		properties.engine = this.engine;
		properties.extentSize = this.extentSize;
		properties.initialSize = this.initialSize;
		properties.logFileGroup = this.logFileGroup;

		return properties;
	}

	public LinkedHashMap<String, String> getPropertiesMap() {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

		map.put("label.tablespace.data.file", this.getDataFile());
		map.put("label.tablespace.log.file.group", this.getLogFileGroup());
		map.put("label.tablespace.extent.size", this.getExtentSize());
		map.put("label.tablespace.initial.size", this.getInitialSize());
		map.put("label.storage.engine", this.getEngine());

		return map;
	}

	public List<String> validate() {
		List<String> errorMessage = new ArrayList<String>();

		if (Check.isEmptyTrim(this.getDataFile())) {
			errorMessage.add("error.tablespace.data.file.empty");
		}
		if (Check.isEmptyTrim(this.getLogFileGroup())) {
			errorMessage.add("error.tablespace.log.file.group.empty");
		}
		if (Check.isEmptyTrim(this.getEngine())) {
			errorMessage.add("error.tablespace.storage.engine.empty");
		}

		return errorMessage;
	}

}
