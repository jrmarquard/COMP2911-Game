import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.util.Queue;
import java.util.Random;

@SuppressWarnings("serial")
public class GUI_2 extends JFrame implements DisplayInterface {
    
    Preferences pref;
    MazeWorld world;
    Queue<Command> commands;
    
    JPanel windowPanel;
    JPanel titlePanel;
    JPanel gamePanel;
    JPanel menuPanel;
    
    public GUI_2 (Preferences pref, MazeWorld world, Queue<Command> commands) {
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
        titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        gamePanel = new JPanel(new GridBagLayout());
        gamePanel.setPreferredSize(new Dimension(600, 600));
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
        // Reset game panels, remove them (hopefully clears memory)
        gamePanel.removeAll();
        
        GameMap innerGamePanel = new GameMap(world);
        innerGamePanel.setLayout(new GridBagLayout());
        innerGamePanel.setColour("wallColour", pref.getColour("wallColour"));
        innerGamePanel.setColour("floorColour", pref.getColour("floorColour"));
        innerGamePanel.setColour("startColour", pref.getColour("startColour"));
        innerGamePanel.setColour("finishColour", pref.getColour("finishColour"));
        innerGamePanel.setColour("playerColour", pref.getColour("playerColour"));
        innerGamePanel.setColour("coinColour", pref.getColour("coinColour"));
        
        gamePanel.add(innerGamePanel);
        gamePanel.setPreferredSize(gamePanel.getSize());
    }

    public void drawTitlePanel() {
        titlePanel.removeAll();
        
        if (world.getWinStatus()) {
            titlePanel.setBackground(pref.getColour("titleWinColour"));
        } else {
            titlePanel.setBackground(pref.getColour("titleDefaultColour"));
        }
        
        JLabel title = new JLabel();
        title.setText("Coins: "+world.getPlayerCoins());
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

        JButton newMazeButton = new JButton("New Maze");
        newMazeButton.setMnemonic(KeyEvent.VK_N);
        newMazeButton.addActionListener(new ActionListener() {
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
        JCheckBox auto = new JCheckBox("Auto", pref.getBool("autoComplete"));
        auto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                pref.toggleBool("autoComplete");
                if (pref.getBool("autoComplete")) {
                    // if the preference is now on, solve the maze
                    addCommand(new Command(Com.SOLVE));
                }
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
        JButton closeButton = new JButton("Exit");
        closeButton.setMnemonic(KeyEvent.VK_W);
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCommand(new Command(Com.EXIT));
            }
        });
        menuPanel.add(new JLabel("Width"));
        menuPanel.add(widthSize);
        menuPanel.add(new JLabel("Height"));
        menuPanel.add(heightSize);
        menuPanel.add(solveButton);
        menuPanel.add(auto);
        menuPanel.add(newMazeButton);
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

