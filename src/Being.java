
public class Being extends Entity {

    private String name;
    private boolean AIControl;
    private int coins;
    private boolean key;
    private boolean dead;
    
    public Being(Node node, String name) {
        super(node);        
        this.AIControl = false;
        this.name = name;
        this.coins = 0;
        this.key = false;
        this.dead = false;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public boolean isAIControl() {
        return AIControl;
    }

    public void setAIControl(boolean aiControl) {
        this.AIControl = aiControl;
    }
    public void toggleAIControl() {
        if (AIControl==false) {
            AIControl = true;
        } else {
            AIControl = false;
        }
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
