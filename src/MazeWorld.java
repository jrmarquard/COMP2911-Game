import java.util.ArrayList;
import java.util.Iterator;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MazeWorld {
    private Queue<Command> commands;
    private Preferences pref;
    private Maze maze;
    private ArrayList<Character> players;
    private String aiName;
    private ArrayList<Entity> entities;
    private boolean lockPlayerControl;
    private boolean winStatus;
    private int winPlayer;
    private boolean updated;
    
    public MazeWorld (Queue<Command> commands, Preferences pref) {
        this.commands = commands;
        this.pref = pref;
        generateWorld(pref.getValue("defaultMapWidth"), pref.getValue("defaultMapHeight"));
        
        // Creates a runable to add the the Scheduled Executor Service
        Runnable aiRunnable = new Runnable() {
            public void run() {
                runAI();
            }
        };

        // Creates a thread pool that can schedule commands to run after a given delay.
        // executor is a scheduled thread pool
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        
        // Adds the runnable to the scheduler to run
        // Waits 1 second before running it every 500 miliseconds
        executor.scheduleAtFixedRate(aiRunnable, 1000, 500, TimeUnit.MILLISECONDS);
        
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
        // Initialise local variables
        this.maze = new Maze(width, height);
        this.aiName = "The Teamaker";
        addCommand(new Command(Com.CREATE_AI));
        this.entities = new ArrayList<Entity>();
        this.players = new ArrayList<Character>();
        
        // Generate maze
        maze.mazeGenerator();
        
        // Add player
        players.add(new Character(new Coordinate(maze.getStart().getX(), maze.getStart().getY()), pref.getText("playerName")));
        
        float h = (float)maze.getHeight();
        float w = (float)maze.getWidth();
        float r = (float)pref.getValue("defaultCoinRatio");
        int numberOfCoins = (int)(h*w*(r/100));
        generateCoins(numberOfCoins);

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
    
    public int getNumberOfPlayers() {
    	return this.players.size();
    }
    
    /**
     * Sets the maze to multiplayer and adds an extra player
     * @param multiplayer the boolean to tell if it is a multiplayer maze
     */
    public void setMuptiplayer(int numPlayers) {
    	int i;
    	
    	for(i = 1; i < numPlayers; i++) {
    		players.add(new Character(new Coordinate(maze.getStart().getX(), maze.getStart().getY()), pref.getText("playerName")));
    	}
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
     * Returns the player who won
     * @return the player who won
     */
    public int getWinPlayer() {
    	return this.winPlayer;
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
    public String getCharacterName(int playerID) {
        return players.get(playerID-1).getName();
    }
    
    public void moveCharacterDown(int playerID) {
        int player = -1 + playerID;
        if (lockPlayerControl) {
            return;
        }
        if (maze.isDown(players.get(player).getCoordinate())) {
        	players.get(player).setY(players.get(player).getY()+1);
        }
        update();
    }
    public void moveCharacterLeft(int playerID) {
        int player = -1 + playerID;
        if (lockPlayerControl) {
            return;
        }
        if (maze.isLeft(players.get(player).getCoordinate())) {
        	players.get(player).setX(players.get(player).getX()-1);
        }
        update();
    }
    public void moveCharacterRight(int playerID) {
        int player = -1 + playerID;
        if (lockPlayerControl) {
            return;
        }
        if (maze.isRight(players.get(player).getCoordinate())) {
        	players.get(player).setX(players.get(player).getX()+1);
        }
        update();
    }
    public void moveCharacterUp(int playerID) {
        
        int player = -1 + playerID;
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
    
    public void turnAIOn() {
        players.get(0).toggleAIControl();
    }

    public void runAI() {
        for (Character c : players) {
            if (c.isAIControl()) {
                try {
                    // send a command to the AIAgency telling the AI of name "" to make a move
                    Thread.sleep(50);
                    addCommand(new Command(Com.RUN_AI));
                } catch(InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        if (winStatus) {
            pref.toggleBool("autoComplete");
        }
        
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
