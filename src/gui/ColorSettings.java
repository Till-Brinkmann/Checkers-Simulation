package gui;
import javax.swing.event.*;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
/**
 * A window that holds components to customize color parameters of the program.
 */
public class ColorSettings extends JFrame{

	private ImageIcon colorSettingsIcon;
	private JPanel foregroundPanel;
    private JSlider[] foregroundSlider;
    private JLabel labelBg;
    private JPanel backgroundPanel;
    private JSlider[] backgroundSlider;
    private JLabel labelFg;
    private JButton okButton;
    GUI gui;
    /**
     * Initializes the color settings with random colors.
     * <p>
     * @param pGui A reference to the GUI objet.
     */
	public ColorSettings(GUI pGui) {
		this(pGui, new Color((int)Math.round((Math.random()*Integer.MAX_VALUE*2)+Integer.MIN_VALUE)),
				new Color((int)Math.round((Math.random()*Integer.MAX_VALUE*2)+Integer.MIN_VALUE)));
	}
	/**
	 * Initializes the color settings with a distinct colors. 
	 * <p>
	 * @param gui A reference to the GUI object.
	 * @param fg  A Color object.
	 * @param bg A Color object
	 */
	public ColorSettings(GUI gui, Color fg ,Color bg) {
		super("Color Settings");
		this.gui = gui;
		initialize(fg, bg);
		createWindow();
		updateColors();
	}
	/**
	 * Initializes the rgb-sliders and the confirm button. Furthermore, it adds the actionlisteners.
	 * <p>
	 * @param fg  A Color object.
	 * @param bg A Color object
	 */
	private void initialize(Color fg, Color bg) {
		colorSettingsIcon = new ImageIcon("resources/Icons/colorChanger.png");
		setIconImage(colorSettingsIcon.getImage());
		backgroundSlider = new JSlider[3];
		foregroundSlider = new JSlider[3];
		for(int i = 0; i < 3; i++){
			//some fancy bitmath to get red,green and blue separately inside the loop
			backgroundSlider[i] = new JSlider(0,255,(bg.getRGB() & (255 << 8*i))>> 8*i);
			backgroundSlider[i].setBackground(Color.WHITE);
			foregroundSlider[i] = new JSlider(0,255,(fg.getRGB() & (255 << 8*i))>> 8*i);
			foregroundSlider[i].setBackground(Color.WHITE);
		}
		okButton = new JButton("confirm");
		okButton.setBackground(Color.WHITE);
		for(int i = 0; i < 3; i++){
	        backgroundSlider[i].addChangeListener(new ChangeListener()
	        {
	        	public void stateChanged(ChangeEvent evt){
	        		updateColors();
	            }
	        });
	        foregroundSlider[i].addChangeListener(new ChangeListener()
	        {
	        	public void stateChanged(ChangeEvent evt){
	        		updateColors();
	            }
	        });
		}
		okButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	setVisible(false);
            }

        });
	}
	/**
	 * Sets up the window properties and adds the elements to the window.
	 */
	private void createWindow() {
		setResizable(false);
		setBackground(Color.WHITE);
		setSize(350,450);
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		//Background slider
		backgroundPanel = new JPanel();
		backgroundPanel.setBackground(Color.WHITE);
		backgroundPanel.setLayout(new GridLayout(4,1));
		labelBg = new JLabel("Background");
		backgroundPanel.add(labelBg);
		for(int i = 0; i < 3; i++){
			backgroundPanel.add(backgroundSlider[i]);
		}
		add(backgroundPanel);
		//Foreground slider
		foregroundPanel = new JPanel();
		foregroundPanel.setLayout(new GridLayout(4,1));
		labelFg = new JLabel("Foreground");
		foregroundPanel.add(labelFg);
		for(int i = 0; i < 3; i++){
			foregroundPanel.add(foregroundSlider[i]);
		}
		add(foregroundPanel);
		add(okButton);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		pack();
	}
	/**
	 * A Color object is created by getting the values from the three rgb-sliders.
	 * <p>
	 * @param rgb A JSlider array.
	 * @return A Color object.
	 */
	public Color sliderMixer(JSlider[] rgb){
		Color color = new Color(rgb[0].getValue(),rgb[1].getValue(),rgb[2].getValue());
		return color;
	}
	/**
	 * The color of every element is set to the current color of the three rgb-sliders.
	 */
	private void updateColors(){
		Color backgroundColor = sliderMixer(backgroundSlider);
		Color foregroundColor = sliderMixer(foregroundSlider); 
		gui.updateBackground(backgroundColor);
		gui.updateForeground(foregroundColor);
		backgroundPanel.setBackground(backgroundColor);
		foregroundPanel.setBackground(foregroundColor);
	}

}
