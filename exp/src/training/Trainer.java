package training;

public interface Trainer {
	/**
	 * Evaluates the nets.
	 */
	public void evaluate();
	/**
	 * Does improvements to the net(s).
	 */
	public void improve();
	/**
	 * Provides an (optional) report of the current situation.
	 * E.g. the overall fitness of the nn, the number of epochs etc.
	 * @return A String with some information about the training.
	 */
	public String report();
}
