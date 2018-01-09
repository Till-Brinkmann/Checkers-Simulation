package network;
import java.net.*;
import java.io.*;
public class Server extends Connector {


	public Server(NetworkManager manager,int port, int clientLimit) {
		super(manager);
		try {
			ServerSocket server = new ServerSocket(port);

			establishingConnection(server);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void establishingConnection(ServerSocket server) throws IOException {
		server.setSoTimeout(600000000);
		try		{
		  manager.console.printInfo("Wait for Clients...", "Server");
		  connection = server.accept();
		  manager.console.printInfo("Connection is established", "Server");
		  receiveStrings();
		}
		catch(Exception e) {
			e.printStackTrace();
			manager.console.printInfo("Could not establish a connection", "sERVER");
		}
	}
}