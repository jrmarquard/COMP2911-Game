import java.awt.EventQueue;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

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
    MazeWorld world;
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
        this.world = new MazeWorld(commands, pref);
        this.disp = new GUI(this.pref, this.world, this.commands);
        
        this.addCommand(new Command(Com.DRAW, null));
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
	            case NEW_MAP:      game.newMap(c);                     break;
	            case DRAW:         game.refreshDisplay();              break;
	            case EXIT:         game.close();	                   break;
	            case MOVE_DOWN:    game.moveCharacterDown();           break;
	            case MOVE_LEFT:    game.moveCharacterLeft();           break;
	            case MOVE_RIGHT:   game.moveCharacterRight();          break;
	            case MOVE_UP:      game.moveCharacterUp();             break;
	            case SOLVE:        game.solveCharacter();              break;
	        }
	    }
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
	 * Executed when a new map is requested
	 * 
	 * @param o the command object which ordered this method
	 */
    private void newMap(Command o) {
        CommandMap c = (CommandMap)o;
        int width = c.getWidth();
        int height = c.getHeight();
        world.generateWorld(width, height);
        addCommand(new Command(Com.DRAW));
    }
    
    /**
     * Executed when asked to close.
     */
    public void close () {
        disp.close();
        System.exit(0);
    }

    /**
     * Moves player up a coordinate
     */
    private void moveCharacterUp() {
	    world.moveCharacterUp();
        addCommand(new Command(Com.DRAW));
	}
    
    /**
     * Moves player left a coordinate
     */
    private void moveCharacterLeft() {
        world.moveCharacterLeft();
        addCommand(new Command(Com.DRAW));
    }
    
    /**
     * Moves player right a coordinate
     */
    private void moveCharacterRight() {
        world.moveCharacterRight();
        addCommand(new Command(Com.DRAW));
    }
    
    /**
     * Moves player down a coordinate
     */
    private void moveCharacterDown() {
        world.moveCharacterDown();
        addCommand(new Command(Com.DRAW));
    }
    
    /**
     * Moves the player once and checks if it has finished
     */
    private void solveCharacter() {
        // Move the character 1 spot and redraw
        world.solveCharacter();
        addCommand(new Command(Com.DRAW));
    }
    
    /**
     * Adds command c to the command queue
     * @param c
     */
	public void addCommand(Command c) {
	    commands.add(c);
	}
	
	/**
	 * Gets first command from the queue
	 * 
	 * @return
	 */
	public Command pollCommands() {
	    return commands.poll();
	}
	
}