package evaluation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import checkers.Figure.FigureColor;
import checkers.GameLogic;
import checkers.GameLogic.Situations;
import checkers.Player;
import checkers.Playfield;
import generic.List;
import utilities.FileUtilities;

public class Round {
	EvaluationManager manager;
	
	private Player player1;
	private Player player2;
	
	protected double[] moveTimeAvg = new double[2];
	protected double[] moveTimeMin = new double[2];
	protected double[] moveTimeMax = new double[2];
	protected double[] moveTimeOverall = new double[2];
	private int round;
	private int player1turns;
	private int player2turns;
	private FigureColor inTurn;
	private Situations endSituation;
	private boolean failed;
	List<Long> moveTimePlayer1 = new List<Long>();
	List<Long> moveTimePlayer2 = new List<Long>();
	private String roundsPathString;
	private File roundsPath;
	private Playfield field;
	
	
	public Round(int currentRound, File path, Player player1, Player player2, Playfield field, EvaluationManager manager) {
		this.field = field;
		this.player1 = player1;
		this.player2 = player2;
		this.manager = manager;
		player1turns = 0;
		player2turns = 0;
		round = currentRound;
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
	public void evaluateGame(GameLogic gmlc) {
		endSituation = gmlc.getFinalSituation();
		failed = gmlc.getFailed();
		player1turns = gmlc.getTurnCountRed();
		player2turns = gmlc.getTurnCountWhite();
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
	public FigureColor getInturn() {
		return inTurn;
	}
	public Situations getEndSitation() {
		return endSituation;
	}
}
