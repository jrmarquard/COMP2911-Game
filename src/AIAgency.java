import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class AIAgency {

    private Map<String, AIControl> agents;
    private Queue<Command> commands;
    
    public AIAgency (Queue<Command> commands) {
        this.commands = commands;
        this.agents = new HashMap<String, AIControl>();
    }
    
    public void createAI(String name, MazeWorld world) {
        // AIControl ai = new AISolver(world);
        
        // Temporary until other AIs have been created
        AIControl ai = new AI(world);
        
        agents.put(name, ai);
    }
    
    public void makeMove(String s) {
        commands.add(agents.get(s).makeMove());
    }
}
