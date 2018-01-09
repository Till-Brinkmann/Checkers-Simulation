package network;

import java.io.IOException;

import java.net.Socket;
import java.net.UnknownHostException;


public class Client extends Connector {
	
	public Client(NetworkManager manager, String host, int port) {
		super(manager);
		try {
			connection = new Socket(host,port);
			manager.console.printInfo("Connection was established successfully","Client");
			receiveStrings();
		} catch (UnknownHostException e) {
			//KP
		} catch (IOException e) {
			//KP
		}
	}

}
