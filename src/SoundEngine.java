import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sound.sampled.*;

public class SoundEngine {
    // Sounds
	private Clip menuMusic;
	private Clip backgroundMusic;
	
	// Sound options
	private float masterVolume;
	private List<FloatControl> volControls;
    private boolean soundEnabled;
	private int gameSoundsPlaying;
	
	// Permanent options
	private final int MAX_GAME_SOUNDS = 15;
	
	// Thread pool to run sounds in
	ExecutorService soundPool;
	
	public SoundEngine() {
		this.soundEnabled = true;
		this.masterVolume = 0f;
		this.volControls = new ArrayList<FloatControl>();
		this.gameSoundsPlaying = 0;
		
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
		

        FloatControl menuVolumeControl = (FloatControl) menuMusic.getControl(FloatControl.Type.MASTER_GAIN);
        volControls.add(menuVolumeControl);
        FloatControl bgBolumeControl = (FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
        volControls.add(bgBolumeControl);
        
        setMasterVolume();
	}

    public void inbox(String[] message) {
        try {
            SoundRunnable run = new SoundRunnable(message);
            soundPool.execute(run);
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
    private void setMasterVolume() {
        
        int volume = App.pref.getValue("masterVolume");
        
        float minVol = -20.0f;
        float maxVol = 2.0f;
        float volUnit = (maxVol-minVol)/100f;
        
        masterVolume = minVol + volUnit*(float)volume;
        if (volume == 0) masterVolume = -64f;
        
        for (FloatControl f : volControls) {
            f.setValue(masterVolume);
        }
    }
	
	private void playSound(String soundName) {
	    if (gameSoundsPlaying >= MAX_GAME_SOUNDS) return; 
		if (this.soundEnabled) {
			try {
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
		        
		        gameSoundsPlaying++;

		        /* Get and set the volume control*/
		        FloatControl volumeControl = (FloatControl) audioOutputLine.getControl(FloatControl.Type.MASTER_GAIN);
		        volumeControl.setValue(masterVolume);
		        
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
                gameSoundsPlaying--;
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
                    setMasterVolume();
                    break;
                default:
                    break;
            }
        }
    }
	
	
}
