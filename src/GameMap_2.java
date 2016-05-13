import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import javax.swing.JPanel;


public class GameMap_2 extends JPanel {

    private Color wallColour;
    private Color floorColour;
    private Color startColour;
    private Color finishColour;
    private Color playerColour;
    private Color coinColour;
    
    private Color colour;
    private MazeWorld world;
    
    public GameMap_2 (MazeWorld world) {
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
        
        System.out.println("Drawing graphics mkII!");
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

        Dimension d = this.getParent().getSize();
        int windowHeight = d.height;
        int windowWidth = d.width;
        int windowSize = windowWidth > windowHeight ? windowHeight : windowWidth;
        
        // draw the backdrop
        g2d.setColor(floorColour);
        g2d.fillRect(0, 0, windowWidth, windowHeight);
        
        // draw wall columns
        int cols = world.getMaze().getWidth();
        int rows = world.getMaze().getHeight();
        
        float tileSizeF = ((float) windowSize)/((float)rows);
        
        int tileSize = (int)tileSizeF;
        
        Maze m = world.getMaze();

        
        // Draw on start
        Node n = m.getStart();
        g2d.setColor(startColour);
        g2d.fillRect(n.getX()*tileSize, n.getY()*tileSize, tileSize, tileSize);

        // Draw on finish
        n = m.getFinish();
        g2d.setColor(finishColour);
        g2d.fillRect(n.getX()*tileSize, n.getY()*tileSize, tileSize, tileSize);

        // Draw on character
        Coordinate playerC = world.getPlayerCoordinate();
        int playerX = playerC.getX();
        int playerY = playerC.getY();
        
        int circleDivision = 2;
        
        int diameter = tileSize/circleDivision;
        
        int circleCentreX = playerX*tileSize + ((tileSize/2) -(diameter/2));
        int circleCentreY = playerY*tileSize + ((tileSize/2) -(diameter/2));
        
        g2d.setColor(playerColour);
        Ellipse2D.Double circle = new Ellipse2D.Double(circleCentreX, circleCentreY, diameter, diameter);
        g2d.fill(circle);

        // Draw on coins
        ArrayList<Coordinate> coords = world.getEntityCoordinates();
        for (Coordinate c : coords) {
            g2d.setColor(coinColour);
            int eX = c.getX();
            int eY = c.getY();
            g2d.fillRect(eX*tileSize, eY*tileSize, tileSize, tileSize);
        }
        
        // Draw walls
        for (int row = 0; row < rows; row++) {
            // iterate over columns
            for (int col = 0; col < cols; col++) {
                // draw a wall corner
                
                int cellX = col*tileSize;
                int cellY = row*tileSize;

                // draw cell walls
                g2d.setColor(wallColour);
                if (m.isNorthWall(new Coordinate(col,row))) {
                    g2d.drawLine(cellX, cellY, cellX+tileSize-1, cellY);                  
                }
                if (m.isEastWall(new Coordinate(col,row))) {
                    g2d.drawLine(cellX+tileSize-1, cellY, cellX+tileSize-1, cellY+tileSize-1);                
                }
                if (m.isSouthWall(new Coordinate(col,row))) {
                    g2d.drawLine(cellX, cellY+tileSize-1, cellX+tileSize-1, cellY+tileSize-1);                  
                }
                if (m.isWestWall(new Coordinate(col,row))) {
                    g2d.drawLine(cellX, cellY, cellX, cellY+tileSize-1);                   
                }
            }
        }
        
        /*
        */
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
