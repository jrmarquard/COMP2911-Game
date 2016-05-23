import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;

public class SoundEngine {
	private boolean soundEnabled;
	private Map<String, File> sounds;
	private Clip menuMusic;
	private Clip backgroundMusic;
	private float masterVolume;
	private boolean soundMuted;
	
	public SoundEngine() {
		this.soundEnabled = true;
		this.sounds = new HashMap<String, File>();
		this.masterVolume = 0.7f;
		this.soundMuted = false;
	
		ArrayList<String> soundFileNames = new ArrayList<String>();
		soundFileNames.add("coin.wav");
        soundFileNames.add("intro.wav");
        soundFileNames.add("finish.wav");
        soundFileNames.add("step.wav");
        soundFileNames.add("click.wav");
        soundFileNames.add("key.wav");
        soundFileNames.add("door.wav");
        
		try {
		    for (String fileName : soundFileNames) {
	            String fileLocation = "sounds/"+fileName;
	            fileName = fileName.split("\\.")[0];
	            sounds.put(fileName, new File(fileLocation));
            }
		    String menuSound = new String("sounds/menu.wav");
		    AudioInputStream stream = AudioSystem.getAudioInputStream(new File(menuSound));
            AudioFormat format = stream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            this.menuMusic = (Clip) AudioSystem.getLine(info);
            menuMusic.open(stream);
            String backgroundSound = new String("sounds/background.wav");
		    stream = AudioSystem.getAudioInputStream(new File(backgroundSound));
            format = stream.getFormat();
            info = new DataLine.Info(Clip.class, format);
            this.backgroundMusic = (Clip) AudioSystem.getLine(info);
            backgroundMusic.open(stream);
		} 
		catch (Exception e){
			this.soundEnabled = false;
			System.out.println("Sound disabled");
		}
	}
	
	public void playSound(String soundName) {
		if (this.soundEnabled) {
			if (!(soundMuted)) {
				try {
					AudioInputStream stream = AudioSystem.getAudioInputStream(this.sounds.get(soundName));
		            AudioFormat format = stream.getFormat();
		            DataLine.Info info = new DataLine.Info(Clip.class, format);
		            Clip clip = (Clip) AudioSystem.getLine(info);
		            clip.open(stream);
		            FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		            volume.setValue(((volume.getMaximum() - volume.getMinimum())*this.masterVolume) + volume.getMinimum());
				    clip.start();
				}
				catch (Exception e){
					
				}
			}
		}
	}
	
	public void startMenuMusic() {
		if (this.soundEnabled) {
			if (!this.soundMuted) {
				FloatControl menuVolume = (FloatControl) this.menuMusic.getControl(FloatControl.Type.MASTER_GAIN);
		        menuVolume.setValue(((menuVolume.getMaximum() - menuVolume.getMinimum())*this.masterVolume) + menuVolume.getMinimum());
			}
			this.menuMusic.setFramePosition(0);
			this.menuMusic.loop(Clip.LOOP_CONTINUOUSLY);
		}
	}
	
	public void endMenuMusic() {
		if (this.soundEnabled) {
			this.menuMusic.stop();
		}
	}
	
	public void startBackgroundMusic() {
		if (this.soundEnabled) {
			if (!this.soundMuted) {
				FloatControl backgroundVolume = (FloatControl) this.backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
		        backgroundVolume.setValue(((backgroundVolume.getMaximum() - backgroundVolume.getMinimum())*this.masterVolume) + backgroundVolume.getMinimum());
			}
			this.backgroundMusic.setFramePosition(0);
			this.backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
		}
	}
	
	public void endBackgroundMusic() {
		if (this.soundEnabled) {
			this.backgroundMusic.stop();
		}
	}
	
	public void setVolume(float volume) {
		this.masterVolume = (0.5f + (0.4f*volume));
	}
	
	public void increaseVolume() {
		if (this.masterVolume < 0.9f) {
			this.masterVolume = (this.masterVolume + 0.1f);
		}
	}
	
	public void decreaseVolume() {
		if (this.masterVolume > 0.5f) {
			this.masterVolume = (this.masterVolume - 0.1f);
		}
	}
	
	public void toggleMute() {
		if (this.soundEnabled) {
			if (!this.soundMuted) {
				this.soundMuted = true;
				FloatControl menuVolume = (FloatControl) this.menuMusic.getControl(FloatControl.Type.MASTER_GAIN);
		        menuVolume.setValue(menuVolume.getMinimum());
		        FloatControl backgroundVolume = (FloatControl) this.backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
		        backgroundVolume.setValue(backgroundVolume.getMinimum());
			} else {
				this.soundMuted = false;
				FloatControl menuVolume = (FloatControl) this.menuMusic.getControl(FloatControl.Type.MASTER_GAIN);
		        menuVolume.setValue(((menuVolume.getMaximum() - menuVolume.getMinimum())*this.masterVolume) + menuVolume.getMinimum());
		        FloatControl backgroundVolume = (FloatControl) this.backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
		        backgroundVolume.setValue(((backgroundVolume.getMaximum() - backgroundVolume.getMinimum())*this.masterVolume) + backgroundVolume.getMinimum());
			}
		}
	}

    public void inbox(String[] message) {
        switch(message[0]) {
            case "play":
                playSound(message[1]);
                break;                    
            case "loop":
                if (message[1].equals("background")) startBackgroundMusic();
                else if (message[1].equals("menu")) startMenuMusic();
                break;
            case "stop":
                if (message[1].equals("background")) endBackgroundMusic();
                else if (message[1].equals("menu")) endMenuMusic();
                break;
            case "volume":
            	if (message[1].equals("up")) increaseVolume();
            	else if (message[1].equals("down")) decreaseVolume();
            	else if (message[1].equals("mute")) toggleMute();
            	break;
            default:
                break;
        }
    }
	
	
}
