
public class Entity {
    
    public static final int MODE_IDLE = 0;
    public static final int MODE_ATTACK = 1;
    public static final int MODE_DEAD = 2;
    
    private String name;
    private boolean AIControl;
    private int coins;
    private boolean key;
    private Node node;
    private String direction;
    private int mode;
    private int decay;
    
    public Entity(Node node, String name) {
        this.node = node;        
        this.AIControl = false;
        this.name = name;
        this.coins = 0;
        this.key = false;
        this.mode = MODE_IDLE;
        this.decay = -1;
        if (node.getDown() != null) this.direction = "down";
        else if (node.getUp() != null) this.direction = "up";
        else if (node.getLeft() != null) this.direction = "left";
        else if (node.getRight() != null) this.direction = "right";
    }
    
    public Node getNode() {
        return node;
    }
    public void setNode(Node n) {
        this.node = n;
    }

    public int getMode() {
        return mode;
    }
    public void setMode(int mode) {
        this.mode = mode;
        if (mode == MODE_ATTACK) {
            this.decay = 2;
        }
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
    
    public void decay() {
        if (decay == -1) {
            return;
        } else {
            decay--;
            if (decay == 0) {
                mode = MODE_IDLE;            
            }
        }
    }
}
