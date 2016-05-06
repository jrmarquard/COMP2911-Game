
public class MazeRunner {

	public static void main (String[] args) {
		Maze maze = new Maze(10, 10);
		maze.mazeGenerator();
		maze.setStart(-1, 0);
		maze.setFinish(10, 9);
		maze.printMaze();
	}
}