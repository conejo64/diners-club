package com.openkm.dinners.workflow.utils;

import com.openkm.sdk4j.bean.Configuration;
import com.openkm.sdk4j.exception.OKMException;
import com.openkm.sdk4j.exception.WebserviceException;
import com.openkm.sdk4j.impl.OKMWebservices;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.def.Swimlane;
import org.jbpm.taskmgmt.def.TaskMgmtDefinition;
import org.jbpm.taskmgmt.exe.SwimlaneInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.jbpm.taskmgmt.exe.TaskMgmtInstance;

public class WorkflowUtils {

	public static String getConfigValue(OKMWebservices ws, String cfgKey) throws OKMException, WebserviceException {
		Configuration cfg = ws.repository.getConfiguration(cfgKey);

		if (cfg != null) {
			if (cfg.getValue() != null) {
				return cfg.getValue();
			} else {
				return "Missing property value: " + cfgKey;
			}
		} else {
			return "Missing property value: " + cfgKey;
		}
	}

	/**
	 * Get initiator
	 */
	public static String getInitiator(ExecutionContext ctx) {
		ProcessDefinition procDef = ctx.getProcessDefinition();
		TaskMgmtDefinition taskMgmtDef = procDef.getTaskMgmtDefinition();
		TaskMgmtInstance taskMgmtIns = ctx.getTaskMgmtInstance();
		Swimlane swimlane = taskMgmtDef.getSwimlane("initiator");
		SwimlaneInstance swimlaneIns = taskMgmtIns.getInitializedSwimlaneInstance(ctx, swimlane);
		return swimlaneIns.getActorId();
	}

	/**
	 * Get last task instance
	 */
	public static TaskInstance getLastTaskInstance(ExecutionContext ctx) {
		TaskInstance ret = null;

		for (TaskInstance ti : ctx.getTaskMgmtInstance().getTaskInstances()) {
			if (ret == null || (ti.hasEnded() && ti.getEnd().after(ret.getEnd()))) {
				ret = ti;
			}
		}

		return ret;
	}

	/**
	 * Get last task assigned actor
	 */
	public static TaskInstance getLastTaskInstance(ExecutionContext ctx, String taskName) {
		for (TaskInstance ti : ctx.getTaskMgmtInstance().getTaskInstances()) {
			if (ti.hasEnded()) {
				if (taskName.equals(ti.getName())) {
					return ti;
				}
			}
		}

		return null;
	}
}
