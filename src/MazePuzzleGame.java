import java.awt.EventQueue;
import java.awt.event.KeyEvent;
import java.util.EventObject;
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
    }

	public static void main(String[] args) {
	    
	    MazePuzzleGame game = new MazePuzzleGame(null);
        
	    game.addCommand(new Command(Command.commandID.DRAW, null));
	    
	    for (Command c = null; ; c = game.pollCommands()) {
	        if (c==null) continue;
	        
	        switch (c.getCommandID()) {
	            case NEW_MAP:      game.newMap();	                   break;
	            case DRAW:         game.refreshDisplay();              break;
	            case EXIT:         game.close();	                   break;
	            case KEYSTROKE:    game.keystroke(c.getEvent());       break;
	        }
	    }
	}
	
	private void keystroke(EventObject o) {
	    KeyEvent e = (KeyEvent) o;
	    
        switch (e.getKeyCode()) {
            case KeyEvent.VK_DOWN: world.moveCharacterDown(); break;
            case KeyEvent.VK_LEFT: world.moveCharacterLeft(); break;
            case KeyEvent.VK_RIGHT: world.moveCharacterRight();  break;
            case KeyEvent.VK_UP: world.moveCharacterUp();  break;
        }
        
        if (world.characterAtFinish()) {
            System.out.println("winnrder");
        }
        
        addCommand(new Command(Command.commandID.DRAW));
    }

    private void newMap() {
        world.generateMap(5, 5);
        addCommand(new Command(Command.commandID.DRAW));
    }

    public void refreshDisplay() {
	    disp.update();
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