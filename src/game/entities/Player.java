package game.entities;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Player {
    private BufferedImage[] skins;
    private Rectangle box;
    private double x, y;
    private double speed;
    private int skinIndex;

    public Player(BufferedImage[] skins, int x, int y){
        this.skins=skins;
        this.x=x;
        this.y=y;
        speed=2;
        skinIndex=0;
        box=new Rectangle(x+16,y+18,28,28);
    }

    public void update(){
        switch(skinIndex){
            case 0:box.setBounds((int)x+16,(int)y+18,32,44);break;
            case 1:box.setBounds((int)x+16,(int)y+8,30,38);break;
            case 2:box.setBounds((int)x+16,(int)y+16,46,30);break;
            case 3:box.setBounds((int)x+4,(int)y+16,46,30);break;
        }

    }
    public void moveUp(){
        y-=speed;
        skinIndex=0;
    }
    public void moveDown(){
        y+=speed;
        skinIndex=1;
    }
    public void moveLeft(){
        x-=speed;
        skinIndex=2;
    }
    public void moveRight(){
        x+=speed;
        skinIndex=3;
    }

    public void render(Graphics g){
        g.drawImage(skins[skinIndex],(int)x,(int)y,64,64,null);
//        g.setColor(Color.RED);
//        g.fillRect(box.x,box.y,box.width,box.height);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setSpeed(double newSpeed) {
    speed=newSpeed;
    }
}
