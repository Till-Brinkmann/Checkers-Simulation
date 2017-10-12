package checkers;

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
	 * @param coords that the figure goes to during the move in chronological order
	 * @return move object that represents the move described by the coordinates 
	 */
	public static Move makeMove(int[][] coords){
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
	
	public static List<Move> getAllJumps(Figure figure, Playfield field){
		//first moves has maximum size to avoid out of bounds exceptions
		List<Move> moves = new List<Move>();
		//used for recursive multijump testing
		Playfield tmp;
		if(figure.x + 2 < field.SIZE){
			if(figure.y + 2 < field.SIZE){
				if(field.isOccupied(figure.x+1, figure.y+1) 
					&& field.field[figure.x+1][figure.y+1].color != figure.color
					&& !field.isOccupied(figure.x+2, figure.y+2)){
					moves.append(new Move(MoveDirection.BR, figure.x, figure.y));
					//TODO die rekursion für multijumps fertigstellen
					/*tmp = field.copy();
					tmp.executeMove(moves[0]);
					getAllJumps(tmp.field[figure.x+2][figure.y+2], tmp);*/
				}
			}
			if(figure.y - 2 > field.SIZE){
				if(field.isOccupied(figure.x+1, figure.y-1) 
					&& field.field[figure.x+1][figure.y-1].color != figure.color
					&& !field.isOccupied(figure.x+2, figure.y-2)){
					moves.append(new Move(MoveDirection.BL, figure.x, figure.y));
					//TODO die rekursion für multijumps fertigstellen
					/*tmp = field.copy();
					tmp.executeMove(moves[0]);
					getAllJumps(tmp.field[figure.x+2][figure.y-2], tmp);*/
				}
			}
		}
		if(figure.x - 2 > field.SIZE){
			if(figure.y + 2 < field.SIZE){
				if(field.isOccupied(figure.x-1, figure.y+1) 
					&& field.field[figure.x-1][figure.y+1].color != figure.color
					&& !field.isOccupied(figure.x-2, figure.y+2)){
					moves.append(new Move(MoveDirection.FR, figure.x, figure.y));
					//TODO die rekursion für multijumps fertigstellen
					/*tmp = field.copy();
					tmp.executeMove(moves[0]);
					getAllJumps(tmp.field[figure.x-2][figure.y+2], tmp);*/
				}
			}
			if(figure.y - 2 > field.SIZE){
				if(field.isOccupied(figure.x-1, figure.y-1) 
					&& field.field[figure.x-1][figure.y-1].color != figure.color
					&& !field.isOccupied(figure.x-2, figure.y-2)){
					moves.append(new Move(MoveDirection.FL, figure.x, figure.y));
					//TODO die rekursion für multijumps fertigstellen
					/*tmp = field.copy();
					tmp.executeMove(moves[0]);
					getAllJumps(tmp.field[figure.x-2][figure.y-2], tmp);*/
				}
			}
		}
		//make a new array of right length
		return moves;
	}
	public boolean isInvalid() {
		return type == MoveType.INVALID;
	}
}






