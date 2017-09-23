package gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.event.KeyEvent;

import javax.swing.*;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import generic.DLList;
import generic.List;


/**
 * provides a JPanel with a scrollable text output area
 * and a single line textarea for input
 * @author Till
 *
 */
public class Console extends JPanel{

	//TODO eine printCommand, print, printWarning, printError methode (oder so)
	private DLList<String> previousCommands;
	
	private List<CommandListener> listener;
	
	private JScrollPane scrollpaneOutput;
	private JScrollPane scrollpaneInput;
    private JTextArea output;
    private JTextArea input;
    private DefaultCaret caret;
    
	public Console() {
		super();
		previousCommands = new DLList<String>();
		previousCommands.add("");
		listener = new List<CommandListener>();
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
        		switch(e.getKeyCode()){
        		case KeyEvent.VK_ENTER:
        			e.consume();
        			//go to the top
        			while(!previousCommands.isEmpty() && previousCommands.hasNext()){
        				previousCommands.next();
        			}
        			if(!input.getText().equals("")){
            			previousCommands.addBefore(input.getText());
            			input.setText("");
        			}
        			//output.append(input.getText() + "\n");
        			break;
        		case KeyEvent.VK_UP:
        			e.consume();
        			if(previousCommands.hasPrevious()){
                   	   previousCommands.previous();
                      }
        			input.setText(previousCommands.get());
        			break;
        		case KeyEvent.VK_DOWN:
        			e.consume();
        			if(previousCommands.hasNext()){
    					previousCommands.next();
                    }
        			input.setText(previousCommands.get());
        			break;
        		}
        		
        	}
        });
		return scrollpaneInput;		
		
	}
	public void addCommandListener(CommandListener l){
		listener.append(l);
	}
	
	private void printCommand(String arg){
		output.append(">>" + arg + "\n");
	}
	public void printCommandOutput(String... args){
		for(int i = 0; i < args.length; i++){
			output.append("  " + args[i].replace("\n", "\n  ") + "\n");
		}
	}
	public void printInfo(String info, String from){
		output.append("[INFO] " + from + ": " + info);
	}
	public void printInfo(String info){
		printInfo(info, "Unknown");
	}
	public void printWarning(String warning, String from){
		output.append("[WARNING] " + from + ": " + warning);
	}
	public void printWarning(String warning){
		printWarning(warning, "Unknown");
	}
	
	public void updateBackground(Color color){
		setBackground(color);
	}
	public void updateForeground(Color color){
		output.setForeground(color);
		input.setForeground(color);
	}
}
