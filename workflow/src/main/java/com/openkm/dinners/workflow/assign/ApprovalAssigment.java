package com.openkm.dinners.workflow.assign;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.def.AssignmentHandler;
import org.jbpm.taskmgmt.exe.Assignable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApprovalAssigment implements AssignmentHandler {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(ApprovalAssigment.class);

	@Override
	public void assign(Assignable assign, ExecutionContext ctx) throws Exception {
		String uuid = (String) ctx.getContextInstance().getVariable("uuid");
		log.info("Node uuid: {}", uuid);

		try {
			String userId = "okmAdmin";
			assign.setActorId(userId);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
}
