package algo;

public class MiniMaxABManager{
	
	public final boolean random;
	
	private int alphaCuts;
	private int betaCuts;
	public MiniMaxABManager(boolean random) {
		this.random = random;
	}
	
	public void incrementAlphaCut() {
		alphaCuts++;
	}
	public void incrementBetaCut() {
		betaCuts++;
	}
	public int getAlphaCuts() {
		return alphaCuts;
		
	}
	public int getBetaCuts() {
		return betaCuts;
	}
}
