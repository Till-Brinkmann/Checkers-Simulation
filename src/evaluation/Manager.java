package evaluation;

import java.io.File;

import checkers.Player;
import checkers.Playfield;

public class Manager {

	Playfield field;
	int rounds;
	Round round[];
	String gameName;
	Player player1;
	Player player2;
	File path;
	public String pathString;
	public Manager( int rounds, String gameName, Player playerRed, Player playerWhite) {
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
		
	}
	public void setPlayfield(Playfield field) {
		this.field = field;
	}
	public String getRunName() {
		return gameName;
	}
}
