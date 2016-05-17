import java.awt.EventQueue;
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
		        case NEW_MAP:       game.newMap(c);                    break;
	            case DRAW:          game.refreshDisplay();             break;
	            case EXIT:          game.close();	                   break;
	            case ARROW_DOWN:    game.moveCharacterADown();         break;
	            case ARROW_LEFT:    game.moveCharacterALeft();         break;
	            case ARROW_RIGHT:   game.moveCharacterARight();        break;
	            case ARROW_UP:      game.moveCharacterAUp();           break;
	            case S_DOWN:        game.moveCharacterBDown();         break;
	            case A_LEFT:        game.moveCharacterBLeft();         break;
	            case D_RIGHT:       game.moveCharacterBRight();        break;
	            case W_UP:          game.moveCharacterBUp();           break;
	            case G_DOWN:        game.moveCharacterCDown();         break;
	            case F_LEFT:        game.moveCharacterCLeft();         break;
	            case H_RIGHT:       game.moveCharacterCRight();        break;
	            case T_UP:          game.moveCharacterCUp();           break;
	            case K_DOWN:        game.moveCharacterDDown();         break;
	            case J_LEFT:        game.moveCharacterDLeft();         break;
	            case L_RIGHT:       game.moveCharacterDRight();        break;
	            case I_UP:          game.moveCharacterDUp();           break;
	            case SOLVE:         game.solveCharacter();             break;
	            case IDLE:                                             break;
	            default: 
	                break;
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
    private void moveCharacterAUp() {
    	world.moveCharacterUp(world.getNumberOfPlayers() - 1);
        addCommand(new Command(Com.DRAW));
	}
    
    /**
     * Moves player left a coordinate
     */
    private void moveCharacterALeft() {
    	world.moveCharacterLeft(world.getNumberOfPlayers() - 1);
        addCommand(new Command(Com.DRAW));
    }
    
    /**
     * Moves player right a coordinate
     */
    private void moveCharacterARight() {
    	world.moveCharacterRight(world.getNumberOfPlayers() - 1);
        addCommand(new Command(Com.DRAW));
    }
    
    /**
     * Moves player down a coordinate
     */
    private void moveCharacterADown() {
    	world.moveCharacterDown(world.getNumberOfPlayers() - 1);
        addCommand(new Command(Com.DRAW));
    }
    
    /**
     * Moves player up a coordinate
     */
    private void moveCharacterBUp() {
    	if(world.getNumberOfPlayers() > 1) {
    		world.moveCharacterUp(0);
            addCommand(new Command(Com.DRAW));
    	}
	}
    
    /**
     * Moves player left a coordinate
     */
    private void moveCharacterBLeft() {
    	if(world.getNumberOfPlayers() > 1) {
	        world.moveCharacterLeft(0);
	        addCommand(new Command(Com.DRAW));
    	}
    }
    
    /**
     * Moves player right a coordinate
     */
    private void moveCharacterBRight() {
    	if(world.getNumberOfPlayers() > 1) {
	        world.moveCharacterRight(0);
	        addCommand(new Command(Com.DRAW));
    	}
    }
    
    /**
     * Moves player down a coordinate
     */
    private void moveCharacterBDown() {
    	if(world.getNumberOfPlayers() > 1) {
	        world.moveCharacterDown(0);
	        addCommand(new Command(Com.DRAW));
    	}
    }
    
    /**
     * Moves player up a coordinate
     */
    private void moveCharacterCUp() {
    	if(world.getNumberOfPlayers() > 2) {
    		world.moveCharacterUp(1);
            addCommand(new Command(Com.DRAW));
    	}
	}
    
    /**
     * Moves player left a coordinate
     */
    private void moveCharacterCLeft() {
    	if(world.getNumberOfPlayers() > 2) {
	        world.moveCharacterLeft(1);
	        addCommand(new Command(Com.DRAW));
    	}
    }
    
    /**
     * Moves player right a coordinate
     */
    private void moveCharacterCRight() {
    	if(world.getNumberOfPlayers() > 2) {
	        world.moveCharacterRight(1);
	        addCommand(new Command(Com.DRAW));
    	}
    }
    
    /**
     * Moves player down a coordinate
     */
    private void moveCharacterCDown() {
    	if(world.getNumberOfPlayers() > 2) {
	        world.moveCharacterDown(1);
	        addCommand(new Command(Com.DRAW));
    	}
    }
    
    /**
     * Moves player up a coordinate
     */
    private void moveCharacterDUp() {
    	if(world.getNumberOfPlayers() > 3) {
    		world.moveCharacterUp(2);
            addCommand(new Command(Com.DRAW));
    	}
	}
    
    /**
     * Moves player left a coordinate
     */
    private void moveCharacterDLeft() {
    	if(world.getNumberOfPlayers() > 3) {
	        world.moveCharacterLeft(2);
	        addCommand(new Command(Com.DRAW));
    	}
    }
    
    /**
     * Moves player right a coordinate
     */
    private void moveCharacterDRight() {
    	if(world.getNumberOfPlayers() > 3) {
	        world.moveCharacterRight(2);
	        addCommand(new Command(Com.DRAW));
    	}
    }
    
    /**
     * Moves player down a coordinate
     */
    private void moveCharacterDDown() {
    	if(world.getNumberOfPlayers() > 3) {
	        world.moveCharacterDown(2);
	        addCommand(new Command(Com.DRAW));
    	}
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
