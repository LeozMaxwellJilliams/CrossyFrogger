/**
 * @(#)Platform.java
 *
 *
 * @Leon Ouyang
 * @A class that makes Platforms. Platforms make up the background of the game. This class mainly stores drawing info such as the images.
 */

import java.awt.*;
import java.util.*;
import javax.swing.*;
public class Platform {
	
	private String type; //the type of the platform(water, road, grass, railroad)
	private Rectangle box; //hitbox that stores info on position and dimensions
	private Image img; //the image of the platform

    public Platform(Image img,int x, int y, String type) { //contructor (image, position(x,y),type)
    	this.img = img; 
    	box = new Rectangle(x,y,img.getWidth(null),img.getHeight(null));
    	this.type = type;
    }
    
    public Platform(String line){//constructor used with file io
    	String[] items = line.split(","); //splits the info read in from the file
    	//assign the appropriate info to each field
    	img = new ImageIcon("images/"+items[0]).getImage();
    	box = new Rectangle(0,0,img.getWidth(null),img.getHeight(null));
    	type = items[1];
    }
    
    public Platform copy(){ //returns a copy of the platform
    	return new Platform(img,getX(),getY(),type);
    }
    
    public void draw(Graphics g){ //draws the platform
    	g.drawImage(img,(int)box.getX(),(int)box.getY(),null);
    	
    }
    
    public void check(Frog f){ //checks to see if the frog is on the platform
    	if (type.equals("water") && f.intersects(this) && !(f.isAfloat())){ //if the frog is on water and if frog is not on a log
    	//the frog dies and resets
    		f.reset();
    		f.die();
    		f.setAlive(false);
    	}
    }
    
    
    //get and set functions
    public void setLoc(int lx, int ly){
    	box.setLocation(lx,ly);
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
    
    public String getType(){
    	return type;
    }
    public Image getImage(){
    	return img;
    }
    
}