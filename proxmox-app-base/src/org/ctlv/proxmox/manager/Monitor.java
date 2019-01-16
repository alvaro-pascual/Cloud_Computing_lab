package org.ctlv.proxmox.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.LoginException;

import org.ctlv.proxmox.api.Constants;
import org.ctlv.proxmox.api.ProxmoxAPI;
import org.ctlv.proxmox.api.data.LXC;
import org.json.JSONException;

public class Monitor implements Runnable {

	Analyzer analyzer;
	ProxmoxAPI api;
	
	public Monitor(ProxmoxAPI api, Analyzer analyzer) {
		this.api = api;
		this.analyzer = analyzer;
	}
	

	@Override
	public void run() {
		
		while(true) {
			System.out.println("Monitor");
			// Récupérer les données sur les serveurs
			// ...
			Map<String, List<LXC>> myCTsPerServer = new HashMap<String, List<LXC>>();
			try{
			List<LXC> ctsS7 = new ArrayList<>();
			List<LXC> ctsS8 = new ArrayList<>();
			
				List<LXC> ctsS7all = api.getCTs(Constants.SERVER7);
				for (LXC lxc : ctsS7all) {
					if(lxc.getName().contains(Constants.CT_BASE_NAME)){
						ctsS7.add(lxc);
					}
				}
				List<LXC> ctsS8all = api.getCTs(Constants.SERVER8);
				for (LXC lxc : ctsS8all) {
					if(lxc.getName().contains(Constants.CT_BASE_NAME)){
						ctsS8.add(lxc);
					}
				}
				myCTsPerServer.put(Constants.SERVER7, ctsS7);
				myCTsPerServer.put(Constants.SERVER8, ctsS8);
			} catch (LoginException | JSONException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			// Lancer l'analyse
			// ...
			try {
				analyzer.analyze(myCTsPerServer);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			
			// attendre une certaine période
			try {
				Thread.sleep(Constants.MONITOR_PERIOD * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
}
