package player;

import algo.MinMaxManager;
import algo.MinMaxTask;
import checkers.Figure.FigureColor;
import checkers.GameLogic;
import checkers.Move;
import checkers.Player;
import checkers.Playfield;
import generic.List;
import gui.CommandListener;
import gui.Console;

public class MiniMaxPlayer implements Player{
	
	public static final int defaultMaxDepth = 5;
	
	public GameLogic gmlc;
	public Console csl;
	
	private MinMaxManager manager;
	
	private float bestValue;
	private Move bestMove;
	
	public MiniMaxPlayer(GameLogic gmlc, Console csl) {
		this.gmlc = gmlc;
		this.csl = csl;
		csl.addCommandListener(new CommandListener() {
			@Override
			public boolean processCommand(String command, String[] args) {
				if(command.equals("set")) {
					if(args.length == 2 && args[0].equals("MMMaxDepth")) {
						//creating a new manager is the easiest way to make sure that the maxDepth does not change while a move is calculated
						//(the current Tasks all have references to the old manager)
						manager = new MinMaxManager(manager.player, Integer.parseInt(args[1]), manager.playerColor, manager.enemyColor);
						csl.printCommandOutput("maxDepth set to " + manager.maxDepth);
						return true;
					}
				}
				return false;
			}
		});
	}

	@Override
	public void prepare(FigureColor color) {
		manager = new MinMaxManager(this, defaultMaxDepth, color, color == FigureColor.RED ? FigureColor.WHITE : FigureColor.RED);
	}
	@Override
	public void requestMove(){
		//reset/init variables
		bestValue = -Float.MAX_VALUE;
		//start maximizing minMaxTask for every possible move
		List<Move> moves = Move.getPossibleMoves(manager.playerColor, gmlc.getPlayfield());
		//in case the manager changes while the tasks are started
		MinMaxManager tmpman = manager;
		tmpman.updateFigureCounts();
		float v;
		for(moves.toFirst(); moves.hasAccess(); moves.next()){
				v = new MinMaxTask(
							tmpman,
							moves.get(),
							gmlc.getPlayfield().copy(),
							1,
							//the next task is always not maximizing
							false
						).compute();
			if(v > bestValue){
				bestValue = v;
				bestMove = moves.get();
			}
		}
		if(moves.length == 0){
			manager.logError("NUUUUUUUUUUUUUUUUUUUUUUUUUUUUUULLLLLLLLL");
		}
		gmlc.makeMove(bestMove);
	}
	@Override
	public boolean acceptDraw() {
		return false;
	}
	@Override
	public String getName() {
		return "MiniMax algorithm based Checkers player";
	}
	
	public Playfield getPlayfield(){
		return gmlc.getPlayfield();
	}
}
