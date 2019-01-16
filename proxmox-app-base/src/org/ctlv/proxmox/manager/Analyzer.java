package org.ctlv.proxmox.manager;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.LoginException;

import org.ctlv.proxmox.api.Constants;
import org.ctlv.proxmox.api.ProxmoxAPI;
import org.ctlv.proxmox.api.data.LXC;
import org.json.JSONException;

public class Analyzer {
	ProxmoxAPI api;
	Controller controller;
	
	public Analyzer(ProxmoxAPI api, Controller controller) {
		this.api = api;
		this.controller = controller;
	}
	
	public void analyze(Map<String, List<LXC>> myCTsPerServer) throws LoginException, JSONException, IOException, InterruptedException  {

		System.out.println("Analyzer");
		// Calculer la quantité de RAM utilisée par mes CTs sur chaque serveur
		// ...
		long ramSa = 0;
		long ramSb = 0;
		List<LXC> ctsA = myCTsPerServer.get(Constants.SERVER7);
		for (LXC lxc : ctsA) {
			ramSa += lxc.getMem();
		}
		List<LXC> ctsB = myCTsPerServer.get(Constants.SERVER8);
		for (LXC lxc : ctsB) {
			ramSb += lxc.getMem();
		}
		
		
		// Mémoire autorisée sur chaque serveur
		// ...
		long memAllowedOnServerA_8 = (long) (api.getNode(Constants.SERVER7).getMemory_total() * 0.04);
		long memAllowedOnServerB_8 = (long) (api.getNode(Constants.SERVER8).getMemory_total() * 0.04);
		long memAllowedOnServerA_12 = (long) (api.getNode(Constants.SERVER7).getMemory_total() * 0.07);
		long memAllowedOnServerB_12 = (long) (api.getNode(Constants.SERVER8).getMemory_total() * 0.07);
		float memOnServer1 = ((float)ramSa/(float)api.getNode(Constants.SERVER7).getMemory_total())*100;
		float memOnServer2 = ((float)ramSb/(float)api.getNode(Constants.SERVER7).getMemory_total())*100;
		System.out.println(memOnServer1 + " % SERVER7");
		System.out.println(memOnServer2 + " % SERVER8");
		
		// Analyse et Actions
		// ...
		Controller controller = new Controller(api);
		if (ramSa > memAllowedOnServerA_12){
			controller.offLoad(Constants.SERVER7);
		}
		else if (ramSb > memAllowedOnServerB_12) {
			controller.offLoad(Constants.SERVER8);
		}
		else if (ramSa > memAllowedOnServerA_8 && ramSb > memAllowedOnServerB_8){ // continue balancing
			if(ramSa > ramSb){
				controller.migrateFromTo(Constants.SERVER7, Constants.SERVER8);
			}
			else{
				controller.migrateFromTo(Constants.SERVER8, Constants.SERVER7);
			}
		}
		else if (ramSa > memAllowedOnServerA_8){
			controller.migrateFromTo(Constants.SERVER7, Constants.SERVER8);
		}
		else if (ramSb > memAllowedOnServerB_8){
			controller.migrateFromTo(Constants.SERVER8, Constants.SERVER7);
		}
		else {
			System.out.println("Not reaching 4%");
		}
		
		
	}

}
