import java.util.Map;


public class AIAgency {

    private Map<String, AIControl> agents;
    
    public AIAgency () {
        
    }
    
    public Command makeMove(String s) {
        agents.get(s).makeMove();
        return null;
    }
}
