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
    
    public AISolve(World world, String id, String diff) {
        this.world = world;
        this.worldName = world.getName();;
        this.id = id;
        this.diff = diff;
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
        
        int randValue = (new Random()).nextInt(5);
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
        return easyMove();
    }

    /**
     * Hard difficulty
     * @return
     */
    private Message hardMove() {
        return easyMove();
    }
}
