package checkers;

public class Playfield {

	public final int SIZE;
	public Playfield() {
		this(8);
	}
	
	/**
	 * can be used by any superclass to customize basic required parameters
	 * @param size
	 */
	public Playfield(int size){
		SIZE = size;
	}

}
