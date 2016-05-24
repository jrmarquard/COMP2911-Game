import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.Timer;


@SuppressWarnings("serial")
public class GameMap extends JPanel {

    private Preferences pref;
    private World world;
    private Graphics2D g2d;
    private PaintRefresh timer;
    
    public GameMap (World world, Preferences pref) {
        super();
        this.world = world;
        this.pref = pref;
        
        timer = new PaintRefresh(this);
        timer.start();
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }
    
    /**
     * Draws the map onto graphics g.
     * 
     * The map is represented by a grid where each wall and each floor tile
     * have a cell. The wall rows/columns are resized so that they are smaller
     * than the floor tiles and look like an actual wall.
     * 
     * Each wall/tile is then checked and drawn onto the Graphics g, essentially
     * laying out the maze. This is followed by the game entities and features
     * such as the player, coins, start, finish, ... etc which are drawn on top.
     * 
     * @param g The graphics object to draw onto.
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
        int mazeColumns = world.getWidth();
        int mazeRows = world.getHeight();
        
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
        
        // Draw on start
        Node n = world.getStartNode();
        g2d.setColor(pref.getColour("startColour"));
        g2d.fillRect(wallWidth+(n.getX()*(wallWidth+tileSize)), wallWidth+(n.getY()*(wallWidth+tileSize)), tileSize, tileSize);
        
        // Draw on start
        n = world.getFinishNode();
        g2d.setColor(pref.getColour("finishColour"));
        g2d.fillRect(wallWidth+(n.getX()*(wallWidth+tileSize)), wallWidth+(n.getY()*(wallWidth+tileSize)), tileSize, tileSize);

        // Draw on character
        n = world.getPlayerNode();
        g2d.setColor(pref.getColour("playerColour"));
        Ellipse2D.Double circle = new Ellipse2D.Double(
                wallWidth+(n.getX()*(wallWidth+tileSize))+tileSize/4, 
                wallWidth+(n.getY()*(wallWidth+tileSize))+tileSize/4, 
                tileSize/2, 
                tileSize/2
        );
        g2d.fill(circle);
        
        // Draw on coins
        ArrayList<Node> nodes = world.getEntityNodes();
        g2d.setColor(pref.getColour("coinColour"));
        for (Node s : nodes) {
            g2d.fillRect(wallWidth+(s.getX()*(wallWidth+tileSize)), wallWidth+(s.getY()*(wallWidth+tileSize)), tileSize, tileSize);
        }
        
        // Draw key
        n = world.getKeyNode();
        if(n != null) {
            g2d.setColor(pref.getColour("keyColour"));
            g2d.fillRect(wallWidth+(n.getX()*(wallWidth+tileSize)), wallWidth+(n.getY()*(wallWidth+tileSize)), tileSize, tileSize);
        }

        // Draw the east/north/west/south walls of entire maze
        g2d.setColor(pref.getColour("wallColour"));
        g2d.fillRect(0, 0, wallWidth, mazeUnitRows*wallWidth);
        g2d.fillRect(0, 0, mazeUnitCols*wallWidth, wallWidth);
        g2d.fillRect((mazeUnitCols-1)*wallWidth, 0, wallWidth, mazeUnitRows*wallWidth);
        g2d.fillRect(0, (mazeUnitRows-1)*wallWidth, mazeUnitCols*wallWidth, wallWidth);
        
        // Draw inside of maze
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                g2d.setColor(pref.getColour("wallColour"));
                Color shade = pref.getColour("wallColour");
                if (col%2 == 0 && row%2 == 0) {
                    // Wall corners
                    g2d.fillRect((col/2)*(wallWidth+tileSize),(row/2)*(wallWidth+tileSize),wallWidth,wallWidth);                    
                } else if (col == 0 || col == cols-1 || row == 0 || row == rows-1) {
                    // Skips north/east/south/west boundaries
                } else if (col%2 != 0 && row%2 != 0) {
                    // TILES!!
                    float vis = world.getNodeVisibility((col-1)/2, (row-1)/2);
                    float opacity = (255f/100f)*vis;
                    g2d.setColor(new Color(shade.getRed(), shade.getGreen(), shade.getBlue(), (int)opacity));
                    g2d.fillRect(wallWidth+(((col-1)/2)*(wallWidth+tileSize)), wallWidth+(((row-1)/2)*(wallWidth+tileSize)), tileSize, tileSize);
                } else {
                    // Vertical walls
                    if (col%2 == 0) {
                        if (world.isDoor((col/2)-1, (row-1)/2, col/2, (row-1)/2)) {
                            // Draw the door
                            g2d.setColor(pref.getColour("doorColour"));
                            g2d.fillRect((tileSize+wallWidth)*((col/2)), wallWidth + (tileSize+wallWidth)*((row-1)/2), wallWidth, tileSize);
                            // Draw the shade on top
                            float vis = world.getWallVisibility((col/2)-1, (row-1)/2, col/2, (row-1)/2);
                            float opacity = (255f/100f)*vis;
                            g2d.setColor(new Color(shade.getRed(), shade.getGreen(), shade.getBlue(), (int)opacity));
                            g2d.fillRect((tileSize+wallWidth)*((col/2)), wallWidth + (tileSize+wallWidth)*((row-1)/2), wallWidth, tileSize);  
                    	} else if (!world.isConnected((col/2)-1, (row-1)/2, col/2, (row-1)/2)){
                    	    // Draw a wall
                            g2d.fillRect((tileSize+wallWidth)*((col/2)), wallWidth + (tileSize+wallWidth)*((row-1)/2), wallWidth, tileSize);   
                        } else {
                            // Draw shade
                            float vis = world.getWallVisibility((col/2)-1, (row-1)/2, col/2, (row-1)/2);
                            float opacity = (255f/100f)*vis;
                            g2d.setColor(new Color(shade.getRed(), shade.getGreen(), shade.getBlue(), (int)opacity));
                            g2d.fillRect((tileSize+wallWidth)*((col/2)), wallWidth + (tileSize+wallWidth)*((row-1)/2), wallWidth, tileSize);  
                        }
                    }
                    // Horizontal walls
                    else if (row%2 == 0) {
                    	if (world.isDoor((col-1)/2, (row/2)-1, (col-1)/2, (row/2))) {
                            // Draw the door
                    	    g2d.setColor(pref.getColour("doorColour"));
                            g2d.fillRect(wallWidth + (tileSize+wallWidth)*((col-1)/2), (tileSize+wallWidth)*(row/2), tileSize, wallWidth);
                            // Draw the shade on top
                            float vis = world.getWallVisibility((col-1)/2, (row/2)-1, (col-1)/2, (row/2));
                            float opacity = (255f/100f)*vis;
                            g2d.setColor(new Color(shade.getRed(), shade.getGreen(), shade.getBlue(), (int)opacity));
                    		g2d.fillRect(wallWidth + (tileSize+wallWidth)*((col-1)/2), (tileSize+wallWidth)*(row/2), tileSize, wallWidth);
                    	} else if (!world.isConnected((col-1)/2, (row/2)-1, (col-1)/2, (row/2))){
                            // Draw a wall
                            g2d.fillRect(wallWidth + (tileSize+wallWidth)*((col-1)/2), (tileSize+wallWidth)*(row/2), tileSize, wallWidth);   
                        } else {
                            // Draw shade
                            float vis = world.getWallVisibility((col-1)/2, (row/2)-1, (col-1)/2, (row/2));
                            float opacity = (255f/100f)*vis;
                            g2d.setColor(new Color(shade.getRed(), shade.getGreen(), shade.getBlue(), (int)opacity));
                            g2d.fillRect(wallWidth + (tileSize+wallWidth)*((col-1)/2), (tileSize+wallWidth)*(row/2), tileSize, wallWidth);
                        }
                    }
                } 
            }
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
    
    /**
     * Attached to this GameMap to refresh the display at a set rate.     *
     */
    private class PaintRefresh extends Timer {
        public PaintRefresh(GameMap gameMap) {
            //pref.getValue("refreshPeriod")
            super(pref.getValue("refreshPeriod"), new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event)
                {
                    /*
                     *  Checks to see if the world has actually changed
                     *  before repainting.
                     */
                    if (world.isWorldChangeFlag()) {
                        repaint();
                        world.setWorldChangeFlag(false);
                    }
                    
                }
            });
        }
    }
} 

