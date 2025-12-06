/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package snakegame;


import java.awt.Dimension;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
/**
 *
 * @author omar
 */
public class SnakeGUI {
    private JFrame frame;
    private GameEngine gameEngine;
    private DatabaseManager dbManager;

    public SnakeGUI() {
        frame = new JFrame("Snake");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(800, 600));
        frame.setResizable(false);
        
        dbManager = new DatabaseManager();
        gameEngine = new GameEngine();
        frame.getContentPane().add(gameEngine);
        
        setupMenu();

        frame.pack();
        frame.setVisible(true);
        gameEngine.displayWelcomeScreen();
    }
    public GameEngine getGameEngine() {
        return gameEngine;
    }
    
    private void setupMenu() {
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        
        JMenu gameMenu = new JMenu("Game");
        menuBar.add(gameMenu);
        
        JMenuItem newGameItem = new JMenuItem("New Game");
        newGameItem.addActionListener(e -> gameEngine.restart());
        gameMenu.add(newGameItem);
        
        JMenuItem highScoresItem = new JMenuItem("High Scores");
        highScoresItem.addActionListener(e -> showHighScores());
        gameMenu.add(highScoresItem);
        
        gameMenu.addSeparator();
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        gameMenu.add(exitItem);
    }
    
    private void showHighScores() {
        dbManager.connect();
        List<String> scores = dbManager.getHighScores();
        dbManager.disconnect();
        
        StringBuilder scoresText = new StringBuilder("--- TOP 10 HIGH SCORES ---\n\n");
        if (scores.isEmpty()) {
            scoresText.append("No scores recorded yet.");
        } else {
            for (String score : scores) {
                scoresText.append(score).append("\n");
            }
        }
        
        JOptionPane.showMessageDialog(frame, scoresText.toString(), "High Scores", JOptionPane.INFORMATION_MESSAGE);
    }
}
