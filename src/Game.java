import java.util.ArrayList;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Game {
    // The game manager, we use this to notify it off messages with the nofity() method
    private MazePuzzleGame manager;
    
    // Used to load preferences set by the user
    private Preferences pref;
    
    // Collection of the worlds which are identified by a unique string
    private Map<String, World> worlds;
    
    // Executor to run the AIs in
    private ScheduledExecutorService aiPool;
    
    public Game (MazePuzzleGame manager, Preferences pref) {
        this.manager = manager;
        this.pref = pref;
        this.worlds = new ConcurrentSkipListMap<String, World>();       
    }
    
    public void newGame() {
        // String gameMode = pref.getText("gameMode");

        int height = pref.getValue("defaultMapHeight");
        int width = pref.getValue("defaultMapWidth");
        aiPool = Executors.newScheduledThreadPool(4); 
        boolean doorAndKey = pref.getBool("doorAndKey");
        
        TimeUnit timeUnit = TimeUnit.MILLISECONDS;
        int aiRefreshRate = 10;
        
        for (int x = 1; x <= 4; x++) {
            String opt = pref.getText("player"+x);
            if (opt.equals("Human")) {
                World world = new World(manager, "world"+x, height, width, doorAndKey);
                worlds.put("world"+x, world);
                System.out.println("Added Human");
                world.addPlayer("Moneymaker");                    
            } else if (opt.equals("Easy AI")) {
                World world = new World(manager, "world"+x, height, width, doorAndKey);
                worlds.put("world"+x, world);
                world.addPlayer("Moneymaker");
                AI ai = new AISolve(world,"Moneymaker", "easy");
                aiRunnable air = new aiRunnable(ai);
                aiPool.scheduleAtFixedRate(air, 0, aiRefreshRate, timeUnit);
            } else if (opt.equals("Med AI")) {
                World world = new World(manager, "world"+x, height, width, doorAndKey);
                worlds.put("world"+x, world);
                world.addPlayer("Moneymaker");
                AI ai = new AISolve(world,"Moneymaker", "med");
                aiRunnable air = new aiRunnable(ai);
                aiPool.scheduleAtFixedRate(air, 0, aiRefreshRate, timeUnit);
            } else if (opt.equals("Hard AI")) {
                World world = new World(manager, "world"+x, height, width, doorAndKey);
                worlds.put("world"+x, world);
                world.addPlayer("Moneymaker");
                AI ai = new AISolve(world,"Moneymaker", "hard");
                aiRunnable air = new aiRunnable(ai);
                aiPool.scheduleAtFixedRate(air, 0, aiRefreshRate, timeUnit);
            }
        }
    }
    
    /**
     * Returns the coins of each player in each world
     * @return
     */
    public ArrayList<Integer> getPlayerCoins() {
        ArrayList<Integer> coins = new ArrayList<Integer>();
        for (String s: worlds.keySet()) {
            World w = worlds.get(s);
            coins.add(w.getPlayerCoins("Moneymaker"));
        }        
        return coins;
    }
    
    public World getWorld(String worldName) {
        return worlds.get(worldName);
    }

    public ArrayList<World> getWorlds() {
        ArrayList<World> worlds = new ArrayList<World>();
        for (String s : this.worlds.keySet()) {
            worlds.add(this.worlds.get(s));
        }
        return worlds;
    }

    public void inbox(String[] message) {
        switch(message[0]) {
            case "move":
                World w = worlds.get(message[1]);
                w.moveBeing(message[2], message[3]);
                break;                    
            case "newGame":
                newGame();
                break;
            case "endGame":
                endGame();
                break;
            default:
                break;
        }
    }
    
    private void endGame() {
        // Remove old worlds
        worlds.clear();
        // Stop new submissions and end all currently running tasks immediately
        aiPool.shutdownNow();
        
    }

    private class aiRunnable implements Runnable {
        
        AI ai;
        
        public aiRunnable(AI ai) {
            this.ai = ai;
        }
        
        public void run() {
            try {
                manager.submitCommand(ai.makeMove());
            } catch (Exception e){
                System.out.println("AI run error.");
                e.printStackTrace();
            }
        }
    };
    
}
