package gui;

import javax.swing.*;
import java.awt.*;

import javax.swing.event.*;
import generic.Queue;

import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.*;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
public class SoundSettings extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1850463064180012073L;

	
	private Timer timer;
	private boolean musicIsPlaying = false;
	private JPanel musicPlayer;
	private JLabel currentlyPlayingLabel;
	private JTextField currentMusicField;
	private JLabel currentMusicImage;
	private JPanel musicPlayerButtons;
	private JComboBox<String> musicPlayerOptions;
	private long musicDurationMs;
	private JButton stopPlay;
	private JButton back;
	private JButton forth;
	private Icon playerBackIcon;
	private Icon playerStopIcon;
	private Icon playerPlayIcon;
	private Icon playerForthIcon;
	private Icon standardImage;
	private Clip musicClip;
	private Clip soundClip;	
	private JPanel volume;
	private JSlider musicVolumeSlider;
    private JSlider soundVolumeSlider;
    private JCheckBox[] muteSounds;
    private JPanel sounds;
    private float musicVolume = (float)0.5;
    private float soundsVolume = (float)0.5;
    private boolean winSoundActive = true;
    private boolean beatSoundActive =true;
    private boolean moveSoundActive = true;
    private boolean toDameSoundActive = true;
    private FloatControl gainControlMusic;
    private FloatControl gainControlSound;
    private Queue<File> musicFiles;
    private File[] soundFiles;
    private File[] imageArray;
    private GUI gui;

    
    public SoundSettings(GUI pGui,String... files) 
    {
    	super("GameSettings");
    	gui = pGui;
    	playerBackIcon = new ImageIcon("resources/Icons/playerBackIcon.png");
    	playerStopIcon = new ImageIcon("resources/Icons/playerStopIcon.png");
    	playerPlayIcon = new ImageIcon("resources/Icons/playerPlayIcon.png");
    	playerForthIcon = new ImageIcon("resources/Icons/playerForthIcon.png");
    	standardImage = new ImageIcon("resources/MusicImages/standard.png");
    	File imageDirectory = new File("resources/MusicImages");
        imageArray = new File[imageDirectory.listFiles().length];
        imageArray = imageDirectory.listFiles();
    	
        musicFiles = new Queue<File>();
        File fileDirectory = new File("resources/Music");
        File[] fileArray = new File[1000];
        fileArray = fileDirectory.listFiles();
        
        for(int i = 0; i < fileDirectory.listFiles().length; i++) {
        	if(fileArray[i].getName().endsWith(".wav"))
        	musicFiles.enqueue(fileArray[i]);
        }   
       
   		timer = new Timer();
        setIconImage(new ImageIcon("/resources/pics/soundSettings.png").getImage());
        setLayout(new GridLayout(3,1));
        setBackground(Color.WHITE);
        createMusicPlayerPanel();        
        createVolumePanel();        
        createSoundPanel();
        setSize(250,755);
        setResizable(false);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setTitle("Sound options");
        try {
			initializeAllSounds();
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			gui.console.printWarning("SoundSettings", "Sounds could not be initialized");
			e.printStackTrace();
		}
        
    }
	private void createMusicPlayerPanel() {
        musicPlayer = new JPanel();
        musicPlayer.setPreferredSize(new Dimension(250,450));
        musicPlayer.setBackground(Color.WHITE);
        //amusicPlayer.setLayout(null);
        currentlyPlayingLabel = new JLabel();
        currentlyPlayingLabel.setPreferredSize(new Dimension(250,20));
        currentlyPlayingLabel.setText("   Currently playing:");
        currentlyPlayingLabel.setBackground(Color.WHITE);
        currentlyPlayingLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        currentMusicField = new JTextField();
        currentMusicField.setPreferredSize(new Dimension(250,20));
        currentMusicField.setBackground(Color.WHITE);
        currentMusicField.setEditable(false);
        currentMusicField.setFocusable(true);
		currentMusicField.setFont(new Font("Arial", Font.BOLD, 12));
		currentMusicField.setBorder(null);
		
        currentMusicImage = new JLabel(standardImage);
        currentMusicImage.setPreferredSize(new Dimension(250,100));
        if(!musicFiles.isEmpty()){
        	displayMusicInfo(musicFiles.front().getName());
        }
		
        musicPlayerButtons = new JPanel();
        musicPlayerButtons.setBackground(Color.WHITE);
		musicPlayerButtons.setLayout(new FlowLayout());
		createMusicPlayerButtons();
		musicPlayerOptions = new JComboBox<String>(new String[]{"Linear","Loop","Random"});
		musicPlayerOptions.setBackground(Color.WHITE);
		musicPlayer.add(currentlyPlayingLabel);
		musicPlayer.add(currentMusicField);
		musicPlayer.add(currentMusicImage);
		musicPlayer.add(musicPlayerButtons);
		musicPlayer.add(musicPlayerOptions);
		add(musicPlayer);
	}
    private void createMusicPlayerButtons(){
    	stopPlay = new JButton("");
    	stopPlay.setIcon(playerPlayIcon);
    	stopPlay.setBackground(Color.WHITE);
		stopPlay.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	try {
            		if(!musicFiles.isEmpty())
					try {						
						playStopMusic(false);						
						
					} catch (UnsupportedAudioFileException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (LineUnavailableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}            	
            }

        });
    	back = new JButton("");
    	back.setIcon(playerBackIcon);
    	back.setBackground(Color.WHITE);
		back.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	if(!musicFiles.isEmpty()) {
        			try {
						if(musicPlayerOptions.getSelectedItem().equals("Loop") || musicPlayerOptions.getSelectedItem().equals("Linear")) {
							beforeMusicClip();
						}
						else {
							randomMusicClip(false);
						}
					} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	}
            }


        });
    	forth = new JButton("");
    	forth.setIcon(playerForthIcon);
    	forth.setBackground(Color.WHITE);
		forth.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	if(!musicFiles.isEmpty()) {
					try {
						if(musicPlayerOptions.getSelectedItem().equals("Loop") || musicPlayerOptions.getSelectedItem().equals("Linear")) {
							nextMusicClip(false);
						}
						else {
							randomMusicClip(false);
						}
					} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	}
            }

        });
    	musicPlayerButtons.add(back);    	
    	musicPlayerButtons.add(stopPlay);
    	musicPlayerButtons.add(forth);
    }
	private void createVolumePanel() {
        volume = new JPanel();
        volume.setBackground(Color.WHITE);
        volume.setLayout(new GridLayout(4,1));
        volume.add(new JLabel("   Music volume:"));
        volume.add(createMusicVolumeSlider());
        volume.add(new JLabel("   Sound volume:"));
        volume.add(createSoundVolumeSlider());
        add(volume);
	}
	private JSlider createSoundVolumeSlider() {
        soundVolumeSlider = new JSlider();
        soundVolumeSlider.setMinimum(0);
        soundVolumeSlider.setMaximum(100);
        soundVolumeSlider.setValue(50);
        soundVolumeSlider.createStandardLabels(1);
        soundVolumeSlider.setMajorTickSpacing(50);
        soundVolumeSlider.setPaintLabels(true);
        soundVolumeSlider.setBackground(Color.WHITE); 
        soundVolumeSlider.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e){
                soundsVolume = (float)soundVolumeSlider.getValue()/100;
                changeSoundVolume();
            }
        });
        return soundVolumeSlider;
	}
	private JSlider createMusicVolumeSlider() {
        musicVolumeSlider = new JSlider();
        musicVolumeSlider.setMinimum(0);
        musicVolumeSlider.setMaximum(100);
        musicVolumeSlider.setValue(50);
        musicVolumeSlider.createStandardLabels(1);
        musicVolumeSlider.setMajorTickSpacing(50);
        musicVolumeSlider.setPaintLabels(true);
        musicVolumeSlider.setBackground(Color.WHITE);
        musicVolumeSlider.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e){
            	if(musicIsPlaying) {
	                musicVolume = ((float)musicVolumeSlider.getValue()/100);
	                changeMusicVolume();
            	}
            }
        }); 
        return musicVolumeSlider;
	}
	private void createSoundPanel() {
    	sounds = new JPanel();
    	sounds.setBackground(Color.WHITE);
    	sounds.setLayout(new GridLayout(5,1));
        sounds.add(new JLabel("   Disable Sounds:"));       
        muteSounds = new JCheckBox[4];
        muteSounds[0] =  new JCheckBox("win Sound                 ");
        muteSounds[1] =  new JCheckBox("beat Sound               ");
        muteSounds[2] =  new JCheckBox("move Sound             ");
        muteSounds[3] =  new JCheckBox("to dame Sound        ");
        for(int i = 0;i < 4; i++){
        	muteSounds[i].setBackground(Color.WHITE);
            sounds.add(muteSounds[i]);
        }
        for(int i = 0;i< 4; i++){        
            muteSounds[i].addItemListener(new ItemListener(){
                public void itemStateChanged(ItemEvent event){
                        String checkBoxName = ((JCheckBox)event.getSource()).getText();
                        boolean isSelected;
                        if(event.getStateChange() == ItemEvent.SELECTED){
                            isSelected = true;
                        }
                        else{
                            isSelected = false;
                        }
                        changeSoundState(checkBoxName, isSelected);
                }
            });
        }
        add(sounds);
	}
    private void playStopMusic(boolean next) throws LineUnavailableException, UnsupportedAudioFileException, IOException {
    	if(musicIsPlaying && !next) {
    		stopPlay.setIcon(playerPlayIcon);
    		stopMusic();
    		musicIsPlaying = false;
    		return;
    	}
    	
    	if(!musicIsPlaying || next){
    		if(!musicIsPlaying) {
    			musicIsPlaying = true;
    		}
    		stopPlay.setIcon(playerStopIcon);    		
    		playMusic(musicFiles.front());
			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() 
				{
					try {
						switch((String)musicPlayerOptions.getSelectedItem()) {
							case "Linear":
								nextMusicClip(true);
								break;
							case "Loop":
								stopMusic();
								playStopMusic(true);
								break;
							case "Random":
								randomMusicClip(true);
								break;
						}
					} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}						
				}				
			},musicDurationMs);   		    		
    	}	
	}    
	private void beforeMusicClip() throws UnsupportedAudioFileException, IOException, LineUnavailableException {			
		boolean stopMusicCalled = false;
		if(musicIsPlaying) {
			timer.cancel();
			stopMusic();
			stopMusicCalled = true;
			
		}
		File tmpFile;
		tmpFile = musicFiles.front();
		musicFiles.enqueue(musicFiles.front());
		musicFiles.dequeue();
		int j = 0;
		while(tmpFile != musicFiles.front()) {
			musicFiles.enqueue(musicFiles.front());
			musicFiles.dequeue();
			j++;
		}
		for(int i = 0;i< j; i++) {
			musicFiles.enqueue(musicFiles.front());
			musicFiles.dequeue();
		}
		displayMusicInfo(musicFiles.front().getName());
		if(stopMusicCalled) {
			playStopMusic(true);
		}
	}
	private void nextMusicClip(boolean alreadyCancelled) throws UnsupportedAudioFileException, IOException, LineUnavailableException{
		if(!alreadyCancelled && musicIsPlaying) {
			timer.cancel();
		}
		boolean stopMusicCalled = false;
		if(musicIsPlaying) {
			stopMusic();
			stopMusicCalled = true;
		}
		musicFiles.enqueue(musicFiles.front());
		musicFiles.dequeue();
		displayMusicInfo(musicFiles.front().getName());
		if(stopMusicCalled) {
			playStopMusic(true);
		}
	}
	private void randomMusicClip(boolean alreadyCancelled) throws LineUnavailableException, UnsupportedAudioFileException, IOException {
		if(!alreadyCancelled && musicIsPlaying) {
			timer.cancel();
		}
		boolean stopMusicCalled = false;
		if(musicIsPlaying) {
			stopMusic();
			stopMusicCalled = true;
		}
		Random rand = new Random();
		int random = rand.nextInt(100)+1;
		for(int i = 0; i < random; i++) {
			musicFiles.enqueue(musicFiles.front());
			musicFiles.dequeue();
		}
		displayMusicInfo(musicFiles.front().getName());
		if(stopMusicCalled) {
			playStopMusic(true);
		}
	}
    private void displayMusicInfo(String music) {
    	currentMusicField.setText("");
    	currentMusicField.setText("   " + music.substring(0, music.length()-4));
    	for(int i = 0;i < imageArray.length ; i++ ) {

    		if(music.substring(0,music.length()-5).equals(imageArray[i].getName().substring(0,imageArray[i].getName().length()-5))) {
    			Icon image;
    			image = new ImageIcon(imageArray[i].getPath());
    			currentMusicImage.setIcon(image);
    			return;
    		}
    	}    	

  	currentMusicImage.setIcon(standardImage);
    }
    private void playMusic(File file) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
    	AudioInputStream ais;
    	AudioFormat format;
    	DataLine.Info info;
		ais = AudioSystem.getAudioInputStream(file);

		format = ais.getFormat();
		long audioFileLength = file.length();
	    int frameSize = format.getFrameSize();
	    float frameRate = format.getFrameRate();	    
		musicDurationMs =  (long)(audioFileLength / (frameSize * frameRate) * 1000);
		info = new DataLine.Info(Clip.class, format);
		musicClip = (Clip) AudioSystem.getLine(info);
		musicClip.open(ais);
		gainControlMusic = (FloatControl) musicClip.getControl(FloatControl.Type.MASTER_GAIN);

		changeMusicVolume();
		musicClip.start();

    }
    private void stopMusic() {
    	musicIsPlaying = false;
    	musicClip.close();
    }
	public void changeSoundState(String checkBoxName, boolean isSelected){
        switch(checkBoxName){
            case "win Sound                 ":
                winSoundActive = !isSelected;               
                break;
            case "beat Sound               ":
                beatSoundActive = !isSelected;
                break;
            case "move Sound             ":
                moveSoundActive = !isSelected;
                break;
            case "to dame Sound        ":
                toDameSoundActive = !isSelected;
                break;
        }
    }
    public void initializeAllSounds() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
    	soundFiles = new File[4];   	
    	soundFiles[0] = new File("resources/Sounds/winSound.wav");
    	soundFiles[1] = new File("resources/Sounds/beatSound.wav");
    	soundFiles[2] = new File("resources/Sounds/moveSound.wav");
    	soundFiles[3] = new File("resources/Sounds/toDameSound.wav");

    }
    public void selectSound(String fileName){
        switch(fileName){
            case "winSound.wav":
                if(winSoundActive == false){
                    return;
                }
				try {
					playSound(soundFiles[0]);
				} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                break;
            case "beatSound.wav":   
                if(beatSoundActive == false){
                    return;
                }
				try {
					playSound(soundFiles[1]);
				} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                break;
            case "moveSound.wav":
                if(moveSoundActive == false){
                    return;
                }
				try {
					playSound(soundFiles[2]);
				} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                break;
            case "toDameSound.wav": 
                if(toDameSoundActive == false){
                    return;
                }
				try {
					playSound(soundFiles[3]);
				} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                break;
        }
    }  
    private void playSound(File file) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        	AudioInputStream ais;
        	AudioFormat format;
        	DataLine.Info info;
    		ais = AudioSystem.getAudioInputStream(file);
    		format = ais.getFormat();
    		info = new DataLine.Info(Clip.class, format);
    		soundClip = (Clip) AudioSystem.getLine(info);
    		soundClip.open(ais);
			gainControlSound = (FloatControl) soundClip.getControl(FloatControl.Type.MASTER_GAIN);        		
    		changeSoundVolume();
    		soundClip.start();
    }
       
    public void changeMusicVolume(){
        float max = gainControlMusic.getMaximum();
        float min = gainControlMusic.getMinimum();
        float range = max-min;
        gainControlMusic.setValue(min + (range * musicVolume));
    }
    public void changeSoundVolume(){
        float max = gainControlSound.getMaximum();
        float min = gainControlSound.getMinimum();
        float range = max-min;
        gainControlSound.setValue(min + (range * soundsVolume));
    }
    
}
