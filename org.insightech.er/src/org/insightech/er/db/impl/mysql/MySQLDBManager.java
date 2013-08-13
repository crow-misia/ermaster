package org.insightech.er.db.impl.mysql;

import static org.insightech.er.db.SupportFunction.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.insightech.er.db.DBManagerBase;
import org.insightech.er.db.SupportFunction;
import org.insightech.er.db.impl.mysql.tablespace.MySQLTablespaceProperties;
import org.insightech.er.db.sqltype.SqlTypeManager;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.db.PreTableExportManager;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.dbimport.ImportFromDBManager;
import org.insightech.er.editor.model.dbimport.PreImportFromDBManager;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;

public class MySQLDBManager extends DBManagerBase {

	public static final String ID = "MySQL";

	private static final ResourceBundle CHARACTER_SET_RESOURCE = ResourceBundle
			.getBundle("mysql_characterset");

	public String getId() {
		return ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDriverClassName() {
		return "com.mysql.jdbc.Driver";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getURL() {
		return "jdbc:mysql://<SERVER NAME>:<PORT>/<DB NAME>";
	}

	public int getDefaultPort() {
		return 3306;
	}

	public SqlTypeManager getSqlTypeManager() {
		return new MySQLSqlTypeManager();
	}

	public TableProperties createTableProperties(TableProperties tableProperties) {
		if (tableProperties != null
				&& tableProperties instanceof MySQLTableProperties) {
			return tableProperties;
		}

		return new MySQLTableProperties();
	}

	public DDLCreator getDDLCreator(ERDiagram diagram) {
		return new MySQLDDLCreator(diagram);
	}

	public String[] getIndexTypeList(ERTable table) {
		return new String[] { "BTREE", "RTREE", "HASH", };
	}

	@Override
	protected SupportFunction[] getSupportItems() {
		return new SupportFunction[] {
				AUTO_INCREMENT,
				AUTO_INCREMENT_SETTING,
				DESC_INDEX,
				FULLTEXT_INDEX,
				SCHEMA,
				COLUMN_CHARSET,
		};
	}

	public ImportFromDBManager getTableImportManager() {
		return new MySQLTableImportManager();
	}

	public PreImportFromDBManager getPreTableImportManager() {
		return new MySQLPreTableImportManager();
	}

	public PreTableExportManager getPreTableExportManager() {
		return new MySQLPreTableExportManager();
	}

	public TablespaceProperties createTablespaceProperties() {
		return new MySQLTablespaceProperties();
	}

	public TablespaceProperties checkTablespaceProperties(
			TablespaceProperties tablespaceProperties) {

		if (!(tablespaceProperties instanceof MySQLTablespaceProperties)) {
			return new MySQLTablespaceProperties();
		}

		return tablespaceProperties;
	}

	public String[] getCurrentTimeValue() {
		return new String[] { "NOW(), SYSDATE()" };
	}

	public BigDecimal getSequenceMaxValue() {
		return null;
	}

	public int getCacheMinValue() {
		return 1;
	}

	public static List<String> getCharacterSetList() {
		List<String> list = new ArrayList<String>();

		Enumeration<String> keys = CHARACTER_SET_RESOURCE.getKeys();

		while (keys.hasMoreElements()) {
			list.add(keys.nextElement());
		}

		return list;
	}

	public static List<String> getCollationList(String characterset) {
		List<String> list = new ArrayList<String>();

		if (characterset != null) {
			try {
				String values = CHARACTER_SET_RESOURCE.getString(characterset);

				if (values != null) {
					StringTokenizer tokenizer = new StringTokenizer(values, ",");

					while (tokenizer.hasMoreElements()) {
						list.add(StringUtils.trim(tokenizer.nextToken()));
					}
				}
			} catch (MissingResourceException e) {
			}
		}

		return list;
	}
}
