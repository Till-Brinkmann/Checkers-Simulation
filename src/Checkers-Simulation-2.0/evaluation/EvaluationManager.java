package evaluation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import checkers.Player;
import utilities.FileUtilities;
/**
 * Manages the information of a run(1 or more game rounds) and  the round objects, which are saving all information from one game.
 */
public class EvaluationManager {
	int rounds;
	Round round[];
	String gameName;
	Player player1;
	Player player2;
	File path;
	public String pathString;
	/**
	 * After starting a run, the object is initialized with all important parameters that are relevant for the 
	 * evaluation. And a new array of Rounds object with the length of game in this run is created.
	 * <p>
	 * @param rounds An Integer that represents the number of games that will be played in this run.
	 * @param gameName  The give name for the run.
	 * @param playerRed The Player to which the red pieces were assigned.
	 * @param playerWhite The Player to which the white pieces were assigned.
	 */
	public EvaluationManager( int rounds, String gameName, Player playerRed, Player playerWhite) {
		this.rounds = rounds;
		this.gameName = gameName;
		player1 = playerRed;
		player2 = playerWhite;
		round = new Round[rounds];
		pathString = "resources/RecordedGames/" + gameName;
		path = new File("resources/RecordedGames/" + gameName);
		path.mkdirs();
	}
	/**
	 * Creates a new Round object at a distinct spot in the rounds array.
	 * <p>
	 * @param currentRound An Integer that represents a spot in an array. It stands for the certain round in a run.
	 */
	public void createRound(int currentRound) {
		round[currentRound] = new Round(currentRound,path,player1,player2,this);
	}
	/**
	 * Return a round object for the round array at a certain spot.
	 * <p>
	 * @param currentRound An Integer that represents a spot in an array. It stands for the certain round in a run.
	 * @return A object from the type rounds.
	 */	
	public Round getRound(int currentRound) {
		return round[currentRound];
	}
	/**
	 * In this method all information from every round is gathered and evaluatated. Then two static methods in FileUtitlies are called which are creating the
	 * .txt Files.
	 */
	public void runEvaluation() {
		double min = 0;
		double max = 0;
		double avg = 0;
		double overall = 0;
		for(Round r : round) {
			if(r == null) continue;
			min += r.moveTimeMin[0] + r.moveTimeMin[1];
			max += r.moveTimeMax[0] + r.moveTimeMax[1];
			avg += r.moveTimeAvg[0] + r.moveTimeAvg[1];
			overall += r.moveTimeOverall[0] + r.moveTimeOverall[1];
		}
		int divisor = round.length*2;
		min /= divisor;
		max /= divisor;
		avg /= divisor;
		overall /= divisor;
		FileUtilities.createTotalTimesFile(min, max, avg, overall, pathString);
		
		int redWins = 0;
		int whiteWins = 0;
		int draws = 0;
		int stopped = 0;
		int avgTurnCounts = 0;
		//TODO the player is not 
		for(Round r : round) {
			if(r == null) continue;
			switch(r.getEndSituation()) {
			case DRAW:
				draws++;
				break;
			case REDWIN:
				redWins++;
				break;
			case STOP:
				stopped++;
				break;
			case WHITEWIN:
				whiteWins++;
				break;
			case NOTHING:
				break;
			}
			avgTurnCounts += r.getTurnCount();
		}
		
		FileUtilities.createRunSummaryFile(redWins,whiteWins,draws,stopped,avgTurnCounts/round.length,player1.getName(),player2.getName(),pathString);

	}
	/**
	 * @return A string that represents the name of the run.
	 */
	public String getRunName() {
		return gameName;
	}
}
