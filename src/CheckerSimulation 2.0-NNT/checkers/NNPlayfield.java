package checkers;
/**
 * special efficient playfield for nn training
 *
 */
public class NNPlayfield {
	
	public static final boolean RED = true;
	public static final boolean WHITE = false;
	
	int figuresRed, figuresWhite, kings;

	public NNPlayfield(int figuresRed, int figuresWhite, int kings) {
		this.figuresRed = figuresRed;
		this.figuresWhite = figuresWhite;
		this.kings = kings;
	}
	
	public static NNPlayfield startPosition() {
		return new NNPlayfield(
				0b00000000000000000000111111111111,
				0b11111111111100000000000000000000,
				0
				);
	}
	
	public NNPlayfield copy() {
		return new NNPlayfield(figuresRed, figuresWhite, kings);
	}

	public void executeMove(NNMove m){
		if(m == null || m == NNMove.INVALID) return;
		//if the figure is red
		if((figuresRed & 1<<m.from) != 0){
			//remove figure from old spot
			figuresRed &= ~(1<<m.from);
			//set figure at new spot
			figuresRed |= (1<<m.to);
			//geschlagene figuren löschen
			figuresWhite &= ~m.beatenFigures;
		}
		else{
			figuresWhite &= ~(1<<m.from);
			figuresWhite |= (1<<m.to);
			figuresRed &= ~m.beatenFigures;
		}
		//update kings
		if((kings & 1<<m.from) != 0) {
			kings |= 1<<m.to;
		}
		else {
			kings &= ~(1<<m.to);
		}
		kings &= ~(1<<m.from);
		//king einträge geschlagener Figuren löschen
		kings &= ~m.beatenFigures;
	}
	
	public void toKing(byte field){
		kings |= 1<<field;
	}

	public boolean isOccupied(byte field){
		return ((figuresRed | figuresWhite) & 1<<field) != 0;
	}
	public boolean isOccupiedByColor(byte field, boolean color) {
		return ((color ? figuresRed : figuresWhite) & 1<<field) != 0;
	}
	
	public boolean isKing(byte field){
		return (kings & 1<<field) != 0;
	}
	
	public byte getFigureQuantity(){
		return (byte)Integer.bitCount(figuresRed | figuresWhite);
	}
	public byte getFigureQuantityOfColor(boolean isRed){
		if(isRed){
			return (byte)Integer.bitCount(figuresRed);
		}
		return (byte)Integer.bitCount(figuresWhite);
	}
	
	public byte[] getFigurePositionsOfType(boolean isRed){
		byte[] positions = new byte[getFigureQuantityOfColor(isRed)];
		int counter = 0;
		for(int i = 0; i < 32; i++){
			if(((isRed ? figuresRed : figuresWhite) & 1<<i) != 0){
				positions[counter] = (byte)i;
				counter++;
			}
		}
		return positions;
	}

	public static int fieldDistance(byte from, byte to) {
		//if the y  value of the move is even
		if(Math.floor(from/4) % 2 == 0){
			if(to-from == 4 || to-from == 3 || to-from == -5 || to-from == -4) {
				return 1;
			}
			
		}
		else {
			if(to-from == -4 || to-from == -3 || to-from == 5 || to-from == 4) return 1;
		}
		return 0;
	}
}