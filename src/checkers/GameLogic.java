package checkers;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;

import checkers.Figure.FigureColor;
import checkers.Figure.FigureType;
import checkers.Move.MoveType;
import checkers.Player;
import generic.List;
import gui.GUI;
import gui.GUI.AISpeed;
import gui.GameSettings;
import gui.PlayfieldPanel;

/**
 * provides methods for game logic
 * @author Till
 *
 */

public class GameLogic {



	/**
	 *
	 * @param playfield default playfield
	 */
	public GameLogic() {
	}
	static void testFigureToKing(Playfield field){
		int y1 = 0;
		int y2 = 7;
		for(int x = 0; x < field.getSize();x++) {
			if(field.isOccupied(x, y1)) {
				if(field.colorOf(x, y1) == FigureColor.WHITE && field.getType(x, y1) == FigureType.NORMAL) {
					field.changeFigureToKing(x, y1);
				}
			}
			if(field.isOccupied(x, y2)) {
				if(field.colorOf(x, y2) == FigureColor.RED && field.getType(x, y2) == FigureType.NORMAL) {
					field.changeFigureToKing(x, y2);
				}
			}
		}
	}
	/**
	 * tests if the given move is possible on the given playfield
	 * @param move
	 * @param field
	 * @return false if move is not possible, true if it is
	 */
	public static boolean testMove(Move m, Playfield f){
		int x = m.getX();
		int y = m.getY();
		if(!f.isOccupied(x, y)){
			return false;
		}
		FigureColor color = f.colorOf(x, y);
		FigureType type = f.getType(x, y);
		//TODO wenn es kein Jump ist aber jumps möglich sind return false
		if(m.getMoveType() == MoveType.STEP && Move.getPossibleJumps(f.field[x][y], f).length != 0){
			return false;
		}
		switch(m.getMoveType()){
		case INVALID:
			return false;
		case STEP:
			if(f.getType(x,y) == FigureType.NORMAL){
				switch (color) {
				case RED:
					switch(m.getMoveDirection()){
					case BL:
					case BR:
						//normal red figures can not go backwards
						return false;
					case FL:
						//the field is not outside the playfield and is not occupied
						//so the step is possible otherwise not
						return (x - 1 >= 0 && y + 1 < f.SIZE && !f.isOccupied(x-1, y+1));
					case FR:
						return (x + 1 < f.SIZE && y + 1 < f.SIZE && !f.isOccupied(x+1, y+1));
					}
				case WHITE:
					switch(m.getMoveDirection()){
					case BL:
						return (x - 1 >= 0 && y - 1 >= 0 && !f.isOccupied(x-1, y-1));
					case BR:
						return (x + 1 < f.SIZE && y - 1 >= 0 && !f.isOccupied(x+1, y-1));
					case FL:
					case FR:
						//normal white figures can not go forwards
						return false;
					}
				}
			}
			else {//FT = King
				switch(m.getMoveDirection()){
				case BL:
					return (x - 1 >= 0 && y - 1 >= 0 && !f.isOccupied(x-1, y-1));
				case BR:
					return (x + 1 < f.SIZE && y - 1 >= 0 && !f.isOccupied(x+1, y-1));
				case FL:
					return (x - 1 >= 0 && y + 1 < f.SIZE && !f.isOccupied(x-1, y+1));
				case FR:
					return (x + 1 < f.SIZE && y + 1 < f.SIZE && !f.isOccupied(x+1, y+1));
				}
			}
		case JUMP:
		case MULTIJUMP:
			for(int i = 0; i < m.getSteps(); i++){
				switch(m.getMoveDirection(i)){
				case BL:
					if(type == FigureType.NORMAL && color == FigureColor.RED){
						return false;//normal red figures can not go backwards
					}
					if (!(x - 2 >= 0 && y - 2 >= 0 &&//two fields space,
						f.isOccupied(x-1, y-1) &&//a figure on the next field...
					   	f.colorOf(x-1, y-1) != color &&//... that is of a different color,
					   	!f.isOccupied(x-2, y-2))//no figure on the field to land on
					){
						return false;
					}
					x -= 2;
					y -= 2;
					break;
				case BR:
					if(type == FigureType.NORMAL && color == FigureColor.RED){
						return false;
					}
					if (!(x + 2 < f.SIZE && y - 2 >= 0 &&
						f.isOccupied(x+1, y-1) &&
						f.colorOf(x+1, y-1) != color &&
						!f.isOccupied(x+2, y-2))
					){
						return false;
					}
					x += 2;
					y -= 2;
					break;
				case FL:
					if(type == FigureType.NORMAL && color == FigureColor.WHITE){
						return false;//normal white figures can not go forwards
					}
					if (!(x - 2 >= 0 && y + 2 < f.SIZE &&
						f.isOccupied(x-1, y+1) &&
						f.colorOf(x-1, y+1) != color &&
						!f.isOccupied(x-2, y+2))
					){
						return false;
					}
					x -= 2;
					y += 2;
					break;
				case FR:
					if(type == FigureType.NORMAL && color == FigureColor.WHITE){
						return false;
					}
					if (!(x + 2 < f.SIZE && y + 2 < f.SIZE &&
						f.isOccupied(x+1, y+1) &&
						f.colorOf(x+1, y+1) != color &&
						!f.isOccupied(x+2, y+2))
					){
						return false;
					}
					x += 2;
					y += 2;
					break;
				}
			}
			break;
		}
		return true;
	}
	public static List<Move> testForMultiJump(int x, int y, Playfield f) {
		List<Move> list =Move.getPossibleJumps(f.field[x][y], f);
		if(list.length != 0) {
			return list;
		}
		else
		{
		return null;
		}
	}

}

