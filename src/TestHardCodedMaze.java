
public class TestHardCodedMaze {

	public static void main(String[] args) {
		Maze maze1 = new Maze(5, 6);
		
		maze1.setStart(-1, 1);
		maze1.setFinish(5, 4);
		maze1.makePath(0, 0, 1, 0);
		maze1.makePath(2, 0, 3, 0);
		maze1.makePath(3, 0, 4, 0);
		maze1.makePath(0, 0, 0, 1);
		maze1.makePath(2, 0, 2, 1);
		maze1.makePath(4, 0, 4, 1);
		maze1.makePath(1, 1, 2, 1);
		maze1.makePath(0, 1, 0, 2);
		maze1.makePath(1, 1, 1, 2);
		maze1.makePath(3, 1, 3, 2);
		maze1.makePath(4, 1, 4, 2);
		maze1.makePath(1, 2, 2, 2);
		maze1.makePath(2, 2, 3, 2);
		maze1.makePath(0, 2, 0, 3);
		maze1.makePath(3, 2, 3, 3);
		maze1.makePath(4, 2, 4, 3);
		maze1.makePath(0, 3, 1, 3);
		maze1.makePath(1, 3, 2, 3);
		maze1.makePath(0, 3, 0, 4);
		maze1.makePath(3, 3, 3, 4);
		maze1.makePath(4, 3, 4, 4);
		maze1.makePath(1, 4, 2, 4);
		maze1.makePath(2, 4, 3, 4);
		maze1.makePath(0, 4, 0, 5);
		maze1.makePath(1, 4, 1, 5);
		maze1.makePath(0, 5, 1, 5);
		maze1.makePath(1, 5, 2, 5);
		maze1.makePath(2, 5, 3, 5);
		maze1.makePath(3, 5, 4, 5);
		
		maze1.printMaze();
		
		System.out.print("\n");
		
		maze1 = new Maze(8, 8);
		
		maze1.setStart(-1, 6);
		maze1.setFinish(6,  -1);
		
		maze1.makePath(0, 0, 1, 0);
		maze1.makePath(2, 0, 3, 0);
		maze1.makePath(4, 0, 5, 0);
		maze1.makePath(6, 0, 7, 0);
		maze1.makePath(0, 0, 0, 1);
		maze1.makePath(1, 0, 1, 1);
		maze1.makePath(2, 0, 2, 1);
		maze1.makePath(3, 0, 3, 1);
		maze1.makePath(4, 0, 4, 1);
		maze1.makePath(7, 0, 7, 1);
		maze1.makePath(1, 1, 2, 1);
		maze1.makePath(3, 1, 4, 1);
		maze1.makePath(0, 1, 0, 2);
		maze1.makePath(5, 1, 5, 2);
		maze1.makePath(6, 1, 6, 2);
		maze1.makePath(7, 1, 7, 2);
		maze1.makePath(0, 2, 1, 2);
		maze1.makePath(1, 2, 2, 2);
		maze1.makePath(3, 2, 4, 2);
		maze1.makePath(4, 2, 5, 2);
		maze1.makePath(5, 2, 6, 2);
		maze1.makePath(0, 2, 0, 3);
		maze1.makePath(3, 2, 3, 3);
		maze1.makePath(5, 2, 5, 3);
		maze1.makePath(7, 2, 7, 3);
		maze1.makePath(1, 3, 2, 3);
		maze1.makePath(2, 3, 3, 3);
		maze1.makePath(3, 3, 4, 3);
		maze1.makePath(0, 3, 0, 4);
		maze1.makePath(1, 3, 1, 4);
		maze1.makePath(5, 3, 5, 4);
		maze1.makePath(6, 3, 6, 4);
		maze1.makePath(7, 3, 7, 4);
		maze1.makePath(2, 4, 3, 4);
		maze1.makePath(3, 4, 4, 4);
		maze1.makePath(4, 4, 5, 4);
		maze1.makePath(5, 4, 6, 4);
		maze1.makePath(0, 4, 0, 5);
		maze1.makePath(1, 4, 1, 5);
		maze1.makePath(6, 4, 6, 5);
		maze1.makePath(7, 4, 7, 5);
		maze1.makePath(1, 5, 2, 5);
		maze1.makePath(2, 5, 3, 5);
		maze1.makePath(4, 5, 5, 5);
		maze1.makePath(5, 5, 6, 5);
		maze1.makePath(0, 5, 0, 6);
		maze1.makePath(1, 5, 1, 6);
		maze1.makePath(3, 5, 3, 6);
		maze1.makePath(4, 5, 4, 6);
		maze1.makePath(7, 5, 7, 6);
		maze1.makePath(1, 6, 2, 6);
		maze1.makePath(5, 6, 6, 6);
		maze1.makePath(0, 6, 0, 7);
		maze1.makePath(3, 6, 3, 7);
		maze1.makePath(4, 6, 4, 7);
		maze1.makePath(5, 6, 5, 7);
		maze1.makePath(6, 6, 6, 7);
		maze1.makePath(7, 6, 7, 7);
		maze1.makePath(0, 7, 1, 7);
		maze1.makePath(1, 7, 2, 7);
		maze1.makePath(2, 7, 3, 7);
		maze1.makePath(4, 7, 5, 7);
		maze1.makePath(6, 7, 7, 7);
		
		maze1.printMaze();
	}

}
