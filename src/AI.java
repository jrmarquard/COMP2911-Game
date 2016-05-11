import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class AI {

    private Queue<Command> commands;
	private ArrayList<Node> shortestPath;
	
	public AI() {
        this.shortestPath = new ArrayList<Node>();
    }
	public AI(Queue<Command> commands) {
		this.shortestPath = new ArrayList<Node>();
		this.commands = commands;
	}
	
	public ArrayList<Node> getShortestPath(){
		return this.shortestPath;
	}
	
	public ArrayList<Node> traverseMaze(Maze maze, Node start){
		
		ArrayList<Node> path = new ArrayList<Node>();
		
		Queue<Node> q = new LinkedList<Node>();
		Queue<Node> visited = new LinkedList<Node>();
		
		q.add(start);
		while (!q.isEmpty()){
			Node n = q.remove();
			visited.add(n);
			
			ArrayList<Node> reachable = new ArrayList<Node>();
			if (n.getLeft() != null) reachable.add(n.getLeft());
			if (n.getDown() != null) reachable.add(n.getDown());
			if (n.getRight() != null) reachable.add(n.getRight());
			if (n.getUp() != null) reachable.add(n.getUp());
			
			int i = 0;
			while(i != reachable.size()){
				Node neighbour = reachable.get(i);
				path.add(neighbour);
				path.add(n);
				if (neighbour.equals(maze.getFinish())){
					return processPath(maze, path, start, maze.getFinish());
	
				} else if (!visited.contains(neighbour)){
					q.add(neighbour);
				}
				i++;
			}
		}
		return null;
	}
	
	private ArrayList<Node> processPath(Maze maze, ArrayList<Node> path, Node start, Node dest){
		int i = path.indexOf(dest);
		Node source = path.get(i + 1);
		
		shortestPath.add(0, dest);
		
		if (source.equals(start)) {
			shortestPath.add(0, start);
			return shortestPath;
		} else {
			return processPath(maze, path, start, source);
		}
	}
	
	public void makeMove() {
        Node previous = shortestPath.remove(0);
        Node next = shortestPath.get(0);

        if (previous.isLeft(next)) {
            if (shortestPath.size() == 1) shortestPath.remove(0);
            commands.add(new Command(Com.MOVE_LEFT));
        } else if (previous.isUp(next)) {
            if (shortestPath.size() == 1) shortestPath.remove(0);
            commands.add(new Command(Com.MOVE_UP));
        } else if (previous.isRight(next)) {
            if (shortestPath.size() == 1) shortestPath.remove(0);
            commands.add(new Command(Com.MOVE_RIGHT));
        } else if (previous.isDown(next)) {
            if (shortestPath.size() == 1) shortestPath.remove(0);
            commands.add(new Command(Com.MOVE_DOWN));
        } else {
            System.out.println("invalid");
        }
    }
	
	public boolean isFinished() {
	    return shortestPath.isEmpty();
	}
}
