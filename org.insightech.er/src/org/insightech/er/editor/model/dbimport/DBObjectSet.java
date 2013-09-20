package org.insightech.er.editor.model.dbimport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

public class DBObjectSet {

	private final Map<String, List<DBObject>> schemaDbObjectListMap;

	private final List<DBObject> tablespaceList;

	private final List<DBObject> noteList;

	private final List<DBObject> groupList;

	public DBObjectSet() {
		this.schemaDbObjectListMap = new TreeMap<String, List<DBObject>>();
		this.tablespaceList = new ArrayList<DBObject>();
		this.noteList = new ArrayList<DBObject>();
		this.groupList = new ArrayList<DBObject>();
	}

	public Map<String, List<DBObject>> getSchemaDbObjectListMap() {
		return schemaDbObjectListMap;
	}

	public List<DBObject> getTablespaceList() {
		return tablespaceList;
	}

	public List<DBObject> getNoteList() {
		return noteList;
	}

	public List<DBObject> getGroupList() {
		return groupList;
	}

	public void addAll(List<DBObject> dbObjectList) {
		for (DBObject dbObject : dbObjectList) {
			this.add(dbObject);
		}
	}

	public void add(DBObject dbObject) {
		if (DBObject.TYPE_TABLESPACE.equals(dbObject.getType())) {
			this.tablespaceList.add(dbObject);

		} else if (DBObject.TYPE_NOTE.equals(dbObject.getType())) {
			this.noteList.add(dbObject);

		} else if (DBObject.TYPE_GROUP.equals(dbObject.getType())) {
			this.groupList.add(dbObject);

		} else {
			String schema = StringUtils.defaultString(dbObject.getSchema());
			List<DBObject> dbObjectList = this.schemaDbObjectListMap
					.get(schema);
			if (dbObjectList == null) {
				dbObjectList = new ArrayList<DBObject>();
				this.schemaDbObjectListMap.put(schema, dbObjectList);
			}

			dbObjectList.add(dbObject);
		}
	}

}
