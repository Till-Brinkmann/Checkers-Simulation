package network;

import checkers.Figure.FigureColor;
import checkers.Player;

public class NetworkPlayer implements Player {

	private String name = "Online Player";
	public NetworkPlayer(NetworkManager manager) {
	}
    @Override
	public void prepare(FigureColor color) {
		// TODO Auto-generated method stub

	}

	@Override
	public void requestMove() {
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public boolean acceptDraw() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void saveInformation(String directory) {
		// TODO Auto-generated method stub

	}

}
