package player;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import algo.MinMaxABManager;
import algo.MinMaxABTask;
import checkers.Figure.FigureColor;
import checkers.GameLogic;
import checkers.Move;
import checkers.Player;
import checkers.Playfield;
import generic.List;
import gui.CommandListener;
import gui.Console;

public class MiniMaxABPlayer implements Player{
	
	public static final int defaultMaxDepth = 5;
	public static final boolean defaultRandom = false;
	public GameLogic gmlc;
	public Console csl;
	
	private MinMaxABManager manager;
	
	private float alpha;
	private float beta;
	private float bestValue;
	private Move bestMove;
	
	public MiniMaxABPlayer(GameLogic gmlc, Console csl) {
		this.gmlc = gmlc;
		this.csl = csl;
		csl.addCommandListener(new CommandListener() {
			@Override
			public boolean processCommand(String command, String[] args) {
				if(command.equals("set")) {
					if(args.length == 2 && args[0].equals("MMMaxDepth")) {
						//creating a new manager is the easiest way to make sure that the maxDepth does not change while a move is calculated
						//(the current Tasks all have references to the old manager)
						manager = new MinMaxABManager(manager.player, Integer.parseInt(args[1]), manager.playerColor, manager.enemyColor, manager.random);
						csl.printCommandOutput("maxDepth set to " + manager.maxDepth);
						return true;
					}
					else if(args.length == 2 && args[0].equals("Random")) {
						if(args[1].equals("true")) {
							manager = new MinMaxABManager(manager.player, manager.maxDepth,manager.playerColor, manager.enemyColor,true);
							csl.printCommandOutput("random was set to true");
						}
						if(args[1].equals("false")) {
							manager = new MinMaxABManager(manager.player, manager.maxDepth,manager.playerColor, manager.enemyColor,false);
							csl.printCommandOutput("random was set to false");
						}
					}
					else {
						csl.printWarning("This command could not be processed", "MiniMaxPlayer");
					}
				}
				return false;
			}
		});
	}

	@Override
	public void prepare(FigureColor color) {
		manager = new MinMaxABManager(this, defaultMaxDepth, color, color == FigureColor.RED ? FigureColor.WHITE : FigureColor.RED, defaultRandom);
	}
	@Override
	public void requestMove(){
		//reset/init variables
		bestValue = -Float.MAX_VALUE;
		alpha = -Float.MAX_VALUE;
		beta = Float.MAX_VALUE;
		//start maximizing minMaxTask for every possible move
		List<Move> moves = Move.getPossibleMoves(manager.playerColor, gmlc.getPlayfield());
		//in case the manager changes while the tasks are started
		MinMaxABManager tmpman = manager;
		tmpman.updateFigureCounts();
		float v;
		for(moves.toFirst(); moves.hasAccess(); moves.next()){
				v = new MinMaxABTask(
							tmpman,
							moves.get(),
							gmlc.getPlayfield().copy(),
							1,
							//the next task is always not maximizing
							false,
							alpha,
							beta
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
		return "MiniMax algorithm with alpha-beta pruning based Checkers player";
	}
	@Override
	public void saveInformation(String pathName) {
		File file = new File(pathName + "/MiniMaxAB Information.txt") ;
		PrintWriter writer;
		try {
			writer = new PrintWriter(file);
			writer.write("Performed alpha cuts:\n");
			writer.write(manager.getAlphaCuts() + "\n");			
			writer.write("Performed beta-cuts:\n");
			writer.write(manager.getAlphaCuts() + "\n");
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	public Playfield getPlayfield(){
		return gmlc.getPlayfield();
	}
}
