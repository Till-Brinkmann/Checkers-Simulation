package checkers;

import java.io.File;
import java.io.IOException;

import checkers.Figure.FigureColor;
import gui.Console;
import gui.GUI;
import gui.GUI.AISpeed;
import gui.PlayfieldPanel;

public class GameManager {
	GUI gui;
	Console console;
	Game[] games;
	Playfield playfield;
	int winCountPlayer1;
	int winCountPlayer2;
	int drawCount;
	int currentRound;
	FigureColor startingPlayer;
	public GameManager(GUI gui, boolean recordGameIsEnabled, String gameName, Player player1, Player player2, int rounds, int slowness, boolean displayActivated) {
		this.gui = gui;
		console = gui.console;
		currentRound = 0;
		winCountPlayer1 = 0;
		winCountPlayer2 = 0;
		drawCount = 0;
		startingPlayer = null;
		testForAISpeed(player1,player2, slowness);
		gui.setEnableResume(false);
		gui.setEnablePause(true);
		gui.setEnableStop(true);
		games = new Game[rounds];
		while(currentRound < rounds) {			
			if(gui.playfieldpanel.getPlayfield() == null) {
				playfield = new Playfield();
				try {
					playfield.createStartPosition();
				} catch (IOException e) {
					console.printInfo("GameManager","Could not load starting position. Please test if startPositionForSize8.pfs is in the folder playfieldsaves");
					e.printStackTrace();
				}
			}
			else {
				playfield = gui.playfieldpanel.getPlayfield();
			}
			new Thread(){
				public void run(){
					games[currentRound] = new Game(gui.getConsole(), player1, player2, slowness, gameName, displayActivated, displayActivated,startingPlayer,playfield);
				}
			};
			switch(games[currentRound].getFinalSituation()) {
			case DRAW:
				drawCount++;
				break;
			case NOTHING:
				
				break;
			case REDWIN:
				if(player1.equals(games[currentRound].getPlayerRed())) {
					winCountPlayer1++;
				}
				else {
					winCountPlayer2++;
				}
				break;
			case STOP:
				console.printInfo("Game was terminated. All information is lost... FOREVER!");
				reset();
				return;
			case WHITEWIN:
				if(player1.equals(games[currentRound].getPlayerWhite())) {
					winCountPlayer1++;
				}
				else {
					winCountPlayer2++;
				}
				break;
			}
			try {
				playfield.loadGameSituation(new File("resources/playfieldSaves/noFigures.pfs"));
			} catch (IOException e) {
				console.printInfo("GameManager","Could not load the pfs. Please test if noFigures.pfs is in the folder playfieldsaves");
				e.printStackTrace();
			}
		}		
		gatherInformationFromGames(games, player1, player2);
		reset();
	}
	private void gatherInformationFromGames(Game[] games,Player player1, Player player2) {
		
		gui.console.printInfo("The AI" + player1.getName() + " won " + winCountPlayer1 + " times.","GameLogic");
		gui.console.printInfo( "The AI" + player2.getName() + " won " + winCountPlayer2 + " times.","GameLogic");
		gui.console.printInfo("Draw: " + drawCount + " times.", "GameLogic");
		
		
	}
	private void reset() {
		try {
			playfield.loadGameSituation(new File("resources/playfieldSaves/noFigures.pfs"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		gui.setAISpeed(AISpeed.NOTACTIVE);
		gui.setEnableResume(false);
		gui.setEnablePause(false);
		gui.setEnableStop(false);
		currentRound = 0;
	}
	private void testForAISpeed(Player player1, Player player2, int slowness) {
		if(player1 instanceof PlayfieldPanel && player2 instanceof PlayfieldPanel) {
			gui.setAISpeed(AISpeed.NOTACTIVE);
		}
		else {
			if(slowness == 0) {
				gui.setAISpeed(AISpeed.FAST);
			}
			else if(slowness == 1000) {
				gui.setAISpeed(AISpeed.MEDIUM);
			}
			else {
				gui.setAISpeed(AISpeed.SLOW);
			}
		}
	}
	public Game getGame() {
		return games[currentRound];
	}
}
