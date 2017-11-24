package gui;

public interface CommandListener {
	/**
	 * method to process the command passed in the command argument
	 * @param command
	 * @return true if the command could be processed otherwise false
	 */
	public boolean processCommand(String command, String[] args);
}
