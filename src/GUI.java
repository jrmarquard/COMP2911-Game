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
        gamePanel = new JPanel(new GridBagLayout());
        gamePanel.setPreferredSize(new Dimension(400, 400));
        menuPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        // Add children panels to the parent windowPanel
        windowPanel.add(titlePanel);
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
        
        gamePanel.removeAll();
        gamePanel.setBackground(pref.getColour("tileColour"));
        
        gamePanel.setBorder(null);
        
        int cols = m.getWidth()*2;
        int rows = m.getHeight()*2;
        
        Color wallColour = pref.getColour("wallColour");
        Color floorColour = pref.getColour("tileColour");
        Color startColour = pref.getColour("startColour");
        Color finishColour = pref.getColour("finishColour");

        // Adjusts each panel's constraints
        GridBagConstraints panelConstraints = new GridBagConstraints();
        
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
                    if (world.isChatacterHere((col-1)/2, (row-1)/2)) {
                        panel.setBackground(pref.getColour("playerColour"));
                    } else if (m.isStart((col-1)/2, (row-1)/2)) {
                        panel.setBackground(pref.getColour("startColour"));
                    } else if (m.isFinish((col-1)/2, (row-1)/2)) {
                        panel.setBackground(pref.getColour("finishColour"));
                    }
                }
                gamePanel.add(panel, panelConstraints);
            }
        }
        gamePanel.setPreferredSize(gamePanel.getSize());
    }

    public void drawTitlePanel() {
        titlePanel.removeAll();
        titlePanel.setBackground(pref.getColour("titleDefaultColour"));
        titlePanel.setPreferredSize(new Dimension(300, 50));
        
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
        
        Integer[] sizes = new Integer[]{4,5,6,7,8,9,10};

        JComboBox<Integer> widthSelect;
        widthSelect= new JComboBox<>(sizes);

        JComboBox<Integer> heightSelect;
        heightSelect = new JComboBox<>(sizes);
        
        JButton playButton = new JButton("New Maze");
        playButton.setMnemonic(KeyEvent.VK_N);
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                JComboBox<Integer> box = (JComboBox<Integer>)menuPanel.getComponent(1);
                int width = (int)box.getSelectedItem();

                box = (JComboBox<Integer>)menuPanel.getComponent(3);
                int height = (int)box.getSelectedItem();
                
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
        menuPanel.add(widthSelect);
        menuPanel.add(new JLabel("Height"));
        menuPanel.add(heightSelect);
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