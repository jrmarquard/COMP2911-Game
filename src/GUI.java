import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GUI extends JFrame implements DisplayInterface {  
    public GUI () {
        initUI();
    }

    @Override
    public void update(MazeWorld m) {
        setVisible(true);
    }
    
    private void initUI() {
        
        JPanel basic = new JPanel();
        basic.setLayout(new BoxLayout(basic, BoxLayout.Y_AXIS));
        add(basic);
        
        // The top panel
        JPanel topPanel = new JPanel(new BorderLayout(0, 0));
        topPanel.setBackground(Color.blue);
        topPanel.setMaximumSize(new Dimension(450, 0));
        
        JLabel title = new JLabel("MazeRunner");
        title.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));
        topPanel.add(title, BorderLayout.CENTER);

        basic.add(topPanel);
        
        // Graphics Panel - temporarily a text panel
        

        createGamePanel();
        
        basic.add(createGamePanel());
        
        // Bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Color.pink);

        JButton ntip = new JButton("Play");
        ntip.setMnemonic(KeyEvent.VK_N);
        JButton close = new JButton("Exit");
        close.setMnemonic(KeyEvent.VK_C);
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });

        bottomPanel.add(ntip);
        bottomPanel.add(close);
        basic.add(bottomPanel);
        
        // Sets the title of the window
        setTitle("Maze Runner");
        
        // Sets the size of the window
        setSize(400, 300);
        
        // Sets the location of the window to the middle of the screen
        setLocationRelativeTo(null);
        
        // Sets the window to EXIT_ON_CLOSE when the system closes the window
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    
    private JPanel createGamePanel() {
        
        JPanel middlePanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        int rows = 5;
        int cols = 5;
        
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                JPanel jp1 = new JPanel();
                if (x%2 == 0) {
                    jp1.setBackground(Color.black);
                } else {
                    jp1.setBackground(Color.white);
                }
                c.fill = GridBagConstraints.BOTH;
                c.weightx = 0.5;
                c.weighty = 3;
                c.gridx = x;
                c.gridy = 0;
                middlePanel.add(jp1, c);
            }
        }
        
        
        
        JPanel jp1 = new JPanel();
        jp1.setBackground(Color.black);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.5;
        c.weighty = 3;
        c.gridx = 0;
        c.gridy = 1;
        middlePanel.add(jp1, c);

        
        JPanel jp2 = new JPanel();
        jp2.setBackground(Color.white);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.5;
        c.weighty = 1;
        c.gridx = 1;
        c.gridy = 1;
        middlePanel.add(jp2, c);
        
        JPanel jp3 = new JPanel();
        jp3.setBackground(Color.yellow);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 2;
        c.weighty = 1;
        c.gridx = 2;
        c.gridy = 1;
        middlePanel.add(jp3, c);
        
        JPanel jp4 = new JPanel();
        jp4.setBackground(Color.green);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 3;
        c.gridx = 2;
        c.gridy = 2;
        middlePanel.add(jp4, c);
        
        
        middlePanel.setBackground(Color.red);
        middlePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        /*
        for (int x = 0; x < 10; x++) {
            JPanel jp = new JPanel();
            jp.setSize(10, 10);
            middlePanel.add(jp);
        }
        
        
        middlePanel.add(new JButton("Button 1"));
        middlePanel.add(new JButton("Button 2"));
        middlePanel.add(new JButton("Button 3"));
        middlePanel.add(new JButton("Long-Named Button 4"));
        middlePanel.add(new JButton("5"));
        middlePanel.add(new JButton("Button 1"));
        middlePanel.add(new JButton("Button 1"));
        middlePanel.add(new JButton("Button 1"));
        middlePanel.add(new JButton("Button 1"));
        middlePanel.add(new JButton("Button 1"));
        middlePanel.add(new JButton("Button 1"));
        middlePanel.add(new JButton("Button 1"));
        */
        return middlePanel;
        
    }

    private void createLayout(JComponent... arg) {
        JPanel pane = (JPanel) getContentPane();
        GroupLayout gl = new GroupLayout(pane);
        pane.setLayout(gl);
        
        gl.setAutoCreateContainerGaps(true);
        pane.setToolTipText("Content pane");        
        
        gl.setAutoCreateContainerGaps(true);
        
        gl.setHorizontalGroup(gl.createParallelGroup()
                .addComponent(arg[0], GroupLayout.DEFAULT_SIZE, 
                        GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(arg[1], GroupLayout.DEFAULT_SIZE, 
                        GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        gl.setVerticalGroup(gl.createSequentialGroup()
                .addComponent(arg[0])
                .addComponent(arg[1])
        );
    }
    
    private void createMenuBar(JComponent... arg) {

        JMenuBar menubar = new JMenuBar();

        JMenu gameMenu = new JMenu("Game");
        JMenu fileMenu = new JMenu("File");
        
        gameMenu.setMnemonic(KeyEvent.VK_G);
        fileMenu.setMnemonic(KeyEvent.VK_F);
        
        JMenuItem PlayMi = new JMenuItem(new MenuItemAction("Play", KeyEvent.VK_P));
        JMenuItem LoadMapMi = new JMenuItem(new MenuItemAction("Load Map", KeyEvent.VK_L));
        JMenuItem newMi = new JMenuItem(new MenuItemAction("New", KeyEvent.VK_N));
        
        JMenuItem aboutMi = new JMenuItem(new MenuItemAction("About", KeyEvent.VK_A));
        
        JMenuItem exitMi = new JMenuItem("Exit");
        exitMi.setMnemonic(KeyEvent.VK_E);
        exitMi.setToolTipText("Exit application");
        exitMi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));

        exitMi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });

        fileMenu.add(gameMenu);
        gameMenu.add(newMi);
        gameMenu.add(PlayMi);
        gameMenu.add(LoadMapMi);
        fileMenu.addSeparator();
        fileMenu.add(aboutMi);
        fileMenu.add(exitMi);
        
        menubar.add(fileMenu);
        setJMenuBar(menubar);
        

        JCheckBoxMenuItem sbarMi = new JCheckBoxMenuItem("Show statubar");
        sbarMi.setMnemonic(KeyEvent.VK_S);
        sbarMi.setDisplayedMnemonicIndex(5);
        sbarMi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
        sbarMi.setSelected(true);
        
        sbarMi.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                
//                if (e.getStateChange() == ItemEvent.SELECTED) {
//                    statusbar.setVisible(true);
//                } else {
//                    statusbar.setVisible(false);
//                }
                
            }

        });
        fileMenu.add(sbarMi);
    }
    
    private class MenuItemAction extends AbstractAction {
        public MenuItemAction(String text, Integer mnemonic) {
            super(text);
            putValue(MNEMONIC_KEY, mnemonic);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println(e.getActionCommand());
        }
    }
}