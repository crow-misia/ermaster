package org.insightech.er.db.impl.oracle;

import java.util.HashMap;
import java.util.Map;

import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.db.sqltype.SqlTypeManagerBase;
import org.insightech.er.db.sqltype.SqlType.TypeKey;

public class OracleSqlTypeManager extends SqlTypeManagerBase {

	public String getAlias(SqlType type) {
		if (type.equals(SqlType.ALERT_TYPE)) {
			return "alert_type";
		}
		if (type.equals(SqlType.ANYDATA)) {
			return "anydata";
		}
		if (type.equals(SqlType.BIGINT)) {
			return "number(19,0)";
		}
		if (type.equals(SqlType.BIG_SERIAL)) {
			return "number(19,0)";
		}
		if (type.equals(SqlType.BINARY_DOUBLE)) {
			return "binary_double";
		}
		if (type.equals(SqlType.BINARY_FLOAT)) {
			return "binary_float";
		}
		if (type.equals(SqlType.BINARY_N)) {
			return "blob";
		}
		if (type.equals(SqlType.BIT_N)) {
			return "number";
		}
		if (type.equals(SqlType.BIT_VARYING_N)) {
			return "number";
		}
		if (type.equals(SqlType.BLOB)) {
			return "blob";
		}
		if (type.equals(SqlType.BOOLEAN)) {
			return "char(1)";
		}
		if (type.equals(SqlType.CHARACTER_N)) {
			return "char(n)";
		}
		if (type.equals(SqlType.CLOB)) {
			return "clob";
		}
		if (type.equals(SqlType.DATE)) {
			return "date";
		}
		if (type.equals(SqlType.DATE_TIME)) {
			return "date";
		}
		if (type.equals(SqlType.DECIMAL_P_S)) {
			return "number(p,s)";
		}
		if (type.equals(SqlType.DOUBLE_PRECISION)) {
			return "binary_double";
		}
		if (type.equals(SqlType.FLOAT)) {
			return "float";
		}
		if (type.equals(SqlType.FLOAT_P)) {
			return "float(p)";
		}
		if (type.equals(SqlType.FLOAT_M_D)) {
			return "float(p)";
		}
		if (type.equals(SqlType.INTEGER)) {
			return "number(10,0)";
		}
		if (type.equals(SqlType.INTERVAL_YEAR_TO_MONTH)) {
			return "interval year to month";
		}
		if (type.equals(SqlType.INTERVAL_DAY_TO_SECOND)) {
			return "interval day to second";
		}
		if (type.equals(SqlType.INTERVAL_YEAR_TO_MONTH_P)) {
			return "interval year to month";
		}
		if (type.equals(SqlType.INTERVAL_DAY_TO_SECOND_P)) {
			return "interval day to second";
		}
		if (type.equals(SqlType.LONG)) {
			return "long";
		}
		if (type.equals(SqlType.LONG_BLOB)) {
			return "blob";
		}
		if (type.equals(SqlType.LONG_RAW)) {
			return "long raw";
		}
		if (type.equals(SqlType.LONG_TEXT)) {
			return "clob";
		}
		if (type.equals(SqlType.MEDIUM_BLOB)) {
			return "blob";
		}
		if (type.equals(SqlType.MEDIUM_INT)) {
			return "number(7,0)";
		}
		if (type.equals(SqlType.MEDIUM_TEXT)) {
			return "clob";
		}
		if (type.equals(SqlType.NCHAR_N)) {
			return "nchar(n)";
		}
		if (type.equals(SqlType.NCLOB)) {
			return "nclob";
		}
		if (type.equals(SqlType.NUMERIC)) {
			return "number";
		}
		if (type.equals(SqlType.NUMERIC_P_S)) {
			return "number(p,s)";
		}
		if (type.equals(SqlType.NVARCHAR_N)) {
			return "nvarchar2(n)";
		}
		if (type.equals(SqlType.REAL)) {
			return "float(24)";
		}
		if (type.equals(SqlType.REAL_M_D)) {
			return "float(24)";
		}
		if (type.equals(SqlType.SERIAL)) {
			return "number(10,0)";
		}
		if (type.equals(SqlType.SMALLINT)) {
			return "number(5,0)";
		}
		if (type.equals(SqlType.TEXT)) {
			return "clob";
		}
		if (type.equals(SqlType.TIME)) {
			return "date";
		}
		if (type.equals(SqlType.TIME_P)) {
			return "date";
		}
		if (type.equals(SqlType.TIME_WITH_TIME_ZONE)) {
			return "timestamp with time zone";
		}
		if (type.equals(SqlType.TIME_WITH_TIME_ZONE_P)) {
			return "timestamp(p) with time zone";
		}
		if (type.equals(SqlType.TIMESTAMP)) {
			return "timestamp";
		}
		if (type.equals(SqlType.TIMESTAMP_P)) {
			return "timestamp(p)";
		}
		if (type.equals(SqlType.TIMESTAMP_WITH_TIME_ZONE)) {
			return "timestamp with time zone";
		}
		if (type.equals(SqlType.TIMESTAMP_WITH_TIME_ZONE_P)) {
			return "timestamp(p) with time zone";
		}
		if (type.equals(SqlType.TINY_BLOB)) {
			return "blob";
		}
		if (type.equals(SqlType.TINY_INT)) {
			return "number(3,0)";
		}
		if (type.equals(SqlType.TINY_TEXT)) {
			return "varchar2(n)";
		}
		if (type.equals(SqlType.VARBINARY_N)) {
			return "raw(n)";
		}
		if (type.equals(SqlType.VARCHAR)) {
			return "varchar2(n)";
		}
		if (type.equals(SqlType.VARCHAR_N)) {
			return "varchar2(n)";
		}
		if (type.equals(SqlType.YEAR_2)) {
			return "number";
		}
		if (type.equals(SqlType.YEAR_4)) {
			return "number";
		}
		if (type.equals(SqlType.XML)) {
			return "xmltype(n)";
		}
		return null;
	}

	public int getByteLength(SqlType type, Integer length, Integer decimal) {
		if (type == null) {
			return 0;
		}

		if (type.equals(SqlType.ALERT_TYPE)) {
			return 0;
		}
		if (type.equals(SqlType.ANYDATA)) {
			return 0;
		}
		if (type.equals(SqlType.BIGINT)) {
			return 11;
		}
		if (type.equals(SqlType.BIG_SERIAL)) {
			return 11;
		}
		if (type.equals(SqlType.BINARY_DOUBLE)) {
			return 8;
		}
		if (type.equals(SqlType.BINARY_FLOAT)) {
			return 4;
		}
		if (type.equals(SqlType.BINARY_N)) {
			return 0;
		}
		if (type.equals(SqlType.BIT_N)) {
			return 0;
		}
		if (type.equals(SqlType.BIT_VARYING_N)) {
			return 0;
		}
		if (type.equals(SqlType.BLOB)) {
			return 0;
		}
		if (type.equals(SqlType.BOOLEAN)) {
			return 1 * 3;
		}
		if (type.equals(SqlType.CHARACTER_N)) {
			return length * 3;
		}
		if (type.equals(SqlType.CLOB)) {
			return 0;
		}
		if (type.equals(SqlType.DATE)) {
			return 7;
		}
		if (type.equals(SqlType.DATE_TIME)) {
			return 7;
		}
		if (type.equals(SqlType.DECIMAL_P_S)) {
			return 1 + (int) Math.ceil(((double) length) / 2);
		}
		if (type.equals(SqlType.DOUBLE_PRECISION)) {
			return 8;
		}
		if (type.equals(SqlType.FLOAT)) {
			return 4;
		}
		if (type.equals(SqlType.FLOAT_P)) {
			return (int) Math.ceil(((double) length) / 4);
		}
		if (type.equals(SqlType.FLOAT_M_D)) {
			return (int) Math.ceil(((double) length) / 4);
		}
		if (type.equals(SqlType.INTEGER)) {
			return 6;
		}
		if (type.equals(SqlType.INTERVAL_YEAR_TO_MONTH)) {
			return 5;
		}
		if (type.equals(SqlType.INTERVAL_DAY_TO_SECOND)) {
			return 11;
		}
		if (type.equals(SqlType.INTERVAL_YEAR_TO_MONTH_P)) {
			return 5;
		}
		if (type.equals(SqlType.INTERVAL_DAY_TO_SECOND_P)) {
			return 11;
		}
		if (type.equals(SqlType.LONG)) {
			return length;
		}
		if (type.equals(SqlType.LONG_BLOB)) {
			return 0;
		}
		if (type.equals(SqlType.LONG_RAW)) {
			return length;
		}
		if (type.equals(SqlType.LONG_TEXT)) {
			return 0;
		}
		if (type.equals(SqlType.MEDIUM_BLOB)) {
			return 0;
		}
		if (type.equals(SqlType.MEDIUM_INT)) {
			return 5;
		}
		if (type.equals(SqlType.MEDIUM_TEXT)) {
			return 0;
		}
		if (type.equals(SqlType.NCHAR_N)) {
			return length * 3;
		}
		if (type.equals(SqlType.NCLOB)) {
			return 0;
		}
		if (type.equals(SqlType.NUMERIC)) {
			return 20;
		}
		if (type.equals(SqlType.NUMERIC_P_S)) {
			return 1 + (int) Math.ceil(((double) length) / 2);
		}
		if (type.equals(SqlType.NVARCHAR_N)) {
			return length * 3;
		}
		if (type.equals(SqlType.REAL)) {
			return 3;
		}
		if (type.equals(SqlType.REAL_M_D)) {
			return 3;
		}
		if (type.equals(SqlType.SERIAL)) {
			return 6;
		}
		if (type.equals(SqlType.SMALLINT)) {
			return 4;
		}
		if (type.equals(SqlType.TEXT)) {
			return 0;
		}
		if (type.equals(SqlType.TIME)) {
			return 7;
		}
		if (type.equals(SqlType.TIME_P)) {
			return 7;
		}
		if (type.equals(SqlType.TIME_WITH_TIME_ZONE)) {
			return 13;
		}
		if (type.equals(SqlType.TIME_WITH_TIME_ZONE_P)) {
			return 13;
		}
		if (type.equals(SqlType.TIMESTAMP)) {
			return 11;
		}
		if (type.equals(SqlType.TIMESTAMP_P)) {
			return 11;
		}
		if (type.equals(SqlType.TIMESTAMP_WITH_TIME_ZONE)) {
			return 13;
		}
		if (type.equals(SqlType.TIMESTAMP_WITH_TIME_ZONE_P)) {
			return 13;
		}
		if (type.equals(SqlType.TINY_BLOB)) {
			return 0;
		}
		if (type.equals(SqlType.TINY_INT)) {
			return 2;
		}
		if (type.equals(SqlType.TINY_TEXT)) {
			return length * 3;
		}
		if (type.equals(SqlType.VARBINARY_N)) {
			return length;
		}
		if (type.equals(SqlType.VARCHAR)) {
			return length * 3;
		}
		if (type.equals(SqlType.VARCHAR_N)) {
			return length * 3;
		}
		if (type.equals(SqlType.YEAR_2)) {
			return 20;
		}
		if (type.equals(SqlType.YEAR_4)) {
			return 20;
		}
		if (type.equals(SqlType.XML)) {
			return length;
		}
		return 0;
	}

	public SqlType[] getNeedLengthType() {
		return new SqlType[] { SqlType.CHARACTER_N, SqlType.DECIMAL_P_S,
				SqlType.FLOAT_P, SqlType.FLOAT_M_D, SqlType.NCHAR_N,
				SqlType.NUMERIC_P_S, SqlType.NVARCHAR_N,
				SqlType.TIME_WITH_TIME_ZONE_P, SqlType.TIMESTAMP_P,
				SqlType.TIMESTAMP_WITH_TIME_ZONE_P, SqlType.TINY_TEXT,
				SqlType.VARBINARY_N, SqlType.VARCHAR, SqlType.VARCHAR_N,
				SqlType.XML };
	}

	public SqlType[] getNeedDecimalType() {
		return new SqlType[] { SqlType.DECIMAL_P_S, SqlType.NUMERIC_P_S };
	}

	public Map<TypeKey, SqlType> getSqlTypeMap() {
		Map<TypeKey, SqlType> sqlTypeMap = new HashMap<TypeKey, SqlType>();

		sqlTypeMap.put(new TypeKey("char", 0), SqlType.BOOLEAN);
		sqlTypeMap.put(new TypeKey("char", 1), SqlType.CHARACTER_N);
		sqlTypeMap.put(new TypeKey("nchar", 1), SqlType.NCHAR_N);
		sqlTypeMap.put(new TypeKey("varchar2", 1), SqlType.VARCHAR_N);
		sqlTypeMap.put(new TypeKey("number", 1), SqlType.NUMERIC_P_S);
		sqlTypeMap.put(new TypeKey("nvarchar2", 1), SqlType.NVARCHAR_N);
		sqlTypeMap.put(new TypeKey("raw", 1), SqlType.VARBINARY_N);
		sqlTypeMap.put(new TypeKey("xmltype", 1), SqlType.XML);

		return sqlTypeMap;
	}

	public SqlType[] getUnsupportedType() {
		return new SqlType[] { SqlType.BOX, SqlType.CIDR, SqlType.CIRCLE,
				SqlType.ENUM, SqlType.GRAPHIC, SqlType.INET, SqlType.LINE,
				SqlType.LSEG, SqlType.MACADDR, SqlType.MONEY, SqlType.PATH,
				SqlType.POINT, SqlType.POLYGON, SqlType.SET, SqlType.UUID,
				SqlType.VARGRAPHIC, SqlType.XML };
	}

}
