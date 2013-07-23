package org.insightech.er.editor.model.dbexport.html;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.ObjectModel;
import org.insightech.er.editor.model.dbexport.html.page_generator.HtmlReportPageGenerator;
import org.insightech.er.editor.model.dbexport.html.page_generator.OverviewHtmlReportPageGenerator;
import org.insightech.er.editor.model.dbexport.html.page_generator.impl.CategoryHtmlReportPageGenerator;
import org.insightech.er.editor.model.dbexport.html.page_generator.impl.GroupHtmlReportPageGenerator;
import org.insightech.er.editor.model.dbexport.html.page_generator.impl.IndexHtmlReportPageGenerator;
import org.insightech.er.editor.model.dbexport.html.page_generator.impl.SequenceHtmlReportPageGenerator;
import org.insightech.er.editor.model.dbexport.html.page_generator.impl.TableHtmlReportPageGenerator;
import org.insightech.er.editor.model.dbexport.html.page_generator.impl.TablespaceHtmlReportPageGenerator;
import org.insightech.er.editor.model.dbexport.html.page_generator.impl.TriggerHtmlReportPageGenerator;
import org.insightech.er.editor.model.dbexport.html.page_generator.impl.ViewHtmlReportPageGenerator;
import org.insightech.er.editor.model.dbexport.html.page_generator.impl.WordHtmlReportPageGenerator;
import org.insightech.er.editor.model.diagram_contents.element.node.Location;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.util.Closer;
import org.insightech.er.util.io.FileUtils;
import org.insightech.er.util.io.IOUtils;

public class ExportToHtmlManager<T extends ObjectModel> {

	private static final Map<String, String> PROPERTIES = ResourceString
			.getResources("html.report.");

	private static final String[] FIX_FILES = { "help-doc.html", "index.html",
			"stylesheet.css" };

	public static final String[] ICON_FILES = { "icons/pkey.png",
			"icons/foreign_key.png" };

	private static final String TEMPLATE_DIR = "html/";

	protected List<HtmlReportPageGenerator<T>> htmlReportPageGeneratorList = new ArrayList<HtmlReportPageGenerator<T>>();

	protected OverviewHtmlReportPageGenerator<T> overviewPageGenerator;

	private String outputDir;

	protected ERDiagram diagram;

	private Map<TableView, Location> tableLocationMap;

	@SuppressWarnings("unchecked")
	public ExportToHtmlManager(String outputDir, ERDiagram diagram,
			Map<TableView, Location> tableLocationMap) {
		this.outputDir = outputDir;
		this.diagram = diagram;
		this.tableLocationMap = tableLocationMap;

		Map<Object, Integer> idMap = new HashMap<Object, Integer>();

		this.overviewPageGenerator = new OverviewHtmlReportPageGenerator<T>(idMap);
		htmlReportPageGeneratorList
				.add((HtmlReportPageGenerator<T>) new TableHtmlReportPageGenerator(idMap));
		htmlReportPageGeneratorList
				.add((HtmlReportPageGenerator<T>) new IndexHtmlReportPageGenerator(idMap));
		htmlReportPageGeneratorList.add((HtmlReportPageGenerator<T>) new SequenceHtmlReportPageGenerator(
				idMap));
		htmlReportPageGeneratorList.add((HtmlReportPageGenerator<T>) new ViewHtmlReportPageGenerator(idMap));
		htmlReportPageGeneratorList.add((HtmlReportPageGenerator<T>) new TriggerHtmlReportPageGenerator(
				idMap));
		htmlReportPageGeneratorList
				.add((HtmlReportPageGenerator<T>) new GroupHtmlReportPageGenerator(idMap));
		htmlReportPageGeneratorList.add((HtmlReportPageGenerator<T>) new TablespaceHtmlReportPageGenerator(
				idMap));
		htmlReportPageGeneratorList.add((HtmlReportPageGenerator<T>) new WordHtmlReportPageGenerator(idMap));
		htmlReportPageGeneratorList.add((HtmlReportPageGenerator<T>) new CategoryHtmlReportPageGenerator(
				idMap));
	}

	protected void doPreTask(HtmlReportPageGenerator<T> pageGenerator,
			T object) {
	}

	protected void doPostTask() throws InterruptedException {
	}

	public void doProcess() throws IOException, InterruptedException {
		// 固定ファイルのコピー
		for (final String file : FIX_FILES) {
			this.copyOut(file, file);
		}

		// テンプレートから生成
		String template = null;

		// イメージ
		String imageSrc = "image/er.png";

		// アイコン
		for (String iconFile : ICON_FILES) {
			this.copyOutResource("image/" + iconFile, iconFile);
		}

		// トップ階層
		String allclasses = overviewPageGenerator.generateAllClasses(diagram,
				htmlReportPageGeneratorList);
		this.writeOut("allclasses.html", allclasses);

		String overviewFrame = overviewPageGenerator
				.generateFrame(htmlReportPageGeneratorList);
		this.writeOut("overview-frame.html", overviewFrame);

		String overviewSummary = overviewPageGenerator.generateSummary(
				imageSrc, tableLocationMap, htmlReportPageGeneratorList);
		this.writeOut("overview-summary.html", overviewSummary);

		// オブジェクトタイプ毎の階層
		for (int i = 0, n = htmlReportPageGeneratorList.size(); i < n; i++) {
			HtmlReportPageGenerator<T> pageGenerator = htmlReportPageGeneratorList
					.get(i);
			try {
				HtmlReportPageGenerator<T> prevPageGenerator = null;
				if (i != 0) {
					prevPageGenerator = htmlReportPageGeneratorList.get(i - 1);
				}
				HtmlReportPageGenerator<?> nextPageGenerator = null;
				if (i != htmlReportPageGeneratorList.size() - 1) {
					nextPageGenerator = htmlReportPageGeneratorList.get(i + 1);
				}

				String type = pageGenerator.getType();

				template = pageGenerator.generatePackageFrame(diagram);
				this.writeOut(type + "/package-frame.html", template);

				template = pageGenerator.generatePackageSummary(
						prevPageGenerator, nextPageGenerator, diagram);
				this.writeOut(type + "/package-summary.html", template);

				List<T> objectList = pageGenerator.getObjectList(diagram);
				for (int j = 0, m = objectList.size(); j < m; j++) {
					T object = objectList.get(j);

					this.doPreTask(pageGenerator, object);

					Object prevObject = null;
					if (j != 0) {
						prevObject = (Object) objectList.get(j - 1);
					}
					Object nextObject = null;
					if (j != objectList.size() - 1) {
						nextObject = (Object) objectList.get(j + 1);
					}

					template = pageGenerator.generateContent(diagram, object,
							prevObject, nextObject);

					String objectId = pageGenerator.getObjectId(object);
					this.writeOut(type + "/" + objectId + ".html", template);

					this.doPostTask();
				}

			} catch (RuntimeException e) {
				throw new IllegalStateException(pageGenerator.getClass()
						.getName(), e);
			}
		}
	}

	public static String getTemplate(String key) throws IOException {
		InputStream in = ExportToHtmlManager.class.getClassLoader()
				.getResourceAsStream(TEMPLATE_DIR + key);
		if (in == null) {
			throw new FileNotFoundException(TEMPLATE_DIR + key);
		}

		try {
			String content = IOUtils.toString(in);
			content = replaceProperties(content);

			return content;

		} finally {
			Closer.close(in);
		}
	}

	private void writeOut(String dstPath, String content) throws IOException {
		dstPath = this.outputDir + dstPath;
		File file = new File(dstPath);
		file.getParentFile().mkdirs();

		FileUtils.writeStringToFile(file, content, "UTF-8");
	}

	private void copyOut(String dstPath, String key)
			throws FileNotFoundException, IOException {
		String content = getTemplate(key);
		this.writeOut(dstPath, content);
	}

	private static String replaceProperties(String content) {
		for (final Entry<String, String> entry : PROPERTIES. entrySet()) {
			content = content.replaceAll(entry.getKey(), entry.getValue());
		}

		return content;
	}

	private void copyOutResource(String dstPath, String srcPath)
			throws FileNotFoundException, IOException {
		InputStream in = null;

		try {
			in = ExportToHtmlManager.class.getClassLoader()
					.getResourceAsStream(srcPath);
			copyOutResource(dstPath, in);

		} finally {
			Closer.close(in);
		}
	}

	private void copyOutResource(String dstPath, InputStream in)
			throws FileNotFoundException, IOException {
		FileOutputStream out = null;

		try {
			dstPath = this.outputDir + dstPath;
			File file = new File(dstPath);
			file.getParentFile().mkdirs();

			out = new FileOutputStream(file);

			IOUtils.copy(in, out);

		} finally {
			Closer.close(out);
		}
	}
}
