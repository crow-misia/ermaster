package org.insightech.er.editor.model.dbexport.java;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;

public class ExportToJavaWithProgressManager extends ExportToJavaManager
		implements IRunnableWithProgress {

	private Exception exception;

	private IProgressMonitor monitor;

	public ExportToJavaWithProgressManager(String outputDir,
			String fileEncoding, String packageName, String classNameSuffix,
			ERDiagram diagram) {
		super(outputDir + "/src", fileEncoding, packageName, classNameSuffix,
				null, diagram);
	}

	/**
	 * exception ‚ðŽæ“¾‚µ‚Ü‚·.
	 * 
	 * @return exception
	 */
	public Exception getException() {
		return exception;
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {

		int count = this.diagram.getDiagramContents().getContents()
				.getTableSet().getList().size();

		monitor.beginTask(ResourceString
				.getResourceString("dialog.message.export.java"), count);

		try {
			this.monitor = monitor;
			doProcess();

		} catch (InterruptedException e) {
			throw e;

		} catch (Exception e) {
			this.exception = e;
		}

		monitor.done();
	}

	@Override
	protected void doPreTask(ERTable table) {
		this.monitor.subTask("writing : " + this.getClassName(table));
	}

	@Override
	protected void doPostTask() throws InterruptedException {
		this.monitor.worked(1);

		if (this.monitor.isCanceled()) {
			throw new InterruptedException("Cancel has been requested.");
		}
	}

}
