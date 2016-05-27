import java.awt.*;
import java.awt.event.*;
import java.io.FileReader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
    
    private Game game;
    private AppState appState;
    private JPanel windowPanel;
    private App manager;
    
    private Map<Integer, String[]> adventureControls;
    private Map<Integer, String[]> raceControls;
    private Map<Integer, String[]> battleControls;
    
    public GUI (App manager, Game game) {
        this.manager = manager;
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
        setTitle(App.pref.getText("appName"));
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        this.adventureControls = new HashMap<Integer, String[]>();
        this.raceControls = new HashMap<Integer, String[]>();
        this.battleControls = new HashMap<Integer, String[]>();
        
        // Add keystrokes to controls
        // Adventure gamemode
        adventureControls.put(KeyEvent.VK_UP, new String[]{"world1", "move", "Moneymaker", "up"});
        adventureControls.put(KeyEvent.VK_LEFT, new String[]{"world1", "move", "Moneymaker", "left"});
        adventureControls.put(KeyEvent.VK_DOWN, new String[]{"world1", "move", "Moneymaker", "down"});
        adventureControls.put(KeyEvent.VK_RIGHT, new String[]{"world1", "move", "Moneymaker", "right"});
        adventureControls.put(KeyEvent.VK_SPACE, new String[]{"world1", "melee", "Moneymaker"});
        adventureControls.put(KeyEvent.VK_W, new String[]{"world2", "move", "Moneymaker", "up"});
        adventureControls.put(KeyEvent.VK_A, new String[]{"world2", "move", "Moneymaker", "left"});
        adventureControls.put(KeyEvent.VK_S, new String[]{"world2", "move", "Moneymaker", "down"});
        adventureControls.put(KeyEvent.VK_D, new String[]{"world2", "move", "Moneymaker", "right"});
        adventureControls.put(KeyEvent.VK_SHIFT, new String[]{"world2", "melee", "Moneymaker", "right"});

        // Race gamemode
        raceControls.put(KeyEvent.VK_UP, new String[]{"world1", "move", "Moneymaker", "up"});
        raceControls.put(KeyEvent.VK_LEFT, new String[]{"world1", "move", "Moneymaker", "left"});
        raceControls.put(KeyEvent.VK_DOWN, new String[]{"world1", "move", "Moneymaker", "down"});
        raceControls.put(KeyEvent.VK_RIGHT, new String[]{"world1", "move", "Moneymaker", "right"});
        raceControls.put(KeyEvent.VK_W, new String[]{"world2", "move", "Moneymaker", "up"});
        raceControls.put(KeyEvent.VK_A, new String[]{"world2", "move", "Moneymaker", "left"});
        raceControls.put(KeyEvent.VK_S, new String[]{"world2", "move", "Moneymaker", "down"});
        raceControls.put(KeyEvent.VK_D, new String[]{"world2", "move", "Moneymaker", "right"});
        raceControls.put(KeyEvent.VK_T, new String[]{"world3", "move", "Moneymaker", "up"});
        raceControls.put(KeyEvent.VK_F, new String[]{"world3", "move", "Moneymaker", "left"});
        raceControls.put(KeyEvent.VK_G, new String[]{"world3", "move", "Moneymaker", "down"});
        raceControls.put(KeyEvent.VK_H, new String[]{"world3", "move", "Moneymaker", "right"});
        raceControls.put(KeyEvent.VK_I, new String[]{"world4", "move", "Moneymaker", "up"});
        raceControls.put(KeyEvent.VK_J, new String[]{"world4", "move", "Moneymaker", "left"});
        raceControls.put(KeyEvent.VK_K, new String[]{"world4", "move", "Moneymaker", "down"});
        raceControls.put(KeyEvent.VK_L, new String[]{"world4", "move", "Moneymaker", "right"});
        
        // Battle controls
        // Player 1
        battleControls.put(KeyEvent.VK_UP, new String[]{"world1", "move", "Moneymaker", "up"});
        battleControls.put(KeyEvent.VK_LEFT, new String[]{"world1", "move", "Moneymaker", "left"});
        battleControls.put(KeyEvent.VK_DOWN, new String[]{"world1", "move", "Moneymaker", "down"});
        battleControls.put(KeyEvent.VK_RIGHT, new String[]{"world1", "move", "Moneymaker", "right"});
        battleControls.put(KeyEvent.VK_SPACE, new String[]{"world1", "melee", "Moneymaker"});
        /*battleControls.put(KeyEvent.VK_I, new String[]{"world1", "range", "Moneymaker", "up"});
        battleControls.put(KeyEvent.VK_J, new String[]{"world1", "range", "Moneymaker", "down"});
        battleControls.put(KeyEvent.VK_K, new String[]{"world1", "range", "Moneymaker", "left"});
        battleControls.put(KeyEvent.VK_L, new String[]{"world1", "range", "Moneymaker", "right"});*/
        // Player 2
        battleControls.put(KeyEvent.VK_W, new String[]{"world1", "move", "Teadrinker", "up"});
        battleControls.put(KeyEvent.VK_A, new String[]{"world1", "move", "Teadrinker", "left"});
        battleControls.put(KeyEvent.VK_S, new String[]{"world1", "move", "Teadrinker", "down"});
        battleControls.put(KeyEvent.VK_D, new String[]{"world1", "move", "Teadrinker", "right"});
        battleControls.put(KeyEvent.VK_SHIFT, new String[]{"world1", "melee", "Teadrinker"});
        /*battleControls.put(KeyEvent.VK_T, new String[]{"world1", "range", "Teadrinker", "up"});
        battleControls.put(KeyEvent.VK_F, new String[]{"world1", "range", "Teadrinker", "down"});
        battleControls.put(KeyEvent.VK_G, new String[]{"world1", "range", "Teadrinker", "left"});
        battleControls.put(KeyEvent.VK_H, new String[]{"world1", "range", "Teadrinker", "right"});
        */
        // Register a keystroke
        this.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                String[] message = getControlMessage(e.getKeyCode());
                if (message != null) {
                    sendMessage(new Message(Message.GAME_MSG, message));
                }
                
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ESCAPE:   
	                    sendMessage(new Message(Message.GAME_MSG, new String[]{"endGame"}));
	                    sendMessage(new Message(Message.SOUND_MSG, new String[]{"stop", "background"}));
	                    sendMessage(new Message(Message.SOUND_MSG, new String[]{"loop", "menu"}));
	                    setAppState(AppState.MENU);
	                    break;
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
    
    private String[] getControlMessage(int keyPressed ) {
        String gameMode = App.pref.getText("gameMode");
        try {
            if (gameMode.equals("Race")) {
                return raceControls.get(keyPressed);
            } else if (gameMode.equals("Adventure")) {
                return adventureControls.get(keyPressed);            
            } else if (gameMode.equals("Battle")) {
                return battleControls.get(keyPressed);            
            }
        } catch (Exception e) {    
        }
        return null;
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

        String pack = App.pref.getText("texturePack");
        
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
        String pack = App.pref.getText("texturePack");
        
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

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
         * Start building the settings list
         * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
         */
        c.weightx = 1;
        int row = 0;
        
        // Game mode selection 
        c.gridx = 1;
        c.gridy = row++;
        c.anchor = GridBagConstraints.WEST;
        JLabel gameModeText = new JLabel();
        gameModeText.setText("Gamemode: ");
        gameSettingsPanel.add(gameModeText, c);

        c.gridx = 2;
        c.anchor = GridBagConstraints.EAST;
        String[] gameModes = new String[]{"Race", "Adventure", "Battle"};
        String gameMode = App.pref.getText("gameMode");
        JComboBox<String> gameModeSelection = new JComboBox<String>(gameModes);
        gameModeSelection.setSelectedItem(gameMode);
        gameModeSelection.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (ItemEvent.SELECTED == e.getStateChange()) {
                    sendMessage(new Message(Message.SOUND_MSG, new String[]{"play", "click"}));
                    App.pref.setPreference("text.gameMode="+(String)e.getItem());
                    refresh();
                }
            }
        });
        gameSettingsPanel.add(gameModeSelection, c);

        // Door and key generation option       
        JCheckBox doorAndKey = new JCheckBox("", App.pref.getBool("doorAndKey"));
        if (gameMode.equals("Battle")){
            doorAndKey.setSelected(false);
            doorAndKey.setEnabled(false);
        }
        doorAndKey.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	sendMessage(new Message(Message.SOUND_MSG, new String[]{"play", "click"}));
                App.pref.toggleBool("doorAndKey");
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
        JCheckBox enemyCheckBox = new JCheckBox("", App.pref.getBool("enemy"));
        if (!gameMode.equals("Adventure")){
            enemyCheckBox.setSelected(false);
            enemyCheckBox.setEnabled(false);
        }
        enemyCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage(new Message(Message.SOUND_MSG, new String[]{"play", "click"}));
                App.pref.toggleBool("enemy");
            }
        });

        c.gridx = 1;
        c.gridy = row++;
        c.anchor = GridBagConstraints.WEST;
        gameSettingsPanel.add(new JLabel("Enemy in maze?"), c);
        c.gridx = 2;
        c.anchor = GridBagConstraints.EAST;
        gameSettingsPanel.add(enemyCheckBox, c);
        
        // Visibility selection
        String[] visModes = new String[]{"Off", "Low", "Med", "High"};
        JComboBox<String> visSelection = new JComboBox<String>(visModes);
        if (gameMode.equals("Battle")){
            visSelection.setEnabled(false);
        }
        int vis = App.pref.getValue("visibleRange");
        switch (vis) {
            case 3: visSelection.setSelectedItem("Low");     break;
            case 6: visSelection.setSelectedItem("Med");     break;
            case 8: visSelection.setSelectedItem("High");   break;
            default:  visSelection.setSelectedItem("Off");    break;
        }
        
        visSelection.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (ItemEvent.SELECTED == e.getStateChange()) {
                    sendMessage(new Message(Message.SOUND_MSG, new String[]{"play", "click"}));
                    switch ((String)e.getItem()) {
                        case "Off": App.pref.setPreference("value.visibleRange=-1");    break;
                        case "Low": App.pref.setPreference("value.visibleRange=3");     break;
                        case "Med": App.pref.setPreference("value.visibleRange=6");     break;
                        case "High": App.pref.setPreference("value.visibleRange=8");   break;
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
        widthSize.setValue(Integer.toString(App.pref.getValue("defaultMapWidth")));
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
        heightSize.setValue(Integer.toString(App.pref.getValue("defaultMapHeight")));
        heightSize.setColumns(2);
        heightSize.getDocument().addDocumentListener(new PrefUpdate("value", "defaultMapHeight"));
        c.gridx = 1;
        c.gridy = row++;
        c.anchor = GridBagConstraints.WEST;
        gameSettingsPanel.add(new JLabel("Height"), c);
        c.gridx = 2;
        c.anchor = GridBagConstraints.EAST;
        gameSettingsPanel.add(heightSize, c);

        String[] playerOptions = null;
        
        if(gameMode.equals("Battle")) {
        	playerOptions = new String[]{"Human", "Battle AI"};
        } else {
        	playerOptions = new String[]{"Human", "Easy AI", "Med AI", "Hard AI", "Off"};
        }

        for (int x = 1; x <= 4; x++) {
            row++;
            c.gridx = 1;
            c.gridy = x+row;
            c.anchor = GridBagConstraints.WEST;
            gameSettingsPanel.add(new JLabel("Player "+x+": "), c);
            c.gridx = 2;
            c.anchor = GridBagConstraints.EAST;
            PlayerOptions opt = new PlayerOptions(playerOptions, "player"+x);
            if (!gameMode.equals("Race")){
                if (x >= 3) {
                    opt.setEnabled(false);
                }
            }
            gameSettingsPanel.add(opt, c);  
            
        }
        
        // Button should always be at the bottom, so it's y is 99. For some reason
        // using row as with the rest of the buttons did not work
        c.gridy = 99;
        c.gridx = 1;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        JClickButton startGameButton = new JClickButton("Start Game");
        if (gameMode.equals("Battle")) {
        	if (App.pref.getText("player1").equals("Off") || App.pref.getText("player2").equals("Off")) {
                startGameButton.setEnabled(false);
            }
        } else {
            if (App.pref.getText("player1").equals("Off")) {
                startGameButton.setEnabled(false);
            }            
        }
        
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
        JTextArea aboutText = new JTextArea(30, 25);
        aboutText.setMargin(new Insets(10,10,10,10));
        try {
	        FileReader aboutReader = new FileReader(this.game.getAboutFile());
	        aboutText.read(aboutReader, null);
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
        
        
        JScrollPane scroll = new JScrollPane(aboutText);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        
        aboutTextPanel.add(scroll);
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
                App.pref.loadPreferences();
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
        JPanel settingsPanel = new JPanel(new GridLayout(6,1)) {
            @Override
            public Dimension getPreferredSize() {
                Dimension d = this.getParent().getSize();        
                int windowHeight = d.height;
                int windowWidth = d.width;
                return new Dimension(windowWidth,windowHeight);
            }
        };
        windowPanel.add(settingsPanel);
        settingsPanel.add(Box.createRigidArea(new Dimension(0,20)));
        
        /*for (String s : App.pref.getKeys("colour")) {
            Color c = App.pref.getColour(s);
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
        */
        JPanel textureSelectPanel = new JPanel();
        JLabel textureSelectLabel = new JLabel("Select texture: ");
        JRadioButton textureCastle = new JRadioButton("Castle");
        textureCastle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage(new Message(Message.SOUND_MSG, new String[]{"play", "click"}));
                App.pref.setPreference("text.texturePack=castle");
            }
        });
        JRadioButton textureDesert = new JRadioButton("Desert");
        textureDesert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage(new Message(Message.SOUND_MSG, new String[]{"play", "click"}));
                App.pref.setPreference("text.texturePack=desert");
            }
        });
        JRadioButton textureSpace = new JRadioButton("Space");
        textureSpace.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage(new Message(Message.SOUND_MSG, new String[]{"play", "click"}));
                App.pref.setPreference("text.texturePack=space");
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
        settingsPanel.add(textureSelectPanel);

        JPanel musicSliderPanel = new JPanel();
        JLabel musicSliderLabel = new JLabel("Music Volume: ");
        JSlider musicSlider = new JSlider(0, 100, App.pref.getValue("musicVolume"));
        musicSlider.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                int newVolume = (int)source.getValue();
                App.pref.setPreference("value.musicVolume="+newVolume);
                sendMessage(new Message(Message.SOUND_MSG, new String[]{"changeVolume", "music"}));
                
            }
        });
        musicSliderPanel.add(musicSliderLabel);
        musicSliderPanel.add(musicSlider);
        settingsPanel.add(musicSliderPanel);
        
        JPanel effectsSliderPanel = new JPanel();
        JLabel effectsSliderLabel = new JLabel("Effects Volume: ");
        JSlider effectsSlider = new JSlider(0, 100, App.pref.getValue("effectsVolume"));
        effectsSlider.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                int newVolume = (int)source.getValue();
                App.pref.setPreference("value.effectsVolume="+newVolume);
                sendMessage(new Message(Message.SOUND_MSG, new String[]{"changeVolume", "effects"}));
                
            }
        });
        
        effectsSliderPanel.add(effectsSliderLabel);
        effectsSliderPanel.add(effectsSlider);
        settingsPanel.add(effectsSliderPanel);
        
        windowPanel.add(Box.createRigidArea(new Dimension(0,50)));
        
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
        
        String pack = App.pref.getText("texturePack");

        // Title Panel
        titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        titlePanel.setBackground(windowPanel.getBackground());

        ArrayList<Integer> coins = game.getPlayerCoins();
        JLabel coins1 = new JLabel();
        JLabel coins2 = new JLabel();
        JLabel coins3 = new JLabel();
        JLabel coins4 = new JLabel();
        
        // Game Panel        
        gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.Y_AXIS));
        
        JPanel gamePanelTop = new JPanel();
        gamePanelTop.setLayout(new BoxLayout(gamePanelTop, BoxLayout.X_AXIS));
        gamePanel.add(gamePanelTop);
        
        ArrayList<World> worlds = game.getWorlds();
        int numWorlds = worlds.size();
        
        if (numWorlds >= 1) {
            JPanel innerPanelA = new JPanel(new GridBagLayout());
            Integer playerOneCoins = coins.get(0);
            coins1.setText("Coins: "+playerOneCoins);
            titlePanel.add(coins1);
            innerPanelA.add(new GameMap(worlds.get(0), coins1));
            gamePanelTop.add(innerPanelA);
            if (numWorlds >= 2) {            
                JPanel innerPanelB = new JPanel(new GridBagLayout());
                Integer playerTwoCoins = coins.get(1);
                coins2.setText("Coins: "+playerTwoCoins);
                titlePanel.add(coins2);
                innerPanelB.add(new GameMap(worlds.get(1), coins2));
                gamePanelTop.add(innerPanelB);
                if (numWorlds >= 3) {    
                    JPanel gamePanelBot = new JPanel();
                    Integer playerThreeCoins = coins.get(2);
                    coins3.setText("Coins: "+playerThreeCoins);
                    titlePanel.add(coins3);
                    gamePanelBot.setLayout(new BoxLayout(gamePanelBot, BoxLayout.X_AXIS));
                    gamePanel.add(gamePanelBot);
                    
                    JPanel innerPanelC = new JPanel(new GridBagLayout());
                    innerPanelC.add(new GameMap(worlds.get(2), coins3));
                    gamePanelBot.add(innerPanelC);
                    if (numWorlds == 4) {            
                        JPanel innerPanelD = new JPanel(new GridBagLayout());
                        Integer playerFourCoins = coins.get(3);
                        coins4.setText("Coins: "+playerFourCoins);
                        titlePanel.add(coins4);
                        innerPanelD.add(new GameMap(worlds.get(3), coins4));
                        gamePanelBot.add(innerPanelD);
                    }
                }
            }
        }
        
        // Menu Panel
        gameMenuPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        gameMenuPanel.setBackground(windowPanel.getBackground().darker());
        
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
                            App.pref.setPreference(spaceName+"."+prefName+"="+value);
                        }
                        break;
                    case "colour":
                        if (textLength == 6) {
                            value = e.getDocument().getText(0,textLength);
                            App.pref.setPreference(spaceName+"."+prefName+"="+value);
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
            setSelectedItem(App.pref.getText(s));
            addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (ItemEvent.SELECTED == e.getStateChange()) {
                        App.pref.setPreference("text."+setting+"="+(String)e.getItem());
                        sendMessage(new Message(Message.SOUND_MSG, new String[]{"play", "click"}));
                    }
                    refresh();
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
