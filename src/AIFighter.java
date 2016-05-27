import java.util.ArrayList;
import java.util.HashMap;

public class AIFighter implements AI {

	private World world;
	private String worldName;
	private String id;
    private HashMap<Node, Integer> visited;
    private boolean attacked;
	
    /**
     * Constructs an AIEnemy with the given world and ID
     * @param world the world
     * @param id the ID of the maze
     */
    public AIFighter(World world, String id) {
    	this.world = world;
        this.worldName = world.getName();;
        this.id = id;
        this.visited = new HashMap<Node, Integer>();
        this.attacked = false;
    }
    
	@Override
	public Message makeMove() {
		Node current = this.world.getEntityNode(this.id);
		Node enemy = null;
		boolean attack = false;
		
		if(this.id.equals("Moneymaker")) {
			enemy = this.world.getEntityNode("Teadrinker");
		} else {
			enemy = this.world.getEntityNode("Moneymaker");
		}
    	
    	String[] message = new String[4];
        message[0] = worldName;
        message[2] = id;
        
        if(current.isConnected(enemy) && !this.attacked) {
        	message[1] = "melee";
        	attack = true;
        	this.attacked = true;
        } else {
        	message[1] = "move";
        }
        
        if(!attack) {
        	ArrayList<Node> reachable = current.getConnectedNodes();
    		if(this.visited.containsKey(current)) {
    			int currentCost = this.visited.get(current);
    			this.visited.put(current, currentCost += 1);
    		} else {
    			this.visited.put(current, 1);
    		}
            
            Node next = null;
            boolean allVisited = isReachableInVisited(reachable);
            
            for(Node node: reachable) {
            	if(allVisited) {
            		if(next == null) {
            			next = node;
            		} else {
            			int nodeCost, nextCost;
            			
            			if(this.visited.containsKey(node)) {
            				nodeCost = this.visited.get(node);
            			} else {
            				nodeCost = 0;
            			}
            			
            			if(this.visited.containsKey(next)) {
            				nextCost = this.visited.get(next);
            			} else {
            				nextCost = 0;
            			}
            			
            			if(nodeCost < nextCost) {
            				next = node;
            			}
            		}
            	} else if(!visited.containsKey(node)) {
            		next = node;
            	}
            }
            
            putDirectionInMessage(current, next, message);
        }

        return new Message(Message.GAME_MSG, message);
	}


    /**
     * Stores the direction that the AI would like to go in message
     * base on its current location and the node that it is trying to get to
     * @param current the node where the AI is
     * @param next the node where the AI is trying to get to
     * @param message the message to store the direction
     */
	private void putDirectionInMessage(Node current, Node next, String[] message) {
		int currX = current.getX();
        int currY = current.getY();
        int nextX = next.getX();
    	int nextY = next.getY();
    	
    	if(nextX == currX - 1 && nextY == currY) {
    		message[3] = "left";
    	} else if(nextX == currX + 1 && nextY == currY) {
    		message[3] = "right";
    	} else if(nextX == currX && nextY == currY - 1) {
    		message[3] = "up";
    	} else if(nextX == currX && nextY == currY + 1) {
    		message[3] = "down";
    	}
	}
	
	/**
	 * Returns if all the nodes in reachable are in the visited list
	 * @param reachable the list that contains the reachable nodes
	 * @return if all the nodes in reachable are in the visited list
	 */
    private boolean isReachableInVisited(ArrayList<Node> reachable) {
    	boolean isAllIn = true;
    	
    	for(Node node: reachable) {
    		if(!this.visited.containsKey(node)) {
    			isAllIn = false;
    			break;
    		}
    	}
    	
    	return isAllIn;
    }
}