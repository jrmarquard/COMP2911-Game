
public interface DisplayInterface {
    
    /**
     * Updates the interface. Will be typically called 
     * when the data the interface displays has been changed.
     */
    public void update();
    
    /**
     * Closes the interface.
     */
    public void close();

    /** 
     * Initlialises the 
     */
    public void initGUI();
}
