/**
 * The Class Item is used to keep information on permanent things stored in the maze.
 * This includes coins, k
 */
public class Item {
    
    /** The constant COIN used for item type. */
    public final static int COIN = 1;
    
    /** The constant KEY used for item type. */
    public final static int KEY = 2;
    
    /** The constant ENERGY used for item type. */
    public final static int ENERGY = 3;
    
    /** The constant PLAYER_CORPSE used for item type. */
    public final static int PLAYER_CORPSE = 4;
    
    /** The constant ENEMY_CORPSE used for item type. */
    public final static int ENEMY_CORPSE = 5;

    /** The node where the item is. */
    private final Node node;
    
    /** The type of item, defined by final static ints of item. */
    private int type;
    
    /** The current decay of the item, default is -1. */
    private int decay;
    
    /** The value of the item. */
    private int value;
    
    /**
     * Constructor used for generic items with no decay and no value.
     *
     * @param node the node where the item is
     * @param itemType the item type
     */
    public Item(Node node, int itemType) {
        this.node = node;
        this.type = itemType;
        this.value = -1;
        this.decay = -1;
    }
    
    /**
     * Constructor used for items with decay or value.
     *
     * @param node the node where the item is
     * @param itemType the item type
     * @param value the decay or value of the item, depending on what type it is
     */
    public Item(Node node, int itemType, int value) {
        this.node = node;
        this.type = itemType;
        if (itemType == Item.COIN) {
            this.value = value;
            this.decay = -1;            
        } else if (itemType == Item.ENERGY) {
            this.value = -1;
            this.decay = value;
        } else {
            System.out.println("Invalid item type.");
        }
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
     * Gets the decay.
     *
     * @return the decay
     */
    public int getDecay() {
        return decay;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    public int getValue() {
        return value;
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
     * Decay decreases the decay by 1 until the game handles the item appropriately.
     * E.g. energy dissipates when it reaches 0.
     */
    public void decay() {
        decay--;
    } 
}
