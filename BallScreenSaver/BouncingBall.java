//BouncingBall.java
//by Shanti Pothapragada, poth0018@umn.edu, Chad Myers, chadm@umn.edu,
//   Ryan Zoeller, zoell031@umn.edu

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
 
public class BouncingBall extends AnimationFrame{
	private double ballX, ballY, ballXSpeed, ballYSpeed;
	private int ballSize=18;
	private final int START_SPEED=300;
	public final int BORDER=30;
	
	/* These are data members for managing the collision logging, which
	   you should also use in your class. */
	private CollisionLogger collisionLogger;
	private static final int COLLISION_BUCKET_WIDTH = 20;
    private int saveCounter=0;


	
	public BouncingBall(){
		super();
		ballX=randdouble(BORDER,getWidth()-BORDER);
		ballY=randdouble(BORDER,getHeight()-BORDER);
		setRandDir(START_SPEED);
		setFPS(20);
		/* This instantiates the CollisionLogger, which you should also do in your class. */
        collisionLogger = new CollisionLogger(this.getWidth(),this.getHeight(), COLLISION_BUCKET_WIDTH);
	}
	
	public BouncingBall(int w, int h, String name){
		super(w,h,name);
		ballX=randdouble(BORDER,getWidth()-BORDER);
		ballY=randdouble(BORDER,getHeight()-BORDER);
		setRandDir(START_SPEED);
		setFPS(20);
		/* This instantiates the CollisionLogger, which you should also do in your class. */
        collisionLogger = new CollisionLogger(this.getWidth(), this.getHeight(), COLLISION_BUCKET_WIDTH);
	}
	
	public static void main(String[] args){
		BouncingBall bb= new BouncingBall(800,800,"Bouncing Ball");
		bb.start();
	}
	
	public void action(){
		//This method is called once every frame to update the state of the BouncingBall.
		
		//update both X and Y positions
		ballX+=ballXSpeed/getFPS();
		ballY+=ballYSpeed/getFPS();
		
		//handle collisions with the border
		if ( ballX<BORDER || ballX+ballSize>getHeight()-BORDER ){
			ballXSpeed*=-1;
		}
		if ( ballY<BORDER || ballY+ballSize>getWidth()-BORDER ){
			ballYSpeed*=-1;
		}
		
		
		/* In your screen saver implementation, you will need to handle collisions with
		   the border as well as collisions between all pairs of balls. You will also need to 
		   log any collisions using the CollisionLogger class. For example, the following code
		   updates the state of two ball objects to indicate that they have collided and records
		   this collision in the collisionLogger.
		   
		   if (ball1.intersect(ball2)) {
		   		ball1.collide(ball2);
		   		collisionLogger.collide(ball1,ball2);
		   		}
		   		
		   
		   Note that the processKeyEvent method implemented below will handle printing the collision log
		   when the "p" key is pressed.
		  */ 
	}
	
	public void draw(Graphics g){
		//This method is called once every frame to draw the Frame.
		
		//This is how you use the graphics object to draw
		g.setColor(Color.black);
		g.fillRect(0,0,getWidth(),getHeight());
		g.setColor(Color.white);
		g.drawRect(BORDER,BORDER,getWidth()-BORDER*2,getHeight()-BORDER*2);
		g.fillOval((int)ballX, (int)ballY, ballSize, ballSize); 
	}

	public void setRandDir(double speed){
		double dir=randdouble(0,Math.PI*2);
		ballXSpeed=Math.cos(dir)*speed;
		ballYSpeed=Math.sin(dir)*speed;
	}
	
	public int randInt(int min, int max){
		//a utility method to get a random int between min and max.
		return (int)(Math.random()*(max-min)+min);
	}
	
	public double randdouble(double min, double max){
		//a utility method to get a random double between min and max.
		return (Math.random()*(max-min)+min);
	}

    
     protected void processKeyEvent(KeyEvent e) {
        int keyCode = e.getKeyCode();
        
        /* This captures the user pressing the "p" key and prints out the current collisionLog to an image. 
        	You can use this directly in your implementation. Add other cases to the if/else statement to
        	handle other key events.
        */
        if (e.getID() == KeyEvent.KEY_PRESSED && keyCode == KeyEvent.VK_P) {
        	EasyBufferedImage image = EasyBufferedImage.createImage(collisionLogger.getNormalizedHeatMap());
            try {
                image.save("heatmap"+saveCounter+".png", EasyBufferedImage.PNG);
                saveCounter++;
            } catch (IOException exc) {
                exc.printStackTrace();
                System.exit(1);
            }
            
        }
     }
    
}