package org.insightech.er.db.sqltype;

import java.io.Serializable;
import java.sql.Blob;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.TypeData;
import org.insightech.er.util.Format;

public class SqlType implements Serializable {

	private static Logger logger = Logger.getLogger(SqlType.class.getName());

	private static final long serialVersionUID = -8273043043893517634L;

	private static final List<SqlType> SQL_TYPE_LIST = new ArrayList<SqlType>();

	public static final SqlType NUMERIC = new SqlType("numeric", true,
			Integer.class);

	public static final SqlType CHARACTER_N = new SqlType("character(n)",
			false, String.class);

	public static final SqlType VARCHAR_N = new SqlType("varchar(n)", false,
			String.class);

	public static final SqlType INTEGER = new SqlType("integer", true,
			Integer.class);

	public static final SqlType LONG = new SqlType("long", false, Long.class);

	public static final SqlType FLOAT = new SqlType("float", true, Float.class);

	public static final SqlType FLOAT_P = new SqlType("float(p)", true,
			Float.class);

	public static final SqlType FLOAT_M_D = new SqlType("float(m,d)", true,
			Float.class);

	public static final SqlType DOUBLE_PRECISION = new SqlType(
			"double precision", false, Double.class);

	public static final SqlType ALERT_TYPE = new SqlType("alert_type", false,
			Object.class);

	public static final SqlType ANYDATA = new SqlType("anydata", false,
			Object.class);

	public static final SqlType BIGINT = new SqlType("bigint", true, Long.class);

	public static final SqlType BIG_SERIAL = new SqlType("bigserial", true,
			Long.class);

	public static final SqlType BINARY_DOUBLE = new SqlType("binary_double",
			true, Double.class);

	public static final SqlType BINARY_FLOAT = new SqlType("binary_float",
			true, Float.class);

	public static final SqlType BINARY_N = new SqlType("binary(n)", false,
			Blob.class);

	public static final SqlType BIT_N = new SqlType("bit(n)", true,
			Object.class);

	public static final SqlType BIT_VARYING_N = new SqlType("bit varying(n)",
			true, Object.class);

	public static final SqlType BLOB = new SqlType("blob", false, Blob.class);

	public static final SqlType BOOLEAN = new SqlType("boolean", false,
			Boolean.class);

	public static final SqlType BOX = new SqlType("box", false, Object.class);

	public static final SqlType CIDR = new SqlType("cidr", false, Object.class);

	public static final SqlType CIRCLE = new SqlType("circle", false,
			Object.class);

	public static final SqlType CLOB = new SqlType("clob", false, String.class);

	public static final SqlType DATE = new SqlType("date", false, Date.class);

	public static final SqlType DATE_TIME = new SqlType("datetime", false,
			Date.class);

	public static final SqlType DECIMAL_P_S = new SqlType("decimal(p,s)", true,
			Double.class);

	public static final SqlType ENUM = new SqlType("enum", false, Object.class);

	public static final SqlType GRAPHIC = new SqlType("graphic(n)", false,
			Object.class);

	public static final SqlType INET = new SqlType("inet", false, Object.class);

	public static final SqlType INTERVAL_YEAR_TO_MONTH = new SqlType(
			"interval year to month", false, Integer.class);

	public static final SqlType INTERVAL_DAY_TO_SECOND = new SqlType(
			"interval day to second", false, Integer.class);

	public static final SqlType INTERVAL_YEAR_TO_MONTH_P = new SqlType(
			"interval year to month(p)", false, Integer.class);

	public static final SqlType INTERVAL_DAY_TO_SECOND_P = new SqlType(
			"interval day to second(p)", false, Integer.class);

	public static final SqlType LINE = new SqlType("line", false, Object.class);

	public static final SqlType LONG_BLOB = new SqlType("longblob", false,
			Blob.class);

	public static final SqlType LONG_RAW = new SqlType("long raw", false,
			Blob.class);

	public static final SqlType LONG_TEXT = new SqlType("longtext", false,
			String.class);

	public static final SqlType LSEG = new SqlType("lseg", false, Object.class);

	public static final SqlType MACADDR = new SqlType("macaddr", false,
			Object.class);

	public static final SqlType MEDIUM_BLOB = new SqlType("mediumblob", false,
			Blob.class);

	public static final SqlType MEDIUM_INT = new SqlType("mediumint", true,
			Integer.class);

	public static final SqlType MEDIUM_TEXT = new SqlType("mediumtext", false,
			String.class);

	public static final SqlType MONEY = new SqlType("money", false,
			Object.class);

	public static final SqlType NCHAR_N = new SqlType("nchar(n)", false,
			String.class);

	public static final SqlType NCLOB = new SqlType("nclob", false,
			String.class);

	public static final SqlType NUMERIC_P_S = new SqlType("numeric(p,s)", true,
			Double.class);

	public static final SqlType NVARCHAR_N = new SqlType("nvarchar(n)", false,
			String.class);

	public static final SqlType PATH = new SqlType("path", false, Object.class);

	public static final SqlType POINT = new SqlType("point", false,
			Object.class);

	public static final SqlType POLYGON = new SqlType("polygon", false,
			Object.class);

	public static final SqlType REAL = new SqlType("real", true, Double.class);

	public static final SqlType REAL_M_D = new SqlType("real(m,d)", true,
			Double.class);

	public static final SqlType SERIAL = new SqlType("serial", true,
			Integer.class);

	public static final SqlType SET = new SqlType("set", false, Object.class);

	public static final SqlType SMALLINT = new SqlType("smallint", true,
			Short.class);

	public static final SqlType TEXT = new SqlType("text", false, String.class);

	public static final SqlType TIME = new SqlType("time", false, Time.class);

	public static final SqlType TIME_P = new SqlType("time(p)", false,
			Time.class);

	public static final SqlType TIME_WITH_TIME_ZONE = new SqlType(
			"time with time zone", false, Time.class);

	public static final SqlType TIME_WITH_TIME_ZONE_P = new SqlType(
			"time(p) with time zone", false, Time.class);

	public static final SqlType TIMESTAMP = new SqlType("timestamp", false,
			Date.class);

	public static final SqlType TIMESTAMP_P = new SqlType("timestamp(p)",
			false, Date.class);

	public static final SqlType TIMESTAMP_WITH_TIME_ZONE = new SqlType(
			"timestamp with time zone", false, Date.class);

	public static final SqlType TIMESTAMP_WITH_TIME_ZONE_P = new SqlType(
			"timestamp(p) with time zone", false, Date.class);

	public static final SqlType TINY_BLOB = new SqlType("tinyblob", false,
			Blob.class);

	public static final SqlType TINY_INT = new SqlType("tinyint", true,
			Byte.class);

	public static final SqlType TINY_TEXT = new SqlType("tinytext", false,
			String.class);

	public static final SqlType UUID = new SqlType("uuid", false,
			String.class);

//	public static final SqlType VARBINARY_MAX = new SqlType("varbinary(max)",
//	false, Blob.class);
	
	public static final SqlType VARBINARY_N = new SqlType("varbinary(n)",
			false, Blob.class);

	public static final SqlType VARCHAR = new SqlType("varchar", false,
			String.class);

	public static final SqlType VARGRAPHIC = new SqlType("vargraphic(n)",
			false, Object.class);

	public static final SqlType XML = new SqlType("xml", false, Object.class);

	public static final SqlType YEAR_2 = new SqlType("year(2)", false,
			Integer.class);

	public static final SqlType YEAR_4 = new SqlType("year(4)", false,
			Integer.class);

	private String name;

	private boolean number;

	private Class javaClass;

	private static Map<String, Map<TypeKey, SqlType>> databaseSqlTypeMap = new HashMap<String, Map<TypeKey, SqlType>>();

	static {
		for (String database : DBManagerFactory.getAllDBList()) {
			Map<TypeKey, SqlType> sqlTypeMap = new HashMap<TypeKey, SqlType>();

			for (SqlType sqlType : SQL_TYPE_LIST) {

				String alias = sqlType.getAlias(database);
				if (alias != null) {
					alias = alias.replaceAll("\\(\\.+\\)", "");
				}

				TypeKey typeKey = new TypeKey(alias, 0);
				if (!sqlTypeMap.containsKey(typeKey)) {
					sqlTypeMap.put(typeKey, sqlType);
				}
			}

			SqlTypeManager manager = getSqlTypeManager(database);
			sqlTypeMap.putAll(manager.getSqlTypeMap());

			databaseSqlTypeMap.put(database, sqlTypeMap);
		}
	}

	public static class TypeKey {
		private String alias;

		private int size;

		public TypeKey(String alias, int size) {
			if (alias != null) {
				alias = alias.toUpperCase();
			}

			this.alias = alias;

			if (size == 0) {
				this.size = 0;
			} else if (size == Integer.MAX_VALUE) {
				this.size = 0;
			} else if (size > 0) {
				this.size = 1;
			} else {
				this.size = -1;
			}
		}

		@Override
		public boolean equals(Object obj) {
			TypeKey other = (TypeKey) obj;

			if (this.alias == null) {
				if (other.alias == null) {
					if (this.size == other.size) {
						return true;
					}
					return false;

				} else {
					return false;
				}

			} else {
				if (this.alias.equals(other.alias) && this.size == other.size) {
					return true;
				}
			}

			return false;
		}

		@Override
		public int hashCode() {
			if (this.alias == null) {
				return this.size;
			}
			return (this.alias.hashCode() * 10) + this.size;
		}

		@Override
		public String toString() {
			return "TypeKey [alias=" + alias + ", size=" + size + "]";
		}

	}

	private SqlType(String name, boolean number, Class javaClass) {
		this.name = name;
		this.number = number;
		this.javaClass = javaClass;

		SQL_TYPE_LIST.add(this);
	}

	public String getId() {
		return this.name;
	}

	public Class getJavaClass() {
		return javaClass;
	}

	protected static List<SqlType> getAllSqlType() {
		return SQL_TYPE_LIST;
	}

	public static SqlType valueOf(String database, String alias) {
		return valueOf(database, alias, 0);
	}

	public static SqlType valueOf(String database, String alias, int size) {
		if (alias == null) {
			return null;
		}

		Map<TypeKey, SqlType> sqlTypeMap = databaseSqlTypeMap.get(database);

		TypeKey typeKey = new TypeKey(alias, size);
		SqlType sqlType = sqlTypeMap.get(typeKey);

		if (sqlType == null) {
			alias = alias.replaceAll("\\(\\d*\\)", "");
			alias = alias.replaceAll(" UNSIGNED", "");

			typeKey = new TypeKey(alias, size);
			sqlType = sqlTypeMap.get(typeKey);

			if (sqlType == null) {
				typeKey = new TypeKey(alias, 0);
				sqlType = sqlTypeMap.get(typeKey);
			}
		}

		return sqlType;
	}

	public static SqlType valueOfId(String id) {
		SqlType sqlType = null;

		if (id == null) {
			return null;
		}

		for (SqlType type : SQL_TYPE_LIST) {
			if (id.equals(type.getId())) {
				sqlType = type;
			}
		}
		return sqlType;
	}

	public boolean isNeedLength(String database) {
		SqlTypeManager manager = getSqlTypeManager(database);

		SqlType[] types = manager.getNeedLengthType();

		for (int i = 0; i < types.length; i++) {
			if (this.equals(types[i])) {
				return true;
			}
		}

		return false;
	}

	public boolean isNeedDecimal(String database) {
		SqlTypeManager manager = getSqlTypeManager(database);

		SqlType[] types = manager.getNeedDecimalType();

		for (int i = 0; i < types.length; i++) {
			if (this.equals(types[i])) {
				return true;
			}
		}

		return false;
	}

	public boolean isTimestamp() {
		if (this == SqlType.TIMESTAMP) {
			return true;
		}
		if (this == SqlType.TIMESTAMP_P) {
			return true;
		}
		if (this == SqlType.TIMESTAMP_WITH_TIME_ZONE) {
			return true;
		}
		if (this == SqlType.TIMESTAMP_WITH_TIME_ZONE_P) {
			return true;
		}
		if (this == SqlType.DATE_TIME) {
			return true;
		}
		if (this == SqlType.DATE) {
			return true;
		}

		return false;
	}

	public boolean isNumber() {
		return this.number;
	}

	public static List<String> getAliasList(String database) {
		SqlTypeManager manager = getSqlTypeManager(database);
		return manager.getAliasList();
	}

	public String getAlias(String database) {
		SqlTypeManager manager = getSqlTypeManager(database);

		return manager.getAlias(this);
	}

	public boolean isUnsupported(String database) {
		SqlTypeManager manager = getSqlTypeManager(database);
		SqlType[] unsupportedTypes = manager.getUnsupportedType();

		if (unsupportedTypes != null) {
			for (SqlType type : unsupportedTypes) {
				if (type.equals(this)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof SqlType)) {
			return false;
		}

		SqlType type = (SqlType) obj;

		return this.name.equals(type.name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return this.getId();
	}

	private static SqlTypeManager getSqlTypeManager(String database) {
		return DBManagerFactory.getDBManager(database).getSqlTypeManager();

	}

	public static void main(String[] args) {
		StringBuilder msg = new StringBuilder();

		msg.append("\n");

		List<SqlType> list = getAllSqlType();

		List<String> dbList = DBManagerFactory.getAllDBList();

		String str = "ID";
		msg.append(str);

		for (String db : dbList) {
			int spaceLength = 20 - str.length();
			if (spaceLength < 4) {
				spaceLength = 4;
			}

			for (int i = 0; i < spaceLength; i++) {
				msg.append(" ");
			}

			str = db;
			msg.append(db);
		}

		msg.append("\n");
		msg.append("\n");

		StringBuilder builder = new StringBuilder();
		int errorCount = 0;

		for (SqlType type : list) {
			builder.append(type.name);
			int spaceLength = 20 - type.name.length();
			if (spaceLength < 4) {
				spaceLength = 4;
			}

			for (String db : dbList) {
				for (int i = 0; i < spaceLength; i++) {
					builder.append(" ");
				}

				String alias = type.getAlias(db);

				if (alias != null) {
					builder.append(type.getAlias(db));
					spaceLength = 20 - type.getAlias(db).length();
					if (spaceLength < 4) {
						spaceLength = 4;
					}

				} else {
					if (type.isUnsupported(db)) {
						builder.append("      ");
					} else {
						builder.append("¡¡¡¡¡¡");
						errorCount++;
					}

					spaceLength = 20 - "      ".length();
					if (spaceLength < 4) {
						spaceLength = 4;
					}
				}
			}

			builder.append("\r\n");
		}

		String allColumn = builder.toString();
		msg.append(allColumn + "\n");

		int errorCount2 = 0;
		int errorCount3 = 0;

		Pattern p = Pattern.compile(".*\\([a-z].*");

		for (String db : dbList) {
			msg.append("-- for " + db + "\n");
			msg.append("CREATE TABLE TYPE_TEST (\n");

			int count = 0;

			for (SqlType type : list) {
				String alias = type.getAlias(db);
				if (alias == null) {
					continue;
				}

				if (count != 0) {
					msg.append(",\n");
				}
				msg.append("\tCOL_" + count + " ");

				if (type.isNeedLength(db) && type.isNeedDecimal(db)) {
					TypeData typeData = new TypeData(new Integer(1),
							new Integer(1), false, null, false, null);

					str = Format.formatType(type, typeData, db);
					if (str.equals(alias)) {
						errorCount3++;
						msg.append("~3");
					}

				} else if (type.isNeedLength(db)) {
					TypeData typeData = new TypeData(new Integer(1), null,
							false, null, false, null);

					str = Format.formatType(type, typeData, db);

					if (str.equals(alias)) {
						errorCount3++;
						msg.append("~3");
					}

				} else if (type.isNeedDecimal(db)) {
					TypeData typeData = new TypeData(null, new Integer(1),
							false, null, false, null);

					str = Format.formatType(type, typeData, db);

					if (str.equals(alias)) {
						errorCount3++;
						msg.append("~3");
					}

				} else {
					str = alias;
				}

				if (str != null) {

					Matcher m = p.matcher(str);

					if (m.matches()) {
						errorCount2++;
						msg.append("~2");
					}
				}

				msg.append(str);

				count++;
			}
			msg.append("\n");
			msg.append(");\n");
			msg.append("\n");
		}

		msg.append("\n");
		msg.append(errorCount + " ŒÂ‚ÌŒ^‚ª•ÏŠ·‚Å‚«‚Ü‚¹‚ñ‚Å‚µ‚½B\n");
		msg.append(errorCount2 + " ŒÂ‚Ì”ŽšŒ^‚ÌŽw’è‚ª•s‘«‚µ‚Ä‚¢‚Ü‚·B\n");
		msg.append(errorCount3 + " ŒÂ‚Ì”ŽšŒ^‚ÌŽw’è‚ª—]•ª‚Å‚·B\n");

		logger.info(msg.toString());
	}
}
