import java.util.EventObject;


public class Command {
    public enum commandID {
        NEW_MAP, DRAW, EXIT, KEYSTROKE
    }
    
    
    private commandID id;
    private EventObject event; 
    
    public Command (commandID id, EventObject event) {
        this.id = id;
        this.event = event;
    }
    public Command (commandID id) {
        this.id = id;
        this.event = null;
    }

    public commandID getCommandID() {
        return id;
    }

    public void setCommandID(commandID commandID) {
        this.id = commandID;
    }

    public EventObject getEvent() {
        return event;
    }

    public void setEvent(EventObject e) {
        this.event = e;
    }
    
    
}
