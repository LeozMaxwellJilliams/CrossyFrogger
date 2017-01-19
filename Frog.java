/**
 * @(#)Frog.java
 *
 *
 * @Leon Ouyang
 * @Creates frog objects. Frogs are the characters you control in game. This class stores statuses about the frog and it can move the frog. 
 */

import java.awt.*;
import java.util.*;
import javax.swing.*;
public class Frog {
	
	private int lives, delay, resetdelay; //lives, delay for frog sprites, delay for when the frog respawns
	private Rectangle box; //hitbox that stores info on position and dimensions
	private boolean afloat, alive, selfmove; //is the frog on a log or lily, is the frog alive, is the frog moving itself(versus being pulled by a log)
	private Image[] imgs; //stores the different frog sprites
	private Image img; //current sprite

    public Frog(int x, int y, int w, int h) {//constructor (position x, position y, width, height)
    	box = new Rectangle(x,y,w,h);
    	afloat = false;
    	lives = 5;
    	alive = true;
    	imgs = new Image[8];
    	for (int i=0;i<8;i++){ //adds all the frog images to imgs
    		imgs[i] = new ImageIcon("images/frog"+(i+1)+".png").getImage();
    	}
    	img = imgs[4];
    	delay = 0;
    	selfmove = true;    
    	resetdelay = 0;
    }
    
    //move functions that move the frog in the desired direction
    public void moveRight(int x){
    	box.translate(x,0);
    	if (selfmove){ //if the frog is moving itself
    		img = imgs[1]; //draw a moving animation
    		delay = 10; //for 10 frames
    	}
    	
    }
    
    public void moveLeft(int x){
    	box.translate(-x,0);
    	if (selfmove){
    		img = imgs[3];
    		delay = 10;
    	}
    }
    
    public void moveDown(int y){
    	box.translate(0,y);
    	if (selfmove){
    		img = imgs[7];
    		delay = 10;
    	}
    }
    
    public void moveUp(int y){
    	box.translate(0,-y);
    	if (selfmove){
    		img = imgs[5];
    		delay = 10;
    	}
    }
    
    //set function that sets the frog's location
    public void setLoc(int lx, int ly){
    	box.setLocation(lx,ly);
    }
    
    //resets the frog to the starting point
    public void reset(){
    	if (resetdelay<=0){//if he didn't just die
    		setLoc(400,575);
    		img = imgs[4];
    	}
    }
    
    public void die(){ //removes a life from the frog
    	if (resetdelay<=0){ //if he didn't just die
    		resetdelay = 100; //gives the frog 100 frames of invincibility
    		lives-=1;
    	}
    }
    
    public boolean isAlive(){ //checks if the frog is alive
    	return alive;
    }
    
    public void setAlive(boolean x){ //sets the frog to alive or not alive
    	alive = x;
    }
    
    public void draw(Graphics g){//draws the frog
    	resetdelay -= 1;
    	delay -= 1;
    	int index = Arrays.asList(imgs).indexOf(img); //index in imgs of the currrent frog picture
    	if (delay<=0 && index%2!=0){ //if delay is less than zero and the current image is a moving animation
    		index -= 1;
    		img = imgs[index]; //sets the frog to the corresponding stationary image
    	}
    	if ((resetdelay%100-resetdelay%10)/10%2 == 0 || resetdelay<=0){ //if the frog just died, than blink the frog
    		g.drawImage(img,(int)box.getX(),(int)box.getY(),null); //otherwise just draw the frog
    	}
    }
    
    public boolean intersects(Obstacle v){ //does the frog intersect an obstacle
    	return box.intersects(v.getBox());    	
    }
    
    public boolean intersects(Platform p){ //does the frog intersect a platform
    	return box.intersects(p.getBox());
    }
    
    public boolean isAfloat(){ //is the frog on a log or lily
    	return afloat;
    }
    
    
    //get and set functions
    public void setAfloat(boolean x){
    	afloat = x;
    }
    
    public Rectangle getBox(){
    	return box;
    }
    public int getX(){
    	return (int)box.getX();
    }
    public int getY(){
    	return (int)box.getY();
    }
    public int getWidth(){
    	return (int)box.getWidth();
    }
    public int getHeight(){
    	return (int)box.getHeight();
    }
    public int getLives(){
    	return lives;
    }
    public void setLives(int x){
    	lives = x;
    }
    public Image [] getImages(){
    	return imgs;
    }
    public void setImage(Image image){
    	img = image;
    }
    public void setSelfmove(boolean x){
    	selfmove = x;
    }
    
    
}