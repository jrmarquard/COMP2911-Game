import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class AIFighter implements AI {

	private World world;
	private String worldName;
	private String id;
	private LinkedList<Node> explore;
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
        this.explore = new LinkedList<Node>();
        this.visited = new HashMap<Node, Integer>();
        this.attacked = false;
    }
    
	@Override
	public Message makeMove() {
		String[] message = new String[4];
        message[0] = worldName;
        message[2] = id;
        
        Node enemy = null;
    	String enemyName = null;
        
        if(this.id.equals("Moneymaker")) {
    		enemyName = "Teadrinker";
			enemy = this.world.getEntityNode(enemyName);
		} else {
			enemyName = "Moneymaker";
			enemy = this.world.getEntityNode(enemyName);
		}
    	
    	boolean attack = this.attack(enemy, enemyName, message);
    	
        if(!attack) {
        	Node current = this.world.getEntityNode(this.id);
            
        	if(!this.explore.isEmpty()) {
            	Node next = this.explore.remove();
            	putDirectionInMessage(current, next, message);
            } else {
            	boolean playerReached = reachEnemy(current, enemy);
            	boolean keepExploring = false;
            	
            	if(!playerReached) {
            		keepExploring = true;
            		this.explore.clear();
            	}
            	
            	// Cannot reach player/player has not been seen
            	if(keepExploring) {
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
	 * Returns if the AI is going to attack or not
     * and stores either attack or move inside message
	 * @param enemy the enemy node
	 * @param enemyName the name of the enemy 
	 * @param message to store either attack or move
	 * @return if the AI is going to attack or not
	 */
    private boolean attack(Node enemy, String enemyName, String[] message) {
    	Node current = this.world.getEntityNode(this.id);
    	boolean attack = false;
    	
    	for (Entity entity : world.getEntities()) {
    	    if (entity.getName().equals(enemyName) && 
    	    		entity.getMode() != Entity.MODE_DEAD &&
    	    		isEnemyClose(current, enemy) && !this.attacked) {
                message[1] = "melee";
                attack = true;
                this.attacked = true;
                break;
    	    } else {
                message[1] = "move";
                this.attacked = false;
    	    }
    	}
    	
    	return attack;
    }
    
    /**
     * Returns if the enemy is within 2 nodes away from current
     * @param current the current node
     * @param enemy the enemy node
     * @return if the enemy is within 2 nodes away from current
     */
    private boolean isEnemyClose(Node current, Node enemy) {
    	boolean enemyClose = false;
    	int currX = current.getX();
    	int currY = current.getY();
    	int enemyX = enemy.getX();
    	int enemyY = enemy.getY();
    	
		if((enemyX >= currX - 2 && enemyX <= currX + 2 && currY == enemyY) || 
    			(enemyY >= currY - 2 && enemyY <= currY + 2 && currX == enemyX)) {
    		enemyClose = true;
    	}
    	
    	return enemyClose;
    }


    /**
     * Returns if a player is reachable by going straight to one direction, and
     * Adds the straight path to explore
     * @param current the current Node
     * @param player the player Node
     * @return if a player is reachable by going straight to one direction
     */
    private boolean reachEnemy(Node current, Node player) {
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
