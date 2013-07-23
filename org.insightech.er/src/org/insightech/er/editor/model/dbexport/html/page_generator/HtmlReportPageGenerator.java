package org.insightech.er.editor.model.dbexport.html.page_generator;

import java.io.IOException;
import java.util.List;

import org.insightech.er.editor.model.ERDiagram;

public interface HtmlReportPageGenerator<T> {

	String generatePackageFrame(ERDiagram diagram) throws IOException;

	String generatePackageSummary(
			HtmlReportPageGenerator<?> prevPageGenerator,
			HtmlReportPageGenerator<?> nextPageGenerator, ERDiagram diagram)
			throws IOException;

	String generateContent(ERDiagram diagram, T object,
			Object prevObject, Object nextObject) throws IOException;

	String getPageTitle();

	String getType();

	String getObjectId(Object object);

	String getObjectName(T object);

	List<T> getObjectList(ERDiagram diagram);

}
