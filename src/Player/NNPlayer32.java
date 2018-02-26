import checkers.Figure.FigureColor;
import checkers.GameLogic;
import checkers.Player;
import gui.Console;

public class NNPlayer32 implements Player{

	private FigureColor color;
	
	private GameLogic gl;
	private Console csl;
	
	private NNPlayer32INRE nnp;
	public NNPlayer32(GameLogic gl, Console csl) {
		this.gl = gl;
		this.csl = csl;
		nnp = new NNPlayer32INRE(new NNSpecification());
	}
	
	@Override
	public void prepare(FigureColor color) {
		this.color = color;
	}

	@Override
	public void requestMove() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean acceptDraw() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void saveInformation(String directory) {
		// TODO Auto-generated method stub
		
	}

}
