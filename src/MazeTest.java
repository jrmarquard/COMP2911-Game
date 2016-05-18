
public class MazeTest {

	public static void main(String[] args) {
		Maze maze = new Maze(20, 20);
		maze.mazeGenerator();
		maze.KeyAndDoorGenerator();
		maze.printMaze();
	}
}
