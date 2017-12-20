package gui;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import checkers.Figure;
import checkers.Figure.FigureColor;
import checkers.GameLogic;
import checkers.Move;
import checkers.Move.MoveType;
import checkers.Player;
import checkers.Playfield;

import generic.List;
/**
 *
 * @author Till
 *
 */
@SuppressWarnings("serial")
public class PlayfieldPanel extends JPanel implements PlayfieldDisplay, Player{

	public ImageIcon king;
	private Playfield playfield;
	private JButton[][] buttons;
	GameLogic gamelogic;
	Console console;
	CommandListener drawDecision;
	//for move-making
	private int[][] coords;
	private int[][] multiJumpOptions;

	private enum Click{ZERO,FIRST,SECOND};
	private Click clickSituation = Click.ZERO;
	//the PlayfieldPanel must support up to two player
	FigureColor figurecolor;
	List<Figure> jumpFigures;
	private boolean hasChosen;
	private boolean wantsDraw;
	List<Move> list;

	public PlayfieldPanel(GameLogic pGamelogic, Console pConsole){
		super();
		king = new ImageIcon("resources/Icons/dame.png");
		gamelogic = pGamelogic;
		console = pConsole;
		//TODO darüber muss man noch nachdenken
//		console.addCommandListener(new CommandListener(){
//			public boolean processCommand(String command){
//				if(command.equals("requestDraw")){
//					gamelogic.requestDraw();
//					return true;
//				}
//				return false;
//			}
//		});
		drawDecision = new CommandListener(){
			@Override
			public boolean processCommand(String command, String[] args){
				switch(command){
				case "yes":
				case "y":
					hasChosen = true;
					wantsDraw = true;
					return true;
				case "no":
				case "n":
					hasChosen = true;
					wantsDraw = false;
					return true;
				}
				return false;
			}
		};
		playfield = gamelogic.getPlayfield();
		playfield.setPlayfieldDisplay(this);
		coords = new int[2][2];
		multiJumpOptions = new int[3][2];
		jumpFigures = new List<Figure>();
		buttons = new JButton[playfield.SIZE][playfield.SIZE];
		createPlayfieldPanel();
		enableAllButtons(false);
	}
	public void createPlayfieldPanel(){
		setLayout(new GridLayout(playfield.SIZE,playfield.SIZE));
		setPreferredSize(new Dimension(700,700));
        for (int y = playfield.SIZE - 1; y >= 0 ; y--) {
            for(int x = 0; x < playfield.SIZE; x++){
                buttons[x][y] = new JButton();
                buttons[x][y].setBackground(Color.lightGray);
                buttons[x][y].setVerticalTextPosition(SwingConstants.BOTTOM);
                buttons[x][y].setHorizontalTextPosition(SwingConstants.CENTER);
                buttons[x][y].setIconTextGap(0);
                buttons[x][y].setActionCommand(x +""+ y);
                buttons[x][y].setEnabled(true);
                buttons[x][y].setBorder(BorderFactory.createLineBorder(Color.GRAY));
                add (buttons[x][y] );
            }
        }
        for (int y = 0; y < playfield.SIZE; y++) {
            for(int x = 0; x < playfield.SIZE; x++){
            	buttons[x][y].addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent event)
                    {
                    	String location = event.getActionCommand();
                    	saveCoordinates(Character.getNumericValue(location.charAt(0)),Character.getNumericValue(location.charAt(1)));
                    }
                  });
            }
        }
	}
	public void buttonNumeration(boolean selected) {
        for (int y = 0; y < playfield.SIZE; y++) {
            for(int x = 0; x < playfield.SIZE; x++){
            	if(selected){
            		buttons[x][y].setText( "(" + (x+1) + ")(" + (y+1) + ")" );
            	}
            	else{
            		buttons[x][y].setText("");
            	}
            }
        }

	}
	/**
	 * @return an array of all the buttons the field exists of
	 */
	public JButton[][] getButtons(){
		return buttons;
	}

	private void setButtonColor(int x, int y, Color color){
		buttons[x][y].setBackground(color);
	}
	private void setButtonIcon(int x, int y, ImageIcon icon) {
		buttons[x][y].setIcon(icon);
	}

	@Override
	public void updateDisplay() {
		for(int y = 0; y < playfield.SIZE; y++){
			for(int x = 0; x < playfield.SIZE; x++){
					updateField(x, y);
			}
		}
	}
	@Override
	public void updateField(int x, int y) {
		if(playfield.isOccupied(x, y)){
			switch (playfield.field[x][y].getFigureType()){
				case KING:
					setButtonIcon(x, y, king);
					break;
				case NORMAL:
					setButtonIcon(x, y, null);
					break;
			}
			switch (playfield.field[x][y].getFigureColor()){
			case WHITE:
				setButtonColor(x, y, Color.white);
				return;
			case RED:
			//TODO das richtige rot für die figuren finden
				setButtonColor(x, y, new Color(160,10,10));
				return;
		}
		}
		else {
			setButtonColor(x, y, Color.lightGray);
			setButtonIcon(x, y, null);
		}
	}

	private void saveCoordinates(int x, int y) {
		switch(clickSituation) {
		case ZERO:
			if(playfield.isOccupied(x, y)){
				if(jumpIsPossible()){
					//if a jump is possible only figures that can jump can be clicked
					jumpFigures.toFirst();
					while(jumpFigures.hasAccess()){
						if(playfield.field[x][y] == jumpFigures.get()){
							selectFigure(x, y);
						}
						jumpFigures.next();
					}
				}
				else {
						selectFigure(x,y);
				}
			}
			break;
		case FIRST:
			if(coords[0][0] == x && coords[0][1] == y){
				//unselect figure
				buttons[x][y].setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
				clickSituation = Click.ZERO;
				return;
			}
			//you can not move to an occupied field
			if(!playfield.isOccupied(x, y)){
				coords[1][0] = x;
				coords[1][1] = y;
				Move m = Move.createMoveFromCoords(coords);
				if(m.isInvalid() || !gamelogic.testMove(m) || (jumpIsPossible() && m.getMoveType() == MoveType.STEP)){
					//TODO cancel move
					console.printWarning("Invalid move", "playfieldPnael");
					buttons[coords[0][0]][coords[0][1]].setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
					clickSituation = Click.ZERO;
					return;
				}
				if(m.getMoveType() == MoveType.JUMP){
					//multiJumpTesting
					list = gamelogic.testForMultiJump(coords[0][0],coords[0][1]);
					list.toFirst();
					while(list.hasAccess()) {
						if(list.get().getMoveType() == MoveType.MULTIJUMP ) {
							if(list.get().getMoveDirection(0).equals(m.getMoveDirection(0))){
								console.printInfo("A Mulitjump was found","playfieldPanel");
							}
							else {
								list.remove();
							}
						}else {
							list.remove();
						}
						list.next();
					}
					if(list.length != 0) {
						list.toFirst();
						int i = 0;
						while(list.hasAccess()) {
							switch(list.get().getMoveDirection(1)) {
								case BL:
									multiJumpOptions[i][0]	= coords[1][0]-2;
									multiJumpOptions[i][1]	= coords[1][1]-2;
									break;
								case BR:
									multiJumpOptions[i][0]	= coords[1][0]+2;
									multiJumpOptions[i][1]	= coords[1][1]-2;
									break;
								case FL:
									multiJumpOptions[i][0]	= coords[1][0]-2;
									multiJumpOptions[i][1]	= coords[1][1]+2;
									break;
								case FR:
									multiJumpOptions[i][0]	= coords[1][0]+2;
									multiJumpOptions[i][1]	= coords[1][1]+2;
									break;
							}
							buttons[multiJumpOptions[i][0]][multiJumpOptions[i][1]].setBorder(BorderFactory.createLineBorder(Color.GRAY, 4));
							i++;
							list.next();
						}
						clickSituation = Click.SECOND;
						return;
					}	
					
				}
				if(m.getMoveType() == MoveType.JUMP) {
				console.printInfo("Jump: (" + coords[0][0] + "/" + coords[0][1] + ") - ("+ coords[1][0] + "/" + coords[1][1] + ")","playfieldPanel");
				}
				if(m.getMoveType() == MoveType.STEP) {
				console.printInfo("Step: (" + coords[0][0] + "/" + coords[0][1] + ") - ("+ coords[1][0] + "/" + coords[1][1] + ")","playfieldPanel");
				}
				resetAndExecute(m,0);
				
			}
			break;
		case SECOND:
			for(int i = 0; i < list.length;i++) {
				if(multiJumpOptions[i][0] == x && multiJumpOptions[i][1] == y) {
					list.toFirst();
					for(int j = 0; i < j;j++) {
						list.next();
					}
					console.printInfo("Multijump: (" + coords[0][0] + "/" + coords[0][1] + ") - ("+ coords[1][0] + "/" + coords[1][1] + ") - ("+ multiJumpOptions[i][0] + "/" + multiJumpOptions[i][1] + ")","PlayfieldPanel");
					resetAndExecute(list.get(),i);
				}
			}
			break;
		}

	}
	private void resetAndExecute(Move m,int i) {
		if(gamelogic.getTwoPlayerMode()){
			//toggle color
			figurecolor = (figurecolor == FigureColor.RED) ? FigureColor.WHITE : FigureColor.RED;
		}		
		buttons[coords[0][0]][coords[0][1]].setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		buttons[coords[1][0]][coords[1][1]].setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		buttons[multiJumpOptions[i][0]][multiJumpOptions[i][1]].setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		clickSituation = Click.ZERO;
		enableAllButtons(false);
		gamelogic.makeMove(m);
	}
	private void selectFigure(int x, int y){
		coords[0][0] = x;
		coords[0][1] = y;
		clickSituation = Click.FIRST;
		buttons[x][y].setBorder(BorderFactory.createLineBorder(Color.GRAY, 4));
	}

	private boolean jumpIsPossible() {
		//TODO gibt fehlerhafte werte zurück
		boolean canJump = false;
		jumpFigures = new List<Figure>();
		for(Figure f : playfield.getFiguresFor(figurecolor)){
			if(Move.getPossibleJumps(f, playfield).length > 0){
				canJump = true;
				jumpFigures.append(f);
			}
		}
		return canJump;
	}

	public void enableAllButtons(boolean enabled) {
		for(int x = 0; x < playfield.SIZE; x++){
			for(int y = 0; y < playfield.SIZE; y++){
				buttons[x][y].setEnabled(enabled);
			}
		}
	}

	@Override
	public void prepare(FigureColor color) {
		figurecolor = color;
	}
	@Override
	public void requestMove() {
		//enable all buttons exept for enemy figures
		enableAllButtons(true);
		for(int x = 0; x < playfield.SIZE; x++){
			for(int y = 0; y < playfield.SIZE; y++){
				if(playfield.isOccupied(x, y) && playfield.field[x][y].getFigureColor() != figurecolor)
				buttons[x][y].setEnabled(false);
			}
		}
	}
	@Override
	public String getName(){
		return "Human player (PlayfieldPanel)";
	}
	/**
	 * has to be synchronized because otherwise it is not able to wait.
	 */
	@Override
	public synchronized boolean acceptDraw(){
		//console.addCommandListener(drawDecision);
		console.printInfo("do you accept a draw? [yes/no] (default no)", "PlayfieldPanel");
		//int counter = 5;
		//defaults to false
		wantsDraw = false;
//		while(!hasChosen && counter != 0){
//			console.print(String.valueOf(counter));
//			try {
//				wait(1000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			counter--;
//		}
//		console.removeCommandListener(drawDecision);
//		hasChosen = false;
		return wantsDraw;
	}
}
