package org.ctlv.proxmox.manager;

import org.ctlv.proxmox.api.ProxmoxAPI;
import org.ctlv.proxmox.generator.GeneratorMain;

public class ManagerMain {

	public static void main(String[] args) throws Exception {
		
		ProxmoxAPI api = new ProxmoxAPI();
		Monitor monitor = new Monitor(api, new Analyzer(api, new Controller(api)));
		
		monitor.run();
		
		
		// to do
	}

}