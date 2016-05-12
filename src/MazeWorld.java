import java.util.ArrayList;
import java.util.Iterator;
import java.util.Queue;
import java.util.Random;


public class MazeWorld {
    private Queue<Command> commands;
    private Preferences pref;
    private Maze maze;
    private Character player;
    private AIControl ai;
    private ArrayList<Entity> entities;
    private boolean lockPlayerControl;
    private boolean winStatus;
    private boolean updated;
    
    public MazeWorld (Queue<Command> commands, Preferences pref) {
        this.commands = commands;
        this.pref = pref;
        generateWorld(pref.getValue("defaultMapWidth"), pref.getValue("defaultMapHeight"));
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
        ai = new AI2();
        entities = new ArrayList<Entity>();
        maze.mazeGenerator();
        player = new Character(maze.getStart().getX(), maze.getStart().getY(), pref.getText("playerName"));
        
        float h = (float)maze.getHeight();
        float w = (float)maze.getWidth();
        float r = (float)pref.getValue("defaultCoinRatio");
        
        float numberOfCoins = (h*w)*(r/100);
        generateCoins((int)numberOfCoins);
        winStatus = false;
        lockPlayerControl = false;
        updated = false;
    }
    
    public void generateCoins(int instances) {

        Random rand = new Random();
        int xC = rand.nextInt(maze.getWidth());
        int yC = rand.nextInt(maze.getHeight());
        
        for (int x = 0; x < instances; x++) {
            while (!uniqueCoordinates(xC, yC)) {
                xC = rand.nextInt(maze.getWidth());
                yC = rand.nextInt(maze.getHeight());
            }
            Coins coins = new Coins(xC,yC,50);
            entities.add(coins);
        }
    }
    
    public boolean uniqueCoordinates(int x, int y) {
        if (maze.isStart(x,y)) return false;
        if (maze.isFinish(x,y)) return false;
        for (Entity e : entities) {
            if (e.getX() == x && e.getY() == y) return false;
        }
        return true;
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
        entityCollision();
        
        if (updated) addCommand(new Command(Com.DRAW));
    }
    
    private void entityCollision () {
        Iterator<Entity> iter = entities.iterator();
        while (iter.hasNext()) {
            Entity e = iter.next();
            if (isChatacterHere(e.getX(), e.getY())) {
                if (e instanceof Coins) {
                    player.addCoins(((Coins)e).getValue());
                    iter.remove();
                    updated = true;
                }
            }
        }
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
        // Ask AI to make a move and add that to the command queue
        try {
            addCommand(ai.makeMove(this));
            Thread.sleep(50);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        
        // if the player has reached the end
        if (winStatus) {
            pref.toggleBool("autoComplete");
        }
        
        // if the player is set to auto complete, send another solve command
        if (pref.getBool("autoComplete")) {
            addCommand(new Command(Com.SOLVE));
        }
        update();
        return;
        
    }

    public int getPlayerCoins() {
        return player.getCoins();
    }

    public boolean isCoins(int x, int y) {
        for (Entity e : entities) {
            if (e instanceof Coins) {
                if (e.getX() == x && e.getY() == y) {
                    return true;
                }
            }
        }
        return false;        
    }
}





