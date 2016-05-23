import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

public class SoundEngine {
	private boolean soundEnabled;
	private Map<String, File> sounds;
	private Clip menuMusic;
	private Clip backgroundMusic;
	
	// Thread pool to run sounds in
	ExecutorService soundPool;
	
	public SoundEngine() {
		this.soundEnabled = true;
		this.sounds = new HashMap<String, File>();
		
		// Initiliase thread pool
		soundPool = Executors.newCachedThreadPool();
	
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

    public void inbox(String[] message) {
        try {
            soundRunnable run = new soundRunnable(message);
            soundPool.execute(run);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	private void playSound(String soundName) {
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
	
	private void startMenuMusic() {
		if (this.soundEnabled) {
			this.menuMusic.setFramePosition(0);
			this.menuMusic.loop(Clip.LOOP_CONTINUOUSLY);
		}
	}
	
	private void endMenuMusic() {
		if (this.soundEnabled) {
			this.menuMusic.stop();
		}
	}
	
	private void startBackgroundMusic() {
		if (this.soundEnabled) {
			this.backgroundMusic.setFramePosition(0);
			this.backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
		}
	}
	
	private void endBackgroundMusic() {
		if (this.soundEnabled) {
			this.backgroundMusic.stop();
		}
	}
    
    private class soundRunnable implements Runnable {
        String[] msg;
        public soundRunnable (String[] msg) {
            this.msg = msg;
        }
        @Override
        public void run() {
            switch(msg[0]) {
                case "play":
                    // playSound(msg[1]);
                    break;                    
                case "loop":
                    if (msg[1].equals("background")) startBackgroundMusic();
                    else if (msg[1].equals("menu")) startMenuMusic();
                    break;
                case "stop":
                    if (msg[1].equals("background")) endBackgroundMusic();
                    else if (msg[1].equals("menu")) endMenuMusic();
                    break;
                default:
                    break;
            }
        }
        
    }
	
	
}
