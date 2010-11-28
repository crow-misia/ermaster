package org.insightech.er.ant_task;

import org.apache.tools.ant.Task;
import org.eclipse.core.runtime.IProgressMonitor;

public class AntConsoleProgressMonitor implements IProgressMonitor {
	
//	private String taskInfo = "";
	private Task task;
//	private String taskname = "unknown";

	public AntConsoleProgressMonitor(Task t) {
		task = t;
//		taskname = t.getTaskName();
	}

	public void beginTask(String name, int totalTime) {
//		taskInfo = "";
//		taskname = name;
//		task.log("beginTask " + name + " " + taskInfo + "...");
	}

	public void done() {
//		task.log("doneTask=" + taskname);
//		taskname = "unknown";
	}

	public void setTaskName(String name) {
//		taskname = name;
	}

	public void subTask(String arg0) {
//		task.log(taskname + " ... subtask: " + arg0 + "...");
	}

	public void displayMsg(String msg) {
		task.log("Message=" + msg);
	}

	public void setCurrentTaskInfo(String info) {
//		taskInfo = info;
//		task.log("TaskInfo=" + info);
	}

	public void internalWorked(double arg0) {
	}

	public void worked(int timework) {
	}

	public boolean isCanceled() {
		return false;
	}

	public void setCanceled(boolean arg0) {
	}
}
