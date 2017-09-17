package gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.event.KeyEvent;

import javax.swing.*;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import generic.DLList;


/**
 * provides a JPanel with a scrollable text output area
 * and a single line textarea for input
 * @author Till
 *
 */
public class Console extends JPanel{

	//TODO eine printCommand, print, printWarning, printError methode (oder so)
	private DLList<String> previousCommands;
	
	private JScrollPane scrollpaneOutput;
	private JScrollPane scrollpaneInput;
    private JTextArea output;
    private JTextArea input;
    private DefaultCaret caret;
    
	public Console() {
		super();
		previousCommands = new DLList<String>();
		setPreferredSize(new Dimension(350,700));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(createOutput());
		add(createInput());
	}
	private JScrollPane createOutput(){
		output = new JTextArea();
		output.setPreferredSize(new Dimension(300,650));
		output.setEditable(false);
        output.setLineWrap(true);
        output.setWrapStyleWord(true);
		output.setFont(new Font("Arial", Font.BOLD, 15));
		caret = (DefaultCaret)output.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE); 
		scrollpaneOutput = new JScrollPane(output, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		return scrollpaneOutput;
	}
	private JScrollPane createInput(){
		input = new JTextArea();
		input.setPreferredSize(new Dimension(315,19));
		input.setFont(new Font("Arial", Font.BOLD, 15));		
		scrollpaneInput = new JScrollPane(input);
		
		
		
        input.addKeyListener(new KeyAdapter()
        {
        	@Override
        	public void keyPressed(KeyEvent e) {
        		e.consume();
        		switch(e.getKeyCode()){
        		case KeyEvent.VK_ENTER:
        			previousCommands.add(input.getText());
        			output.append(input.getText() + "\n");
        			input.setText("");
        			break;
        		case KeyEvent.VK_UP:
                    if(previousCommands.hasPrevious()){
                 	   input.setText(previousCommands.get());
                 	   previousCommands.previous();
                    }
        			break;
        		case KeyEvent.VK_DOWN:
                    if(previousCommands.hasNext()){
                    	input.setText(previousCommands.get());
                  	   previousCommands.next();
                    }
        			break;
        		}
        		
        	}
        });
		return scrollpaneInput;		
		
	}
	public void updateBackground(Color color){
		setBackground(color);
	}
	
	public void updateForeground(Color color){
		output.setForeground(color);
		input.setForeground(color);
	}
}
