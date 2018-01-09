package network;


import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ForkJoinPool;

import javax.swing.JButton;
import javax.swing.JFrame;

import gui.Console;
import gui.GUI;

public class NetworkManager extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name = "Steve";
	private Connector connector;
	private NetworkManager manager;
	private GUI gui;
	public Console console;
	private int clientLimit;
	public NetworkManager(GUI gui, Console console) {
		super("NetworkManager");
		manager = this;
		this.gui = gui;
		this.console = console;
		createWindow();
		setVisible(true);
	}

	private void createWindow() {
		setResizable(false);
		setSize(400,300);
		setLayout(new FlowLayout());
		JButton newServer = new JButton("new Server");
		newServer.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	createServer(6666);
            }
        });
		add(newServer);
		JButton newClient = new JButton("new Client");
		newClient.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	createClient("localhost", 6666);
            }
        });
		add(newClient);
		pack();
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	}
	public void createServer(int port) {
		ForkJoinPool.commonPool().execute(new Runnable() {
    	    public void run()
    	    {
    	    	connector = new Server(manager, port, clientLimit);
    	    }
		});
	}
	public void createClient(String host,int port) {
		ForkJoinPool.commonPool().execute(new Runnable() {
    	    public void run()
    	    {
    	    	connector = new Client(manager,host,port);
    	    }
		});
	}
	public void displayMessage(String message) {
			console.print(">>>" + message);
	}
	public void sendMessage(String message) {
		connector.sendString("[" + name + "]" + message);
	}

	public boolean isConnected() {
		if(connector != null) {
			if(connector.isConnected()) {
				return true;
			}
		}
		return false;
	}
}
