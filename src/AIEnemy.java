import java.util.LinkedList;
import java.util.Random;

public class AIEnemy implements AI {

	World world;
    String worldName;
    String id;
    private LinkedList<Node> explore;
	
    public AIEnemy(World world, String id) {
    	this.world = world;
        this.worldName = world.getName();;
        this.id = id;
        this.explore = new LinkedList<Node>();
    }
    
	@Override
	public Command makeMove() {
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
        			while(this.world.getNode(currX, currY).getDown() != null) {
            			this.explore.add(this.world.getNode(currX, currY).getDown());
            			currY++;
            		}
        		}
        		
        		// Player is above
        		else {
        			while(this.world.getNode(currX, currY).getUp() != null) {
            			this.explore.add(this.world.getNode(currX, currY).getUp());
            			currY--;
            		}
        		}
        	} else if(currY == playerY) {
        		// Player is on the right
        		if(currX < playerX) {
        			while(this.world.getNode(currX, currY).getRight() != null) {
            			this.explore.add(this.world.getNode(currX, currY).getRight());
            			currX++;
            		}
        		}
        		
        		// Player is on the left
        		else {
        			while(this.world.getNode(currX, currY).getLeft() != null) {
            			this.explore.add(this.world.getNode(currX, currY).getLeft());
            			currX--;
            		}
        		}
        	} else {
        		int randValue = (new Random()).nextInt(4);
                switch(randValue) {
                    case 0:     message[3] = "up";      break;
                    case 1:     message[3] = "down";    break;
                    case 2:     message[3] = "left";    break;
                    case 3:     message[3] = "right";   break;
                    default:    message[3] = "";        break;
                }
        	}
        }

        return new Command(Com.GAME_MSG, message);
	}

}
