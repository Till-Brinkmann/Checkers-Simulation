package checkers;

import checkers.Figure.FigureColor;
import checkers.Figure.FigureType;
import generic.List;

/**
 * A class to save one particular Move. A move consists of many different variable which are all saved in this class. Furthermore,
 * it includes all static methods for selecting possible moves.
 * <p>
 * It is neccessary to now that in our definition of a move a move also contains a mulitjump which consist of various directions
 * which are saved in the global array directions. So even if a mulitjump consist of many subcomponents which could be also seen
 * as a move, it is one move!
 * @author Till
 * @author Marco
 */
public class Move {

	public static enum MoveType{
		STEP,
		JUMP,
		MULTIJUMP,
		INVALID
	};
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
	 * This is the constructor with the biggest amount of input parameters which are all important in order to have all information. It is suitable for
	 * multijumps, because you transfer an array of direction.
	 * <p>
	 * @param pDirection         An array of variables from the enumeration MoveDirection .
	 * @param pSteps		     This Integer is representing the number of subcomponents of a comlex move like a mulitjump
	 * @param pX                  An integer variable which is representing a point on the vertical axis of the playfield.
	 * @param pY	 	             An integer variable which is representing a point on the horizontal axis of the playfield.	
	 */
	public Move(MoveDirection[] pDirection, int pSteps, int pX, int pY) {
		directions = pDirection;
		steps = pSteps;
		x = pX;
		y = pY;
		if(pSteps > 1){
			type = MoveType.MULTIJUMP;
		}
	}
	/**
	 * This constructor is only suitable for move that do not invole a jump in general, because it only needs one direction. It does not
	 * specify the move further. It lets open what moveType(JUMP or STEP) this move is.
	 * <p>
	 * @param pDirection		A variables from the enumeration MoveDirection.
	 * @param pX                An integer variable which is representing a point on the vertical axis of the playfield.
	 * @param pY	 	        An integer variable which is representing a point on the horizontal axis of the playfield.	
	 */
	public Move(MoveDirection pDirection, int pX, int pY){
		this(new MoveDirection[4], 0, pX, pY);
		addStep(pDirection);
	}
	/**
	 * This constructor is only suitable for move that do not invole a multijump, because it needs only one direction.
	 * <p>
	 * @param pDirection		A variables from the enumeration MoveDirection.
	 * @param pType				A variable form the enumeration MoveType.
	 * @param pX                An integer variable which is representing a point on the vertical axis of the playfield.
	 * @param pY	 	        An integer variable which is representing a point on the horizontal axis of the playfield.	
	 */
	public Move(MoveDirection pDirection, MoveType pType, int pX, int pY){
		this(pDirection, pX, pY);
		type = pType;
	}
	/**
	 * Here the move is only defined by the type. Everything else is left open.
	 * <p>
	 * @param pType             A variable form the enumeration MoveType.
	 */
	public Move(MoveType pType) {
		type = pType;
	}
	/**
	 * The method basicly dupicates the object move. As a result the move returned by this method will describe the exact same movement
	 * on the playfield.
	 * <p>
	 * @return         A new move object.
	 */
	public Move copy(){
		return new Move(directions, steps, x, y);
	}
	/**
	 * A step is being added to a already existing move. It inserts a new varible from the enumeration MoveDirection into the a global
	 * moveDirection array. This is needed when a move is going to be a multijump in which obviously more than one direction is possible.   
	 * <p>
	 * @param dir     A variables from the enumeration MoveDirection .    
	 */
	public void addStep(MoveDirection dir){
		if(steps + 1 > directions.length){
			MoveDirection[] tmp = new MoveDirection[steps+4];
			for(int i = 0; i < directions.length; i++){
				tmp[i] = directions[i];
			}
			directions = tmp;
		}
		directions[steps++] = dir;
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
	 * Returns the x axis of the playfield form which a move is going to be performed. It is where the the figure which is going to be moved
	 * was in the initial situation. 
	 * <p>
	 * @return      An integer variable which is representing a point on the vertical axis of the playfield.
	 */
	public int getX(){
		return x;
	}
	/**
	 * Returns the y axis of the playfield form which a move is going to be performed. It is where the the figure which is going to be moved
	 * was in the initial situation. 
	 * @return   	An integer variable which is representing a point on the horizontal axis of the playfield.	
	 */
	public  int getY(){
		return y;
	}
	/**
	 * Turns two pairs of coordinates into a specific move object.
	 * <p>
	 * !ATTENTION!:the method does not apply complete move validation!
	 * So the move could not be valid although it is not set invalid!
	 * test with Gamelogic.testMove()!
	 * <p>
	 * @param coords     a two dimensional integer array which respresents in chronological order on which fields the figure was during
	 * 					 the move. 
	 * @return move      A object that represents the move described by the coordinates. 
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
					move = new Move(direction, coords[0][0], coords[0][1]);
				} else {
					move.addStep(direction);
				}
			}
			else {//Move is invalid
				return new Move(MoveType.INVALID);
			}
		}
		//then it is a jump
		if(Math.abs(step) == 2){
			//the move must be a multijump if it has 3 or more coordinate pairs
			if(coords.length > 2){
				move.setMoveType(MoveType.MULTIJUMP);
			} else {
				move.setMoveType(MoveType.JUMP);
			}
		} else {
			move.setMoveType(MoveType.STEP);
		}
		return move;
	}
	/**
	 * 
	 * @param figure
	 * @param field
	 * @return
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
					multiJumps.toFirst();
					if(multiJumps.length > 0){
						while(multiJumps.hasAccess()){
							//take the move we just created and copy it
							m = moves.get().copy();
							//append the other steps of the multijump
							for(int steps = 0; steps < multiJumps.get().getSteps(); steps++){
								m.addStep(multiJumps.get().getMoveDirection(steps));
								m.setMoveType(MoveType.MULTIJUMP);
							}
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
					multiJumps.toFirst();
					if(multiJumps.length > 0){
						while(multiJumps.hasAccess()){
							m = moves.get().copy();
							for(int steps = 0; steps < multiJumps.get().getSteps(); steps++){
								m.addStep(multiJumps.get().getMoveDirection(steps));
								m.setMoveType(MoveType.MULTIJUMP);
							}
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
					multiJumps.toFirst();
					if(multiJumps.length > 0){
						while(multiJumps.hasAccess()){
							m = moves.get().copy();
							for(int steps = 0; steps < multiJumps.get().getSteps(); steps++){
								m.addStep(multiJumps.get().getMoveDirection(steps));
								m.setMoveType(MoveType.MULTIJUMP);
							}
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
					multiJumps.toFirst();
					if(multiJumps.length > 0){
						while(multiJumps.hasAccess()){
							m = moves.get().copy();
							for(int steps = 0; steps < multiJumps.get().getSteps(); steps++){
								m.addStep(multiJumps.get().getMoveDirection(steps));
								m.setMoveType(MoveType.MULTIJUMP);
							}
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
	 * 
	 * @param f
	 * @param p
	 * @return
	 */
	public static List<Move> getPossibleSteps(Figure f, Playfield p){
		List<Move> moves = new List<Move>();
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
	 * 
	 * @param figure
	 * @param playfield
	 * @return
	 */
	public static List<Move> getPossibleMoves(Figure figure, Playfield playfield){
		List<Move> jumps = getPossibleJumps(figure, playfield);
		if(jumps.length == 0){
			return getPossibleSteps(figure, playfield);
		}
		else {
			return jumps;
		}
	}
	/**
	 * 
	 * @param color
	 * @param playfield
	 * @return
	 */
	public static List<Move> getPossibleMoves(FigureColor color, Playfield playfield){
		List<Move> moves = new List<Move>();
		for(Figure f : playfield.getFiguresFor(color)){
			moves.concat(getPossibleMoves(f, playfield));
		}
		List<Move> jumps = new List<Move>();
		moves.toFirst();
		while(moves.hasAccess()){
			if(moves.get().getMoveType() != MoveType.STEP){
				jumps.append(moves.get());
				jumps.remove();
			}
			moves.next();
		}
		if(jumps.length > 0){
			return jumps;
		}
		return moves;
	}
	/**
	 * This method tests if the type of the global variable from the enumeration type, which provides more details about the move, if it
	 * has been set to INVALID. IF this is the case the method returns true. 
	 * <p>
	 * @return     A boolean which if true declares the move as invalid(unexecutable).
	 */
	public boolean isInvalid() {
		return type == MoveType.INVALID;
	}
}


