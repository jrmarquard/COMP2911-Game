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
	private Map<String, File> sounds;
	
	public SoundEngine() {
		this.soundEnabled = true;
		this.sounds = new HashMap<String, File>();
	
		ArrayList<String> soundFileNames = new ArrayList<String>();
		soundFileNames.add("coin.wav");
        soundFileNames.add("intro.wav");
        soundFileNames.add("finish.wav");
        soundFileNames.add("step.wav");
        soundFileNames.add("click.wav");
        
		try {
		    for (String fileName : soundFileNames) {
	            String fileLocation = "sounds/"+fileName;
	            fileName = fileName.split("\\.")[0];
	            sounds.put(fileName, new File(fileLocation));
            }
		} 
		catch (Exception e){
			this.soundEnabled = false;
			System.out.println("Sound disabled");
		}
	}
	public void playSound(String soundName) {
		if (this.soundEnabled) {
			try {
				AudioInputStream stream = AudioSystem.getAudioInputStream(this.sounds.get(soundName));
	            AudioFormat format = stream.getFormat();
	            DataLine.Info info = new DataLine.Info(Clip.class, format);
	            Clip clip = (Clip) AudioSystem.getLine(info);
	            clip.open(stream);
			    clip.start();
			}
			catch (Exception e){
				
			}
		}
	}
}
