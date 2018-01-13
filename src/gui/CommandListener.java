package gui;
/**
 * A CommandListener is used to add own commands to the console.
 */
public interface CommandListener {
	/**
	 * method to process the command passed in the command argument
	 * @param command
	 * @return true if the command could be processed otherwise false
	 */
	public boolean processCommand(String command, String[] args);
}
