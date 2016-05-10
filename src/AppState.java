import java.awt.Color;
import java.util.HashMap;
import java.util.Map;


public class AppState {

    private Map<String, Color> GUIcolours;
    private Map<String, String> appText;
    
    
    // TODO: Load settings from a file
    public AppState (String settingsLocation) {
        this.GUIcolours = new HashMap<String,Color>();
        this.appText = new HashMap<String,String>();
        this.initDefaults();
    }
    
    public Color getColour (String s) {
        return GUIcolours.get(s);
    }
    public String getText (String s) {
        return appText.get(s);
    }
    
    public void initDefaults() {
        // Text
        this.appText.put("appName", "Maze Runner");
        this.appText.put("winMessage", "You won!");
        
        // Colours
        this.GUIcolours.put("wallColour", Color.black);
        this.GUIcolours.put("tileDefaultColour", Color.white);
        this.GUIcolours.put("startColour", Color.green);
        this.GUIcolours.put("finishColour", Color.red);
        this.GUIcolours.put("titleDefaultColour", Color.blue);
        this.GUIcolours.put("titleWinColour", Color.green);
        this.GUIcolours.put("menuColour", Color.gray);
        this.GUIcolours.put("playerColour", Color.pink);
    }
}
