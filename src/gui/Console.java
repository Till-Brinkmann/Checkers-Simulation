package gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.event.KeyEvent;
import java.util.Arrays;

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
@SuppressWarnings("serial")
public class Console extends JPanel{
	private DLList<String> previousCommands;

	private List<CommandListener> listener;
	private JScrollPane scrollpaneOutput;
	private JScrollPane scrollpaneInput;
	public JTextArea output;
	private JTextArea input;
	private DefaultCaret caret;
	
	private int maxLines = 180;
	private int lines;

	private int fontSize = 14;
	private String fontStyle = "BOLD";
	private String fontName = "Arial";
	private List<String> fontList;
	public Console() {
		super();
		previousCommands = new DLList<String>();
		previousCommands.add("");
		listener = new List<CommandListener>();
		setPreferredSize(new Dimension(350,700));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(createOutput());
		add(createInput());
		lines = 0;
		//create fontList
		fontList = new List<String>();
		for ( String fonts : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames() ) 
			fontList.append(fonts);		
	}
	private JScrollPane createOutput(){
		output = new JTextArea();
		output.setEditable(false);
        output.setLineWrap(true);
        output.setWrapStyleWord(true);
		output.setFont(new Font(fontName, Font.BOLD, fontSize));
		caret = (DefaultCaret)output.getCaret();
		
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		scrollpaneOutput = new JScrollPane(output, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollpaneOutput.setPreferredSize(new Dimension(300,650));
		return scrollpaneOutput;
	}
	private JScrollPane createInput(){
		input = new JTextArea();
		input.setPreferredSize(new Dimension(315,19));
		input.setLineWrap(true);
		input.setWrapStyleWord(true);
		input.setFont(new Font(fontName, Font.BOLD, fontSize));
		scrollpaneInput = new JScrollPane(input);



        input.addKeyListener(new KeyAdapter()
        {
        	@Override
        	public void keyPressed(KeyEvent e) {
        		switch(e.getKeyCode()){
        		case KeyEvent.VK_ENTER:
        			String in = input.getText();
        			e.consume();
        			//it's a command
        			if(in.startsWith("/") && !in.startsWith("//")){
        				printCommand(input.getText());

        				processCommand(in);

            			while(!previousCommands.isEmpty() && previousCommands.hasNext()){
            				previousCommands.next();
            			}
            			if(!input.getText().equals("")){
                			previousCommands.addBefore(input.getText());
                			input.setText("");
            			}
        			}
        			//it's something different
        			else {

        			}
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
	
	public void removeCommandListener(CommandListener l){
		listener.toFirst();
		while(listener.hasAccess()){
			if(listener.get() == l){
				listener.remove();
				return;
			}
			listener.next();
		}
	}

	private void processCommand(String in) {
		boolean wasProcessed = true;
		//cut the slash off
		
		String command = in.substring(1,in.indexOf(" ") < 1 ? in.length() : in.indexOf(" "));
		String[] args;
		if(in.length() > 1){
			args = in.split(" ");
			args = Arrays.copyOfRange(args, 1, args.length);
		}
		else {
			args = new String[0];
		}
		
		//own Commands
		switch (command){
		case "exit":
			System.exit(0);
			break;
		case "sayhello":
			printCommandOutput("This is an easteregg!!:", "Hello World");
			break;
		case "set":
			if(args.length == 2){
				if(args[0].equals("MaxLines")){
					try{
						maxLines = Math.max(1, Integer.parseInt(args[1]));
					}
					catch(NumberFormatException e){
						printCommandOutput("second Argument has to be a number!");
					}
				}
				if(args[0].equals("FontSize")){
					try{
						fontSize = Math.max(1, Integer.parseInt(args[1]));
					}
					catch(NumberFormatException e){
						printCommandOutput("second Argument has to be a number!");
					}
					changeFont();
				}	
				if(args[0].equals("FontStyle")) {
					switch(args[1]) {
					case "BOLD":
						fontStyle = "BOLD";
						printInfo("Changed FontStyle to BOLD","Console");
						break;
					case "PLAIN":
						fontStyle = "PLAIN";
						printInfo("Changed FontStyle to PLAIN","Console");
						break;
					case "ITALIC":
						fontStyle = "ITALIC";
						printInfo("Changed FontStyle to ITALIC","Console");
						break;
					default:
						printWarning(args[1] +" was not found.");
						break;
					}
					changeFont();
				}
			}
			if(args[0].equals("FontType")) {
				String newFontName = "";
				if(args.length == 2) {
					newFontName = args[1];
				}
				if(args.length == 3) {
					newFontName = args[1] + " " + args[2];
				}
				if(args.length == 4) {
					newFontName = args[1] + " " + args[2] + " " + args[3];
				}
				if(args.length == 5) {
					newFontName = args[1] + " " + args[2] + " " + args[3] + " " + args[4];
				}
				fontList.toFirst();
				while(fontList.hasAccess()) {
					if(newFontName.equals(fontList.get())) {
						printInfo("Font was found.","Console");
						fontName = newFontName;
						changeFont();
						return;
					}
					fontList.next();
				}
				printWarning("There is no Font with this name. Maybe you wrote it wrong.Please check the /availableFonts command for further information.","Console");
				
			}
			//TODO implementieren
			break;
		case "availableCommands":
			commandInfos();
			break;
		case "availableFonts":
			availableFonts();
			break;
		default:
			wasProcessed = false;
			break;
		}
		//go through all listeners
		listener.toFirst();
		while(listener.hasAccess()){
			if(listener.get().processCommand(command, args)){
				wasProcessed = true;
			}
			listener.next();
		}
		if(!wasProcessed){
			printCommandOutput("The command could not be processed by any module.", "Maybe you wrote it wrong.");
		}
	}
	public void commandInfos() {
		printInfo("Available console commands:","GUI");
		print("/set FontSize [number]");
		print("/set MaxLines [number]");
		print("/set FontType [font name]");
		print("/set FontStyle [Bold],[ITALIC] or [PLAIN]");
		print("/sayhello");
		print("/exit");
		print("/availableFonts");
		print("/availableCommands");
	}
	public synchronized void print(String arg){
		output.append(arg + "\n");
		lines++;
		if(lines >= maxLines){
			output.setText("");
			lines = 0;
		}
	}
	private void printCommand(String arg){
		print(">>" + arg);
	}
	public void printCommandOutput(String... args){
		for(int i = 0; i < args.length; i++){
			print("  " + args[i].replace("\n", "\n  "));
		}
	}
	public void printInfo(String info, String from){
		print("[INFO] " + from + ": " + info);
	}
	public void printInfo(String info){
		printInfo(info, "Unknown");
	}
	public void printWarning(String warning, String from){
		print("[WARNING] " + from + ": " + warning);
	}
	public void printWarning(String warning){
		printWarning(warning, "Unknown");
	}
	public void updateBackground(Color color){
		setBackground(color);
	}
	public void printError(String error, String from) {
		print("[ERROR] " + from + ": " + error);
	}
	public void printError(String error){
		printError(error, "Unknown");
	}
	public void updateForeground(Color color){
		output.setForeground(color);
		input.setForeground(color);
}
	public void changeFont() {
		if(fontStyle.equals("BOLD")) {
			input.setFont(new Font(fontName, Font.BOLD, fontSize));
			output.setFont(new Font(fontName, Font.BOLD, fontSize));
		}
		else if(fontStyle.equals("PLAIN")) {
			input.setFont(new Font(fontName, Font.PLAIN, fontSize));
			output.setFont(new Font(fontName, Font.PLAIN, fontSize));
		}
		else {
			input.setFont(new Font(fontName, Font.ITALIC, fontSize));
			output.setFont(new Font(fontName, Font.ITALIC, fontSize));
		}
	}
	public void availableFonts() {
		fontList.toFirst();
		while(fontList.hasAccess()) {
			print(fontList.get());
			fontList.next();
		}
	}

}
