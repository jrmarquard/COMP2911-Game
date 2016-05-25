import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
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
    
    // false if unpaused, true if paused
    private boolean pause;
    
    public Game (MazePuzzleGame manager, Preferences pref) {
        this.manager = manager;
        this.pref = pref;
        this.worlds = new ConcurrentSkipListMap<String, World>();
        this.pause = false;
    }
    
    public void newGame() {
        pause = false;
        String gameMode = pref.getText("gameMode");

        int height = pref.getValue("defaultMapHeight");
        int width = pref.getValue("defaultMapWidth");
        aiPool = Executors.newScheduledThreadPool(4); 
        boolean doorAndKey = pref.getBool("doorAndKey");
        boolean enemy = pref.getBool("enemy");
        
        TimeUnit timeUnit = TimeUnit.MILLISECONDS;
        int aiRefreshRate = 150;
        if (gameMode.equals("Race")) {
            for (int x = 1; x <= 4; x++) {
                String opt = pref.getText("player"+x);
                if (opt.equals("Human")) {
                    World world = new World(manager, "world"+x, height, width, doorAndKey);
                    worlds.put("world"+x, world);
                    world.addPlayer("Moneymaker");
                    
                    if(enemy) {
                    	world.addEnemy("Enemy");
                        AI aiE = new AIEnemy(world,"Enemy");
                        aiRunnable airE = new aiRunnable(aiE);
                        aiPool.scheduleAtFixedRate(airE, aiRefreshRate*(2/10), aiRefreshRate, timeUnit);

                    }
                } else if (opt.equals("Easy AI")) {
                    World world = new World(manager, "world"+x, height, width, doorAndKey);
                    worlds.put("world"+x, world);
                    world.addPlayer("Moneymaker");
                    AI ai = new AISolve(world,"Moneymaker", "easy");
                    aiRunnable air = new aiRunnable(ai);
                    aiPool.scheduleAtFixedRate(air, aiRefreshRate*(2/10), aiRefreshRate, timeUnit);
                    
                    if(enemy) {
                    	world.addEnemy("Enemy");
                        AI aiE = new AIEnemy(world,"Enemy");
                        aiRunnable airE = new aiRunnable(aiE);
                        aiPool.scheduleAtFixedRate(airE, aiRefreshRate*(2/10), aiRefreshRate, timeUnit);

                    }
                } else if (opt.equals("Med AI")) {
                    World world = new World(manager, "world"+x, height, width, doorAndKey);
                    worlds.put("world"+x, world);
                    world.addPlayer("Moneymaker");
                    AI ai = new AISolve(world,"Moneymaker", "med");
                    aiRunnable air = new aiRunnable(ai);
                    aiPool.scheduleAtFixedRate(air, aiRefreshRate*(5/10), aiRefreshRate, timeUnit);
                    
                    if(enemy) {
                    	world.addEnemy("Enemy");
                        AI aiE = new AIEnemy(world,"Enemy");
                        aiRunnable airE = new aiRunnable(aiE);
                        aiPool.scheduleAtFixedRate(airE, aiRefreshRate*(2/10), aiRefreshRate, timeUnit);

                    }
                } else if (opt.equals("Hard AI")) {
                    World world = new World(manager, "world"+x, height, width, doorAndKey);
                    worlds.put("world"+x, world);
                    world.addPlayer("Moneymaker");
                    AI ai = new AISolve(world,"Moneymaker", "hard");
                    aiRunnable air = new aiRunnable(ai);
                    aiPool.scheduleAtFixedRate(air, aiRefreshRate*(8/10), aiRefreshRate, timeUnit);
                    
                    if(enemy) {
                    	world.addEnemy("Enemy");
                        AI aiE = new AIEnemy(world,"Enemy");
                        aiRunnable airE = new aiRunnable(aiE);
                        aiPool.scheduleAtFixedRate(airE, aiRefreshRate*(2/10), aiRefreshRate, timeUnit);

                    }
                }
            }
        } else if (gameMode.equals("Solve")) {
            String opt = pref.getText("player"+1);
            World world = new World(manager, "world"+1, height, width, doorAndKey);
            worlds.put("world"+1, world);
            world.addPlayer("Moneymaker");
            
            if(enemy) {
            	world.addEnemy("Enemy");
                AI aiE = new AIEnemy(world,"Enemy");
                aiRunnable airE = new aiRunnable(aiE);
                aiPool.scheduleAtFixedRate(airE, aiRefreshRate*(2/10), aiRefreshRate, timeUnit);

            }
            
            if (opt.equals("Easy AI")) {
                AI ai = new AISolve(world,"Moneymaker", "easy");
                aiRunnable air = new aiRunnable(ai);
                aiPool.scheduleAtFixedRate(air, aiRefreshRate*(2/10), aiRefreshRate, timeUnit);
            } else if (opt.equals("Med AI")) {
                AI ai = new AISolve(world,"Moneymaker", "med");
                aiRunnable air = new aiRunnable(ai);
                aiPool.scheduleAtFixedRate(air, aiRefreshRate*(2/10), aiRefreshRate, timeUnit);
            } else if (opt.equals("Hard AI")) {
                AI ai = new AISolve(world,"Moneymaker", "hard");
                aiRunnable air = new aiRunnable(ai);
                aiPool.scheduleAtFixedRate(air, aiRefreshRate*(2/10), aiRefreshRate, timeUnit);
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
                if (pause) return;
                worlds.get(message[1]).moveBeing(message[2], message[3]);
                break;                    
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
                break;
        }
    }
    
    private void togglePause() {
        if (pause == false) {
            pause = true;
        } else {
            pause = false;
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
                manager.sendMessage(ai.makeMove());
            } catch (Exception e){
                System.out.println("AI run error.");
                e.printStackTrace();
            }
        }
    };
    
}
