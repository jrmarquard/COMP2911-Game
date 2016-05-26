import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;


@SuppressWarnings("serial")
public class GameMap extends JPanel {

    private World world;
    private Graphics2D g2d;
    private PaintRefresh timer;
    private BufferedImage wallV = null;
    private BufferedImage wallH = null;
    private BufferedImage wallC = null;
    private BufferedImage floor = null;
    private BufferedImage doorV = null;
    private BufferedImage doorH = null;
    private BufferedImage coin = null;
    private BufferedImage key = null;
    private BufferedImage playerU = null;
    private BufferedImage playerD = null;
    private BufferedImage playerL = null;
    private BufferedImage playerR = null;
    private BufferedImage enemyU = null;
    private BufferedImage enemyD = null;
    private BufferedImage enemyL = null;
    private BufferedImage enemyR = null;
    private BufferedImage start = null;
    private BufferedImage finishU = null;
    private BufferedImage finishD = null;
    private BufferedImage finishL = null;
    private BufferedImage finishR = null;
    
    public GameMap (World world) {
        super();
        this.world = world;
        
        String pack = App.pref.getText("texturePack");
        
        timer = new PaintRefresh(this);
        timer.start();
        try{
	        wallV = ImageIO.read(new File("Images/"+pack+"/wallVertical.png"));
	        wallH = ImageIO.read(new File("Images/"+pack+"/wallHorizontal.png"));
	        wallC = ImageIO.read(new File("Images/"+pack+"/wallCorner.png"));
	        floor = ImageIO.read(new File("Images/"+pack+"/floor.png"));
	        doorV = ImageIO.read(new File("Images/"+pack+"/doorVertical.png"));
	        doorH = ImageIO.read(new File("Images/"+pack+"/doorHorizontal.png"));
	        coin = ImageIO.read(new File("Images/"+pack+"/coin.png"));
	        key = ImageIO.read(new File("Images/"+pack+"/key.png"));
	        playerU = ImageIO.read(new File("Images/"+pack+"/charU.png"));
	        playerD = ImageIO.read(new File("Images/"+pack+"/charD.png"));
	        playerL = ImageIO.read(new File("Images/"+pack+"/charL.png"));
	        playerR = ImageIO.read(new File("Images/"+pack+"/charR.png"));
	        enemyU = ImageIO.read(new File("Images/"+pack+"/enemyUp.png"));
	        enemyD = ImageIO.read(new File("Images/"+pack+"/enemyDown.png"));
	        enemyL = ImageIO.read(new File("Images/"+pack+"/enemyLeft.png"));
	        enemyR = ImageIO.read(new File("Images/"+pack+"/enemyRight.png"));
	        start = ImageIO.read(new File("Images/"+pack+"/start.png"));
	        finishU = ImageIO.read(new File("Images/"+pack+"/finishUp.png"));
	        finishD = ImageIO.read(new File("Images/"+pack+"/finishDown.png"));
	        finishL = ImageIO.read(new File("Images/"+pack+"/finishLeft.png"));
	        finishR = ImageIO.read(new File("Images/"+pack+"/finishRight.png"));
        } catch (IOException e) {
        }
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

        g2d.setColor(App.pref.getColour("backgroundColour"));
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
        
        Rectangle2D wallVSize = new Rectangle2D.Double(0, 0, wallWidth, tileSize+wallWidth);
        Rectangle2D wallHSize = new Rectangle2D.Double(0, 0, tileSize+wallWidth, wallWidth);
        Rectangle2D wallCSize = new Rectangle2D.Double(0, 0, wallWidth, wallWidth);
        Rectangle2D floorSize = new Rectangle2D.Double(0, 0, tileSize+wallWidth, tileSize+wallWidth);
        
        TexturePaint floorTexture = new TexturePaint(floor, floorSize);
        TexturePaint wallVTexture = new TexturePaint(wallV, wallVSize);
        TexturePaint wallHTexture = new TexturePaint(wallH, wallHSize);
        TexturePaint wallCTexture = new TexturePaint(wallC, wallCSize);
        TexturePaint doorVTexture = new TexturePaint(doorV, wallVSize);
        TexturePaint doorHTexture = new TexturePaint(doorH, wallHSize);
        TexturePaint coinTexture = new TexturePaint(coin, floorSize);
        TexturePaint playerUTexture = new TexturePaint(playerU, floorSize);
        TexturePaint playerDTexture = new TexturePaint(playerD, floorSize);
        TexturePaint playerLTexture = new TexturePaint(playerL, floorSize);
        TexturePaint playerRTexture = new TexturePaint(playerR, floorSize);
        TexturePaint keyTexture = new TexturePaint(key, floorSize);
        TexturePaint finishUTexture = new TexturePaint(finishU, floorSize);
        TexturePaint finishDTexture = new TexturePaint(finishD, floorSize);
        TexturePaint finishLTexture = new TexturePaint(finishL, floorSize);
        TexturePaint finishRTexture = new TexturePaint(finishR, floorSize);
        TexturePaint startTexture = new TexturePaint(start, floorSize);
        TexturePaint enemyUTexture = new TexturePaint(enemyU, floorSize);
        TexturePaint enemyDTexture = new TexturePaint(enemyD, floorSize);
        TexturePaint enemyLTexture = new TexturePaint(enemyL, floorSize);
        TexturePaint enemyRTexture = new TexturePaint(enemyR, floorSize);
        
        
        // Translate the maze to be centred in the given panel
        g2d.translate(offsetX + (mazeWidth - mazeUnitCols*wallWidth)/2, offsetY + (mazeHeight - mazeUnitRows*wallWidth)/2);
        
        // Draw floor
        //g2d.setColor(pref.getColour("floorColour"));
        g2d.setPaint(floorTexture);
        g2d.fillRect(0, 0, mazeUnitCols*wallWidth, mazeUnitRows*wallWidth);
        
        // Draw on start
        Node n = world.getStartNode();
        g2d.setPaint(startTexture);
        g2d.fillRect(wallWidth+(n.getX()*(wallWidth+tileSize)), wallWidth+(n.getY()*(wallWidth+tileSize)), tileSize, tileSize);
        
        // Draw on finish
        g2d.setPaint(finishUTexture);
        n = world.getFinishNode();
        if(n.getUp() != null) g2d.setPaint(finishUTexture);
        else if(n.getDown() != null) g2d.setPaint(finishDTexture);
        else if(n.getLeft() != null) g2d.setPaint(finishLTexture);
        else if(n.getRight() != null) g2d.setPaint(finishRTexture);
        g2d.fillRect(wallWidth+(n.getX()*(wallWidth+tileSize)), wallWidth+(n.getY()*(wallWidth+tileSize)), tileSize, tileSize);

        // Draw on items
        for (Item i : world.getItems()) {
            n = i.getNode();
            if (i.getType() == Item.COIN) {
                // Draw on coins
                g2d.setPaint(coinTexture);
                g2d.fillRect(wallWidth+(n.getX()*(wallWidth+tileSize)), wallWidth+(n.getY()*(wallWidth+tileSize)), tileSize, tileSize);  
            } else if (i.getType() == Item.ENERGY) {
                // Draw on 'energy'
                g2d.setColor(Color.pink);
                g2d.fillRect(wallWidth+(n.getX()*(wallWidth+tileSize)), wallWidth+(n.getY()*(wallWidth+tileSize)), tileSize, tileSize);                
            }
        }
        // Draw on character
        n = world.getEntityNode("Moneymaker");
        String dirP = world.getEntityDirection("Moneymaker");
        if (world.isBeingDead("Moneymaker")) {
            g2d.setColor(Color.black);
            g2d.fillRect(wallWidth+(n.getX()*(wallWidth+tileSize)), wallWidth+(n.getY()*(wallWidth+tileSize)), tileSize, tileSize);
        } else {
            if (dirP.equals("up")) g2d.setPaint(playerUTexture);
            if (dirP.equals("down")) g2d.setPaint(playerDTexture);
            if (dirP.equals("left")) g2d.setPaint(playerLTexture);
            if (dirP.equals("right")) g2d.setPaint(playerRTexture);
            g2d.fillRect(wallWidth+(n.getX()*(wallWidth+tileSize)), wallWidth+(n.getY()*(wallWidth+tileSize)), tileSize, tileSize);
        } 
        
        // Draw on Enemy
        if(App.pref.getBool("enemy")) {
            n = world.getEntityNode("Enemy");
            if (world.isBeingDead("Enemy")) {
                g2d.setColor(Color.black);
                g2d.fillRect(wallWidth+(n.getX()*(wallWidth+tileSize)), wallWidth+(n.getY()*(wallWidth+tileSize)), tileSize, tileSize);
            } else {
                String dirE = world.getEntityDirection("Enemy");
                if (dirE.equals("up")) g2d.setPaint(enemyUTexture);
                if (dirE.equals("down")) g2d.setPaint(enemyDTexture);
                if (dirE.equals("left")) g2d.setPaint(enemyLTexture);
                if (dirE.equals("right")) g2d.setPaint(enemyRTexture);
                g2d.fillRect(wallWidth+(n.getX()*(wallWidth+tileSize)), wallWidth+(n.getY()*(wallWidth+tileSize)), tileSize, tileSize);
            }
        }
        
        // Draw key
        n = world.getKeyNode();
        //g2d.setColor(pref.getColour("keyColour"));
        g2d.setPaint(keyTexture);
        if(n != null) {
            g2d.fillRect(wallWidth+(n.getX()*(wallWidth+tileSize)), wallWidth+(n.getY()*(wallWidth+tileSize)), tileSize, tileSize);
        }

        // Draw the east/north/west/south walls of entire maze
        g2d.setPaint(wallVTexture);
        g2d.fillRect(0, 0, wallWidth, mazeUnitRows*wallWidth);
        g2d.setPaint(wallHTexture);
        g2d.fillRect(0, 0, mazeUnitCols*wallWidth, wallWidth);
        g2d.setPaint(wallVTexture);
        g2d.fillRect((mazeUnitCols-1)*wallWidth, 0, wallWidth, mazeUnitRows*wallWidth);
        g2d.setPaint(wallHTexture);
        g2d.fillRect(0, (mazeUnitRows-1)*wallWidth, mazeUnitCols*wallWidth, wallWidth);
        
        // Draw inside of maze
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                g2d.setColor(App.pref.getColour("wallColour"));
                Color shade = App.pref.getColour("wallColour");
                if (col%2 == 0 && row%2 == 0) {
                    // Wall corners
                	g2d.setPaint(wallCTexture);
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
                        	g2d.setPaint(doorVTexture);
                            g2d.fillRect((tileSize+wallWidth)*((col/2)), wallWidth + (tileSize+wallWidth)*((row-1)/2), wallWidth, tileSize);
                            // Draw the shade on top
                            float vis = world.getWallVisibility((col/2)-1, (row-1)/2, col/2, (row-1)/2);
                            float opacity = (255f/100f)*vis;
                            g2d.setColor(new Color(shade.getRed(), shade.getGreen(), shade.getBlue(), (int)opacity));
                            g2d.fillRect((tileSize+wallWidth)*((col/2)), wallWidth + (tileSize+wallWidth)*((row-1)/2), wallWidth, tileSize);  
                    	} else if (!world.isConnected((col/2)-1, (row-1)/2, col/2, (row-1)/2)){
                    	    // Draw a wall
                    		g2d.setPaint(wallVTexture);
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
                    		g2d.setPaint(doorHTexture);
                            g2d.fillRect(wallWidth + (tileSize+wallWidth)*((col-1)/2), (tileSize+wallWidth)*(row/2), tileSize, wallWidth);
                            // Draw the shade on top
                            float vis = world.getWallVisibility((col-1)/2, (row/2)-1, (col-1)/2, (row/2));
                            float opacity = (255f/100f)*vis;
                            g2d.setColor(new Color(shade.getRed(), shade.getGreen(), shade.getBlue(), (int)opacity));
                    		g2d.fillRect(wallWidth + (tileSize+wallWidth)*((col-1)/2), (tileSize+wallWidth)*(row/2), tileSize, wallWidth);
                    	} else if (!world.isConnected((col-1)/2, (row/2)-1, (col-1)/2, (row/2))){
                            // Draw a wall
                    		g2d.setPaint(wallHTexture);
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
            //App.pref.getValue("refreshPeriod")
            super(App.pref.getValue("refreshPeriod"), new ActionListener() {
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

