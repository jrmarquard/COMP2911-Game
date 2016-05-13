
public class Entity {

    private Coordinate coord;
    
    public Entity (Coordinate coord) {
        this.coord = coord;
    }

    public Coordinate getCoordinate() {
        // passes out a new coordinate so that it can't be changed by another class
        return new Coordinate(coord);
    }
    public void setCoordinate(Coordinate coord) {
        this.coord = coord;
    }
    
    public int getY() {
        return coord.getY();
    }
    public int getX() {
        return coord.getX();
    }
    public void setX(int x) {
        coord.setX(x);
    }
    public void setY(int y) {
        coord.setY(y);
    }


}
