import java.util.ArrayList;
import java.util.Queue;


public class MazeWorld {
    private Queue<Command> commands;
    private Maze maze;
    private Character player;
    private boolean lockPlayerControl;
    private boolean winStatus;
    private boolean updated;
    
    public MazeWorld (int x, int y, Queue<Command> commands) {
        this.commands = commands;
        generateMap(x,y);
    }

    public void generateMap(int x, int y) {
        maze = new Maze(x, y);
        maze.mazeGenerator();
        player = new Character(maze.getNodeNextToStart().getX(), maze.getNodeNextToStart().getY(), "@");
        winStatus = false;
        lockPlayerControl = false;
        updated = false;
    }

    public Maze getMaze() {
        return maze;
    }
    public boolean getWinStatus () {
        return winStatus;
    }
    
    public void update() {
        // Things the mazeWorld needs to do/check
        if (hasCharacterWon()) {
            winStatus = true;
            lockPlayerControl = true;
            updated = true;
        }
        
        if (updated) addCommand(new Command(Com.DRAW));
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
        if (lockPlayerControl) return;
        if (maze.isDown(player.getX(), player.getY())) player.setY(player.getY()+1);
        update();
    }
    public void moveCharacterLeft() {
        if (lockPlayerControl) return;
        if (maze.isLeft(player.getX(), player.getY())) player.setX(player.getX()-1);
        update();
    }
    public void moveCharacterRight() {
        if (lockPlayerControl) return;
        if (maze.isRight(player.getX(), player.getY())) player.setX(player.getX()+1);
        update();
    }
    public void moveCharacterUp() {
        if (lockPlayerControl) return;
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
    
    public void solveCharacter() {
        // Creates new AI
        AI ai = new AI();
        
        // start node is the player's position
        Node previous = maze.getNode(player.getX(), player.getY());
        
        // get the path to be traversed
        ArrayList<Node> path = ai.traverseMaze(maze, previous);
        
        // temp solution. just get first move.
//        previous = path.remove(0);
//        Node next = path.remove(0);
//        
//        previous.print();
//        next.print();
//        
//        if (previous.isLeft(next)) {
//            moveCharacterLeft();
//        } else if (previous.isUp(next)) {
//            moveCharacterUp();
//        } else if (previous.isRight(next)) {
//            moveCharacterRight();
//        } else if (previous.isDown(next)) {
//            moveCharacterDown();
//        } else {
//            System.out.println("invalid");
//        }
        
        
        // removes the first node (where the path starts??)
        Node next = path.remove(0);
        while(!path.isEmpty()){
            previous = next;
            next = path.remove(0);
            
            // get direction of movement
            if (previous.isLeft(next)) {
                moveCharacterLeft();
            } else if (previous.isUp(next)) {
                moveCharacterUp();
            } else if (previous.isRight(next)) {
                moveCharacterRight();
            } else if (previous.isDown(next)) {
                moveCharacterDown();
            } else {
                System.out.println("invalid");
            }
            
            // update maze to show movement
            update();
            
            // wait
            try {
                Thread.sleep(50);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }

        updated = true;
        update();
    }
}





