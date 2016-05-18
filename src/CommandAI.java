
public class CommandAI extends Command{

    MazeWorld world;
    String name;

    public CommandAI(Com id, String name) {
        super(id);
        this.name = name;
    }
    public CommandAI(Com id, MazeWorld world, String name) {
        super(id);
        this.world = world;
        this.name = name;
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
