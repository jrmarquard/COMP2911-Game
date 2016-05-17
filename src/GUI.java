import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
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
    JPanel titlePanel;
    JPanel gameMainPanel;
    JPanel gamePanelA;
    JPanel gamePanelB;
    JPanel gameMenuPanel;
    
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
	                case KeyEvent.VK_DOWN:  addCommand(new Command(Com.ARROW_DOWN));    break;
	                case KeyEvent.VK_LEFT:  addCommand(new Command(Com.ARROW_LEFT));    break;
	                case KeyEvent.VK_RIGHT: addCommand(new Command(Com.ARROW_RIGHT));   break;
	                case KeyEvent.VK_UP:    addCommand(new Command(Com.ARROW_UP));      break;
	                case KeyEvent.VK_W:     addCommand(new Command(Com.W_UP));          break;
	                case KeyEvent.VK_A:     addCommand(new Command(Com.A_LEFT));        break;
	                case KeyEvent.VK_S:     addCommand(new Command(Com.S_DOWN));        break;
	                case KeyEvent.VK_D:     addCommand(new Command(Com.D_RIGHT));       break;
                    case KeyEvent.VK_C:     addCommand(new Command(Com.SOLVE));         break;
                    case KeyEvent.VK_N:     newGame(1);                                  break;
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
            case MENU:      drawMenu();         break;
            case GAME:      drawGame();         break;
            case SETTINGS:  drawSettings();     break;
            case ABOUT:     drawAbout();        break;
            case EXIT:      ;                   break;
        }
        
        // Refocuses the window so keystrokes are registered
        setFocusable(true);
        
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
        JButton startGameButton = new JButton("Start Game");
        startGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setState(AppState.GAME);
                newGame(1);
            }
        });

        // Button will go to settings
        JButton settingsButton = new JButton("Settings");
        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setState(AppState.SETTINGS);
                update();
            }
        });
        
        // Button will go to settings
        JButton aboutButton = new JButton("About");
        aboutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setState(AppState.ABOUT);
                update();
            }
        });
        // Button will quit the game
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCommand(new Command(Com.EXIT));
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
        JTextField about = new JTextField();
        about.setText("Game written by: John, Joshua, Patrick, Tim, Tyler");
        JButton backButton = new JButton("Back");
        backButton.setMnemonic(KeyEvent.VK_W);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                appState = AppState.MENU;
                addCommand(new Command(Com.DRAW));
            }
        });
        windowPanel.add(about);
        windowPanel.add(backButton);
    }
    
    private void drawSettings() {
        windowPanel.setLayout(new BoxLayout(windowPanel, BoxLayout.Y_AXIS));
        
        for (String s : pref.getColourKeys()) {
        Color c = pref.getColour(s);
        int red = c.getRed();
        int blue = c.getBlue();
        int green = c.getGreen();
        String value = "" + Integer.toHexString(red) + Integer.toHexString(green) + Integer.toHexString(blue);
        JPanel settingRow = new JPanel();
        JTextField settingName = new JTextField();
        JTextField settingValue = new JTextField();
        settingRow.add(settingName);
        settingRow.add(settingValue);
        settingName.setText(s);
        settingValue.setText(value);
        settingValue.setColumns(value.length());
        settingName.setEditable(true);
        
        windowPanel.add(settingRow);
        }
        
        JButton backButton = new JButton("Back");
        backButton.setMnemonic(KeyEvent.VK_W);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                appState = AppState.MENU;
                addCommand(new Command(Com.DRAW));
            }
        });
        windowPanel.add(backButton);
    }
    
    private void drawGame() {
        System.out.println("Drawing game");
        windowPanel.setLayout(new BoxLayout(windowPanel, BoxLayout.Y_AXIS));

        // This recreates the panels EVERYTIME the game is drawn.
        // Need to update this to something that is halfway efficient
        titlePanel = new JPanel();
        gameMainPanel = new JPanel();
        gamePanelA = new JPanel();
        gamePanelB = new JPanel();
        gameMenuPanel = new JPanel();
        
        // Sets the layout for each component
        titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        gameMainPanel.setLayout(new BoxLayout(gameMainPanel, BoxLayout.X_AXIS));
        gamePanelA.setLayout(new GridBagLayout());
        gamePanelB.setLayout(new GridBagLayout());
        gameMenuPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        
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
        
        drawTitlePanel();
        drawGameMenuPanel();
        
        windowPanel.setPreferredSize(windowPanel.getSize());
    }

    private void drawTitlePanel() {
        titlePanel.removeAll();
        
        if (world.getWinStatus()) {
            titlePanel.setBackground(pref.getColour("titleWinColour"));
        } else {
            titlePanel.setBackground(pref.getColour("titleDefaultColour"));
        }
        
        JLabel title = new JLabel();
        title.setText("Coins: "+world.getPlayerCoins(0));

        titlePanel.add(title);
    }
    
    private void drawGameMenuPanel() {
        gameMenuPanel.removeAll();
        gameMenuPanel.setBackground(pref.getColour("menuColour"));

        JFormattedTextField widthSize = new JFormattedTextField();
        widthSize.setValue(Integer.toString(pref.getValue("defaultMapWidth")));
        widthSize.setColumns(2);
        widthSize.getDocument().addDocumentListener(new ValueUpdate("defaultMapWidth"));
        
        JFormattedTextField heightSize = new JFormattedTextField();
        heightSize.setValue(Integer.toString(pref.getValue("defaultMapHeight")));
        heightSize.setColumns(2);
        heightSize.getDocument().addDocumentListener(new ValueUpdate("defaultMapHeight"));
        
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
        
        JButton solveButton = new JButton("Solve");
        solveButton.setMnemonic(KeyEvent.VK_S);
        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCommand(new Command(Com.SOLVE));
            }
        });
        JButton closeButton = new JButton("Exit to menu");
        closeButton.setMnemonic(KeyEvent.VK_W);
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                appState = AppState.MENU;
                addCommand(new Command(Com.DRAW));
            }
        });
        gameMenuPanel.add(new JLabel("Width"));
        gameMenuPanel.add(widthSize);
        gameMenuPanel.add(new JLabel("Height"));
        gameMenuPanel.add(heightSize);
        if(!world.getIsMultiplayer()) {
        	gameMenuPanel.add(solveButton);
            gameMenuPanel.add(auto);
        }
        gameMenuPanel.add(newMazeButton);
        gameMenuPanel.add(closeButton);
    }
    
    public void close() {
        this.dispose();
    }
    
    /**
     * Creates a new game for the specified number of players
     * 
     * @param numPlayers 1 or 2, nothing else
     */
    private void newGame(int numPlayers) {
        appState = AppState.GAME;
        int width = pref.getValue("defaultMapWidth");
        int height = pref.getValue("defaultMapHeight");
        CommandMap c = new CommandMap(Com.NEW_MAP, width, height, numPlayers);
        addCommand(c);
    }
    
    private void addCommand(Command c) {
        commands.add(c);
    }
    private void setState(AppState s) {
        appState = s;
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
    
    private class ValueUpdate implements DocumentListener {
            
            String valueName;
            
            public ValueUpdate(String s) {
                super();
                this.valueName = s;
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
                int value;
                try {
                    int textLength = e.getDocument().getLength();
                    if (textLength >= 2) textLength = 2;
                    value = Integer.parseInt(e.getDocument().getText(0,textLength));
                    pref.setPreference("value."+valueName+"="+value);
                } catch (NumberFormatException | BadLocationException e1) {
                    // Do nothing
                }
            }
    
    }
    
}