package planetwars.core;

import planetwars.publicapi.IStrategy;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.Random;

public final class GameWindow extends JFrame {

    // Main panels
    private JPanel main;                // Hosts the window
    private JScrollPane gameScroll;     // Handles small screen sizes
    private JPanel game;                // Hosts the actual game

    // Game controls
    private JPanel control;

    // Game config swings
    private JPanel configLayout;
    private JPanel configPane;
    private JPanel selectorLabels;
    private JPanel selectorBoxes;
    private JComboBox player1Selector;
    private JComboBox player2Selector;
    private JComboBox graphSelector;
    private JPanel randomGraphPane;
    private JButton randomGraphButton;

    // GUI swings
    private JPanel uiLayout;
    private JPanel uiPane;
    private JPanel fpsPane;
    private JSpinner fpsSpinner;
    private JPanel pathPane;
    private JButton pathButton;

    // Game flow swings
    private JPanel buttonLayout;
    private JPanel buttonPane;
    private JButton startGameButton;
    private JButton newGameButton;
    private JButton exitButton;
    private JButton pauseGameButton;


    private Class<? extends IStrategy> strategy1Class;
    private Class<? extends IStrategy> strategy2Class;

    private PlanetWarsFrame gameFrame;

    private static final int GAME_WINDOW_WIDTH = (int) Math.min(1200, Toolkit.getDefaultToolkit().getScreenSize().getWidth() - 50);
    private static final int GAME_WINDOW_HEIGHT = (int) Math.min(1000, Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 50);

    static final int PLANET_WARS_WIDTH = 1000;
    static final int PLANET_WARS_HEIGHT = 950;

    public GameWindow() throws FileNotFoundException {
        setSize(GAME_WINDOW_WIDTH, GAME_WINDOW_HEIGHT);
        add(main);
        initButtons();
        initSpinner();
        initSelectors();

        initGame();
    }

    public GameWindow(Class<? extends IStrategy> strategyClass, boolean isPlayerOne) throws FileNotFoundException {
        if (isPlayerOne) {
            this.strategy1Class = strategyClass;
        } else {
            this.strategy2Class = strategyClass;
        }

        setSize(GAME_WINDOW_WIDTH, GAME_WINDOW_HEIGHT);
        add(main);
        initButtons();
        initSpinner();
        initSelectors();

        if (isPlayerOne) {
            this.player1Selector.setEnabled(false);
        } else {
            this.player2Selector.setEnabled(false);
        }

        initGame();
    }

    public GameWindow(Class<? extends IStrategy> strategy1Class, Class<? extends IStrategy> strategy2Class) throws FileNotFoundException {
        this.strategy1Class = strategy1Class;
        this.strategy2Class = strategy2Class;

        setSize(GAME_WINDOW_WIDTH, GAME_WINDOW_HEIGHT);
        add(main);
        initButtons();
        initSpinner();
        initSelectors();

        this.player1Selector.setEnabled(false);
        this.player2Selector.setEnabled(false);

        initGame();
    }

    private void initGame() throws FileNotFoundException {
        IStrategy player1;
        IStrategy player2;

        if (this.strategy1Class == null) {
            final String jar1 = String.valueOf(player1Selector.getSelectedItem());
            player1 = Assets.loadPlayer(jar1);
        } else {
            player1 = Assets.loadPlayer(strategy1Class);
        }

        if (this.strategy2Class == null) {
            final String jar2 = String.valueOf(player2Selector.getSelectedItem());
            player2 = Assets.loadPlayer(jar2);
        } else {
            player2 = Assets.loadPlayer(strategy2Class);
        }

        String graph = String.valueOf(graphSelector.getSelectedItem());
        PlanetWars wars = new PlanetWars(player1, player2, graph);

        // If there is an existing gameFrame, we are restarting and need to cancel the old one.
        // If we don't cancel it, it will continue trying to draw and crash when it is removed from the window.
        if (gameFrame != null) {
            gameFrame.cancel();
            game.remove(gameFrame);
        }

        gameFrame = new PlanetWarsFrame(PLANET_WARS_WIDTH, PLANET_WARS_HEIGHT, "PlanetWars", wars);
        game.add(gameFrame);
        game.setPreferredSize(new Dimension(PLANET_WARS_WIDTH, PLANET_WARS_HEIGHT));
        gameFrame.pause();
    }

    private void initButtons() {
        startGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameFrame.start();
                toggleGraphSelectors(false);
                startGameButton.setEnabled(false);
                pauseGameButton.setEnabled(true);
            }
        });

        pauseGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameFrame.pause();
                toggleGraphSelectors(true);
                startGameButton.setEnabled(true);
                pauseGameButton.setEnabled(false);
            }
        });

        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    initGame();
                } catch (FileNotFoundException e1) {
                    // TODO: How do we give this as feedback?
                    e1.printStackTrace();
                }
                gameFrame.pause();
                gameFrame.repaint();
                toggleGraphSelectors(true);
                startGameButton.setEnabled(true);
                pauseGameButton.setEnabled(false);
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(1);
            }
        });
    }

    private void initSpinner() {
        fpsSpinner.setModel(new SpinnerNumberModel(60, 1, 60, 1));
        ((JSpinner.DefaultEditor) fpsSpinner.getEditor()).getTextField().setEditable(false);
        fpsSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                gameFrame.setFPS((Integer) fpsSpinner.getValue());
            }
        });
    }

    private void toggleGraphSelectors(boolean enable) {
        graphSelector.setEnabled(enable);
        randomGraphButton.setEnabled(enable);
    }

    private void initSelectors() {
        initPlayerConfigs();
        initGraphConfigs();
    }

    private void initPlayerConfigs() {
        String[] strategies = Assets.getStrategies();

        assert strategies != null;
        for (String strategy : strategies) {
            String clean = strategy.replaceAll(".jar", "");
            player1Selector.addItem(clean);
            player2Selector.addItem(clean);
        }

        player1Selector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                IStrategy player1 = Assets.loadPlayer(String.valueOf(player1Selector.getSelectedItem()));
                gameFrame.setPlayer1(player1);
            }
        });

        player2Selector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                IStrategy player2 = Assets.loadPlayer(String.valueOf(player2Selector.getSelectedItem()));
                gameFrame.setPlayer2(player2);
            }
        });
    }

    private void initGraphConfigs() {
        String[] graphs = Assets.getGraphs();

        for (String graph : graphs) {
            String clean = graph.replaceAll(".dot", "");
            graphSelector.addItem(clean);
        }

        graphSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    initGame();
                    gameFrame.repaint();
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
            }
        });

        randomGraphButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Random rand = new Random();
                graphSelector.setSelectedIndex(rand.nextInt(graphs.length));
                gameFrame.repaint();
            }
        });
    }
}
