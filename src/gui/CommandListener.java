package gui;
/**
 * A CommandListener is used to add own commands to the console.
 */
public interface CommandListener {
	/**
	 * A method to process the command passed in the command argument.
	 * <p>
	 * @param command
	 * @return true if the command could be processed otherwise false
	 */
	public boolean processCommand(String command, String[] args);
}
