/**
 * MazePuzzleGame maintains and connects the different parts of the
 * game, allowing for easy communication between the user interface and the 
 * game data.x
 * 
 * @author John, Joshua, Patrick, Tim, Tyler
 *
 */
public class App {
    
    public static Preferences pref;
    private Game game;
    private SoundEngine sounds;
    
    /**
     * Initialises MazePuzzleGame.
     * 
     * MazePuzzleGame links together the MazeWorld with MazePuzzlaGame
     * via the commands queue, and links together the display with the preferences,
     * MazeWorld, and commands queue.
     */
    public App() {
        App.pref = new Preferences();
        this.game = new Game(this);
        this.sounds = new SoundEngine();
        new GUI(this, game);
    }
    
    /**
     * main function for the game. Execution starts here.
     * 
     * The main function simply:
     * - creates MazePuzzleGame
     * - waits for commands
     * 
     * @param args Arguments for the program. Unused.
     */
	public static void main(String[] args) {
        new App();
	}
	
	/**
	 * Takes an input message and hands it off to the appropriate handler
	 * 
	 * @param c Message that has been delivered
	 */
	public void sendMessage(Message c) {
	    switch (c.getCommandID()) {
            case Message.EXIT:         System.exit(0);;                 break;
            case Message.GAME_MSG:     game.inbox(c.getMessage());      break;
            case Message.SOUND_MSG:    sounds.inbox(c.getMessage());    break;
	    }
	}
}
