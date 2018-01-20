package gui;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import checkers.Move;
import checkers.Move.MoveType;
/**
 * A window that displays every move in the game as text.
 */
public class Moves extends JFrame{
	
	private DefaultCaret caret;
	private ImageIcon guideIcon;
	private JPanel text;
	private JTextArea textarea;
	private int fontSize = 13;
	private String fontName = "Arial";
	private JScrollPane scrollpane;
	/**
	 * Set the name of the window and calls another method for setting up the window.
	 */
	public Moves() {
		super("Moves");
		createWindow();
	}
	/**
	 * Sets up the window properties and adds the elements to the window.
	 */
	private void createWindow() {
		guideIcon = new ImageIcon("resources/Icons/guideIcon.png");
		setIconImage(guideIcon.getImage());
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
	/**
	 * Initializes and the returns a JScrollpane object.
	 * <p>
	 * @return A JScrollpane object.
	 */
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
	/**
	 * This method writes a distinct move, represented by a move object, as a String, into global text area.
	 * <p>
	 * @param m A move object.
	 */
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
	/**
	 * Clears the global text area.
	 */
	public void resetTextArea() {
		textarea.setText("");
	}
	/**
	 * Updates the used fonts and font size in the global text area.
	 * <p>
	 * @param fontName A stands for a specific font.
	 * @param fontSize An Integer that represent the font size.
	 */
	public void updateFont(String fontName,int fontSize) {
		this.fontName = fontName;
		this.fontSize = fontSize;
		textarea.setFont(new Font(fontName, Font.BOLD, fontSize));

	}
}
