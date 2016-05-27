/**
 * Entity is used in world.java to store information about all
 * players and enemies. They are different to items in that they can change 
 * position during the game, and have additional states.
 */
public class Entity {
    
    /** An entity who is an enemy has it's type field set to ENEMY. */
    public static final int ENEMY = 0;
    
    /** An entity who is a player has it's type field set to PLAYER. */
    public static final int PLAYER = 1;
    
    /** An entity mode is set to idle when it is not attacking or dead, but moving around the maze. */
    public static final int MODE_IDLE = 10;
    
    /** An entity can will be in attack mode briefly when it tries to
     * attack other entities around it. */
    public static final int MODE_ATTACK = 11;
    
    /** An entity mode is set to dead when it dies. */
    public static final int MODE_DEAD = 12;
    
    /** The name of the entity. */
    private String name;
    
    /** The number of coins collected by this entity. */
    private int coins;
    
    /** The level counter for the entity. */
    private int level;
    
    /** If key is true, the entity has the key for the level. */
    private boolean key;
    
    /** The node that the entity is currently at.     */
    private Node node;
    
    /** The direction the entity is facing. */
    private String direction;
    
    /** The mode of the entity, can be idle, attacking, or dead. */
    private int mode;
    
    /** Decay holds allows the entity to hold a mode for a short period of time. */
    private int decay;
    
    /** The type of the entity is either player or enemy. */
    private int type;

    /**
     * Constructor of an entity.
     *
     * @param node What node the entity starts at.
     * @param name The name of the entity.
     * @param type The type of entity (either Entity.PLAYER or Entity.ENEMY)
     */
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
    
    /**
     * Gets the level.
     *
     * @return the level
     */
    public int getLevel() {
		return level;
	}

	/**
	 * Sets the level.
	 *
	 * @param level the new level
	 */
	public void setLevel(int level) {
		this.level = level;
	}
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public int getType() {
        return type;
    }

	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Gets the node.
     *
     * @return the node
     */
    public Node getNode() {
        return node;
    }
    
    /**
     * Sets the node.
     *
     * @param n the new node
     */
    public void setNode(Node n) {
        this.node = n;
    }

    /**
     * Gets the mode.
     *
     * @return the mode
     */
    public int getMode() {
        return mode;
    }
    
    /**
     * Sets the mode.
     *
     * @param mode the new mode
     */
    public void setMode(int mode) {
        this.mode = mode;
        if (mode == MODE_ATTACK) {
            this.decay = 2;
        }
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the coins.
     *
     * @return the coins
     */
    public int getCoins() {
        return coins;
    }
    
    /**
     * Removes the coins.
     *
     * @param coins the coins
     */
    public void removeCoins(int coins) {
        this.coins -= coins;
    }

    /**
     * Adds the coins.
     *
     * @param coins the coins
     */
    public void addCoins(int coins) {
        this.coins += coins;
    } 
    
    /**
     * Gets the key.
     *
     * @return the key
     */
    public boolean getKey() {
        return this.key;
    }
    
    /**
     * Sets the key.
     *
     * @param key the new key
     */
    public void setKey(boolean key) {
        this.key = key;
    }
    
    /**
     * Gets the direction.
     *
     * @return the direction
     */
    public String getDirection() {
        return direction;
    }

    /**
     * Sets the direction.
     *
     * @param direction the new direction
     */
    public void setDirection(String direction) {
        this.direction = direction;
    }
    
    /**
     * Decay decreases the decay by 1 until it reaches 0, where it changes
     * the mode back to MODE_IDLE
     */
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
