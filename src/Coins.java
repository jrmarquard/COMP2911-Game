
public class Coins extends Entity {
    
    private int value;
    
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Coins(int x, int y, int value) {
        super(x, y);
        this.value = value;
    }

}
