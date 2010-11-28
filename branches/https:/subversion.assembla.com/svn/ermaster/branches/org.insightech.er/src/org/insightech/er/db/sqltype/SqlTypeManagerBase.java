package org.insightech.er.db.sqltype;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public abstract class SqlTypeManagerBase implements SqlTypeManager {

	private List<String> aliasList;

	protected SqlTypeManagerBase() {
		List<SqlType> allSqlTypeList = SqlType.getAllSqlType();

		Set<String> aliasSet = new TreeSet<String>();

		for (SqlType type : allSqlTypeList) {
			String alias = this.getAlias(type);
			if (alias != null) {
				aliasSet.add(alias);
			}
		}

		this.aliasList = new ArrayList<String>(aliasSet);
	}

	public List<String> getAliasList() {
		return aliasList;
	}

}
