import java.awt.EventQueue;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * MazePuzzleGame maintains and connects the different parts of the
 * game, allowing for easy communication between the user interface and the 
 * game data.
 * 
 * @author John, Joshua, Patrick, Tim, Tyler
 *
 */
public class MazePuzzleGame extends Thread {
    
    Preferences pref;
    GUI gui;
    Game game;
    SoundEngine soundEngine;
    
    final public ExecutorService gameExecutor;
    
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
        this.soundEngine = new SoundEngine();
        this.gui = new GUI(this, pref, game);
        
        gameExecutor = Executors.newSingleThreadExecutor();
    }
    
    public void run() {
        submitCommand(new Command(Com.DRAW));
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
	    game.start();
	}
	
	public void submitCommand(Command c) {
	    switch (c.getCommandID()) {
            case DRAW:         gui.refresh();                       break;
            case EXIT:         System.exit(0);;                     break;
            case GAME_MSG:     game.inbox(c.getMessage());          break;
            case SOUND_MSG:    soundEngine.inbox(c.getMessage());   break;
	    }
	}
}
