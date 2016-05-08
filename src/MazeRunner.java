
import java.util.Scanner;

public class MazeRunner {

	public static void main (String[] args) {
		Maze maze = new Maze(10, 10);
		char move;
		boolean endGame = false;
		
		Scanner input = new Scanner(System.in);

		System.out.println(System.lineSeparator() + "	    A-MAZE-ING MAZE GAME" + System.lineSeparator());
		System.out.println("  use WASD to move, R to reset and Q to quit" + System.lineSeparator());
		System.out.println("please enter the maze size (n*n) then press ENTER" + System.lineSeparator());
		
		int n = Integer.parseInt(input.next());
		
		maze = new Maze(n, n);
		maze.mazeGenerator();
		maze.setStart(-1, 0);
		maze.setFinish(n, n-1);
		Player player = new Player(0, 0);
		maze.printMaze(player);
		
		move = input.next().charAt(0);
		while (endGame != true){
			if (move == 'a'){
				Node node = maze.getNode(player.getX(), player.getY());
				
				if (node.getLeft() != null && node.getLeft().getX() >= 0){
					player.setX(player.getX() - 1);
					maze.printMaze(player);
				}
			}
			if (move == 's'){
				if (maze.getNode(player.getX(), player.getY()).getDown() != null){
					player.setY(player.getY() + 1);
					maze.printMaze(player);
				}
				
			}
			if (move == 'd'){
				if (maze.getNode(player.getX(), player.getY()).getRight() != null){
					player.setX(player.getX() + 1);
					maze.printMaze(player);
				}
			}
			if (move == 'w'){
				if (maze.getNode(player.getX(), player.getY()).getUp() != null){
					player.setY(player.getY() - 1);
					maze.printMaze(player);
				}
				
			}
			if (move == 'r'){
				maze = new Maze(n, n);
				maze.mazeGenerator();
				maze.setStart(-1, 0);
				maze.setFinish(n, n-1);
				player = new Player(0, 0);
				maze.printMaze(player);
			}
			if (move == 'q'){
				endGame = true;
				System.out.println("	Quiting. Thanks for playing!");
			}
			if (player.getX() == maze.getFinish().getX() && player.getY() == maze.getFinish().getY()){
				System.out.println("	Congratulations!!! Press any key to play again.");
				move = input.next().charAt(0);
				maze = new Maze(n, n);
				maze.mazeGenerator();
				maze.setStart(-1, 0);
				maze.setFinish(n, n-1);
				player = new Player(0, 0);
				maze.printMaze(player);
			}
			move = input.next().charAt(0);			
		}
		
		if(input != null) {
			input.close();
		}
	}
}
