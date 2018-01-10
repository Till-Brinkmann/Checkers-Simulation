package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;

public class Connector{
	
	protected Socket connection;
	protected NetworkManager manager;
	protected Scanner input;
	public String connectedUser = "other User";

	public Connector(NetworkManager manager) {
		this.manager = manager;
	}
	public void closeConnection() {
		if(connection != null) {
			try {
				connection.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			manager.console.printInfo("There is no existing connection","Client/Server");
		}
	}
	public void sendInfo(Info info) {
		if(!connection.isClosed()) {
			try {
				ObjectOutputStream output = new ObjectOutputStream(connection.getOutputStream());
				output.writeObject(info);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else{
			manager.console.printInfo("There is no existing connection", "Client/server");
		}
	}
	public void receiveInfo() {
		ForkJoinPool.commonPool().execute(new Runnable() {
    	    public void run()
    	    {
    			while(!connection.isClosed()) {	
    				try {
    					ObjectInputStream input = new ObjectInputStream(connection.getInputStream());
        				manager.interpretInfo((Info)input.readObject());
        				
    				} catch (IOException | ClassNotFoundException e) {
    					e.printStackTrace();
    				}
    			}
    	    }
		});		
	}
	public boolean isConnected() {
		return connection.isConnected();
	}
}
