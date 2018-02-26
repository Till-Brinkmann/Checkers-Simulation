package nn;

import checkers.Player;

public interface NNPlayer extends Player {
	public int getInputSize();
	public int getOutputSize();
	public NNPlayer clone();
	public NN getNet();
	public int getFitness();
	public void addFitness(int value);
	public void setFitness(int value);
	public int getRightMoves();
	public void setRightMoves(int value);
}
