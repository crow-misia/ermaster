package org.insightech.er.db.sqltype;

import java.util.List;
import java.util.Map;

import org.insightech.er.db.sqltype.SqlType.TypeKey;

public interface SqlTypeManager {

	public String getAlias(SqlType type);

	public int getByteLength(SqlType type, Integer length, Integer decimal);

	public List<String> getAliasList();

	public SqlType[] getNeedLengthType();

	public SqlType[] getNeedDecimalType();

	public SqlType[] getUnsupportedType();

	public Map<TypeKey, SqlType> getSqlTypeMap();
}
