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
        Graphics2D g2d = (Graphics2D) g;
        
        // Turn on anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Get maze
        Maze m = world.getMaze();
        
        // Get the size of the window to draw into
        Dimension d = this.getParent().getSize();
        int windowHeight = d.height;
        int windowWidth = d.width;
        int windowSize = windowWidth > windowHeight ? windowHeight : windowWidth;

        // Draw the background of the maze
        g2d.setColor(floorColour);
        g2d.fillRect(0, 0, windowWidth, windowHeight);
        
        // Get the number of tiles in each direction of the maze
        int cols = m.getWidth();
        int rows = m.getHeight();
        
        // Find the size of the tile to be drawn        
        float tileSizeF = ((float) windowSize)/((float)rows);
        int tileSize = (int)tileSizeF;
        
        // Set the width of the walls to be drawn
        int wallWidth = 4;
        
        // Draw on start
        Coordinate c = m.getStartCoordinate();
        g2d.setColor(startColour);
        g2d.fillRect(c.getX()*tileSize, c.getY()*tileSize, tileSize, tileSize);

        // Draw on finish
        c = m.getFinishCoordinate();
        g2d.setColor(finishColour);
        g2d.fillRect(c.getX()*tileSize, c.getY()*tileSize, tileSize, tileSize);

        // Draw on character
        c = world.getPlayerCoordinate();
        int playerX = c.getX();
        int playerY = c.getY();
        
        // set how big the circle diameter should be relative to the tileSize
        int diameter = tileSize/3;
        
        // find coordinate of where to draw circle from
        int circleCentreX = playerX*tileSize + ((tileSize/2) -(diameter/2));
        int circleCentreY = playerY*tileSize + ((tileSize/2) -(diameter/2));
        
        g2d.setColor(playerColour);
        Ellipse2D.Double circle = new Ellipse2D.Double(circleCentreX, circleCentreY, diameter, diameter);
        g2d.fill(circle);

        // Draw on coins
        ArrayList<Coordinate> coords = world.getEntityCoordinates();
        for (Coordinate s : coords) {
            g2d.setColor(coinColour);
            int eX = s.getX();
            int eY = s.getY();
            g2d.fillRect(eX*tileSize, eY*tileSize, tileSize, tileSize);
        }
        
        // Draw walls on the inside of tiles
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                
                int cellX = col*tileSize;
                int cellY = row*tileSize;
                
                // draw cell walls
                g2d.setColor(wallColour);
                if (m.isNorthWall(new Coordinate(col,row))) {
                    g2d.fillRect(cellX, cellY, tileSize, wallWidth);                  
                }
                if (m.isEastWall(new Coordinate(col,row))) {
                    g2d.fillRect(cellX+tileSize-wallWidth, cellY, wallWidth, tileSize);                
                }
                if (m.isSouthWall(new Coordinate(col,row))) {
                    g2d.fillRect(cellX, cellY+tileSize-wallWidth, tileSize, wallWidth);                  
                }
                if (m.isWestWall(new Coordinate(col,row))) {
                    g2d.fillRect(cellX, cellY, wallWidth, tileSize);                   
                }
            }
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
