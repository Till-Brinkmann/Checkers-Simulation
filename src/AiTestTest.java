
import java.util.Random;

import checkers.Figure.FigureColor;
import checkers.GameLogic;
import checkers.Move;
import checkers.Player;
import generic.List;
import gui.Console;

public class AiTestTest implements Player {
	String name = "Test random Ai";
	List<Move> moveList;
	GameLogic gmlc;
	Console console;
	FigureColor aiFigureColor;
	Random rand;

	public AiTestTest(GameLogic pGmlc, Console pConsole) {
		rand = new Random();
		moveList = new List<Move>();
		gmlc = pGmlc;
		console = pConsole;
	}
	@Override
	public void prepare(FigureColor color) {
		aiFigureColor = color;
	}

	@Override
	public void requestMove() {
		moveList = Move.getPossibleMoves(aiFigureColor,gmlc.getPlayfield());
		int randomNumber = rand.nextInt(moveList.length);
		moveList.toFirst();
		for(int i = 0;i < randomNumber; i++) {
			moveList.next();
		}
		gmlc.makeMove(moveList.getContent());
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean acceptDraw() {
		return false;
	}

}
