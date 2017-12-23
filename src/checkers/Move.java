package checkers;

import checkers.Figure.FigureColor;
import checkers.Figure.FigureType;
import generic.List;

/**
 * class to save and share moves in an easy way
 *
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
	 * @param type should only be MoveType.INVALID
	 */
	private Move(MoveType type){
		this.type = type;
	}
	
	public Move copy(){
		return new Move(directions, type, x, y);
	}
	
	public void addStep(MoveDirection dir){
		//new direction is added and AFTERWARDS steps is incremented
		directions[steps++] = dir;
		if(steps > 1) type = MoveType.MULTIJUMP;
	}
	public void setMoveType(MoveType pType){
		type = pType;
	}
	public MoveType getMoveType(){
		return type;
	}
	public MoveDirection getMoveDirection(int step){
		return directions[step];		
	}
	public MoveDirection getMoveDirection(){
		return directions[0];
	}
	public int getSteps(){
		return steps;
	}
	public int getX(){
		return x;
	}
	public  int getY(){
		return y;
	}
	
	public boolean isInvalid() {
		return type == MoveType.INVALID;
	}
	/**
	 * turns an array of coordinates into a move object
	 * !ATTENTION!:the method does not apply complete move validation!
	 * So the move could not be valid although it is not set invalid!
	 * test with Gamelogic.testMove()!
	 * @param coords that the figure goes to during the move in chronological order
	 * @return move object that represents the move described by the coordinates 
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
	 * @param color of the figures that should be tested
	 * @param field playfield to test on
	 * @return true if a jump with a figure of the given color on the given playfield is possible
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
	
	public static List<Move> getPossibleMoves(Figure figure, Playfield playfield){
		return getPossibleSteps(figure, playfield).concat(getPossibleJumps(figure, playfield));
	}
	public static List<Move> getPossibleMoves(FigureColor color, Playfield playfield){
		List<Move> moves = new List<Move>();
		for(Figure f : playfield.getFiguresFor(color)){
			moves.concat(getPossibleMoves(f, playfield));
		}
		return moves;
	}
}


