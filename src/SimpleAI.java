import checkers.Figure.FigureColor;
import checkers.Figure.FigureType;
import generic.List;
import gui.Console;


import checkers.GameLogic;
import checkers.Move;
import checkers.Move.MoveType;
import checkers.Player;
import checkers.Playfield;

public class SimpleAI implements Player {
	GameLogic gmlc;
	Console console;
	String name;
	Playfield playfield;
	FigureColor aiFigureColor;
	
	int[] qualityArray;
	int minEvaluationScore = 5;
	int depth = 3;
	
	public SimpleAI(GameLogic pGmlc, Console pConsole) {
		name = "Simple AI";
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
		qualityArray = new int[moveList.length];
		for(int i = 0;i < qualityArray.length;i++) {
			qualityArray[i] = 0;
		}
		//recursion
		int moveNumber = 0;
		Playfield newPlayfield;
		while(moveList.hasAccess()) {
			newPlayfield = playfield.copy();
			newPlayfield.executeMove(moveList.get());
			int quality = moveEvaluation(moveList.get(),newPlayfield);
			moveListLoop(moveOutsorting(newPlayfield,reverseFigureColor(aiFigureColor)),0, newPlayfield, reverseFigureColor(aiFigureColor), moveNumber, quality);
			moveNumber++;
			moveList.next();
		}
		//best move quality
		int bestQualityMove = 0;
		for(int i = 0; i < qualityArray.length; i++){
			if(qualityArray[i] > bestQualityMove) {
				bestQualityMove = i;
			}
		}
		moveList.toFirst();
		for(int i = 0; i < moveList.length;i++) {
			if(i == bestQualityMove) {
				return moveList.get();
			}
			moveList.next();
		}
		return null;
	}
	@SuppressWarnings("static-access")
	private void moveListLoop(List<Move> moveList, int currentDepth, Playfield newPlayfield,FigureColor newColor, int moveNumber, int quality) {
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
						quality += moveEvaluation(moveList.get(),newPlayfield);
						moveListLoop(moveOutsorting(newPlayfield,reverseFigureColor(newColor)),currentDepth, newPlayfield.copy(), reverseFigureColor(newColor),moveNumber, quality);
					}
					else {
						quality -= moveEvaluation(moveList.get(),newPlayfield);
						moveListLoop(moveOutsorting(newPlayfield,reverseFigureColor(newColor)),currentDepth+1, newPlayfield.copy(), reverseFigureColor(newColor), moveNumber, quality);
					}
					moveList.next();
				}else {
					if(quality > qualityArray[moveNumber]) {
						qualityArray[moveNumber] = quality;
					}
				}
			}
		}
	}
	private List<Move> moveOutsorting(Playfield newPlayfield,FigureColor figureColor) {

		List<Move> moveList = Move.getPossibleMoves(figureColor,newPlayfield);
		moveList.toFirst();
		while(moveList.hasAccess()) {			
			moveList.get().setScore(moveEvaluation(moveList.get(),newPlayfield));
			moveList.next();
		}
		if(moveList.isEmpty()) {
			return null;
		}
		
		return moveSelection(moveList);		
	}	
	private List<Move> moveSelection(List<Move> moveList){
		List<Move> finalList = new List<Move>();		
		//criteria for Selection unclear. Either a limited amount of moves per Recursion or an overall score limit
		//I would personally go for the move OBERGRENZE! (5 Moves)
		if(moveList.length <= 5) {
			return moveList;
		}
		int bestScore = 0;
		int currentScore;
		while(finalList.length != 6) {		
			moveList.toFirst();
			while(moveList.hasAccess()) {
				currentScore = moveList.get().getScore();
				if(currentScore > bestScore) {
					bestScore = currentScore;
				}
				moveList.next();
			}
			moveList.toFirst();
			while(moveList.get().getScore() != bestScore) {
				moveList.next();
			}
			finalList.append(moveList.get());
			moveList.remove();
			bestScore = 0;
		}
		return finalList;		
	}
	private int moveEvaluation(Move m, Playfield newPlayfield) {
		int quality = 0;
		int x = m.getX();
		int y = m.getY();
//		FigureColor color = newPlayfield.field[x][y].getFigureColor();
//		FigureType type = newPlayfield.field[x][y].getFigureType();
		if(m.getMoveType() == MoveType.JUMP) {
			quality=+2;
		}
		if(m.getMoveType() == MoveType.MULTIJUMP) {
			quality=+6;
		}
		if(x == 1 || x == 6) {
			quality=+10;
		}
		return quality;
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
