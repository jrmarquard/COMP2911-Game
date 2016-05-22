import java.util.ArrayList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Game {
    // The game manager, we use this to notify it off messages with the nofity() method
    private Queue<Command> commands;
    
    // Used to load preferences set by the user
    private Preferences pref;
    
    // Collection of the worlds which are identified by a unique string
    private Map<String, World> worlds;
    
    // Executor to run the AIs in
    private ScheduledExecutorService aiThreadPool;
    
    private boolean winStatus;
    private int winPlayer;
    
    public Game (Queue<Command> commands, Preferences pref) {
        this.commands = commands;
        this.pref = pref;
        this.worlds = new TreeMap<String, World>();       
    }
    
    public void newGame() {
        // String gameMode = pref.getText("gameMode");

        int height = pref.getValue("defaultMapHeight");
        int width = pref.getValue("defaultMapWidth");
        aiThreadPool = Executors.newScheduledThreadPool(4); 
        
        for (int x = 1; x <= 4; x++) {
            String opt = pref.getText("player"+x);
            if (opt.equals("Human")) {
                World world = new World(commands, "world"+x, height, width);
                worlds.put("world"+x, world);
                System.out.println("Added Human");
                world.addPlayer("Moneymaker");                    
            } else if (opt.equals("Easy AI")) {
                World world = new World(commands, "world"+x, height, width);
                worlds.put("world"+x, world);
                world.addPlayer("Moneymaker");
                AI ai = new AISolve(world,"Moneymaker", "easy");
                aiRunnable air = new aiRunnable(ai, commands);
                aiThreadPool.scheduleAtFixedRate(air, 0, 200, TimeUnit.MILLISECONDS);
            } else if (opt.equals("Med AI")) {
                World world = new World(commands, "world"+x, height, width);
                worlds.put("world"+x, world);
                world.addPlayer("Moneymaker");
                AI ai = new AISolve(world,"Moneymaker", "med");
                aiRunnable air = new aiRunnable(ai, commands);
                aiThreadPool.scheduleAtFixedRate(air, 0, 200, TimeUnit.MILLISECONDS);
            } else if (opt.equals("Hard AI")) {
                World world = new World(commands, "world"+x, height, width);
                worlds.put("world"+x, world);
                world.addPlayer("Moneymaker");
                AI ai = new AISolve(world,"Moneymaker", "hard");
                aiRunnable air = new aiRunnable(ai, commands);
                aiThreadPool.scheduleAtFixedRate(air, 0, 200, TimeUnit.MILLISECONDS);
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
    
    private void sendCommand (Command c) {
        commands.add(c);
    }

    public void moveBeing(String worldName, String id, String dir) {
        World w = this.worlds.get(worldName);
        w.moveBeing(id, dir);
        sendCommand(new Command(Com.DRAW));
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
        // TODO Auto-generated method stub

        worlds.clear();
        aiThreadPool.shutdownNow();
        
    }

    private class aiRunnable implements Runnable {
        
        AI ai;
        Queue<Command> commands;
        
        public aiRunnable(AI ai, Queue<Command> commands) {
            this.ai = ai;
            this.commands = commands;
        }
        
        public void run() {
            try {
                commands.add(ai.makeMove());
            } catch (Exception e){
                System.out.println("AI run error.");
                e.printStackTrace();
            }
        }
    };
    
}
