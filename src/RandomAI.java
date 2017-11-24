
import java.util.Random;

import checkers.Figure.FigureColor;
import checkers.GameLogic;
import checkers.Move;
import checkers.Player;
import generic.List;
import gui.Console;

public class RandomAI implements Player {
	String name;
	List<Move> moveList;
	GameLogic gmlc;
	Console console;
	FigureColor aiFigureColor;
	Random rand;

	public RandomAI(GameLogic pGmlc, Console pConsole) {
		name = "Random Ai";
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
		gmlc.makeMove(moveList.get());
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
