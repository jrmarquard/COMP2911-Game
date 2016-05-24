public class Message {
    /**
     * 
     */
    public static final int EXIT = 0;
    public static final int GAME_MSG = 1;
    public static final int SOUND_MSG = 2;
    
    private int id;
    private String[] message;
    
    public Message (int id) {
        this.id = id;
        this.message = null;
    }

    public Message(int id, String[] message) {
        this.id = id;
        this.setMessage(message);
    }

    public int getCommandID() {
        return id;
    }

    public void setCommandID(int Com) {
        this.id = Com;
    }

    public String[] getMessage() {
        return message;
    }

    public void setMessage(String[] message) {
        this.message = message;
    }
}
