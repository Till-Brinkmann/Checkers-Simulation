package gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.TextArea;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import checkers.Move;
import checkers.Move.MoveType;

public class Moves extends JFrame{
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -2256803045454871738L;
	private DefaultCaret caret;
	private ImageIcon guideIcon;
	private JPanel text;
	private JTextArea textarea;
	private int fontSize = 13;
	private String fontName = "Arial";
	private JScrollPane scrollpane;
	public Moves() {
		super("Moves");
		guideIcon = new ImageIcon("resources/Icons/guideIcon.png");
		setIconImage(guideIcon.getImage());
		createWindow();
	}
	private void createWindow() {
		setResizable(false);
		setSize(700,550);
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		text = new JPanel();
		textarea = new JTextArea();
		
		text.add(createTextField());
		add(text);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		pack();
	}
	private JScrollPane createTextField() {
		textarea.setEditable(false);
		textarea.setLineWrap(true);
		textarea.setWrapStyleWord(true);
		textarea.setFont(new Font(fontName, Font.BOLD, fontSize));
		caret = (DefaultCaret)textarea.getCaret();		
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        scrollpane = new JScrollPane(textarea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollpane.setPreferredSize(new Dimension(300,400));
		return scrollpane;
		

	}
	@SuppressWarnings("incomplete-switch")
	public void addMove(Move m) {
		String finalMoveMessage = "";
		int tmp;
		int x = m.getX();
		int y = m.getY();
		switch(m.getMoveType()) {
		case STEP:
			finalMoveMessage = "Step: ";
			break;
		case JUMP:
			finalMoveMessage = "Jump: ";
			break;
		case MULTIJUMP:
			finalMoveMessage = "Multijump: ";
			break;
		}
		if(m.getMoveType() == MoveType.STEP) {
			tmp = 1;
		}
		else {
			tmp = 2;
		}
		finalMoveMessage = finalMoveMessage+ " ( " + x + " / " + y + " ) ";
		for(int s = 0, steps = m.getSteps(); s < steps; s++){
			finalMoveMessage = finalMoveMessage + " to ";
			switch(m.getMoveDirection(s)){
			case BL:
				x = x - tmp;
				y = y - tmp;
				finalMoveMessage = finalMoveMessage + " ( " + x + " / " + y + " ) ";
				break;
			case BR:
				x = x + tmp;
				y = y - tmp;
				finalMoveMessage = finalMoveMessage + " ( " + x + " / " + y + " ) ";
				break;
			case FL:
				x = x - tmp;
				y = y + tmp;
				finalMoveMessage = finalMoveMessage + " ( " + x + " / " + y + " ) ";
				break;
			case FR:
				x = x + tmp;
				y = y + tmp;
				finalMoveMessage = finalMoveMessage + " ( " + x + " / " + y + " ) ";
				break;
			}
		}
		textarea.append(finalMoveMessage + "\n");
	}
	public void resetTextArea() {
		textarea.setText("");
	}
	public void updateFont(String fontName,int fontSize) {
		this.fontName = fontName;
		this.fontSize = fontSize;
		textarea.setFont(new Font(fontName, Font.BOLD, fontSize));

	}
}
