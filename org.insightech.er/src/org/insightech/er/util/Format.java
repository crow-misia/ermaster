package org.insightech.er.util;

import org.apache.commons.lang3.StringUtils;
import org.insightech.er.db.impl.mysql.MySQLDBManager;
import org.insightech.er.db.impl.postgres.PostgresDBManager;
import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.TypeData;

public final class Format {

	public static String formatType(SqlType sqlType, TypeData typeData,
			String database) {
		StringBuilder ret = new StringBuilder();

		if (sqlType != null) {
			String type = sqlType.getAlias(database);
			if (type != null) {
				if (typeData.getLength() != null
						&& typeData.getDecimal() != null) {
					type = type.replaceAll("\\(.,.\\)", "("
							+ typeData.getLength() + ","
							+ typeData.getDecimal() + ")");

					type = type.replaceFirst("\\([a-z]\\)",
							"(" + typeData.getLength() + ")").replaceFirst(
							"\\([a-z]\\)", "(" + typeData.getDecimal() + ")");

					ret.append(type);

				} else if (typeData.getLength() != null) {
					String len = null;

					if ("BLOB".equalsIgnoreCase(type)) {
						len = getFileSizeStr(typeData.getLength().longValue());
					} else {
						len = String.valueOf(typeData.getLength());
					}

					ret.append(type.replaceAll("\\(.\\)", "(" + len + ")"));

				} else {
					ret.append(type);
				}

				if (typeData.isArray() && PostgresDBManager.ID.equals(database)) {
					for (int i=typeData.getArrayDimension(); i > 0; i--) {
						ret.append("[]");
					}
				}

				if (sqlType.isNumber() && typeData.isUnsigned()
						&& MySQLDBManager.ID.equals(database)) {
					ret.append(" unsigned");
				}

				if (sqlType.doesNeedArgs()) {
					ret.append('(').append(typeData.getArgs()).append(')');
				}
			}
		}

		return ret.toString();
	}

	public static String getFileSizeStr(long fileSize) {
		long size = fileSize;
		String unit = "";

		if (size > 1024) {
			size = size / 1024;
			unit = "K";

			if (size > 1024) {
				size = size / 1024;
				unit = "M";

				if (size > 1024) {
					size = size / 1024;
					unit = "G";
				}
			}
		}

		return size + unit;
	}

	public static String null2blank(String str) {
		if (str == null) {
			return "";
		}
		return str;
	}

	public static String escapeSQL(final String str) {
		final String r = StringUtils.replace(str, "'", "''");
		return StringUtils.replace(r, "\\", "\\\\");
	}

	public static String toString(Object value) {
		if (value == null) {
			return "";
		}

		return String.valueOf(value);
	}
}
