package org.insightech.er.db.impl.mysql;

import java.util.HashMap;
import java.util.Map;

import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.db.sqltype.SqlTypeManagerBase;
import org.insightech.er.db.sqltype.SqlType.TypeKey;

public class MySQLSqlTypeManager extends SqlTypeManagerBase {

	public String getAlias(SqlType type) {
		if (type.equals(SqlType.BIGINT)) {
			return "bigint";
		}
		if (type.equals(SqlType.BIG_SERIAL)) {
			return "bigint";
		}
		if (type.equals(SqlType.BINARY_DOUBLE)) {
			return "double";
		}
		if (type.equals(SqlType.BINARY_FLOAT)) {
			return "float";
		}
		if (type.equals(SqlType.BINARY_N)) {
			return "binary(n)";
		}
		if (type.equals(SqlType.BIT_N)) {
			return "bit";
		}
		if (type.equals(SqlType.BIT_VARYING_N)) {
			return "bit";
		}
		if (type.equals(SqlType.BLOB)) {
			return "blob";
		}
		if (type.equals(SqlType.BOOLEAN)) {
			return "bool";
		}
		if (type.equals(SqlType.CHARACTER_N)) {
			return "char(n)";
		}
		if (type.equals(SqlType.CLOB)) {
			return "text";
		}
		if (type.equals(SqlType.DATE)) {
			return "date";
		}
		if (type.equals(SqlType.DATE_TIME)) {
			return "datetime";
		}
		if (type.equals(SqlType.DECIMAL_P_S)) {
			return "decimal(p,s)";
		}
		if (type.equals(SqlType.DOUBLE_PRECISION)) {
			return "double precision";
		}
		if (type.equals(SqlType.ENUM)) {
			return "enum";
		}
		if (type.equals(SqlType.FLOAT)) {
			return "float";
		}
		if (type.equals(SqlType.FLOAT_P)) {
			return "float(m)";
		}
		if (type.equals(SqlType.FLOAT_M_D)) {
			return "float(m,d)";
		}
		if (type.equals(SqlType.INTEGER)) {
			return "int";
		}
		if (type.equals(SqlType.LONG)) {
			return "text";
		}
		if (type.equals(SqlType.LONG_BLOB)) {
			return "longblob";
		}
		if (type.equals(SqlType.LONG_RAW)) {
			return "blob";
		}
		if (type.equals(SqlType.LONG_TEXT)) {
			return "longtext";
		}
		if (type.equals(SqlType.MEDIUM_BLOB)) {
			return "mediumblob";
		}
		if (type.equals(SqlType.MEDIUM_INT)) {
			return "mediumint";
		}
		if (type.equals(SqlType.MEDIUM_TEXT)) {
			return "mediumtext";
		}
		if (type.equals(SqlType.NCHAR_N)) {
			return "char(n)";
		}
		if (type.equals(SqlType.NCLOB)) {
			return "text";
		}
		if (type.equals(SqlType.NUMERIC)) {
			return "numeric";
		}
		if (type.equals(SqlType.NUMERIC_P_S)) {
			return "numeric(p,s)";
		}
		if (type.equals(SqlType.NVARCHAR_N)) {
			return "varchar(n)";
		}
		if (type.equals(SqlType.POINT)) {
			return "point";
		}
		if (type.equals(SqlType.POLYGON)) {
			return "polygon";
		}
		if (type.equals(SqlType.REAL)) {
			return "real";
		}
		if (type.equals(SqlType.REAL_M_D)) {
			return "real(m,d)";
		}
		if (type.equals(SqlType.SERIAL)) {
			return "integer";
		}
		if (type.equals(SqlType.SET)) {
			return "set";
		}
		if (type.equals(SqlType.SMALLINT)) {
			return "smallint";
		}
		if (type.equals(SqlType.TEXT)) {
			return "text";
		}
		if (type.equals(SqlType.TIME)) {
			return "time";
		}
		if (type.equals(SqlType.TIME_P)) {
			return "time";
		}
		if (type.equals(SqlType.TIME_WITH_TIME_ZONE)) {
			return "time";
		}
		if (type.equals(SqlType.TIME_WITH_TIME_ZONE_P)) {
			return "time";
		}
		if (type.equals(SqlType.TIMESTAMP)) {
			return "timestamp";
		}
		if (type.equals(SqlType.TIMESTAMP_P)) {
			return "timestamp(p)";
		}
		if (type.equals(SqlType.TIMESTAMP_WITH_TIME_ZONE)) {
			return "timestamp";
		}
		if (type.equals(SqlType.TIMESTAMP_WITH_TIME_ZONE_P)) {
			return "timestamp(p)";
		}
		if (type.equals(SqlType.TINY_BLOB)) {
			return "tinyblob";
		}
		if (type.equals(SqlType.TINY_INT)) {
			return "tinyint";
		}
		if (type.equals(SqlType.TINY_TEXT)) {
			return "tinytext";
		}
		if (type.equals(SqlType.VARBINARY_N)) {
			return "varbinary(n)";
		}
		if (type.equals(SqlType.VARCHAR_N)) {
			return "varchar(n)";
		}
		if (type.equals(SqlType.YEAR_2)) {
			return "year(2)";
		}
		if (type.equals(SqlType.YEAR_4)) {
			return "year(4)";
		}

		return null;
	}

	public int getByteLength(SqlType type, Integer length, Integer decimal) {
		return 0;
	}

	public SqlType[] getNeedLengthType() {
		return new SqlType[] { SqlType.BINARY_N, SqlType.CHARACTER_N,
				SqlType.DECIMAL_P_S, SqlType.REAL_M_D, SqlType.FLOAT_P,
				SqlType.FLOAT_M_D, SqlType.NCHAR_N, SqlType.NUMERIC_P_S,
				SqlType.NVARCHAR_N, SqlType.VARBINARY_N, SqlType.VARCHAR_N,
				SqlType.TIMESTAMP_P, SqlType.TIMESTAMP_WITH_TIME_ZONE_P };
	}

	public SqlType[] getNeedDecimalType() {
		return new SqlType[] { SqlType.DECIMAL_P_S, SqlType.FLOAT_M_D,
				SqlType.NUMERIC_P_S, SqlType.REAL_M_D };
	}

	public Map<TypeKey, SqlType> getSqlTypeMap() {
		Map<TypeKey, SqlType> sqlTypeMap = new HashMap<TypeKey, SqlType>();

		sqlTypeMap.put(new TypeKey("int", 0), SqlType.INTEGER);
		sqlTypeMap.put(new TypeKey("varchar", 1), SqlType.VARCHAR_N);
		sqlTypeMap.put(new TypeKey("char", 1), SqlType.CHARACTER_N);
		sqlTypeMap.put(new TypeKey("decimal", 1), SqlType.DECIMAL_P_S);

		return sqlTypeMap;
	}

	public SqlType[] getUnsupportedType() {
		return new SqlType[] { SqlType.ALERT_TYPE, SqlType.ANYDATA,
				SqlType.BOX, SqlType.CIDR, SqlType.CIRCLE, SqlType.GRAPHIC,
				SqlType.INET, SqlType.INTERVAL_YEAR_TO_MONTH,
				SqlType.INTERVAL_DAY_TO_SECOND,
				SqlType.INTERVAL_YEAR_TO_MONTH_P,
				SqlType.INTERVAL_DAY_TO_SECOND_P, SqlType.LINE, SqlType.LSEG,
				SqlType.MACADDR, SqlType.MONEY, SqlType.PATH, SqlType.POINT,
				SqlType.POLYGON, SqlType.UUID, SqlType.VARCHAR,
				SqlType.VARGRAPHIC, SqlType.XML };
	}
}
