import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * MazePuzzleGame maintains and connects the different parts of the
 * game, allowing for easy communication between the user interface and the 
 * game data.
 * 
 * @author John, Joshua, Patrick, Tim, Tyler
 *
 */
public class MazePuzzleGame {
    
    Preferences pref;
    DisplayInterface disp;
    Game game;
    Queue<Command> commands;
    
    /**
     * Initialises MazePuzzleGame.
     * 
     * MazePuzzleGame links together the MazeWorld with MazePuzzlaGame
     * via the commands queue, and links together the display with the preferences,
     * MazeWorld, and commands queue.
     */
    public MazePuzzleGame() {
        this.pref = new Preferences();
        this.commands = new LinkedList<Command>();
        this.game = new Game(commands, pref);
        this.disp = new GUI(this.pref, this.game, this.commands);
        
        // Draws the GUI
        this.addCommand(new Command(Com.DRAW));
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
	    
	    for (Command c = null; ; c = game.pollCommands()) {
	        // Adds a delay to stop the program hanging
	        try {Thread.sleep(0);} 
	        catch (Exception e) {e.printStackTrace();}
	        
	        // If there are no commands, continue
	        if (c==null) continue;
	        
	        // Get the command ID from the command and run appropriate game method
	        switch (c.getCommandID()) {
	            case DRAW:          game.refreshDisplay();              break;
	            case EXIT:          game.close();	                   break;
	            case GAME_MESSAGE:  game.gameMessage(c);              break;
	            default:                                               break;
	        }
	    }
	}

	/**
	 * A public method to tell the MazePuzzleGame to do something.
	 */
	public void notify(Command c) {
	    addCommand(c);
	}

    /**
     * Executed when the display needs to be refreshed.
     * Do this after the game has been updated in some way.
     */
    public void refreshDisplay() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                disp.update();
            }
        });
    }

    /**
     * Moves player up a coordinate
     */
    private void gameMessage(Command c) {
        String[] message = c.getMessage();
        game.inbox(message);
        addCommand(new Command(Com.DRAW));
	}
    
    /**
     * Adds command c to the command queue
     * @param c
     */
	private void addCommand(Command c) {
	    commands.add(c);
	}
	
	/**
	 * Gets first command from the queue
	 * 
	 * @return
	 */
	private Command pollCommands() {
	    return commands.poll();
	}
    
    /**
     * Executed when asked to close.
     */
    private void close () {
        disp.close();
        System.exit(0);
    }
	
}
