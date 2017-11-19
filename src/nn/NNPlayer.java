package nn;
import checkers.Figure;
import checkers.Figure.FigureColor;
import checkers.Figure.FigureType;
import gui.Console;
import checkers.GameLogic;
import checkers.Move;
import checkers.Move.MoveDirection;
import checkers.Move.MoveType;
import checkers.Player;
import checkers.Playfield;
import generic.List;

public class NNPlayer implements Player {
	/*
	 * das nn bekommt das feld als input und gibt das feld nach dem zug aus.
	 * Dann wird der h�chste wert im outputfeld genommen und es wird geguckt ob eine figur dieses erreichen kann
	 * 
	 */

	private double[] inputVector;
	private FigureColor aiFigureColor;
	private int round = 0;
    private double changeQuality = 50;
    NN net;
    public GameLogic gmlc;
    public Console console;
    public Playfield playfield; 
	public NNPlayer(GameLogic pGmlc, Console pConsole, NN pNet){
		gmlc = pGmlc;
		console = pConsole;        
        net = pNet;
        net.randomWeights(-1, 1);
        
        playfield = gmlc.getPlayfield();
	}
	@Override
	public void prepare(FigureColor color) {
		aiFigureColor = color;
		
	}
	@Override
	public void requestMove() {
		inputVector = new double[64];
		int field = 0;
        for(int y = 0;y < playfield.SIZE; y++){
            for(int x = 0;x < playfield.SIZE; x++){      
            	if(y%2 == 1) {
            		if(x%2 == 1) {
            			addValueTOInputVector(x,y,field);
    					field++;
            		}
            	}
            	else {
            		if(x%2 != 1) {
            			addValueTOInputVector(x,y,field);
    					field++;
            		}
            	}
			}
		}
        Move m = moveDecision(net.run(inputVector));
        
        if(m.getMoveType() == MoveType.JUMP){
        	if(gmlc.testMove(m)) {
        		List<Move> moveList = gmlc.testForMultiJump(m.getX(),m.getY());
        		while(!moveList.isEmpty()) {
        			if(moveList.get().getMoveDirection(0) == m.getMoveDirection(0)) {
        				multiJumpRun();
        				
        			}
        		}
        			
        	}
        	else {
        		m.setMoveType(MoveType.INVALID);
        	}
        }
    	gmlc.makeMove(m);
	}
	
	public void addValueTOInputVector(int x, int y, int field) {
		if(playfield.isOccupied(x, y)) {
			if(playfield.colorOf(x,y) == aiFigureColor) {
				if(playfield.getType(x,y) == FigureType.KING) {
					inputVector[field+32] = 1;
					inputVector[field] = 0;
				}
				else {
					inputVector[field] = 1;	
					inputVector[field+32] = 0;
				}
			}
			else {
				if(playfield.getType(x,y) == FigureType.KING) {
					inputVector[field+32] = -1;	
					inputVector[field] = 0;	
				}
				else {
					inputVector[field] = -1;	
					inputVector[field+32] = 0;
				}
			}			
		}
		else {
			inputVector[field] = 0;			
		}
	}
	
	private void multiJumpRun() {
		
		
	}
	private Move moveDecision(double[] outputVector) {
		Move move = new Move(MoveType.INVALID);
		//find out the field it wants to move to
		double max = Integer.MIN_VALUE;
        int choiceField = 0;
        for(int i = 1; i < 32; i++) {
        	if(outputVector[i] > max){
        		max = outputVector[i];
        		choiceField = i;
        	}
        }
        //test which figures can reach this field
        //TODO do it!
        List<Move> availableMoves = new List<Move>();
        int[][] coords = new int[2][2];
        for( Figure f : gmlc.getPlayfield().getFiguresFor(aiFigureColor)) {
        	coords[0][0] = f.x;
        	coords[0][1] = f.y;
        	coords[1] = fieldToCoords(choiceField);
        	move = Move.createMoveFromCoords(coords);
        	if(move.getMoveType() != MoveType.INVALID) {
        		//TODO der move zu dem feld ist m�glichirgendwie speichern oder so
        		availableMoves.append(move);
        	}
        }
        availableMoves.toFirst();
        if(availableMoves.length == 0){
        	move = new Move(MoveType.INVALID);
        }
        else {//case: there is a move that ends at this field
        	//find out if a figure that can do the move is on the field which is chosen by the nn to be the move start
        	max = Integer.MIN_VALUE;
        	for(int i = 32; i < 64; i++) {
            	if(outputVector[i] > max){
            		max = outputVector[i];
            		choiceField = i;
            	}
            }
        	coords[0] = fieldToCoords(choiceField);
        	
    		for(;availableMoves.hasAccess();availableMoves.next()){
    			move = availableMoves.get();
    			if(move.getX() == coords[0][0] && move.getY() == coords[0][1]){
    				break;
    			}
    			else{
    				move = new Move(MoveType.INVALID);
    			}
    		}
        }
        return move;
	}

	private int[] fieldToCoords(int choiceField) {
		int[] c = new int[2];
		c[0] = (choiceField - choiceField % 4) / 4;
        c[1] = choiceField % 4;
        c[1] *= 2;
        if(c[0]%2 == 1) {
        	c[1]++;
        	c[0] *= 2;
        	c[1]--;
        }
        else {
        	c[0] *= 2;
        }
		return c;
	}
	public void setGL(GameLogic gl){
		gmlc = gl;
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "standard NN";
	}

	@Override
	public boolean acceptDraw() {
		// TODO Auto-generated method stub
		return false;
	}
	public FigureColor getFigureColor() {
		return aiFigureColor;	
	}
}
