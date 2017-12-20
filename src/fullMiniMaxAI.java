import checkers.Figure.FigureColor;
import checkers.Figure.FigureType;
import generic.List;
import gui.Console;


import checkers.GameLogic;
import checkers.Move;
import checkers.Player;
import checkers.Playfield;

public class fullMiniMaxAI implements Player {
	GameLogic gmlc;
	Console console;
	String name;
	Playfield playfield;
	FigureColor aiFigureColor;
	
	double[] qualityArray;
	double[] possibilitiesArray;
	int minEvaluationScore = 5;
	int depth = 12;
	
	public fullMiniMaxAI(GameLogic pGmlc, Console pConsole) {
		name = "FullMiniMaxAI";
		gmlc = pGmlc;
		console = pConsole;
	}
	@Override
	public void prepare(FigureColor color) {
		aiFigureColor = color;

	}

	@Override
	public void requestMove() {		
		Move finalMove;
		finalMove = algorithm();
		if(gmlc.testMove(finalMove)) {
		gmlc.makeMove(finalMove);
		}
		else {
			console.printWarning("SimpleAI","move wrong");
		}
	}

	private Move algorithm() {
		List<Move> moveList;
		playfield = gmlc.getPlayfield();
		moveList = Move.getPossibleMoves(aiFigureColor,playfield);
		moveList.toFirst();
		possibilitiesArray = new double[moveList.length];
		qualityArray = new double[moveList.length];
		for(int i = 0;i < qualityArray.length;i++) {
			qualityArray[i] = 0;
		}
		//recursion
		int moveNumber = 0;
		Playfield newPlayfield;
		while(moveList.hasAccess()) {
			newPlayfield = playfield.copy();
			newPlayfield.executeMove(moveList.get());
			moveListLoop(getNewMoveList(newPlayfield,reverseFigureColor(aiFigureColor)),0, newPlayfield, reverseFigureColor(aiFigureColor), moveNumber);
			moveNumber++;
			moveList.next();
		}
		//best move quality
		Double[] overallQuality = new Double[moveList.length];
		for(int i = 0; i < qualityArray.length; i++){
			overallQuality[i] = (qualityArray[i]);
		}
		moveList.toFirst();
		
		double bestMoveQuality = -1000;
		int bestMove = 0;
		for(int i = 0; i < moveList.length;i++) {
			if(overallQuality[i] > bestMoveQuality) {
				bestMoveQuality = overallQuality[i];
				bestMove = i;
			}
			moveList.next();
		}
		moveList.toFirst();	
		for(int i = 0; i < bestMove; i++) {
			moveList.next();
		}
		return moveList.get();
	}
	@SuppressWarnings("static-access")
	private void moveListLoop(List<Move> moveList, int currentDepth, Playfield newPlayfield,FigureColor newColor, int moveNumber) {
		if(moveList != null) {
			moveList.toFirst();
			while(moveList.hasAccess()) {
				Move m = moveList.get();
				if(gmlc.testMove(m,newPlayfield)) {
					newPlayfield.executeMove(m);
				}
				else {
					
					//console.printWarning("SimpleAI","Move not Possible. Depth:" + currentDepth );
					return;
				}
				if(currentDepth < depth) {
					if(newColor == aiFigureColor) {
						moveListLoop(getNewMoveList(newPlayfield,reverseFigureColor(newColor)),currentDepth, newPlayfield.copy(), reverseFigureColor(newColor),moveNumber);
					}
					else {
						moveListLoop(getNewMoveList(newPlayfield,reverseFigureColor(newColor)),currentDepth+1, newPlayfield.copy(), reverseFigureColor(newColor), moveNumber);
					}
					moveList.next();
				}else {						
					qualityArray[moveNumber] =+ moveEvaluation(newPlayfield);	
					possibilitiesArray[moveNumber]++;
				}
			}
		}
	}
	private int moveEvaluation(Playfield newPlayfield) {
		int quality;
		int enemyFigureDiff = playfield.getFigureQuantity(reverseFigureColor(aiFigureColor)) - newPlayfield.getFigureQuantity(reverseFigureColor(aiFigureColor));
		int ownFigureDiff = playfield.getFigureQuantity(aiFigureColor) - newPlayfield.getFigureQuantity(aiFigureColor);
		int enemyKingDiff = playfield.getFigureTypeQuantity(reverseFigureColor(aiFigureColor), FigureType.KING) - playfield.getFigureTypeQuantity(reverseFigureColor(aiFigureColor), FigureType.KING);
		int ownKingDiff = playfield.getFigureTypeQuantity(aiFigureColor, FigureType.KING) - playfield.getFigureTypeQuantity(aiFigureColor, FigureType.KING);
		
		quality = (enemyFigureDiff - ownFigureDiff) + (enemyKingDiff - ownKingDiff);
		
		return quality;
	}
	private List<Move> getNewMoveList(Playfield newPlayfield,FigureColor figureColor) {
		List<Move> moveList = Move.getPossibleMoves(figureColor,newPlayfield);
		return moveList;
	}	

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean acceptDraw() {
		// TODO Auto-generated method stub
		return false;
	}
	private FigureColor reverseFigureColor(FigureColor fc) {
		if(fc == FigureColor.RED) {
			return FigureColor.WHITE;
		}
		else {
			return FigureColor.RED;
		}
	}

}

