package network;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import network.NetworkManager.StartsFirst;

public class GameRequestWindow extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	NetworkManager manager;
	GameRequest request;
	public GameRequestWindow(NetworkManager manager, GameRequest request) {
		super("GameRequest");
		this.manager = manager;
		this.request = request;
		setLayout(new FlowLayout());
		initializeComponents();
		pack();
		setVisible(true);
	}
	private void initializeComponents() {
		JLabel info = new JLabel(formatRequestInfo());
		add(info);
		JButton yes = new JButton("Yes");
		yes.setBackground(Color.WHITE);
		yes.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	manager.sendAnswerToGameRequest("Yes");
            	manager.setGameRequest(request);
            	manager.startGame();
            	setVisible(false);
            }
        });
		add(yes);
		JButton no  = new JButton("No");
		no.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	manager.sendAnswerToGameRequest("No");
            	setVisible(false);
            }
        });
		add(no);
		no.setBackground(Color.WHITE);
	}
	private String formatRequestInfo() {
		String completeString = "Info:\n Rounds: " + request.getRounds() + "\nPlayerStarting: ";
		if(manager.connector.getClass().equals(Client.class)) {
			if(request.getStartingPlayer() == StartsFirst.CLIENT) {
				completeString.concat(manager.user);
			}
			else {
				completeString.concat(manager.connector.connectedUser);
			}
		}
		else {
			if(request.getStartingPlayer() == StartsFirst.CLIENT) {
				completeString.concat(manager.connector.connectedUser);
			}
			else {
				completeString.concat(manager.user);
			}
		}
		return completeString;
	}
}
