import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

@SuppressWarnings("serial")
public class GUI extends JFrame  {
    
    enum AppState {
        /* Menu displays the main menu */
        MENU, 
        
        /* Screen that displays settings for creating a new game */
        GAME_INIT,
        
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
    Game game;
    Queue<Command> commands;
    AppState appState;
    
    JPanel windowPanel;
    
    public GUI (Preferences pref, Game game, Queue<Command> commands) {
        this.pref = pref;
        this.game = game;
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
        setAppState(AppState.MENU); 
        
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
                int keyPressed = e.getKeyCode();
                
                Map<Integer, String[]> controls = new HashMap<Integer, String[]>();
                // Player 1
                controls.put(KeyEvent.VK_UP, new String[]{"move", "world1", "Moneymaker", "up"});
                controls.put(KeyEvent.VK_LEFT, new String[]{"move", "world1", "Moneymaker", "left"});
                controls.put(KeyEvent.VK_DOWN, new String[]{"move", "world1", "Moneymaker", "down"});
                controls.put(KeyEvent.VK_RIGHT, new String[]{"move", "world1", "Moneymaker", "right"});
                // Player 2
                controls.put(KeyEvent.VK_W, new String[]{"move", "world2", "Moneymaker", "up"});
                controls.put(KeyEvent.VK_A, new String[]{"move", "world2", "Moneymaker", "left"});
                controls.put(KeyEvent.VK_S, new String[]{"move", "world2", "Moneymaker", "down"});
                controls.put(KeyEvent.VK_D, new String[]{"move", "world2", "Moneymaker", "right"});
                // Player 3
                controls.put(KeyEvent.VK_T, new String[]{"move", "world3", "Moneymaker", "up"});
                controls.put(KeyEvent.VK_F, new String[]{"move", "world3", "Moneymaker", "left"});
                controls.put(KeyEvent.VK_G, new String[]{"move", "world3", "Moneymaker", "down"});
                controls.put(KeyEvent.VK_H, new String[]{"move", "world3", "Moneymaker", "right"});
                // Player 4
                controls.put(KeyEvent.VK_I, new String[]{"move", "world4", "Moneymaker", "up"});
                controls.put(KeyEvent.VK_J, new String[]{"move", "world4", "Moneymaker", "left"});
                controls.put(KeyEvent.VK_K, new String[]{"move", "world4", "Moneymaker", "down"});
                controls.put(KeyEvent.VK_L, new String[]{"move", "world4", "Moneymaker", "right"});
                
                String[] message = controls.get(keyPressed);
                if (message != null) {
                    addCommand(new Command(Com.GAME_MESSAGE, message));
                }
                
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ESCAPE:   setAppState(AppState.MENU);                 break; 
                    case KeyEvent.VK_N:        newGame();                                 break;
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
        // Reset game panels, remove them (hopefully clears memory)
        windowPanel.removeAll();
        windowPanel.repaint();

        // Draws whatever mode the GUI is currently in
        switch(appState) {
            case MENU:      drawMenu();         
                            break;
            case GAME_INIT: drawGameInit();         
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
        windowPanel.setLayout(new BoxLayout(windowPanel, BoxLayout.Y_AXIS));

        // Button will start a new game
        JButton startGameButton = new JButton("Play");
        startGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAppState(AppState.GAME_INIT);
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
    
    private void drawGameInit() {
        windowPanel.setLayout(new BoxLayout(windowPanel, BoxLayout.Y_AXIS));
        
        // Navigation panel across the top of the screen.
        JPanel navPanelTop = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        navPanelTop.setBackground(windowPanel.getBackground().darker());
        windowPanel.add(navPanelTop);
        
        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAppState(AppState.MENU);
            }
        });
        navPanelTop.add(backButton);
        
        // Settings Panel Layout Begin
        JPanel settingsPanel = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                Dimension d = this.getParent().getSize();
                return new Dimension(d.height,d.height);
            }
        };
        settingsPanel.setLayout(new GridBagLayout());
        windowPanel.add(settingsPanel);
        
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,0,6,0);
        
        // Add two columsn on the sides
        JPanel blankColumnLeft = new JPanel();
        JPanel blankColumnRight = new JPanel();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 5;
        settingsPanel.add(blankColumnLeft, c);
        c.gridx = 3;
        settingsPanel.add(blankColumnRight, c);

        String gameMode = pref.getText("gameMode");

        // Start building the settings list
        c.weightx = 1;

        // Map size selection
        JFormattedTextField widthSize = new JFormattedTextField();
        widthSize.setValue(Integer.toString(pref.getValue("defaultMapWidth")));
        widthSize.setColumns(2);
        widthSize.getDocument().addDocumentListener(new PrefUpdate("value", "defaultMapWidth"));
        
        
        c.gridx = 1;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        settingsPanel.add(new JLabel("Width"), c);
        c.gridx = 2;
        c.anchor = GridBagConstraints.EAST;
        settingsPanel.add(widthSize, c);

        JFormattedTextField heightSize = new JFormattedTextField();
        heightSize.setValue(Integer.toString(pref.getValue("defaultMapHeight")));
        heightSize.setColumns(2);
        heightSize.getDocument().addDocumentListener(new PrefUpdate("value", "defaultMapHeight"));
        c.gridx = 1;
        c.gridy = 1;
        c.anchor = GridBagConstraints.WEST;
        settingsPanel.add(new JLabel("Height"), c);
        c.gridx = 2;
        c.anchor = GridBagConstraints.EAST;
        settingsPanel.add(heightSize, c);
        
        
        
        
        // Gamemode selection 
        c.gridx = 1;
        c.gridy = 2;
        c.anchor = GridBagConstraints.WEST;
        JLabel gameModeText = new JLabel();
        gameModeText.setText("Gamemode: ");
        settingsPanel.add(gameModeText, c);

        c.gridx = 2;
        c.anchor = GridBagConstraints.EAST;
        String[] gameModes = new String[]{"Solve", "Race"};
        JComboBox<String> gameModeSelection = new JComboBox<String>(gameModes);
        gameModeSelection.setSelectedItem(gameMode);
        gameModeSelection.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (ItemEvent.SELECTED == e.getStateChange()) {
                    pref.setPreference("text.gameMode="+(String)e.getItem());
                    addCommand(new Command(Com.DRAW));
                }
            }
        });
        settingsPanel.add(gameModeSelection, c);

        String[] playerOptions = new String[]{"Human", "Easy AI", "Med AI", "Hard AI", "Off"};
        
        if (gameMode.equals("Solve")) {
            c.gridx = 1;
            c.gridy = 3;
            c.anchor = GridBagConstraints.WEST;
            settingsPanel.add(new JLabel("Player 1: "), c);
            c.gridx = 2;
            c.anchor = GridBagConstraints.EAST;
            settingsPanel.add(new PlayerOptions(playerOptions, "player1"), c);
        } else if (gameMode.equals("Race")) {
            for (int x = 1; x <= 4; x++) {
                c.gridx = 1;
                c.gridy = x+4;
                c.anchor = GridBagConstraints.WEST;
                settingsPanel.add(new JLabel("Player "+x+": "), c);
                c.gridx = 2;
                c.anchor = GridBagConstraints.EAST;
                settingsPanel.add(new PlayerOptions(playerOptions, "player"+x), c);                
            }
        }
        
        c.gridy = 9;
        c.gridx = 1;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        JButton startGameButton = new JButton("Start Game");
        startGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAppState(AppState.GAME);
                newGame();
            }
        });
        settingsPanel.add(startGameButton, c);

        
        windowPanel.add(settingsPanel);
        // Settings Panel Layout End        
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
        windowPanel.setLayout(new BoxLayout(windowPanel, BoxLayout.Y_AXIS));
        
        // Setup layout of 3 main panels
        JPanel titlePanel = new JPanel();
        JPanel gamePanel = new JPanel();
        JPanel gameMenuPanel = new JPanel();       
        windowPanel.add(titlePanel);
        windowPanel.add(gamePanel);
        windowPanel.add(gameMenuPanel);

        // Title Panel
        titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        titlePanel.setBackground(pref.getColour("titleDefaultColour"));

        ArrayList<Integer> coins = game.getPlayerCoins();
        for (Integer i : coins) {
            JLabel text = new JLabel();
            text.setText("Coins: "+i);
            titlePanel.add(text);
            
        }
        
        // Game Panel        
        gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.Y_AXIS));
        
        JPanel gamePanelTop = new JPanel();
        gamePanelTop.setLayout(new BoxLayout(gamePanelTop, BoxLayout.X_AXIS));
        gamePanel.add(gamePanelTop);
        
        ArrayList<World> worlds = game.getWorlds();
        int numWorlds = worlds.size();
        
        if (numWorlds >= 1) {
            JPanel innerPanelA = new JPanel(new GridBagLayout());
            innerPanelA.add(new GameMap(worlds.get(0), pref));
            gamePanelTop.add(innerPanelA);
            if (numWorlds >= 2) {            
                JPanel innerPanelB = new JPanel(new GridBagLayout());
                innerPanelB.add(new GameMap(worlds.get(1), pref));
                gamePanelTop.add(innerPanelB);
                if (numWorlds >= 3) {    
                    JPanel gamePanelBot = new JPanel();
                    gamePanelBot.setLayout(new BoxLayout(gamePanelBot, BoxLayout.X_AXIS));
                    gamePanel.add(gamePanelBot);
                    
                    JPanel innerPanelC = new JPanel(new GridBagLayout());
                    innerPanelC.add(new GameMap(worlds.get(2), pref));
                    gamePanelBot.add(innerPanelC);
                    if (numWorlds == 4) {            
                        JPanel innerPanelD = new JPanel(new GridBagLayout());
                        innerPanelD.add(new GameMap(worlds.get(3), pref));
                        gamePanelBot.add(innerPanelD);
                    }
                }
            }
        }
        
        // Menu Panel
        gameMenuPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        drawGameMenuPanel(gameMenuPanel);

        windowPanel.setPreferredSize(windowPanel.getSize());
    }
    
    private void drawGameMenuPanel(JPanel gameMenuPanel) {
        gameMenuPanel.removeAll();
        gameMenuPanel.setBackground(pref.getColour("menuColour"));
        
        JButton closeButton = new JButton("Exit to menu");
        closeButton.setMnemonic(KeyEvent.VK_W);
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCommand(new Command(Com.GAME_MESSAGE, new String[]{"endGame"}));
                setAppState(AppState.MENU);
            }
        });
        gameMenuPanel.add(closeButton);
    }
    
    /**
     * Creates a new game for the specified number of players
     * 
     * @param numPlayers 1 or 2, nothing else
     */
    private void newGame() {
        addCommand(new Command(Com.GAME_MESSAGE, new String[]{"newGame"}));
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
    
    private class PrefUpdate implements DocumentListener {
        String spaceName;
        String prefName;
        
        public PrefUpdate(String s, String p) {
            this.spaceName = s;
            this.prefName = p;
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            update(e);
        }
        @Override
        public void insertUpdate(DocumentEvent e) {
            update(e);    
        }
        @Override
        public void removeUpdate(DocumentEvent e) {
            update(e);
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
    
    private class PlayerOptions extends JComboBox<String> {
        String setting;
        public PlayerOptions(String[] o, String s) {
            super(o);
            this.setting = s;
            setSelectedItem(pref.getText(s));
            addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (ItemEvent.SELECTED == e.getStateChange()) {
                        pref.setPreference("text."+setting+"="+(String)e.getItem());
                    }
                }
            });
        }
    }
    
}
