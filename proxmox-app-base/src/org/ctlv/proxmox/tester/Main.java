package org.ctlv.proxmox.tester;

import java.io.IOException;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.ctlv.proxmox.api.Constants;
import org.ctlv.proxmox.api.ProxmoxAPI;
import org.ctlv.proxmox.api.data.LXC;
import org.json.JSONException;


public class Main {

	public static void main(String[] args) throws LoginException, JSONException, IOException {

		ProxmoxAPI api = new ProxmoxAPI();		
		
		
		// Listes les CTs par serveur
		for (int i=1; i<=10; i++) {
			String srv ="srv-px"+i;
			System.out.println("CTs sous "+srv);
			List<LXC> cts = api.getCTs(srv);
			
			for (LXC lxc : cts) {
				if(lxc.getName().contains(Constants.CT_BASE_NAME)){
					
					if(lxc.getStatus().equals("running")){
					api.stopCT(srv, lxc.getVmid());
					}
					api.deleteCT(srv, lxc.getVmid());
				}
				System.out.println("\t" + lxc.getName() + " -> " + lxc.getUptime() + " " + lxc.getVmid());
			}
		}
		
		
		
		// Cr�er un CT
		//api.createCT(Constants.SERVER7, "4056", "ct-tpiss-virt-A2-ct1", 512);
		
		// Start CT
		//api.startCT(Constants.SERVER7, "4056");
		
		
		//Stop CT
		//api.stopCT(node, ctID);
		/*
		api.deleteCT(Constants.SERVER7, "2001");
		api.deleteCT(Constants.SERVER8, "2002");
		api.deleteCT(Constants.SERVER8, "2003");
		api.deleteCT(Constants.SERVER8, "2004");
		api.deleteCT(Constants.SERVER8, "2005");
		api.deleteCT(Constants.SERVER7, "2006");
		api.deleteCT(Constants.SERVER8, "2007");
		api.deleteCT(Constants.SERVER7, "2008");
		api.deleteCT(Constants.SERVER7, "2009");
		api.deleteCT(Constants.SERVER7, "2010");
		api.deleteCT(Constants.SERVER7, "2011");
		api.deleteCT(Constants.SERVER7, "2012");
		api.deleteCT(Constants.SERVER7, "2012");
		api.deleteCT(Constants.SERVER7, "2014");
		*/
		// Supprimer un CT
		//api.deleteCT("srv-px3", "4056");
		
	}

}
