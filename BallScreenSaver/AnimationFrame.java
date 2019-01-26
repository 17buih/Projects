//AnimationFrame.java
//by Shanti Pothapragada, poth0018@umn.edu, Chad Myers, chadm@umn.edu
 
import java.awt.*;
import java.awt.event.*;

public abstract class AnimationFrame extends Frame implements Runnable {
	private int fps=50;
	
	public AnimationFrame(){
		super();
		setSize(800,800);
	}
	
	public AnimationFrame(int w, int h, String name){
		super(name);
		setSize(w,h);
	}
	
	
	/** 
	 *	Call this method when you're ready for the animation to start.
	 */
	public void start(){	
		super.setVisible(true);//shows the Frame.
		
		//these lines are to enable listening for keyboard and window closing events:
		//see the methods processKeyEvent and processWindowEvent below which are invoked
		//upon receiving one of these events
		enableEvents(AWTEvent.KEY_EVENT_MASK);
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
	
		thread=new Thread(this);
		thread.start();//thread will call run() when Thread says it's our turn.
		//run() is responsible for maintaining the framerate and calling 
		//action() and draw() each frame.
	}
	
	/** 
	 *	This method is called every frame to update the state of the animation frame.
	 */
	public abstract void action();
	
	/**
	 * This method is called every frame to draw the animation frame.
	 * 
	 * @param g	a reference to a graphics object, for drawing
	 */
	public abstract void draw(Graphics g); 
	
	
	/** 
	 *	Returns the current frame rate.
	 */
	public int getFPS(){ return fps;}
	
	
	/**
	 * This mutator method allows you to control the framerate.  
	 * Setting FPS=1 means that the image will be updated 1 time per second.
	 * Recomended values are 12-60.  Below 12 will look unnaturally choppy,
	 * most moniters cannot draw more than 50 times/second anyways.
	 * Actual rate may be lower if computer can't keep up.
	 * A lower rate may improve performance.	
	 */
	public void setFPS(int newFPS){
		if (newFPS!=0)//to prevent div by 0 errors.
			fps=newFPS;
	}


	/** 
	 *	This method is invoked whenever the animation frame receives a keyboard event.
	 */

	protected void processKeyEvent(KeyEvent e)
	{
	    int keyCode = e.getKeyCode();
	    //move if left key is pressed
	    if (keyCode == KeyEvent.VK_LEFT)
	    {
	       System.out.println("Left key pressed...");
	    }
	    else if (keyCode == KeyEvent.VK_RIGHT)
	    {
	        System.out.println("Right key pressed...");
	    }
	}
	

	//-----------------------------------------------------------------------------
	//everything on your side of the abstraction barrier is above.
	//in theory, you don't need to know about anything below here.
	//some of it is obscure and/or poorly commented.
	//It is based on code from "Animation in Java applets" in JavaWorld
	//at http://www.javaworld.com/javaworld/jw-03-1996/jw-03-animation.html
	//-----------------------------------------------------------------------------
	
	private Image buffer;
	private Thread thread;
	
	public void run(){
		//Run is in charge of maintaining the framerate.
		//run implements runnable, and needs to be public. 
		//( Thread(this) needs this to be a runnable.)
		//Remember the starting time
		long tm = System.currentTimeMillis();
		while(Thread.currentThread()==thread){//Main loop
			action();
			update(getGraphics());
			// Delay depending on how far we are behind:
			tm += 1000/fps;
			try {
			Thread.sleep(Math.max(0, tm - System.currentTimeMillis()));
			} catch (InterruptedException e) {
			break;
			}
		}// end main loop.
	}
	public void paint(Graphics g){
		//paint is called if part of the window is covered/uncovered/etc.
		//need to overwrite the Frame method to redirect it to update.
		update(g);
	}
	public void update(Graphics g){
		//( overwriting Frame.update() )
		//update hands a buffer image to draw(), then draws the buffer to screen.
		//ensure the buffer is ready and draw to it:
		if (buffer==null)
			buffer=createImage(getWidth(), getHeight());//createImage is a method of the Frame class.
		//call draw:
		draw(buffer.getGraphics());
		//draw buffer to screen:
		g.drawImage(buffer,0,0,null);
	}

	
	  public void processWindowEvent(WindowEvent e) {
	
		  if(e.getID() == WindowEvent.WINDOW_CLOSED) {
			System.exit(0);
		  } else if(e.getID() == WindowEvent.WINDOW_CLOSING) {
			System.exit(0);
		  }		  
	  }

	
}