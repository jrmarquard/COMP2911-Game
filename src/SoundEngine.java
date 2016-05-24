import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sound.sampled.*;

public class SoundEngine {
	private boolean soundEnabled;
	private Clip menuMusic;
	private Clip backgroundMusic;
	
	// Thread pool to run sounds in
	ExecutorService soundPool;
	
	public SoundEngine() {
		this.soundEnabled = true;
		
		// Initiliase thread pool
		soundPool = Executors.newCachedThreadPool();
        
		try {
		    // Load menu music
		    String menuSound = new String("sounds/menu.wav");
		    AudioInputStream stream = AudioSystem.getAudioInputStream(new File(menuSound));
            AudioFormat format = stream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            this.menuMusic = (Clip) AudioSystem.getLine(info);
            menuMusic.open(stream);
            
            // Load background music
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
            SoundRunnable run = new SoundRunnable(message);
            soundPool.execute(run);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	private void playSound(String soundName) {
		if (this.soundEnabled) {
			try {
			    // Loads
			    File soundFile = new File("sounds/"+soundName+".wav");
		        
		        /* Gets an audioInputStream for the given soundFile*/
		        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
		        		        
		        /* Gets the format from the audioInputStream, so we know how to interpret the file. */
		        AudioFormat audioFormat = audioInputStream.getFormat();
		        
		        /* Stores the audio format so soundLine knows how to interpret the audio. */
		        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
		        
		        /* Obtains a line that matches the description in info. */		         
		        SourceDataLine audioOutputLine = (SourceDataLine) AudioSystem.getLine(info);
		        audioOutputLine.open(audioFormat);
		        audioOutputLine.start();

                // buffer of 32 kb
		        byte[] sampleByte = new byte[32*1024];
		        
		        // Read from the audioInputSteam to the audioOutputLine until there is nothing left
		        int b = audioInputStream.read(sampleByte);
		        while(b != -1) {
	                audioOutputLine.write(sampleByte, 0, b);
	                b = audioInputStream.read(sampleByte);
		        }
		        
		        // Sleep the thread allowing the sound to finish playing, and then close resources.
		        // Should make this based on the length of the sound clip to ensure everything
		        // has enough time to finish playing.
                Thread.sleep(100);
                audioOutputLine.close();
                audioInputStream.close();
			}
			catch (Exception e){
			    e.printStackTrace();
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
    
    private class SoundRunnable implements Runnable {
        String[] msg;
        
        public SoundRunnable (String[] msg) {
            this.msg = msg;
        }
        
        @Override
        public void run() {
            switch(msg[0]) {
                case "play":
                    playSound(msg[1]);
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
