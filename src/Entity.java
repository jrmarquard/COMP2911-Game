
public class Entity {
    
    public static final int ENEMY = 0;
    public static final int PLAYER = 1;
    
    public static final int MODE_IDLE = 10;
    public static final int MODE_ATTACK = 11;
    public static final int MODE_DEAD = 12;
    
    private String name;
    private int coins;
    private int level;
    private boolean key;
    private Node node;
    private String direction;
    private int mode;
    private int decay;
    private int type;

    public Entity(Node node, String name, int type) {
        this.node = node;
        this.type = type;
        this.name = name;
        this.coins = 0;
        this.level = 1;
        this.key = false;
        this.mode = MODE_IDLE;
        this.decay = -1;
        if (node.getDown() != null) this.direction = "down";
        else if (node.getUp() != null) this.direction = "up";
        else if (node.getLeft() != null) this.direction = "left";
        else if (node.getRight() != null) this.direction = "right";
    }
    
    public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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
