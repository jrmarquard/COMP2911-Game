import java.util.Queue;


public class MazeWorld {
    Queue<Command> commands;
    Maze maze;
    Character player;
    private boolean winStatus;
    
    public MazeWorld (int x, int y, Queue<Command> commands) {
        this.commands = commands;
        generateMap(x,y);
    }

    public void generateMap(int x, int y) {
        maze = new Maze(x, y);
        maze.mazeGenerator();
        player = new Character(maze.getNodeNextToStart().getX(), maze.getNodeNextToStart().getY(), "@");
        winStatus = false;
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
        return player.getX();
    }
    public int getCharacterPosY () {
        return player.getY();
    }
    public String getCharacterName() {
        return player.getName();
    }
    public boolean isChatacterHere (int x, int y) {
        return x == player.getX() && y == player.getY();
    }
    public void moveCharacterDown() {
        if (maze.isDown(player.getX(), player.getY())) player.setY(player.getY()+1);
        update();
    }
    public void moveCharacterLeft() {
        if (maze.isLeft(player.getX(), player.getY())) player.setX(player.getX()-1);
        update();
    }
    public void moveCharacterRight() {
        if (maze.isRight(player.getX(), player.getY())) player.setX(player.getX()+1);
        update();
    }
    public void moveCharacterUp() {
        if (maze.isUp(player.getX(), player.getY())) player.setY(player.getY()-1);
        update();
    }

    public boolean hasCharacterWon() {
        // TODO Auto-generated method stub
        int characterX = player.getX();
        int characterY = player.getY();
        
        int finishX = maze.getNodeNextToFinish().getX();
        int finishY = maze.getNodeNextToFinish().getY();
        
        return characterX == finishX && characterY == finishY;
    }
    public void addCommand (Command c) {
        commands.add(c);
    }
}





