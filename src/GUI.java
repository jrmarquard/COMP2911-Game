import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
    Queue<Message> messages;
    AppState appState;
    JPanel windowPanel;
    MazePuzzleGame manager;
    
    public GUI (MazePuzzleGame manager, Preferences pref, Game game) {
        this.manager = manager;
        this.pref = pref;
        this.game = game;
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                initUI();
                refresh();
            }
        });
    }
    
    private void refresh() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                draw();
            }
        });
    }
    
    
    private void initUI() {
        /* Any layout information that should never be changed
         * should be contained in here. Anything that can be redrawn
         * must be added into the draw*() functions. 
         */
        
        // Defaults to display the main menu first
        appState = AppState.MENU;
        sendMessage(new Message(Message.SOUND_MSG, new String[]{"loop", "menu"}));
        
        // windowPanel is the root panel within this object
        windowPanel = new JPanel();
        windowPanel.setPreferredSize(new Dimension(560, 640));
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
                    sendMessage(new Message(Message.GAME_MSG, message));
                }
                
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ESCAPE:   setAppState(AppState.MENU);                 break;
                }
            }
        });
        
        // Exits from the main thread
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent){
                sendMessage(new Message(Message.EXIT));
            }
        });
    }
    
    private void draw() {
        // Reset game panels, remove them (hopefully clears memory)
        windowPanel.removeAll();
        windowPanel.revalidate();
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
            case EXIT:      sendMessage(new Message(Message.EXIT));
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
     * drawMenu displays the menu screen.
     */
    private void drawMenu() {
        windowPanel.setLayout(new BoxLayout(windowPanel, BoxLayout.PAGE_AXIS));

        String pack = pref.getText("texturePack");
        
        if (pack.equals("castle")) windowPanel.setBackground(new Color(66, 132, 157));
        else if (pack.equals("desert")) windowPanel.setBackground(new Color(255, 188, 131));
        else if (pack.equals("space")) windowPanel.setBackground(new Color(175, 175, 175));
        
        Icon start = new ImageIcon("Images/"+pack+"/startGame.png");
        Icon settings = new ImageIcon("Images/"+pack+"/settings.png");
        Icon about = new ImageIcon("Images/"+pack+"/about.png");
        Icon quit = new ImageIcon("Images/"+pack+"/quit.png");
        Icon title = new ImageIcon("Images/"+pack+"/title.gif");
        
        ImagePanel buttonBackground = new ImagePanel(new ImageIcon("Images/"+pack+"/background.png").getImage());
        buttonBackground.setLayout(new BoxLayout(buttonBackground, BoxLayout.PAGE_AXIS));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Button will start a new game
        JButton startGameButton = new JButton(start);
        startGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startGameButton.setBorderPainted(false);
        startGameButton.setFocusPainted(false);
        startGameButton.setContentAreaFilled(false);
        startGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAppState(AppState.GAME_INIT);
                sendMessage(new Message(Message.SOUND_MSG, new String[]{"play", "click"}));
            }
        });

        // Button will go to settings
        JButton settingsButton = new JButton(settings);
        settingsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingsButton.setBorderPainted(false);
        settingsButton.setFocusPainted(false);
        settingsButton.setContentAreaFilled(false);
        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAppState(AppState.SETTINGS);
                sendMessage(new Message(Message.SOUND_MSG, new String[]{"play", "click"}));
            }
        });
        
        // Button will go to settings
        JButton aboutButton = new JButton(about);
        aboutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        aboutButton.setBorderPainted(false);
        aboutButton.setFocusPainted(false);
        aboutButton.setContentAreaFilled(false);
        aboutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAppState(AppState.ABOUT);
                sendMessage(new Message(Message.SOUND_MSG, new String[]{"play", "click"}));
            }
        });
        
        // Button will quit the game
        JButton exitButton = new JButton(quit);
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.setBorderPainted(false);
        exitButton.setFocusPainted(false);
        exitButton.setContentAreaFilled(false);
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage(new Message(Message.SOUND_MSG, new String[]{"stop", "menu"}));
                setAppState(AppState.EXIT);
            }
        });

        buttonBackground.add(Box.createRigidArea(new Dimension(0,70)));
        buttonBackground.add(titleLabel);
        buttonBackground.add(Box.createRigidArea(new Dimension(0,40)));
        buttonBackground.add(startGameButton);
        buttonBackground.add(Box.createRigidArea(new Dimension(0,15)));
        buttonBackground.add(settingsButton);
        buttonBackground.add(Box.createRigidArea(new Dimension(0,15)));
        buttonBackground.add(aboutButton);
        buttonBackground.add(Box.createRigidArea(new Dimension(0,15)));
        buttonBackground.add(exitButton);
        
        windowPanel.add(buttonBackground);
    }
    
    /**
     * drawGameInit shows the screen before starting a game, allowing you to select various
     * options for the game.
     */
    private void drawGameInit() {
        windowPanel.setLayout(new BoxLayout(windowPanel, BoxLayout.Y_AXIS));
        
        // Navigation panel across the top of the screen.
        //JPanel navPanelTop = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        //navPanelTop.setBackground(windowPanel.getBackground().darker());
        //windowPanel.add(navPanelTop);
        
        String pack = pref.getText("texturePack");
        
        // Settings Panel Layout Begin
        ImagePanel gameSettingsPanel = new ImagePanel(new ImageIcon("Images/"+pack+"/settingsBackground.png").getImage());
       
        gameSettingsPanel.setLayout(new GridBagLayout());
        windowPanel.add(gameSettingsPanel);
        
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,0,6,0);
        
        // Add two columns on the sides
        JPanel blankColumnLeft = new JPanel();
        JPanel blankColumnRight = new JPanel();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 5;
        gameSettingsPanel.add(blankColumnLeft, c);
        c.gridx = 3;
        gameSettingsPanel.add(blankColumnRight, c);

        String gameMode = pref.getText("gameMode");

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
         * Start building the settings list
         * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
         */
        c.weightx = 1;
        int row = 0;

        // Door and key generation option       
        JCheckBox doorAndKey = new JCheckBox("", pref.getBool("doorAndKey"));
        doorAndKey.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	sendMessage(new Message(Message.SOUND_MSG, new String[]{"play", "click"}));
                pref.toggleBool("doorAndKey");
            }
        });
        
        c.gridx = 1;
        c.gridy = row++;
        c.anchor = GridBagConstraints.WEST;
        gameSettingsPanel.add(new JLabel("Generate door and key?"), c);
        c.gridx = 2;
        c.anchor = GridBagConstraints.EAST;
        gameSettingsPanel.add(doorAndKey, c);
        
        // Enemy generation option
        JCheckBox enemy = new JCheckBox("", pref.getBool("enemy"));
        enemy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage(new Message(Message.SOUND_MSG, new String[]{"play", "click"}));
                pref.toggleBool("enemy");
            }
        });

        c.gridx = 1;
        c.gridy = row++;
        c.anchor = GridBagConstraints.WEST;
        gameSettingsPanel.add(new JLabel("Enemy in maze?"), c);
        c.gridx = 2;
        c.anchor = GridBagConstraints.EAST;
        gameSettingsPanel.add(enemy, c);
        
        // Visibility selection
        String[] visModes = new String[]{"Off", "Low", "Med", "High"};
        JComboBox<String> visSelection = new JComboBox<String>(visModes);
        int vis = pref.getValue("visibleRange");
        switch (vis) {
            case 3: visSelection.setSelectedItem("Low");     break;
            case 6: visSelection.setSelectedItem("Med");     break;
            case 10: visSelection.setSelectedItem("High");   break;
            default:  visSelection.setSelectedItem("Off");    break;
        }
        
        visSelection.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (ItemEvent.SELECTED == e.getStateChange()) {
                    sendMessage(new Message(Message.SOUND_MSG, new String[]{"play", "click"}));
                    switch ((String)e.getItem()) {
                        case "Off": pref.setPreference("value.visibleRange=-1");    break;
                        case "Low": pref.setPreference("value.visibleRange=3");     break;
                        case "Med": pref.setPreference("value.visibleRange=6");     break;
                        case "High": pref.setPreference("value.visibleRange=10");   break;
                    }
                    refresh();
                }
            }
        });
        gameSettingsPanel.add(visSelection, c);
        
        c.gridx = 1;
        c.gridy = row++;
        c.anchor = GridBagConstraints.WEST;
        gameSettingsPanel.add(new JLabel("Visibility"), c);
        c.gridx = 2;
        c.anchor = GridBagConstraints.EAST;
        gameSettingsPanel.add(visSelection, c);
        
        JFormattedTextField widthSize = new JFormattedTextField();
        widthSize.setValue(Integer.toString(pref.getValue("defaultMapWidth")));
        widthSize.setColumns(2);
        widthSize.getDocument().addDocumentListener(new PrefUpdate("value", "defaultMapWidth"));
        
        c.gridx = 1;
        c.gridy = row++;
        c.anchor = GridBagConstraints.WEST;
        gameSettingsPanel.add(new JLabel("Width"), c);
        c.gridx = 2;
        c.anchor = GridBagConstraints.EAST;
        gameSettingsPanel.add(widthSize, c);

        JFormattedTextField heightSize = new JFormattedTextField();
        heightSize.setValue(Integer.toString(pref.getValue("defaultMapHeight")));
        heightSize.setColumns(2);
        heightSize.getDocument().addDocumentListener(new PrefUpdate("value", "defaultMapHeight"));
        c.gridx = 1;
        c.gridy = row++;
        c.anchor = GridBagConstraints.WEST;
        gameSettingsPanel.add(new JLabel("Height"), c);
        c.gridx = 2;
        c.anchor = GridBagConstraints.EAST;
        gameSettingsPanel.add(heightSize, c);
                
        // Gamemode selection 
        c.gridx = 1;
        c.gridy = row++;
        c.anchor = GridBagConstraints.WEST;
        JLabel gameModeText = new JLabel();
        gameModeText.setText("Gamemode: ");
        gameSettingsPanel.add(gameModeText, c);

        c.gridx = 2;
        c.anchor = GridBagConstraints.EAST;
        String[] gameModes = new String[]{"Solve", "Race"};
        JComboBox<String> gameModeSelection = new JComboBox<String>(gameModes);
        gameModeSelection.setSelectedItem(gameMode);
        gameModeSelection.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (ItemEvent.SELECTED == e.getStateChange()) {
                	sendMessage(new Message(Message.SOUND_MSG, new String[]{"play", "click"}));
                    pref.setPreference("text.gameMode="+(String)e.getItem());
                    refresh();
                }
            }
        });
        gameSettingsPanel.add(gameModeSelection, c);

        String[] playerOptions = new String[]{"Human", "Easy AI", "Med AI", "Hard AI", "Off"};

        for (int x = 1; x <= 4; x++) {
            row++;
            c.gridx = 1;
            c.gridy = x+row;
            c.anchor = GridBagConstraints.WEST;
            gameSettingsPanel.add(new JLabel("Player "+x+": "), c);
            c.gridx = 2;
            c.anchor = GridBagConstraints.EAST;
            gameSettingsPanel.add(new PlayerOptions(playerOptions, "player"+x), c);      
        }
        // turned off gamemode selection changing the avaiable options for the moment
        /*
        if (gameMode.equals("Solve")) {
            c.gridx = 1;
            c.gridy = 5;
            c.anchor = GridBagConstraints.WEST;
            gameSettingsPanel.add(new JLabel("Player 1: "), c);
            c.gridx = 2;
            c.anchor = GridBagConstraints.EAST;
            gameSettingsPanel.add(new PlayerOptions(playerOptions, "player1"), c);
        } else if (gameMode.equals("Race")) {
        }
        */
        // Button should always be at the bottom, so it's y is 99. For some reason
        // using row as with the rest of the buttons did not work
        c.gridy = 99;
        c.gridx = 1;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        JClickButton startGameButton = new JClickButton("Start Game");
        startGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage(new Message(Message.SOUND_MSG, new String[]{"stop", "menu"}));
                sendMessage(new Message(Message.SOUND_MSG, new String[]{"loop", "background"}));
                sendMessage(new Message(Message.GAME_MSG, new String[]{"newGame"}));
                setAppState(AppState.GAME);
            }
        });
        gameSettingsPanel.add(startGameButton, c);
        
        c.gridy = 100;
        JClickButton backButton = new JClickButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAppState(AppState.MENU);
            }
        });
        gameSettingsPanel.add(backButton, c);

        
        windowPanel.add(gameSettingsPanel);
        // Settings Panel Layout End        
    }
    
    /**
     * drawAbout shows the about screen, with information on the game and it's authors. 
     */
    private void drawAbout() {
        windowPanel.setLayout(new BoxLayout(windowPanel, BoxLayout.Y_AXIS));
        
        // Navigation panel across the top of the screen.
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        navPanel.setBackground(windowPanel.getBackground().darker());
        windowPanel.add(navPanel);
        
        JClickButton backButton = new JClickButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAppState(AppState.MENU);
            }
        });
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
    
    /**
     * drawsettings allows you to change non-gameplay settings like sound and graphics.
     */
    private void drawSettings() {
        windowPanel.setLayout(new BoxLayout(windowPanel, BoxLayout.Y_AXIS));
        
        // Navigation panel across the top of the screen.
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        navPanel.setBackground(windowPanel.getBackground().darker());
        windowPanel.add(navPanel);
        
        JClickButton resetButton = new JClickButton("Reset to defaults");
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pref.loadPreferences();
                setAppState(AppState.SETTINGS);
            }
        });
        JClickButton backButton = new JClickButton("Back");
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
            JPanel settingColour = new JPanel();
            
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
        
        JPanel textureSelectPanel = new JPanel();
        JLabel textureSelectLabel = new JLabel("Select texture: ");
        JRadioButton textureCastle = new JRadioButton("Castle");
        textureCastle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage(new Message(Message.SOUND_MSG, new String[]{"play", "click"}));
                pref.setPreference("text.texturePack=castle");
            }
        });
        JRadioButton textureDesert = new JRadioButton("Desert");
        textureDesert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage(new Message(Message.SOUND_MSG, new String[]{"play", "click"}));
                pref.setPreference("text.texturePack=desert");
            }
        });
        JRadioButton textureSpace = new JRadioButton("Space");
        textureSpace.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage(new Message(Message.SOUND_MSG, new String[]{"play", "click"}));
                pref.setPreference("text.texturePack=space");
            }
        });
        ButtonGroup group = new ButtonGroup();
        group.add(textureCastle);
        group.add(textureDesert);
        group.add(textureSpace);
        textureSelectPanel.add(textureSelectLabel);
        textureSelectPanel.add(textureCastle);
        textureSelectPanel.add(textureDesert);
        textureSelectPanel.add(textureSpace);
        windowPanel.add(textureSelectPanel);
        
        JPanel volSliderPanel = new JPanel();
        JLabel volSliderLabel = new JLabel("Master Volume: ");
        JSlider volSlider = new JSlider(0, 100, pref.getValue("masterVolume"));
        volSlider.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                int newVolume = (int)source.getValue();
                pref.setPreference("value.masterVolume="+newVolume);
                sendMessage(new Message(Message.SOUND_MSG, new String[]{"changeVolume"}));
                
            }
        });
        volSliderPanel.add(volSliderLabel);
        volSliderPanel.add(volSlider);
        windowPanel.add(volSliderPanel);
        
        windowPanel.add(Box.createRigidArea(new Dimension(0,100)));
        
    }
    
    /**
     * drawGame shows the game.
     */
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
        gameMenuPanel.setBackground(pref.getColour("menuColour"));
        
        JClickButton closeButton = new JClickButton("Exit to menu");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage(new Message(Message.GAME_MSG, new String[]{"endGame"}));
                sendMessage(new Message(Message.SOUND_MSG, new String[]{"stop", "background"}));
                sendMessage(new Message(Message.SOUND_MSG, new String[]{"loop", "menu"}));
                setAppState(AppState.MENU);
            }
        });
        gameMenuPanel.add(closeButton);

        windowPanel.setPreferredSize(windowPanel.getSize());
    }
    
    private void sendMessage(Message c) {
        manager.sendMessage(c);
    }
    private void setAppState(AppState s) {
        appState = s;
        refresh();
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
                        sendMessage(new Message(Message.SOUND_MSG, new String[]{"play", "click"}));
                    }
                }
            });
        }
    }
    
    /**
     * Exactly the same as a normal button but it sends a click command
     * to the sound engine.
     * 
     */
    private class JClickButton extends JButton {
        public JClickButton (String s) {
            super(s);
            this.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    sendMessage(new Message(Message.SOUND_MSG, new String[]{"play", "click"}));
                }
            });
        }
    }
    
    /**
     * Class used to add a background image to a JPanel
     * @author Tyler
     */
    class ImagePanel extends JPanel {

    	  private Image img;

    	  public ImagePanel(String img) {
    	    this(new ImageIcon(img).getImage());
    	  }

    	  public ImagePanel(Image img) {
    	    this.img = img;
    	    Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
    	    setPreferredSize(size);
    	    setMinimumSize(size);
    	    setMaximumSize(size);
    	    setSize(size);
    	    setLayout(null);
    	  }

    	  public void paintComponent(Graphics g) {
    	    g.drawImage(img, 0, 0, null);
    	  }

    	}
    
}
