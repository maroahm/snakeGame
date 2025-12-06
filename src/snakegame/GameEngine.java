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
    private Image headImage, bodyImage,foodImage,rockImage, backgroundImage;
    private DatabaseManager dbManager;
    public GameEngine(){
        super();
        loadImages();
        setupKeyBindings();
        dbManager = new DatabaseManager();
        restart();
    }
    private void loadImages() {
        // You will need to create a "data/images/" folder in your project root
        // and place these images there.
        headImage = new ImageIcon("data/images/head.png").getImage();
        bodyImage = new ImageIcon("data/images/body.png").getImage();
        foodImage = new ImageIcon("data/images/food.png").getImage();
        rockImage = new ImageIcon("data/images/rock.png").getImage();
        backgroundImage = new ImageIcon("data/images/background.png").getImage();
    }
    public final void restart(){
        score =0;
        isGameOver = false;
        Snake = new snake(GRID_WIDTH /2 * TILE_SIZE, GRID_HEIGHT /2 * TILE_SIZE,
                TILE_SIZE,headImage, bodyImage);
        rocks = new ArrayList<>();
        rocks.add(new rock(5*TILE_SIZE, 5*TILE_SIZE,TILE_SIZE, TILE_SIZE, rockImage));
        rocks.add(new rock(35 * TILE_SIZE, 25 * TILE_SIZE, TILE_SIZE, TILE_SIZE, rockImage));
        
        spawnFood();
        if(gameLoop != null){
            gameLoop.start();
        }
                
    }
    
    private void spawnFood(){
        Random rand = new Random();
        int x, y;
        boolean onSnakeOrRock;
        do{
            onSnakeOrRock = false;
            x = rand.nextInt(GRID_WIDTH) * TILE_SIZE;
            y = rand.nextInt(GRID_HEIGHT) * TILE_SIZE;
            
            for(sprite part : Snake.getBody()){
                if(part.getX() == x && part.getY() == y){
                    onSnakeOrRock= true;
                    break;
                }
            }
            for (sprite rock: rocks){
                if(rock.getX() == x && rock.getY() == y){
                    onSnakeOrRock = true;
                    break;
                }
            }
            
        }while(onSnakeOrRock);
        Food = new food(x, y, TILE_SIZE, TILE_SIZE, foodImage);
    }
    
    @Override
    protected void paintComponent(Graphics g){
        
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0,0 , 800, 600, null);
        Snake.draw(g);
        Food.drawImage(g);
        for(rock r:rocks){
            r.drawImage(g);
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
        sprite head = Snake.getHead();
        
        // Collision with food
        if (head.collide(Food)) {
            Snake.grow();
            score++;
            spawnFood();
        }
        
        // Collision with rocks
        for (rock r : rocks) {
            if (head.collide(r)) {
                gameOver();
            }
        }
        
        // Collision with walls
        if (head.getX() < 0 || head.getX() >= 800 || head.getY() < 0 || head.getY() >= 600) {
            gameOver();
        }
        
        // Collision with self
        if (Snake.checkSelfCollision()) {
            gameOver();
        }
    }
    
    private void gameOver() {
        isGameOver = true;
        gameLoop.stop();
        
        String playerName = JOptionPane.showInputDialog(this, 
                "Game Over! Your score: " + score + "\nPlease enter your name:", 
                "Save High Score", JOptionPane.PLAIN_MESSAGE);
        
        if (playerName != null && !playerName.trim().isEmpty()) {
            dbManager.connect();
            dbManager.saveScore(playerName.trim(), score);
            dbManager.disconnect();
        }
        
        // Optionally, ask to restart here or let the menu handle it.
        // For now, the user must use the menu.
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
    
    

