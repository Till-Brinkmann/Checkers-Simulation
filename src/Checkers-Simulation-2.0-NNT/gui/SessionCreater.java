package gui;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JFrame;

public class SessionCreater extends JFrame{
	
	public SessionCreater() {
		setLayout(new GridLayout());
		//initComponents();
		setSize(new Dimension(400,600));
		setVisible(true);
	}
}
