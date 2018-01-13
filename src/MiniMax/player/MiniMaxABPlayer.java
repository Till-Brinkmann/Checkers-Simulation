package player;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import algo.MiniMaxABManager;
import algo.MiniMaxABTask;
import algo.MiniMaxManager;
import checkers.Figure.FigureColor;
import datastructs.List;
import checkers.GameLogic;
import checkers.Move;
import checkers.Player;
import checkers.Playfield;
import gui.CommandListener;
import gui.Console;

public class MiniMaxABPlayer extends MiniMaxPlayer implements Player{
	public static final boolean defaultRandom = false;
	
	private MiniMaxABManager abm;
	
	private float alpha;
	private float beta;
	private float bestValue;
	private Move bestMove;
	
	public MiniMaxABPlayer(GameLogic gmlc, Console csl) {
		super(gmlc, csl);
		csl.addCommandListener(new CommandListener() {
			@Override
			public boolean processCommand(String command, String[] args) {
				if(command.equals("set")) {
					if(args.length == 2 && args[0].equals("Random")) {
						if(args[1].equals("true")) {
							abm = new MiniMaxABManager(true);
							csl.printCommandOutput("random was set to true");
						}
						if(args[1].equals("false")) {
							abm = new MiniMaxABManager(false);
							csl.printCommandOutput("random was set to false");
						}
					}
				}
				return false;
			}
		});
	}

	@Override
	public void prepare(FigureColor color) {
		super.prepare(color);
		abm = new MiniMaxABManager(defaultRandom);
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
		MiniMaxManager tmpman = manager;
		MiniMaxABManager tmpabm = abm;
		tmpman.updateFigureCounts();
		float v;
		for(moves.toFirst(); moves.hasAccess(); moves.next()){
				v = new MiniMaxABTask(
							tmpman,
							tmpabm,
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
		File file = new File(pathName + "/MiniMaxAB Information.txt");
		PrintWriter writer;
		try {
			writer = new PrintWriter(file);
			writer.write("Performed alpha cuts:\n");
			writer.write(abm.getAlphaCuts() + "\n");	
			writer.write("Performed beta-cuts:\n");
			writer.write(abm.getBetaCuts() + "\n");
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
