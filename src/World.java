import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

/**
 * World contains:
 * - 1 maze
 * - Unlimited beings
 * - Unlimited items
 * 
 * @author John
 *
 */
public class World {
    
    private String name;
    
    // Only one maze per world
    private Maze maze;
    
    // Multiple beings in a world
    // Beings are things that can move/make decisions
    private Map<String, Being> beings;
    
    // Multiple items in a world.
    // Items are stationary objects that can be interacted with by 
    private ArrayList<Item> items;
    
    public World (String name, int width, int height) {
        this.setName(name);
        this.maze = new Maze(width, height);
        this.beings = new HashMap<String, Being>();
        this.items = new ArrayList<Item>();
        
        maze.mazeGenerator();
        
        // Add player
        
        // Generate coins
        generateCoins();
    }
    
    public void addPlayer(String name) {
        Being player = new Being(maze.getStartCoordinate(), name);
        beings.put(name, player);
    }
    
    public void generateCoins() {
        
        float h = (float)maze.getHeight();
        float w = (float)maze.getWidth();
        float r = (float)15;
        int numberOfCoins = (int)(h*w*(r/100));
        
        Random rand = new Random();
        int xC = rand.nextInt(maze.getWidth());
        int yC = rand.nextInt(maze.getHeight());
        int coinValue = 5+rand.nextInt(80);
        
        for (int x = 0; x < numberOfCoins; x++) {
            while (!uniqueCoordinates(xC, yC)) {
                xC = rand.nextInt(maze.getWidth());
                yC = rand.nextInt(maze.getHeight());
                coinValue = 5+rand.nextInt(80);
            }
            Coins coins = new Coins(new Coordinate(xC,yC),coinValue);
            items.add(coins);
        }
    }
    
    private boolean uniqueCoordinates(int x, int y) {
        if (maze.isStart(x,y)) return false;
        if (maze.isFinish(x,y)) return false;
        for (Item i : items) {
            if (i.getX() == x && i.getY() == y) return false;
        }
        return true;
    }
    
    public Maze getMaze() {
        return maze;
    }

    public int getPlayerCoins(String id) {
        return beings.get(id).getCoins();
    }
    
    public ArrayList<Coordinate> getEntityCoordinates() {
        ArrayList<Coordinate> coords = new ArrayList<Coordinate>();
        for (Item i : items) {
            coords.add(i.getCoordinate());
        }
        return coords;
    }
    
    public Coordinate getStart() {
        return new Coordinate(maze.getStart().getX(), maze.getStart().getY());
    }
    public Coordinate getFinish() {
        return new Coordinate(maze.getFinish().getX(), maze.getFinish().getY());
    }
    
    public void moveBeing(String id, String dir) {
        Being b = beings.get(id);
        if (b != null) {
            if (dir == "up" && maze.isUp(b.getCoordinate())) {
                b.setY(b.getY()-1);
            } else if (dir == "down" && maze.isDown(b.getCoordinate())) {
                b.setY(b.getY()+1);
            } else if (dir == "left" && maze.isLeft(b.getCoordinate())) {
                b.setX(b.getX()-1);
            } else if (dir == "right" && maze.isRight(b.getCoordinate())) {
                b.setX(b.getX()+1);
            }
        }
        update();
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
        entityCollision();
    }
    
    private void entityCollision () {
        Iterator<Item> iter = items.iterator();
        while (iter.hasNext()) {
            Entity e = iter.next();
            
            for(Map.Entry<String,Being> entry : beings.entrySet()) {
                Being b = entry.getValue();
                if (b.getCoordinate().equals(e.getCoordinate())) {
                    if (e instanceof Coins) {
                        b.addCoins(((Coins)e).getValue());
                        iter.remove();
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
    public Coordinate getPlayerCoordinate () {
        return beings.get("Moneymaker").getCoordinate();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coordinate getCoordinate(String id) {
        return beings.get(id).getCoordinate();
    }
    
}
