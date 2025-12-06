/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package snakegame;

import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Random;
/**
 *
 * @author omar
 */
public class snake {
    private ArrayList<snakeBodyPart> body;
    private int velx;
    private int vely;
    private final Image headImage;
    private final Image bodyImage;
    private final int partSize;

    public snake(int startX, int startY, int partSize, Image headImage, Image bodyImage) {
        this.partSize = partSize;
        this.headImage = headImage;
        this.bodyImage = bodyImage;
        body = new ArrayList<>();
        
        body.add(new snakeBodyPart(startX, startY, partSize, partSize, headImage));
        body.add(new snakeBodyPart(startX - partSize, startY, partSize, partSize, bodyImage));
        
        // Start in a random cardinal direction
        int startDir = new Random().nextInt(4);
        if (startDir == 0) { velx = partSize; vely = 0; } // Right
        else if (startDir == 1) { velx = -partSize; vely = 0; } // Left
        else if (startDir == 2) { velx = 0; vely = partSize; } // Down
        else { velx = 0; vely = -partSize; } // Up
    }

    public void move() {
        for (int i = body.size() - 1; i > 0; i--) {
            snakeBodyPart behind = body.get(i);
            snakeBodyPart ahead = body.get(i - 1);
            behind.x = ahead.x;
            behind.y = ahead.y;
        }
        snakeBodyPart head = getHead();
        head.x += velx;
        head.y += vely;
    }

    public void grow() {
        snakeBodyPart lastPart = body.get(body.size() - 1);
        body.add(new snakeBodyPart(lastPart.x, lastPart.y, partSize, partSize, bodyImage));
    }

    public boolean checkSelfCollision() {
        snakeBodyPart head = getHead();
        for (int i = 1; i < body.size(); i++) {
            if (head.collide(body.get(i))) {
                return true;
            }
        }
        return false;
    }
    
    public void draw(Graphics g) {
        for (sprite part : body) {
            part.drawImage(g);
        }
    }
    
    public void setVelX(int vx) {
        if (velx == 0) { // Can only change horizontal speed if moving vertically
            this.velx = vx;
            this.vely = 0;
        }
    }
    
    public void setVelY(int vy) {
        if (vely == 0) { // Can only change vertical speed if moving horizontally
            this.vely = vy;
            this.velx = 0;
        }
    }

    public snakeBodyPart getHead() { return body.get(0); }
    public ArrayList<snakeBodyPart> getBody() { return body; }
}