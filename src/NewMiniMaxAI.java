import checkers.Figure.FigureColor;
import gui.Console;
import checkers.GameLogic;
import checkers.Move;
import checkers.Player;
import checkers.Playfield;

public class NewMiniMaxAI implements Player {
	GameLogic gmlc;
	Console console;
	String name;
	Playfield playfield;
	FigureColor aiFigureColor;
	public NewMiniMaxAI(GameLogic pGmlc, Console pConsole) {
		gmlc = pGmlc;
		console = pConsole;
	}
	@Override
	public void prepare(FigureColor color) {
		aiFigureColor = color;
	}

	@Override
	public void requestMove() {
		@SuppressWarnings("unused")
		Move finalMove;
		playfield = gmlc.getPlayfield();
		generateTree();

	}

	private void generateTree() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String getName() {		
		return "";
	}

	@Override
	public boolean acceptDraw() {
		// TODO Auto-generated method stub
		return false;
	}

}
