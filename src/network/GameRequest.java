package network;

import java.io.Serializable;

import network.NetworkManager.StartsFirst;

public class GameRequest implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private StartsFirst startingPlayer;
	private int rounds;
	
	public GameRequest(StartsFirst startingPlayer, int rounds) {
		this.startingPlayer = startingPlayer;
		this.rounds = rounds;
	}
	public int getRounds() {
		return rounds;
	}
	public StartsFirst getStartingPlayer() {
		return startingPlayer;
	}
}
