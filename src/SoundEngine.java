import java.io.File;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

public class SoundEngine {
	private boolean soundEnabled;
	private ArrayList<File> files;
	
	public SoundEngine() {
		this.soundEnabled = true;
		this.files = new ArrayList<File>();
		String fileName;
		File soundFile;
			
		try {
			fileName = new String("coin-sound.wav");
		    soundFile = new File(fileName);
		    
		    this.files.add(soundFile);
		    
		    fileName = new String("intro.wav");
		    soundFile = new File(fileName);
		    
		    this.files.add(soundFile);
		    
		    fileName = new String("finish.wav");
		    soundFile = new File(fileName);
		    
		    this.files.add(soundFile);
		    
		    
		    
		} 
		catch (Exception e){
			this.soundEnabled = false;
			System.out.println("Sound disabled");
		}
	}
	public void playSound(String soundName) {
		if (this.soundEnabled) {
			if (soundName.equals("coin")) {
				try {
					AudioInputStream stream = AudioSystem.getAudioInputStream(this.files.get(0));
        	        AudioFormat format = stream.getFormat();
        	        DataLine.Info info = new DataLine.Info(Clip.class, format);
        	        Clip clip = (Clip) AudioSystem.getLine(info);
        	        clip.open(stream);
        	        clip.start();
				}
				catch (Exception e) {
					
				}
			} else if (soundName.equals("intro")) {
				try {
					AudioInputStream stream = AudioSystem.getAudioInputStream(this.files.get(1));
        	        AudioFormat format = stream.getFormat();
        	        DataLine.Info info = new DataLine.Info(Clip.class, format);
        	        Clip clip = (Clip) AudioSystem.getLine(info);
        	        clip.open(stream);
        	        clip.start();
				}
				catch (Exception e) {
					
				}
			} else if (soundName.equals("finish")) {
				try {
					AudioInputStream stream = AudioSystem.getAudioInputStream(this.files.get(2));
        	        AudioFormat format = stream.getFormat();
        	        DataLine.Info info = new DataLine.Info(Clip.class, format);
        	        Clip clip = (Clip) AudioSystem.getLine(info);
        	        clip.open(stream);
        	        clip.start();
				}
				catch (Exception e) {
					
				}
			}
		}
	}
}
