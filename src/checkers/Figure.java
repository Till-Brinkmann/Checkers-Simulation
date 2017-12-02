package checkers;


/**
 * Base class for a figure(Man or King) on the board.
 * <p>
 * All methods are public, because they have to be accessible 
 * from Playfield, GameLogic, PlayfieldPanel and Move.
 */

public class Figure {
	
	public enum FigureColor{WHITE, RED};	
	public enum FigureType{NORMAL,KING};
	
	private FigureColor color;
	private FigureType type;
	public int x,y;
	
	public Figure(){
		
	}
	/**
	 * In order to initialize a Figure object, the following parameters are needed:
	 * <p>
	 * @param x       an integer variable which is representing a point on the vertical axis of the playfield.
	 * @param y	 	  an integer variable which is representing a point on the horizontal axis of the playfield.	
	 * @param color   a global predefined constant from the enumeration FigureColor. 
	 * @param type	  a global predefined constant from the enumeration FigureType. 
	 */
	public Figure(int x, int y, FigureColor color, FigureType type) {
		this.color = color;
		this.type = type;
		this.x = x;
		this.y = y;
	}
	/**
	 * Essential method to identify whether this the Figure Object is white or Red
	 * <p>
	 * @return color  a global predefined constant variable from the enumeration FigureColor.
	 */
	public FigureColor getFigureColor(){
		return color;
	}
	/**
	 * Essential method to identify whether this the Figure Object is a Man or a King
	 * <p>
	 * @return type  a global predefined constant variable from the enumeration FigureType.
	 */
	public FigureType getFigureType(){
		return type;
	}	
	/**
	 * This method alters the Figure's type.
	 * This is needed, because when a man reaches the other side of the playfield his type has to change into a king. 
	 * <p>
	 * @param type  a global predefined constant variable from the enumeration FigureType.
	 */
	public void setFigureType(FigureType type){
		this.type = type;
	}
	/**
	 * It returns a new Figure Object with the same properties. 
	 * <p>
	 * @return Figure  a new instance of the object Figure;
	 */
	public Figure copy() {
		return new Figure(x, y, color, type);
	}
}
