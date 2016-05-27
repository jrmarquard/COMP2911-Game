public class Message {
    public static final int EXIT = 0;
    public static final int GAME_MSG = 1;
    public static final int SOUND_MSG = 2;
    
    private int id;
    private String[] message;
    
    /**
     * Creates a message with input id
     * 
     * @param id Id of the message
     */
    public Message (int id) {
        this.id = id;
        this.message = null;
    }

    /**
     * Creates a message with input id and which contains the input string
     * 
     * @param id Id of the message
     * @param message The message to be delivered
     */
    public Message(int id, String[] message) {
        this.id = id;
        this.setMessage(message);
    }

    /**
     * Gets the command Id of this message
     * 
     * @return The command Id of this message
     */
    public int getCommandID() {
        return id;
    }

    /**
     * Gets the string representation of this message
     * 
     * @return String representation of the message
     */
    public String[] getMessage() {
        return message;
    }

    /**
     * Sets the message string to the input string
     * 
     * @param message String to be delivered with the message
     */
    public void setMessage(String[] message) {
        this.message = message;
    }
}
