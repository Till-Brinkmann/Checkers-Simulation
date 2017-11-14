package checkers;

import java.io.IOException;

import checkers.Figure.FigureColor;

import gui.Console;
import gui.PlayfieldPanel;

public class Game {
	public enum Situations{WHITEWIN, REDWIN, DRAW,NOTHING, STOP};
	private Console console;
	private Playfield playfield;
	private boolean twoPlayerMode;
	private boolean recordGameIsEnabled;
	private boolean pause;
	private boolean redFailedOnce = true;
	private boolean whiteFailedOnce = true;
	private String gameName;
	private Player playerWhite;
	private Player playerRed;
	private int slowness;
	private int turnCount;
	private String namePlayerWhite;
	private String namePlayerRed;
	private FigureColor inTurn;
	private Situations endSituation;
	private boolean failed;
	
	public Game(Console console, Player player1, Player player2, int slowness, String gameName, boolean displayActivated,  boolean recordGameIsEnabled, FigureColor startingPlayer, Playfield playfield) {
		turnCount = 0;
		
		this.playfield = playfield;
		this.console = console;
		this.recordGameIsEnabled = recordGameIsEnabled;
		this.gameName = gameName;
		this.slowness = slowness;		
		twoPlayerMode = player1 == player2;
		switch(startingPlayer) {
		case RED:
			redStarts(player1,player2);
			break;
		case WHITE:
			whiteStarts(player1,player2);
			break;
		default:
			if(Math.random() < 0.5){
				redStarts(player1,player2);
			}
			else {
				whiteStarts(player1,player2);
			}
			break;
		}
		playerRed.prepare(FigureColor.RED);
		//prepare only needs to be called once for Red then
		if(!twoPlayerMode){
			playerWhite.prepare(FigureColor.WHITE);
		}
		//red always starts
		console.printInfo("gmlc", "Therefore " + namePlayerRed + "starts first");
		inTurn = FigureColor.RED;
		if(!(playerRed instanceof PlayfieldPanel)) {
			try {
				Thread.sleep(slowness);
			} catch (InterruptedException e) {
				console.printWarning("");
				e.printStackTrace();
			}
		}
		playerRed.requestMove();
	}
	private void redStarts(Player player1, Player player2) {
		playerRed = player1;		
		playerWhite = player2;
		namePlayerRed = player1.getName();
		namePlayerWhite = player2.getName();
		console.printInfo("gmlc","The White pieces have been assigned to " + player2.getName() + "");
		console.printInfo("gmlc","The red pieces have been assigned to " + player1.getName() + "");
	}
	private void whiteStarts(Player player1, Player player2) {
		playerWhite = player1;		
		playerRed = player2;
		namePlayerRed = player2.getName();
		namePlayerWhite = player1.getName();
		console.printInfo("gmlc","The White pieces have been assigned to " + player1.getName() + "");
		console.printInfo("gmlc","The red pieces have been assigned to " + player2.getName() + "");
	}
	public void makeMove(Move m){
		if(!(playfield.field[m.getX()][m.getY()].color == inTurn) || !GameLogic.testMove(m, playfield)){
			console.printWarning("Invalid move!", "Gamelogic");
			if(inTurn == FigureColor.RED){
				if(redFailedOnce){
					finishGameTest(Situations.WHITEWIN,true);
				}
				else {
					redFailedOnce = true;
				}
			}
			else {
				if(whiteFailedOnce){
					finishGameTest(Situations.REDWIN,true);

				}
				else {
					whiteFailedOnce = true;
				}
			}
		}
		else {//move is valid			
			playfield.executeMove(m);
			//automatic figureToKing check
			GameLogic.testFigureToKing(playfield);
			//test if game is Finished
			Situations gamestate = testFinished();
			if(gamestate != Situations.NOTHING){
				finishGameTest(gamestate,false);
			}
			else {
				//for game recording
				if(recordGameIsEnabled) {
					infosGameRecording();
				}
				//changing turn
				turnCount++;
				inTurn = (inTurn == FigureColor.RED) ? FigureColor.WHITE : FigureColor.RED;
				if(!pause) {
					moveRequesting();
				}
			}
		
		}
	}
	private void moveRequesting() {
		switch(inTurn){
			case RED:
				if(!(playerRed  instanceof PlayfieldPanel)) {
					try {
						Thread.sleep(slowness);
					} catch (InterruptedException e) {
						console.printWarning("");
						e.printStackTrace();
					}
				}
				playerRed.requestMove();
				break;
			case WHITE:
				if(!(playerWhite  instanceof PlayfieldPanel)) {
					try {
						Thread.sleep(slowness);
					} catch (InterruptedException e) {
						console.printWarning("");
						e.printStackTrace();
					}
				}
				playerWhite.requestMove();
				break;
		}
	}
	private Situations testFinished(){
		//red has to make the next move. So if Red has just moved it does not need to move in the next round
		if(inTurn == FigureColor.WHITE && Move.getPossibleMoves(FigureColor.RED, playfield).length == 0) {
			return Situations.WHITEWIN;
		}
		if(inTurn == FigureColor.RED && Move.getPossibleMoves(FigureColor.WHITE, playfield).length == 0) {
			return Situations.REDWIN;
		}
		
		//test for draw Situation
		if(playfield.getMovesWithoutJumps() == 30) {
			requestDraw();
		}
		
		
		if(playfield.getMovesWithoutJumps() == 60) {
			return Situations.DRAW;
		}
		return Situations.NOTHING;
	}
	public void finishGameTest(Situations end, boolean pFailed) {
		failed = pFailed;
		endSituation = end;
		switch(end) {
		case DRAW:
			console.printInfo("GameLogic", "Game is finished!");
			console.printInfo("GameLogic", "Result: Draw!");		
			break;

		case REDWIN:
			console.printInfo("GameLogic", "Game is finished!");
			if(failed) {
				console.printInfo("GameLogic", playerWhite.getName() +"(White) did a wrong move!");
			}
			
			console.printInfo("GameLogic", "Result: "+ playerRed.getName() +"(Red) won the game!");
			break;
		case WHITEWIN:
			console.printInfo("GameLogic", "Game is finished!");
			if(failed) {
				console.printInfo("GameLogic", playerRed.getName() +"(Red) did a wrong move!");
			}
			console.printInfo("GameLogic", "Result: "+ playerWhite.getName() +"(White) won the game!");
			break;
		case STOP:
			console.printInfo("GameLogic", "Game was stoped");
			break;
		case NOTHING:
			return;
		}
	}
	public void requestDraw(){
		if(playerRed.acceptDraw() && playerWhite.acceptDraw()){
			finishGameTest(Situations.DRAW,false);
		}
	}
	private void infosGameRecording() {
		try {
			playfield.saveGameSituation(gameName, inTurn, turnCount, playerRed.getName(), playerWhite.getName());
		} catch (IOException e) {
			console.printWarning("playfield could not be saved. IOException: " + e);
			e.printStackTrace();
		}
	}
	 public void setPause(boolean b) {
		pause = b;
		if(b == false) {			
			moveRequesting();
		}
	 }
	public Situations getFinalSituation() {
		return endSituation;
		
	}
	public boolean getFailed() {
		return failed;
	}
	public int getTurnCount() {
		return turnCount;
	}
	public void setSlowness(int slowness) {
		this.slowness = slowness;
	}
	public Playfield getPlayfield(){
		return playfield;
	}
	public boolean getTwoPlayerMode(){
		return twoPlayerMode;
	}
	public Player getPlayerRed() {
		return playerRed;
	}
	public Player getPlayerWhite() {
		return playerWhite;
	}
}
