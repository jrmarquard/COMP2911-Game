import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Preferences {
    private Map<String, Color> GUIcolours;
    private Map<String, String> appText;
    private Map<String, Integer> values;
    private Map<String, Boolean> bools;
    
    public Preferences () {
        this.GUIcolours = new HashMap<String,Color>();
        this.appText = new HashMap<String,String>();
        this.values = new HashMap<String,Integer>();
        this.bools = new HashMap<String,Boolean>();
        this.loadPreferences();
    }
    
    public Color getColour (String s) {
        return GUIcolours.get(s);
    }
    public String getText (String s) {
        return appText.get(s);
    }
    public int getValue (String s) {
        return values.get(s);
    }
    public boolean getBool (String s) {
        return bools.get(s);
    }
    public void toggleBool (String s) {
        if (bools.get(s) == true) {
            bools.put(s, false);
        } else {
            bools.put(s, true);
        }
    }
    
    public void setPreference (String s) {
        // Of the format mapName.setting=value
        String[] line = s.split("=");
        String[] setting = line[0].split("\\.");

        switch (setting[0]) {
            case "text":    this.appText.put(setting[1],line[1]); break;
            case "colour":  this.GUIcolours.put(setting[1],new Color(Integer.parseInt(line[1], 16)));   break;
            case "value":   this.values.put(setting[1],Integer.parseInt(line[1])); break;
            case "bool":    this.bools.put(setting[1],Boolean.valueOf(line[1])); break;
        }
    }
    
    public void loadPreferences() {
        Scanner sc = null;
        try {
            sc = new Scanner(new FileReader("pref.properties"));

            for( ; true == sc.hasNextLine() ; ) {
                setPreference(sc.nextLine());
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("Cannot find pref.properties");
            System.out.println(e.getMessage());
        }
        finally {
            if (sc != null) sc.close();
        }
    }
    
    public Set<String> getKeys(String s) {
        switch (s) {
            case "colour": return GUIcolours.keySet();
            case "value": return values.keySet();
            case "text": return values.keySet();
            case "bool": return values.keySet();
            default:
                return null;
        }
    }
    
    
    
    
}
