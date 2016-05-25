import java.util.ArrayList;
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
        	boolean playerReached = false;
        	boolean goRandom = false;
        	boolean playerAtDeadEnd = isAtDeadEnd(player);
        	
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
        			
        			if(playerReached && !playerAtDeadEnd) {
        				// Returns towards the opposite direction
            			while(this.world.getNode(currX, currY).getUp() != null) {
                			this.explore.add(this.world.getNode(currX, currY).getUp());
                			currY--;
                		}
        			} else {
        				this.explore.clear();
        				goRandom = true;
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
        			
        			if(playerReached && !playerAtDeadEnd) {
        				// Returns towards the opposite direction
            			while(this.world.getNode(currX, currY).getDown() != null) {
                			this.explore.add(this.world.getNode(currX, currY).getDown());
                			currY++;
                		}
        			} else {
        				this.explore.clear();
        				goRandom = true;
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
        			
        			if(playerReached && !playerAtDeadEnd) {
        				// Returns towards the opposite direction
            			while(this.world.getNode(currX, currY).getLeft() != null) {
                			this.explore.add(this.world.getNode(currX, currY).getLeft());
                			currX--;
                		}
        			} else {
        				this.explore.clear();
        				goRandom = true;
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
        			
        			if(playerReached && !playerAtDeadEnd) {
        				// Returns towards the opposite direction
            			while(this.world.getNode(currX, currY).getRight() != null) {
                			this.explore.add(this.world.getNode(currX, currY).getRight());
                			currX++;
                		}
        			} else {
        				this.explore.clear();
        				goRandom = true;
        			}
        		}
        	} else {
        		goRandom = true;
        	}
        	
        	if(goRandom) {
        		if(playerAtDeadEnd) {
        			player.addVisitCost(3);
        			
        			for(Node node: player.getConnectedNodes()) {
        				node.addVisitCost(3);
        			}
        		}
        		
        		ArrayList<Node> reachable = current.getConnectedNodes();
                current.addVisitCost(1);
                this.visited.add(current);
                
                Node next = null;
                boolean first = true;
                boolean allVisited = isReachableInVisited(reachable);
                
                for(Node node: reachable) {
                	if(allVisited || playerAtDeadEnd) {
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
                currX = current.getX();
                currY = current.getY();
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
    
    private boolean isReachableInVisited(ArrayList<Node> reachable) {
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
