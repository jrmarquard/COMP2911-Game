import java.util.EventObject;


public class Command {
    private Com id;
    private EventObject event; 
    
    public Command (Com id, EventObject event) {
        this.id = id;
        this.event = event;
    }
    public Command (Com id) {
        this.id = id;
        this.event = null;
    }

    public Com getCommandID() {
        return id;
    }

    public void setCommandID(Com Com) {
        this.id = Com;
    }

    public EventObject getEvent() {
        return event;
    }

    public void setEvent(EventObject e) {
        this.event = e;
    }
}
