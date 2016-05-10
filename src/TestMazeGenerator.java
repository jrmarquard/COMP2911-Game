
public class TestMazeGenerator {

	public static void main(String[] args) {
		Maze maze = new Maze(7, 7);
		maze.mazeGenerator();
		
		int x, y;
		x = maze.getStart().getX();
		y = maze.getStart().getY();
		
		if(x < 0) {
			x = 0;
		} else if(x == maze.getWidth()) {
			x -= 1;
		} else if(y < 0) {
			y = 0;
		} else if(y == maze.getHeight()) {
			y -= 1;
		}
		
		Player player = new Player(x, y);
		maze.printMaze(player);
	}
}
