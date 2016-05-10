import java.awt.EventQueue;
import java.util.LinkedList;
import java.util.Queue;

public class MazePuzzleGame {
    
    AppState state;
    DisplayInterface disp;
    MazeWorld world;
    Queue<Command> commands;
    
    public MazePuzzleGame(String settingsLocation) {
        this.state = new AppState(settingsLocation);
        this.commands = new LinkedList<Command>();
        this.world = new MazeWorld(5, 5, commands);
        this.disp = new GUI(this.state, this.world, this.commands);
        
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                disp.initGUI();
            }
        });
    }

	public static void main(String[] args) {
	    
	    MazePuzzleGame game = new MazePuzzleGame(null);
        
	    game.addCommand(new Command(Com.DRAW, null));
	    
	    for (Command c = null; ; c = game.pollCommands()) {
	        try {
	            Thread.sleep(0);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        
	        if (c==null) continue;
	        
	        switch (c.getCommandID()) {
	            case NEW_MAP:      game.newMap();	                   break;
	            case DRAW:         game.refreshDisplay();              break;
	            case EXIT:         game.close();	                   break;
	            case MOVE_DOWN:    game.moveCharacterDown();          break;
	            case MOVE_LEFT:    game.moveCharacterLeft();          break;
	            case MOVE_RIGHT:   game.moveCharacterRight();         break;
	            case MOVE_UP:      game.moveCharacterUp();            break;  
	        }
	    }
	}
	private void moveCharacterUp() {
	    world.moveCharacterUp();
        addCommand(new Command(Com.DRAW));
	}
    private void moveCharacterLeft() {
        world.moveCharacterLeft();
        addCommand(new Command(Com.DRAW));
    }
    private void moveCharacterRight() {
        world.moveCharacterRight();
        addCommand(new Command(Com.DRAW));
    }
    private void moveCharacterDown() {
        world.moveCharacterDown();
        addCommand(new Command(Com.DRAW));
    }

    private void newMap() {
        world.generateMap(5, 5);
        addCommand(new Command(Com.DRAW));
    }

    public void refreshDisplay() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                disp.update();
            }
        });
	}
	public boolean isCommandsEmpty() {
	    return commands.isEmpty();
	}
	public void addCommand(Command c) {
	    commands.add(c);
	}
	public Command pollCommands() {
	    return commands.poll();
	}
	public void close () {
        disp.close();
        System.exit(0);
	}
	
}