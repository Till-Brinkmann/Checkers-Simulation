package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class AboutCS extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ImageIcon aboutscIcon;
	JPanel text;
	JTextArea textarea;
	public AboutCS() {
		super("About Checker Simulation");
		aboutscIcon = new ImageIcon("resources/Icons/dame.png");
		setIconImage(aboutscIcon.getImage());
		createWindow();
	}
	private void createWindow() {
		setResizable(false);
		setSize(400,300);
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		text = new JPanel();
		text.setBackground(Color.WHITE);
		textarea = new JTextArea();
		textarea.setPreferredSize(new Dimension(400,300));

		textarea.setEditable(false);
		textarea.setFont(new Font("Arial", Font.BOLD, 15));
		textarea.setLineWrap(true);
		textarea.setWrapStyleWord(true);
		textarea.setText("This project is made for the \"Jugend Forscht\" competition.\r\n" + 
				"\r\n" + 
				"The Goal is to program a versatile platform to test and compare different algorithms through the board game 'checkers'.\r\n" + 
				"\r\n" + 
				"In the finished program you will be able to add your own checker algorithms or neutral networks as a module written in java.\n\n" +
				"Creator:\n" +
				"Till Brinkmann\nMarco Adamczyk\nCan Timur Ward");
		text.add(textarea);
		add(text);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		pack();
	}
}
