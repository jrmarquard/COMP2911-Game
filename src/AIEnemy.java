import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;

/**
 * An enemy AI that will explore the maze and try and reach the player
 */
public class AIEnemy implements AI {

	private World world;
	private String worldName;
	private String id;
    private LinkedList<Node> explore;
    private HashMap<Node, Integer> visited;
	
    /**
     * Constructs an AIEnemy with the given world and ID
     * @param world the world
     * @param id the ID of the maze
     */
    public AIEnemy(World world, String id) {
    	this.world = world;
        this.worldName = world.getName();;
        this.id = id;
        this.explore = new LinkedList<Node>();
        this.visited = new HashMap<Node, Integer>();
    }
    
    @Override
	/**
	 * Returns a message which contains the name of the world, 
	 * a String to tell if it wants to move or attack, its ID, and 
	 * the move that the AI would like to make
	 */
	public Message makeMove() {
		String[] message = new String[4];
        message[0] = worldName;
        message[1] = "move";
        message[2] = id;
        
        Node current = this.world.getEntityNode(this.id);
        Node player = this.world.getEntityNode("Moneymaker");
        
        if(current.equals(player)) {
        	message[3] = "";
        } else {
        	if(!this.explore.isEmpty()) {
            	Node next = this.explore.remove();
            	putDirectionInMessage(current, next, message);
            } else {
            	boolean playerReached = reachPlayer(current, player);
            	boolean keepExploring = false;
            	
            	if(!playerReached) {
            		keepExploring = true;
            		this.explore.clear();
            	}
            	
            	// Cannot reach player/player has not been seen
            	if(keepExploring) {
            		if(this.visited.containsKey(current)) {
            			int currentCost = this.visited.get(current);
            			this.visited.put(current, currentCost += 1);
            		} else {
            			this.visited.put(current, 1);
            		}
            		
            		Node next = nextExploreNode(current);
                    putDirectionInMessage(current, next, message);
            	} else {
            		message[3] = "";
            	}
            }
        }

        return new Message(Message.GAME_MSG, message);
	}
    
    /**
     * Returns if a player is reachable by going straight to one direction, and
     * Adds the straight path to explore
     * @param current the current Node
     * @param player the player Node
     * @return if a player is reachable by going straight to one direction
     */
    private boolean reachPlayer(Node current, Node player) {
    	int currX = current.getX();
        int currY = current.getY();
        int playerX = player.getX();
        int playerY = player.getY();
    	/*
    	 * A boolean to tell when the AI is in the same row/column
    	 * with the player, the AI will try to get to where the player is
    	 * with that row/column. If the AI doesn't reach the player,
    	 * this boolean will be false, ie there is a wall between
    	 * the AI and the player
    	 */
    	boolean playerReached = false;
    	
    	if(currX == playerX) {
    		// Player is below
    		if(currY < playerY) {
    			// Tries to get to where player is
    			while(this.world.getNode(currX, currY).getDown() != null) {
    				if(this.world.getNode(currX, currY).getDown().equals(player)) {
    					playerReached = true;
    				}
    				
        			this.explore.add(this.world.getNode(currX, currY).getDown());
        			currY++;
        		}
    		}
    		
    		// Player is above
    		else {
    			// Tries to get to where player is
    			while(this.world.getNode(currX, currY).getUp() != null) {
    				if(this.world.getNode(currX, currY).getUp().equals(player)) {
    					playerReached = true;
    				}
    				
        			this.explore.add(this.world.getNode(currX, currY).getUp());
        			currY--;
        		}
    		}
    	} else if(currY == playerY) {
    		// Player is on the right
    		if(currX < playerX) {
    			// Tries to get to where player is
    			while(this.world.getNode(currX, currY).getRight() != null) {
    				if(this.world.getNode(currX, currY).getRight().equals(player)) {
    					playerReached = true;
    				}
    				
        			this.explore.add(this.world.getNode(currX, currY).getRight());
        			currX++;
        		}
    		}
    		
    		// Player is on the left
    		else {
    			// Tries to get to where player is
    			while(this.world.getNode(currX, currY).getLeft() != null) {
    				if(this.world.getNode(currX, currY).getLeft().equals(player)) {
    					playerReached = true;
    				}
    				
        			this.explore.add(this.world.getNode(currX, currY).getLeft());
        			currX--;
        		}
    		}
    	}
    	
    	return playerReached;
    }
    
    /**
     * Returns the next node that will be visited
     * @param current the current Node
     * @return the next node that will be visited
     */
    private Node nextExploreNode(Node current) {
    	ArrayList<Node> reachable = current.getConnectedNodes();
    	Node next = null;

    	for(Node node: reachable) {
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
    	}
    	
    	LinkedList<Node> sameCostNodes = this.getSameCostNodes(reachable);
    	
    	if(sameCostNodes.size() > 1) {
    		Random rand = new Random();
    		int randNum = rand.nextInt(sameCostNodes.size());
    		next = sameCostNodes.get(randNum);
    	}
        
        return next;
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
	 * Returns a list of nodes with the same visited cost
	 * @param reachable the list of nodes to scan through
	 * @return a list of nodes with the same visited cost
	 */
	private LinkedList<Node> getSameCostNodes(ArrayList<Node> reachable) {
		LinkedList<Node> sameCostNodes = new LinkedList<Node>();
		ArrayList<Integer> costList = new ArrayList<Integer>();
		Set<Integer> duplicateCost = new HashSet<Integer>();
		Set<Integer> tempSet = new HashSet<Integer>();
		
		// Extracts a list of costs
		for(Node node: reachable) {
			if(this.visited.containsKey(node)) {
				costList.add(this.visited.get(node));
			} else {
				costList.add(0);
			}
		}
 
		// Finds a set of duplicate costs
		for (Integer yourInt : costList) {
			if (!tempSet.add(yourInt)) {
				duplicateCost.add(yourInt);
			}
		}
		
		if(duplicateCost.size() > 0) {
			int cost = 0;
			
			// Retrieves the duplicate cost
			for(int tempInt: duplicateCost) {
				cost = tempInt;
				break;
			}
			
			// Adds the nodes that are associated with the duplicate cost to the list
			for(Node node: reachable) {
				int nodeCost;
				
				if(this.visited.containsKey(node)) {
					nodeCost = this.visited.get(node);
				} else {
					nodeCost = 0;
				}
				
				if(nodeCost == cost) {
					sameCostNodes.add(node);
				}
			}
		}
		
		return sameCostNodes;
	}
}
