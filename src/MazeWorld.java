import java.util.Queue;


public class MazeWorld {
    Queue<Command> commands;
    Maze maze;
    Character c;
    private boolean winStatus;
    
    public MazeWorld (int x, int y, Queue<Command> commands) {
        this.commands = commands;
        this.maze = new Maze(x, y);        
        this.maze.mazeGenerator();
        this.c = new Character(0, 0, "@");
        this.winStatus = false;
    }

    public void generateMap(int x, int y) {
        maze = new Maze(x, y);
        c = new Character(0,0,"@");
        maze.mazeGenerator();
    }

    public Maze getMaze() {
        return maze;
    }
    public boolean getWinStatus () {
        return winStatus;
    }
    public void setWinStatus (boolean b) {
        winStatus = b;
    }
    
    public void update() {
        boolean changed = false;
        // Things the mazeWorld needs to do/check
        if (hasCharacterWon()) {
            setWinStatus(true);
            changed = true;
        }
        
        if (changed) addCommand(new Command(Com.DRAW));
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
        update();
    }
    public void moveCharacterLeft() {
        if (maze.isLeft(c.getX(), c.getY())) c.setX(c.getX()-1);
        update();
    }
    public void moveCharacterRight() {
        if (maze.isRight(c.getX(), c.getY())) c.setX(c.getX()+1);
        update();
    }
    public void moveCharacterUp() {
        if (maze.isUp(c.getX(), c.getY())) c.setY(c.getY()-1);
        update();
    }

    public boolean hasCharacterWon() {
        // TODO Auto-generated method stub
        int characterX = c.getX();
        int characterY = c.getY();
        
        int finishX = maze.getFinish().getX();
        int finishY = maze.getFinish().getY();
        
        if (characterY == finishY) {
            if (characterX == -1 || characterX+1 == maze.getWidth()) {
                return true;
            }
        } else if (characterX == finishX) {
            if (characterY == -1 || characterY+1 == maze.getHeight()) {
                return true;
            }
        }
        return false;
    }
    public void addCommand (Command c) {
        commands.add(c);
    }
}





