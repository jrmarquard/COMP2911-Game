
public class Entity {

    private String name;
    private boolean AIControl;
    private int coins;
    private boolean key;
    private boolean dead;
    private Node node;
    private String direction;
    
    public Entity(Node node, String name) {
        this.node = node;        
        this.AIControl = false;
        this.name = name;
        this.coins = 0;
        this.key = false;
        this.dead = false;
        if (node.getDown() != null) this.direction = "down";
        else if (node.getUp() != null) this.direction = "up";
        else if (node.getLeft() != null) this.direction = "left";
        else if (node.getRight() != null) this.direction = "right";
    }

    public boolean isDead() {
        return dead;
    }
    
    public Node getNode() {
        return node;
    }
    public void setNode(Node n) {
        this.node = n;
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
    
    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
