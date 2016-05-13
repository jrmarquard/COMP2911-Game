import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JPanel;


public class GameMap extends JPanel {

    private Color wallColour;
    private Color floorColour;
    private Color startColour;
    private Color finishColour;
    private Color playerColour;
    private Color coinColour;
    
    private Color colour;
    private MazeWorld world;
    
    public GameMap (MazeWorld world) {
        super();
        this.world = world;
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }
    
    @Override
    public void setBackground(Color colour) {
        this.colour = colour;
    }
    
    public void setColour (String s, Color c) {
        switch (s) {
            case "wallColour": wallColour = c; break;
            case "floorColour": floorColour = c; break;
            case "startColour": startColour = c; break;
            case "finishColour": finishColour = c; break;
            case "playerColour": playerColour = c; break;
            case "coinColour": coinColour = c; break;
        }
    }
    
    /**
     * Draws the map onto graphics g.
     * 
     * @param g
     */
    private void doDrawing(Graphics g) {
        
        System.out.println("Drawing graphics!");
        Graphics2D g2d = (Graphics2D) g;

        Dimension d = this.getParent().getSize();
        int windowHeight = d.height;
        int windowWidth = d.width;
        int windowSize = windowWidth > windowHeight ? windowHeight : windowWidth;
        
        // draw the backdrop
        g2d.setColor(floorColour);
        g2d.fillRect(0, 0, windowWidth, windowHeight);
        
        // draw wall columns
        int cols = world.getMaze().getWidth()*2;
        int rows = world.getMaze().getHeight()*2;
        
        float sizeOfWallCorner = ((float) windowSize)/((float)(rows+1));
        
        int wallCornerD = (int)sizeOfWallCorner;
        
        Maze m = world.getMaze();
        
        // Draw the maze layout first
        // iterate over rows
        for (int row = 0; row <= rows; row++) {
            // iterate over columns
            for (int col = 0; col <= cols; col++) {
                // draw a wall corner
                if (col%2 == 0 && row%2 == 0) {
                    g2d.setColor(wallColour);
                    g2d.fillRect(col*wallCornerD, row*wallCornerD, wallCornerD, wallCornerD);                    
                } else if (col == 0 || col == cols || row == 0 || row == rows) {
                    g2d.setColor(wallColour);
                    g2d.fillRect(col*wallCornerD, row*wallCornerD, wallCornerD, wallCornerD);
                } else {
                    // Vertical walls
                    if (row%2 == 0) {
                        // Check if the wall exists
                        if (!m.isAdjacent((col-1)/2, (row/2)-1, (col-1)/2, (row/2))){
                            g2d.setColor(wallColour);
                            g2d.fillRect(col*wallCornerD, row*wallCornerD, wallCornerD, wallCornerD);
                        }
                    } // Horizontal walls
                    else if (col%2 == 0) {
                        if (!m.isAdjacent((col/2)-1, (row-1)/2, col/2, (row-1)/2)){
                            g2d.setColor(wallColour);
                            g2d.fillRect(col*wallCornerD, row*wallCornerD, wallCornerD, wallCornerD);
                        }
                    }
                }
            }
        }
        
        // Draw on start
        Node start = m.getStart();
        int startX = start.getX() * 2 + 1;
        int startY = start.getY() * 2 + 1;
        g2d.setColor(startColour);
        g2d.fillRect(startX*wallCornerD, startY*wallCornerD, wallCornerD, wallCornerD);
        
        // Draw on finish
        Node finish = m.getFinish();
        int finishX = finish.getX() * 2 + 1;
        int finishY = finish.getY() * 2 + 1;
        g2d.setColor(finishColour);
        g2d.fillRect(finishX*wallCornerD, finishY*wallCornerD, wallCornerD, wallCornerD);
        
        // Draw on character
        int playerX = world.getCharacterPosX() * 2 + 1;
        int playerY = world.getCharacterPosY() * 2 + 1;
        g2d.setColor(playerColour);
        g2d.fillRect(playerX*wallCornerD, playerY*wallCornerD, wallCornerD, wallCornerD);
        
        
        // Draw on coins
        ArrayList<Coordinate> coords = world.getEntityCoordinates();
        for (Coordinate c : coords) {
            g2d.setColor(coinColour);
            int eX = c.getX() * 2 + 1;
            int eY = c.getY() * 2 + 1;
            g2d.fillRect(eX*wallCornerD, eY*wallCornerD, wallCornerD, wallCornerD);
        }
    }
    
    /*
     * This creates a new JPanel with the gePreferredSize() method
     * @Overriden by the code inside. This code is called when java builds
     * the swing interface (I think).
     * This method relies on the JPanel being the only component inside
     * the parent container.
     */
    @Override
    public Dimension getPreferredSize() {
        // Get the dimensions of the parent
        Dimension d = this.getParent().getSize();
        
        Maze m = world.getMaze();
        double height = m.getHeight();
        double width = m.getWidth();
        
        double windowHeight = d.height;
        double windowWidth = d.width;

        // if < 1, there should be extra space on the left/right
        // if > 1, there should be extra space on the top/bottom
        // do the maths yourself, it just works (tm)
        double magic = (windowWidth/windowHeight) * (height/width);
        if (magic <= 1) {
            int returnHeight = (int) (windowWidth*(height/width));
            int returnWidth = (int) windowWidth;
            return new Dimension(returnWidth,returnHeight);
        } else {
            int returnHeight = (int) windowHeight;
            int returnWidth = (int) (windowHeight*(width/height));
            return new Dimension(returnWidth,returnHeight);
        }
    }
}
