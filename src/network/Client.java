package network;

import java.io.IOException;
import java.net.Socket;

/**
 * Client for games and chat over network.
 */
public class Client extends Connector {
	
	public Client(NetworkManager manager, String host, int port){
		super(manager);
		try {
			manager.console.print("Searching for Server available Server...");
			connection = new Socket(host,port);
			manager.console.printInfo("Server was found.Connection was established successfully. Now you can write and receive textmessages on the console.","Client");
			receiveInfo();		

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Client(NetworkManager manager,int port ,String ip) {
		super(manager);		
		try {
			manager.console.print("Searching for Server available Server...");
			connection = new Socket(ip,port);
			manager.console.printInfo("Server was found.Connection was established successfully. Now you can write and receive textmessages on the console.","Client");
			receiveInfo();
	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
