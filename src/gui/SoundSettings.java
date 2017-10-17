package gui;

import javax.swing.*;

@SuppressWarnings("serial")
public class SoundSettings extends JFrame{

	ImageIcon soundSettingsIcon;
	public SoundSettings() {
		super("Sound Settings");
	}
	public void enableVisibility() {
		setVisible(true);
		soundSettingsIcon = new ImageIcon("resources/Icons/soundSettings.png");
		setIconImage(soundSettingsIcon.getImage());

	}
}
