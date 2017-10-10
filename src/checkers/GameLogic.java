package checkers;

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
	public void startGame(){
		
	}
	public void makeMove(Move m) {
		//TODO test move and all the other things that are need to be done here
	}
	//---
	
	/**
	 * tests if the given move is possible on the given playfield
	 * @param move
	 * @param field
	 * @return Moves.INVALID if not possible
	 */
	public static boolean testMove(Move m, Playfield f){
		int x = m.getX();
		int y = m.getY();
		FigureColor color = f.field[x][y].getFigureColor();
		if(m.getMoveType() == MoveType.INVALID){
			return false;
		}
		if(f.field[x][y] == null){
			return false;
		}
		if(f.field[x][y].getFigureType() == FigureType.NORMAL && Math.abs(m.getSteps()) > 2){
			return false;
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
