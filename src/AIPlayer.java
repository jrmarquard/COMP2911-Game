
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

/**
 * AI to solve a maze with easy/medium/hard difficulty
 * 
 * @author John
 */
public class AIPlayer implements AI {

	private World world;
	private String worldName;
	private String id;
	private String diff;
    private LinkedList<Node> explore;
    private HashMap<Node, Integer> visited;
    
    public AIPlayer(World world, String id, String diff) {
        this.world = world;
        this.worldName = world.getName();;
        this.id = id;
        this.diff = diff;
        this.explore = new LinkedList<Node>();
        this.visited = new HashMap<Node, Integer>();
    }
    
    @Override
    /**
	 * Returns a message which contains the move that
	 * the AI would like to make
	 */
    public Message makeMove() {
        switch (diff) {
            case "easy": return easyMove();
            case "med": return medMove();
            case "hard": return hardMove();
            default:
                return null;
        }
    }

    /**
     * Easiest setting on the AI.
     * Makes a completely random move, sometimes stays still
     * @return a message which contains the move that the AI would like to make
     */
    private Message easyMove() {
    	String[] message = new String[4];
        message[0] = worldName;
        message[2] = id;
    	
    	boolean attack = this.attack(message);
    	
        if(!attack) {
        	int randValue = (new Random()).nextInt(4);
            switch(randValue) {
                case 0:     message[3] = "up";      break;
                case 1:     message[3] = "down";    break;
                case 2:     message[3] = "left";    break;
                case 3:     message[3] = "right";   break;
                default:    message[3] = "";        break;
            }
        }
        
        return new Message(Message.GAME_MSG, message);
    }

    /**
     * Medium Difficulty
     * Picks a random corridor and goes through it until it reaches the end of it
     * @return a message which contains the move that the AI would like to make
     */
    private Message medMove() {
    	String[] message = new String[4];
        message[0] = worldName;
        message[2] = id;
    	
    	boolean attack = this.attack(message);
    	
        if(!attack) {
        	Node current = this.world.getEntityNode(this.id);
        	this.visited.put(current, 0);
            int currX = current.getX();
            int currY = current.getY();
            
            if(!this.explore.isEmpty()) {
            	Node next = this.explore.remove();
            	putDirectionInMessage(current, next, message);
            } else {
            	boolean deadEnd = current.isDeadEnd();

            	if(deadEnd) {
            		boolean addedToExplore = false;
            		this.visited.clear();

            		while(this.world.getNode(currX, currY).getUp() != null) {
            			this.explore.add(this.world.getNode(currX, currY).getUp());
            			currY--;
            			addedToExplore = true;
            		}
            		
            		if(!addedToExplore) {
            			while(this.world.getNode(currX, currY).getDown() != null) {
                			this.explore.add(this.world.getNode(currX, currY).getDown());
                			currY++;
                			addedToExplore = true;
                		}
            		}
            		
            		if(!addedToExplore) {
            			while(this.world.getNode(currX, currY).getLeft() != null) {
                			this.explore.add(this.world.getNode(currX, currY).getLeft());
                			currX--;
                			addedToExplore = true;
                		}
            		}
            		
            		if(!addedToExplore) {
            			while(this.world.getNode(currX, currY).getRight() != null) {
                			this.explore.add(this.world.getNode(currX, currY).getRight());
                			currX++;
                			addedToExplore = true;
                		}
            		}
            	} else {
            		int randValue = (new Random()).nextInt(4);
            		
            		if(randValue == 0) {
            			if(!this.visited.containsKey(this.world.getNode(currX, currY).getUp())) {
                			while(this.world.getNode(currX, currY).getUp() != null) {
                    			this.explore.add(this.world.getNode(currX, currY).getUp());
                    			currY--;
                    		}
                		}
            		} else if(randValue == 1) {
            			if(!this.visited.containsKey(this.world.getNode(currX, currY).getDown())) {
                			while(this.world.getNode(currX, currY).getDown() != null) {
                    			this.explore.add(this.world.getNode(currX, currY).getDown());
                    			currY++;
                    		}
            			}
            		} else if(randValue == 2) {
            			if(!this.visited.containsKey(this.world.getNode(currX, currY).getLeft())) {
                			while(this.world.getNode(currX, currY).getLeft() != null) {
                    			this.explore.add(this.world.getNode(currX, currY).getLeft());
                    			currX--;
                    		}
            			}
            		} else if(randValue == 3) {
            			if(!this.visited.containsKey(this.world.getNode(currX, currY).getRight())) {
                			while(this.world.getNode(currX, currY).getRight() != null) {
                    			this.explore.add(this.world.getNode(currX, currY).getRight());
                    			currX++;
                    		}
                		}
            		}
            	}
            	
            	message[3] = "";
            }
        }
        
        return new Message(Message.GAME_MSG, message);
    }

    /**
     * Hard difficulty
     * Looks for the neighbour nodes each time and pick one depending on their
     * visited cost which will be increased by one each time that node is 
     * being visited
     * @return a message which contains the move that the AI would like to make
     */
    private Message hardMove() {
    	String[] message = new String[4];
        message[0] = worldName;
        message[2] = id;
    	
    	boolean attack = this.attack(message);
    	
        if(!attack) {
        	Node current = this.world.getEntityNode(this.id);
        	
        	if(this.visited.containsKey(current)) {
    			int currentCost = this.visited.get(current);
    			this.visited.put(current, currentCost += 1);
    		} else {
    			this.visited.put(current, 1);
    		}
    		
        	Node next = nextExploreNode(current);
            putDirectionInMessage(current, next, message);
        }
        
        return new Message(Message.GAME_MSG, message);
    }
    
    /**
     * Returns if the AI is going to attack or not
     * and stores either attack or move inside message
     * @param message to store either attack or move
     * @return if the AI is going to attack or not
     */
    private boolean attack(String[] message) {
    	Node current = this.world.getEntityNode(this.id);
    	boolean attack = false;
    	
    	for (Entity entity : world.getEntities()) {
    	    if (entity.getType() == Entity.ENEMY && 
    	    		entity.getMode() != Entity.MODE_DEAD &&
    	    				isEnemyClose(current, entity.getNode())) {
                message[1] = "melee";
                attack = true;
                break;
    	    } else {
                message[1] = "move";
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
}
