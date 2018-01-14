package network;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ForkJoinPool;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;

import checkers.GameLogic.Situations;
import checkers.Move;
import checkers.Player;
import gui.Console;
import gui.GUI;
/**
 * Manages all network activities of this program instance.
 */
public class NetworkManager{
	/**
	 * All types of info that can be send.
	 */
	public enum InfoType{CONNECTIONINFO,MESSAGE,MOVE,GAMEREQUEST,GAMERESPONSE, STATUS}
	public enum StartsFirst{CLIENT, SERVER};
	public String user = "User";
	public Connector connector;
	private NetworkManager manager;
	private GUI gui;
	public Console console;
	private int clientLimit;
	private GameRequest request;
	
	private Player selectedPlayer;
	public boolean runningOnlineGame;
	public NetworkManager(GUI gui, Console console) {
		//only human player available
		selectedPlayer = gui.playfieldpanel;
		manager = this;
		this.gui = gui;
		this.console = console;
		runningOnlineGame = false;
	}
	public void createServer(int port) {  
		if(!isConnected()) {
	    	JFrame serverOptions = new JFrame("ServerOption");
	    	serverOptions.setLayout(new FlowLayout());
	    	JSpinner spinner = new JSpinner();
	    	spinner.setValue(6000);
	    	serverOptions.add(spinner);
	    	JButton okButton = new JButton("confirm");
	    	okButton.addActionListener(new ActionListener()
	        {
	            public void actionPerformed(ActionEvent event)
	            {
	            	serverOptions.setVisible(false);
	            	ForkJoinPool.commonPool().execute(new Runnable() {
	            		public void run()
	            	    {
	            			connector = new Server(manager, (int)spinner.getValue(), clientLimit);
	            	    }
	    			});
	            }
	        });
	    	serverOptions.add(okButton);
	    	serverOptions.pack();
	    	serverOptions.setVisible(true);
		}
		else {
		 console.printInfo("You are already connected!","NetworkManager");
		}
	}
	public void createClient(String host,int port) {
		if(!isConnected()) {
			ForkJoinPool.commonPool().execute(new Runnable() {
	    	    public void run()
	    	    {
	    	    	JFrame clientOptions = new JFrame("ClientOption");
	    	    	clientOptions.setLayout(new FlowLayout());
	    	    	JSpinner spinner = new JSpinner();
	    	    	spinner.setValue(6000);
	    	    	clientOptions.add(spinner);
	    	    	JTextField ip = new JTextField();
	    	    	ip.setText("                                 ");
	    	    	clientOptions.add(ip);
	    	    	JButton okButton = new JButton("confirm"); 
	    	    	okButton.addActionListener(new ActionListener()
	    	        {
	    	            public void actionPerformed(ActionEvent event)
	    	            {
	    	            	if(ip.getText().equals("")) {
	    	            		connector = new Client(manager, "localhost",(int)spinner.getValue());
	    	            	}
	    	            	else {
	    	            		connector = new Client(manager,(int)spinner.getValue(),ip.getText());
	    	            	}
	    	            	clientOptions.setVisible(false);
	    	            }
	    	        });
	    	    	clientOptions.add(okButton);  	    	
	    	    	clientOptions.setVisible(true);
	    	    	clientOptions.pack();
	    	    }
			});
		}
		else {
		 console.printInfo("You are already connected!","NetworkManager");
		}
	}
	public void displayMassage(String message) {
			console.print(">>>" + message);
	}
	public void sendMessage(String message) {
		connector.sendInfo(new Info(InfoType.MESSAGE, "[" + user + "]" + message + ""));
	}
	public void closeConnection() {
		if(isConnected()) {
			if(runningOnlineGame) {
				runningOnlineGame = false;
				
				gui.getGameLogic().finishGame(Situations.STOP, true);
			}
			connector.sendInfo(new Info(InfoType.STATUS,"disconnected"));
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			connector.closeConnection();
		}
		else {
			console.printInfo("There is no connection to close.", "NetworkManager");
		}
	}
	public boolean isConnected() {
		if(connector != null) {
			if(connector.isConnected()) {
				return true;
			}
		}
		return false;
	}
	public void interpretInfo(Info readObject) {
		Object object = readObject.getObject();
		switch(readObject.getInfoType()) {
		case CONNECTIONINFO:
			connector.connectedUser = (String) object;
			break;
		case GAMEREQUEST:
			new GameRequestWindow(this,(GameRequest) object);			
			break;
		case GAMERESPONSE:
			answerEvaluation((String) object);
			break;
		case MESSAGE:			
			displayMassage((String) object);
			break;
		case MOVE:
			gui.getGameLogic().makeMove((Move) object);
			break;
		case STATUS:
			console.printInfo("Connection was close by the other Socket.");
			closeConnection();
			break;
		}
	}
	public void sendGameRequest() {
		if(connector != null) {
			if(connector.isConnected()) {
					
				if(!gui.getGameLogic().isInProgress()) {	
					//nur zu testzwecken schon vorbestimmt
					StartsFirst startingPlayer = StartsFirst.CLIENT;
					int rounds = 1;
					
					
					if(isConnected()) {
						request = new GameRequest(startingPlayer,rounds);
						connector.sendInfo(new Info(InfoType.GAMEREQUEST, request));
						console.printInfo("Request was send. Waiting for answer...", "NetworkManager");		
					}
				}
				else {
					console.printInfo("You are ingame right now.", "NetworkManager");
				}
			}
			else {
				console.printInfo("You are not connected.", "NetworkManager");
			}
		}
		else {
			console.printInfo("You are not connected.", "NetworkManager");
		}
	}
	public void sendAnswerToGameRequest(String response) {
		if(isConnected()) {
			connector.sendInfo(new Info(InfoType.GAMERESPONSE, response));
		}
		else {
			console.printInfo("You are not connected.", "NetworkManager");
		}
	}
	public void answerEvaluation(String accepted) {
		if(connector.isConnected()) {
			if(accepted.equals("Yes")) {
				console.printInfo("Request was accepted. Stopping current game and starting now a game against" + connector.connectedUser, "NetworkManager");
				startGame();
			}
		}
		console.printInfo("You are not connected.", "NetworkManager");
	}
	public void setGameRequest(GameRequest request) {
		this.request = request;
	}
	public void startGame() {
		runningOnlineGame = true;
		Player playerRed;
		Player playerWhite;
		if(request.getStartingPlayer() == StartsFirst.CLIENT && connector.getClass().equals(Client.class)
				|| request.getStartingPlayer() == StartsFirst.SERVER && connector.getClass().equals(Server.class)){		
			playerRed = selectedPlayer;
			playerWhite = new NetworkPlayer(manager);
		}
		else {
			playerRed = new NetworkPlayer(manager);
			playerWhite = selectedPlayer;
		}
		ForkJoinPool.commonPool().execute(new Runnable() {
    	    public void run()
    	    {
    	    	if(gui.getGameLogic().isInProgress()) {
    	    		gui.getGameLogic().finishGame(Situations.STOP,false);
    	    	}
    	    	gui.getGameLogic().startGame("Online Game", playerRed, playerWhite, request.getRounds(), 1000, true, false);	
    	    }
	    });  	
	}

	public void sendMove(Move m) {
		connector.sendInfo(new Info(InfoType.MOVE,m));		
	}		


	public void changeUsername() {
		JFrame userNameWindow = new JFrame("Username"); 
		userNameWindow.setLayout(new GridLayout(3,1));
	//	userNameWindow.setDefaultCloseOperation(EXIT_ON_CLOSE);
		userNameWindow.setSize(new Dimension(100,100));
		JLabel label = new JLabel("Username:");
		label.setBackground(Color.WHITE);
		userNameWindow.add(label);
		JTextField usernameField = new JTextField();
		usernameField.setBackground(Color.WHITE);
		usernameField.setText(user);
		userNameWindow.add(usernameField);
		JButton confirm = new JButton("confirm");
		confirm.setBackground(Color.WHITE);
		confirm.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	user = usernameField.getText();
            	userNameWindow.dispose();
            }
        });
		userNameWindow.add(confirm);
		userNameWindow.setVisible(true);
	}
	public String getUser() {
		return user;
	}
}
