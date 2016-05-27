import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

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
        
        // Get various game settings
        String gameMode = App.pref.getText("gameMode");
        int height = App.pref.getValue("defaultMapHeight");
        int width = App.pref.getValue("defaultMapWidth");
        boolean doorAndKey = App.pref.getBool("doorAndKey");
        
        // Create the game
        if (gameMode.equals("Race")) {
            App.pref.setPreference("bool.enemy=false");
            
            // Create first world, add player, add it to worlds
            World worldMaster = new World(manager, "world1", height, width);
            worldMaster.addPlayer("Moneymaker", App.pref.getText("player1"));
            worlds.put("world1", worldMaster);
            
            // Check if the other 3 worlds should be made
            for (int x = 2; x <= 4; x++) {
                String opt = App.pref.getText("player"+x);
                
                // If this player is turned off, skip this world
                if (opt.equals("Off")) {
                    continue;
                }
                
                // Create a copy of world master, app next player, change name, add it to worlds
                World world = worldMaster.copy();
                world.setName("world"+x);
                world.addPlayer("Moneymaker", opt);
                worlds.put("world"+x, world);
            }
        } else if (gameMode.equals("Adventure")) {
            // Create world 1
            World world1 = new World(manager, "world1", height, width);
            world1.addPlayer("Moneymaker", App.pref.getText("player1"));
            worlds.put("world1", world1);
            
            // If player2 is enabled, create a second world
            String opt = App.pref.getText("player2");
            if (!opt.equals("Off")) {
                World world2 = new World(manager, "world2", height, width);
                world2.addPlayer("Moneymaker", opt);
                worlds.put("world2", world2);
                
            }
        } else if (gameMode.equals("Battle")) {
            App.pref.setPreference("value.visibleRange=-1");
            App.pref.setPreference("bool.enemy=false");
            
            World world = new World(manager, "world1", height, width);
            worlds.put("world1", world);
            world.addPlayer("Moneymaker", App.pref.getText("player1"));
            world.addPlayer("Teadrinker", App.pref.getText("player2"));
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
                if (worlds.isEmpty()) return;
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
        for (World w : worlds.values()) {
            w.endWorld();
        }
        // Remove old worlds
        worlds.clear();
        
    }
    
    public File getAboutFile() {
    	return this.aboutFile;
    }
    
}
