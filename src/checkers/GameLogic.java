package checkers;

import checkers.Player;
import checkers.Player.FigureColors;
import gui.GUI;

/**
 * provides methods for game logic
 * @author Till
 *
 */
public class GameLogic {
	
	public static enum Moves{
		INVALID,
		STEP,
		JUMP,
		MULTI_JUMP
	};
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
	/**
	 * tests if the given move is possible on the given playfield
	 * @param move
	 * @param field
	 * @return Moves.INVALID if not possible
	 */
	public static Moves testMove(Move m, Playfield f){
		return Moves.INVALID;
	}
	public Moves testMove(Move move){
		return testMove(move, field);
	}
	
	public Move[] getPossibleMoves(FigureColors color){
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
