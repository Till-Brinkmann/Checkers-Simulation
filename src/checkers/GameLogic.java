package checkers;

import java.io.IOException;

import checkers.Figure.FigureColor;
import checkers.Figure.FigureType;
import checkers.Move.MoveDirection;
import checkers.Move.MoveType;
import checkers.Player;
import checkers.Figure.FigureColor;
import gui.GUI;

/**
 * provides methods for game logic
 * @author Till
 *
 */
public class GameLogic {
	
	/**
	 * the default playfield to use
	 */
	private Playfield field;
	
	private Player playerWhite;
	private Player playerRed;
	private boolean redFailedOnce = false;
	private boolean whiteFailedOnce = false;
	
	private FigureColor inTurn;
	private GUI gui;
	
	public GameLogic(){
		this(new Playfield());
	}
	/**
	 * 
	 * @param playfield default playfield
	 */
	public GameLogic(Playfield playfield) {
		field = playfield;
	}
	//---methods for game process---
	public void startGame(Player player1, Player player2){
		//reset variables
		redFailedOnce = false;
		whiteFailedOnce = false;
		
		//choose random beginner
		if(Math.random() < 0.5){
			playerWhite = player1;
			playerRed = player2;
		}
		else {
			playerWhite = player2;
			playerRed = player1;
		}
		try {
			field.createStartPosition();
		} catch (IOException e) {
			gui.console.printWarning(
					"Could not load startposition. Please check if your playfieldsaves are at the right position",
					"Gamelogic:startGame");
			return;
		}
		playerRed.prepare(FigureColor.RED);
		playerWhite.prepare(FigureColor.WHITE);
		//red always starts
		inTurn = FigureColor.RED;
		playerRed.requestMove();
	}
	public void makeMove(Move m) {
		//TODO testen ob es einen gewinner gibt und bestimmt noch irgendwas anderes. Außerdem muss man irgendwas gegen patt Situationen tun
		//test if the move is valid
		if(!(field.field[m.getX()][m.getY()].color == inTurn) || !testMove(m)){
			gui.console.printWarning("Invalid move!", "Gamelogic");
			if(inTurn == FigureColor.RED){
				if(redFailedOnce){
					//TODO das spiel muss abgebrochen werden und white hat gewonnen
				}
				else {
					redFailedOnce = true;
				}
			}
			else {
				if(whiteFailedOnce){
					//TODO white wird disqualifiziert
				}
				else {
					whiteFailedOnce = true;
				}
			}
		}
		else {//move is valid
			field.executeMove(m);
			inTurn = (inTurn == FigureColor.RED) ? FigureColor.WHITE : FigureColor.RED;
			switch(inTurn){
			case RED:
				playerRed.requestMove();
				break;
			case WHITE:
				playerWhite.requestMove();
				break;
			}
		}
	}
	//---
	
	/**
	 * tests if the given move is possible on the given playfield
	 * @param move
	 * @param field
	 * @return false if move is not possible
	 */
	public static boolean testMove(Move m, Playfield f){
		int x = m.getX();
		int y = m.getY();
		FigureColor color = f.field[x][y].getFigureColor();
		if(m.getMoveType() == MoveType.INVALID){
			return false;
		}
		if(!f.isOccupied(x, y)){
			return false;
		}
		
		switch(m.getMoveType()){
		case STEP:
			if(f.getType(x,y) == FigureType.NORMAL){
				switch (f.colorOf(m.getX(), m.getY())) {
				case RED:
					switch(m.getMoveDirection()){
					case BL:
					case BR:
						//normal red figures can not go backwards
						return false;
					case FL:
						//the field is not outside the playfield and is not occupied
						//so the step is possible otherwise not
						return (x - 1 >= 0 && y + 1 < f.SIZE && !f.isOccupied(x-1, y+1));
					case FR:
						return (x + 1 < f.SIZE && y + 1 < f.SIZE && !f.isOccupied(x+1, y+1));
					}
				case WHITE:
					switch(m.getMoveDirection()){
					case BL:
						return (x - 1 >= 0 && y - 1 >= 0 && !f.isOccupied(x-1, y-1));
					case BR:
						return (x + 1 < f.SIZE && y - 1 >= 0 && !f.isOccupied(x+1, y-1));
					case FL:
					case FR:
						//normal white figures can not go forwards
						return false;
					}
				}
			}
			else {//FT = King
				switch(m.getMoveDirection()){
				case BL:
					return (x - 1 >= 0 && y - 1 >= 0 && !f.isOccupied(x-1, y-1));
				case BR:
					return (x + 1 < f.SIZE && y - 1 >= 0 && !f.isOccupied(x+1, y-1));
				case FL:
					return (x - 1 >= 0 && y + 1 < f.SIZE && !f.isOccupied(x-1, y+1));
				case FR:
					return (x + 1 < f.SIZE && y + 1 < f.SIZE && !f.isOccupied(x+1, y+1));
				}
			}
		case JUMP:
			break;
		case MULTIJUMP:
			break;
		}
		if(f.field[x][y].getFigureType() == FigureType.NORMAL && m.getSteps() < 0)
		for(int i = 0; i < m.getSteps();i++){
			if(m.getMoveDirection() == MoveDirection.FR){
				x++;
				y++;
			}
			if(m.getMoveDirection() == MoveDirection.FL){
				x--;
				y++;
			}
			if(m.getMoveDirection() == MoveDirection.BR){
				x++;
				y--;
			}
			if(m.getMoveDirection() == MoveDirection.BL){
				x--;
				y--;
			}
			if(f.field[x][y] != null){
				//if()
			}
		}
		return true;
	}
	public boolean testMove(Move move){
		return testMove(move, field);
	}
	public static boolean testForMultiJump(int x, int y, Playfield f){
		
		return true;
	}
	public boolean testForMultiJump(int x, int y){
		return testForMultiJump(x,y, field);
	}
	
	public Move[] getPossibleMoves(FigureColor color){
		return new Move[0];
	}
	
	public Playfield getPlayfield(){
		return field;
	}
	public void setPlayfield(Playfield f){
		field = f;
	}
	/*
	 * TODO vielleicht garnicht nötig
	 */
	public void linkGUI(GUI gui) {
		this.gui = gui;
	}
}
