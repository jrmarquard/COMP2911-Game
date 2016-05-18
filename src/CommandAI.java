import java.util.ArrayList;

public class CommandAI extends Command{

    MazeWorld world;
    String name;
    ArrayList<AIAgency.AISetting> settings;

    public CommandAI(Com id, String name) {
        super(id);
        this.name = name;
    }
    public CommandAI(Com id, MazeWorld world, String name, ArrayList<AIAgency.AISetting> settings) {
        super(id);
        this.world = world;
        this.name = name;
        this.settings = settings;
    }

    public ArrayList<AIAgency.AISetting> getSettings() {
        return settings;
    }
    public void setSettings(ArrayList<AIAgency.AISetting> settings) {
        this.settings = settings;
    }
    public MazeWorld getWorld() {
        return world;
    }

    public void setWorld(MazeWorld world) {
        this.world = world;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    
        
}
