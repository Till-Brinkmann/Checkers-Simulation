package checkers;


/**
 * base class for a man or a king on the board
 * @author Till
 *
 */

public class Figure {
	
	public enum FigureColor{WHITE, RED};	
	public enum FigureType{NORMAL,KING};
	
	public FigureColor color;
	public FigureType type;
	public int x,y;
	
	public Figure(){
		
	}
	public Figure(int px, int py, FigureColor pColor, FigureType pType) {
		color = pColor;
		type = pType;
		x = px;
		y = py;
	}
	public FigureColor getFigureColor(){
		return color;
	}
	public FigureType getFigureType(){
		return type;
	}
	public void setFigureType(FigureType pType){
		type = pType;
	}
	public Figure copy() {
		return new Figure(x, y, color, type);
	}
}
