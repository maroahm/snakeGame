/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package snakegame;


import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

/**
 *
 * @author omar
 */
public class sprite {
    protected int x;
    protected int y;
    protected int height;
    protected int width;
    protected Image image;
    
    public sprite(int x, int y, int height, int width, Image image){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image;
    }
    
    public void drawImage(Graphics g){
        g.drawImage(image, x, y,width, height, null);
    }
    
    public boolean collide(sprite other){
        Rectangle thisRect = new Rectangle(x, y, width, height);
        Rectangle otherRect = new Rectangle(other.x, other.y, other.width, other.height);
        return thisRect.intersects(otherRect);
    }
    
    
    public int getX() { return x; }
    public int getY() { return y; }
}
