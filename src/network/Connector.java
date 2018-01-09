package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;

import checkers.Move;

public class Connector{
	
	protected Socket connection;
	protected NetworkManager manager;
	protected Scanner input;

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
	public void sendString(String message) {
		if(!connection.isClosed()) {
			try {
				PrintWriter output = new PrintWriter( connection.getOutputStream(), true );
				output.println(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else{
			manager.console.printInfo("There is no existing connection", "Client/server");
		}
	}
	public void receiveStrings() {
		ForkJoinPool.commonPool().execute(new Runnable() {
    	    public void run()
    	    {
    	    	String message;
    			while(!connection.isClosed()) {	
    				try {
    					Scanner input  = new Scanner(connection.getInputStream());
        				message = input.nextLine();
        				manager.displayMessage(message);
    				} catch (IOException e) {
    					e.printStackTrace();
    				}
    			}
    	    }
		});		
	}
	public void sendMove(Move m) {
		if(!connection.isClosed()) {
			try {
				ObjectOutputStream output = new ObjectOutputStream(connection.getOutputStream());
				output.writeObject(m);
		      
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		else{
			manager.console.printInfo("There is no existing connection", "Client/server");
		}
	}
	public Move receiveMoveOjects() {
		Move m = null;
		ForkJoinPool.commonPool().execute(new Runnable() {
    	    public void run()
    	    {
    	    	final Move move;;
    			if(!connection.isClosed()) {	
    				try {
    					ObjectInputStream input = new ObjectInputStream(connection.getInputStream());
        				//makeMove((Move) input.readObject());
        				
    				} catch (IOException /*| ClassNotFoundException*/ e) {
    					e.printStackTrace();
    				}
    				
    			}
    	    }
		});	
		return m;
	}
	public boolean isConnected() {
		return connection.isConnected();
	}
}
