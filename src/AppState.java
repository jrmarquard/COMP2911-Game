import java.awt.Color;
import java.util.HashMap;
import java.util.Map;


public class AppState {

    private String appName; 
    private Map<String,Color> GUIcolours;
    
    
    // TODO: Load settings from a file
    public AppState (String settingsLocation) {
        this.GUIcolours = new HashMap<String,Color>();
        this.initDefaults();
    }
    
    public Color getColour (String s) {
        return GUIcolours.get(s);
    }
    public String getAppName () {
        return appName;
    }
    
    public void initDefaults() {
        appName = "Maze Runner";
        this.GUIcolours.put("wallColour", Color.black);
        this.GUIcolours.put("tileColour", Color.white);
        this.GUIcolours.put("startColour", Color.green);
        this.GUIcolours.put("finishColour", Color.red);
        this.GUIcolours.put("titleColour", Color.white);
        this.GUIcolours.put("menuColour", Color.gray);
    }
}
