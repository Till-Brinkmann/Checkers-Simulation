package network;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import network.NetworkManager.InfoType;


public class Client extends Connector {
	
	public Client(NetworkManager manager, String host, int port){
		super(manager);
		try {
			manager.console.print("Searching for Server available Server...");
			connection = new Socket(host,port);
			manager.console.printInfo("Server was found.Connection was established successfully. Now you can write and receive textmessages on the console.","Client");
			receiveInfo();		
			//sendInfo(new Info(InfoType.CONNECTIONINFO, manager.getUser()));

		} catch (UnknownHostException e) {
			//KP
		} catch (IOException e) {
			//KP
		}
	}

	public Client(NetworkManager manager,int port ,String ip) {
		super(manager);		
		try {
			manager.console.print("Searching for Server available Server...");
			connection = new Socket(ip,port);
			manager.console.printInfo("Server was found.Connection was established successfully. Now you can write and receive textmessages on the console.","Client");
			receiveInfo();
			//sendInfo(new Info(InfoType.CONNECTIONINFO, manager.getUser()));
	
		} catch (UnknownHostException e) {
			//KP
		} catch (IOException e) {
			//KP
		}
	}
}
