import java.util.LinkedList;
import java.util.Random;

/**
 * AI to solve a maze with easy/medium/hard difficulty
 * 
 * @author John
 */
public class AIPlayer implements AI {

    World world;
    String worldName;
    String id;
    String diff;
    private LinkedList<Node> explore;
    private LinkedList<Node> visited;
    
    public AIPlayer(World world, String id, String diff) {
        this.world = world;
        this.worldName = world.getName();;
        this.id = id;
        this.diff = diff;
        this.explore = new LinkedList<Node>();
        this.visited = new LinkedList<Node>();
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
        message[0] = worldName;
        message[1] = "move";
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
        message[0] = worldName;
        message[1] = "move";
        message[2] = id;
        
        Node current = this.world.getBeingNode(this.id);
        this.visited.add(current);
        int currX = current.getX();
        int currY = current.getY();
        
        if(!this.explore.isEmpty()) {
        	Node next = this.explore.remove();
        	this.visited.add(next);
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
        	boolean deadEnd = isAtDeadEnd(current);

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
        			if(!this.visited.contains(this.world.getNode(currX, currY).getUp())) {
            			while(this.world.getNode(currX, currY).getUp() != null) {
                			this.explore.add(this.world.getNode(currX, currY).getUp());
                			currY--;
                		}
            		}
        		} else if(randValue == 1) {
        			if(!this.visited.contains(this.world.getNode(currX, currY).getDown())) {
            			while(this.world.getNode(currX, currY).getDown() != null) {
                			this.explore.add(this.world.getNode(currX, currY).getDown());
                			currY++;
                		}
        			}
        		} else if(randValue == 2) {
        			if(!this.visited.contains(this.world.getNode(currX, currY).getLeft())) {
            			while(this.world.getNode(currX, currY).getLeft() != null) {
                			this.explore.add(this.world.getNode(currX, currY).getLeft());
                			currX--;
                		}
        			}
        		} else if(randValue == 3) {
        			if(!this.visited.contains(this.world.getNode(currX, currY).getRight())) {
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
        message[0] = worldName;
        message[1] = "move";
        message[2] = id;
        
        Node current = this.world.getBeingNode(this.id);
        int currX = current.getX();
        int currY = current.getY();
        LinkedList<Node> reachable = this.getReachable(current);
        current.addVisitCost(1);
        this.visited.add(current);
        
        Node next = null;
        boolean first = true;
        boolean allVisited = isReachableInVisited(reachable);
        
        for(Node node: reachable) {
        	if(allVisited) {
        		if(first) {
        			next = node;
        			first = false;
        		} else {
        			if(node.getVisitCost() < next.getVisitCost()) {
        				next = node;
        			}
        		}
        	} else if(!visited.contains(node)) {
        		next = node;
        	}
        }
        
        next.addVisitCost(1);
        this.visited.add(next);
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
    
    private boolean isAtDeadEnd(Node node) {
    	boolean deadEnd = false;
    	int x = node.getX();
    	int y = node.getY();
    	int count = 0;
    	
    	if(this.world.getNode(x, y).getUp() == null) {
    		count++;
    	}
    	
    	if(this.world.getNode(x, y).getDown() == null) {
    		count++;
    	}
    	
    	if(this.world.getNode(x, y).getLeft() == null) {
    		count++;
    	}
    	
    	if(this.world.getNode(x, y).getRight() == null) {
    		count++;
    	}
    	
    	if(count == 3) {
    		deadEnd = true;
    	}
    	
    	return deadEnd;
    }
    
    private LinkedList<Node> getReachable(Node node) {
    	LinkedList<Node> reachable = new LinkedList<Node>();
    	int x = node.getX();
    	int y = node.getY();
    	
    	if(this.world.getNode(x, y).getUp() != null) {
    		reachable.add(this.world.getNode(x, y).getUp());
    	}
    	
    	if(this.world.getNode(x, y).getDown() != null) {
    		reachable.add(this.world.getNode(x, y).getDown());
    	}
    	
    	if(this.world.getNode(x, y).getLeft() != null) {
    		reachable.add(this.world.getNode(x, y).getLeft());
    	}
    	
    	if(this.world.getNode(x, y).getRight() != null) {
    		reachable.add(this.world.getNode(x, y).getRight());
    	}
    	
    	return reachable;
    }
    
    private boolean isReachableInVisited(LinkedList<Node> reachable) {
    	boolean isAllIn = true;
    	
    	for(Node node: reachable) {
    		if(!this.visited.contains(node)) {
    			isAllIn = false;
    			break;
    		}
    	}
    	
    	return isAllIn;
    }
}
