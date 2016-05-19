import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

public class SoundEngine {
	private boolean soundEnabled;
	private Map<String, Clip> sounds;
	
	public SoundEngine() {
		this.soundEnabled = true;
		this.sounds = new HashMap<String, Clip>();
		File soundFile;
	
		ArrayList<String> soundFileNames = new ArrayList<String>();
		soundFileNames.add("coin.wav");
        soundFileNames.add("intro.wav");
        soundFileNames.add("finish.wav");
        soundFileNames.add("step.wav");
        
		try {
		    for (String fileName : soundFileNames) {
	            String fileLocation = "sounds/"+fileName;
	            fileName = fileName.split("\\.")[0];
	            soundFile = new File(fileLocation);
                AudioInputStream stream = AudioSystem.getAudioInputStream(soundFile);
                AudioFormat format = stream.getFormat();
                DataLine.Info info = new DataLine.Info(Clip.class, format);
                Clip clip = (Clip) AudioSystem.getLine(info);
                clip.open(stream);
                sounds.put(fileName, clip);
            }
		} 
		catch (Exception e){
			this.soundEnabled = false;
			System.out.println("Sound disabled");
		}
	}
	public void playSound(String soundName) {
		if (this.soundEnabled) {
		    sounds.get(soundName).setFramePosition(0); 
	        sounds.get(soundName).start();
		}
	}
}
