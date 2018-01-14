package checkers;

import datastructs.List;

public class NNMove {
	
	public static final NNMove INVALID = new NNMove((byte)0,(byte)0,0);
	
	public final byte from;
	public final byte to;
	public final int beatenFigures;
	public NNMove(byte from, byte to, int beatenFigures) {
		this.from = from;
		this.to = to;
		this.beatenFigures = beatenFigures;
	}
	
	public boolean isJump() {
		return beatenFigures != 0;
	}
	
	public static NNMove createMove(byte from, byte to, NNPlayfield field){
		NNMove move = new NNMove(from, to, 0);
		boolean color = field.isOccupiedByColor(from, true);
		byte[] jumpPos = getJumpPositions(from, color, field);
		for(int i = 0; i < 4; i++) {
			if(jumpPos[i] == to) {
				int[] coords = fieldToCoords(from);
				byte beatenFigurePos = 0;
				switch(i) {
				case 0:
					beatenFigurePos = coordsToField(coords[0]+1, coords[1]+1);
					break;
				case 1:
					beatenFigurePos = coordsToField(coords[0]+1, coords[1]-1);
					break;
				case 2:
					beatenFigurePos = coordsToField(coords[0]-1, coords[1]-1);
					break;
				case 3:
					beatenFigurePos = coordsToField(coords[0]-1, coords[1]+1);
					break;
				}
				return new NNMove(move.from, move.to, 1<<beatenFigurePos);
			}
		}
		for(byte p : getStepPositions(from, color, field)) {
			if(p == to) return move;
		}
		return NNMove.INVALID;
	}
	
	private static int[] fieldToCoords(int choiceField) {
		int[] c = new int[2];
        c[0] = choiceField%4;
        c[1] = (choiceField-c[0])/4;
        c[0] *= 2;
        if(c[1]%2 == 1){
        	c[0]++;
        }
		return c;
	}
	private static byte coordsToField(int x, int y){
		if(y%2 == 1){
        	x--;
        }
		x /= 2;
		return (byte) (y*4+x);
	}
	/**
	 * 
	 * @param pos
	 * @param field
	 * @return Returns an  array of every possible position you can step to from pos. Invalid positions are set to -1.
	 */
	public static byte[] getStepPositions(byte from, boolean color, NNPlayfield field) {
		if(!field.isOccupiedByColor(from, color)) return new byte[] {-1,-1,-1,-1};
		int[] coords = fieldToCoords(from);
		int x = coords[0];
		int y = coords[1];
		byte[] positions = new byte[4];
		byte pos = coordsToField(x+1,y+1);
		positions[0] = (!(x+1 > 7 || y+1 > 7) && !field.isOccupied(pos) && (color || field.isKing(from))) ? pos : -1;
		pos = coordsToField(x+1,y-1);
		positions[1] = (!(x+1 > 7 || y-1 < 0) && !field.isOccupied(pos) && (!color || field.isKing(from))) ? pos : -1;
		pos = coordsToField(x-1,y-1);
		positions[2] = (!(x-1 < 0 || y-1 < 0) && !field.isOccupied(pos) && (!color || field.isKing(from))) ? pos : -1;
		pos = coordsToField(x-1,y+1);
		positions[3] = (!(x-1 < 0 || y+1 > 7) && !field.isOccupied(pos) && (color || field.isKing(from))) ? pos : -1;
		return positions;
	}
	/**
	 * returns only jumps not multijumps
	 * @param pos
	 * @param color
	 * @param field
	 * @return Returns an  array of every possible position you can step to from pos. Invalid positions are set to -1.
	 */
	public static byte[] getJumpPositions(byte from, boolean color, NNPlayfield field) {
		if(!field.isOccupiedByColor(from, color)) return new byte[] {-1,-1,-1,-1};
		int[] coords = fieldToCoords(from);
		int x = coords[0];
		int y = coords[1];
		byte[] positions = new byte[4];
		byte pos = coordsToField(x+2,y+2);
		positions[0] = (!(x+2 > 7 || y+2 > 7) && field.isOccupiedByColor(coordsToField(x+1,y+1), !color) && !field.isOccupied(pos) && (color || field.isKing(from))) ? pos : -1;
		pos = coordsToField(x+2,y-2);
		positions[1] = (!(x+2 > 7 || y-2 < 0) && field.isOccupiedByColor(coordsToField(x+1,y-1), !color) && !field.isOccupied(pos) && (!color || field.isKing(from))) ? pos : -1;
		pos = coordsToField(x-2,y-2);
		positions[2] = (!(x-2 < 0 || y-2 < 0) && field.isOccupiedByColor(coordsToField(x-1,y-1), !color) && !field.isOccupied(pos) && (!color || field.isKing(from))) ? pos : -1;
		pos = coordsToField(x-2,y+2);
		positions[3] = (!(x-2 < 0 || y+2 > 7) && field.isOccupiedByColor(coordsToField(x-1,y+1), !color) && !field.isOccupied(pos) && (color || field.isKing(from))) ? pos : -1;
		return positions;
	}
	
	public static boolean isMovingPossible(boolean color, NNPlayfield field) {
		for(byte position : field.getFigurePositionsOfType(color)) {
			for(byte p : getJumpPositions(position, color, field)) {
				//at least one jump is possible
				if(p != -1) return true;
			}
			for(byte p : getStepPositions(position, color, field)) {
				//at least one move is possible
				if(p != -1) return true;
			}
		}
		return false;
	}
	public static boolean isJumpingPossible(boolean color, NNPlayfield field) {
		for(byte pos : field.getFigurePositionsOfType(color)) {
			for(byte p : getJumpPositions(pos, color, field)) {
				if(p != -1) return true;
			}
		}
		return false;
	}
	
	public static boolean testMove(NNMove move, NNPlayfield field) {
		if(move == null || move == NNMove.INVALID || field == null) return false;
		//if any coordinate is out of bounds
		if(move.from == move.to || (move.from < 0 || move.from > 31)) return false;
		if(move.to < 0 || move.to > 31) return false;
		for(byte end : getJumpPositions(move.from, field.isOccupiedByColor(move.from, true), field)) {
			if(end == move.to) return true;
		}
		for(byte end : getStepPositions(move.from, field.isOccupiedByColor(move.from, true), field)){
			if(end == move.to) return true;
		}
		return false;
	}
	
	public static List<NNMove> getPossibleMovesFor(boolean color, NNPlayfield field) {
		List<NNMove> moves = new List<NNMove>();
		byte[] jumpPos;
		byte[] positions = field.getFigurePositionsOfType(color);
		for(byte pos : positions) {
			jumpPos = getJumpPositions(pos, color, field);
			for(int i = 0; i < 4; i++) {
				if(jumpPos[i] != -1) {
					int[] coords = fieldToCoords(pos);
					byte beatenFigurePos = 0;
					switch(i) {
					case 0:
						beatenFigurePos = coordsToField(coords[0]+1, coords[1]+1);
						break;
					case 1:
						beatenFigurePos = coordsToField(coords[0]+1, coords[1]-1);
						break;
					case 2:
						beatenFigurePos = coordsToField(coords[0]-1, coords[1]-1);
						break;
					case 3:
						beatenFigurePos = coordsToField(coords[0]-1, coords[1]+1);
						break;
					}
					moves.append(new NNMove(pos, jumpPos[i], 1<<beatenFigurePos));
				}
			}
		}
		//if there are jumps available you have to take these
		if(moves.length != 0) return moves;
		for(byte pos : positions) {
			for(byte stepPos : getStepPositions(pos, color, field)) {
				if(stepPos != -1) moves.append(new NNMove(pos, stepPos, 0));
			}
		}
		return moves;
	}
}
