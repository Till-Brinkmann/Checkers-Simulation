package evaluation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import checkers.Figure.FigureColor;
import checkers.GameLogic;
import checkers.GameLogic.Situations;
import checkers.Move.MoveType;
import checkers.Player;
import checkers.Playfield;
import generic.List;
import utilities.FileUtilities;

public class Round {
	EvaluationManager manager;
	
	private Player player1;
	private Player player2;
	private Player startedFirst;
	private Player winner;
	private Player looser;
	
	private double[] moveTimeAvg = new double[2];
	private double[] moveTimeMin = new double[2];
	private double[] moveTimeMax = new double[2];
	private double[] moveTimeOverall = new double[2];
	private int round;
	private int player1turns;
	private int player2turns;
	private Situations endSituation;
	private boolean failed;
	List<Long> moveTimePlayer1 = new List<Long>();
	List<Long> moveTimePlayer2 = new List<Long>();
	private String roundsPathString;
	private File roundsPath;
	private Playfield field;

	private int figureCounterPlayer1;

	private int figureCounterPlayer2;
	
	private int player1Moves[];
	private int player2Moves[];
	
	public Round(int currentRound, File path, Player player1, Player player2, Playfield field, EvaluationManager manager) {
		this.field = field;
		this.player1 = player1;
		this.player2 = player2;
		this.manager = manager;
		player1turns = 0;
		player2turns = 0;
		winner = null;
		looser = null;
		round = currentRound;
		
		player1Moves = new int[3];
		player2Moves = new int[3];
		
		roundsPath = new File(path.getAbsolutePath() + "/Round " + (round+1));
		roundsPath.mkdirs();
	}
	public void setMoveTime(long l,Player player) {
		if(player == player1) {
			moveTimePlayer1.append(l);
		}
		else {
			moveTimePlayer2.append(l);
		}
	}
	public void addToMoves(MoveType moveType, FigureColor inTurn, GameLogic gmlc) {
		if(inTurn == FigureColor.RED) {						
				if(player1 == gmlc.getPlayerRed()) {
					switch(moveType) {
					case STEP:
						player1Moves[0]++;
						break;		
					case JUMP:
						player1Moves[1]++;
						break;
					case MULTIJUMP:
						player1Moves[2]++;
						break;					
					}
				}
				else {
					switch(moveType) {
					case STEP:
						player2Moves[0]++;
						break;		
					case JUMP:
						player2Moves[1]++;
						break;
					case MULTIJUMP:
						player2Moves[2]++;
						break;					
					}
				}
		}
		else {
			if(player1 == gmlc.getPlayerWhite()) {
				switch(moveType) {
				case STEP:
					player1Moves[0]++;
					break;		
				case JUMP:
					player1Moves[1]++;
					break;
				case MULTIJUMP:
					player1Moves[2]++;
					break;					
				}
			}
			else {
				switch(moveType) {
				case STEP:
					player2Moves[0]++;
					break;		
				case JUMP:
					player2Moves[1]++;
					break;
				case MULTIJUMP:
					player2Moves[2]++;
					break;					
				}
			}
		}
		
	}
	public void evaluateGame(GameLogic gmlc) {
		endSituation = gmlc.getFinalSituation();
		if(endSituation == Situations.REDWIN) {
			winner = gmlc.getPlayerRed();
			looser = gmlc.getPlayerWhite();
		}
		if(endSituation == Situations.WHITEWIN) {
			winner = gmlc.getPlayerRed();
			looser = gmlc.getPlayerWhite();
		}
		startedFirst = gmlc.getPlayerRed();
		failed = gmlc.getFailed();
		
		if(player1 == gmlc.getPlayerRed()) {
			figureCounterPlayer1 = gmlc.getPlayfield().getFigureQuantity(FigureColor.RED);
			figureCounterPlayer2 = gmlc.getPlayfield().getFigureQuantity(FigureColor.WHITE);
		}
		if(player1 == gmlc.getPlayerWhite()) {
			figureCounterPlayer1 = gmlc.getPlayfield().getFigureQuantity(FigureColor.WHITE);
			figureCounterPlayer2 = gmlc.getPlayfield().getFigureQuantity(FigureColor.RED);
		}
		player1turns = gmlc.getTurnCountRed();
		player2turns = gmlc.getTurnCountWhite();
		
		
		FileUtilities.createInformationFile(player1.getName(), player2.getName(),startedFirst.getName(), winner , looser, (player1turns + player2turns), figureCounterPlayer1, figureCounterPlayer2, player1Moves, player2Moves , roundsPath.getAbsolutePath());		
		setTimes();
		FileUtilities.createTimesFile(moveTimeMin,moveTimeMax, moveTimeAvg, moveTimeOverall, player1.getName(), player2.getName(), roundsPath.getAbsolutePath());
		player1.saveInformation(roundsPath.getAbsolutePath());
		player2.saveInformation(roundsPath.getAbsolutePath());
	}
	public void setTimes(){
		float minTime = +Float.MAX_VALUE;
		float maxTime = -Float.MIN_VALUE;
		for(moveTimePlayer1.toFirst() ; moveTimePlayer1.hasAccess(); moveTimePlayer1.next()) {
			long tmp = moveTimePlayer1.get();
			if(minTime > tmp) {
				minTime = tmp;
			}
			if(maxTime < tmp) {
				maxTime = tmp;
			}
			moveTimeOverall[0] += tmp;
		}
		moveTimeAvg[0] = moveTimeOverall[0]/moveTimePlayer1.length;
		moveTimeMin[0] = minTime;
		moveTimeMax[0] = maxTime;
		
		minTime = +Float.MAX_VALUE;
		maxTime = -Float.MIN_VALUE;
		for(moveTimePlayer2.toFirst() ; moveTimePlayer2.hasAccess(); moveTimePlayer2.next()) {
			long tmp = moveTimePlayer2.get();
			if(minTime > tmp) {
				minTime = tmp;
			}
			if(maxTime < tmp) {
				maxTime = tmp;
			}
			moveTimeOverall[1] += tmp;
		}
		moveTimeAvg[1] = moveTimeOverall[1]/moveTimePlayer2.length;
		moveTimeMin[1] = minTime;
		moveTimeMax[1] = maxTime;
	}
	public void saveGameSituation(GameLogic gmlc) {
		try {
			FileUtilities.saveGameSituation(gmlc.getPlayfield(),roundsPath.getAbsolutePath(),"Turn" + (gmlc.getTurnCount()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	public EvaluationManager getManager() {
		return manager;
	}
	public Player getPlayer1() {
		return player1;
	}
	public Player getPlayer2() {
		return player2;
	}
	public int getRound() {
		return round;
	}
	public String getRoundsPathString() {
		return	roundsPathString;
	}
	public int getPlayer1turns() {
		return player1turns;
	}
	public int getPlayer2turns() {
		return player2turns;
	}

	public Situations getEndSitation() {
		return endSituation;
	}
	public boolean failed() {
		return failed;
		
	}
}
