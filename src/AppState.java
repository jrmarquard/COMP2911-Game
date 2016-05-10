import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class AppState {

    private Map<String, Color> GUIcolours;
    private Map<String, String> appText;
    
    
    
    // TODO: Load settings from a file
    public AppState () {
        this.GUIcolours = new HashMap<String,Color>();
        this.appText = new HashMap<String,String>();
        this.loadPreferences();
    }
    
    public Color getColour (String s) {
        return GUIcolours.get(s);
    }
    public String getText (String s) {
        return appText.get(s);
    }
    
    public void loadPreferences() {
        Scanner sc = null;
        try {
            sc = new Scanner(new FileReader("settings.properties"));

            for( ; true == sc.hasNextLine() ; ) {
                // Split the command into arguments
                String[] line = sc.nextLine().split("=");
                String[] setting = line[0].split("\\.");
                
                switch (setting[0]) {
                    case "text":    this.appText.put(setting[1],line[1]);   break;
                    case "colour":  this.GUIcolours.put(setting[1],new Color(Integer.parseInt(line[1], 16)));   break;
                }
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("Cannot find settings.properties");
            System.out.println(e.getMessage());
        }
        finally {
            if (sc != null) sc.close();
        }
//        
//        
//        // Text
//        this.appText.put("appName", "Maze Runner");
//        this.appText.put("winMessage", "You won!");
//        
//        // Colours
//        this.GUIcolours.put("wallColour", Color.black);
//        this.GUIcolours.put("tileDefaultColour", Color.white);
//        this.GUIcolours.put("startColour", Color.green);
//        this.GUIcolours.put("finishColour", Color.red);
//        this.GUIcolours.put("titleDefaultColour", Color.blue);
//        this.GUIcolours.put("titleWinColour", Color.green);
//        this.GUIcolours.put("menuColour", Color.gray);
//        this.GUIcolours.put("playerColour", Color.pink);
    }
}
