public class Command {
    private Com id;
    private String[] message;
    
    public Command (Com id) {
        this.id = id;
        this.message = null;
    }

    public Command(Com id, String[] message) {
        this.id = id;
        this.setMessage(message);
    }

    public Com getCommandID() {
        return id;
    }

    public void setCommandID(Com Com) {
        this.id = Com;
    }

    public String[] getMessage() {
        return message;
    }

    public void setMessage(String[] message) {
        this.message = message;
    }
}
