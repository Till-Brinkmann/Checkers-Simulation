package NNStuff;
import checkers.Figure.FigureColor;
import checkers.Figure.FigureType;
import gui.Console;
import checkers.GameLogic;
import checkers.Move;
import checkers.Player;
import checkers.Playfield;

public class NNPlayer implements Player {

	private double[] inputVector;
	private double[] outputVector;
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
        outputVector = net.run(inputVector);
        double max = outputVector[0];
        int choiceField = 0;
        for(int i = 1; i< 32; i++) {
        	if(outputVector[i] > max){
        		max = outputVector[i];
        		choiceField = i;
        	}
        }
        
        int x = 8 / (choiceField - 8 % choiceField);
        int y = 8 % choiceField;
        x = (x+1)*2-1;
        y = (y+1)*2-1;
        
        max = outputVector[32];
        int choiceDirection = 0;
        for(int i = 32; i< 36; i++) {
        	if(outputVector[i] > max){
        		max = outputVector[i];
        		choiceDirection = i;
        	}
        }
        
//      Move m.Move.createMoveFromCoords(coords);
//;
//       gmlc.makeMove(m);
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
	
}
