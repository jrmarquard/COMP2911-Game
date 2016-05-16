import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import javax.swing.JPanel;


public class GameMap extends JPanel {

    private Preferences pref;
    private MazeWorld world;
    private int playerToDraw;
    private Graphics2D g2d;
    
    public GameMap (MazeWorld world, Preferences pref, int player) {
        super();
        this.world = world;
        this.playerToDraw = player;
        this.pref = pref;
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }
    
    /**
     * Draws the map onto graphics g.
     * 
     * @param g
     */
    private void doDrawing(Graphics g) {
        g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Gets the size of the panel to draw into
        Dimension d = this.getPreferredSize();
        int windowHeight = d.height;
        int windowWidth = d.width;

        g2d.setColor(pref.getColour("backgroundColour"));
        g2d.fillRect(0, 0, windowWidth, windowHeight);
        
        // Gets the number of rows and columns in maze
        Maze m = world.getMaze();
        int mazeColumns = m.getWidth();
        int mazeRows = m.getHeight();
        
        // positive integer
        int tileRatio = 5;
        
        // the unit size of the width and height
        int mazeUnitCols = mazeColumns*(1+tileRatio) +1;
        int mazeUnitRows = mazeRows*(1+tileRatio) +1;
        
        // The number of grid coordinates
        int rows = mazeRows*2 + 1;
        int cols = mazeColumns*2 + 1;
        
        int mazeHeight = windowHeight;
        int mazeWidth = windowWidth;
        int offsetX = 0;
        int offsetY = 0;
        
        // windowHeight/windowWidth = lambda * (mazeUnitRows/mazeUnitCols)        
        float lambda = ((float)windowHeight/(float)windowWidth)*((float)mazeUnitCols/(float)mazeUnitRows); 
        
        if (lambda == 1) {
            // The window is a perfect fit for the size of the maze (ratio matches up)
            // The default values are fine
        } else if (lambda > 1) {
            // Space on the top/bottom
            mazeHeight = (int) ((float)windowWidth*((float)mazeUnitRows/(float)mazeUnitCols));
            offsetY = (windowHeight - mazeHeight)/2;
        } else if (lambda < 1) {
            // space on the left and right
            mazeWidth = (int)((float)windowHeight*((float)mazeUnitCols/(float)mazeUnitRows));
            offsetX = (windowWidth - mazeWidth)/2;
        }        
        
        // Lengths of wall and tiles in maze
        int wallWidth = mazeWidth/mazeUnitCols;
        int tileSize = tileRatio*wallWidth;
        
        // Translate the maze to be centred in the given panel
        g2d.translate(offsetX + (mazeWidth - mazeUnitCols*wallWidth)/2, offsetY + (mazeHeight - mazeUnitRows*wallWidth)/2);
        
        // Draw floor
        g2d.setColor(pref.getColour("floorColour"));
        g2d.fillRect(0, 0, mazeUnitCols*wallWidth, mazeUnitRows*wallWidth);

        // Draw the east/north/west/south walls of entire maze
        g2d.setColor(pref.getColour("wallColour"));
        g2d.fillRect(0, 0, wallWidth, mazeUnitRows*wallWidth);
        g2d.fillRect(0, 0, mazeUnitCols*wallWidth, wallWidth);
        g2d.fillRect((mazeUnitCols-1)*wallWidth, 0, wallWidth, mazeUnitRows*wallWidth);
        g2d.fillRect(0, (mazeUnitRows-1)*wallWidth, mazeUnitCols*wallWidth, wallWidth);
        
        // Draw inside of maze
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (col%2 == 0 && row%2 == 0) {
                    // Wall corners
                    g2d.fillRect((col/2)*(wallWidth+tileSize),(row/2)*(wallWidth+tileSize),wallWidth,wallWidth);                    
                } else if (col == 0 || col == cols-1 || row == 0 || row == rows-1) {
                    // Skips north/east/south/west boundaries
                } else {
                    // Vertical walls
                    if (col%2 == 0) {
                        if (!m.isAdjacent((col/2)-1, (row-1)/2, col/2, (row-1)/2)){
                            g2d.fillRect((tileSize+wallWidth)*((col/2)), wallWidth + (tileSize+wallWidth)*((row-1)/2), wallWidth, tileSize);   
                        }
                    }
                    // Horizontal walls
                    else if (row%2 == 0) {
                        if (!m.isAdjacent((col-1)/2, (row/2)-1, (col-1)/2, (row/2))){
                            g2d.fillRect(wallWidth + (tileSize+wallWidth)*((col-1)/2), (tileSize+wallWidth)*(row/2), tileSize, wallWidth);   
                        }
                    }
                }
            }
        }
        
        // Draw on start
        Coordinate c = world.getStart();
        g2d.setColor(pref.getColour("startColour"));
        g2d.fillRect(wallWidth+(c.getX()*(wallWidth+tileSize)), wallWidth+(c.getY()*(wallWidth+tileSize)), tileSize, tileSize);
        
        // Draw on start
        c = world.getFinish();
        g2d.setColor(pref.getColour("finishColour"));
        g2d.fillRect(wallWidth+(c.getX()*(wallWidth+tileSize)), wallWidth+(c.getY()*(wallWidth+tileSize)), tileSize, tileSize);

        // Draw on character
        Coordinate pC = world.getPlayerCoordinate(this.playerToDraw);
        g2d.setColor(pref.getColour("playerColour"));
        Ellipse2D.Double circle = new Ellipse2D.Double(
                wallWidth+(pC.getX()*(wallWidth+tileSize))+tileSize/4, 
                wallWidth+(pC.getY()*(wallWidth+tileSize))+tileSize/4, 
                tileSize/2, 
                tileSize/2
        );
        g2d.fill(circle);
        
        // Draw on coins
        ArrayList<Coordinate> coords = world.getEntityCoordinates();
        g2d.setColor(pref.getColour("coinColour"));
        for (Coordinate s : coords) {
            g2d.fillRect(wallWidth+(s.getX()*(wallWidth+tileSize)), wallWidth+(s.getY()*(wallWidth+tileSize)), tileSize, tileSize);
        }
    }
    
    /*
     * Pushes the size of this panel to the dimensions of its parent.
     * This ensures that the panel takes up the maximum space available to it.
     */
    @Override
    public Dimension getPreferredSize() {
        Dimension d = this.getParent().getSize();        
        int windowHeight = d.height;
        int windowWidth = d.width;
        return new Dimension(windowWidth,windowHeight);
    }
} 

