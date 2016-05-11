import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.util.Queue;
import java.util.Random;

@SuppressWarnings("serial")
public class GUI extends JFrame implements DisplayInterface {
    
    Preferences pref;
    MazeWorld world;
    Queue<Command> commands;
    
    JPanel windowPanel;
    JPanel titlePanel;
    JPanel gamePanel;
    JPanel menuPanel;
    
    public GUI (Preferences pref, MazeWorld world, Queue<Command> commands) {
        this.pref = pref;
        this.world = world;
        this.commands = commands;
        
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                initUI();
            }
        });
    }
    
    public void initGUI() {
        initUI();
    }

    public void update() {
        drawTitlePanel();
        drawGamePanel();
        drawMenuPanel();
        setFocusable(true);
        pack();
    }
    
    private void initUI() {
        /* Any layout information that should never be changed
         * should be contained in here. Anything that can be redrawn
         * must be added into the draw*() functions. 
         */
        
        // Define layout for windowPanel and add it this object (JFrame)
        windowPanel = new JPanel();
        windowPanel.setLayout(new BoxLayout(windowPanel, BoxLayout.Y_AXIS));
        this.add(windowPanel);
        
        // Define Layouts for each panel
        titlePanel = new JPanel(new GridBagLayout());
        titlePanel.setMinimumSize(new Dimension(300, 50));
        gamePanel = new JPanel(new GridBagLayout());
        gamePanel.setPreferredSize(new Dimension(600, 600));
        menuPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        // Add children panels to the parent windowPanel
     //   windowPanel.add(titlePanel);
        windowPanel.add(gamePanel);
        windowPanel.add(menuPanel);
        
        // Set more information
        setTitle(pref.getText("appName"));
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        
        
        // Register a keystroke
        this.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_DOWN:  addCommand(new Command(Com.MOVE_DOWN));     break;
                    case KeyEvent.VK_LEFT:  addCommand(new Command(Com.MOVE_LEFT));     break;
                    case KeyEvent.VK_RIGHT: addCommand(new Command(Com.MOVE_RIGHT));    break;
                    case KeyEvent.VK_UP:    addCommand(new Command(Com.MOVE_UP));       break;
                    case KeyEvent.VK_C:     addCommand(new Command(Com.SOLVE));         break;
                    case KeyEvent.VK_N: 
                        JFormattedTextField box = (JFormattedTextField)menuPanel.getComponent(1);
                        int width = Integer.parseInt(box.getText());
                        
                        box = (JFormattedTextField)menuPanel.getComponent(3);
                        int height = Integer.parseInt(box.getText());
                        
                        int maxSize = pref.getValue("maxMazeSize");
                        if (height>maxSize) height=maxSize;
                        if (width>maxSize) width=maxSize;
                        
                        pref.setPreference("value.defaultMapWidth="+width);
                        pref.setPreference("value.defaultMapHeight="+height);
                        
                        addCommand(new CommandMap(Com.NEW_MAP, width, height));         
                        break;
                }
            }
        });
        
        // Not really necessary I think, but it's clear
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent){
                addCommand(new Command(Com.EXIT));
            }
        });
    }
    
    private void addCommand(Command c) {
        commands.add(c);
    }
    
    
    private void drawGamePanel() {
        // Need to fix this later as to not break encapsulation
        Maze m = world.getMaze();

        Color wallColour = pref.getColour("wallColour");
        Color floorColour = pref.getColour("tileColour");
        Color startColour = pref.getColour("startColour");
        Color finishColour = pref.getColour("finishColour");
        Color playerColour = pref.getColour("playerColour");
        Color tileColour = pref.getColour("tileColor");
        
        gamePanel.removeAll();
        
        /*
         * This creates a new JPanel with the gePreferredSize() method
         * @Overriden by the code inside. This code is called when java builds
         * the swing interface (I think).
         * This method relies on the JPanel being the only component inside
         * the parent container.
         */
        JPanel innerGamePanel = new JPanel() {
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
        };
        innerGamePanel.setLayout(new GridBagLayout());

        gamePanel.setBackground(tileColour);

        GridBagConstraints panelConstraints = new GridBagConstraints();
        
        int cols = m.getWidth()*2;
        int rows = m.getHeight()*2;
        
        // Iterate over the columns
        for (int col = 0; col <= cols; col++) {
            // Iterate over the rows
            for (int row = 0; row <= rows; row++) {
                JPanel panel = new JPanel();

                // Defaults for a floor tile
                panel.setBackground(floorColour);
                panelConstraints.fill = GridBagConstraints.BOTH;
                panelConstraints.weightx = 1;
                panelConstraints.weighty = 1;
                panelConstraints.gridx = col;
                panelConstraints.gridy = row;
                
                // If on a wall column
                if (col%2 == 0) {
                    panelConstraints.weightx = 0.1;
                }
                
                // If on a wall row
                if (row%2 == 0) {
                    panelConstraints.weighty = 0.1;
                }
                
                // If there is a wall intersection
                if (col%2 == 0 && row%2 == 0) {
                    // Wall intersection
                    panel.setBackground(wallColour);                    
                } else if (col == 0 || col == cols || row == 0 || row == rows) {
                    //top, bottom, left, right boundaries
                    panel.setBackground(wallColour);
                } else {
                    // Vertical walls
                    if (row%2 == 0) {
                        // Check if the wall exists
                        if (!m.isAdjacent((col-1)/2, (row/2)-1, (col-1)/2, (row/2))){
                            panel.setBackground(wallColour);
                        }
                    } // Horizontal walls
                    else if (col%2 == 0) {
                        if (!m.isAdjacent((col/2)-1, (row-1)/2, col/2, (row-1)/2)){
                            panel.setBackground(wallColour);
                        }
                    }
                }
                
                // Tile blocks
                if (col%2 != 0 && row%2 != 0) {
                    innerGamePanel.setBackground(tileColour);
                    
                    if (world.isChatacterHere((col-1)/2, (row-1)/2)) {
                        panel.setBackground(playerColour);
                    } else if (m.isStart((col-1)/2, (row-1)/2)) {
                        panel.setBackground(startColour);
                    } else if (m.isFinish((col-1)/2, (row-1)/2)) {
                        panel.setBackground(finishColour);
                    }
                }
                innerGamePanel.add(panel, panelConstraints);
            }
        }
        gamePanel.add(innerGamePanel);
        gamePanel.setPreferredSize(gamePanel.getSize());
    }

    public void drawTitlePanel() {
        titlePanel.removeAll();
        titlePanel.setBackground(pref.getColour("titleDefaultColour"));
        
        JLabel title = new JLabel();
        if (world.getWinStatus()) {
            titlePanel.setBackground(pref.getColour("titleWinColour"));
            title.setText(pref.getText("winMessage"));
        }
        titlePanel.add(title);
    }
    public void drawMenuPanel() {
        menuPanel.removeAll();
        menuPanel.setBackground(pref.getColour("menuColour"));

        JFormattedTextField widthSize = new JFormattedTextField();
        widthSize.setValue(Integer.toString(pref.getValue("defaultMapWidth")));
        widthSize.setColumns(2);
        JFormattedTextField heightSize = new JFormattedTextField();
        heightSize.setValue(Integer.toString(pref.getValue("defaultMapHeight")));
        heightSize.setColumns(2);

        JButton playButton = new JButton("New Maze");
        playButton.setMnemonic(KeyEvent.VK_N);
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                JFormattedTextField box = (JFormattedTextField)menuPanel.getComponent(1);
                int width = Integer.parseInt(box.getText());
                
                box = (JFormattedTextField)menuPanel.getComponent(3);
                int height = Integer.parseInt(box.getText());
                
                int maxSize = pref.getValue("maxMazeSize");
                if (height>maxSize) height=maxSize;
                if (width>maxSize) width=maxSize;
                
                pref.setPreference("value.defaultMapWidth="+width);
                pref.setPreference("value.defaultMapHeight="+height);
                
                addCommand(new CommandMap(Com.NEW_MAP, width, height));
            }
        });
        JButton closeButton = new JButton("Exit");
        closeButton.setMnemonic(KeyEvent.VK_W);
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCommand(new Command(Com.EXIT));
            }
        });
        JButton solveButton = new JButton("Solve");
        solveButton.setMnemonic(KeyEvent.VK_S);
        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCommand(new Command(Com.SOLVE));
            }
        });
        menuPanel.add(new JLabel("Width"));
        menuPanel.add(widthSize);
        menuPanel.add(new JLabel("Height"));
        menuPanel.add(heightSize);
        menuPanel.add(solveButton);
        menuPanel.add(playButton);
        menuPanel.add(closeButton);
    }
    
    public void close() {
        this.dispose();
    }
    
    /*
    private class MenuItemAction extends AbstractAction {
        public MenuItemAction(String text, Integer mnemonic) {
            super(text);
            putValue(MNEMONIC_KEY, mnemonic);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String s = e.getActionCommand();
            addCommand(new Command(Com.EXIT));
        }
    }
    */
}

@SuppressWarnings("serial")
class DrawPanel extends JPanel {

    private void doDrawing(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.blue);
        
        g2d.setColor(Color.black);
        g2d.drawRect(0, 0, 15, 15);
        g2d.fillRect(0, 0, 10, 10);
        
        /*
        for (int i = 0; i <= 1000; i++) {  
            Dimension size = getSize();
            Insets insets = getInsets();

            int w = size.width - insets.left - insets.right;
            int h = size.height - insets.top - insets.bottom;

            Random r = new Random();
            int x = Math.abs(r.nextInt()) % w;
            int y = Math.abs(r.nextInt()) % h;
            g2d.drawLine(x, y, x, y);
        }
        */
    }

    @Override
    public void paintComponent(Graphics g) {
        
        super.paintComponent(g);
        doDrawing(g);
    }
}