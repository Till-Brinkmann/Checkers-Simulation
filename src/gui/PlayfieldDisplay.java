package gui;
/**
 * Interface that every class implements, that is capable of displaying a Playfield in some way.
 */
public interface PlayfieldDisplay {
	public void updateDisplay();
	public void updateField(int x, int y);
}
