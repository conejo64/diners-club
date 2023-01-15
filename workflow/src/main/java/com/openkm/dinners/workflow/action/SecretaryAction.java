package com.openkm.dinners.workflow.action;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SecretaryAction implements ActionHandler {
	private static final Logger log = LoggerFactory.getLogger(SecretaryAction.class);
	private static final long serialVersionUID = 1L;

	public void execute(ExecutionContext ctx) throws Exception {
		// Go to next node
		ctx.getToken().signal();
	}
}
