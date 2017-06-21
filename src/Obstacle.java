/**
 * @(#)Obstacle.java
 *
 *
 * @Leon Ouyang
 * @A class that makes obstacles. Obstacles are anything the frog interacts with in the game. This class performs different tasks based on the
 * type of obstacle created.
 */

import java.awt.*;
import java.util.*;
import javax.swing.*;
public class Obstacle {
	
	private Rectangle box;//hitbox that stores info on position and dimensions
	private String dir,type; //direction, type of obstacle
	private int speed, tdelay, delay; //speed, train delay(only used if its a train), respawn delay
	private Image img, warnimg = new ImageIcon("images/warning.png").getImage(); //image of the obstacle, warning image (only used if its a train)
	private boolean warn; //should the warnimg be drawn (only used if its a train)
	//private static Obstacle firstGuy=null;

    public Obstacle(Image img, int x, int y, int s,String dir,String type) { //constructor
    	//assigns the appropriate info to each field
    	this.img = img;
    	box = new Rectangle(x,y,img.getWidth(null),img.getHeight(null));
    	speed = s;
    	this.dir = dir;
    	this.type = type;
    	tdelay = 0;
    	if (speed!=0){
    		delay = (int)(100/speed);
    	}
    	else{
    		delay = 0;
    	}
    	warn = false;
    }
    
    public Obstacle(String line){ //constructor used with file io
    	String[] items = line.split(",");
    	//assigns the appropriate info to each field
    	img = new ImageIcon("images/"+items[0]).getImage();
    	box = new Rectangle(0,0,img.getWidth(null),img.getHeight(null));
    	dir = items[1];
    	type = items[2];
    	tdelay = 0;
    	delay = 0;
    	warn = false;
    }
    
    public Obstacle copy(){ //returns a copy of the obstacle
    	return new Obstacle(img,getX(),getY(),speed,dir,type);
    }
    
    public void draw(Graphics g){ //draws the obstacle
		
    	if (warn){ //if theres a warning, draw the warning image (only used with trains)
    		g.drawImage(warnimg,0,(int)box.getY(),null);
    	}
    	g.drawImage(img,(int)box.getX(),(int)box.getY(),null); 
    }
    
    public void move(){ //moves the obstacle
    	if (dir.equals("r")){
    		moveRight(speed);
    	}
    	else if (dir.equals("l")){
    		moveLeft(speed);
    	}
    	
    	
 
    }
    
    public void reset(int w){ //resets the obstacle
    	if (!(type.equals("train"))){ //if its not a train
    		delay -= 1;
    		if (delay<=0){ //if the respawn delay has finished
    		//reset the obstacle to the opposite side of the screen
    			if (dir.equals("r")){
	    			setLoc(-getWidth(),getY());
	    		}
	    		else if (dir.equals("l")){
	    			setLoc(w,getY());
	    		}
	    		delay = (int)(100/speed);
    		}
    		
    	}
    	else if (tdelay<=0){ //if its a train and the train delay is finished
    	//reset the obstacle to the opposite side of the screen
	    	if (dir.equals("r")){
	    		setLoc(-getWidth(),getY());
	    	}
	    	else if (dir.equals("l")){
	    		setLoc(w,getY());
	    	}
	    	
    		tdelay = 400;
    		
    	}
    	else if (tdelay==100){//if the delay has 100 frames left, begin the warning
    		warn = true;
    	}
    	else if(tdelay==350){ //when the train comes, stop the warning
    		warn = false;
    	}
   
    }
    
    //moves the obstacle in the appropriate direction
    public void moveRight(int speed){ 
    	box.translate(speed,0);
    }
    
    public void moveLeft(int speed){
    	box.translate(-speed,0);
    }
    
    
    public void pullFrog(Frog f){ //pulls the frog (used with logs)
    	f.setSelfmove(false); //tells the frog that its not moving itself
    	//moves the frog
    	if (dir.equals("r")){
    		f.moveRight(speed);
    	}
    	else if (dir.equals("l")){
    		f.moveLeft(speed);
    	}
    }
    
    public void check(Frog f){ //checks to see if the obstacle is intersecting with the frog
    	if (f.intersects(this)){
    		if (type.equals("log")||type.equals("lily")){ //if the frog is standing on a log or lily, set the frog to be afloat and pull the frog
    			pullFrog(f);
    			f.setAfloat(true);
    		}
    		if (type.equals("vehicle")||type.equals("train")){ //if the frog is hit by a vehicle or train, kill the frog
    			f.reset();
    			f.die();
    			f.setAlive(false);
    		}
    		if (type.equals("fly")){ //if the frog collected a fly
    			f.setAlive(false); //the frog does not actually die but it is treated this way so that its position and score can reset
    		}
    	}
    }
    
    
    public void refreshTDelay(int x){ //updates the train delay
    	tdelay -= x;
    }
    
    //set and get functions
    public void setLoc(int lx, int ly){
    	box.setLocation(lx,ly);
    }
    
    public void setSpeed(int s){
    	speed = s;
    }
    
    public void setDelay(int d){
    	delay = d;
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
    
     public int getHeight(){
    	return (int)box.getHeight();
    }
    
     public int getWidth(){
    	return (int)box.getWidth();
    }
    
    public String getDir(){
    	return dir;
    }
    
    public int getSpeed(){
    	return speed;
    }
    
    public String getType(){
    	return type;
    }
    public int getTDelay(){
    	return tdelay;
    }
    
}