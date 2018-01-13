package network;

import checkers.Figure.FigureColor;
import checkers.Player;
/**
 * Dummy Player for playing over network.
 */
public class NetworkPlayer implements Player {

	private String name = "Online Player";
	
	public NetworkPlayer(NetworkManager manager) {
		
	}
	
    @Override
	public void prepare(FigureColor color) {
		
	}

	@Override
	public void requestMove() {
		
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean acceptDraw() {
		return false;
	}

	@Override
	public void saveInformation(String directory) {
		
	}

}
