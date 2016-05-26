import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

/**
 * AI to solve a maze with easy/medium/hard difficulty
 * 
 * @author John
 */
public class AISolve implements AI {

    World world;
    String worldName;
    String id;
    String diff;
    private LinkedList<Node> explore;
    private HashMap<Node, Integer> visited;
    
    public AISolve(World world, String id, String diff) {
        this.world = world;
        this.worldName = world.getName();;
        this.id = id;
        this.diff = diff;
        this.explore = new LinkedList<Node>();
        this.visited = new HashMap<Node, Integer>();
    }
    
    @Override
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
     * @return
     */
    private Message easyMove() {
        String[] message = new String[4];
        message[0] = "move";
        message[1] = worldName;
        message[2] = id;
        
        int randValue = (new Random()).nextInt(4);
        switch(randValue) {
            case 0:     message[3] = "up";      break;
            case 1:     message[3] = "down";    break;
            case 2:     message[3] = "left";    break;
            case 3:     message[3] = "right";   break;
            default:    message[3] = "";        break;
        }
        
        return new Message(Message.GAME_MSG, message);
    }

    /**
     * Medium Difficulty
     * @return
     */
    private Message medMove() {
    	String[] message = new String[4];
        message[0] = "move";
        message[1] = worldName;
        message[2] = id;
        
        Node current = this.world.getBeingCoordinate(this.id);
        this.visited.put(current, 0);
        int currX = current.getX();
        int currY = current.getY();
        
        if(!this.explore.isEmpty()) {
        	Node next = this.explore.remove();
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
        
        return new Message(Message.GAME_MSG, message);
    }

    /**
     * Hard difficulty
     * @return
     */
    private Message hardMove() {
    	String[] message = new String[4];
        message[0] = "move";
        message[1] = worldName;
        message[2] = id;
        
        Node current = this.world.getBeingCoordinate(this.id);
        int currX = current.getX();
        int currY = current.getY();
        
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
    	} else {
    		message[3] = "";
    	}
        
        return new Message(Message.GAME_MSG, message);
    }
    
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
