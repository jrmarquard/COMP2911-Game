import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class AIEnemy implements AI {

	World world;
    String worldName;
    String id;
    private LinkedList<Node> explore;
    private HashMap<Node, Integer> visited;
	
    public AIEnemy(World world, String id) {
    	this.world = world;
        this.worldName = world.getName();;
        this.id = id;
        this.explore = new LinkedList<Node>();
        this.visited = new HashMap<Node, Integer>();
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
        
        if(currX == playerX && currY == playerY) {
        	message[3] = "";
        } else {
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
            	boolean playerAtDeadEnd = player.isDeadEnd();
            	
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
            			
            			if(!playerReached || playerAtDeadEnd) {
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
            			
            			if(!playerReached || playerAtDeadEnd) {
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
            			
            			if(!playerReached || playerAtDeadEnd) {
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
            			
            			if(!playerReached || playerAtDeadEnd) {
            				this.explore.clear();
            				goRandom = true;
            			}
            		}
            	} else {
            		goRandom = true;
            	}
            	
            	if(goRandom) {
            		if(playerAtDeadEnd) {
            			if(this.visited.containsKey(player)) {
            				int playerCost = this.visited.get(player);
            				this.visited.put(player, playerCost += 1);
            			} else {
            				this.visited.put(player, 1);
            			}
            			
            			for(Node node: player.getConnectedNodes()) {
            				if(this.visited.containsKey(node)) {
                				int nodeCost = this.visited.get(node);
                				this.visited.put(node, nodeCost += 1);
                			} else {
                				this.visited.put(node, 1);
                			}
            			}
            		}
            		
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
                    	if(allVisited || playerAtDeadEnd) {
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
            	} else {
            		message[3] = "";
            	}
            }
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
