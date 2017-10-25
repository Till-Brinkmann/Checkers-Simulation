package checkers;

import checkers.Figure.FigureColor;
import checkers.Figure.FigureType;
import generic.List;

/**
 * class to save and share moves in an easy way
 * @author Till
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
	
	public Move(MoveDirection[] pDirection, int pSteps, int pX, int pY) {
		directions = pDirection;
		steps = pSteps;
		x = pX;
		y = pY;
		if(pSteps > 1){
			type = MoveType.MULTIJUMP;
		}
	}
	public Move(MoveDirection pDirection, int pX, int pY){
		this(new MoveDirection[4], 0, pX, pY);
		addStep(pDirection);
	}
	public Move(MoveType pType) {
		type = pType;
	}
	
	public Move copy(){
		return new Move(directions, steps, x, y);
	}
	
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
	/**
	 * turns an array of coordinates into a move object
	 * the move is not guaranteed to be valid!
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
	
	public static List<Move> getPossibleJumps(Figure figure, Playfield field){
		List<Move> moves = new List<Move>();
		//used for recursive multijump testing
		Playfield tmp;
		List<Move> multiJumps;
		Move m;
		if(figure.x + 2 < field.SIZE
			&& (figure.getFigureType() == FigureType.KING || figure.getFigureColor() == FigureColor.WHITE)){
			if(figure.y + 2 < field.SIZE){
				if(field.isOccupied(figure.x+1, figure.y+1) 
					&& field.field[figure.x+1][figure.y+1].color != figure.color
					&& !field.isOccupied(figure.x+2, figure.y+2)){
					moves.append(new Move(MoveDirection.FR, figure.x, figure.y));
					moves.toLast();
					tmp = field.copy();
					tmp.executeMove(moves.getContent());
					multiJumps = getPossibleJumps(tmp.field[figure.x+2][figure.y+2], tmp);
					multiJumps.toFirst();
					if(multiJumps.length > 0){
						while(multiJumps.hasAccess()){
							//take the move we just created and copy it
							m = moves.getContent().copy();
							//append the other steps of the multijump
							for(int steps = 0; steps < multiJumps.getContent().getSteps(); steps++){
								m.addStep(multiJumps.getContent().getMoveDirection(0));
							}
							//save temporarily in multiJumps
							multiJumps.setContent(m);
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
			if(figure.y - 2 >= 0){
				if(field.isOccupied(figure.x+1, figure.y-1) 
					&& field.field[figure.x+1][figure.y-1].color != figure.color
					&& !field.isOccupied(figure.x+2, figure.y-2)){
					moves.append(new Move(MoveDirection.BR, figure.x, figure.y));
					moves.toLast();
					tmp = field.copy();
					tmp.executeMove(moves.getContent());
					multiJumps = getPossibleJumps(tmp.field[figure.x+2][figure.y-2], tmp);
					multiJumps.toFirst();
					if(multiJumps.length > 0){
						while(multiJumps.hasAccess()){
							m = moves.getContent().copy();
							for(int steps = 0; steps < multiJumps.getContent().getSteps(); steps++){
								m.addStep(multiJumps.getContent().getMoveDirection(0));
							}
							multiJumps.setContent(m);
							multiJumps.next();
						}
						moves.remove();
						moves.concat(multiJumps);
					}
				}
			}
		}
		if(figure.x - 2 >= 0
			&& (figure.getFigureType() == FigureType.KING || figure.getFigureColor() == FigureColor.RED)){
			if(figure.y + 2 < field.SIZE){
				if(field.isOccupied(figure.x-1, figure.y+1) 
					&& field.field[figure.x-1][figure.y+1].color != figure.color
					&& !field.isOccupied(figure.x-2, figure.y+2)){
					moves.append(new Move(MoveDirection.FL, figure.x, figure.y));
					moves.toLast();
					tmp = field.copy();
					tmp.executeMove(moves.getContent());
					multiJumps = getPossibleJumps(tmp.field[figure.x-2][figure.y+2], tmp);
					multiJumps.toFirst();
					if(multiJumps.length > 0){
						while(multiJumps.hasAccess()){
							m = moves.getContent().copy();
							for(int steps = 0; steps < multiJumps.getContent().getSteps(); steps++){
								m.addStep(multiJumps.getContent().getMoveDirection(0));
							}
							multiJumps.setContent(m);
							multiJumps.next();
						}
						moves.remove();
						moves.concat(multiJumps);
					}
				}
			}
			if(figure.y - 2 >= 0){
				if(field.isOccupied(figure.x-1, figure.y-1) 
					&& field.field[figure.x-1][figure.y-1].color != figure.color
					&& !field.isOccupied(figure.x-2, figure.y-2)){
					moves.append(new Move(MoveDirection.BL, figure.x, figure.y));
					moves.toLast();
					tmp = field.copy();
					tmp.executeMove(moves.getContent());
					multiJumps = getPossibleJumps(tmp.field[figure.x-2][figure.y-2], tmp);
					multiJumps.toFirst();
					if(multiJumps.length > 0){
						while(multiJumps.hasAccess()){
							m = moves.getContent().copy();
							for(int steps = 0; steps < multiJumps.getContent().getSteps(); steps++){
								m.addStep(multiJumps.getContent().getMoveDirection(0));
							}
							multiJumps.setContent(m);
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
	public static List<Move> getPossibleSteps(Figure f, Playfield p){
		List<Move> moves = new List<Move>();
		if(f.x + 1 < p.SIZE
				&& (f.getFigureType() == FigureType.KING || f.getFigureColor() == FigureColor.WHITE)){
			if(f.y + 1 < p.SIZE){
				if(!p.isOccupied(f.x+1, f.y+1)){
					moves.append(new Move(MoveDirection.FR, f.x, f.y));
				}
			}
			if(f.y - 1 >= 0){
				if(!p.isOccupied(f.x+1, f.y-1)){
					moves.append(new Move(MoveDirection.BR, f.x, f.y));
				}
			}
		}
		if(f.x - 1 >= 0
				&& (f.getFigureType() == FigureType.KING || f.getFigureColor() == FigureColor.RED)){
			if(f.y + 1 < p.SIZE){
				if(!p.isOccupied(f.x-1, f.y+1)){
					moves.append(new Move(MoveDirection.FL, f.x, f.y));
				}
			}
			if(f.y - 1 >= 0){
				if(!p.isOccupied(f.x-1, f.y-1)){
					moves.append(new Move(MoveDirection.BL, f.x, f.y));
				}
			}
		}
		return moves;
	}
	public static List<Move> getPossibleMoves(Figure figure, Playfield playfield){		
		return getPossibleJumps(figure, playfield).concat(getPossibleSteps(figure, playfield));
	}
	public static List<Move> getPossibleMoves(FigureColor color, Playfield playfield){
		List<Move> moves = new List<Move>();
		for(Figure f : playfield.getFiguresFor(color)){
			moves.concat(getPossibleMoves(f, playfield));
		}
		return moves;
	}
	public boolean isInvalid() {
		return type == MoveType.INVALID;
	}
}


