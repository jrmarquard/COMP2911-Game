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
    
    /**
     * generatWorld resets the mazeWorld. It needs to intiliase everything: 
     * - create a new maze
     * - creates new entities
     *    - player
     *    - coins
     *    - enemies .. etc
     * - sets flags to defaults
     *  
     * @param height height of the maze to be generated
     * @param width width of the maze to be generated
     */
    public void generateWorld(int width, int height) {
        maze = new Maze(width, height);
        ai = new AI(commands);
        maze.mazeGenerator();
        player = new Character(maze.getStart().getX(), maze.getStart().getY(), "@");
        winStatus = false;
        lockPlayerControl = false;
        updated = false;
    }

    /**
     * gets the maze
     * 
     * @return the maze
     */
    public Maze getMaze() {
        return maze;
    }
    
    /**
     * Returns true if the game has been won.
     * 
     * @return the winStatus boolean
     */
    public boolean getWinStatus () {
        return winStatus;
    }
    
    /**
     * Run this after any changes in the maze. It checks for anything
     * that needs to be updated. This includes:
     * - win conditions
     * - entity collisions
     *     - player picks up coins
     *     - player dies
     *     
     * If something has happened, ask the GUI to redraw the world.
     */
    public void update() {
        // Things the mazeWorld needs to do/check
        if (hasCharacterWon()) {
            winStatus = true;
            lockPlayerControl = true;
            updated = true;
        }
        
        if (updated) addCommand(new Command(Com.DRAW));
    }
    
    /**
     * Return the x coordinate of the player
     * 
     * @return x coordinate of the player
     */
    public int getCharacterPosX () {
        return player.getX();
    }

    /**
     * Return the y coordinate of the player
     * 
     * @return y coordinate of the player
     */
    public int getCharacterPosY () {
        return player.getY();
    }
    
    /**
     * Gets the name of the character
     * 
     * @return character's name
     */
    public String getCharacterName() {
        return player.getName();
    }
    
    /**
     * 
     * 
     * @param x
     * @param y
     * @return
     */
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





