import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;


@SuppressWarnings("serial")
public class GameMap extends JPanel {

    private World world;
    private Graphics2D g2d;
    private PaintRefresh timer;
    
    // Textures
    private BufferedImage floor = null;
    private BufferedImage wallC = null;
    private BufferedImage wallV = null;
    private BufferedImage wallH = null;
    private BufferedImage doorV = null;
    private BufferedImage doorH = null;
    private BufferedImage coin = null;
    private BufferedImage key = null;
    private BufferedImage playerU = null;
    private BufferedImage playerD = null;
    private BufferedImage playerL = null;
    private BufferedImage playerR = null;
    private BufferedImage playerDead = null;
    private BufferedImage enemyU = null;
    private BufferedImage enemyD = null;
    private BufferedImage enemyL = null;
    private BufferedImage enemyR = null;
    private BufferedImage enemyDead = null;
    private BufferedImage start = null;
    private BufferedImage finishU = null;
    private BufferedImage finishD = null;
    private BufferedImage finishL = null;
    private BufferedImage finishR = null;
    private BufferedImage attackU = null;
    private BufferedImage attackD = null;
    private BufferedImage attackL = null;
    private BufferedImage attackR = null;
    
    public GameMap (World world) {
        super();
        this.world = world;
        
        String pack = App.pref.getText("texturePack");
        timer = new PaintRefresh(this);
        timer.start();
        try{
            // Maze textures
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
	        playerDead = ImageIO.read(new File("Images/"+pack+"/charDead.png"));
	        enemyU = ImageIO.read(new File("Images/"+pack+"/enemyUp.png"));
	        enemyD = ImageIO.read(new File("Images/"+pack+"/enemyDown.png"));
	        enemyL = ImageIO.read(new File("Images/"+pack+"/enemyLeft.png"));
	        enemyR = ImageIO.read(new File("Images/"+pack+"/enemyRight.png"));
	        enemyDead = ImageIO.read(new File("Images/"+pack+"/enemyDead.png"));
	        start = ImageIO.read(new File("Images/"+pack+"/start.png"));
	        finishU = ImageIO.read(new File("Images/"+pack+"/finishUp.png"));
	        finishD = ImageIO.read(new File("Images/"+pack+"/finishDown.png"));
	        finishL = ImageIO.read(new File("Images/"+pack+"/finishLeft.png"));
	        finishR = ImageIO.read(new File("Images/"+pack+"/finishRight.png"));
	        attackU = ImageIO.read(new File("Images/"+pack+"/attackUp.png"));
	        attackD = ImageIO.read(new File("Images/"+pack+"/attackDown.png"));
	        attackL = ImageIO.read(new File("Images/"+pack+"/attackLeft.png"));
	        attackR = ImageIO.read(new File("Images/"+pack+"/attackRight.png"));
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
        
        /*
         * Load textures into TexturePaint objects.
         * Loading the textures loads them to a specific size to be draw on the maze.
         */
        
        // The 
        Rectangle2D wallVSize = new Rectangle2D.Double(0, 0, wallWidth, tileSize+wallWidth);
        Rectangle2D wallHSize = new Rectangle2D.Double(0, 0, tileSize+wallWidth, wallWidth);
        Rectangle2D wallCSize = new Rectangle2D.Double(0, 0, wallWidth, wallWidth);
        Rectangle2D floorSize = new Rectangle2D.Double(0, 0, wallWidth+tileSize, wallWidth+tileSize);

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
        TexturePaint playerDeadTexture = new TexturePaint(playerDead, floorSize);
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
        TexturePaint enemyDeadTexture = new TexturePaint(enemyDead, floorSize);
        TexturePaint attackUTexture = new TexturePaint(attackU, floorSize);
        TexturePaint attackDTexture = new TexturePaint(attackD, floorSize);
        TexturePaint attackLTexture = new TexturePaint(attackL, floorSize);
        TexturePaint attackRTexture = new TexturePaint(attackR, floorSize);

        // Translate the maze to be centred in the given panel
        g2d.translate(offsetX + (mazeWidth - mazeUnitCols*wallWidth)/2, offsetY + (mazeHeight - mazeUnitRows*wallWidth)/2);
        
        /*
         * Draws maze
         */
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                // Wall Corners
                if (col%2 == 0 && row%2 == 0) {
                    g2d.setPaint(wallCTexture);
                    g2d.fillRect((col/2)*(wallWidth+tileSize),(row/2)*(wallWidth+tileSize),wallWidth,wallWidth);                    
                } 
                // Horizontal walls
                else if (col%2 != 0 && row%2 == 0) {
                    String wallType = world.getWallType((col-1)/2, (row/2)-1, (col-1)/2, (row/2));
                    if (wallType.equals("wall")) {
                        g2d.setPaint(wallHTexture);
                    } else if (wallType.equals("door")) {
                        g2d.setPaint(doorHTexture);
                    } else if (wallType.equals("space")) {
                        g2d.setPaint(floorTexture);
                    }
                    g2d.fillRect(wallWidth + (tileSize+wallWidth)*((col-1)/2), (tileSize+wallWidth)*(row/2), tileSize, wallWidth);
                } 
                // Vertical walls
                else if (col%2 == 0 && row%2 != 0) {
                    String wallType = world.getWallType((col/2)-1, (row-1)/2, col/2, (row-1)/2);
                    if (wallType.equals("wall")) {
                        g2d.setPaint(wallVTexture);
                    } else if (wallType.equals("door")) {
                        g2d.setPaint(doorVTexture);
                    } else if (wallType.equals("space")) {
                        g2d.setPaint(floorTexture);
                    }
                    g2d.fillRect((tileSize+wallWidth)*((col/2)), wallWidth + (tileSize+wallWidth)*((row-1)/2), wallWidth, tileSize);
                } 
                // Floor tiles
                else if (col%2 != 0 && row%2 != 0) {
                    g2d.setPaint(floorTexture);
                    g2d.fillRect(wallWidth+(((col-1)/2)*(wallWidth+tileSize)), wallWidth+(((row-1)/2)*(wallWidth+tileSize)), tileSize, tileSize);                    
                } 
            }
        }
        
        /*
         * Draw items, entities, and other objects in the maze
         */
        
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

        String dirP = world.getEntityDirection("Moneymaker");
        
        // Draw on items
        for (Item i : world.getItems()) {
            n = i.getNode();
            if (i.getType() == Item.COIN) {
                // Draw on coins
                g2d.setPaint(coinTexture); 
            } else if (i.getType() == Item.ENERGY) {
                // Draw on 'energy'
            } else if (i.getType() == Item.PLAYER_CORPSE) {
                g2d.setPaint(playerDeadTexture);                
            } else if (i.getType() == Item.ENEMY_CORPSE) {
                g2d.setPaint(enemyDeadTexture);                
            }
            g2d.fillRect(wallWidth+(n.getX()*(wallWidth+tileSize)), wallWidth+(n.getY()*(wallWidth+tileSize)), tileSize, tileSize); 
        }
        
        // Draw on entities
        for (Entity e : world.getEntities()) {
            n = e.getNode(); 
            String dir = e.getDirection();
            int mode = e.getMode();
            if (e.getType() == Entity.PLAYER) {
                if (mode == Entity.MODE_ATTACK) {
                    if (dir.equals("up")) g2d.setColor(Color.red);
                    if (dir.equals("down")) g2d.setColor(Color.blue);
                    if (dir.equals("left")) g2d.setColor(Color.green);
                    if (dir.equals("right")) g2d.setColor(Color.pink);
                    g2d.fillRect(wallWidth+(n.getX()*(wallWidth+tileSize)), wallWidth+(n.getY()*(wallWidth+tileSize)), tileSize, tileSize);
                }  else if (mode == Entity.MODE_IDLE) {
                    if (dir.equals("up")) g2d.setPaint(playerUTexture);
                    if (dir.equals("down")) g2d.setPaint(playerDTexture);
                    if (dir.equals("left")) g2d.setPaint(playerLTexture);
                    if (dir.equals("right")) g2d.setPaint(playerRTexture);
                    g2d.fillRect(wallWidth+(n.getX()*(wallWidth+tileSize)), wallWidth+(n.getY()*(wallWidth+tileSize)), tileSize, tileSize);
                }
                
            } else if (e.getType() == Entity.ENEMY) {
                if (mode == Entity.MODE_IDLE) {
                    if (dir.equals("up")) g2d.setPaint(enemyUTexture);
                    if (dir.equals("down")) g2d.setPaint(enemyDTexture);
                    if (dir.equals("left")) g2d.setPaint(enemyLTexture);
                    if (dir.equals("right")) g2d.setPaint(enemyRTexture);
                    g2d.fillRect(wallWidth+(n.getX()*(wallWidth+tileSize)), wallWidth+(n.getY()*(wallWidth+tileSize)), tileSize, tileSize);
                }
            }
        }
        
        // Draw key
        n = world.getKeyNode();
        g2d.setPaint(keyTexture);
        if(n != null) {
            g2d.fillRect(wallWidth+(n.getX()*(wallWidth+tileSize)), wallWidth+(n.getY()*(wallWidth+tileSize)), tileSize, tileSize);
        }
        
        /*
         * Draws the lighting on the maze
         */
        Color shade = Color.black;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                // Wall Corners
                if (col%2 == 0 && row%2 == 0) {
                    float vis = world.getCornerVisibility((col/2)-1, (row/2)-1, (col/2)-1, row/2, col/2, (row/2)-1, col/2, row/2);
                    float opacity = (255f/100f)*vis;
                    g2d.setColor(new Color(shade.getRed(), shade.getGreen(), shade.getBlue(), (int)opacity));
                    g2d.fillRect((col/2)*(wallWidth+tileSize),(row/2)*(wallWidth+tileSize),wallWidth,wallWidth);                    
                } 
                // Horizontal walls
                else if (col%2 != 0 && row%2 == 0) {
                    float vis = world.getWallVisibility((col-1)/2, (row/2)-1, (col-1)/2, (row/2));
                    float opacity = (255f/100f)*vis;
                    g2d.setColor(new Color(shade.getRed(), shade.getGreen(), shade.getBlue(), (int)opacity));
                    g2d.fillRect(wallWidth + (tileSize+wallWidth)*((col-1)/2), (tileSize+wallWidth)*(row/2), tileSize, wallWidth);
                } 
                // Vertical walls
                else if (col%2 == 0 && row%2 != 0) {
                    float vis = world.getWallVisibility((col/2)-1, (row-1)/2, col/2, (row-1)/2);
                    float opacity = (255f/100f)*vis;
                    g2d.setColor(new Color(shade.getRed(), shade.getGreen(), shade.getBlue(), (int)opacity));
                    g2d.fillRect((tileSize+wallWidth)*((col/2)), wallWidth + (tileSize+wallWidth)*((row-1)/2), wallWidth, tileSize);  
                } 
                // Floor tiles
                else if (col%2 != 0 && row%2 != 0) {
                    float vis = world.getNodeVisibility((col-1)/2, (row-1)/2);
                    float opacity = (255f/100f)*vis;
                    g2d.setColor(new Color(shade.getRed(), shade.getGreen(), shade.getBlue(), (int)opacity));
                    g2d.fillRect(wallWidth+(((col-1)/2)*(wallWidth+tileSize)), wallWidth+(((row-1)/2)*(wallWidth+tileSize)), tileSize, tileSize);                    
                } 
            }
        }
        
    }
    
    /*      Code to calculate visibility
    
            float vis = world.getNodeVisibility((col-1)/2, (row-1)/2);
            float opacity = (255f/100f)*vis;
            g2d.setColor(new Color(shade.getRed(), shade.getGreen(), shade.getBlue(), (int)opacity));
            g2d.fillRect(wallWidth+(((col-1)/2)*(wallWidth+tileSize)), wallWidth+(((row-1)/2)*(wallWidth+tileSize)), tileSize, tileSize);
     */
    
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
                    repaint();   
                }
            });
        }
    }
} 

