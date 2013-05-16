package org.insightech.er.editor.model.diagram_contents.element.node.model_properties;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.diagram_contents.element.node.Location;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.util.NameValue;

public class ModelProperties extends NodeElement implements Cloneable {

	private static final long serialVersionUID = 5311013351131568260L;

	public static final String PROPERTY_CHANGE_MODEL_PROPERTIES = "model_properties";

	public static final String KEY_PROJECT_NAME = ResourceString.getResourceString("label.project.name");

	public static final String KEY_MODEL_NAME = ResourceString.getResourceString("label.model.name");

	public static final String KEY_VERSION = ResourceString.getResourceString("label.version");

	public static final String KEY_COMPANY_NAME = ResourceString.getResourceString("label.company.name");

	public static final String KEY_DEPARTMENT_NAME = ResourceString.getResourceString("label.department.name");

	public static final String KEY_AUTHOR = ResourceString.getResourceString("label.author");

	public static final String KEY_UPDATER = ResourceString.getResourceString("label.updater");

	private boolean display;

	private List<NameValue> properties;

	private Date creationDate;

	private Date updatedDate;

	public ModelProperties() {
		this.creationDate = new Date();
		this.updatedDate = new Date();

		this.setLocation(new Location(50, 50, -1, -1));

		this.properties = new ArrayList<NameValue>();
	}

	public void init() {
		properties.add(new NameValue(KEY_PROJECT_NAME, ""));
		properties.add(new NameValue(KEY_MODEL_NAME, ""));
		properties.add(new NameValue(KEY_VERSION, ""));
		properties.add(new NameValue(KEY_COMPANY_NAME, ""));
		properties.add(new NameValue(KEY_DEPARTMENT_NAME, ""));
		properties.add(new NameValue(KEY_AUTHOR, ""));
		properties.add(new NameValue(KEY_UPDATER, ""));
	}

	public void clear() {
		this.properties.clear();
	}

	public List<NameValue> getProperties() {
		return properties;
	}

	public void addProperty(NameValue property) {
		this.properties.add(property);
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public Map<String, String> getMap() {
		final Map<String, String> retval = new HashMap<String, String>();

		for (final NameValue n : this.properties) {
			retval.put(n.getName(), n.getValue());
		}

		return retval;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;

		this.firePropertyChange(PROPERTY_CHANGE_MODEL_PROPERTIES, null, null);
	}

	public boolean isDisplay() {
		return display;
	}

	public void setDisplay(boolean display) {
		this.display = display;

		this.firePropertyChange(PROPERTY_CHANGE_MODEL_PROPERTIES, null, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLocation(Location location) {
		location.width = -1;
		location.height = -1;

		super.setLocation(location);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelProperties clone() {
		ModelProperties clone = (ModelProperties) super.clone();

		List<NameValue> list = new ArrayList<NameValue>();

		for (NameValue nameValue : this.properties) {
			list.add(nameValue.clone());
		}

		clone.properties = list;

		return clone;
	}

	public void setProperties(List<NameValue> properties) {
		this.properties = properties;

		this.firePropertyChange(PROPERTY_CHANGE_MODEL_PROPERTIES, null, null);
	}

	public String getDescription() {
		return null;
	}

	public String getName() {
		return null;
	}

	public String getObjectType() {
		return "model_properties";
	}
}
