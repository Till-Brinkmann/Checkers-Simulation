package evaluation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import checkers.Player;
import checkers.Playfield;
/**
 * Manages collecting and saving data for evaluating a whole run (1 or more gamerounds).
 */
public class EvaluationManager {

	Playfield field;
	int rounds;
	Round round[];
	String gameName;
	Player player1;
	Player player2;
	File path;
	public String pathString;
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
	public void createRound(int currentRound) {
		round[currentRound] = new Round(currentRound,path,player1,player2,this);
	}
	public Round getRound(int currentRound) {
		return round[currentRound];
	}
	public void runEvaluation() {
		File total = new File(pathString + "/Total Time.txt");
		try {
			FileWriter writer = new FileWriter(total);
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
			
			writer.write("Min: " + min + "\n");
			writer.write("Max: " + max + "\n");
			writer.write("Avg: " + avg + "\n");
			writer.write("Overall: " + overall + "\n");
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		runSummary();

	}
	private void runSummary() {
		File runSummary = new File(pathString + "/RunSummary.txt");
		try {
			FileWriter writer = new FileWriter(runSummary);
			int redWins = 0;
			int whiteWins = 0;
			int draws = 0;
			int stopped = 0;
			int avgTurnCounts = 0;
			for(Round r : round) {
				if(r == null) continue;
				switch(r.getEndSitation()) {
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
			writer.write(player1.getName() + " wins:" + redWins + "\n");
			writer.write(player2.getName() + ":" + whiteWins + "\n");
			writer.write("Draws:"+ draws + "\n");
			writer.write("Stopped games:" + stopped + "\n\n");
			writer.write("Avg Turns per game: " + avgTurnCounts/round.length);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	public void setPlayfield(Playfield field) {
		this.field = field;
	}
	public String getRunName() {
		return gameName;
	}
}
