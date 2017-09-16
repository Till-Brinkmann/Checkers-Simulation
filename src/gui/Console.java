package gui;

import javax.swing.*;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

/**
 * provides a JPanel with a scrollable text output area
 * and a single line textarea for input
 * @author Till
 *
 */
public class Console extends JPanel{

	private JScrollPane scrollpaneOutput;
    private JScrollPane scrollpaneInput;
    private JTextArea output;
    private JTextArea input;
    private DefaultCaret caret;
    
	public Console() {
		// TODO Auto-generated constructor stub
	}

}
