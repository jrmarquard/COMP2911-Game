
public class TestMazeGenerator {

	public static void main(String[] args) {
		Maze maze = new Maze(30, 30);
		maze.mazeGenerator();
		Player player = new Player(maze.getPlayerStart());
		maze.printMaze(player);
	}
}
