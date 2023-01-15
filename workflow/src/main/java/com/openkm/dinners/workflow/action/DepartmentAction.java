package com.openkm.dinners.workflow.action;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.form.Option;
import com.openkm.bean.form.Select;

public class DepartmentAction implements ActionHandler {
	private static final Logger log = LoggerFactory.getLogger(DepartmentAction.class);
	private static final long serialVersionUID = 1L;

	public void execute(ExecutionContext ctx) throws Exception {
		Select select = new Select();
		Option opt = new Option();
		opt.setValue("1");
		opt.setSelected(true);
		select.getOptions().add(opt);
		ctx.setVariable("userData", select);

		// Go to next node
		ctx.getToken().signal();
	}
}
