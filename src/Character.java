
public class Character extends Entity {

    private String name;
    private int coins;
    
    public Character(int x, int y, String name) {
        super(x, y);
        this.name = name;
        this.coins = 0;
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

    
}
