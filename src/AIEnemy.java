import java.util.LinkedList;

public class AIEnemy implements AI {

	World world;
    String worldName;
    String id;
    private LinkedList<Node> explore;
    private LinkedList<Node> visited;
	
    public AIEnemy(World world, String id) {
    	this.world = world;
        this.worldName = world.getName();;
        this.id = id;
        this.explore = new LinkedList<Node>();
        this.visited = new LinkedList<Node>();
    }
    
	@Override
	public Message makeMove() {
		String[] message = new String[4];
        message[0] = "move";
        message[1] = worldName;
        message[2] = id;
        
        Node current = this.world.getBeingCoordinate(this.id);
        Node player = this.world.getPlayerNode();
        int currX = current.getX();
        int currY = current.getY();
        int playerX = player.getX();
        int playerY = player.getY();
        
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
        	if(currX == playerX) {
        		// Player is below
        		if(currY < playerY) {
        			// Tries to get to where player is
        			while(this.world.getNode(currX, currY).getDown() != null) {
            			this.explore.add(this.world.getNode(currX, currY).getDown());
            			currY++;
            		}
        			
        			// Returns towards the opposite direction
        			while(this.world.getNode(currX, currY).getUp() != null) {
            			this.explore.add(this.world.getNode(currX, currY).getUp());
            			currY--;
            		}
        		}
        		
        		// Player is above
        		else {
        			// Tries to get to where player is
        			while(this.world.getNode(currX, currY).getUp() != null) {
            			this.explore.add(this.world.getNode(currX, currY).getUp());
            			currY--;
            		}
        			
        			// Returns towards the opposite direction
        			while(this.world.getNode(currX, currY).getDown() != null) {
            			this.explore.add(this.world.getNode(currX, currY).getDown());
            			currY++;
            		}
        		}
        	} else if(currY == playerY) {
        		// Player is on the right
        		if(currX < playerX) {
        			// Tries to get to where player is
        			while(this.world.getNode(currX, currY).getRight() != null) {
            			this.explore.add(this.world.getNode(currX, currY).getRight());
            			currX++;
            		}
        			
        			// Returns towards the opposite direction
        			while(this.world.getNode(currX, currY).getLeft() != null) {
            			this.explore.add(this.world.getNode(currX, currY).getLeft());
            			currX--;
            		}
        		}
        		
        		// Player is on the left
        		else {
        			// Tries to get to where player is
        			while(this.world.getNode(currX, currY).getLeft() != null) {
            			this.explore.add(this.world.getNode(currX, currY).getLeft());
            			currX--;
            		}
        			
        			// Returns towards the opposite direction
        			while(this.world.getNode(currX, currY).getRight() != null) {
            			this.explore.add(this.world.getNode(currX, currY).getRight());
            			currX++;
            		}
        		}
        	} else {
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
        	}
        }

        return new Message(Message.GAME_MSG, message);
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
