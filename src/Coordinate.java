
public class Coordinate {
    private int x;
    private int y;
    
    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public Coordinate (Coordinate coord) {
        this.x = coord.getX();
        this.y = coord.getY();
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
    
    public boolean equals(Object o) {
        if(o == null || !(o instanceof Coordinate)) {
            return false;
        }
        
        if(o == this) {
            return true;
        }
        
        Coordinate coord = (Coordinate) o;
        if(this.x == coord.getX() && this.y == coord.getY()) {
            return true;
        } else {
            return false;
        }
    }
    
    
}
