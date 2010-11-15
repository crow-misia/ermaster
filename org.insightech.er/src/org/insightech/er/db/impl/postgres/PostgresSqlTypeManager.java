package org.insightech.er.db.impl.postgres;

import java.util.HashMap;
import java.util.Map;

import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.db.sqltype.SqlTypeManagerBase;
import org.insightech.er.db.sqltype.SqlType.TypeKey;

public class PostgresSqlTypeManager extends SqlTypeManagerBase {

	public String getAlias(SqlType type) {
		if (type.equals(SqlType.BIGINT)) {
			return "bigint";
		}
		if (type.equals(SqlType.BIG_SERIAL)) {
			return "bigserial";
		}
		if (type.equals(SqlType.BINARY_DOUBLE)) {
			return "float8";
		}
		if (type.equals(SqlType.BINARY_FLOAT)) {
			return "float8";
		}
		if (type.equals(SqlType.BINARY_N)) {
			return "char(n)";
		}
		if (type.equals(SqlType.BIT_N)) {
			return "bit";
		}
		if (type.equals(SqlType.BIT_VARYING_N)) {
			return "bit varying(n)";
		}
		if (type.equals(SqlType.BLOB)) {
			return "bytea";
		}
		if (type.equals(SqlType.BOOLEAN)) {
			return "boolean";
		}
		if (type.equals(SqlType.BOX)) {
			return "box";
		}
		if (type.equals(SqlType.CHARACTER_N)) {
			return "char(n)";
		}
		if (type.equals(SqlType.CIDR)) {
			return "cidr";
		}
		if (type.equals(SqlType.CIRCLE)) {
			return "circle";
		}
		if (type.equals(SqlType.CLOB)) {
			return "text";
		}
		if (type.equals(SqlType.DATE)) {
			return "date";
		}
		if (type.equals(SqlType.DATE_TIME)) {
			return "timestamp";
		}
		if (type.equals(SqlType.DECIMAL_P_S)) {
			return "decimal(p,s)";
		}
		if (type.equals(SqlType.DOUBLE_PRECISION)) {
			return "float8";
		}
		if (type.equals(SqlType.FLOAT)) {
			return "float8";
		}
		if (type.equals(SqlType.FLOAT_P)) {
			return "float8";
		}
		if (type.equals(SqlType.FLOAT_M_D)) {
			return "float8";
		}
		if (type.equals(SqlType.INET)) {
			return "inet";
		}
		if (type.equals(SqlType.INTEGER)) {
			return "integer";
		}
		if (type.equals(SqlType.INTERVAL_YEAR_TO_MONTH)) {
			return "interval";
		}
		if (type.equals(SqlType.INTERVAL_DAY_TO_SECOND)) {
			return "interval";
		}
		if (type.equals(SqlType.INTERVAL_YEAR_TO_MONTH_P)) {
			return "interval(p)";
		}
		if (type.equals(SqlType.INTERVAL_DAY_TO_SECOND_P)) {
			return "interval(p)";
		}
		if (type.equals(SqlType.LINE)) {
			return "line";
		}
		if (type.equals(SqlType.LONG)) {
			return "text";
		}
		if (type.equals(SqlType.LONG_BLOB)) {
			return "bytea";
		}
		if (type.equals(SqlType.LONG_RAW)) {
			return "bytea";
		}
		if (type.equals(SqlType.LONG_TEXT)) {
			return "text";
		}
		if (type.equals(SqlType.LSEG)) {
			return "lseg";
		}
		if (type.equals(SqlType.MACADDR)) {
			return "macaddr";
		}
		if (type.equals(SqlType.MEDIUM_BLOB)) {
			return "bytea";
		}
		if (type.equals(SqlType.MEDIUM_INT)) {
			return "integer";
		}
		if (type.equals(SqlType.MEDIUM_TEXT)) {
			return "text";
		}
		if (type.equals(SqlType.MONEY)) {
			return "money";
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
		if (type.equals(SqlType.PATH)) {
			return "path";
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
			return "real";
		}
		if (type.equals(SqlType.SERIAL)) {
			return "serial";
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
			return "time(p)";
		}
		if (type.equals(SqlType.TIME_WITH_TIME_ZONE)) {
			return "time with time zone";
		}
		if (type.equals(SqlType.TIME_WITH_TIME_ZONE_P)) {
			return "time(p) with time zone";
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
			return "bytea";
		}
		if (type.equals(SqlType.TINY_INT)) {
			return "smallint";
		}
		if (type.equals(SqlType.TINY_TEXT)) {
			return "text";
		}
		if (type.equals(SqlType.UUID)) {
			return "uuid";
		}
		if (type.equals(SqlType.VARBINARY_N)) {
			return "varchar(n)";
		}
		if (type.equals(SqlType.VARCHAR)) {
			return "varchar";
		}
		if (type.equals(SqlType.VARCHAR_N)) {
			return "varchar(n)";
		}
		if (type.equals(SqlType.XML)) {
			return "xml";
		}

		return null;
	}

	public int getByteLength(SqlType type, Integer length, Integer decimal) {
		return 0;
	}

	public SqlType[] getNeedLengthType() {
		return new SqlType[] { SqlType.BINARY_N, SqlType.BIT_VARYING_N,
				SqlType.CHARACTER_N, SqlType.DECIMAL_P_S,
				SqlType.INTERVAL_YEAR_TO_MONTH_P,
				SqlType.INTERVAL_DAY_TO_SECOND_P, SqlType.NCHAR_N,
				SqlType.NUMERIC_P_S, SqlType.NVARCHAR_N, SqlType.TIME_P,
				SqlType.TIME_WITH_TIME_ZONE_P, SqlType.TIMESTAMP_P,
				SqlType.TIMESTAMP_WITH_TIME_ZONE_P, SqlType.VARBINARY_N,
				SqlType.VARCHAR_N };
	}

	public SqlType[] getNeedDecimalType() {
		return new SqlType[] { SqlType.DECIMAL_P_S, SqlType.NUMERIC_P_S

		};
	}

	public Map<TypeKey, SqlType> getSqlTypeMap() {
		Map<TypeKey, SqlType> sqlTypeMap = new HashMap<TypeKey, SqlType>();

		sqlTypeMap.put(new TypeKey("bool", 0), SqlType.BOOLEAN);
		sqlTypeMap.put(new TypeKey("bpchar", 0), SqlType.CHARACTER_N);
		sqlTypeMap.put(new TypeKey("varchar", 0), SqlType.VARCHAR);
		sqlTypeMap.put(new TypeKey("varchar", 1), SqlType.VARCHAR_N);
		sqlTypeMap.put(new TypeKey("int", 0), SqlType.INTEGER);
		sqlTypeMap.put(new TypeKey("int2", 0), SqlType.SMALLINT);
		sqlTypeMap.put(new TypeKey("int4", 0), SqlType.INTEGER);
		sqlTypeMap.put(new TypeKey("int8", 0), SqlType.BIGINT);
		sqlTypeMap.put(new TypeKey("float4", 0), SqlType.REAL);
		sqlTypeMap.put(new TypeKey("timetz", 0), SqlType.TIME_WITH_TIME_ZONE);
		sqlTypeMap.put(new TypeKey("timestamptz", 0),
				SqlType.TIMESTAMP_WITH_TIME_ZONE);
		sqlTypeMap.put(new TypeKey("varbit", 1), SqlType.BIT_VARYING_N);

		return sqlTypeMap;
	}

	public SqlType[] getUnsupportedType() {
		return new SqlType[] { SqlType.ALERT_TYPE, SqlType.ANYDATA,
				SqlType.ENUM, SqlType.GRAPHIC, SqlType.SET, SqlType.VARGRAPHIC,
				SqlType.YEAR_2, SqlType.YEAR_4 };
	}

}
