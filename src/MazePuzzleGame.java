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
    MazeWorld world;
    Queue<Command> commands;
    AIAgency aiAgency;
    
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
        this.world = new MazeWorld(this, pref);
        this.disp = new GUI(this.pref, this.world, this.commands);
        this.aiAgency = new AIAgency(commands);
        
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
		        case NEW_MAP:      game.newMap(c);                     break;
	            case DRAW:         game.refreshDisplay();              break;
	            case EXIT:         game.close();	                   break;
	            case MOVE_DOWN:
	            case MOVE_LEFT:
	            case MOVE_RIGHT:
	            case MOVE_UP:      game.moveCharacter(c);              break;
	            case TOGGLE_AI:    game.turnAIOn();                    break;
	            case RUN_AI:       game.runAI(c);                       break;
	            case CREATE_AI:    game.createAI(c);                    break;
	            case IDLE:                                             break;
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
	 * Asks the aiAgency to create an AI of a given name.
	 */
    private void createAI(Command c) {
        CommandAI cAI = (CommandAI) c;
        aiAgency.createAI(cAI.getName(), cAI.getWorld(), cAI.getSettings());
    }

    /**
     * Asks the aiAgency to run the AI of a given name.
     * It is expected that this AI will return a command 
     * to update itself in the game.
     */
    private void runAI(Command c) {
        CommandAI cAI = (CommandAI) c;
        aiAgency.makeMove(cAI.getName());
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
        
        if (c.getPlayers() > 1) {
            world.setMuptiplayer(c.getPlayers());
        }
        addCommand(new Command(Com.DRAW));
    }
    
    /**
     * Executed when asked to close.
     */
    private void close () {
        disp.close();
        System.exit(0);
    }

    /**
     * Moves player up a coordinate
     */
    private void moveCharacter(Command o) {
        CommandMap c = (CommandMap)o;
        int id = c.getPlayerID();
        switch(c.getCommandID()) {
            case MOVE_DOWN:     world.moveCharacterDown(id);    break;
            case MOVE_UP:       world.moveCharacterUp(id);      break;
            case MOVE_LEFT:     world.moveCharacterLeft(id);    break;
            case MOVE_RIGHT:    world.moveCharacterRight(id);   break;
            default: 
                break;
        }
        addCommand(new Command(Com.DRAW));
	}
    
    /**
     * Turns AI on
     */
    private void turnAIOn() {
        world.turnAIOn();
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
	
}
