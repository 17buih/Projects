import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;

/*
* @author Xiaoxuan Zhang 5375317
* @author Wiley Bui 5368469
*/

public class BallScreenSaver extends AnimationFrame{
  private int frameWidth, frameHeight, numBouncingBall, saveCounter = 0;
  private ArrayList<Ball> ballList = new ArrayList<>();
  private CollisionLogger collisionLogger;

  private static final int COLLISION_BUCKET_WIDTH = 20;
  public final int BORDER = 30;
  public final double MAX_SPEED = 300;
  public final double MIN_SPEED = 100;

  public BallScreenSaver() {
  } //ends default constructor

  public BallScreenSaver(int w, int h, String name, int numBouncingBall) {
    super(w,h,name);
    this.numBouncingBall = numBouncingBall;

    int redLuckyBall = randInt(0, numBouncingBall-1); //sets up a random initial red ball from the total amount of balls
    for (int i = 0; i < numBouncingBall; i++) {
      Color myColor = Color.GREEN;
      if (i == redLuckyBall) {
        myColor = Color.RED;  //the only special and inital RED ball
      }
      ballList.add(new Ball(200, 200, 10, myColor));
    } //ends putting balls into the ballList

    for (Ball eachBall : ballList) {
      double myXMin = BORDER + 2*eachBall.getRadius();
      double myXMax = getWidth() - BORDER - 2*eachBall.getRadius();
      double myYMin = BORDER + 2*eachBall.getRadius();
      double myYMax = getHeight() - 2*eachBall.getRadius() - BORDER;
      eachBall.setPos(randdouble(myXMin, myXMax), randdouble(myYMin, myYMax));
    }
    setRandDir();
    /* This instantiates the CollisionLogger, which you should also do in your class. */
    collisionLogger = new CollisionLogger(this.getWidth(), this.getHeight(), COLLISION_BUCKET_WIDTH);
    setFPS(20);
  }

  public static void main(String[] args) {
    BallScreenSaver bb= new BallScreenSaver(800,800,"BouncingBall",25);
    bb.start();
  }

  public void action() {
    //This method is called once every frame to update the state of the BouncingBall.
    //update both X and Y positions

    for (Ball eachBall : ballList) {
      eachBall.setPos(eachBall.getXPos() + eachBall.getSpeedX() / getFPS(), eachBall.getYPos() + eachBall.getSpeedY() / getFPS());
      if ((eachBall.getXPos() < BORDER) || (eachBall.getXPos() +2 * eachBall.getRadius() > getWidth() - BORDER)){
        eachBall.setSpeedX(eachBall.getSpeedX() * -1);
      }
      else if ((eachBall.getYPos() < BORDER) || (eachBall.getYPos() > getHeight() - BORDER - 2*eachBall.getRadius())){
        eachBall.setSpeedY(eachBall.getSpeedY() * -1);

      }
      //handle collision with two balls
      for (Ball otherBall: ballList) {
        if (eachBall != otherBall) {
          Color newColor = eachBall.getColor();
          eachBall.collide(otherBall);


          if (eachBall.intersect(otherBall) == true) {
            collisionLogger.collide(eachBall,otherBall);
            if (eachBall.getColor() == Color.RED && otherBall.getColor() == Color.GREEN) {
              otherBall.setColor(Color.RED);
            }
            else if (otherBall.getColor() == Color.RED && eachBall.getColor() == Color.GREEN) {
              eachBall.setColor(Color.RED);
            }
          } // end changing color if intersect
        }
      }
    }
  }

  public void draw(Graphics g){
    //This method is called once every frame to draw the Frame.
    //This is how you use the graphics object to draw
    g.setColor(Color.black);
    g.fillRect(0,0,getWidth(),getHeight());
    g.setColor(Color.white);
    g.drawRect(BORDER,BORDER,getWidth()-BORDER*2,getHeight()-BORDER*2);

    for (Ball eachBall : ballList) {
      g.setColor(eachBall.getColor()); //not every ball will be green at first, so getColor is best used here
      g.fillOval((int)eachBall.getXPos(), (int)eachBall.getYPos(), (int)(2 * eachBall.getRadius()), (int)(2 * eachBall.getRadius()));
    }
  }


  //sets random direction for each ball (when it starts)
  public void setRandDir() {
    for (Ball eachBall : ballList) {
      double myOwnSpeed = randdouble(MIN_SPEED, MAX_SPEED);
      double dir = randdouble(0, Math.PI * 2);
      eachBall.setSpeedX(Math.cos(dir) * myOwnSpeed);
      eachBall.setSpeedY(Math.sin(dir) * myOwnSpeed);
    }
  }

  public int randInt(int min, int max) {
    //a utility method to get a random int between min and max.
    return (int) (Math.random() * (max - min) + min);
  }

  public double randdouble(double min, double max) {
    //a utility method to get a random double between min and max.
    return (Math.random() * (max - min) + min);
  }

  protected void processKeyEvent(KeyEvent e) {
    int keyCode = e.getKeyCode();
    if (e.getID() == KeyEvent.KEY_PRESSED && (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_RIGHT)) {
      double tenPercentCurrentValue = (keyCode == KeyEvent.VK_LEFT) ? -0.1 : 0.1; //If user presses left key, the speed will decrease by 10%. Vice versa for right key

      for (Ball myBall : ballList) {
        double speedX = myBall.getSpeedX() * tenPercentCurrentValue;
        double speedY = myBall.getSpeedY() * tenPercentCurrentValue;
        myBall.setSpeedX(myBall.getSpeedX() + speedX);
        myBall.setSpeedY(myBall.getSpeedY() + speedY);
      }
    }

    /* This captures the user pressing the "p" key and prints out the current collisionLog to an image.
    You can use this directly in your implementation. Add other cases to the if/else statement to
    handle other key events.
    */
    if (e.getID() == KeyEvent.KEY_PRESSED && keyCode == KeyEvent.VK_P) {
      EasyBufferedImage image = EasyBufferedImage.createImage(collisionLogger.getNormalizedHeatMap());
      try {
        image.save("heatmap" + saveCounter + ".png", EasyBufferedImage.PNG);
        saveCounter++;
      } catch (IOException exc) {
        exc.printStackTrace();
        System.exit(1);
      }
    }
  }
}
