import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import java.util.Queue;

@SuppressWarnings("serial")
public class GUI extends JFrame implements DisplayInterface {
    
    enum AppState {
        /* Menu displays the main menu */
        MENU, 
        
        /* Game displays the game state, this can currently
         * be several came states, will likely seperate this into
         * different game modes
         */
        GAME, 
        
        /* Setting state allows user to change settings like
         * controls and colours.
         */
        SETTINGS, 
        
        /* About displays information about the game. */
        ABOUT, 
        
        /* Unused at the moment, but left in just because it's another state */
        EXIT
    }
    
    Preferences pref;
    MazeWorld world;
    Queue<Command> commands;
    AppState appState;
    
    JPanel windowPanel;
    
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
    
    private void initUI() {
        /* Any layout information that should never be changed
         * should be contained in here. Anything that can be redrawn
         * must be added into the draw*() functions. 
         */
        
        // Defaults to display the main menu first
        this.appState = AppState.MENU; 
        
        // windowPanel is the root panel within this object
        windowPanel = new JPanel();
        windowPanel.setPreferredSize(new Dimension(600, 600));
        this.add(windowPanel);
        
        // Set more information
        setTitle(pref.getText("appName"));
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        
        // Register a keystroke
        this.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
	                case KeyEvent.VK_DOWN:      addCommand(new Command(Com.ARROW_DOWN));    break;
	                case KeyEvent.VK_LEFT:      addCommand(new Command(Com.ARROW_LEFT));    break;
	                case KeyEvent.VK_RIGHT:     addCommand(new Command(Com.ARROW_RIGHT));   break;
	                case KeyEvent.VK_UP:        addCommand(new Command(Com.ARROW_UP));      break;
	                case KeyEvent.VK_W:         addCommand(new Command(Com.W_UP));          break;
	                case KeyEvent.VK_A:         addCommand(new Command(Com.A_LEFT));        break;
	                case KeyEvent.VK_S:         addCommand(new Command(Com.S_DOWN));        break;
	                case KeyEvent.VK_D:         addCommand(new Command(Com.D_RIGHT));       break;
                    case KeyEvent.VK_C:         addCommand(new Command(Com.SOLVE));         break;
                    case KeyEvent.VK_ESCAPE:    setAppState(AppState.MENU);                 break; 
                    case KeyEvent.VK_N:         newGame(1);                                  break;
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
    
    public void update() {
        System.out.println("Updating display");

        // Reset game panels, remove them (hopefully clears memory)switch (state)
        windowPanel.removeAll();
        windowPanel.repaint();

        // Draws whatever mode the GUI is currently in
        switch(appState) {
            case MENU:      drawMenu();         
                            break;
            case GAME:      drawGame();         
                            break;
            case SETTINGS:  drawSettings();     
                            break;
            case ABOUT:     drawAbout();        
                            break;
            case EXIT:      addCommand(new Command(Com.EXIT));
                            break;
        }
        
        // Refocuses the window so keystrokes are registered
        setFocusable(true);
        
        // Will retain window size when switching between menus
        windowPanel.setPreferredSize(windowPanel.getSize());
        
        // Packs
        pack();
    }
    
    
    /**
     * drawMenu 
     */
    private void drawMenu() {
        System.out.println("Drawing menu");
        windowPanel.setLayout(new BoxLayout(windowPanel, BoxLayout.Y_AXIS));

        // Button will start a new game
        JButton startGameButton = new JButton("Play");
        startGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newGame(1);
                setAppState(AppState.GAME);
            }
        });

        // Button will go to settings
        JButton settingsButton = new JButton("Settings");
        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAppState(AppState.SETTINGS);
            }
        });
        
        // Button will go to settings
        JButton aboutButton = new JButton("About");
        aboutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAppState(AppState.ABOUT);
            }
        });
        // Button will quit the game
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAppState(AppState.EXIT);
            }
        });

        windowPanel.add(startGameButton);
        windowPanel.add(settingsButton);
        windowPanel.add(aboutButton);
        windowPanel.add(exitButton);
    }
    
    /**
     *  
     */
    private void drawAbout() {
        windowPanel.setLayout(new BoxLayout(windowPanel, BoxLayout.Y_AXIS));
        
        // Navigation panel across the top of the screen.
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        navPanel.setBackground(windowPanel.getBackground().darker());
        windowPanel.add(navPanel);
        
        JButton resetButton = new JButton("Reset to defaults");
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pref.loadPreferences();
                setAppState(AppState.SETTINGS);
            }
        });
        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAppState(AppState.MENU);
            }
        });
        navPanel.add(resetButton);
        navPanel.add(backButton);
        
        JPanel aboutTextPanel = new JPanel() {
                @Override
                public Dimension getPreferredSize() {
                    Dimension d = this.getParent().getSize();
                    return new Dimension(d.height,d.height);
                }
            };
        aboutTextPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JTextField aboutText = new JTextField();
        aboutText.setMargin(new Insets(10,10,10,10));
        aboutText.setText("Game written by: John, Joshua, Patrick, Tim, Tyler");
        aboutTextPanel.add(aboutText);
        
        windowPanel.add(aboutTextPanel);
    }
    
    private void drawSettings() {
        windowPanel.setLayout(new BoxLayout(windowPanel, BoxLayout.Y_AXIS));
        
        // Navigation panel across the top of the screen.
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        navPanel.setBackground(windowPanel.getBackground().darker());
        windowPanel.add(navPanel);
        
        JButton resetButton = new JButton("Reset to defaults");
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pref.loadPreferences();
                setAppState(AppState.SETTINGS);
            }
        });
        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAppState(AppState.MENU);
            }
        });
        navPanel.add(resetButton);
        navPanel.add(backButton);
        
        // Settings panel to display all settings
        JPanel settingsPanel = new JPanel(new GridLayout(10,1)) {
            @Override
            public Dimension getPreferredSize() {
                Dimension d = this.getParent().getSize();        
                int windowHeight = d.height;
                int windowWidth = d.width;
                return new Dimension(windowWidth,windowHeight);
            }
        };
        windowPanel.add(settingsPanel);
        
        for (String s : pref.getKeys("colour")) {
            Color c = pref.getColour(s);
            String red = String.format("%02X",c.getRed());
            String green = String.format("%02X",c.getGreen());
            String blue = String.format("%02X",c.getBlue());
            String value = red+green+blue;
            
            // Create row for setting
            JPanel settingRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JLabel settingName = new JLabel();
            JTextField settingValue = new JTextField();
            JPanel settingColour = new JPanel() {
//                @Override
//                public Dimension getPreferredSize() {
//                    Dimension d = this.getParent().getSize();
//                    return new Dimension(d.height,d.height);
//                }
            };
            
            settingRow.setBorder(BorderFactory.createLineBorder(Color.black));
            settingValue.setColumns(6);
            settingValue.getDocument().addDocumentListener(new PrefUpdate("colour", s));
            settingColour.setBorder(BorderFactory.createLineBorder(Color.black));
            
            // Display
            settingName.setText(s);
            settingValue.setText(value);
            settingValue.setColumns(value.length());
            settingColour.setBackground(c);
            
            // Add to parent panels
            settingRow.add(settingName);
            settingRow.add(settingValue);
            settingRow.add(settingColour);
            settingsPanel.add(settingRow);
        }
    }
    
    private void drawGame() {
        System.out.println("Drawing game");
        windowPanel.setLayout(new BoxLayout(windowPanel, BoxLayout.Y_AXIS));
        
        // This recreates the panels EVERYTIME the game is drawn.
        // Need to update this to something that is halfway efficient
        JPanel titlePanel = new JPanel();
        JPanel gameMainPanel = new JPanel();
        JPanel gamePanelA = new JPanel();
        JPanel gamePanelB = new JPanel();
        JPanel gameMenuPanel = new JPanel();
        
        // Sets the layout for each component
        titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        gameMainPanel.setLayout(new BoxLayout(gameMainPanel, BoxLayout.X_AXIS));
        gamePanelA.setLayout(new GridBagLayout());
        gamePanelB.setLayout(new GridBagLayout());
        gameMenuPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        
        if (world.getWinStatus()) {
            titlePanel.setBackground(pref.getColour("titleWinColour"));
        } else {
            titlePanel.setBackground(pref.getColour("titleDefaultColour"));
        }
        JLabel title = new JLabel();
        title.setText("Coins: "+world.getPlayerCoins(0));
        titlePanel.add(title);
        
        // Creates the GameMap for the world to be draw onto
        GameMap innerGamePanelA = new GameMap(world, pref, 0);
        
        // Attaches the innerGamePanel onto the gamePanel
        gamePanelA.add(innerGamePanelA);
        gameMainPanel.add(gamePanelA);  
              
        if (world.getIsMultiplayer()) {
        	gameMainPanel.add(gamePanelB);
        	GameMap innerGamePanelB = new GameMap(world, pref, 1);
        	gamePanelB.add(innerGamePanelB);
        }
        
        windowPanel.add(titlePanel);
        windowPanel.add(gameMainPanel);
        windowPanel.add(gameMenuPanel);
        
        drawGameMenuPanel(gameMenuPanel);
        
        windowPanel.setPreferredSize(windowPanel.getSize());
    }
    
    private void drawGameMenuPanel(JPanel gameMenuPanel) {
        gameMenuPanel.removeAll();
        gameMenuPanel.setBackground(pref.getColour("menuColour"));

        JFormattedTextField widthSize = new JFormattedTextField();
        widthSize.setValue(Integer.toString(pref.getValue("defaultMapWidth")));
        widthSize.setColumns(2);
        widthSize.getDocument().addDocumentListener(new PrefUpdate("value", "defaultMapWidth"));
        
        JFormattedTextField heightSize = new JFormattedTextField();
        heightSize.setValue(Integer.toString(pref.getValue("defaultMapHeight")));
        heightSize.setColumns(2);
        heightSize.getDocument().addDocumentListener(new PrefUpdate("value", "defaultMapHeight"));
        
        
        JCheckBox auto = new JCheckBox("Auto", pref.getBool("autoComplete"));
        auto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pref.toggleBool("autoComplete");
                if (pref.getBool("autoComplete")) {
                    addCommand(new Command(Com.SOLVE));
                }
            }
        });
        
        final JPopupMenu newMazePopup = new JPopupMenu();
        newMazePopup.add(new JMenuItem(new AbstractAction("Single Player") {
        	public void actionPerformed(ActionEvent event) {
        	    newGame(1);
            }
        }));
        newMazePopup.add(new JMenuItem(new AbstractAction("Two Players") {
        	public void actionPerformed(ActionEvent event) {
        	    newGame(2);
            }
        }));
        
        JButton newMazeButton = new JButton("New Maze");
        newMazeButton.addMouseListener(new MouseAdapter() {
        	public void mousePressed(MouseEvent e) {
                newMazePopup.show(e.getComponent(), e.getX(), e.getY());
            }
        });
        
        JButton closeButton = new JButton("Exit to menu");
        closeButton.setMnemonic(KeyEvent.VK_W);
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAppState(AppState.MENU);
            }
        });
        gameMenuPanel.add(new JLabel("Width"));
        gameMenuPanel.add(widthSize);
        gameMenuPanel.add(new JLabel("Height"));
        gameMenuPanel.add(heightSize);
        if(!world.getIsMultiplayer()) {
            gameMenuPanel.add(auto);
        }
        gameMenuPanel.add(newMazeButton);
        gameMenuPanel.add(closeButton);
    }
    
    /**
     * Creates a new game for the specified number of players
     * 
     * @param numPlayers 1 or 2, nothing else
     */
    private void newGame(int numPlayers) {
        int width = pref.getValue("defaultMapWidth");
        int height = pref.getValue("defaultMapHeight");
        addCommand(new CommandMap(Com.NEW_MAP, width, height, numPlayers));
        setAppState(AppState.GAME);
    }
    
    private void addCommand(Command c) {
        commands.add(c);
    }
    private void setAppState(AppState s) {
        appState = s;
        addCommand(new Command(Com.DRAW));
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
    
    private class PrefUpdate implements DocumentListener {
        String spaceName;
        String prefName;
        
        public PrefUpdate(String s, String p) {
            this.spaceName = s;
            this.prefName = p;
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            // update(e);
        }
        @Override
        public void insertUpdate(DocumentEvent e) {
            update(e);    
        }
        @Override
        public void removeUpdate(DocumentEvent e) {
            // update(e);
        }
        private void update(DocumentEvent e) {
            try {
                String value = "";
                int textLength = e.getDocument().getLength();
                switch(spaceName) {
                    case "value":
                        if (textLength == 1 || textLength == 2)  {
                            value = e.getDocument().getText(0,textLength);
                            pref.setPreference(spaceName+"."+prefName+"="+value);
                        }
                        break;
                    case "colour":
                        if (textLength == 6) {
                            value = e.getDocument().getText(0,textLength);
                            pref.setPreference(spaceName+"."+prefName+"="+value);
                        }
                        break;
                    default:
                        break;
                }
            } catch (NumberFormatException | BadLocationException e1) {
                // Do nothing
            }
        }
    }
    
}