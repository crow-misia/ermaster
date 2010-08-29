package org.insightech.er.editor.model.dbexport.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.TreeSet;

import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.util.Format;
import org.insightech.er.util.io.FileUtils;
import org.insightech.er.util.io.IOUtils;

public class ExportToJavaManager {

	private static final String TEMPLATE_DIR = "java/";

	private String outputDir;

	private String fileEncoding;

	private String packageName;

	private String packageDir;

	private String classNameSuffix;

	private String extendsClass;

	protected ERDiagram diagram;

	private Set<String> importClasseNames;

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
	}

	protected void doPreTask(ERTable table) {
	}

	protected void doPostTask() throws InterruptedException {
	}

	public void doProcess() throws IOException, InterruptedException {
		// テンプレートから生成
		String template = null;

		for (ERTable table : diagram.getDiagramContents().getContents()
				.getTableSet().getList()) {
			this.doPreTask(table);

			this.importClasseNames.clear();
			this.importClasseNames.add("java.io.Serializable");
			
			template = this.generateContent(diagram, table, packageName,
					"template");

			String className = this.getClassName(table)
					+ this.getClassName(this.classNameSuffix, true);

			this.writeOut("/" + this.packageDir + "/" + className + ".java",
					template);

			this.doPostTask();
		}
	}

	protected String getClassName(ERTable table) {
		return this.getClassName(table.getPhysicalName(), true);
	}

	protected String getClassName(String name, boolean capital) {
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
			if (className.length() == index) {
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

	private String loadResource(String templateName) throws IOException {
		InputStream in = ExportToJavaManager.class.getClassLoader()
				.getResourceAsStream(TEMPLATE_DIR + templateName + ".txt");
		if (in == null) {
			throw new FileNotFoundException(TEMPLATE_DIR + templateName
					+ ".txt");
		}

		try {
			String content = IOUtils.toString(in);
			return content;

		} finally {
			in.close();
		}
	}

	private String generateContent(ERDiagram diagram, ERTable table,
			String packageName, String templateName) throws IOException {
		InputStream in = ExportToJavaManager.class.getClassLoader()
				.getResourceAsStream(TEMPLATE_DIR + templateName + ".txt");
		if (in == null) {
			throw new FileNotFoundException(TEMPLATE_DIR + templateName
					+ ".txt");
		}

		try {
			String content = IOUtils.toString(in);

			String propertyTemplate = this.loadResource("@properties");
			String setterGetterTemplate = this.loadResource("@setter_getter");

			StringBuilder properties = new StringBuilder();
			StringBuilder setterGetters = new StringBuilder();

			for (NormalColumn normalColumn : table.getExpandedColumns()) {
				this.addContent(properties, propertyTemplate, normalColumn);
				this.addContent(setterGetters, setterGetterTemplate,
						normalColumn);
			}

			content = content.replaceAll("@properties\r\n", properties.toString());
			content = content.replaceAll("@setter_getter\r\n", setterGetters
					.toString());

			if (table.getPrimaryKeySize() > 0) {
				String hashCodeEquals = this.loadResource("@hashCode_equals");

				String hashCodeTemplate = this.loadResource("@hashCode logic");
				String equalsTemplate = this.loadResource("@equals logic");

				StringBuilder hashCodes = new StringBuilder();
				StringBuilder equals = new StringBuilder();

				for (NormalColumn primaryKey : table.getPrimaryKeys()) {
					this.addContent(hashCodes, hashCodeTemplate, primaryKey);
					this.addContent(equals, equalsTemplate, primaryKey);
				}

				hashCodeEquals = hashCodeEquals.replaceAll(
						"@hashCode logic\r\n", hashCodes.toString());
				hashCodeEquals = hashCodeEquals.replaceAll("@equals logic\r\n",
						equals.toString());

				content = content.replaceAll("@hashCode_equals\r\n",
						hashCodeEquals.toString());

			} else {
				content = content.replaceAll("@hashCode_equals\r\n", "");
			}

			content = content.replaceAll("@package", packageName);
			content = content.replaceAll("@LogicalTableName", table
					.getLogicalName());
			content = content.replaceAll("@PhysicalTableName", this
					.getClassName(table));
			content = content.replaceAll("@suffix", Format
					.null2blank(this.classNameSuffix));
			content = content.replaceAll("@version", "@version \\$Id\\$");
			

			String interfaceTemplate = this.loadResource("@implements");
			content = content.replaceAll("@implements", interfaceTemplate);

			if ("".equals(this.extendsClass)) {
				content = content.replaceAll("@import extends\r\n", "");
				content = content.replaceAll("@extends ", "");

			} else {
				this.importClasseNames.add(this.extendsClass);

				String importExtendsTemplate = this
						.loadResource("@import extends");
				content = content.replaceAll("@import extends",
						importExtendsTemplate);

				String extendsTemplate = this.loadResource("@extends");
				content = content.replaceAll("@extends", extendsTemplate);

				int index = extendsClass.lastIndexOf(".");
				String extendsClassWithoutPackage = null;

				if (index == -1) {
					extendsClassWithoutPackage = extendsClass;

				} else {
					extendsClassWithoutPackage = extendsClass
							.substring(index + 1);
				}

				content = content.replaceAll("@extendsClassWithoutPackage",
						extendsClassWithoutPackage);
				content = content.replaceAll("@extendsClass", extendsClass);
			}

			StringBuilder imports = new StringBuilder();
			for (String importClasseName : this.importClasseNames) {
				imports.append("import ");
				imports.append(importClasseName);
				imports.append(";\r\n");
			}

			content = content.replaceAll("@import\r\n", imports.toString());

			return content;

		} finally {
			in.close();
		}
	}

	private void addContent(StringBuilder contents, String template,
			NormalColumn normalColumn) {
		String value = template.replaceAll("@type", this
				.getClassName(normalColumn.getType()));
		value = value.replaceAll("@logicalColumnName", normalColumn
				.getLogicalName());
		value = value.replaceAll("@physicalColumnName", this.getClassName(
				normalColumn.getPhysicalName(), false));
		value = value.replaceAll("@PhysicalColumnName", this.getClassName(
				normalColumn.getPhysicalName(), true));

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
