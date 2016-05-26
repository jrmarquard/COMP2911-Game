import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import javax.sound.sampled.*;

public class SoundEngine {
	private boolean soundEnabled;
	private Clip menuMusic;
	private Clip backgroundMusic;
	private float musicVolume;
	private float soundEffectsVolume;
	private List<FloatControl> volControls;
	private Preferences pref;
	private int gameSoundsPlaying;
	final private int MAX_GAME_SOUNDS = 50;
	
	//Locking System
	private Semaphore soundsPlayingSemaphore;
	
	// Thread pool to run sounds in
	ExecutorService soundPool;
	
	public SoundEngine(Preferences pref) {
	    this.pref = pref;
		this.soundEnabled = true;
		this.musicVolume = 0f;
		this.soundEffectsVolume = 0f;
		this.volControls = new ArrayList<FloatControl>();
		this.gameSoundsPlaying = 0;
		
		this.soundsPlayingSemaphore = new Semaphore(1, true);
		
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
            
            FloatControl menuVolumeControl = (FloatControl) menuMusic.getControl(FloatControl.Type.MASTER_GAIN);
            volControls.add(menuVolumeControl);
            FloatControl bgVolumeControl = (FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
            volControls.add(bgVolumeControl);
        
            setMusicVolume();
            setSoundEffectVolume();
		} 
		catch (Exception e){
			this.soundEnabled = false;
			System.out.println("Sound disabled");
		}
		

        
	}

    public void inbox(String[] message) {
        try {
        	if (this.soundEnabled) {
	            SoundRunnable run = new SoundRunnable(message);
	            soundPool.execute(run);
        	}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Sets the master volume up between max and min values.
     * 
     * 
     * @param volume A number between 0 and 100. Where 0 is mute and 100 max volume.
     */
    private void setMusicVolume() {
	        int volume = pref.getValue("musicVolume");
	        
	        float minVol = -20.0f;
	        float maxVol = 2.0f;
	        float volUnit = (maxVol-minVol)/100f;
	        
	        musicVolume = minVol + volUnit*(float)volume;
	        if (volume == 0) musicVolume = -64f;
	        
	        for (FloatControl f : volControls) {
	            f.setValue(musicVolume);
	        }
    }
    
    private void setSoundEffectVolume() {
    	int volume = pref.getValue("soundEffectsVolume");
        
        float minVol = -20.0f;
        float maxVol = 2.0f;
        float volUnit = (maxVol-minVol)/100f;
        
        soundEffectsVolume = minVol + volUnit*(float)volume;
        if (volume == 0) soundEffectsVolume = -64f;
    }
	        
	 
	
	private void playSound(String soundName) {
			try {
				this.soundsPlayingSemaphore.acquire();
			} catch (InterruptedException e1) {
				return;
			}
		    if (gameSoundsPlaying >= MAX_GAME_SOUNDS) {
		    	this.soundsPlayingSemaphore.release();
		    	return; 
		    }
		
			try {
				gameSoundsPlaying++;
				this.soundsPlayingSemaphore.release();
			    /* Loads the audio file into memory. */
			    File soundFile = new File("sounds/"+soundName+".wav");
		        
		        /* Gets an audioInputStream for the audio file. */
		        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
		        		        
		        /* Gets the format from the audioInputStream, so we know how to interpret the file. */
		        AudioFormat audioFormat = audioInputStream.getFormat();
		        
		        /* Stores the audio format so soundLine knows how to interpret the audio. */
		        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
		        
		        /* Obtains a line that matches the description in info. */		         
		        SourceDataLine audioOutputLine = (SourceDataLine) AudioSystem.getLine(info);
		        audioOutputLine.open(audioFormat);
		        audioOutputLine.start();
		        
		        

		        /* Get and set the volume control*/
		        FloatControl volumeControl = (FloatControl) audioOutputLine.getControl(FloatControl.Type.MASTER_GAIN);
		        volumeControl.setValue(soundEffectsVolume);
		        
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
		        this.soundsPlayingSemaphore.acquire();
                gameSoundsPlaying--;
                this.soundsPlayingSemaphore.release();
                Thread.sleep(1000);
                audioOutputLine.close();
                audioInputStream.close();
			}
			catch (Exception e){
			    e.printStackTrace();
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
                case "changeVolume":
                	if (msg[1].equals("soundEffects")) setSoundEffectVolume();
                	else if (msg[1].equals("music")) setMusicVolume();
                    break;
                default:
                    break;
            }
        }
    }
	
	
}
