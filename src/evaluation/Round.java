package evaluation;

import java.io.File;
import java.io.IOException;

import checkers.Figure.FigureColor;
import checkers.GameLogic;
import checkers.GameLogic.Situations;
import checkers.Player;
import datastructs.List;
import utilities.FileUtilities;
/**
 * Holds the information of one round and provides methods to modify and acquire these values.
 */
public class Round {
	EvaluationManager manager;
	
	private Player player1;
	private Player player2;
	
	protected double[] moveTimeAvg = new double[2];
	protected double[] moveTimeMin = new double[2];
	protected double[] moveTimeMax = new double[2];
	protected double[] moveTimeOverall = new double[2];
	private int round;
	private FigureColor inTurn;
	private Situations endSituation;
	private boolean failed;
	List<Long> moveTimePlayer1 = new List<Long>();
	List<Long> moveTimePlayer2 = new List<Long>();
	private double gameTime;
	private String roundsPathString;
	private File roundsPath;

	private int[] stepCount = new int[2];

	private int[] jumpCount = new int[2];

	private int[] multijumpCount = new int[2];
	
	private int[] movePossibilitiesAvg = new int[2];
	private int[] movePossibilitiesOverall = new int[2];

	private int turnCount = 0;

	/**
	 * The constructor set all necessary variables, so that the basic information about this round are known.
	 * <p>
	 * @param currentRound The round number in the run.
	 * @param path A String thats is representing the path to the round directory, in which all Files for this round are saved.
	 * @param player1 An object with the Player interface that possesses in this round the red pieces.
	 * @param player2 An object with the Player interface that possesses in this round the red pieces.
	 * @param manager A reference to the EvaluationManager.
	 */
	public Round(int currentRound, File path, Player player1, Player player2, EvaluationManager manager) {
		this.player1 = player1;
		this.player2 = player2;
		this.manager = manager;

		round = currentRound;
		roundsPath = new File(path.getAbsolutePath() + "/Round " + (round+1));
		roundsPath.mkdirs();
	}
	/**
	 * Adds the time a player spend on a move to a List.
	 * <p>
	 * @param moveTime The time a Player spend on a move.
	 * @param player An object with the Player interface.
	 */
	public void setMoveTime(long moveTime,Player player) {
		if(player == player1) {
			moveTimePlayer1.append(moveTime);
		}
		else {
			moveTimePlayer2.append(moveTime);
		}
	}
	/**
	 * This method sets all information for this one round. It is only called after a round was finished.
	 * @param gmlc A reference to the GameLogic.
	 */
	public void evaluateGame(GameLogic gmlc) {
		endSituation = gmlc.getFinalSituation();
		failed = gmlc.getFailed();
		turnCount  = gmlc.getTurnCount();
		stepCount = gmlc.getStepCount();
		jumpCount = gmlc.getJumpCount();
		multijumpCount = gmlc.getMultijump();
		movePossibilitiesOverall[0] = gmlc.getOverallMovePossibilitiesRed();
		movePossibilitiesOverall[1] = gmlc.getOverallMovePossibilitiesWhite();
		movePossibilitiesAvg[0] = movePossibilitiesOverall[0] / (turnCount);
		movePossibilitiesAvg[1] = movePossibilitiesOverall[1] / (turnCount);		
		setTimes();
		FileUtilities.createTimesFile(moveTimeMin,moveTimeMax, moveTimeAvg, moveTimeOverall, player1.getName(), player2.getName(), roundsPath.getAbsolutePath(), gameTime);
		FileUtilities.createGameSummaryFile(endSituation, failed, player1.getName(), player2.getName(), roundsPath.getAbsolutePath(), stepCount, jumpCount, multijumpCount, movePossibilitiesAvg, movePossibilitiesOverall);
		player1.saveInformation(roundsPath.getAbsolutePath());
		player2.saveInformation(roundsPath.getAbsolutePath());
	}
	/**
	 * Evaluates all move times from both players. Important values are saved in other variables.
	 */
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
	public FigureColor getInturn() {
		return inTurn;
	}
	public Situations getEndSitation() {
		return endSituation;
	}
	public void setgameTime(double l) {
		gameTime = l;
			}
	public int[] getStepCount() {
		return stepCount;
	}
	public int[] getJumpCount() {
		return jumpCount;
	}
	public int[] getMultijumpCount() {
		return multijumpCount;
	}
	public int[] getMovePossibilitiesOverall() {
		return movePossibilitiesOverall;		
	}
	public int[] getMovePossibilitiesAvg() {
		return movePossibilitiesAvg;
	}
	public int getTurnCount() {
		return turnCount;
	}

}
