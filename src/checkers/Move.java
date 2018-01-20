package checkers;

import java.io.Serializable;

import checkers.Figure.FigureColor;
import checkers.Figure.FigureType;
import datastructs.List;

/**
 * A class to save one particular Move. A move consists of many different variable which are all saved in this class. Furthermore,
 * it includes all static methods for selecting possible moves.
 * <p>
 * It is neccessary to know that in our definition of a move a move also contains a mulitjump which consists of various directions
 * which are saved in the global array directions. So even if a mulitjump consist of many subcomponents which could be also seen
 * as a move, it is one move.
 * @author Till
 * @author Marco
 */
public class Move implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * All possible types of a move
	 */
	public static enum MoveType{
		STEP,
		JUMP,
		MULTIJUMP,
		INVALID
	};
	/**
	 * All possible directions a Figure can move.
	 */
	public static enum MoveDirection{
		FR, // Forward/Right
		FL, // Forward/Left
		BR, // Backward/Right
		BL, // Backward/Left
	};
	private MoveType type;
	private MoveDirection[] directions;
	private int steps;

	private int x, y;
	
	public static final Move INVALID = new Move(MoveType.INVALID);
	
	/**
	 * This is the constructor with the most input parameters which are all important in order to have all information. It is suitable for
	 * multijumps, because you transfer an array of direction.
	 * <p>
	 * @param directions	An array of variables from the enumeration MoveDirection.
	 * @param type	The type of the move(from enum MoveType)
	 * @param x	An integer variable which is representing a point on the vertical axis of the playfield.
	 * @param y	An integer variable which is representing a point on the horizontal axis of the playfield.	
	 */
	public Move(MoveDirection[] directions, MoveType type, int x, int y) {
		this.directions = new MoveDirection[12];
		int step = 0;
		for(; step < directions.length; step++){
			if(directions[step] == null) break;
			this.directions[step] = directions[step];
		}
		steps = step;
		this.x = x;
		this.y = y;
		this.type = steps > 1 ? MoveType.MULTIJUMP : type;
	}
	public Move(MoveDirection direction, MoveType type, int x, int y){
		this(new MoveDirection[]{direction}, type, x, y);
	}
	/**
	 * This constructor is only used for invalid moves
	 * <p>
	 * @param type should only be MoveType.INVALID
	 */
	private Move(MoveType type){
		this.type = type;
	}
	
	/**
	 * The method basically duplicates the object move. As a result the move returned by this method will describe the exact same movement
	 * on the playfield.
	 * <p>
	 * @return	A new move object.
	 */
	public Move copy(){
		return new Move(directions, type, x, y);
	}
	
	/**
	 * A step is being added to an already existing move. It inserts a new variable from the enumeration MoveDirection into the global
	 * moveDirection array. This is needed when a move is going to be a multijump in which more than one direction is possible.   
	 * <p>
	 * @param dir     A variables from the enumeration MoveDirection .    
	 */
	public void addStep(MoveDirection dir){
		//new direction is added and AFTERWARDS steps is incremented
		directions[steps++] = dir;
		if(steps > 1) type = MoveType.MULTIJUMP;
	}
	
	/**
	 * This methods sets the type of the move.
	 * <p>
	 * @param pType       A variable form the enumeration MoveType.
	 */
	public void setMoveType(MoveType pType){
		type = pType;
	}
	/**
	 * This method returns the type of this move object.
	 * <p>
	 * @return         A variable form the enumeration MoveType.
	 */
	public MoveType getMoveType(){
		return type;
	}
	/**
	 * Returns a certain direction from the directions array which saves all direction of a move successively.
	 * <p>
	 * @param step     	 An Integer variable which represents one of the subcomponent of a move.
	 * @return           A variable from the enumeration MoveDirection.
	 */
	public MoveDirection getMoveDirection(int step){
		return directions[step];		
	}
	/**
	 * Returns the first direction of the direcions array. It is suitable for steps and jumps that do not involve more than one direction 
	 *  <p>
	 * @return       A variable from the enumeration MoveDirection.
	 */
	public MoveDirection getMoveDirection(){
		return directions[0];
	}
	/**
	 * Returns the number of the move components one jump has.(jumps and steps have always only one)
	 * <p>
	 * @return      A integer variable which saves the amount of move components one move has.
	 */
	public int getSteps(){
		return steps;
	}
	/**
	 * Returns the x axis of the playfield from which a move is going to be performed. It is where the figure which is going to be moved
	 * was in the initial situation. 
	 * <p>
	 * @return      An integer variable which is representing a point on the horizontal axis of the playfield.
	 */
	public int getX(){
		return x;
	}
	/**
	 * Returns the y axis of the playfield from which a move is going to be performed. It is where the figure which is going to be moved
	 * was in the initial situation. 
	 * @return   	An integer variable which is representing a point on the vertical axis of the playfield.	
	 */
	public  int getY(){
		return y;
	}
	
	/**
	 * This method tests if the type of the global variable type (@see MoveType)
	 * has been set to INVALID. IF this is the case the method returns true. 
	 * <p>
	 * @return     A boolean which if true declares the move as invalid(unexecutable).
	 */
	public boolean isInvalid() {
		return type == MoveType.INVALID;
	}
	/**
	 * turns an array of coordinates into a move object
	 * !ATTENTION!:the method does not apply complete move validation!
	 * So the move could not be valid although it is not set invalid!
	 * Test with Gamelogic.testMove()!
	 * <p>
	 * @param coords     a two dimensional integer array which respresents in chronological order on which fields the figure was during
	 * 					 the move. 
	 * @return move      A Move object that represents the move described by the coordinates.
	 */
	public static Move createMoveFromCoords(int[][] coords){
		Move move = new Move(MoveType.INVALID);
		MoveDirection direction;
		int step = 0;
		int xShift;
		for(int i = 0; i < coords.length - 1; i++){
			step = coords[i+1][1] - coords[i][1];
			xShift = coords[i+1][0] - coords[i][0];
			if(Math.abs(step) == Math.abs(xShift) && (Math.abs(step) == 2 || Math.abs(step) == 1)){
				if(step < 0){
					if(xShift < 0){
						direction = MoveDirection.BL;
					}
					else {
						direction = MoveDirection.BR;
					}
				}
				else {
					if(xShift < 0){
						direction = MoveDirection.FL;
					}
					else {
						direction = MoveDirection.FR;
					}
				}
				if(i == 0){
					move = new Move(direction, Math.abs(step) == 2 ? MoveType.JUMP : MoveType.STEP, coords[0][0], coords[0][1]);
				} else {
					move.addStep(direction);
				}
			}
			else {//Move is invalid
				return new Move(MoveType.INVALID);
			}
		}
		return move;
	}
	/**
	 * @param figure the figure that should jump
	 * @param field the Playfield that is used to search for jumps
	 * @return returns every possible jump for the given Figure on the given Playfield
	 */
	public static List<Move> getPossibleJumps(Figure figure, Playfield field){
		List<Move> moves = new List<Move>();
		//used for recursive multijump testing
		Playfield tmp;
		List<Move> multiJumps;
		Move m;
		if(figure.y + 2 < field.SIZE
			&& (figure.getFigureType() == FigureType.KING || figure.getFigureColor() == FigureColor.RED)){
			if(figure.x + 2 < field.SIZE){
				if(field.isOccupied(figure.x+1, figure.y+1) 
					&& field.field[figure.x+1][figure.y+1].getFigureColor() != figure.getFigureColor()
					&& !field.isOccupied(figure.x+2, figure.y+2)){
					moves.append(new Move(MoveDirection.FR, MoveType.JUMP, figure.x, figure.y));
					moves.toLast();
					tmp = field.copy();
					tmp.executeMove(moves.get());
					multiJumps = getPossibleJumps(tmp.field[figure.x+2][figure.y+2], tmp);
					if(multiJumps.length > 0){
						multiJumps.toFirst();
						while(multiJumps.hasAccess()){
							//take the move we just created and copy it
							m = moves.get().copy();
							//append the other steps of the multijump
							for(int step = 0, steps = multiJumps.get().getSteps(); step < steps; step++){
								m.addStep(multiJumps.get().getMoveDirection(step));
							}
							m.setMoveType(MoveType.MULTIJUMP);
							//save temporarily in multiJumps
							multiJumps.set(m);
							multiJumps.next();
						}
						//remove the old single jump
						//(you can not move a single jump when you can multijump in the same move)
						moves.remove();
						//append new moves
						moves.concat(multiJumps);
					}
				}
			}
			if(figure.x - 2 >= 0){
				if(field.isOccupied(figure.x-1, figure.y+1) 
					&& field.field[figure.x-1][figure.y+1].getFigureColor() != figure.getFigureColor()
					&& !field.isOccupied(figure.x-2, figure.y+2)){
					moves.append(new Move(MoveDirection.FL, MoveType.JUMP, figure.x, figure.y));
					moves.toLast();
					tmp = field.copy();
					tmp.executeMove(moves.get());
					multiJumps = getPossibleJumps(tmp.field[figure.x-2][figure.y+2], tmp);
					if(multiJumps.length > 0){
						multiJumps.toFirst();
						while(multiJumps.hasAccess()){
							m = moves.get().copy();
							for(int step = 0, steps = multiJumps.get().getSteps(); step < steps; step++){
								m.addStep(multiJumps.get().getMoveDirection(step));
							}
							m.setMoveType(MoveType.MULTIJUMP);
							multiJumps.set(m);
							multiJumps.next();
						}
						moves.remove();
						moves.concat(multiJumps);
					}
				}
			}
		}
		if(figure.y - 2 >= 0
			&& (figure.getFigureType() == FigureType.KING || figure.getFigureColor() == FigureColor.WHITE)){
			if(figure.x + 2 < field.SIZE){
				if(field.isOccupied(figure.x+1, figure.y-1) 
					&& field.field[figure.x+1][figure.y-1].getFigureColor() != figure.getFigureColor()
					&& !field.isOccupied(figure.x+2, figure.y-2)){
					moves.append(new Move(MoveDirection.BR, MoveType.JUMP, figure.x, figure.y));
					moves.toLast();
					tmp = field.copy();
					tmp.executeMove(moves.get());
					multiJumps = getPossibleJumps(tmp.field[figure.x+2][figure.y-2], tmp);
					if(multiJumps.length > 0){
						multiJumps.toFirst();
						while(multiJumps.hasAccess()){
							m = moves.get().copy();
							for(int step = 0, steps = multiJumps.get().getSteps(); step < steps; step++){
								m.addStep(multiJumps.get().getMoveDirection(step));
							}
							m.setMoveType(MoveType.MULTIJUMP);
							multiJumps.set(m);
							multiJumps.next();
						}
						moves.remove();
						moves.concat(multiJumps);
					}
				}
			}
			if(figure.x - 2 >= 0){
				if(field.isOccupied(figure.x-1, figure.y-1) 
					&& field.field[figure.x-1][figure.y-1].getFigureColor() != figure.getFigureColor()
					&& !field.isOccupied(figure.x-2, figure.y-2)){
					moves.append(new Move(MoveDirection.BL, MoveType.JUMP, figure.x, figure.y));
					moves.toLast();
					tmp = field.copy();
					tmp.executeMove(moves.get());
					multiJumps = getPossibleJumps(tmp.field[figure.x-2][figure.y-2], tmp);
					if(multiJumps.length > 0){
						multiJumps.toFirst();
						while(multiJumps.hasAccess()){
							m = moves.get().copy();
							for(int step = 0, steps = multiJumps.get().getSteps(); step < steps; step++){
								m.addStep(multiJumps.get().getMoveDirection(step));
							}
							m.setMoveType(MoveType.MULTIJUMP);
							multiJumps.set(m);
							multiJumps.next();
						}
						moves.remove();
						moves.concat(multiJumps);
					}
				}
			}
		}
		return moves;
	}
	/**
	 * This method returns a list containing all possible multijumps from the figure standing on the given coordinates on the given playfield.
	 * <p>
	 * @param x An integer variable which is representing a point on the horizontal axis of the playfield.
	 * @param y	An integer variable which is representing a point on the vertical axis of the playfield.	
	 * @param field Field is an Object which represents the playfield.
	 * @return List This new List contains all the possible multijumps on a specific playfield situation for one figure. 
	 */
	public static List<Move> getMultiJumps(int x, int y, Playfield field) {
		List<Move> list = Move.getPossibleJumps(field.field[x][y], field);
		for(list.toFirst();list.hasAccess();list.next()) {
			if(list.get().getMoveType() != MoveType.MULTIJUMP) list.remove();
		}
		return list;
	}
	/**
	 * @param color of the figures that should be tested.
	 * @param field playfield to test on.
	 * @return Returns true if a jump with a figure of the given color on the given playfield is possible.
	 */
	public static boolean jumpIsPossible(FigureColor color, Playfield field){
		for(Figure figure : field.getFiguresFor(color)){
			if(figure.y + 2 < field.SIZE
					&& (figure.getFigureType() == FigureType.KING || figure.getFigureColor() == FigureColor.RED)){
				if(figure.x + 2 < field.SIZE){
					if(field.isOccupied(figure.x+1, figure.y+1) 
						&& field.field[figure.x+1][figure.y+1].getFigureColor() != figure.getFigureColor()
						&& !field.isOccupied(figure.x+2, figure.y+2)){
						return true;
					}
				}
				if(figure.x - 2 >= 0){
					if(field.isOccupied(figure.x-1, figure.y+1) 
						&& field.field[figure.x-1][figure.y+1].getFigureColor() != figure.getFigureColor()
						&& !field.isOccupied(figure.x-2, figure.y+2)){
						return true;
					}
				}
			}
			if(figure.y - 2 >= 0
				&& (figure.getFigureType() == FigureType.KING || figure.getFigureColor() == FigureColor.WHITE)){
				if(figure.x + 2 < field.SIZE){
					if(field.isOccupied(figure.x+1, figure.y-1) 
						&& field.field[figure.x+1][figure.y-1].getFigureColor() != figure.getFigureColor()
						&& !field.isOccupied(figure.x+2, figure.y-2)){
						return true;
					}
				}
				if(figure.x - 2 >= 0){
					if(field.isOccupied(figure.x-1, figure.y-1) 
						&& field.field[figure.x-1][figure.y-1].getFigureColor() != figure.getFigureColor()
						&& !field.isOccupied(figure.x-2, figure.y-2)){
						return true;
					}
				}
			}
		}
		return false;
	}
	/**
	 * @param f The figure that should step.
	 * @param p The Playfield that is used to search for steps.
	 * @return Returns every possible step for the given Figure on the given Playfield.
	 */
	public static List<Move> getPossibleSteps(Figure f, Playfield p){
		List<Move> moves = new List<Move>();
		if(jumpIsPossible(f.getFigureColor(), p)){
			return moves;
		}
		if(f.y + 1 < p.SIZE
				&& (f.getFigureType() == FigureType.KING || f.getFigureColor() == FigureColor.RED)){
			if(f.x + 1 < p.SIZE){
				if(!p.isOccupied(f.x+1, f.y+1)){
					moves.append(new Move(MoveDirection.FR, MoveType.STEP, f.x, f.y));
				}
			}
			if(f.x - 1 >= 0){
				if(!p.isOccupied(f.x-1, f.y+1)){
					moves.append(new Move(MoveDirection.FL, MoveType.STEP, f.x, f.y));
				}
			}
		}
		if(f.y - 1 >= 0
				&& (f.getFigureType() == FigureType.KING || f.getFigureColor() == FigureColor.WHITE)){
			if(f.x + 1 < p.SIZE){
				if(!p.isOccupied(f.x+1, f.y-1)){
					moves.append(new Move(MoveDirection.BR, MoveType.STEP, f.x, f.y));
				}
			}
			if(f.x - 1 >= 0){
				if(!p.isOccupied(f.x-1, f.y-1)){
					moves.append(new Move(MoveDirection.BL, MoveType.STEP, f.x, f.y));
				}
			}
		}
		return moves;
	}
	/**
	 * @param figure The figure that should move.
	 * @param playfield The Playfield that is used to search for moves.
	 * @return Returns every possible move for the given Figure on the given Playfield.
	 */
	public static List<Move> getPossibleMoves(Figure figure, Playfield playfield){
		return getPossibleSteps(figure, playfield).concat(getPossibleJumps(figure, playfield));
	}
	/**
	 * 
	 * @param color The figureColor of the figures to find moves for.
	 * @param playfield The Playfield that is used to search for moves.
	 * @return Returns a List with every possible move for all figures on the field that have the FigureColor color on the given Playfield.
	 */
	public static List<Move> getPossibleMoves(FigureColor color, Playfield playfield){
		List<Move> moves = new List<Move>();
		for(Figure f : playfield.getFiguresFor(color)){
			moves.concat(getPossibleMoves(f, playfield));
		}
		return moves;
	}
}


