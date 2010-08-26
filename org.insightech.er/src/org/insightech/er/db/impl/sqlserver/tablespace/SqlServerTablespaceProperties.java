package org.insightech.er.db.impl.sqlserver.tablespace;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;

public class SqlServerTablespaceProperties implements TablespaceProperties {

	private static final long serialVersionUID = 3581869274788998047L;

	// (REGULAR/LARGI/SYSTEM TEMPORARY/USER TEMPORARY)
	private String type;

	private String pageSize;

	private String managedBy;

	private String container;

	// private String containerDirectoryPath;
	//
	// private String containerFilePath;
	//
	// private String containerPageNum;
	//
	// private String containerDevicePath;

	private String extentSize;

	private String prefetchSize;

	private String bufferPoolName;

	/**
	 * type ‚ğæ“¾‚µ‚Ü‚·.
	 * 
	 * @return type
	 */
	public String getType() {
		return type;
	}

	/**
	 * type ‚ğİ’è‚µ‚Ü‚·.
	 * 
	 * @param type
	 *            type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * pageSize ‚ğæ“¾‚µ‚Ü‚·.
	 * 
	 * @return pageSize
	 */
	public String getPageSize() {
		return pageSize;
	}

	/**
	 * pageSize ‚ğİ’è‚µ‚Ü‚·.
	 * 
	 * @param pageSize
	 *            pageSize
	 */
	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * managedBy ‚ğæ“¾‚µ‚Ü‚·.
	 * 
	 * @return managedBy
	 */
	public String getManagedBy() {
		return managedBy;
	}

	/**
	 * managedBy ‚ğİ’è‚µ‚Ü‚·.
	 * 
	 * @param managedBy
	 *            managedBy
	 */
	public void setManagedBy(String managedBy) {
		this.managedBy = managedBy;
	}

	/**
	 * extentSize ‚ğæ“¾‚µ‚Ü‚·.
	 * 
	 * @return extentSize
	 */
	public String getExtentSize() {
		return extentSize;
	}

	/**
	 * extentSize ‚ğİ’è‚µ‚Ü‚·.
	 * 
	 * @param extentSize
	 *            extentSize
	 */
	public void setExtentSize(String extentSize) {
		this.extentSize = extentSize;
	}

	/**
	 * prefetchSize ‚ğæ“¾‚µ‚Ü‚·.
	 * 
	 * @return prefetchSize
	 */
	public String getPrefetchSize() {
		return prefetchSize;
	}

	/**
	 * prefetchSize ‚ğİ’è‚µ‚Ü‚·.
	 * 
	 * @param prefetchSize
	 *            prefetchSize
	 */
	public void setPrefetchSize(String prefetchSize) {
		this.prefetchSize = prefetchSize;
	}

	/**
	 * bufferPoolName ‚ğæ“¾‚µ‚Ü‚·.
	 * 
	 * @return bufferPoolName
	 */
	public String getBufferPoolName() {
		return bufferPoolName;
	}

	/**
	 * bufferPoolName ‚ğİ’è‚µ‚Ü‚·.
	 * 
	 * @param bufferPoolName
	 *            bufferPoolName
	 */
	public void setBufferPoolName(String bufferPoolName) {
		this.bufferPoolName = bufferPoolName;
	}

	/**
	 * container ‚ğæ“¾‚µ‚Ü‚·.
	 * 
	 * @return container
	 */
	public String getContainer() {
		return container;
	}

	/**
	 * container ‚ğİ’è‚µ‚Ü‚·.
	 * 
	 * @param container
	 *            container
	 */
	public void setContainer(String container) {
		this.container = container;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TablespaceProperties clone() {
		SqlServerTablespaceProperties properties = new SqlServerTablespaceProperties();

		properties.bufferPoolName = this.bufferPoolName;
		properties.container = this.container;
		// properties.containerDevicePath = this.containerDevicePath;
		// properties.containerDirectoryPath = this.containerDirectoryPath;
		// properties.containerFilePath = this.containerFilePath;
		// properties.containerPageNum = this.containerPageNum;
		properties.extentSize = this.extentSize;
		properties.managedBy = this.managedBy;
		properties.pageSize = this.pageSize;
		properties.prefetchSize = this.prefetchSize;
		properties.type = this.type;

		return properties;
	}

	public LinkedHashMap<String, String> getPropertiesMap() {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

		map.put("label.tablespace.type", this.getType());
		map.put("label.tablespace.page.size", this.getPageSize());
		map.put("label.tablespace.managed.by", this.getManagedBy());
		map.put("label.tablespace.container", this.getContainer());
		map.put("label.tablespace.extent.size", this.getExtentSize());
		map.put("label.tablespace.prefetch.size", this.getPrefetchSize());
		map.put("label.tablespace.buffer.pool.name", this.getBufferPoolName());

		return map;
	}

	public List<String> validate() {
		List<String> errorMessage = new ArrayList<String>();
		return errorMessage;
	}
}
