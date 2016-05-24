/**
 * MazePuzzleGame maintains and connects the different parts of the
 * game, allowing for easy communication between the user interface and the 
 * game data.x
 * 
 * @author John, Joshua, Patrick, Tim, Tyler
 *
 */
public class MazePuzzleGame {
    
    Preferences pref;
    GUI gui;
    Game game;
    SoundEngine sounds;
    
    /**
     * Initialises MazePuzzleGame.
     * 
     * MazePuzzleGame links together the MazeWorld with MazePuzzlaGame
     * via the commands queue, and links together the display with the preferences,
     * MazeWorld, and commands queue.
     */
    public MazePuzzleGame() {
        this.pref = new Preferences();
        this.game = new Game(this, pref);
        this.sounds = new SoundEngine(pref);
        this.gui = new GUI(this, pref, game);
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
	    MazePuzzleGame game = new MazePuzzleGame();
	}
	
	public void submitCommand(Command c) {
	    switch (c.getCommandID()) {
            case EXIT:         System.exit(0);;                     break;
            case GAME_MSG:     game.inbox(c.getMessage());          break;
            case SOUND_MSG:    sounds.inbox(c.getMessage());   break;
	    }
	}
}
