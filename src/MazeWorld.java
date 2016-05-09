import java.util.Queue;


public class MazeWorld {
    Queue<Command> commands;
    Maze maze;
    Character c;
    
    public MazeWorld (int x, int y, Queue<Command> commands) {
        this.maze = new Maze(x, y);        
        maze.mazeGenerator();
        c = new Character(0, 0, "@");
    }

    public void generateMap(int x, int y) {
        maze = new Maze(x, y);
        c = new Character(0,0,"@");
        maze.mazeGenerator();
    }

    public Maze getMaze() {
        return maze;
    }
    
    public int getCharacterPosX () {
        return c.getX();
    }
    public int getCharacterPosY () {
        return c.getY();
    }
    public String getCharacterName() {
        return c.getName();
    }
    public boolean isChatacterHere (int x, int y) {
        return x == c.getX() && y == c.getY();
    }
    public void moveCharacterDown() {
        if (maze.isDown(c.getX(), c.getY())) c.setY(c.getY()+1);
    }
    public void moveCharacterLeft() {
        if (maze.isLeft(c.getX(), c.getY())) c.setX(c.getX()-1);
    }
    public void moveCharacterRight() {
        if (maze.isRight(c.getX(), c.getY())) c.setX(c.getX()+1);
    }
    public void moveCharacterUp() {
        if (maze.isUp(c.getX(), c.getY())) c.setY(c.getY()-1);
    }

    public boolean characterAtFinish() {
        // TODO Auto-generated method stub
        // return maze.isAdjacent(maze.getFinish().getX(), maze.getFinish().getY() ,c.getX(), c.getY());
        return false;
    }
}
