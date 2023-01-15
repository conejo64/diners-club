package com.openkm.dinners.workflow.action;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.dinners.workflow.config.Config;
import com.openkm.sdk4j.OKMWebservicesFactory;
import com.openkm.sdk4j.exception.OKMException;
import com.openkm.sdk4j.impl.OKMWebservices;

public class FinalAction implements ActionHandler {
	private static final Logger log = LoggerFactory.getLogger(FinalAction.class);
	private static final long serialVersionUID = 1L;

	public void execute(ExecutionContext ctx) throws Exception {
		String uuid = (String) ctx.getContextInstance().getVariable("uuid");
		log.info("Node uuid: {}", uuid);

		try {
			OKMWebservices ws = OKMWebservicesFactory.getInstance(Config.OKM_URL);
			ws.login(Config.OKM_USER, Config.OKM_PASS);
			ws.note.addNote(uuid, "Document approved");
		} catch (OKMException e) {
			log.error(e.getMessage());
		}

		// Go to next node
		ctx.getToken().signal();
	}
}
