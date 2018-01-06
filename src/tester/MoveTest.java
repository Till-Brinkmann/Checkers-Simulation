package tester;

import checkers.NNMove;
import checkers.NNPlayfield;

public class MoveTest {
	public static void main(String[] args) {
		System.out.println("Possible step positions of the figure on field 11 on startposition");
		byte[] pos = NNMove.getStepPositions((byte)11, true, NNPlayfield.startPosition());
		for(byte p : pos) {
			System.out.print(p + ",");
		}
		System.out.println("");
		System.out.println("There is a figure on field 9 in startposition of color red: " + Boolean.toString(NNPlayfield.startPosition().isOccupiedByColor((byte)9, true)));
		System.out.println("getJumpPositions Test.\n Should return 18,-1,-1,16,-1,-1,-1,-1,-1,-1,1,-1");
		NNPlayfield field = new NNPlayfield(0b00000000000000000000011000001000,0b00000000000000100011000011110000,0b10000000000);
		pos = NNMove.getJumpPositions((byte)9, true, field);
		for(byte p : pos) {
			System.out.print(p + ",");
		}
		pos = NNMove.getJumpPositions((byte)3, true, field);
		for(byte p : pos) {
			System.out.print(p + ",");
		}
		pos = NNMove.getJumpPositions((byte)10, true, field);
		for(byte p : pos) {
			System.out.print(p + ",");
		}
		System.out.println("");
		System.out.println("Is moving possible for red in startposition?\n" + Boolean.toString(NNMove.isMovingPossible(true, NNPlayfield.startPosition())));
		System.out.println("Is moving possible for white in startposition?\n" + Boolean.toString(NNMove.isMovingPossible(false, NNPlayfield.startPosition())));
		field = new NNPlayfield(0b00000000000000001111111111111111,0b11111111111111110000000000000000,0);
		System.out.println("Moving should not be possible here:\nisMovingPossible returns: " + Boolean.toString(NNMove.isMovingPossible(false, field)));
		System.out.println("makeMove test. Startsituation. From 11 to 14 , 5 to 12 , 21 to 18 and 25 to 16.");
		NNMove move;
		 move = NNMove.createMove((byte)11, (byte)14, NNPlayfield.startPosition());
		 System.out.println("From " + move.from + " to " + move.to + ". beatenFigures: " + move.beatenFigures);
		 move = NNMove.createMove((byte)5, (byte)12, NNPlayfield.startPosition());
		 System.out.println("From " + move.from + " to " + move.to + ". beatenFigures: " + move.beatenFigures);
		 move = NNMove.createMove((byte)21, (byte)18, NNPlayfield.startPosition());
		 System.out.println("From " + move.from + " to " + move.to + ". beatenFigures: " + move.beatenFigures);
		 move = NNMove.createMove((byte)25, (byte)16, NNPlayfield.startPosition());
		 System.out.println("From " + move.from + " to " + move.to + ". beatenFigures: " + move.beatenFigures);
		 System.out.println("For jumps: Should be from 0 to 9 beatenFigure at 4(B10000)");
		 move = NNMove.createMove((byte)0, (byte)9, new NNPlayfield(0b00000000000000000000000000010000, 0b00000000000000000000000000000001, 0b00000000000000000000000000000001));
		 System.out.println("From " + move.from + " to " + move.to + ". beatenFigures: " + Integer.toBinaryString(move.beatenFigures));
	}
}
