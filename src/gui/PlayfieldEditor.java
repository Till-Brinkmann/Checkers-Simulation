package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;



public class PlayfieldEditor extends JFrame {
	
	private Console console;
	private PlayfieldPanel playfieldpanel;
	private GUI gui;
	enum SelectedFigure{NORMAL_RED, NORMAL_WHITE, KING_RED, KING_WHITE, EMPTY}; 
	private SelectedFigure figure;
	private ImageIcon kingIcon;
	private JButton normalR;
	private JButton normalW;
	private JButton kingR;
	private JButton kingW;
	private JButton empty;
	private JButton clear;
	private JButton applyOnBoard;
	private JButton flipPf;
	private JButton showCoordinates;
	
	public PlayfieldEditor(Console console,GUI gui) {
		super("Editor");
		
		kingIcon = new ImageIcon("resources/Icons/dame.png");
		this.gui = gui;
		this.console = console;
		
		//TODO funktionier nicht
		setIconImage(new ImageIcon("recources/Icons/Editor.png").getImage());
		
		setResizable(true);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
		playfieldpanel = new PlayfieldPanel(gui.gmlc.getPlayfield().SIZE);
		playfieldpanel.addOneActionListenerToAll(new ActionListener()
              {
              public void actionPerformed(ActionEvent event)
              {
              	String location = event.getActionCommand();
              	changeFieldState(Character.getNumericValue(location.charAt(0)),Character.getNumericValue(location.charAt(1)));
              }
            });
		playfieldpanel.enableAllButtons(false);
		add(playfieldpanel);
		add(createOptionsPanel());		
		pack();
	}
	protected void changeFieldState(int x, int y) {
		switch(figure) {
		case EMPTY:
			playfieldpanel.setButtonColor(x, y, Color.lightGray);
			playfieldpanel.setButtonIcon(x, y, null);
			break;
		case KING_RED:
			playfieldpanel.setButtonIcon(x, y, kingIcon);
			playfieldpanel.setButtonColor(x, y, new Color(160,10,10));
			break;
		case KING_WHITE:
			playfieldpanel.setButtonIcon(x, y, kingIcon);
			playfieldpanel.setButtonColor(x, y, Color.WHITE);
			break;
		case NORMAL_RED:
			playfieldpanel.setButtonColor(x, y, new Color(160,10,10));
			playfieldpanel.setButtonIcon(x, y, null);
			break;
		case NORMAL_WHITE:
			playfieldpanel.setButtonColor(x, y, Color.WHITE);
			playfieldpanel.setButtonIcon(x, y, null);
			break;
		default:
			break;
		}
		
	}
	private JPanel createOptionsPanel() {
		JPanel optionsPanel = new JPanel();
		optionsPanel.setBackground(Color.WHITE);
		optionsPanel.setLayout(new FlowLayout());
		optionsPanel.setPreferredSize(new Dimension(140,700));
		normalR = new JButton("Red figure");
		normalR.setBackground(Color.WHITE);
		normalR.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	playfieldpanel.enablePlayableButtons();
            	resetOptionButtons();
            	normalR.setBackground(Color.gray);
            	figure = SelectedFigure.NORMAL_RED;
            }
        });
		
		normalW = new JButton("White figure");
		normalW.setBackground(Color.WHITE);
		normalW.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	playfieldpanel.enablePlayableButtons();
            	resetOptionButtons();
            	normalW.setBackground(Color.gray);
            	figure = SelectedFigure.NORMAL_WHITE;
            }
        });
		kingR = new JButton("Red king");
		kingR.setBackground(Color.WHITE);
		kingR.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	playfieldpanel.enablePlayableButtons();
            	resetOptionButtons();
            	kingR.setBackground(Color.gray);
            	figure = SelectedFigure.KING_RED;
            }
        });
		
		kingW = new JButton("White king");
		kingW.setBackground(Color.WHITE);
		kingW.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	playfieldpanel.enablePlayableButtons();
            	resetOptionButtons();
            	kingW.setBackground(Color.gray);
            	figure = SelectedFigure.KING_WHITE;
            }
        });
		empty = new JButton("Empty field");
		empty.setBackground(Color.WHITE);
		empty.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	playfieldpanel.enablePlayableButtons();
            	resetOptionButtons();
            	empty.setBackground(Color.gray);
            	figure = SelectedFigure.EMPTY;
            }
        });
		clear = new JButton("Clear playfield");		
		clear.setBackground(Color.WHITE);
		clear.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	playfieldpanel.enablePlayableButtons();
            	playfieldpanel.clearField();
            }
        });
		showCoordinates = new JButton("Show coordinates");
		showCoordinates.setBackground(Color.WHITE);
		showCoordinates.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	if(showCoordinates.getBackground() == Color.WHITE) {
            		playfieldpanel.buttonNumeration(true);
            		showCoordinates.setBackground(Color.gray);
            	}
            	else {
            		playfieldpanel.buttonNumeration(false);
            		showCoordinates.setBackground(Color.WHITE);
            	}
            }
        });
		flipPf = new JButton("Flip playfield");
		flipPf.setBackground(Color.WHITE);
		flipPf.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {        
            	if(flipPf.getBackground() == Color.WHITE) {
            		flipPf.setBackground(Color.gray);
            	}
            	else {
            		flipPf.setBackground(Color.WHITE);
            	}
            	playfieldpanel.turnPlayfield();
            }
        });
		applyOnBoard = new JButton("Apply on board");
		applyOnBoard.setBackground(Color.WHITE);
		applyOnBoard.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {        		
            	if(!gui.gmlc.isInProgress()) {
            		setVisible(false);
            		gui.gmlc.getPlayfield().setGameSituation(playfieldpanel);
            		gui.playfieldplayer.setPlayfieldPanel(playfieldpanel);
            	}
            	else {
            		console.printWarning("A game is in progress. Please stop the game if you want to transfer the playfield","PlayfieldEditor");            		
            	}
            }
        });
		
		optionsPanel.add(normalR);
		optionsPanel.add(normalW);
		optionsPanel.add(kingR);
		optionsPanel.add(kingW);
		optionsPanel.add(empty);	
		optionsPanel.add(clear);
		optionsPanel.add(showCoordinates);
		optionsPanel.add(flipPf);
		optionsPanel.add(applyOnBoard);
		return optionsPanel;
	}
	private void resetOptionButtons(){
		normalR.setBackground(Color.WHITE);
		normalW.setBackground(Color.WHITE);
		kingR.setBackground(Color.WHITE);
		kingW.setBackground(Color.WHITE);
		empty.setBackground(Color.WHITE);
	}	

}
