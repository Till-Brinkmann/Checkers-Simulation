package gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.event.KeyEvent;
import java.util.Arrays;

import javax.swing.*;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import datastructs.DLList;
import datastructs.List;
import network.NetworkManager;


/**
 * provides a JPanel with a scrollable text output area
 * and a single line textarea for input
 */
public class Console{
	/**
	 * Panel that holds all components.
	 */
	protected JPanel panel;
	/**
	 * All commands entered are saved in this double linked list
	 * and can be acessed by pressing the up/down arrow keys. 
	 */
	private DLList<String> previousCommands;
	/**
	 * List of all CommandListeners that registered themselves.
	 */
	private List<CommandListener> listener;
	/**
	 * ScrollPane for output.
	 */
	private JScrollPane scrollpaneOutput;
	/**
	 * ScrollPane for input.
	 */
	private JScrollPane scrollpaneInput;
	/**
	 * Textarea that displays the output.
	 */
	public JTextArea output;
	/**
	 * 
	 */
	private JTextArea input;
	private DefaultCaret caret;
	/**
	 * maximum number of lines in the console output area.
	 */
	private int maxLines = 1800;
	/**
	 * Current number of lines in the console
	 * (technically it is not always exactly the number of lines in the console but the number of calls to print() since it last reached maxLines )
	 */
	private int lines;
	/**
	 * Current fontsize.
	 */
	private int fontSize = 13;
	/**
	 * Current fontStyle("BOLD", "PLAIN" or "ITALIC")
	 */
	private String fontStyle = "BOLD";
	/**
	 * Name of the current font used.
	 */
	private String fontName = "Arial";
	/**
	 * Constructs a console object with default parameters.
	 */
	private NetworkManager networkManager;
	public Console() {
		panel = new JPanel();
		previousCommands = new DLList<String>();
		previousCommands.add("");
		listener = new List<CommandListener>();
		panel.setPreferredSize(new Dimension(350,700));
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(createOutput());
		panel.add(createInput());
		lines = 0;
	}
	/**
	 * Creates the output section of the console.
	 * <p>
	 * @return Returns a JScrollPane holding the console output area.
	 */
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
	/**
	 * Creates the input section of the console.
	 * <p>
	 * @return Returns a JScrollPane holding the console input area.
	 */
	private JScrollPane createInput(){
		input = new JTextArea();
		input.setPreferredSize(new Dimension(315,19));
		input.setLineWrap(true);
		input.setWrapStyleWord(true);
		input.setFont(new Font(fontName, Font.BOLD, fontSize));
		scrollpaneInput = new JScrollPane(input, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        input.addKeyListener(new KeyAdapter()
        {
        	@Override
        	public void keyPressed(KeyEvent e) {
        		processKeyEvent(e);
        	}
        		
        });
		return scrollpaneInput;

	}
	/**
	 * Processes the given KeyEvent. If the enter key was pressed it tests, if the input is a command or a message and based on that descision the next
	 * are induced.
	 * <p>
	 * @param keyevent
	 */
	private void processKeyEvent(KeyEvent keyevent){
		switch(keyevent.getKeyCode()){
		case KeyEvent.VK_ENTER:
			String in = input.getText();
			keyevent.consume();
			//it's a command
			if(in.startsWith("/") && !in.startsWith("//")){
				printCommand(input.getText());

				processCommand(in);
			}
			//it's something different
			else {
				//for the chat system
				print(">>>[" + networkManager.user + "]" + input.getText());
				if(networkManager.isConnected()) {
					networkManager.sendMessage(in);
				}
				else {
					printInfo("There is no connection available. Please try to connect to someone first.");
				}		
			}
			while(!previousCommands.isEmpty() && previousCommands.hasNext()){
				previousCommands.next();
			}
			if(!input.getText().equals("")){
    			previousCommands.addBefore(input.getText());
    			input.setText("");
			}
			break;
		case KeyEvent.VK_UP:
			keyevent.consume();
			if(previousCommands.hasPrevious()){
           	   previousCommands.previous();
              }
			input.setText(previousCommands.get());
			break;
		case KeyEvent.VK_DOWN:
			keyevent.consume();
			if(previousCommands.hasNext()){
				previousCommands.next();
            }
			input.setText(previousCommands.get());
			break;
		}
	}
    /**
     * Adds a CommandListener to the listeners List.
     * <p>
     * @param l An object with an implemented CommandListener interface
     */
	public void addCommandListener(CommandListener l){
		listener.append(l);
	}
    /**
     * Removes a CommandListener from the listeners List. If it does not exist, nothing changes.
     * <p>
     * @param l An object with an implemented CommandListener interface
     */
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
	/**
	 * This method test, if the console input equals to a existing command. If it does then the command is directly executed.
	 * <p>
	 * @param in The input which was typed in the console
	 */
	private void processCommand(String in) {
		boolean wasProcessed = false;
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
			wasProcessed = true;
			break;
		case "set":
			if(args.length == 2){
				if(args[0].equals("MaxLines")){
					try{
						maxLines = Math.max(10, Integer.parseInt(args[1]));
						printCommandOutput("Max Lines are now " + maxLines);
					}
					catch(NumberFormatException e){
						printCommandOutput("second Argument has to be a number!");
					}
					wasProcessed = true;
				}
				if(args[0].equals("FontSize")){
					try{
						fontSize = Math.max(8, Integer.parseInt(args[1]));
					}
					catch(NumberFormatException e){
						printCommandOutput("second Argument has to be a number!");
					}
					changeFont();
					wasProcessed = true;
				}	
				if(args[0].equals("FontStyle")) {
					switch(args[1]) {
					case "BOLD":
					case "PLAIN":
					case "ITALIC":
						fontStyle = args[1];
						printCommandOutput("Changed FontStyle to " + fontStyle);
						break;
					default:
						printCommandOutput(args[1] +" was not found.");
						break;
					}
					changeFont();
					wasProcessed = true;
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
				boolean notFound = true;
				for(String font : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
					if(newFontName.equals(font)) {
						printCommandOutput("Font was found.");
						fontName = newFontName;
						changeFont();
						notFound = false;
					}
				}
				if(notFound) 
					printCommandOutput(
						"There is no Font with this name. Maybe you wrote it wrong.",
						"Please check the /availableFonts command for further information.");
				wasProcessed = true;
			}
			break;
		case "availableCommands":
			commandInfos();
			wasProcessed = true;
			break;
		case "availableFonts":
			printCommandOutput(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
			wasProcessed = true;
			break;
		}
		//go through all listeners of user added commands
		for(listener.toFirst();listener.hasAccess();listener.next()){
			wasProcessed |= listener.get().processCommand(command, args);
		}
		if(!wasProcessed) printCommandOutput("The command could not be processed by any module.", "Maybe you wrote it wrong.");
	}
	/**
	 * Prints all available commands on the console.
	 */
	public void commandInfos() {
		printInfo("Available console commands:","GUI");
		print("/set MaxLines [number]");
		print("/set FontSize [number]");
		print("/availableFonts");
		print("/set FontType [font name]");
		print("/set FontStyle [Bold],[ITALIC] or [PLAIN]");
		print("/exit");
		print("/availableCommands");
	}
	public synchronized void print(String arg){
		output.append(arg + "\n");
		lines++;
		if(lines >= maxLines){
			output.setText("--Cleared Console--");
			lines = 1;
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
		printInfo(from + ": " + info);
	}
	public void printInfo(String info){
		print("[INFO] " + info);
	}
	public void printWarning(String warning, String from){
		printWarning(from + ": " + warning);
	}
	public void printWarning(String warning){
		print("[WARNING] " + warning);
	}
	public void printError(String error, String from) {
		printError(from + ": " + error);
	}
	public void printError(String error){
		print("[ERROR] " + error);
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
	
	public void setNetworkManager(NetworkManager manager) {
		networkManager = manager;
	}

	public void updateForeground(Color color){
		output.setForeground(color);
		input.setForeground(color);
	}
	public void updateBackground(Color color){
		panel.setBackground(color);
	}
}
