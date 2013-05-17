package org.insightech.er.editor.persistent.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.insightech.er.db.impl.db2.tablespace.DB2TablespaceProperties;
import org.insightech.er.db.impl.mysql.MySQLTableProperties;
import org.insightech.er.db.impl.mysql.tablespace.MySQLTablespaceProperties;
import org.insightech.er.db.impl.oracle.OracleTableProperties;
import org.insightech.er.db.impl.oracle.tablespace.OracleTablespaceProperties;
import org.insightech.er.db.impl.postgres.PostgresTableProperties;
import org.insightech.er.db.impl.postgres.tablespace.PostgresTablespaceProperties;
import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ddl.DDLTarget;
import org.insightech.er.editor.model.diagram_contents.DiagramContents;
import org.insightech.er.editor.model.diagram_contents.element.connection.Bendpoint;
import org.insightech.er.editor.model.diagram_contents.element.connection.CommentConnection;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeSet;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.image.InsertedImage;
import org.insightech.er.editor.model.diagram_contents.element.node.model_properties.ModelProperties;
import org.insightech.er.editor.model.diagram_contents.element.node.note.Note;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.insightech.er.editor.model.diagram_contents.element.node.table.unique_key.ComplexUniqueKey;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.model.diagram_contents.element.node.view.properties.ViewProperties;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GroupSet;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.SequenceSet;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceSet;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.TriggerSet;
import org.insightech.er.editor.model.settings.CategorySetting;
import org.insightech.er.editor.model.settings.DBSetting;
import org.insightech.er.editor.model.settings.Environment;
import org.insightech.er.editor.model.settings.EnvironmentSetting;
import org.insightech.er.editor.model.settings.ExportSetting;
import org.insightech.er.editor.model.settings.PageSetting;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.model.settings.TranslationSetting;
import org.insightech.er.editor.model.settings.export.ExportJavaSetting;
import org.insightech.er.editor.model.settings.export.ExportTestDataSetting;
import org.insightech.er.editor.model.testdata.DirectTestData;
import org.insightech.er.editor.model.testdata.RepeatTestData;
import org.insightech.er.editor.model.testdata.RepeatTestDataDef;
import org.insightech.er.editor.model.testdata.TableTestData;
import org.insightech.er.editor.model.testdata.TestData;
import org.insightech.er.editor.model.tracking.ChangeTracking;
import org.insightech.er.editor.model.tracking.ChangeTrackingList;
import org.insightech.er.editor.persistent.Persistent;
import org.insightech.er.util.Format;
import org.insightech.er.util.NameValue;

public class PersistentXmlImpl extends Persistent {
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	private final class PersistentContext {
		private Map<ColumnGroup, Integer> columnGroupMap = new HashMap<ColumnGroup, Integer>();

		private Map<ConnectionElement, Integer> connectionMap = new HashMap<ConnectionElement, Integer>();

		private Map<Column, Integer> columnMap = new HashMap<Column, Integer>();

		private Map<ComplexUniqueKey, Integer> complexUniqueKeyMap = new HashMap<ComplexUniqueKey, Integer>();

		private Map<NodeElement, Integer> nodeElementMap = new HashMap<NodeElement, Integer>();

		private Map<Word, Integer> wordMap = new HashMap<Word, Integer>();

		private Map<Tablespace, Integer> tablespaceMap = new HashMap<Tablespace, Integer>();

		private Map<Environment, Integer> environmentMap = new HashMap<Environment, Integer>();
	}

	private PersistentContext getContext(DiagramContents diagramContents) {
		PersistentContext context = new PersistentContext();

		int columnGroupCount = 0;
		int columnCount = 0;
		for (ColumnGroup columnGroup : diagramContents.getGroups()) {
			context.columnGroupMap.put(columnGroup, Integer.valueOf(
					columnGroupCount));
			columnGroupCount++;

			for (NormalColumn normalColumn : columnGroup.getColumns()) {
				context.columnMap.put(normalColumn, Integer.valueOf(columnCount));
				columnCount++;
			}
		}

		int nodeElementCount = 0;
		int connectionCount = 0;
		int complexUniqueKeyCount = 0;

		for (NodeElement content : diagramContents.getContents()) {
			context.nodeElementMap.put(content, Integer.valueOf(nodeElementCount));
			nodeElementCount++;

			List<ConnectionElement> connections = content.getIncomings();

			for (ConnectionElement connection : connections) {
				context.connectionMap.put(connection, Integer.valueOf(
						connectionCount));
				connectionCount++;
			}

			if (content instanceof ERTable) {

				ERTable table = (ERTable) content;

				List<Column> columns = table.getColumns();

				for (Column column : columns) {
					if (column instanceof NormalColumn) {
						context.columnMap.put(column, Integer.valueOf(columnCount));

						columnCount++;
					}
				}

				for (ComplexUniqueKey complexUniqueKey : table
						.getComplexUniqueKeyList()) {
					context.complexUniqueKeyMap.put(complexUniqueKey,
							Integer.valueOf(complexUniqueKeyCount));

					complexUniqueKeyCount++;
				}

			}
		}

		int wordCount = 0;
		for (Word word : diagramContents.getDictionary().getWordList()) {
			context.wordMap.put(word, Integer.valueOf(wordCount));
			wordCount++;
		}

		int tablespaceCount = 0;
		for (Tablespace tablespace : diagramContents.getTablespaceSet()) {
			context.tablespaceMap.put(tablespace, Integer.valueOf(tablespaceCount));
			tablespaceCount++;
		}

		int environmentCount = 0;
		for (Environment environment : diagramContents.getSettings()
				.getEnvironmentSetting().getEnvironments()) {
			context.environmentMap.put(environment, Integer.valueOf(
					environmentCount));
			environmentCount++;
		}

		return context;
	}

	private PersistentContext getCurrentContext(ERDiagram diagram) {
		return this.getContext(diagram.getDiagramContents());
	}

	private PersistentContext getChangeTrackingContext(
			ChangeTracking changeTracking) {
		return this.getContext(changeTracking.getDiagramContents());
	}

	@Override
	public ERDiagram load(InputStream in) throws Exception {
		XMLLoader loader = new XMLLoader();
		return loader.load(in);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InputStream createInputStream(ERDiagram diagram) throws IOException {
		final String xml = this.createXML(diagram);

		return new ByteArrayInputStream(xml.getBytes("UTF-8"));
	}

	private String createXML(final ERDiagram diagram) {
		final StringBuilder xml = new StringBuilder();

		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		xml.append("<diagram>\n");

		if (diagram.getDbSetting() != null) {
			xml.append("\t<dbsetting>\n");
			createXML(xml, "\t\t", diagram.getDbSetting());
			xml.append("\t</dbsetting>\n");
		}
		if (diagram.getPageSetting() != null) {
			xml.append("\t<page_setting>\n");
			createXML(xml, "\t\t", diagram.getPageSetting());
			xml.append("\t</page_setting>\n");
		}

		xml.append("\t<category_index>")
				.append(diagram.getCurrentCategoryIndex())
				.append("</category_index>\n");
		xml.append("\t<zoom>").append(diagram.getZoom()).append("</zoom>\n");
		xml.append("\t<x>").append(diagram.getX()).append("</x>\n");
		xml.append("\t<y>").append(diagram.getY()).append("</y>\n");

		int[] defaultColor = diagram.getDefaultColor();
		xml.append("\t<default_color>\n");
		xml.append("\t\t<r>").append(defaultColor[0]).append("</r>\n");
		xml.append("\t\t<g>").append(defaultColor[1]).append("</g>\n");
		xml.append("\t\t<b>").append(defaultColor[2]).append("</b>\n");
		xml.append("\t</default_color>\n");
		createXMLColor(xml, "\t", diagram.getColor());
		xml.append("\t<font_name>").append(escape(diagram.getFontName()))
				.append("</font_name>\n");
		xml.append("\t<font_size>").append(diagram.getFontSize())
				.append("</font_size>\n");

		PersistentContext context = this.getCurrentContext(diagram);

		createXML(xml, "\t", diagram.getDiagramContents(), context);
		createXML(xml, "\t", diagram.getChangeTrackingList());

		xml.append("</diagram>\n");

		return xml.toString();
	}

 	private void createXML(final StringBuilder xml, final String tab, final DBSetting dbSetting) {
		xml.append(tab).append("<dbsystem>").append(escape(dbSetting.getDbsystem()))
				.append("</dbsystem>\n");
		xml.append(tab).append("<server>").append(escape(dbSetting.getServer()))
				.append("</server>\n");
		xml.append(tab).append("<port>").append(dbSetting.getPort()).append("</port>\n");
		xml.append(tab).append("<database>").append(escape(dbSetting.getDatabase()))
				.append("</database>\n");
		xml.append(tab).append("<user>").append(escape(dbSetting.getUser()))
				.append("</user>\n");
		xml.append(tab).append("<password>").append(escape(dbSetting.getPassword()))
				.append("</password>\n");
		xml.append(tab).append("<use_default_driver>")
				.append(dbSetting.isUseDefaultDriver())
				.append("</use_default_driver>\n");
		xml.append(tab).append("<url>").append(escape(dbSetting.getUrl()))
				.append("</url>\n");
		xml.append(tab).append("<driver_class_name>")
				.append(escape(dbSetting.getDriverClassName()))
				.append("</driver_class_name>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final PageSetting pageSetting) {
		xml.append(tab).append("<direction_horizontal>")
				.append(pageSetting.isDirectionHorizontal())
				.append("</direction_horizontal>\n");
		xml.append(tab).append("<scale>").append(pageSetting.getScale())
				.append("</scale>\n");
		xml.append(tab).append("<paper_size>").append(escape(pageSetting.getPaperSize()))
				.append("</paper_size>\n");
		xml.append(tab).append("<top_margin>").append(pageSetting.getTopMargin())
				.append("</top_margin>\n");
		xml.append(tab).append("<left_margin>").append(pageSetting.getLeftMargin())
				.append("</left_margin>\n");
		xml.append(tab).append("<bottom_margin>").append(pageSetting.getBottomMargin())
				.append("</bottom_margin>\n");
		xml.append(tab).append("<right_margin>").append(pageSetting.getRightMargin())
				.append("</right_margin>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final DiagramContents diagramContents,
			PersistentContext context) {
		createXML(xml, tab, diagramContents.getSettings(), context);
		createXML(xml, tab, diagramContents.getDictionary(), context);
		createXML(xml, tab, diagramContents.getTablespaceSet(), context);
		createXML(xml, tab, diagramContents.getContents(), context);
		createXML(xml, tab, diagramContents.getGroups(), context);
		createXML(xml, tab, diagramContents.getTestDataList(), context);

		createXML(xml, tab, diagramContents.getSequenceSet());
		createXML(xml, tab, diagramContents.getTriggerSet());
	}

 	private void createXML(final StringBuilder xml, final String tab, final GroupSet columnGroups, final PersistentContext context) {
		xml.append(tab).append("<column_groups>\n");

		final String tab2 = tab + "\t";
		for (ColumnGroup columnGroup : columnGroups) {
			createXML(xml, tab2, columnGroup, context);
		}

		xml.append(tab).append("</column_groups>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final List<TestData> testDataList,
			final PersistentContext context) {
		xml.append(tab).append("<test_data_list>\n");

		final String tab2 = tab + "\t";
		for (TestData testData : testDataList) {
			createXML(xml, tab2, testData, context);
		}

		xml.append(tab).append("</test_data_list>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final TriggerSet triggerSet) {
		xml.append(tab).append("<trigger_set>\n");

		final String tab2 = tab + "\t";
		for (Trigger trigger : triggerSet) {
			createXML(xml, tab2, trigger);
		}

		xml.append(tab).append("</trigger_set>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final Trigger trigger) {
		xml.append(tab).append("<trigger>\n");

		xml.append(tab).append("\t<name>").append(escape(trigger.getName()))
				.append("</name>\n");
		xml.append(tab).append("\t<schema>").append(escape(trigger.getSchema()))
				.append("</schema>\n");
		xml.append(tab).append("\t<sql>").append(escape(trigger.getSql()))
				.append("</sql>\n");
		xml.append(tab).append("\t<description>").append(escape(trigger.getDescription()))
				.append("</description>\n");

		xml.append(tab).append("</trigger>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final SequenceSet sequenceSet) {
		xml.append(tab).append("<sequence_set>\n");

		final String tab2 = tab + "\t";
		for (Sequence sequence : sequenceSet) {
			createXML(xml, tab2, sequence);
		}

		xml.append(tab).append("</sequence_set>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final Sequence sequence) {
		xml.append(tab).append("<sequence>\n");

		xml.append(tab).append("\t<name>").append(escape(sequence.getName()))
				.append("</name>\n");
		xml.append(tab).append("\t<schema>").append(escape(sequence.getSchema()))
				.append("</schema>\n");
		xml.append(tab).append("\t<increment>")
				.append(Format.toString(sequence.getIncrement()))
				.append("</increment>\n");
		xml.append(tab).append("\t<min_value>")
				.append(Format.toString(sequence.getMinValue()))
				.append("</min_value>\n");
		xml.append(tab).append("\t<max_value>")
				.append(Format.toString(sequence.getMaxValue()))
				.append("</max_value>\n");
		xml.append(tab).append("\t<start>").append(Format.toString(sequence.getStart()))
				.append("</start>\n");
		xml.append(tab).append("\t<cache>").append(Format.toString(sequence.getCache()))
				.append("</cache>\n");
		xml.append(tab).append("\t<cycle>").append(sequence.isCycle()).append("</cycle>\n");
		xml.append(tab).append("\t<order>").append(sequence.isOrder()).append("</order>\n");
		xml.append(tab).append("\t<description>").append(escape(sequence.getDescription()))
				.append("</description>\n");
		xml.append(tab).append("\t<data_type>").append(escape(sequence.getDataType()))
				.append("</data_type>\n");
		xml.append(tab).append("\t<decimal_size>")
				.append(Format.toString(sequence.getDecimalSize()))
				.append("</decimal_size>\n");

		xml.append(tab).append("</sequence>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final TablespaceSet tablespaceSet,
			final PersistentContext context) {
		xml.append(tab).append("<tablespace_set>\n");

		final String tab2 = tab + "\t";
		for (Tablespace tablespace : tablespaceSet) {
			createXML(xml, tab2, tablespace, context);
		}

		xml.append(tab).append("</tablespace_set>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final Tablespace tablespace, final PersistentContext context) {
		xml.append(tab).append("<tablespace>\n");

		final String tab2 = tab + "\t";
		if (context != null) {
			xml.append(tab2).append("<id>").append(context.tablespaceMap.get(tablespace))
					.append("</id>\n");
		}
		xml.append(tab2).append("<name>").append(escape(tablespace.getName()))
				.append("</name>\n");

		final String tab3 = tab + "\t\t";
		for (Map.Entry<Environment, TablespaceProperties> entry : tablespace
				.getPropertiesMap().entrySet()) {
			Environment environment = entry.getKey();
			TablespaceProperties tablespaceProperties = entry.getValue();

			xml.append(tab2).append("<properties>\n");

			xml.append(tab3).append("<environment_id>")
					.append(context.environmentMap.get(environment))
					.append("</environment_id>\n");

			if (tablespaceProperties instanceof DB2TablespaceProperties) {
				createXML(xml, tab3, (DB2TablespaceProperties) tablespaceProperties);

			} else if (tablespaceProperties instanceof MySQLTablespaceProperties) {
				createXML(xml, tab3, (MySQLTablespaceProperties) tablespaceProperties);

			} else if (tablespaceProperties instanceof OracleTablespaceProperties) {
				createXML(xml, tab3, (OracleTablespaceProperties) tablespaceProperties);

			} else if (tablespaceProperties instanceof PostgresTablespaceProperties) {
				createXML(xml, tab3, (PostgresTablespaceProperties) tablespaceProperties);

			}

			xml.append(tab2).append("</properties>\n");
		}

		xml.append(tab).append("</tablespace>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final DB2TablespaceProperties tablespace) {
		xml.append(tab).append("<buffer_pool_name>")
				.append(escape(tablespace.getBufferPoolName()))
				.append("</buffer_pool_name>\n");
		xml.append(tab).append("<container>").append(escape(tablespace.getContainer()))
				.append("</container>\n");
		// xml.append(tab).append("<container_device_path>").append(
		// escape(tablespace.getContainerDevicePath())).append(
		// "</container_device_path>\n");
		// xml.append(tab).append("<container_directory_path>").append(
		// escape(tablespace.getContainerDirectoryPath())).append(
		// "</container_directory_path>\n");
		// xml.append(tab).append("<container_file_path>").append(
		// escape(tablespace.getContainerFilePath())).append(
		// "</container_file_path>\n");
		// xml.append(tab).append("<container_page_num>").append(
		// escape(tablespace.getContainerPageNum())).append(
		// "</container_page_num>\n");
		xml.append(tab).append("<extent_size>").append(escape(tablespace.getExtentSize()))
				.append("</extent_size>\n");
		xml.append(tab).append("<managed_by>").append(escape(tablespace.getManagedBy()))
				.append("</managed_by>\n");
		xml.append(tab).append("<page_size>").append(escape(tablespace.getPageSize()))
				.append("</page_size>\n");
		xml.append(tab).append("<prefetch_size>")
				.append(escape(tablespace.getPrefetchSize()))
				.append("</prefetch_size>\n");
		xml.append(tab).append("<type>").append(escape(tablespace.getType()))
				.append("</type>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final MySQLTablespaceProperties tablespace) {
		xml.append(tab).append("<data_file>").append(escape(tablespace.getDataFile()))
				.append("</data_file>\n");
		xml.append(tab).append("<engine>").append(escape(tablespace.getEngine()))
				.append("</engine>\n");
		xml.append(tab).append("<extent_size>").append(escape(tablespace.getExtentSize()))
				.append("</extent_size>\n");
		xml.append(tab).append("<initial_size>")
				.append(escape(tablespace.getInitialSize()))
				.append("</initial_size>\n");
		xml.append(tab).append("<log_file_group>")
				.append(escape(tablespace.getLogFileGroup()))
				.append("</log_file_group>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final OracleTablespaceProperties tablespace) {
		xml.append(tab).append("<auto_extend>").append(tablespace.isAutoExtend())
				.append("</auto_extend>\n");
		xml.append(tab).append("<auto_segment_space_management>")
				.append(tablespace.isAutoSegmentSpaceManagement())
				.append("</auto_segment_space_management>\n");
		xml.append(tab).append("<logging>").append(tablespace.isLogging())
				.append("</logging>\n");
		xml.append(tab).append("<offline>").append(tablespace.isOffline())
				.append("</offline>\n");
		xml.append(tab).append("<temporary>").append(tablespace.isTemporary())
				.append("</temporary>\n");
		xml.append(tab).append("<auto_extend_max_size>")
				.append(escape(tablespace.getAutoExtendMaxSize()))
				.append("</auto_extend_max_size>\n");
		xml.append(tab).append("<auto_extend_size>")
				.append(escape(tablespace.getAutoExtendSize()))
				.append("</auto_extend_size>\n");
		xml.append(tab).append("<data_file>").append(escape(tablespace.getDataFile()))
				.append("</data_file>\n");
		xml.append(tab).append("<file_size>").append(escape(tablespace.getFileSize()))
				.append("</file_size>\n");
		xml.append(tab).append("<initial>").append(escape(tablespace.getInitial()))
				.append("</initial>\n");
		xml.append(tab).append("<max_extents>").append(escape(tablespace.getMaxExtents()))
				.append("</max_extents>\n");
		xml.append(tab).append("<min_extents>").append(escape(tablespace.getMinExtents()))
				.append("</min_extents>\n");
		xml.append(tab).append("<minimum_extent_size>")
				.append(escape(tablespace.getMinimumExtentSize()))
				.append("</minimum_extent_size>\n");
		xml.append(tab).append("<next>").append(escape(tablespace.getNext()))
				.append("</next>\n");
		xml.append(tab).append("<pct_increase>")
				.append(escape(tablespace.getPctIncrease()))
				.append("</pct_increase>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final PostgresTablespaceProperties tablespace) {
		xml.append(tab).append("<location>").append(escape(tablespace.getLocation()))
				.append("</location>\n");
		xml.append(tab).append("<owner>").append(escape(tablespace.getOwner()))
				.append("</owner>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final Settings settings, final PersistentContext context) {
		xml.append(tab).append("<settings>\n");

		final String tab2 = tab + "\t";
		xml.append(tab2).append("<database>").append(escape(settings.getDatabase()))
				.append("</database>\n");
		xml.append(tab2).append("<capital>").append(settings.isCapital())
				.append("</capital>\n");
		xml.append(tab2).append("<table_style>").append(escape(settings.getTableStyle()))
				.append("</table_style>\n");
		xml.append(tab2).append("<notation>").append(escape(settings.getNotation()))
				.append("</notation>\n");
		xml.append(tab2).append("<notation_level>").append(settings.getNotationLevel())
				.append("</notation_level>\n");
		xml.append(tab2).append("<notation_expand_group>")
				.append(settings.isNotationExpandGroup())
				.append("</notation_expand_group>\n");
		xml.append(tab2).append("<view_mode>").append(settings.getViewMode())
				.append("</view_mode>\n");
		xml.append(tab2).append("<outline_view_mode>")
				.append(settings.getOutlineViewMode())
				.append("</outline_view_mode>\n");
		xml.append(tab2).append("<view_order_by>").append(settings.getViewOrderBy())
				.append("</view_order_by>\n");

		xml.append(tab2).append("<auto_ime_change>").append(settings.isAutoImeChange())
				.append("</auto_ime_change>\n");
		xml.append(tab2).append("<validate_physical_name>")
				.append(settings.isValidatePhysicalName())
				.append("</validate_physical_name>\n");
		xml.append(tab2).append("<use_bezier_curve>").append(settings.isUseBezierCurve())
				.append("</use_bezier_curve>\n");
		xml.append(tab2).append("<suspend_validator>")
				.append(settings.isSuspendValidator())
				.append("</suspend_validator>\n");

		createXML(xml, tab2, settings.getExportSetting(), context);
		createXML(xml, tab2, settings.getCategorySetting(), context);
		createXML(xml, tab2, settings.getTranslationSetting(), context);
		createXML(xml, tab2, settings.getModelProperties(), context);
		createXML(xml, tab2, (TableProperties) settings.getTableViewProperties(), context);
		createXML(xml, tab2, settings.getEnvironmentSetting(), context);

		xml.append(tab).append("</settings>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final ColumnGroup columnGroup, final PersistentContext context) {
		xml.append(tab).append("<column_group>\n");

		final String tab2 = tab + "\t";
		xml.append(tab2).append("<id>").append(context.columnGroupMap.get(columnGroup))
				.append("</id>\n");

		xml.append(tab2).append("<group_name>").append(escape(columnGroup.getGroupName()))
				.append("</group_name>\n");

		xml.append(tab2).append("<columns>\n");

		final String tab3 = tab + "\t\t";
		for (NormalColumn normalColumn : columnGroup.getColumns()) {
			createXML(xml, tab3, normalColumn, context);
		}

		xml.append(tab2).append("</columns>\n");

		xml.append(tab).append("</column_group>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final TestData testData, final PersistentContext context) {
		xml.append(tab).append("<test_data>\n");

		final String tab2 = tab + "\t";
		xml.append(tab2).append("<name>").append(escape(testData.getName()))
				.append("</name>\n");
		xml.append(tab2).append("<export_order>").append(testData.getExportOrder())
				.append("</export_order>\n");

		Map<ERTable, TableTestData> tableTestDataMap = testData
				.getTableTestDataMap();
		for (Map.Entry<ERTable, TableTestData> entry : tableTestDataMap
				.entrySet()) {
			ERTable table = entry.getKey();
			TableTestData tableTestData = entry.getValue();

			createXML(xml, tab2, tableTestData, table, context);
		}

		xml.append(tab).append("</test_data>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final TableTestData tableTestData, final ERTable table,
			PersistentContext context) {
		xml.append(tab).append("<table_test_data>\n");

		xml.append(tab).append("\t<table_id>").append(context.nodeElementMap.get(table))
				.append("</table_id>\n");

		DirectTestData directTestData = tableTestData.getDirectTestData();
		RepeatTestData repeatTestData = tableTestData.getRepeatTestData();

		final String tab2 = tab + "\t";
		createXML(xml, tab2, directTestData, table, context);
		createXML(xml, tab2, repeatTestData, table, context);

		xml.append(tab).append("</table_test_data>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final DirectTestData directTestData, ERTable table,
			PersistentContext context) {
		xml.append(tab).append("<direct_test_data>\n");

		for (Map<NormalColumn, String> data : directTestData.getDataList()) {
			xml.append(tab).append("\t<data>\n");
			for (NormalColumn normalColumn : table.getExpandedColumns()) {
				xml.append(tab).append("\t\t<column_data>\n");
				xml.append(tab).append("\t\t\t<column_id>")
						.append(context.columnMap.get(normalColumn))
						.append("</column_id>\n");
				xml.append(tab).append("\t\t\t<value>")
						.append(escape(data.get(normalColumn)))
						.append("</value>\n");
				xml.append(tab).append("\t\t</column_data>\n");
			}
			xml.append(tab).append("\t</data>\n");
		}

		xml.append(tab).append("</direct_test_data>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final RepeatTestData repeatTestData, ERTable table,
			PersistentContext context) {
		xml.append(tab).append("<repeat_test_data>\n");
		xml.append(tab).append("\t<test_data_num>").append(repeatTestData.getTestDataNum())
				.append("</test_data_num>\n");
		xml.append(tab).append("\t<data_def_list>\n");

		final String tab2 = tab + "\t";
		for (NormalColumn normalColumn : table.getExpandedColumns()) {
			createXML(xml, tab2,
					repeatTestData.getDataDef(normalColumn), normalColumn,
					context);
		}

		xml.append(tab).append("\t</data_def_list>\n");
		xml.append(tab).append("</repeat_test_data>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final RepeatTestDataDef repeatTestDataDef,
			NormalColumn column, PersistentContext context) {
		Integer columnId = context.columnMap.get(column);

		if (columnId != null) {
			xml.append(tab).append("<data_def>\n");
			xml.append(tab).append("\t<column_id>").append(columnId)
					.append("</column_id>\n");
			xml.append(tab).append("\t<type>").append(escape(repeatTestDataDef.getType()))
					.append("</type>\n");
			xml.append(tab).append("\t<repeat_num>")
					.append(Format.toString((repeatTestDataDef.getRepeatNum())))
					.append("</repeat_num>\n");
			xml.append(tab).append("\t<template>")
					.append(escape(repeatTestDataDef.getTemplate()))
					.append("</template>\n");
			xml.append(tab).append("\t<from>")
					.append(Format.toString((repeatTestDataDef.getFrom())))
					.append("</from>\n");
			xml.append(tab).append("\t<to>")
					.append(Format.toString((repeatTestDataDef.getTo())))
					.append("</to>\n");
			xml.append(tab).append("\t<increment>")
					.append(Format.toString((repeatTestDataDef.getIncrement())))
					.append("</increment>\n");
			for (String select : repeatTestDataDef.getSelects()) {
				xml.append(tab).append("\t<select>").append(escape(select))
						.append("</select>\n");
			}
			xml.append(tab).append("\t<modified_values>\n");
			for (Integer modifiedRow : repeatTestDataDef.getModifiedValues()
					.keySet()) {
				xml.append(tab).append("\t\t<modified_value>\n");
				xml.append(tab).append("\t\t\t<row>").append(modifiedRow)
						.append("</row>\n");
				xml.append(tab).append("\t\t\t<value>")
						.append(escape(repeatTestDataDef.getModifiedValues()
								.get(modifiedRow))).append("</value>\n");
				xml.append(tab).append("\t\t</modified_value>\n");
			}
			xml.append(tab).append("\t</modified_values>\n");

			xml.append(tab).append("</data_def>\n");
		}
	}

 	private void createXML(final StringBuilder xml, final String tab, final ChangeTrackingList changeTrackingList) {
		xml.append(tab).append("<change_tracking_list>\n");

		final String tab2 = tab + "\t";
		for (ChangeTracking changeTracking : changeTrackingList.getList()) {
			createXML(xml, tab2, changeTracking);
		}

		xml.append(tab).append("</change_tracking_list>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final ChangeTracking changeTracking) {
		xml.append(tab).append("<change_tracking>\n");

		final String tab2 = tab + "\t";
		xml.append(tab2).append("<updated_date>")
				.append(DATE_FORMAT.format(changeTracking.getUpdatedDate()))
				.append("</updated_date>\n");
		xml.append(tab2).append("<comment>").append(escape(changeTracking.getComment()))
				.append("</comment>\n");

		PersistentContext context = this
				.getChangeTrackingContext(changeTracking);

		createXML(xml, tab2, changeTracking.getDiagramContents(),
				context);

		xml.append(tab).append("</change_tracking>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final ExportSetting exportSetting,
			final PersistentContext context) {
		xml.append(tab).append("<export_setting>\n");

		xml.append(tab).append("\t<category_name_to_export>")
				.append(escape(exportSetting.getCategoryNameToExport()))
				.append("</category_name_to_export>\n");
		xml.append(tab).append("\t<ddl_output>")
				.append(escape(exportSetting.getDdlOutput()))
				.append("</ddl_output>\n");
		xml.append(tab).append("\t<excel_output>")
				.append(escape(exportSetting.getExcelOutput()))
				.append("</excel_output>\n");
		xml.append(tab).append("\t<excel_template>")
				.append(escape(exportSetting.getExcelTemplate()))
				.append("</excel_template>\n");
		xml.append(tab).append("\t<image_output>")
				.append(escape(exportSetting.getImageOutput()))
				.append("</image_output>\n");
		xml.append(tab).append("\t<put_diagram_on_excel>")
				.append(exportSetting.isPutERDiagramOnExcel())
				.append("</put_diagram_on_excel>\n");
		xml.append(tab).append("\t<use_logical_name_as_sheet>")
				.append(exportSetting.isUseLogicalNameAsSheet())
				.append("</use_logical_name_as_sheet>\n");
		xml.append(tab).append("\t<open_after_saved>")
				.append(exportSetting.isOpenAfterSaved())
				.append("</open_after_saved>\n");

		DDLTarget ddlTarget = exportSetting.getDdlTarget();

		xml.append(tab).append("\t<create_comment>").append(ddlTarget.createComment)
				.append("</create_comment>\n");
		xml.append(tab).append("\t<create_foreignKey>").append(ddlTarget.createForeignKey)
				.append("</create_foreignKey>\n");
		xml.append(tab).append("\t<create_index>").append(ddlTarget.createIndex)
				.append("</create_index>\n");
		xml.append(tab).append("\t<create_sequence>").append(ddlTarget.createSequence)
				.append("</create_sequence>\n");
		xml.append(tab).append("\t<create_table>").append(ddlTarget.createTable)
				.append("</create_table>\n");
		xml.append(tab).append("\t<create_tablespace>").append(ddlTarget.createTablespace)
				.append("</create_tablespace>\n");
		xml.append(tab).append("\t<create_trigger>").append(ddlTarget.createTrigger)
				.append("</create_trigger>\n");
		xml.append(tab).append("\t<create_view>").append(ddlTarget.createView)
				.append("</create_view>\n");

		xml.append(tab).append("\t<drop_index>").append(ddlTarget.dropIndex)
				.append("</drop_index>\n");
		xml.append(tab).append("\t<drop_sequence>").append(ddlTarget.dropSequence)
				.append("</drop_sequence>\n");
		xml.append(tab).append("\t<drop_table>").append(ddlTarget.dropTable)
				.append("</drop_table>\n");
		xml.append(tab).append("\t<drop_tablespace>").append(ddlTarget.dropTablespace)
				.append("</drop_tablespace>\n");
		xml.append(tab).append("\t<drop_trigger>").append(ddlTarget.dropTrigger)
				.append("</drop_trigger>\n");
		xml.append(tab).append("\t<drop_view>").append(ddlTarget.dropView)
				.append("</drop_view>\n");

		xml.append(tab).append("\t<inline_column_comment>")
				.append(ddlTarget.inlineColumnComment)
				.append("</inline_column_comment>\n");
		xml.append(tab).append("\t<inline_table_comment>")
				.append(ddlTarget.inlineTableComment)
				.append("</inline_table_comment>\n");

		xml.append(tab).append("\t<comment_value_description>")
				.append(ddlTarget.commentValueDescription)
				.append("</comment_value_description>\n");
		xml.append(tab).append("\t<comment_value_logical_name>")
				.append(ddlTarget.commentValueLogicalName)
				.append("</comment_value_logical_name>\n");
		xml.append(tab).append("\t<comment_value_logical_name_description>")
				.append(ddlTarget.commentValueLogicalNameDescription)
				.append("</comment_value_logical_name_description>\n");
		xml.append(tab).append("\t<comment_replace_line_feed>")
				.append(ddlTarget.commentReplaceLineFeed)
				.append("</comment_replace_line_feed>\n");
		xml.append(tab).append("\t<comment_replace_string>")
				.append(Format.null2blank(ddlTarget.commentReplaceString))
				.append("</comment_replace_string>\n");

		final String tab2 = tab + "\t";
		createXML(xml, tab2, exportSetting.getExportJavaSetting(), context);
		createXML(xml, tab2, exportSetting.getExportTestDataSetting(), context);

		xml.append(tab).append("</export_setting>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final ExportJavaSetting exportJavaSetting,
			final PersistentContext context) {
		xml.append(tab).append("<export_java_setting>\n");

		xml.append(tab).append("\t<java_output>")
				.append(escape(exportJavaSetting.getJavaOutput()))
				.append("</java_output>\n");
		xml.append(tab).append("\t<package_name>")
				.append(escape(exportJavaSetting.getPackageName()))
				.append("</package_name>\n");
		xml.append(tab).append("\t<class_name_suffix>")
				.append(escape(exportJavaSetting.getClassNameSuffix()))
				.append("</class_name_suffix>\n");
		xml.append(tab).append("\t<src_file_encoding>")
				.append(escape(exportJavaSetting.getSrcFileEncoding()))
				.append("</src_file_encoding>\n");
		xml.append(tab).append("\t<with_hibernate>")
				.append(exportJavaSetting.isWithHibernate())
				.append("</with_hibernate>\n");

		xml.append(tab).append("</export_java_setting>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final ExportTestDataSetting exportTestDataSetting,
			final PersistentContext context) {
		xml.append(tab).append("<export_testdata_setting>\n");

		xml.append(tab).append("\t<file_encoding>")
				.append(escape(exportTestDataSetting.getExportFileEncoding()))
				.append("</file_encoding>\n");
		xml.append(tab).append("\t<file_path>")
				.append(escape(exportTestDataSetting.getExportFilePath()))
				.append("</file_path>\n");
		xml.append(tab).append("\t<format>")
				.append(exportTestDataSetting.getExportFormat())
				.append("</format>\n");

		xml.append(tab).append("</export_testdata_setting>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final CategorySetting categorySettings,
			final PersistentContext context) {
		xml.append(tab).append("<category_settings>\n");
		xml.append(tab).append("\t<free_layout>").append(categorySettings.isFreeLayout())
				.append("</free_layout>\n");
		xml.append(tab).append("\t<show_referred_tables>")
				.append(categorySettings.isShowReferredTables())
				.append("</show_referred_tables>\n");

		xml.append(tab).append("\t<categories>\n");

		final String tab2 = tab + "\t";
		for (Category category : categorySettings.getAllCategories()) {
			createXML(xml, tab2, category,
					categorySettings.isSelected(category), context);
		}

		xml.append(tab).append("\t</categories>\n");

		xml.append(tab).append("</category_settings>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final TranslationSetting translationSettings,
			final PersistentContext context) {
		xml.append(tab).append("<translation_settings>\n");
		xml.append(tab).append("\t<use>").append(translationSettings.isUse())
				.append("</use>\n");

		xml.append(tab).append("\t<translations>\n");

		final String tab2 = tab + "\t";
		for (String translation : translationSettings.getSelectedTranslations()) {
			createTranslationXML(xml, tab2, translation, context);
		}

		xml.append(tab).append("\t</translations>\n");

		xml.append(tab).append("</translation_settings>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final Category category, final boolean isSelected,
			final PersistentContext context) {
		xml.append(tab).append("<category>\n");

		createXMLNodeElement(xml, tab + "\t", category, context);

		xml.append(tab).append("\t<name>").append(escape(category.getName()))
				.append("</name>\n");
		xml.append(tab).append("\t<selected>").append(isSelected).append("</selected>\n");

		for (NodeElement nodeElement : category.getContents()) {
			xml.append(tab).append("\t<node_element>")
					.append(context.nodeElementMap.get(nodeElement))
					.append("</node_element>\n");
		}

		xml.append(tab).append("</category>\n");
	}

	private void createTranslationXML(final StringBuilder xml, final String tab, final String translation, final PersistentContext context) {
		xml.append(tab).append("<translation>\n");
		xml.append(tab).append("\t<name>").append(escape(translation)).append("</name>\n");
		xml.append(tab).append("</translation>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final NodeSet contents, PersistentContext context) {
		xml.append(tab).append("<contents>\n");

		for (NodeElement content : contents) {
			if (content instanceof ERTable) {
				createXML(xml, tab + "\t", (ERTable) content, context);

			} else if (content instanceof Note) {
				createXML(xml, tab + "\t", (Note) content, context);

			} else if (content instanceof View) {
				createXML(xml, tab + "\t", (View) content, context);

			} else if (content instanceof InsertedImage) {
				createXML(xml, tab + "\t", (InsertedImage) content, context);

			}
		}

		xml.append(tab).append("</contents>\n");
	}

	private void createXMLNodeElement(final StringBuilder xml, final String tab, final NodeElement nodeElement,
			final PersistentContext context) {
		xml.append(tab).append("<id>")
				.append(Format.toString(context.nodeElementMap.get(nodeElement)))
				.append("</id>\n");
		xml.append(tab).append("<height>").append(nodeElement.getHeight())
				.append("</height>\n");
		xml.append(tab).append("<width>").append(nodeElement.getWidth())
				.append("</width>\n");
		xml.append(tab).append("<font_name>").append(escape(nodeElement.getFontName()))
				.append("</font_name>\n");
		xml.append(tab).append("<font_size>").append(nodeElement.getFontSize())
				.append("</font_size>\n");
		xml.append(tab).append("<x>").append(nodeElement.getX()).append("</x>\n");
		xml.append(tab).append("<y>").append(nodeElement.getY()).append("</y>\n");
		createXMLColor(xml, tab, nodeElement.getColor());

		List<ConnectionElement> incomings = nodeElement.getIncomings();
		createXMLConnections(xml, tab, incomings, context);
	}

	private void createXMLColor(final StringBuilder xml, final String tab, int[] colors) {
		if (colors != null) {
			xml.append(tab).append("<color>\n");
			xml.append(tab).append("\t<r>").append(colors[0]).append("</r>\n");
			xml.append(tab).append("\t<g>").append(colors[1]).append("</g>\n");
			xml.append(tab).append("\t<b>").append(colors[2]).append("</b>\n");
			xml.append(tab).append("</color>\n");
		}
	}

 	private void createXML(final StringBuilder xml, final String tab, final ERTable table, PersistentContext context) {
		xml.append(tab).append("<table>\n");

		final String tab2 = tab + "\t";
		createXMLNodeElement(xml, tab2, table, context);

		xml.append(tab).append("\t<physical_name>").append(escape(table.getPhysicalName()))
				.append("</physical_name>\n");
		xml.append(tab).append("\t<logical_name>").append(escape(table.getLogicalName()))
				.append("</logical_name>\n");
		xml.append(tab).append("\t<description>").append(escape(table.getDescription()))
				.append("</description>\n");
		xml.append(tab).append("\t<constraint>").append(escape(table.getConstraint()))
				.append("</constraint>\n");
		xml.append(tab).append("\t<primary_key_name>")
				.append(escape(table.getPrimaryKeyName()))
				.append("</primary_key_name>\n");
		xml.append(tab).append("\t<option>").append(escape(table.getOption()))
				.append("</option>\n");

		List<Column> columns = table.getColumns();
		createXMLColumns(xml, tab2, columns, context);

		List<Index> indexes = table.getIndexes();
		createXMLIndexes(xml, tab2, indexes, context);

		List<ComplexUniqueKey> complexUniqueKeyList = table
				.getComplexUniqueKeyList();
		createXMLComplexUniqueKeyList(xml, tab2, complexUniqueKeyList,
				context);

		TableProperties tableProperties = (TableProperties) table
				.getTableViewProperties();
		createXML(xml, tab2, tableProperties, context);

		xml.append(tab).append("</table>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final View view, final PersistentContext context) {
		xml.append(tab).append("<view>\n");

		final String tab2 = tab + "\t";
		createXMLNodeElement(xml, tab2, view, context);

		xml.append(tab).append("\t<physical_name>").append(escape(view.getPhysicalName()))
				.append("</physical_name>\n");
		xml.append(tab).append("\t<logical_name>").append(escape(view.getLogicalName()))
				.append("</logical_name>\n");
		xml.append(tab).append("\t<description>").append(escape(view.getDescription()))
				.append("</description>\n");
		xml.append(tab).append("\t<sql>").append(escape(view.getSql())).append("</sql>\n");

		List<Column> columns = view.getColumns();
		createXMLColumns(xml, tab2, columns, context);

		ViewProperties viewProperties = (ViewProperties) view
				.getTableViewProperties();
		createXML(xml, tab2, viewProperties, context);

		xml.append(tab).append("</view>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final ModelProperties modelProperties,
			final PersistentContext context) {
		xml.append(tab).append("<model_properties>\n");

		final String tab2 = tab + "\t";
		createXMLNodeElement(xml, tab2, modelProperties, context);

		xml.append(tab).append("\t<display>").append(modelProperties.isDisplay())
				.append("</display>\n");
		xml.append(tab).append("\t<creation_date>")
				.append(DATE_FORMAT.format(modelProperties.getCreationDate()))
				.append("</creation_date>\n");
		xml.append(tab).append("\t<updated_date>")
				.append(DATE_FORMAT.format(modelProperties.getUpdatedDate()))
				.append("</updated_date>\n");

		for (NameValue property : modelProperties.getProperties()) {
			createXML(xml, tab2, property, context);
		}

		xml.append(tab).append("</model_properties>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final NameValue property, PersistentContext context) {
		xml.append(tab).append("<model_property>\n");

		xml.append(tab).append("\t<name>").append(escape(property.getName()))
				.append("</name>\n");
		xml.append(tab).append("\t<value>").append(escape(property.getValue()))
				.append("</value>\n");

		xml.append(tab).append("</model_property>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final Note note, PersistentContext context) {
		xml.append(tab).append("<note>\n");

		final String tab2 = tab + "\t";
		createXMLNodeElement(xml, tab2, note, context);
		xml.append(tab).append("\t<text>").append(escape(note.getText()))
				.append("</text>\n");

		xml.append(tab).append("</note>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final InsertedImage insertedImage,
			PersistentContext context) {
		xml.append(tab).append("<image>\n");

		final String tab2 = tab + "\t";
		createXMLNodeElement(xml, tab2, insertedImage, context);
		xml.append(tab).append("\t<data>").append(insertedImage.getBase64EncodedData())
				.append("</data>\n");
		xml.append(tab).append("\t<hue>").append(insertedImage.getHue()).append("</hue>\n");
		xml.append(tab).append("\t<saturation>").append(insertedImage.getSaturation())
				.append("</saturation>\n");
		xml.append(tab).append("\t<brightness>").append(insertedImage.getBrightness())
				.append("</brightness>\n");
		xml.append(tab).append("\t<alpha>").append(insertedImage.getAlpha())
				.append("</alpha>\n");
		xml.append(tab).append("\t<fix_aspect_ratio>")
				.append(insertedImage.isFixAspectRatio())
				.append("</fix_aspect_ratio>\n");

		xml.append(tab).append("</image>\n");
	}

	private void createXMLColumns(final StringBuilder xml, final String tab, final List<Column> columns,
			final PersistentContext context) {
		xml.append(tab).append("<columns>\n");

		final String tab2 = tab + "\t";
		for (Column column : columns) {

			if (column instanceof ColumnGroup) {
				createXMLId(xml, tab2, (ColumnGroup) column, context);

			} else if (column instanceof NormalColumn) {
				createXML(xml, tab2, (NormalColumn) column, context);

			}
		}

		xml.append(tab).append("</columns>\n");
	}

	private void createXMLId(final StringBuilder xml, final String tab, final ColumnGroup columnGroup,
			final PersistentContext context) {
		xml.append(tab).append("<column_group>")
				.append(context.columnGroupMap.get(columnGroup))
				.append("</column_group>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final NormalColumn normalColumn,
			final PersistentContext context) {
		xml.append(tab).append("<normal_column>\n");

		Integer wordId = null;

		if (context != null) {
			wordId = context.wordMap.get(normalColumn.getWord());
			if (wordId != null) {
				xml.append(tab).append("\t<word_id>").append(wordId).append("</word_id>\n");
			}

			xml.append(tab).append("\t<id>").append(context.columnMap.get(normalColumn))
					.append("</id>\n");
			for (NormalColumn referencedColumn : normalColumn
					.getReferencedColumnList()) {
				xml.append(tab).append("\t<referenced_column>")
						.append(Format.toString(context.columnMap
								.get(referencedColumn)))
						.append("</referenced_column>\n");
			}
			for (Relation relation : normalColumn.getRelationList()) {
				xml.append(tab).append("\t<relation>")
						.append(context.connectionMap.get(relation))
						.append("</relation>\n");
			}
		}

		String description = normalColumn.getForeignKeyDescription();
		String logicalName = normalColumn.getForeignKeyLogicalName();
		String physicalName = normalColumn.getForeignKeyPhysicalName();
		SqlType sqlType = normalColumn.getType();

		xml.append(tab).append("\t<description>").append(escape(description))
				.append("</description>\n");
		xml.append(tab).append("\t<unique_key_name>")
				.append(escape(normalColumn.getUniqueKeyName()))
				.append("</unique_key_name>\n");
		xml.append(tab).append("\t<logical_name>").append(escape(logicalName))
				.append("</logical_name>\n");
		xml.append(tab).append("\t<physical_name>").append(escape(physicalName))
				.append("</physical_name>\n");

		String type = "";
		if (sqlType != null) {
			type = sqlType.getId();
		}
		xml.append(tab).append("\t<type>").append(type).append("</type>\n");

		xml.append(tab).append("\t<constraint>")
				.append(escape(normalColumn.getConstraint()))
				.append("</constraint>\n");
		xml.append(tab).append("\t<default_value>")
				.append(escape(normalColumn.getDefaultValue()))
				.append("</default_value>\n");

		xml.append(tab).append("\t<auto_increment>").append(normalColumn.isAutoIncrement())
				.append("</auto_increment>\n");
		xml.append(tab).append("\t<foreign_key>").append(normalColumn.isForeignKey())
				.append("</foreign_key>\n");
		xml.append(tab).append("\t<not_null>").append(normalColumn.isNotNull())
				.append("</not_null>\n");
		xml.append(tab).append("\t<primary_key>").append(normalColumn.isPrimaryKey())
				.append("</primary_key>\n");
		xml.append(tab).append("\t<unique_key>").append(normalColumn.isUniqueKey())
				.append("</unique_key>\n");

		xml.append(tab).append("\t<character_set>")
				.append(escape(normalColumn.getCharacterSet()))
				.append("</character_set>\n");
		xml.append(tab).append("\t<collation>").append(escape(normalColumn.getCollation()))
				.append("</collation>\n");

		createXML(xml, tab + "\t", normalColumn.getAutoIncrementSetting());
		xml.append(tab).append("</normal_column>\n");
	}

	private void createXMLConnections(final StringBuilder xml, final String tab, final List<ConnectionElement> incomings,
			final PersistentContext context) {
		xml.append(tab).append("<connections>\n");

		final String tab2 = tab + "\t";
		for (ConnectionElement connection : incomings) {

			if (connection instanceof CommentConnection) {
				createXML(xml, tab2, (CommentConnection) connection, context);

			} else if (connection instanceof Relation) {
				createXML(xml, tab2, (Relation) connection, context);
			}

		}

		xml.append(tab).append("</connections>\n");
	}

	private void createXMLConnectionElement(final StringBuilder xml, final String tab, final ConnectionElement connection,
			final PersistentContext context) {
		xml.append(tab).append("<id>").append(context.connectionMap.get(connection))
				.append("</id>\n");
		xml.append(tab).append("<source>")
				.append(context.nodeElementMap.get(connection.getSource()))
				.append("</source>\n");
		xml.append(tab).append("<target>")
				.append(context.nodeElementMap.get(connection.getTarget()))
				.append("</target>\n");
		xml.append(tab).append("\t<source_xp>").append(connection.getSourceXp())
				.append("</source_xp>\n");
		xml.append(tab).append("\t<source_yp>").append(connection.getSourceYp())
				.append("</source_yp>\n");
		xml.append(tab).append("\t<target_xp>").append(connection.getTargetXp())
				.append("</target_xp>\n");
		xml.append(tab).append("\t<target_yp>").append(connection.getTargetYp())
				.append("</target_yp>\n");

		final String tab2 = tab + "\t";
		for (Bendpoint bendpoint : connection.getBendpoints()) {
			createXML(xml, tab2, bendpoint);
		}
	}

 	private void createXML(final StringBuilder xml, final String tab, final Bendpoint bendpoint) {
		xml.append(tab).append("<bendpoint>\n");

		xml.append(tab).append("\t<relative>").append(bendpoint.isRelative())
				.append("</relative>\n");
		xml.append(tab).append("\t<x>").append(bendpoint.getX()).append("</x>\n");
		xml.append(tab).append("\t<y>").append(bendpoint.getY()).append("</y>\n");

		xml.append(tab).append("</bendpoint>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final CommentConnection connection,
			final PersistentContext context) {
		xml.append(tab).append("<comment_connection>\n");

		createXMLConnectionElement(xml, tab + "\t", connection, context);

		xml.append(tab).append("</comment_connection>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final Relation relation, final PersistentContext context) {
		xml.append(tab).append("<relation>\n");

		createXMLConnectionElement(xml, tab + "\t", relation, context);

		xml.append(tab).append("\t<child_cardinality>")
				.append(escape(relation.getChildCardinality()))
				.append("</child_cardinality>\n");
		xml.append(tab).append("\t<parent_cardinality>")
				.append(escape(relation.getParentCardinality()))
				.append("</parent_cardinality>\n");
		xml.append(tab).append("\t<reference_for_pk>").append(relation.isReferenceForPK())
				.append("</reference_for_pk>\n");
		xml.append(tab).append("\t<name>").append(escape(relation.getName()))
				.append("</name>\n");
		xml.append(tab).append("\t<on_delete_action>")
				.append(escape(relation.getOnDeleteAction()))
				.append("</on_delete_action>\n");
		xml.append(tab).append("\t<on_update_action>")
				.append(escape(relation.getOnUpdateAction()))
				.append("</on_update_action>\n");
		xml.append(tab).append("\t<referenced_column>")
				.append(context.columnMap.get(relation.getReferencedColumn()))
				.append("</referenced_column>\n");
		xml.append(tab).append("\t<referenced_complex_unique_key>")
				.append(context.complexUniqueKeyMap.get(relation
						.getReferencedComplexUniqueKey()))
				.append("</referenced_complex_unique_key>\n");

		xml.append(tab).append("</relation>\n");
	}

	private void createXMLIndexes(final StringBuilder xml, final String tab, final List<Index> indexes,
			final PersistentContext context) {
		xml.append(tab).append("<indexes>\n");

		final String tab2 = tab + "\t";
		for (final Index index : indexes) {
			createXML(xml, tab2, index, context);
		}

		xml.append(tab).append("</indexes>\n");
	}

	private void createXMLComplexUniqueKeyList(
			final StringBuilder xml, final String tab, final List<ComplexUniqueKey> complexUniqueKeyList,
			final PersistentContext context) {
		xml.append(tab).append("<complex_unique_key_list>\n");

		final String tab2 = tab + "\t";
		for (ComplexUniqueKey complexUniqueKey : complexUniqueKeyList) {
			createXML(xml, tab2, complexUniqueKey, context);
		}

		xml.append(tab).append("</complex_unique_key_list>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final EnvironmentSetting environmentSetting,
			final PersistentContext context) {
		xml.append(tab).append("<environment_setting>\n");

		for (Environment environment : environmentSetting.getEnvironments()) {
			xml.append(tab).append("\t<environment>\n");

			Integer environmentId = context.environmentMap.get(environment);
			xml.append(tab).append("\t\t<id>").append(environmentId).append("</id>\n");
			xml.append(tab).append("\t\t<name>").append(environment.getName())
					.append("</name>\n");

			xml.append(tab).append("\t</environment>\n");
		}

		xml.append(tab).append("</environment_setting>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final TableProperties tableProperties,
			final PersistentContext context) {
		xml.append(tab).append("<table_properties>\n");

		Integer tablespaceId = context.tablespaceMap.get(tableProperties
				.getTableSpace());
		if (tablespaceId != null) {
			xml.append(tab).append("\t<tablespace_id>").append(tablespaceId)
					.append("</tablespace_id>\n");
		}

		xml.append(tab).append("\t<schema>").append(escape(tableProperties.getSchema()))
				.append("</schema>\n");

		final String tab2 = tab + "\t";
		if (tableProperties instanceof MySQLTableProperties) {
			createXML(xml, tab2, (MySQLTableProperties) tableProperties);

		} else if (tableProperties instanceof PostgresTableProperties) {
			createXML(xml, tab2, (PostgresTableProperties) tableProperties);

		} else if (tableProperties instanceof OracleTableProperties) {
			createXML(xml, tab2, (OracleTableProperties) tableProperties);
		}

		xml.append(tab).append("</table_properties>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final MySQLTableProperties tableProperties) {
		xml.append(tab).append("<character_set>")
				.append(escape(tableProperties.getCharacterSet()))
				.append("</character_set>\n");
		xml.append(tab).append("<collation>")
				.append(escape(tableProperties.getCollation()))
				.append("</collation>\n");
		xml.append(tab).append("<storage_engine>")
				.append(escape(tableProperties.getStorageEngine()))
				.append("</storage_engine>\n");
		xml.append(tab).append("<primary_key_length_of_text>")
				.append(tableProperties.getPrimaryKeyLengthOfText())
				.append("</primary_key_length_of_text>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final PostgresTableProperties tableProperties) {
		xml.append(tab).append("<without_oids>").append(tableProperties.isWithoutOIDs())
				.append("</without_oids>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final OracleTableProperties tableProperties) {
		xml.append(tab).append("<character_set>")
				.append(escape(tableProperties.getCharacterSet()))
				.append("</character_set>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final ViewProperties viewProperties,
			PersistentContext context) {
		xml.append(tab).append("<view_properties>\n");

		Integer tablespaceId = context.tablespaceMap.get(viewProperties
				.getTableSpace());
		if (tablespaceId != null) {
			xml.append(tab).append("\t<tablespace_id>").append(tablespaceId)
					.append("</tablespace_id>\n");
		}

		xml.append(tab).append("<schema>").append(escape(viewProperties.getSchema()))
				.append("</schema>\n");

		xml.append(tab).append("</view_properties>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final Index index, PersistentContext context) {
		xml.append(tab).append("<inidex>\n");

		xml.append(tab).append("\t<full_text>").append(index.isFullText())
				.append("</full_text>\n");
		xml.append(tab).append("\t<non_unique>").append(index.isNonUnique())
				.append("</non_unique>\n");
		xml.append(tab).append("\t<name>").append(escape(index.getName()))
				.append("</name>\n");
		xml.append(tab).append("\t<type>").append(escape(index.getType()))
				.append("</type>\n");
		xml.append(tab).append("\t<description>").append(escape(index.getDescription()))
				.append("</description>\n");

		xml.append(tab).append("\t<columns>\n");

		List<Boolean> descs = index.getDescs();

		int count = 0;

		for (Column column : index.getColumns()) {
			xml.append(tab).append("\t\t<column>\n");
			xml.append(tab).append("\t\t\t<id>").append(context.columnMap.get(column))
					.append("</id>\n");

			Boolean desc = Boolean.FALSE;

			if (descs.size() > count) {
				desc = descs.get(count);
			}
			xml.append(tab).append("\t\t\t<desc>").append(desc).append("</desc>\n");
			xml.append(tab).append("\t\t</column>\n");

			count++;
		}

		xml.append(tab).append("\t</columns>\n");

		xml.append(tab).append("</inidex>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final ComplexUniqueKey complexUniqueKey,
			PersistentContext context) {
		xml.append(tab).append("<complex_unique_key>\n");

		xml.append(tab).append("\t<id>")
				.append(context.complexUniqueKeyMap.get(complexUniqueKey))
				.append("</id>\n");
		xml.append(tab).append("\t<name>")
				.append(Format.null2blank(complexUniqueKey.getUniqueKeyName()))
				.append("</name>\n");
		xml.append(tab).append("\t<columns>\n");

		for (NormalColumn column : complexUniqueKey.getColumnList()) {
			xml.append(tab).append("\t\t<column>\n");
			xml.append(tab).append("\t\t\t<id>").append(context.columnMap.get(column))
					.append("</id>\n");
			xml.append(tab).append("\t\t</column>\n");
		}

		xml.append(tab).append("\t</columns>\n");

		xml.append(tab).append("</complex_unique_key>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final Dictionary dictionary, PersistentContext context) {
		xml.append(tab).append("<dictionary>\n");

		final String tab2 = tab + "\t";
		for (Word word : dictionary.getWordList()) {
			createXML(xml, tab2, word, context);
		}

		xml.append(tab).append("</dictionary>\n");
	}

 	private void createXML(final StringBuilder xml, final String tab, final Word word, final PersistentContext context) {
		xml.append(tab).append("<word>\n");

		if (context != null) {
			xml.append(tab).append("\t<id>").append(context.wordMap.get(word))
					.append("</id>\n");
		}

		xml.append(tab).append("\t<length>").append(word.getTypeData().getLength())
				.append("</length>\n");
		xml.append(tab).append("\t<decimal>").append(word.getTypeData().getDecimal())
				.append("</decimal>\n");

		Integer arrayDimension = word.getTypeData().getArrayDimension();
		xml.append(tab).append("\t<array>").append(word.getTypeData().isArray())
				.append("</array>\n");
		xml.append(tab).append("\t<array_dimension>").append(arrayDimension)
				.append("</array_dimension>\n");

		xml.append(tab).append("\t<unsigned>").append(word.getTypeData().isUnsigned())
				.append("</unsigned>\n");
		xml.append(tab).append("\t<args>").append(escape(word.getTypeData().getArgs()))
				.append("</args>\n");

		xml.append(tab).append("\t<description>").append(escape(word.getDescription()))
				.append("</description>\n");
		xml.append(tab).append("\t<logical_name>").append(escape(word.getLogicalName()))
				.append("</logical_name>\n");
		xml.append(tab).append("\t<physical_name>").append(escape(word.getPhysicalName()))
				.append("</physical_name>\n");

		String type = "";
		if (word.getType() != null) {
			type = word.getType().getId();
		}
		xml.append(tab).append("\t<type>").append(type).append("</type>\n");

		xml.append(tab).append("</word>\n");
	}

	public static String escape(String s) {
		if (s == null) {
			return "";
		}

		final int n = s.length();
		StringBuilder result = new StringBuilder(n + 10);
		for (int i = 0; i < n; ++i) {
			appendEscapedChar(result, s.charAt(i));
		}
		return result.toString();
	}

	private static void appendEscapedChar(final StringBuilder buf, final char c) {
		// Encode special XML characters into the equivalent character
		// references.
		// The first five are defined by default for all XML documents.
		// The next three (#xD, #xA, #x9) are encoded to avoid them
		// being converted to spaces on deserialization
		switch (c) {
		case '<':
			buf.append("&lt;");
			return;
		case '>':
			buf.append("&gt;");
			return;
		case '"':
			buf.append("&quot;");
			return;
		case '\'':
			buf.append("&apos;");
			return;
		case '&':
			buf.append("&amp;");
			return;
		case '\r':
			buf.append("&#x0D;");
			return;
		case '\n':
			buf.append("&#x0A;");
			return;
		case '\u0009':
			buf.append("&#x09;");
			return;
		default:
			buf.append(c);
			return;
		}
	}

}