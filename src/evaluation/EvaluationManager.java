package evaluation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import checkers.Player;
import checkers.Playfield;

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
		round[currentRound] = new Round(currentRound,path,player1,player2,field,this);
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
	}
	public void setPlayfield(Playfield field) {
		this.field = field;
	}
	public String getRunName() {
		return gameName;
	}
}
