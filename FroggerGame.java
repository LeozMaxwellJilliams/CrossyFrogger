/**
 * @(#)FroggerGame.java
 *
 *
 * @Leon Ouyang
 * @The class that actually runs the game. It creates a frog and obstacles for you to cross in order to get to the other side. 
 */
 
import javax.sound.sampled.AudioSystem;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.MouseInfo;
import java.util.*;
import java.io.*;

public class FroggerGame extends JFrame implements ActionListener{
	javax.swing.Timer myTimer, gameTimer;   
	GamePanel game;
	public static final int MENU = 0, INSTRUCTIONS = 1, GAME = 2, END = 3, WIN = 4;
		
    public FroggerGame() {
		super("Froggerish game");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800,630);

		myTimer = new javax.swing.Timer(10, this);	 // trigger every 10 ms
		gameTimer = new javax.swing.Timer(1000,this); //timer used to keep track of seconds left in each stage 

		game = new GamePanel(this);
		add(game);
	
		
		setResizable(false);
		setVisible(true);
    }
	
	public void start(){
		myTimer.start();
		gameTimer.start();
	}

	public void actionPerformed(ActionEvent evt){
		Object source = evt.getSource();
		if (source==myTimer){ //if myTimer triggered this event
			if (game.getScreen()==GAME){ //if you're in the game
				game.move();
				game.check();
			}
			else if (game.getScreen()==MENU){ //if you're at the menu
				game.menu();
			}
			else if (game.getScreen()==INSTRUCTIONS){ //if you're at the instructions page
				game.instructions();
			}
			else if (game.getScreen()==END || game.getScreen()==WIN){ //if you're at one of the end screens
				game.end();
			}
			game.repaint();
			game.resetMouse();
		}
		if (source==gameTimer){ //if gameTimer triggered this event
			if (game.getScreen()==GAME){
				game.timer();
			}
		}
			
		
	}

    public static void main(String[] arguments) { //creates the game
		FroggerGame frame = new FroggerGame();		
    }
}

class GamePanel extends JPanel implements KeyListener,MouseListener{
	public static final int MENU = 0, INSTRUCTIONS = 1, GAME = 2, END = 3, WIN = 4; //final ints for the different screens
	private boolean displayinglevel, draweagle; //are we displaying the level title or drawing the eagle
	private FroggerGame mainFrame;
	private Frog frog; 
	private ArrayList<Obstacle>vs; //array list of all the obstacles in the level
	private ArrayList<Platform>ps,AllPlats; //array list of all the platforms in the level, all the possible platforms
	private ArrayList<ArrayList<ArrayList<Obstacle>>>AllObs;//all the possible obstacles
	private int score,downcount,curlevel,maxlevel,time,cx,cy,eaglex,eagley,curscreen; //downcount is the number of steps back the frog takes,
	//cx,cy are the x and y of clicks you make, curscreen is the current screen, the rest are self explainatory
	private Image clockPic, scorePic, eaglePic;
	private Image [] levelPics,menuPics;
	private Rectangle playbox, instructbox, backbox, menubox, retrybox; //hitboxes for buttons on the menus
	private AudioClip back,jump;
	
	public GamePanel(FroggerGame m){ //constructor
		setSize(800,600);
		
		frog = new Frog(400,575,25,25);//creates the frog
		
		//load the music
		jump = Applet.newAudioClip(getClass().getResource("dp_frogger_hop.wav"));
		back = Applet.newAudioClip(getClass().getResource("dp_frogger_start.wav"));
		
		back.loop();
		
		score = downcount = cx = cy = eaglex = eagley = 0;
		curlevel = 1;
		maxlevel = 6;
		time = 60;
		curscreen = MENU; //starts the current screen at the menu
		
		//initialize the different buttons
		playbox = new Rectangle(275,297,250,50);
		instructbox = new Rectangle(275,407,250,50);
		backbox = new Rectangle(52,499,250,50);
		menubox = new Rectangle(120,420,250,50);
		retrybox = new Rectangle(437,420,250,50);
		
		//load pictures
		levelPics = new Image[maxlevel];
		for (int i=0;i<maxlevel;i++){
    		levelPics[i] = new ImageIcon("images/level"+(i+1)+".png").getImage();
    	}
    	
    	menuPics = new Image[4];
    	for (int i=0;i<4;i++){
    		menuPics[i] = new ImageIcon("images/menu"+(i+1)+".png").getImage();
    	}
		
		clockPic = new ImageIcon("images/clock.png").getImage();
		scorePic = new ImageIcon("images/score.png").getImage();
		eaglePic = new ImageIcon("images/eagle.png").getImage();
		
		displayinglevel = draweagle = false;
		
		Scanner infile = null; 
    	
    	try{ //catch errors in file IO
    		infile = new Scanner(new File("obstacles.txt"));//read in all the possible obstacles
    	}
    	catch(IOException ex){
    		System.out.println(ex);
    	}
    	int n;
    	n = Integer.parseInt(infile.nextLine());
    	
    	AllPlats = new ArrayList<Platform>(); //all possible platforms
    	
    	AllObs = new ArrayList<ArrayList<ArrayList<Obstacle>>>(); //all possible obstacles
    	//the obstacles are divided into different types and than into different directions, which is why AllObs is a 3D arraylist
    	//2D array lists of the different types of obstacles
    	ArrayList<ArrayList<Obstacle>>	AllVehicles = new ArrayList<ArrayList<Obstacle>>();
    	ArrayList<ArrayList<Obstacle>>	AllLogs = new ArrayList<ArrayList<Obstacle>>();
    	ArrayList<ArrayList<Obstacle>>	AllBarriers = new ArrayList<ArrayList<Obstacle>>();
    	ArrayList<ArrayList<Obstacle>>	AllTrains = new ArrayList<ArrayList<Obstacle>>();
    	ArrayList<ArrayList<Obstacle>>	AllLilys = new ArrayList<ArrayList<Obstacle>>();
    	ArrayList<ArrayList<Obstacle>>	AllFlys = new ArrayList<ArrayList<Obstacle>>();
    	
    	for(int i=0;i<2;i++){ //adds two array lists to each, one for each possible direction
	    	AllVehicles.add(new ArrayList<Obstacle>());
	    	AllLogs.add(new ArrayList<Obstacle>());
	    	AllBarriers.add(new ArrayList<Obstacle>());
	    	AllTrains.add(new ArrayList<Obstacle>());
	    	AllLilys.add(new ArrayList<Obstacle>());
	    	AllFlys.add(new ArrayList<Obstacle>());
    	}
    	
    	//adds these to AllObs
    	AllObs.add(AllVehicles);
    	AllObs.add(AllLogs);
    	AllObs.add(AllBarriers);
    	AllObs.add(AllTrains);
    	AllObs.add(AllLilys);
    	AllObs.add(AllFlys);
    	
    	for(int i=0;i<n;i++){ //creates the Obstacle objects and stores them in their corresponding array list
    		String line = infile.nextLine();
    		Obstacle v = new Obstacle(line);
    		if (v.getType().equals("vehicle")){ //if its a vehicle 
    			if(v.getDir().equals("r")){ //if its going right
    				AllVehicles.get(0).add(v); //add it to this arraylist
    			}
    			else if(v.getDir().equals("l")){
    				AllVehicles.get(1).add(v);
    			}
    		}
    		else if (v.getType().equals("log")){
    			if(v.getDir().equals("r")){
    				AllLogs.get(0).add(v);
    			}
    			else if(v.getDir().equals("l")){
    				AllLogs.get(1).add(v);
    			}
    		}
    		else if (v.getType().equals("barrier")){ //barriers don't move so direction doensn't make a difference
    			
    			AllBarriers.get(0).add(v);
    			AllBarriers.get(1).add(v);
    			
    		}
    		else if (v.getType().equals("train")){
    			if(v.getDir().equals("r")){
    				AllTrains.get(0).add(v);
    			}
    			else if(v.getDir().equals("l")){
    				AllTrains.get(1).add(v);
    			}
    		}
    		else if (v.getType().equals("lily")){
    			
    			AllLilys.get(0).add(v);
    			AllLilys.get(1).add(v);
    			
    		}
    		else if (v.getType().equals("fly")){
    			AllFlys.get(0).add(v);
    			AllFlys.get(1).add(v);
    		}
    	}
    	
    	try{ //catch errors in file IO
    		infile = new Scanner(new File("platforms.txt")); //read in all possible platforms
    	}
    	catch(IOException ex){
    		System.out.println(ex);
    	}
    	n = Integer.parseInt(infile.nextLine());
    	
		for(int i=0;i<n;i++){ //creates the platform objects and stores them in AllPlats
			String line = infile.nextLine();
			//System.out.println(line);
			Platform p = new Platform(line);
    		AllPlats.add(p);
    	}
    	
    	loadlevel(1); //loads the first level
		
		mainFrame = m;
	
        addKeyListener(this);
        addMouseListener(this);
	}
	
	public void loadlevel(int n){ //function to load levels
		Scanner infile = null;
		
		try{ //catch errors in file IO
    		infile = new Scanner(new File("level"+Integer.toString(n)+".txt")); //reads in the level's text file
    	}
    	catch(IOException ex){
    		System.out.println(ex);
    	}
    	//int n;
    	n = Integer.parseInt(infile.nextLine());
    	
    	vs = new ArrayList<Obstacle>(); //array list of all the obstacles in the level
    	ps = new ArrayList<Platform>(); //array list of all the platforms in the level
    	
    	for(int i=0;i<n;i++){ //creates the level line by line
    		String line = infile.nextLine();
    		String[] items = line.split(",");
    		int size = AllObs.get(Integer.parseInt(items[1])).get(0).size();//how many of that type of obstacle are there to choose from
    		int y = (int)(Math.random()*2), pindex = 0; //y is used for randomly choosing direction of the obstacle, pindex is the platform index
    		
    		//chooses the appropriate platform to match the obstacles of the current line
    		if(Integer.parseInt(items[1])==0){//if the obstacle being read in is a vehicle
    			if(ps.size()!=0){
    				if(ps.get(ps.size()-1).getType().equals("road")&&ps.get(ps.size()-1).getY()==Integer.parseInt(items[3])-25){
    				pindex = 1; //if there is a road directly above this one, use the dotted lined road as the platform for this line
	    			}
	    			else{
	    				pindex = 0; //else, use a normal road
	    			}
    			}
    			else{
    				pindex = 0;
    			}
    			
    		}
    		else if(Integer.parseInt(items[1])==1||Integer.parseInt(items[1])==4||Integer.parseInt(items[1])==5){ //if the obstacle is water based
    			pindex = 2;//use water as the platform
    		}
    		else if(Integer.parseInt(items[1])==2){ //if the obstacle is grass based
    			//alternate between the two different types of grass
    			if(Integer.parseInt(items[3])%10==0){ 
    				pindex = 3;
    			}
    			else{
    				pindex = 4;
    			}
    			
    		}
    		else if(Integer.parseInt(items[1])==3){//if the obstacle is a train
    			pindex = 5; //use a railroad
    		}
    		Platform p = AllPlats.get(pindex).copy(); //take the chosen platform from AllPlats
    		p.setLoc(0,Integer.parseInt(items[3])); //set it to the appropriate location
    		ps.add(p); //add it to ps
    		
    		//choose random obstacles given the type, amount, and speed
    		for (int j=0;j<Integer.parseInt(items[0]);j++){
    		
    			int x = (int)(Math.random()*size); //chooses a random obstacle of the given type
    			
    			
    			Obstacle v = AllObs.get(Integer.parseInt(items[1])).get(y).get(x).copy();//takes the chosen obstacle from AllObs
    			if (Integer.parseInt(items[2])==0){//if it has no speed
    				v.setLoc(Integer.parseInt(items[4])+(j*Integer.parseInt(items[5])),Integer.parseInt(items[3]));//you can set the obstacles' x coords too
	    			v.setSpeed(0);
	    			v.setDelay(0);
    			}
    			else{
	    			v.setLoc(j*(getWidth()+v.getWidth())/Integer.parseInt(items[0]),Integer.parseInt(items[3]));//sets the location,speed, and delay
	    			v.setSpeed(Integer.parseInt(items[2]));
	    			v.setDelay((int)(100/v.getSpeed()));
    			}
    			vs.add(v);// adds it to vs
    		}
   
    	}
    	
    	for (int i=0;i<24;i++){//goes through the whole screen and checks if there are any lines with no obstacles on them
    		boolean nostuff = true;
    		int pindex = 0;
    		for (Platform p:ps){
    			if (p.getY()==i*25){
    				nostuff = false;
    			}
    		}
    		if (nostuff){ //if there areno obstacles on the given line, make the platform of that line grass
    		//alternate between the two grasses
    			if(i*25%10==0){
    				pindex = 3;
    			}
    			else{
    				pindex = 4;
    			}
    			Platform p = AllPlats.get(pindex).copy();
    			p.setLoc(0,i*25);
    			ps.add(p);//add the chosen platform
    		}
    	}
    	
    	displayinglevel = true;//display the level's title
	}
	
    public void addNotify() {
        super.addNotify();
        requestFocus();
        mainFrame.start();
    }
    
    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {
    	if (curscreen == GAME){//if you're screen is on the game
	    	if (e.getKeyCode()==KeyEvent.VK_SPACE){ //if you press space, stop displaying the level title
	    		displayinglevel = false;
	    	}
	    	if (displayinglevel == false && draweagle == false){ 
	    		frogmove(e); 
	    	}
    	}      
    }
    
    public void keyReleased(KeyEvent e) {
    }
    
    public void mouseClicked(MouseEvent e) {//gets the mouse pos if you click
    	cx = e.getX();
    	cy = e.getY();
    }
    
    public void mouseEntered(MouseEvent e) {
    }
    
    public void mouseExited(MouseEvent e) {
    }
    
    public void mousePressed(MouseEvent e) {
    }
    
    public void mouseReleased(MouseEvent e) {
    }
    
    public void resetMouse(){
    	cx = cy = 0;
    }
	
	//functions that check if you click on any buttons.
	//if you do, it will change the current screen to the appropriate one
	public void menu(){ //if you are on the menu
		if (playbox.contains(cx,cy)){//if you click play, the screen becomes GAME
			curscreen = GAME;
		}
		if (instructbox.contains(cx,cy)){
			curscreen = INSTRUCTIONS;
		}

	}
	
	public void instructions(){//if you are on the instructions page
		if (backbox.contains(cx,cy)){
			curscreen = MENU;
		}
	}
	
	public void end(){
		if (menubox.contains(cx,cy)){
			curscreen = MENU;
		}
		if (retrybox.contains(cx,cy)){
			curscreen = GAME;
		}
	}
	
	public void move(){ //moves all the obstacles
		
		for (Obstacle v:vs){ //goes through all the obstacles and moves them
			if (isOffScreen(v)){
				if (v.getType().equals("train")){
    				v.refreshTDelay(1);
    			}
				v.reset(getWidth());
			}
			v.move();
		}

		
	}
	
	public void frogmove(KeyEvent e){ //moves the frog
		int oldx = frog.getX(), oldy = frog.getY(), oldscore = score, olddowncount = downcount;//stores old stats in case you can't move
		boolean offlog = true; //did you just get off a log
		
		frog.setSelfmove(true);
		
		if(e.getKeyCode()==KeyEvent.VK_RIGHT ){
			jump.play();
			frog.moveRight(25);
		}
		if(e.getKeyCode()==KeyEvent.VK_LEFT ){
			jump.play();
			frog.moveLeft(25);
		}
		if(e.getKeyCode()==KeyEvent.VK_UP ){
			jump.play();
			frog.moveUp(25);
			if (downcount == 0){ //downcount keeps track of how many steps you took backwards 
				score+=1; //adds to your score only if you move further than you'd previously gone
			}
			downcount = Math.max(downcount-1,0);
		}
		if(e.getKeyCode()==KeyEvent.VK_DOWN){
			jump.play();
			frog.moveDown(25);
			downcount+=1;
		}
		
		
		if (frogisOffScreen(frog)){ //if frog is going offscreen, don't move it
			frog.setLoc(oldx,oldy);
			score = oldscore;
			downcount = olddowncount;
		}
		for(Obstacle v:vs){
			if (v.getType().equals("barrier") && frog.intersects(v)){ //if the frog runs into a barrier, don't move it
				frog.setLoc(oldx,oldy);
				score = oldscore;
				downcount = olddowncount;
			}
			if (v.getType().equals("log") && frog.intersects(v)){
				offlog = false;
			}
		}
		if (offlog && frog.getX()%frog.getWidth()!=0){ //if you are not on a log, the frog will be rounded to the nearest grid unit (25 pixels)
			frog.setSelfmove(false);
			if(frog.getX()%frog.getWidth()>=10){
				frog.moveRight(frog.getWidth()-(frog.getX()%frog.getWidth()));
			}
			else {
				frog.moveLeft(frog.getX()%frog.getWidth());
			
			}
		}
	}
	
	public void check(){ //checks for interactions between obstacles, platforms, and frog
		int rindex = -1; //stores the indices of any flys you need to remove from vs
		boolean noflys = true;
		frog.setAfloat(false);
		for (Obstacle v:vs){ //checks for interactions between obatacles and frog
			int oldx = frog.getX(), oldy = frog.getY();
			v.check(frog);
			//if a log moved the frog somewhere where he can't go, don't move him
			if (v.getType().equals("barrier") && frog.intersects(v)){
				frog.setLoc(oldx,oldy);
			}
			if (frogisOffScreen(frog)){
				if (frog.isAfloat()){
					//if a log moved the frog off screen, the frog dies
					frog.reset();
					frog.die();
	    			frog.setAlive(false);
				}
				else{
					frog.setLoc(oldx,oldy);
				}
			}
			if(frog.intersects(v) && v.getType().equals("fly")){
				rindex = vs.indexOf(v); 
			}
		}
		if (rindex!=-1){
			vs.remove(rindex); //removes any flies that the frog caught
			score+=10; //you get 10 added to your score for collecting a fly
			for (Obstacle v:vs){ //checks if there are any more flies left
				if (v.getType().equals("fly")){
					noflys = false;
				}
			}
			if (noflys){ //if there are no flies, you move on to the next level
				score+=100;
				//clears all the obstacles and platforms
				vs.clear();
				ps.clear();
				if (curlevel==maxlevel){//if there are no more levels, you get sent to the win menu
					//resets the level and frog incase you play again
					curlevel = 1;
					loadlevel(curlevel);
					frog.setLives(5);
					score = 0;
					curscreen = WIN;
				}
				else{
					curlevel+=1;
					loadlevel(curlevel);//load the next level
				}
			}
			frog.reset();
		}
		
		for (Platform p:ps){//checks for interactions between the platforms and the frog
			p.check(frog);
		}
		if (!(frog.isAlive())){// if the frog isn't alive
			if (frog.getLives()==0){ //if you have no more lives left
			//reset everything and you get sent the the end menu to possibly retry
				vs.clear();
				ps.clear();
				curlevel=1;
				loadlevel(curlevel);
				frog.setLives(5);
				score = 0;
				curscreen = END;
			}
			resetScore();
			frog.setAlive(true);
		}
		
	}
	
	public void timer(){ //keeps track of the time you have left on each stage
		if (displayinglevel == false && draweagle == false){ //if you are actually playing, then keep track of time
			time-=1;
		}
		if (time==0){ //if you ran out of time, send in the eagle to eat the frog
			draweagle = true;
			eagley = -95;
			eaglex = frog.getX()-25;
		}
		if (time<0){ //keeps the time at 0 until the eagle gets the frog
			time = 0;
		}
	}
	
    
    public boolean isOffScreen(Obstacle o){ //is the obstacle off screen
    	if (o.getDir().equals("l")){
    		return o.getX()<0;
    	}
    	else{
    		return o.getX()+o.getWidth()>getWidth();
    	}
    }
    
    public boolean frogisOffScreen(Frog frog){ //is the frog off screen
    	return frog.getX()+frog.getWidth()>getWidth()||frog.getX()<0||frog.getY()<0||frog.getY()+frog.getHeight()>getHeight();
    }
    
    public void resetScore(){ //resets the score and time
    	downcount = 0;
    	time = 60;
    }
	
    public void drawStats(Graphics g){ //draws the time, score, and lives
    	g.setColor(Color.white);
		g.setFont(new Font("Calibri", Font.BOLD, 18));
		
		g.drawImage(frog.getImages()[4],700,10,null);
		g.drawString("x "+Integer.toString(frog.getLives()),728,28);
		
		g.drawImage(clockPic,0,0,null);
		g.drawImage(scorePic,0,45,null);
		
		String t = time/60+":"+time%60;
		if (time%60==0){
			t += "0";
		}
		g.drawString(t,42,28);
		g.drawString(Integer.toString(score),42,73); 
    }
    
    public void paintComponent(Graphics g){ //draw everything
    	if (curscreen == GAME){ //draw all the objects used in the game
    		g.setColor(new Color(180,230,85));	
	    	g.fillRect(0,0,getWidth(),getHeight());
	
	    	for (Platform p:ps){
				p.draw(g);
			} 
			
			for (Obstacle v:vs){
				v.draw(g);
			}
			
			frog.draw(g);
			
			drawStats(g);
			
			if (displayinglevel){
				g.drawImage(levelPics[curlevel-1],0,0,null);
			}
			
			if (draweagle){ //draws the eagle until it catches the frog, then the frog dies
				g.drawImage(eaglePic,eaglex,eagley,null);
				eagley+=10;
				if (eagley>=frog.getY()){
					frog.die();
					frog.reset();
					frog.setAlive(false);
					time = 60;
					draweagle = false;
				}
			}
    	}
    	//draw the approprate menu that your screen is on
    	else if (curscreen == MENU){
    		g.drawImage(menuPics[0],0,0,null);
    	}
    	else if (curscreen == INSTRUCTIONS){
    		g.drawImage(menuPics[1],0,0,null);
    	}
    	else if (curscreen == END){
    		g.drawImage(menuPics[2],0,0,null);
    	}
    	else if (curscreen == WIN){
    		g.drawImage(menuPics[3],0,0,null);
    	}
    	
	
		
    }
    //get function
    public int getScreen(){
    	return curscreen;
    }
}
