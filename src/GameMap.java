import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
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
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Dimension d = this.getParent().getSize();
        int windowHeight = d.height;
        int windowWidth = d.width;
        int windowSize = windowWidth > windowHeight ? windowHeight : windowWidth;
        
        // draw the backdrop
        
        // draw wall columns
        int cols = world.getMaze().getWidth()*2;
        int rows = world.getMaze().getHeight()*2;
        
        // This is the ratio of tile to wall size. An r of 3 means
        // that the tile is 4 times bigger. r>0
        int r = 6;
        
        int offset = (windowSize %((r)*cols + 1))/2;
        
        while(windowSize %((r)*cols + 1) != 0) {
            windowSize--;
        }
        // should be exactly divisible now
        int unit = windowSize / ((cols*(r) + 1));
        
        int tileSize = unit*(2*r-1);
        int wallWidth = unit;
        
        Maze m = world.getMaze();

        g2d.setColor(floorColour);
        g2d.fillRect(offset, offset, (unit*((rows)*(r)+1)), (unit*((cols)*(r)+1)));

        // Draw the boundaries
        g2d.setColor(wallColour);
        g2d.fillRect(offset+0, offset+0, unit, (unit*((rows)*(r)+1)));
        g2d.fillRect(offset+(unit*(cols)*(r)), offset+0, unit, (unit*((rows)*(r)+1)));
        g2d.fillRect(offset+0, offset+0, (unit*((rows)*(r)+1)), unit);
        g2d.fillRect(offset+0, offset+(unit*(rows)*(r)), (unit*(rows)*(r)+1), unit);
        
        // Draw the inner walls
        for (int row = 0; row <= rows; row++) {
            for (int col = 0; col <= cols; col++) {
                if (col%2 == 0 && row%2 == 0) {
                    // Wall corners
                    g2d.fillRect(offset+(col*(r)*unit), offset+(row*(r)*unit), unit, unit);                    
                } else if (col == 0 || col == cols || row == 0 || row == rows) {
                    
                } else {
                    // Vertical walls
                    if (col%2 == 0) {
                        if (!m.isAdjacent((col/2)-1, (row-1)/2, col/2, (row-1)/2)){
                            g2d.fillRect(offset+(col*(r)*unit), offset+unit+(((row-1)/2)*unit*(2*r)), wallWidth, tileSize);   
                        }
                    }
                    // Horizontal walls
                    else if (row%2 == 0) {
                        if (!m.isAdjacent((col-1)/2, (row/2)-1, (col-1)/2, (row/2))){
                            g2d.fillRect(offset+unit+(((col-1)/2)*unit*(2*r)), offset+(row*(r)*unit), tileSize, wallWidth);
                        }
                    }
                }
            }
        }
        
        // Draw on start
        Node n = m.getStart();
        g2d.setColor(startColour);
        g2d.fillRect(offset+unit+(n.getX()*(wallWidth+tileSize)), offset+unit+(n.getY()*(wallWidth+tileSize)), tileSize, tileSize);
        

        // Draw on start
        n = m.getFinish();
        g2d.setColor(finishColour);
        g2d.fillRect(offset+unit+(n.getX()*(wallWidth+tileSize)), offset+unit+(n.getY()*(wallWidth+tileSize)), tileSize, tileSize);

        
        // Draw on character
        Coordinate pC = world.getPlayerCoordinate();
        g2d.setColor(playerColour);
        g2d.fill(new Ellipse2D.Double(offset+unit+(pC.getX()*(wallWidth+tileSize)), offset+unit+(pC.getY()*(wallWidth+tileSize)), tileSize, tileSize));
        
        // Draw on coins
        ArrayList<Coordinate> coords = world.getEntityCoordinates();
        for (Coordinate c : coords) {
            g2d.setColor(coinColour);
            int eX = c.getX();
            int eY = c.getY();
            g2d.fillRect(offset+unit+(eX*unit*(2*r)), offset+unit+(eY*unit*(2*r)), tileSize, tileSize);
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
