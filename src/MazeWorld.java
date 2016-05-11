import java.util.ArrayList;
import java.util.Queue;


public class MazeWorld {
    private Queue<Command> commands;
    private Maze maze;
    private Character player;
    private AI ai;
    private boolean lockPlayerControl;
    private boolean winStatus;
    private boolean updated;
    
    public MazeWorld (int x, int y, Queue<Command> commands) {
        this.commands = commands;
        generateWorld(x,y);
    }

    public void generateWorld(int x, int y) {
        maze = new Maze(x, y);
        ai = new AI(commands);
        maze.mazeGenerator();
        player = new Character(maze.getStart().getX(), maze.getStart().getY(), "@");
        winStatus = false;
        lockPlayerControl = false;
        updated = false;
    }

    public Maze getMaze() {
        return maze;
    }
    public boolean getWinStatus () {
        return winStatus;
    }
    
    public void update() {
        // Things the mazeWorld needs to do/check
        if (hasCharacterWon()) {
            winStatus = true;
            lockPlayerControl = true;
            updated = true;
        }
        
        if (updated) addCommand(new Command(Com.DRAW));
    }
    
    public int getCharacterPosX () {
        return player.getX();
    }
    public int getCharacterPosY () {
        return player.getY();
    }
    public String getCharacterName() {
        return player.getName();
    }
    public boolean isChatacterHere (int x, int y) {
        return x == player.getX() && y == player.getY();
    }
    public void moveCharacterDown() {
        if (lockPlayerControl) return;
        if (maze.isDown(player.getX(), player.getY())) player.setY(player.getY()+1);
        update();
    }
    public void moveCharacterLeft() {
        if (lockPlayerControl) return;
        if (maze.isLeft(player.getX(), player.getY())) player.setX(player.getX()-1);
        update();
    }
    public void moveCharacterRight() {
        if (lockPlayerControl) return;
        if (maze.isRight(player.getX(), player.getY())) player.setX(player.getX()+1);
        update();
    }
    public void moveCharacterUp() {
        if (lockPlayerControl) return;
        if (maze.isUp(player.getX(), player.getY())) player.setY(player.getY()-1);
        update();
    }

    public boolean hasCharacterWon() {
        // TODO Auto-generated method stub
        int characterX = player.getX();
        int characterY = player.getY();
        
        int finishX = maze.getFinish().getX();
        int finishY = maze.getFinish().getY();
        
        return characterX == finishX && characterY == finishY;
    }
    public void addCommand (Command c) {
        commands.add(c);
    }
    
    public void solveCharacter() {
        // where is the player right now?
        Node currentPosition = maze.getNode(player.getX(), player.getY());
        
        // find shortest path from where it currently is
        ai.traverseMaze(maze, currentPosition);
        
        // ai requests a move
        ai.makeMove();
        

        currentPosition = maze.getNode(player.getX(), player.getY());
        
        // check if they character has reached the end
        if (currentPosition.equals(maze.getFinish())) {
            // if the character is in the last tile
            return;
        } else {
            // if the character isn't in the last tile
            // wait, and issue another command
            try {
                Thread.sleep(50);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            update();         
            addCommand(new Command(Com.SOLVE));
        }
        
    }
}





