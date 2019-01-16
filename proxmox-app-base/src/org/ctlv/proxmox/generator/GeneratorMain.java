package org.ctlv.proxmox.generator;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.security.auth.login.LoginException;

import org.ctlv.proxmox.api.Constants;
import org.ctlv.proxmox.api.ProxmoxAPI;
import org.ctlv.proxmox.api.data.LXC;
import org.ctlv.proxmox.manager.Analyzer;
import org.ctlv.proxmox.manager.Controller;
import org.ctlv.proxmox.manager.Monitor;
import org.json.JSONException;

public class GeneratorMain {
	
	static Random rndTime = new Random(new Date().getTime());
	public static int getNextEventPeriodic(int period) {
		return period;
	}
	public static int getNextEventUniform(int max) {
		return rndTime.nextInt(max);
	}
	public static int getNextEventExponential(int inv_lambda) {
		float next = (float) (- Math.log(rndTime.nextFloat()) * inv_lambda);
		return (int)next;
	}
	
	public static void main(String[] args) throws InterruptedException, LoginException, JSONException, IOException {
		
	
		long baseID = Constants.CT_BASE_ID;
		long baseName = 0;
		int lambda = 30;
		
		
		Map<String, List<LXC>> myCTsPerServer = new HashMap<String, List<LXC>>();

		ProxmoxAPI api = new ProxmoxAPI();
		Random rndServer = new Random(new Date().getTime());
		Random rndRAM = new Random(new Date().getTime()); 
		
		long memAllowedOnServer1 = (long) (api.getNode(Constants.SERVER7).getMemory_total() * Constants.MAX_THRESHOLD);
		long memAllowedOnServer2 = (long) (api.getNode(Constants.SERVER8).getMemory_total() * Constants.MAX_THRESHOLD);
		long driveAllowedOnServer1 = (long) (api.getNode(Constants.SERVER7).getRootfs_total() * Constants.MAX_THRESHOLD);
		long driveAllowedOnServer2 = (long) (api.getNode(Constants.SERVER8).getRootfs_total() * Constants.MAX_THRESHOLD);
		
		
		
		
		while (true) {
			
			
			// 1. Calculer la quantit� de RAM utilis�e par mes CTs sur chaque serveur
			long memOnServer1 = 0;
			memOnServer1 = (long) (api.getNode(Constants.SERVER7).getMemory_used() * Constants.MAX_THRESHOLD);
			float memOnServer1p = ((float)memOnServer1/(float)memAllowedOnServer1)*100;
			
			System.out.println(memOnServer1p + "% RAM used in server 1");
			
			long memOnServer2 = 0;
			memOnServer2 = (long) (api.getNode(Constants.SERVER8).getMemory_used() * Constants.MAX_THRESHOLD);
			float memOnServer2p = ((float)memOnServer2/(float)memAllowedOnServer2)*100;
			
			System.out.println(memOnServer2p + "% RAM used in server 2");
			
			float cpuOnServer1 = 0;
			cpuOnServer1 = api.getNode(Constants.SERVER7).getCpu();
			//System.out.println(cpuOnServer1 + "% CPU used in server 1");
			
			float cpuOnServer2 = 0;
			cpuOnServer2 = api.getNode(Constants.SERVER8).getCpu();
			//System.out.println(cpuOnServer2 + "% CPU used in server 2");
			
			long driveOnServer1 = 0;
			driveOnServer1 = (long) (api.getNode(Constants.SERVER7).getRootfs_used() * Constants.MAX_THRESHOLD);
			float driveOnServer1p = ((float)driveOnServer1/(float)driveAllowedOnServer1)*100;
			//System.out.println(driveOnServer1p + "% hard drive used in server 1");
			
			long driveOnServer2 = 0;
			driveOnServer2 = (long) (api.getNode(Constants.SERVER8).getRootfs_used() * Constants.MAX_THRESHOLD);
			float driveOnServer2p = ((float)driveOnServer2/(float)driveAllowedOnServer2)*100;
			//System.out.println(driveOnServer2p + "% hard drive used in server 2");
			
			
			
			// M�moire autoris�e sur chaque serveur
			float memRatioOnServer1 = 16;
			// ...
			float memRatioOnServer2 = 16;
			// ... 
			
			if (memOnServer1p < memRatioOnServer1 && memOnServer2p < memRatioOnServer2) {  // Exemple de condition de l'arr�t de la g�n�ration de CTs
				
				// choisir un serveur al�atoirement avec les ratios sp�cifi�s 66% vs 33%
				String serverName;
				if (rndServer.nextFloat() < Constants.CT_CREATION_RATIO_ON_SERVER1)
					serverName = Constants.SERVER7;
				else
					serverName = Constants.SERVER8;
				
				// cr�er un contenaire sur ce serveur
				// ...
				baseName ++;
				long ID = baseID+baseName;
				String id = String.valueOf(ID);
				api.createCT(serverName, id, Constants.CT_BASE_NAME+baseName, 512);
				System.out.print("creating...");
				int i = 0;
				while(i!=25){
					i++;
					Thread.sleep(1000);
					System.out.print(".");
				}
				api.startCT(serverName, id);
				System.out.println("started");
				
								
				// planifier la prochaine cr�ation
				int timeToWait = getNextEventExponential(lambda); // par exemple une loi expo d'une moyenne de 30sec
				
				// attendre jusqu'au prochain �v�nement
				Thread.sleep(1000 * timeToWait);
				
			}
			else {
				System.out.println("Servers are loaded, waiting ...");
				Thread.sleep(Constants.GENERATION_WAIT_TIME* 1000);
			}
		
		}
		
	}

}
