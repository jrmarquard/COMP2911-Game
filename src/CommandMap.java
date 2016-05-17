public class CommandMap extends Command {
    
    private int height;
    private int width;
    private int players;
    
    public CommandMap(Com id, int width, int height, int players) {
        super(id);
        this.width = width;
        this.height = height;
        this.players = players;
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
  
}