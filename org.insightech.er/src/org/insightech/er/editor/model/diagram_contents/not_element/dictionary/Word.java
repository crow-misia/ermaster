package org.insightech.er.editor.model.diagram_contents.not_element.dictionary;

import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.ObjectModel;

public class Word extends AbstractModel implements ObjectModel,
		Comparable<Word> {

	private static final long serialVersionUID = 4315217440968295922L;

	private String physicalName;

	private String logicalName;

	private SqlType type;

	private TypeData typeData;

	private String description;

	public Word(String physicalName, String logicalName, SqlType type,
			TypeData typeData, String description, String database) {
		this.physicalName = physicalName;
		this.logicalName = logicalName;
		this.setType(type, typeData, database);
		this.description = description;
	}

	public Word(Word word) {
		this.physicalName = word.physicalName;
		this.logicalName = word.logicalName;
		this.type = word.type;
		this.typeData = word.typeData.clone();
		this.description = word.description;
	}

	public String getLogicalName() {
		return logicalName;
	}

	public String getPhysicalName() {
		return physicalName;
	}

	public SqlType getType() {
		return type;
	}

	public void setLogicalName(String logicalName) {
		this.logicalName = logicalName;
	}

	public void setPhysicalName(String physicalName) {
		this.physicalName = physicalName;
	}

	public void setType(SqlType type, TypeData typeData, String database) {
		this.type = type;
		this.typeData = typeData.clone();

		if (type != null && type.isNeedLength(database)) {
			if (this.typeData.getLength() == null) {
				this.typeData.setLength(0);
			}
		} else {
			this.typeData.setLength(null);
		}

		if (type != null && type.isNeedDecimal(database)) {
			if (this.typeData.getDecimal() == null) {
				this.typeData.setDecimal(0);
			}
		} else {
			this.typeData.setDecimal(null);
		}

	}

	public TypeData getTypeData() {
		return typeData;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void copyTo(Word to) {
		to.physicalName = this.physicalName;
		to.logicalName = this.logicalName;
		to.description = this.description;
		to.type = this.type;
		to.typeData = this.typeData.clone();
	}

	public int compareTo(Word o) {
		if (o == null) {
			return -1;
		}

		if (this.physicalName == null) {
			if (o.physicalName != null) {
				return 1;
			}
		} else {
			if (o.physicalName == null) {
				return -1;
			}
			int value = this.physicalName.toUpperCase().compareTo(
					o.physicalName.toUpperCase());
			if (value != 0) {
				return value;
			}
		}

		if (this.logicalName == null) {
			if (o.logicalName != null) {
				return 1;
			}
		} else {
			if (o.logicalName == null) {
				return -1;
			}
			int value = this.logicalName.toUpperCase().compareTo(
					o.logicalName.toUpperCase());
			if (value != 0) {
				return value;
			}
		}

		if (this.type == null) {
			if (o.type != null) {
				return 1;
			}
		} else {
			if (o.type == null) {
				return -1;
			}
			int value = this.type.getId().compareTo(o.type.getId());
			if (value != 0) {
				return value;
			}
		}

		if (this.typeData == null) {
			if (o.typeData != null) {
				return 1;
			}
		} else {
			if (o.typeData == null) {
				return -1;
			}
			int value = this.typeData.compareTo(o.typeData);
			if (value != 0) {
				return value;
			}
		}

		if (this.description == null) {
			if (o.description != null) {
				return 1;
			}
		} else {
			if (o.description == null) {
				return -1;
			}
			int value = this.description.compareTo(o.description);
			if (value != 0) {
				return value;
			}
		}

		return 0;
	}

	public String getName() {
		return this.getLogicalName();
	}

	public String getObjectType() {
		return "word";
	}

}
