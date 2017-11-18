package gui;

import java.awt.TextArea;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ShowMoves extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2256803045454871738L;
	ImageIcon guideIcon;
	JPanel text;
	TextArea textarea;
	public ShowMoves() {
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
		textarea = new TextArea();
		
		text.add(textarea);
		add(text);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		pack();
	}
}
