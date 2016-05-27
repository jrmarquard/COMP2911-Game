
public class Item {
    public final static int COIN = 1;
    public final static int KEY = 2;
    public final static int ENERGY = 3;
    public final static int PLAYER_CORPSE = 4;
    public final static int ENEMY_CORPSE = 5;
    
    // The node of an item. An item cannot be moved, and as such it's node cannot be changed
    private final Node node;
    private int type;
    private int decay;
    private int value;
    
    /**
     * Constructor used for generic items with no decay and no value
     * @param node the node where the item is
     * @param itemType
     */
    public Item(Node node, int itemType) {
        this.node = node;
        this.type = itemType;
        this.value = -1;
        this.decay = -1;
    }
    
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDecay() {
        return decay;
    }

    public void setDecay(int decay) {
        this.decay = decay;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Node getNode() {
        return node;
    }
    
    public void decay() {
        decay--;
    }
    
    
    
    
    
    
    
}
