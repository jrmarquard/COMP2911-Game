import java.awt.EventQueue;

public class MazePuzzleGame {
    
    AppState state;
    DisplayInterface disp;
    MazeWorld world;
    
    public MazePuzzleGame() {
        AppState state = new AppState();
        DisplayInterface disp = new GUI();
        MazeWorld world = new MazeWorld();
    }
    
    
	public static void main(String[] args) {
    
	    DisplayInterface disp = new GUI();
        disp.update(null);
	    
	    //MazePuzzleGame game = new MazePuzzleGame();
    }	
}