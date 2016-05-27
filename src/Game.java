import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;


public class Game {
    /**
     * The game manager, we use this to notify it off messages with the nofity() method
     */
    private App manager;
    
    /**
     * Collection of the worlds which are identified by a unique string
     */
    private ConcurrentMap<String, World> worlds;
    
    /**
     * Executor to run the AIs in
     */
    private ScheduledExecutorService aiPool;
    
    /**
     * Pause is used to halt all game state changes if true.
     */
    private boolean pause;
    
    private File aboutFile;
    
    /**
     * The constructor for game. 
     * @param manager Manager is where to send messages to.
     */
    public Game (App manager) {
        this.manager = manager;
        this.worlds = new ConcurrentSkipListMap<String, World>();
        this.pause = false;
        String aboutFileName = new String("about text.txt");
	    this.aboutFile = new File(aboutFileName);
    }
    
    /**
     * Creates a new game based on settings in the preferences class.
     */
    public void newGame() {
        // Pause the game while making it.
        pause = true;

        // Create executor service.
        aiPool = Executors.newScheduledThreadPool(4);
        
        // Get various game settings
        String gameMode = App.pref.getText("gameMode");
        int height = App.pref.getValue("defaultMapHeight");
        int width = App.pref.getValue("defaultMapWidth");
        boolean doorAndKey = App.pref.getBool("doorAndKey");
        boolean enemy = App.pref.getBool("enemy");
        
        // AI run settings
        TimeUnit timeUnit = TimeUnit.MILLISECONDS;
        int aiRefreshRate = 150;
        
        // Create the game
        if (gameMode.equals("Race")) {
            for (int x = 1; x <= 4; x++) {
                String opt = App.pref.getText("player"+x);
                
                // If this player is turned off, skip this world
                if (opt.equals("Off")) {
                    continue;
                }
                
                // Create the world and add it to worlds
                World world = new World(manager, "world"+x, height, width, doorAndKey);
                worlds.put("world"+x, world);
                
                // Create the default player
                world.addPlayer("Moneymaker");
                
                // If enemy is selected, add it to the world and attach an AI
                if (enemy) {
                    world.addEnemy("Enemy");
                    aiRunnable AIRunEnemy = new aiRunnable(new AIEnemy(world,"Enemy"));
                    aiPool.scheduleAtFixedRate(AIRunEnemy, 650, aiRefreshRate, timeUnit);
                }
                
                // If AI is specified add it
                if (opt.equals("Easy AI")) {
                    aiRunnable AIRun = new aiRunnable(new AIPlayer(world,"Moneymaker", "easy"));
                    aiPool.scheduleAtFixedRate(AIRun, 650, aiRefreshRate, timeUnit);
                } else if (opt.equals("Med AI")) {
                    aiRunnable air = new aiRunnable(new AIPlayer(world,"Moneymaker", "med"));
                    aiPool.scheduleAtFixedRate(air, aiRefreshRate*(5/10), aiRefreshRate, timeUnit);
                } else if (opt.equals("Hard AI")) {
                    aiRunnable air = new aiRunnable(new AIPlayer(world,"Moneymaker", "hard"));
                    aiPool.scheduleAtFixedRate(air, aiRefreshRate*(8/10), aiRefreshRate, timeUnit);
                }
            }
        } else if (gameMode.equals("Infinite Mazes")) {
            // not implemented yet
        } else if (gameMode.equals("Battle")) {
            // Create a world with 2 players
            // Start them at start/finish
            // No key door
            // No enemy
        }
        pause = false;
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

    synchronized public void inbox(String[] message) {
        switch(message[0]) {
            case "newGame":
                newGame();
                break;
            case "endGame":
                endGame();
                break;
            case "pause":
                togglePause();
                break;
            default:
                if (pause) return;
                try {
                    worlds.get(message[0]).sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Game message error.");
                }
                break;
        }
    }
    
    /**
     * Toggles the pause on the game.
     */
    private void togglePause() {
        if (pause == false) {
            pause = true;
        } else {
            pause = false;
        }
    }
    
    /**
     * endGame() clears the game data and stops processing the ai threads.
     */
    private void endGame() {
        // Stop new submissions and end all currently running tasks immediately
        aiPool.shutdownNow();
        
        // Remove old worlds
        worlds.clear();
        
    }

    /**
     * aiRunnable executes an ai's makeMove method and sends it to the app.
     */
    private class aiRunnable implements Runnable {
        AI ai;
        
        public aiRunnable(AI ai) {
            this.ai = ai;
        }
        
        public void run() {
            try {
                manager.sendMessage(ai.makeMove());
            } catch (Exception e){
                System.out.println("AI runnable error.");
                e.printStackTrace();
            }
        }
    };
    
    public File getAboutFile() {
    	return this.aboutFile;
    }
    
}
