package checkers;
/**
 * class to save and share moves in an easy way
 * @author Till
 *
 */
public class Move {

	public static enum MoveType{
		STEP,
		JUMP,
	};
	public static enum MoveDirection{
		FR, // Forward/Right
		FL, // Forward/Left
		BR, // Backward/Right
		BL, // Backward/Left
		INVALID;
	};
	private MoveType type;
	private MoveDirection direction;
	private int steps, x, y;
	
	public Move(MoveDirection pDirection,int pSteps, int pX, int pY) {
		direction = pDirection;
		steps = pSteps;
		x = pX;
		y = pY;
	}
	public void setMoveType(MoveType pType){
		type = pType;
	}
	public MoveType getMoveType(){
		return type;
	}
	public MoveDirection getMoveDirection(){
		return direction;		
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
}
