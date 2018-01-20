package gui;
/**
 * An interface that every class, which is capable of displaying a playfield in some way, has to implement.
 */
public interface PlayfieldDisplay {
	/**
	 * Updates the whole board.
	 */
	public void updateDisplay();
	/**
	 * Updates a specific field on the board.
	 * <p>
	 * @param x       An integer variable which is representing a point on the vertical axis of the playfield.
	 * @param y	 	  An integer variable which is representing a point on the horizontal axis of the playfield.	
	 */
	public void updateField(int x, int y);
}
