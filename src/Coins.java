
public class Coins extends Item {
    
    private int value;
    
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Coins(Node node, int value) {
        super(node);
        this.value = value;
    }

}
