import java.util.ArrayList;
import java.util.Iterator;
import java.util.Queue;
import java.util.Random;


public class MazeWorld {
    private Queue<Command> commands;
    private Preferences pref;
    private Maze maze;
    private ArrayList<Character> players;
    private AIControl ai;
    private ArrayList<Entity> entities;
    private boolean multiplayer;
    private boolean lockPlayerControl;
    private boolean winStatus;
    private int winPlayer;
    private boolean updated;
    
    public MazeWorld (Queue<Command> commands, Preferences pref) {
        this.commands = commands;
        this.pref = pref;
        this.players = new ArrayList<Character>();
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
        players.clear();
        players.add(new Character(new Coordinate(maze.getStart().getX(), maze.getStart().getY()), pref.getText("playerName")));
        
        float h = (float)maze.getHeight();
        float w = (float)maze.getWidth();
        float r = (float)pref.getValue("defaultCoinRatio");
        
        float numberOfCoins = (h*w)*(r/100);
        generateCoins((int)numberOfCoins);
        multiplayer = false;
        winStatus = false;
        winPlayer = -1;
        lockPlayerControl = false;
        updated = false;
    }
    
    private void generateCoins(int instances) {

        Random rand = new Random();
        int xC = rand.nextInt(maze.getWidth());
        int yC = rand.nextInt(maze.getHeight());
        int coinValue = 5+rand.nextInt(80);
        
        for (int x = 0; x < instances; x++) {
            while (!uniqueCoordinates(xC, yC)) {
                xC = rand.nextInt(maze.getWidth());
                yC = rand.nextInt(maze.getHeight());
                coinValue = 5+rand.nextInt(80);
            }
            Coins coins = new Coins(new Coordinate(xC,yC),coinValue);
            entities.add(coins);
        }
    }
    
    private boolean uniqueCoordinates(int x, int y) {
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
    
    public boolean getIsMultiplayer() {
    	return this.multiplayer;
    }
    
    public void setMuptiplayer(boolean multiplayer) {
    	this.multiplayer = multiplayer;
    	players.add(new Character(new Coordinate(maze.getStart().getX(), maze.getStart().getY()), pref.getText("playerName")));
    }
    
    /**
     * Returns true if the game has been won.
     * 
     * @return the winStatus boolean
     */
    public boolean getWinStatus () {
        return winStatus;
    }
    
    public int getWinPlayer() {
    	return this.winPlayer + 1;
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
    	int i;
        // Things the mazeWorld needs to do/check
    	for(i = 0; i < this.players.size(); i++) {
    		if (hasCharacterWon(i)) {
                winStatus = true;
                winPlayer = i;
                lockPlayerControl = true;
                updated = true;
                break;
            }
    	}
    	
        entityCollision();
        
        if (updated) {
            updated = false;
            addCommand(new Command(Com.DRAW));
        }
    }
    
    private void entityCollision () {
        Iterator<Entity> iter = entities.iterator();
        while (iter.hasNext()) {
            Entity e = iter.next();
            
            for(Character player: this.players) {
            	if (player.getCoordinate().equals(e.getCoordinate())) {
                    if (e instanceof Coins) {
                        player.addCoins(((Coins)e).getValue());
                        iter.remove();
                        updated = true;
                    }
                }
            }
        }
    }
    /**
     * Return the coordinates of the player
     * 
     * @return x coordinate of the player
     */
    public Coordinate getPlayerCoordinate (int player) {
        return players.get(player).getCoordinate();
    }
    
    /**
     * Gets the name of the character
     * 
     * @return character's name
     */
    public String getCharacterName(int player) {
        return players.get(player).getName();
    }
    
    public void moveCharacterDown(int player) {
        if (lockPlayerControl) {
            return;
        }
        if (maze.isDown(players.get(player).getCoordinate())) {
        	players.get(player).setY(players.get(player).getY()+1);
        }
        update();
    }
    public void moveCharacterLeft(int player) {
        if (lockPlayerControl) {
            return;
        }
        if (maze.isLeft(players.get(player).getCoordinate())) {
        	players.get(player).setX(players.get(player).getX()-1);
        }
        update();
    }
    public void moveCharacterRight(int player) {
        if (lockPlayerControl) {
            return;
        }
        if (maze.isRight(players.get(player).getCoordinate())) {
        	players.get(player).setX(players.get(player).getX()+1);
        }
        update();
    }
    public void moveCharacterUp(int player) {
        if (lockPlayerControl) {
            return;
        }
        
        if (maze.isUp(players.get(player).getCoordinate())) {
        	players.get(player).setY(players.get(player).getY()-1);
        }
        update();
    }

    public boolean hasCharacterWon(int player) {
        return maze.getFinishCoordinate().equals(getPlayerCoordinate(player));
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
    }

    public int getPlayerCoins(int player) {
        return players.get(player).getCoins();
    }
    
    public ArrayList<Coordinate> getEntityCoordinates() {
        ArrayList<Coordinate> coords = new ArrayList<Coordinate>();
        for (Entity e : entities) {
            coords.add(e.getCoordinate());
        }
        return coords;
    }
    
    public Coordinate getStart() {
        return new Coordinate(maze.getStart().getX(), maze.getStart().getY());
    }
    public Coordinate getFinish() {
        return new Coordinate(maze.getFinish().getX(), maze.getFinish().getY());
    }
    
}