
public class Character extends Entity {

    private String name;
    private int coins;
    private boolean key;
    
    public Character(Coordinate coord, String name) {
        super(coord);
        this.name = name;
        this.coins = 0;
        key = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCoins() {
        return coins;
    }
    public void removeCoins(int coins) {
        this.coins -= coins;
    }

    public void addCoins(int coins) {
        this.coins += coins;
    }    
    
    public boolean getKey() {
    	return this.key;
    }
    
    public void setKey(boolean key) {
    	this.key = key;
    }
}
