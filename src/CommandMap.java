public class CommandMap extends Command {
    
    private int height;
    private int width;
    private int players;
    private int gameMode;
    int playerID;
    
    public CommandMap(Com id, int width, int height, int players, int gameMode) {
        super(id);
        this.width = width;
        this.height = height;
        this.players = players;
        this.gameMode = gameMode;
    }
    /**
     * This constructor is used to move a player
     * 
     * @param id the command
     * @param player the player
     */
    public CommandMap(Com id, int playerID) {
        super(id);
        this.playerID = playerID;
    }
    
    
    public int getPlayerID() {
        return playerID;
    }
    public void setPlayerID(int player) {
        this.playerID = player;
    }
    
    public int getPlayers() {
        return players;
    }

    public void setPlayers(int players) {
        this.players = players;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
  
    public int getGameMode() {
    	return this.gameMode;
    }
}