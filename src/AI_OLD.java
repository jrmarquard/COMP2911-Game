import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class AI_OLD implements AI{
	private ArrayList<Node> shortestPath;
	private Game world;
	
	public AI_OLD(Game world) {
	    this.world = world;
        this.shortestPath = new ArrayList<Node>();
    }
	
	private ArrayList<Node> traverseMaze(World world, Node start) {	    
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
				if (neighbour.equals(world.getFinishNode())){
				    return processPath(path, start, world.getFinishNode());
				} else if (!visited.contains(neighbour)){
					q.add(neighbour);
				}
				i++;
			}
		}
		return null;
	}
	
	private ArrayList<Node> processPath(ArrayList<Node> path, Node start, Node dest){
		int i = path.indexOf(dest);
		Node source = path.get(i + 1);
		
		shortestPath.add(0, dest);
		if (source.equals(start)) {
		    shortestPath.add(0, start);
		    return shortestPath;
		} else {
		    return processPath(path, start, source);
		}
	}
	
	public Command makeMove() {
//        Coordinate start = world.getPlayerCoordinate(0);
//        
//        // process path
//        // traverseMaze(world.getMaze(), world.getMaze().getNode(start));
//        
//        Coordinate previous = shortestPath.remove(0).getCoordinate();
//        Coordinate next = shortestPath.get(0).getCoordinate();
//        
//        if (shortestPath.size() == 1) {
//            shortestPath.remove(0);
//        }
//        
//        if (previous.getX() == next.getX()+1) {
//            return new CommandMap(Com.MOVE_LEFT, 1);
//        } else if (previous.getY() == next.getY()+1) {
//            return new CommandMap(Com.MOVE_UP, 1);
//        } else if (previous.getX() == next.getX()-1) {
//            return new CommandMap(Com.MOVE_RIGHT, 1);
//        } else if (previous.getY() == next.getY()-1) {
//            return new CommandMap(Com.MOVE_DOWN, 1);
//        } else {
//            System.out.println("invalid");
//            return new Command(Com.IDLE);
//        }
	    return null;
    }
}
