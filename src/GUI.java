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
        
        // Displays the number of players to choose from
        GAME_NUM_PLAYERS,
        
        // Displays the different game modes for single player
        GAME_SINGLE_MODE,
        
        // Displays the different game modes for multiplayer
        GAME_MULTI_MODE,
        
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
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:       
                        addCommand(new CommandMap(Com.MOVE_UP, 1));
                        break;
                    case KeyEvent.VK_LEFT:     
                        addCommand(new CommandMap(Com.MOVE_LEFT, 1));    
                        break;
	                case KeyEvent.VK_DOWN:     
	                    addCommand(new CommandMap(Com.MOVE_DOWN, 1));    
	                    break;
	                case KeyEvent.VK_RIGHT:    
	                    addCommand(new CommandMap(Com.MOVE_RIGHT, 1));   
	                    break;
	                case KeyEvent.VK_W:        
	                    addCommand(new CommandMap(Com.MOVE_UP, 2));          
	                    break;
	                case KeyEvent.VK_A:        
	                    addCommand(new CommandMap(Com.MOVE_LEFT, 2));        
	                    break;
	                case KeyEvent.VK_S:        
	                    addCommand(new CommandMap(Com.MOVE_DOWN, 2));        
	                    break;
	                case KeyEvent.VK_D:        
	                    addCommand(new CommandMap(Com.MOVE_RIGHT, 2));      
	                    break;
                    case KeyEvent.VK_T:        
                        addCommand(new CommandMap(Com.MOVE_UP, 3));          
                        break;
                    case KeyEvent.VK_F:        
                        addCommand(new CommandMap(Com.MOVE_LEFT, 3));        
                        break;
                    case KeyEvent.VK_G:        
                        addCommand(new CommandMap(Com.MOVE_DOWN, 3));        
                        break;
                    case KeyEvent.VK_H:        
                        addCommand(new CommandMap(Com.MOVE_RIGHT, 3));      
                        break;
                    case KeyEvent.VK_I:        
                        addCommand(new CommandMap(Com.MOVE_UP, 4));          
                        break;
                    case KeyEvent.VK_J:        
                        addCommand(new CommandMap(Com.MOVE_LEFT, 4));        
                        break;
                    case KeyEvent.VK_K:        
                        addCommand(new CommandMap(Com.MOVE_DOWN, 4));        
                        break;
                    case KeyEvent.VK_L:        
                        addCommand(new CommandMap(Com.MOVE_RIGHT, 4));      
                        break;
                    case KeyEvent.VK_C:        addCommand(new Command(Com.SOLVE));         break;
                    case KeyEvent.VK_ESCAPE:   setAppState(AppState.MENU);                 break; 
                    case KeyEvent.VK_N:        newGame(1, 0);                                 break;
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
            case MENU:     			drawMenu();         
                        			break;
            case GAME_NUM_PLAYERS:	drawNumPlayers();
            						break;
            case GAME_SINGLE_MODE:	drawSingleMode();
            						break;
            case GAME_MULTI_MODE:	drawMultiMode();
            						break;
            case GAME:      		drawGame();         
                            		break;
            case SETTINGS:  		drawSettings();     
                            		break;
            case ABOUT:     		drawAbout();        
                            		break;
            case EXIT:      		addCommand(new Command(Com.EXIT));
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
                setAppState(AppState.GAME_NUM_PLAYERS);
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
    
    private void drawNumPlayers() {
    	windowPanel.setLayout(new BoxLayout(windowPanel, BoxLayout.Y_AXIS));

        JButton singlePlayerButton = new JButton("Single Player");
        singlePlayerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAppState(AppState.GAME_SINGLE_MODE);
            }
        });
        
        JButton multiPlayerButton = new JButton("Multiplayer");
        multiPlayerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAppState(AppState.GAME_MULTI_MODE);
            }
        });
        
        windowPanel.add(singlePlayerButton);
        windowPanel.add(multiPlayerButton);
    }
    
    private void drawSingleMode() {
    	windowPanel.setLayout(new BoxLayout(windowPanel, BoxLayout.Y_AXIS));

        JButton easyButton = new JButton("Easy");
        easyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	newGame(1, 0);
                setAppState(AppState.GAME);
            }
        });
        
        JButton mediumButton = new JButton("Medium");
        mediumButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	newGame(1, 1);
                setAppState(AppState.GAME);
            }
        });
        
        JButton hardButton = new JButton("Hard");
        hardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	newGame(1, 2);
                setAppState(AppState.GAME);
            }
        });
        
        JButton veryHardButton = new JButton("Very Hard");
        veryHardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	newGame(1, 3);
                setAppState(AppState.GAME);
            }
        });
        
        JButton customButton = new JButton("Custom");
        customButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	newGame(1, 9);
                setAppState(AppState.GAME);
            }
        });
        
        windowPanel.add(easyButton);
        windowPanel.add(mediumButton);
        windowPanel.add(hardButton);
        windowPanel.add(veryHardButton);
        windowPanel.add(customButton);
    }
    
    private void drawMultiMode() {
    	Integer[] numPlayers = new Integer[]{2,3,4};
    	JComboBox<Integer> numPlayersSelect = new JComboBox<>(numPlayers);
    	
    	JButton raceButton = new JButton("Race To Finish");
    	raceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	JComboBox<Integer> box = (JComboBox<Integer>)windowPanel.getComponent(0);
            	int num = (int)box.getSelectedItem();
            	newGame(num, 10);
                setAppState(AppState.GAME);
            }
        });
    	
    	JButton coinsHuntButton = new JButton("Coins Hunting");
    	coinsHuntButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	JComboBox<Integer> box = (JComboBox<Integer>)windowPanel.getComponent(0);
            	int num = (int)box.getSelectedItem();
            	newGame(num, 11);
                setAppState(AppState.GAME);
            }
        });
    	
    	windowPanel.add(numPlayersSelect);
    	windowPanel.add(raceButton);
    	windowPanel.add(coinsHuntButton);
    }
    
    private void drawGame() {
        windowPanel.setLayout(new BoxLayout(windowPanel, BoxLayout.Y_AXIS));
        
        // This recreates the panels EVERYTIME the game is drawn.
        // Need to update this to something that is halfway efficient
        JPanel titleMainPanel = new JPanel();
        JPanel titlePanelA = new JPanel();
        JPanel titlePanelB = new JPanel();
        JPanel titlePanelC = new JPanel();
        JPanel titlePanelD = new JPanel();
        JPanel gameMainPanelA = new JPanel();
        JPanel gameMainPanelB = new JPanel();
        JPanel gamePanelA = new JPanel();
        JPanel gamePanelB = new JPanel();
        JPanel gamePanelC = new JPanel();
        JPanel gamePanelD = new JPanel();
        JPanel gameMenuPanel = new JPanel();
        
        // Sets the layout for each component
        titleMainPanel.setLayout(new BoxLayout(titleMainPanel, BoxLayout.X_AXIS));
        titlePanelA.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        titlePanelB.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        titlePanelC.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        titlePanelD.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        gameMainPanelA.setLayout(new BoxLayout(gameMainPanelA, BoxLayout.X_AXIS));
        gameMainPanelB.setLayout(new BoxLayout(gameMainPanelB, BoxLayout.X_AXIS));
        gamePanelA.setLayout(new GridBagLayout());
        gamePanelB.setLayout(new GridBagLayout());
        gamePanelC.setLayout(new GridBagLayout());
        gamePanelD.setLayout(new GridBagLayout());
        gameMenuPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        
        // Adds the main title panel and the title panel A attched with it
        titlePanelA.setBackground(pref.getColour("titleDefaultColour"));
        windowPanel.add(titleMainPanel);
        titleMainPanel.add(titlePanelA);
        
        // Adds some description for player 1's title panel
        JLabel titleA = new JLabel();
        titleA.setText("Player 1 Coins: "+world.getPlayerCoins(0));
        titlePanelA.add(titleA);
        
        // Creates the GameMap for the world to be draw onto
        GameMap innerGamePanelA = new GameMap(world, pref, 0);
        
        // Attaches the innerGamePanel onto the gamePanel
        gamePanelA.add(innerGamePanelA);
        gameMainPanelA.add(gamePanelA);  
        
        /*
         * If there are more than 1 player,
         * adds the description for player 2's title panel,
         * adds an extra title panel to display information about player 2,
         * and attaches the innerGamePanel onto the gamePanel for player 2
         */
        if (world.getNumberOfPlayers() > 1) {
        	JLabel titleB = new JLabel();
            titleB.setText("Player 2 Coins: "+world.getPlayerCoins(1));
            titlePanelB.add(titleB);
        	titleMainPanel.add(titlePanelB);
        	GameMap innerGamePanelB = new GameMap(world, pref, 1);
        	gamePanelB.add(innerGamePanelB);
        	gameMainPanelA.add(gamePanelB);
        	
        	/*
             * If there are more than 2 player,
             * adds the description for player 3's title panel,
             * adds an extra title panel to display information about player 3,
             * and attaches the innerGamePanel onto the gamePanel for player 3
             */
        	if (world.getNumberOfPlayers() > 2) {
        		JLabel titleC = new JLabel();
                titleC.setText("Player 3 Coins: "+world.getPlayerCoins(2));
                titlePanelC.add(titleC);
        		titleMainPanel.add(titlePanelC);
            	GameMap innerGamePanelC = new GameMap(world, pref, 2);
            	gamePanelC.add(innerGamePanelC);
            	gameMainPanelB.add(gamePanelC);
            	
            	/*
                 * If there are 4 players,
                 * adds the description for player 3's title panel,
                 * adds an extra title panel to display information about player 4,
                 * and attaches the innerGamePanel onto the gamePanel for player 4
                 */
            	if (world.getNumberOfPlayers() == 4) {
            		JLabel titleD = new JLabel();
                    titleD.setText("Player 4 Coins: "+world.getPlayerCoins(3));
                    titlePanelD.add(titleD);
            		titleMainPanel.add(titlePanelD);
                	GameMap innerGamePanelD = new GameMap(world, pref, 3);
                	gamePanelD.add(innerGamePanelD);
                	gameMainPanelB.add(gamePanelD);
                }
            }
        }
                
        titlePanelA.setBackground(pref.getColour("titleDefaultColour"));
        titlePanelB.setBackground(pref.getColour("titleDefaultColour"));
        titlePanelC.setBackground(pref.getColour("titleDefaultColour"));
        titlePanelD.setBackground(pref.getColour("titleDefaultColour"));
        
        // Changes the title panel colour of the winning player
        if (world.getWinStatus()) {
            if(world.getWinPlayer() == 0) {
                titlePanelA.setBackground(pref.getColour("titleWinColour"));
            } else if(world.getWinPlayer() == 1) {
                titlePanelB.setBackground(pref.getColour("titleWinColour"));
            } else if(world.getWinPlayer() == 2) {
                titlePanelC.setBackground(pref.getColour("titleWinColour"));
            } else if(world.getWinPlayer() == 3) {
                titlePanelD.setBackground(pref.getColour("titleWinColour"));
            }
        }
        
        // Adds the main game panel to the window panel
        windowPanel.add(gameMainPanelA);
        /*
         *  Adds the extra game panel to the window panel 
         *  if there are more than 2 players
         */
        if(world.getNumberOfPlayers() > 2) {
        	windowPanel.add(gameMainPanelB);
        }
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
        
        JButton newMazeButton = new JButton("New Maze");
        newMazeButton.setMnemonic(KeyEvent.VK_N);
        newMazeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                newGame(1, 9);
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
        
        if(world.getGameMode() == 9) {
        	gameMenuPanel.add(new JLabel("Width"));
            gameMenuPanel.add(widthSize);
            gameMenuPanel.add(new JLabel("Height"));
            gameMenuPanel.add(heightSize);
            gameMenuPanel.add(newMazeButton);
        }
//        if(world.getNumberOfPlayers() == 1) {
//            gameMenuPanel.add(auto);
//        }
        //
        gameMenuPanel.add(closeButton);
    }
    
    /**
     * Creates a new game for the specified number of players
     * 
     * @param numPlayers 1 or 2, nothing else
     */
    private void newGame(int numPlayers, int gameMode) {
        int width = pref.getValue("defaultMapWidth");
        int height = pref.getValue("defaultMapHeight");
        addCommand(new CommandMap(Com.NEW_MAP, width, height, numPlayers, gameMode));
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
    
}
