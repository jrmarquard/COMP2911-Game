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
		    
		    System.out.println("Loaded first file");

	        fileName = new String("coin.wav");
		    soundFile = new File(fileName);
		    this.files.add(soundFile);
		    
		} 
		catch (Exception e){
			this.soundEnabled = false;
			System.out.println("Sound disabled");
		}
		if (soundEnabled == true) {
			try {
				AudioInputStream stream = AudioSystem.getAudioInputStream(this.files.get(1));
		        AudioFormat format = stream.getFormat();
		        DataLine.Info info = new DataLine.Info(Clip.class, format);
		        Clip clip = (Clip) AudioSystem.getLine(info);
		        clip.open(stream);
		        clip.start();
	        
			/*try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.out.println("Didn't like sleeping");
			}
			stream = AudioSystem.getAudioInputStream(this.files.get(1));
	        format = stream.getFormat();
	        info = new DataLine.Info(Clip.class, format);
	        clip = (Clip) AudioSystem.getLine(info);
	        clip.open(stream);
	        clip.start(); */
			}
			catch (Exception e) {
				
			}
			
		}
	}
}
