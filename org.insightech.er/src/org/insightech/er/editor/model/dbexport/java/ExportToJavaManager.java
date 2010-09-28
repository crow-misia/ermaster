package org.insightech.er.editor.model.dbexport.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.TreeSet;

import org.insightech.er.ResourceString;
import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.util.Format;
import org.insightech.er.util.io.FileUtils;
import org.insightech.er.util.io.IOUtils;

public class ExportToJavaManager {

	private static final String TEMPLATE_DIR = "java/";

	private static final String[] KEYWORDS = {
			"java.template.class.description", "java.template.constructor",
			"java.template.getter.description",
			"java.template.set.adder.description",
			"java.template.set.getter.description",
			"java.template.set.property.description",
			"java.template.set.setter.description",
			"java.template.setter.description", };

	private static final String TEMPLATE;

	private static final String IMPLEMENTS;

	private static final String PROPERTIES;

	private static final String SET_PROPERTIES;

	private static final String SETTER_GETTER;

	private static final String SETTER_GETTER_ADDER;

	private static final String HASHCODE_EQUALS;

	private static final String HASHCODE_LOGIC;

	private static final String EQUALS_LOGIC;

	private static final String EXTENDS;

	static {
		try {
			TEMPLATE = loadResource("template");
			IMPLEMENTS = loadResource("@implements");
			PROPERTIES = loadResource("@properties");
			SET_PROPERTIES = loadResource("@set_properties");
			SETTER_GETTER = loadResource("@setter_getter");
			SETTER_GETTER_ADDER = loadResource("@setter_getter_adder");
			HASHCODE_EQUALS = loadResource("@hashCode_equals");
			HASHCODE_LOGIC = loadResource("@hashCode logic");
			EQUALS_LOGIC = loadResource("@equals logic");
			EXTENDS = loadResource("@extends");

		} catch (IOException e) {
			e.printStackTrace();
			throw new ExceptionInInitializerError(e);
		}
	}

	private String outputDir;

	private String fileEncoding;

	private String packageName;

	private String packageDir;

	private String classNameSuffix;

	private String extendsClass;

	protected ERDiagram diagram;

	private Set<String> importClasseNames;

	private Set<String> sets;

	public ExportToJavaManager(String outputDir, String fileEncoding,
			String packageName, String classNameSuffix, String extendsClass,
			ERDiagram diagram) {
		this.outputDir = outputDir;
		this.fileEncoding = fileEncoding;
		this.packageName = packageName;
		this.packageDir = packageName.replaceAll("\\.", "/");
		this.diagram = diagram;

		this.classNameSuffix = classNameSuffix;
		this.extendsClass = Format.null2blank(this.extendsClass);

		this.importClasseNames = new TreeSet<String>();
		this.sets = new TreeSet<String>();
	}

	protected void doPreTask(ERTable table) {
	}

	protected void doPostTask() throws InterruptedException {
	}

	public void doProcess() throws IOException, InterruptedException {
		for (ERTable table : diagram.getDiagramContents().getContents()
				.getTableSet().getList()) {
			this.doPreTask(table);

			this.importClasseNames.clear();
			this.importClasseNames.add("java.io.Serializable");
			this.sets.clear();

			String template = this.generateContent(diagram, table, packageName);

			String className = this.getClassName(table);

			this.writeOut("/" + this.packageDir + "/" + className + ".java",
					template);
		}
	}

	protected String getClassName(ERTable table) {
		return this.getCamelCaseName(table)
				+ this.getCamelCaseName(this.classNameSuffix, true);
	}

	protected String getCamelCaseName(ERTable table) {
		return this.getCamelCaseName(table.getPhysicalName(), true);
	}

	protected String getCamelCaseName(String name, boolean capital) {
		String className = name.toLowerCase();

		if (capital && className.length() > 0) {
			String first = className.substring(0, 1);
			String other = className.substring(1);

			className = first.toUpperCase() + other;
		}

		while (className.indexOf("_") == 0) {
			className = className.substring(1);
		}

		int index = className.indexOf("_");

		while (index != -1) {
			String before = className.substring(0, index);
			if (className.length() == index + 1) {
				className = before;
				break;
			}

			String target = className.substring(index + 1, index + 2);

			String after = null;

			if (className.length() == index + 1) {
				after = "";

			} else {
				after = className.substring(index + 2);
			}

			className = before + target.toUpperCase() + after;

			index = className.indexOf("_");
		}

		return className;
	}

	private static String loadResource(String templateName) throws IOException {
		InputStream in = ExportToJavaManager.class.getClassLoader()
				.getResourceAsStream(TEMPLATE_DIR + templateName + ".txt");
		if (in == null) {
			throw new FileNotFoundException(TEMPLATE_DIR + templateName
					+ ".txt");
		}

		try {
			String content = IOUtils.toString(in);

			for (String keyword : KEYWORDS) {
				content = content.replaceAll(keyword, ResourceString
						.getResourceString(keyword));
			}

			return content;

		} finally {
			in.close();
		}
	}

	private String generateContent(ERDiagram diagram, ERTable table,
			String packageName) throws IOException {
		String content = TEMPLATE;
		content = content.replace("@implements", IMPLEMENTS);

		content = this.replacePropertiesInfo(content, table);
		content = this.replaceHashCodeEqualsInfo(content, table);
		content = this.replaceClassInfo(content, table);
		content = this.replaceExtendInfo(content);
		content = this.replaceImportInfo(content);
		content = this.replaceConstructorInfo(content);

		return content;
	}

	private String replacePropertiesInfo(String content, ERTable table)
			throws IOException {
		StringBuilder properties = new StringBuilder();
		StringBuilder setterGetters = new StringBuilder();

		for (NormalColumn normalColumn : table.getExpandedColumns()) {
			this.addContent(properties, PROPERTIES, normalColumn);
			this.addContent(setterGetters, SETTER_GETTER, normalColumn);
		}

		for (NodeElement referredElement : table.getReferredElementList()) {
			if (referredElement instanceof TableView) {
				TableView tableView = (TableView) referredElement;

				this.addContent(properties, SET_PROPERTIES, tableView);
				this.addContent(setterGetters, SETTER_GETTER_ADDER, tableView);

				this.sets.add(tableView.getPhysicalName());
			}
		}

		content = content.replaceAll("@properties\r\n", properties.toString());
		content = content.replaceAll("@setter_getter\r\n", setterGetters
				.toString());

		return content;
	}

	private String replaceHashCodeEqualsInfo(String content, ERTable table)
			throws IOException {
		if (table.getPrimaryKeySize() > 0) {
			StringBuilder hashCodes = new StringBuilder();
			StringBuilder equals = new StringBuilder();

			for (NormalColumn primaryKey : table.getPrimaryKeys()) {
				this.addContent(hashCodes, HASHCODE_LOGIC, primaryKey);
				this.addContent(equals, EQUALS_LOGIC, primaryKey);
			}

			String hashCodeEquals = HASHCODE_EQUALS;
			hashCodeEquals = hashCodeEquals.replaceAll("@hashCode logic\r\n",
					hashCodes.toString());
			hashCodeEquals = hashCodeEquals.replaceAll("@equals logic\r\n",
					equals.toString());

			content = content.replaceAll("@hashCode_equals\r\n", hashCodeEquals
					.toString());

		} else {
			content = content.replaceAll("@hashCode_equals\r\n", "");
		}

		return content;
	}

	private String replaceClassInfo(String content, ERTable table) {
		content = content.replaceAll("@package", packageName);
		content = content.replaceAll("@LogicalTableName", table
				.getLogicalName());
		content = content.replaceAll("@PhysicalTableName", this
				.getCamelCaseName(table));
		content = content.replaceAll("@suffix", Format
				.null2blank(this.classNameSuffix));
		content = content.replaceAll("@version", "@version \\$Id\\$");

		return content;
	}

	private String replaceExtendInfo(String content) throws IOException {
		if ("".equals(this.extendsClass)) {
			content = content.replaceAll("@import extends\r\n", "");
			content = content.replaceAll("@extends ", "");

		} else {
			this.importClasseNames.add(this.extendsClass);

			content = content.replaceAll("@extends", EXTENDS);

			int index = extendsClass.lastIndexOf(".");

			String extendsClassWithoutPackage = null;

			if (index == -1) {
				extendsClassWithoutPackage = extendsClass;

			} else {
				extendsClassWithoutPackage = extendsClass.substring(index + 1);
			}

			content = content.replaceAll("@extendsClassWithoutPackage",
					extendsClassWithoutPackage);
			content = content.replaceAll("@extendsClass", extendsClass);
		}

		return content;
	}

	private String replaceImportInfo(String content) {
		StringBuilder imports = new StringBuilder();
		for (String importClasseName : this.importClasseNames) {
			imports.append("import ");
			imports.append(importClasseName);
			imports.append(";\r\n");
		}

		content = content.replaceAll("@import\r\n", imports.toString());

		return content;
	}

	private String replaceConstructorInfo(String content) {
		StringBuilder constructor = new StringBuilder();
		for (String tableName : this.sets) {
			constructor.append("\t\tthis.");
			constructor.append(this.getCamelCaseName(tableName, false));
			constructor.append("Set = new HashSet<");
			constructor.append(this.getCamelCaseName(tableName, true)
					+ this.getCamelCaseName(this.classNameSuffix, true));
			constructor.append(">();\r\n");
		}

		content = content
				.replaceAll("@constructor\r\n", constructor.toString());

		return content;
	}

	private void addContent(StringBuilder contents, String template,
			NormalColumn normalColumn) {

		String value = null;

		if (normalColumn.isForeignKey()) {
			NormalColumn referencedColumn = normalColumn
					.getRootReferencedColumn();

			ERTable referencedTable = (ERTable) referencedColumn
					.getColumnHolder();
			String className = this.getClassName(referencedTable);

			value = template.replaceAll("@type", className);
			value = value.replaceAll("@logicalColumnName", referencedTable
					.getName());

			String physicalName = normalColumn.getPhysicalName().toLowerCase();
			physicalName = physicalName.replaceAll(referencedColumn
					.getPhysicalName().toLowerCase(), "");
			if (physicalName.indexOf(referencedTable.getPhysicalName()
					.toLowerCase()) == -1) {
				physicalName = physicalName + referencedTable.getPhysicalName();
			}

			value = value.replaceAll("@physicalColumnName", this
					.getCamelCaseName(physicalName, false));
			value = value.replaceAll("@PhysicalColumnName", this
					.getCamelCaseName(physicalName, true));

		} else {
			value = template.replaceAll("@type", this.getClassName(normalColumn
					.getType()));
			value = value.replaceAll("@logicalColumnName", normalColumn
					.getLogicalName());
			value = value.replaceAll("@physicalColumnName", this
					.getCamelCaseName(normalColumn.getPhysicalName(), false));
			value = value.replaceAll("@PhysicalColumnName", this
					.getCamelCaseName(normalColumn.getPhysicalName(), true));

		}

		contents.append(value);
		contents.append("\r\n");
	}

	private void addContent(StringBuilder contents, String template,
			TableView tableView) {

		String value = null;

		this.importClasseNames.add("java.util.Set");
		this.importClasseNames.add("java.util.HashSet");

		value = template.replaceAll("@setType", "Set<"
				+ this.getCamelCaseName(tableView.getPhysicalName(), true)
				+ this.getCamelCaseName(this.classNameSuffix, true) + ">");
		value = value.replaceAll("@type", this.getCamelCaseName(tableView
				.getPhysicalName(), true)
				+ this.getCamelCaseName(this.classNameSuffix, true));
		value = value.replaceAll("@logicalColumnName", tableView.getName());

		value = value.replaceAll("@physicalColumnName", this.getCamelCaseName(
				tableView.getPhysicalName(), false));
		value = value.replaceAll("@PhysicalColumnName", this.getCamelCaseName(
				tableView.getPhysicalName(), true));

		contents.append(value);
		contents.append("\r\n");
	}

	private String getClassName(SqlType type) {
		Class clazz = type.getJavaClass();

		String name = clazz.getCanonicalName();
		if (!name.startsWith("java.lang")) {
			this.importClasseNames.add(name);
		}

		return clazz.getSimpleName();
	}

	private void writeOut(String dstPath, String content) throws IOException {
		dstPath = this.outputDir + dstPath;
		File file = new File(dstPath);
		file.getParentFile().mkdirs();

		FileUtils.writeStringToFile(file, content, this.fileEncoding);
	}

}
