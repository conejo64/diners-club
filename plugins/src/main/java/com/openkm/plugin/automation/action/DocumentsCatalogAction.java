package com.openkm.plugin.automation.action;

import com.openkm.api.*;
import com.openkm.bean.Record;
import com.openkm.core.ItemExistsException;
import com.openkm.db.bean.AutomationRule;
import com.openkm.db.bean.NodeBase;
import com.openkm.db.service.ConfigSrv;
import com.openkm.module.db.stuff.DbSessionManager;
import com.openkm.plugin.BasePlugin;
import com.openkm.plugin.automation.Action;
import com.openkm.plugin.automation.AutomationException;
import com.openkm.plugin.automation.AutomationUtils;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@PluginImplementation
public class DocumentsCatalogAction extends BasePlugin implements Action {
	private static final Logger log = LoggerFactory.getLogger(DocumentsCatalogAction.class);
	private static final ArrayList<AutomationRule.EnumEvents> EVENTS_AT_PRE = new ArrayList<>();
	private static final ArrayList<AutomationRule.EnumEvents> EVENTS_AT_POST = Stream.of(AutomationRule.EnumEvents.EVENT_PROPERTY_GROUP_ADD)
			.collect(Collectors.toCollection(ArrayList::new));
	private static final String LOG_NAME = "DinnersClubCatalogAction";

	@Autowired
	private OKMFolder okmFolder;

	@Autowired
	private OKMRecord okmRecord;

	@Autowired
	private OKMDocument okmDocument;

	@Autowired
	private OKMRepository okmRepository;

	@Autowired
	private OKMPropertyGroup okmPropertyGroup;

	@Autowired
	private AutomationUtils automationUtils;

	public void executePre(Map<String, Object> env, Object... params) throws AutomationException {
	}

	public void executePost(Map<String, Object> env, Object... params) throws AutomationException {
		String token = DbSessionManager.getInstance().getSystemToken();

		try {
			NodeBase node = this.automationUtils.getNodeToEvaluate(env);
			String pgName = this.automationUtils.getPropertyGroupName(env);
			if (pgName.equalsIgnoreCase("okg:dinners") && this.okmPropertyGroup.hasGroup(token, node.getUuid(), "okg:dinners")) {
				log.info(LOG_NAME, "Document has property group: okg:dinners");

				// Get metadata
				Map<String, String> pgMap = this.okmPropertyGroup.getProperties(token, node.getUuid(), "okg:dinners");
				String identificationNumber = pgMap.get("okp:dinners.cedula").replaceAll("[\\[\\]\"]+", "");
				String docType = pgMap.get("okp:dinners.tipodoc").replaceAll("[\\[\\]\"]+", "");
				String create = pgMap.get("okp:dinners.crear").replaceAll("[\\[\\]\"]+", "");

				// These folders should be configured
				String dstErrorUuid = "4dd7ae9b-a94f-487a-9217-60f345675c75 "; // Error folder
				String dstCatalogFldUuid = "928faeec-7b1a-406a-a203-5d5d2f86a1a5"; // Folder where data is organized

				if (create.equals("true")) {
					SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
					String currentDate = formatter.format(new Date());
					String fldPath = this.okmFolder.getPath(token, dstCatalogFldUuid);
					fldPath = fldPath + "/" + identificationNumber;
					log.info(LOG_NAME, "Path destination: " + fldPath);

					if (!this.okmRepository.hasNode(token, fldPath)) { // Create folder
						this.okmFolder.createMissingFolders(token, fldPath);
					}

					// Create record inside folder
					String recPath = fldPath + "/" + docType + "-" + currentDate;
					log.info(LOG_NAME, "Record destination: " + fldPath);
					Record record = this.okmRecord.createSimple(token, recPath);

					try {
						this.okmDocument.move(token, node.getUuid(), record.getUuid());
						log.info(LOG_NAME, "Document move properly");
					} catch (ItemExistsException ie) {
						log.info(LOG_NAME, "ItemExistsException: Move to error folder");
						this.okmDocument.move(token, node.getUuid(), dstErrorUuid);
					}
				}
			}
		} catch (Exception e) {
			throw new AutomationException(e.getMessage(), e);
		}
	}

	public String getName() {
		return "DinnersClub Catalogación de Pedidos";
	}

	public String getParamType00() {
		return "";
	}

	public String getParamSrc00() {
		return "";
	}

	public String getParamDesc00() {
		return "";
	}

	public String getParamType01() {
		return "";
	}

	public String getParamSrc01() {
		return "";
	}

	public String getParamDesc01() {
		return "";
	}

	public String getParamType02() {
		return "";
	}

	public String getParamSrc02() {
		return "";
	}

	public String getParamDesc02() {
		return "";
	}

	public List<AutomationRule.EnumEvents> getValidEventsAtPre() {
		return EVENTS_AT_PRE;
	}

	public List<AutomationRule.EnumEvents> getValidEventsAtPost() {
		return EVENTS_AT_POST;
	}
}
