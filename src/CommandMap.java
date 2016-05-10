public class CommandMap extends Command {
    
    private int height;
    private int width;
    
    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public CommandMap(Com id, int width, int height) {
        super(id);
        this.width = width;
        this.height = height;
    }    
}