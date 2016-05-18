import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class AIAgency {
    
    enum AISetting {
        // Difficulty settings for AI
        EASY, 
        MEDIUM, 
        HARD,
        
        // An AI that moves randomly
        RANDOM,
        
        // An AI that solves the maze
        MAZE_SOLVE,
        
        // An AI that chases the player
        PLAYER_CHASE
    }

    private Map<String, AIControl> agents;
    private Queue<Command> commands;
    
    public AIAgency (Queue<Command> commands) {
        this.commands = commands;
        this.agents = new HashMap<String, AIControl>();
    }
    
    public void createAI(String name, MazeWorld world, ArrayList<AISetting> settings) {
        // Default AI
        AIControl ai = new AI(world);
        if (settings.contains(AISetting.MAZE_SOLVE)) {
            if (settings.contains(AISetting.EASY)) {
                ai = new AI(world);
            } else if (settings.contains(AISetting.MEDIUM)) {
                ai = new AI(world);
            } else if (settings.contains(AISetting.HARD)) {
                ai = new AI(world);
            }
        }
        agents.put(name, ai);
    }
    
    public void makeMove(String s) {
        commands.add(agents.get(s).makeMove());
    }
}
