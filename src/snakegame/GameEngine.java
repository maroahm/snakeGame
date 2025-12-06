/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package snakegame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.Timer;
/**
 *
 * @author omar
 */
public class GameEngine extends JPanel{
    private final int FPS = 10;
    private final int TILE_SIZE = 20;
    private final int GRID_WIDTH = 40;
    private final int GRID_HEIGHT = 30;
    
    private Timer gameLoop;
    private snake Snake;
    private food Food;
    private ArrayList<rock> rocks;
    private int score = 0;
    private boolean isGameOver = false;
    private String currentPlayerName = "Player"; 
    private boolean isGameStarted = false;
    private Image headImage, bodyImage,foodImage,rockImage, backgroundImage;
    private DatabaseManager dbManager;
    public GameEngine() {
        super();
        loadImages();
        setupKeyBindings();
        dbManager = new DatabaseManager();
        gameLoop = new Timer(1000 / FPS, new GameLoopListener());
        
         Snake = null;
        rocks = null;
        Food = null;
    }
    public void displayWelcomeScreen() {
        String name = showWelcomeDialog();
        if (name != null) {
            this.currentPlayerName = name;
            repaint();
        } else {
            System.exit(0);
        }
    }
    private String showWelcomeDialog() {
        JTextField nameField = new JTextField("Player", 15);
        Object[] message = {"Please enter your name:", nameField};
        
        Object[] options = {"Continue to Game", "Exit"};
        
        int result = JOptionPane.showOptionDialog(this, message, "Welcome to Snake!",
            JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        
        if (result == JOptionPane.YES_OPTION) { 
            String inputName = nameField.getText();
            return (inputName != null && !inputName.trim().isEmpty()) ? inputName.trim() : "Player";
        }
        return null; 
    }
    private void loadImages() {
        headImage = new ImageIcon("data/head.png").getImage();
        bodyImage = new ImageIcon("data/body.png").getImage();
        foodImage = new ImageIcon("data/food.png").getImage();
        rockImage = new ImageIcon("data/rock.png").getImage();
        backgroundImage = new ImageIcon("data/background.png").getImage();
    }
    public final void restart() {
        score = 0;
        isGameOver = false;
        
        int centerX = GRID_WIDTH / 2 * TILE_SIZE;
        int centerY = GRID_HEIGHT / 2 * TILE_SIZE;
        
        Snake = new snake(centerX, centerY, TILE_SIZE, headImage, bodyImage);
        rocks = new ArrayList<>();
        
        Random rand = new Random();
        int numberOfRocks = 5 + rand.nextInt(11);
        for (int i = 0; i < numberOfRocks; i++) {
             int x, y;
            boolean onSnakeOrRockOrCenter;
            do {
                onSnakeOrRockOrCenter = false;
                x = rand.nextInt(GRID_WIDTH) * TILE_SIZE;
                y = rand.nextInt(GRID_HEIGHT) * TILE_SIZE;
                for (sprite part : Snake.getBody()) {
                    if (part.getX() == x && part.getY() == y) {
                        onSnakeOrRockOrCenter = true;
                        break;
                    }
                }
                if (onSnakeOrRockOrCenter) continue;
                for (sprite rock : rocks) {
                    if (rock.getX() == x && rock.getY() == y) {
                        onSnakeOrRockOrCenter = true;
                        break;
                    }
                }
                if (onSnakeOrRockOrCenter) continue;
                if (x == centerX && y == centerY) {
                    onSnakeOrRockOrCenter = true;
                }
            } while (onSnakeOrRockOrCenter);
            rocks.add(new rock(x, y, TILE_SIZE, TILE_SIZE, rockImage));
        }
        
        spawnFood();
        
        gameLoop.start();
        repaint();
    }
    
    private void spawnFood() {
        Random rand = new Random();
        int x, y;
        boolean onSomething;
        do {
            onSomething = false;
            x = rand.nextInt(GRID_WIDTH) * TILE_SIZE;
            y = rand.nextInt(GRID_HEIGHT) * TILE_SIZE;
            for (sprite part : Snake.getBody()) {
                if (part.getX() == x && part.getY() == y) {
                    onSomething = true;
                    break;
                }
            }
            if(onSomething) continue;
            for (sprite rock : rocks) {
                if (rock.getX() == x && rock.getY() == y) {
                    onSomething = true;
                    break;
                }
            }
        } while (onSomething);
        Food = new food(x, y, TILE_SIZE, TILE_SIZE, foodImage);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, 800, 600, null);
        
        if (Snake != null) Snake.draw(g);
        if (Food != null) Food.drawImage(g);
        if (rocks != null) {
            for (rock r : rocks) {
                r.drawImage(g);
            }
        }
    } 
   private void setupKeyBindings() {
        this.getInputMap().put(KeyStroke.getKeyStroke("UP"), "pressed up");
        this.getActionMap().put("pressed up", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { Snake.setVelY(-TILE_SIZE); }
        });
        this.getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "pressed down");
        this.getActionMap().put("pressed down", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { Snake.setVelY(TILE_SIZE); }
        });
        this.getInputMap().put(KeyStroke.getKeyStroke("LEFT"), "pressed left");
        this.getActionMap().put("pressed left", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { Snake.setVelX(-TILE_SIZE); }
        });
        this.getInputMap().put(KeyStroke.getKeyStroke("RIGHT"), "pressed right");
        this.getActionMap().put("pressed right", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { Snake.setVelX(TILE_SIZE); }
        });
    }

     private void checkCollisions() {
        if (Snake == null) return;
        sprite head = Snake.getHead();
        if (head.collide(Food)) {
            Snake.grow();
            score++;
            spawnFood();
        }
        for (rock r : rocks) {
            if (head.collide(r)) gameOver();
        }
        if (head.getX() < 0 || head.getX() >= 800 || head.getY() < 0 || head.getY() >= 600) {
            gameOver();
        }
        if (Snake.checkSelfCollision()) {
            gameOver();
        }
    }
    
    
private void gameOver() {
        isGameOver = true;
        gameLoop.stop();
        String finalName = JOptionPane.showInputDialog(
            this,                           
            "Game Over! Your score: " + score + "\nPlease enter your name to save:", // message
            this.currentPlayerName                
        );
        
        if (finalName != null && !finalName.trim().isEmpty()) {
            dbManager.connect();
            dbManager.saveScore(finalName.trim(), score);
            dbManager.disconnect();
            
            JOptionPane.showMessageDialog(this, "Score saved for " + finalName.trim() + "!");
        }
        
    }


    class GameLoopListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!isGameOver) {
                Snake.move();
                checkCollisions();
            }
            repaint();
        }
    }
}
    
    

