package org.ctlv.proxmox.manager;

import java.io.IOException;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.ctlv.proxmox.api.Constants;
import org.ctlv.proxmox.api.ProxmoxAPI;
import org.ctlv.proxmox.api.data.LXC;
import org.json.JSONException;

public class Controller {

	ProxmoxAPI api;
	public Controller(ProxmoxAPI api){
		this.api = api;
	}
	
	// migrer un conteneur du serveur "srcServer" vers le serveur "dstServer"
	public void migrateFromTo(String srcServer, String dstServer) throws LoginException, JSONException, IOException, InterruptedException  {
		System.out.println("migrer");
		List<LXC> cts = api.getCTs(srcServer);
		long timeOn = 0;
		long id = -1;
		for (LXC lxc : cts) {
			if(lxc.getName().contains(Constants.CT_BASE_NAME)){
				if(lxc.getUptime() > timeOn){
					System.out.println("mes conteneurs");
					timeOn = lxc.getUptime();
					System.out.println(id);
					
					id = Integer.parseInt(lxc.getVmid());
				}
			}
		}
		System.out.println(id);
		if(id > 0){
			String idCT = Long.toString(id);
			System.out.println("migrate CT");
			api.stopCT(srcServer, idCT);
			System.out.print("stopping CT...");
			int i = 0;
			while(i!=5){
				i++;
				Thread.sleep(1000);
				System.out.print(".");
			}
			System.out.println("");
			System.out.print("migrating CT...");
			api.migrateCT(srcServer, idCT, dstServer);
			int j = 0;
			while(j!=20){
				j++;
				Thread.sleep(1000);
				System.out.print(".");
			}
			System.out.println("migrated and started");
			api.startCT(dstServer, idCT);
		}
	}

	// arrêter le plus vieux conteneur sur le serveur "server"
	public void offLoad(String server) throws LoginException, JSONException, IOException {
		System.out.println("Stopping CT");
		List<LXC> cts = api.getCTs(server);
		long timeOn = 0;
		long id = -1;
		for (LXC lxc : cts) {
			if(lxc.getName().contains(Constants.CT_BASE_NAME)){
				if(lxc.getUptime() > timeOn){
					timeOn = lxc.getUptime();
					id = Integer.parseInt(lxc.getVmid());
				}
			}
		}
		if(id > 0){
			String idCT = Long.toString(id);
			api.stopCT(server, idCT);
		}
	}

}
