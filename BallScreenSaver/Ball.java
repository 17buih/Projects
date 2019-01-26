import java.awt.*;
public class Ball extends Circle {
  private double speedX, speedY;
  public Ball(){
  }

  public Ball(double x, double y, double radius, Color color) {
    super(x, y,radius,color);
  }

  // @param speedX sets the speed for the horizonal x axis
  public void setSpeedX(double speedX) {
    this.speedX = speedX;
  }

  public void setSpeedY(double speedY) {
    this.speedY = speedY;
  }

  public double getSpeedX() {
    return speedX;
  }

  public double getSpeedY() {
    return speedY;
  }

  /*
  * This method updates the position of the current ball based on its time passed
  */
  public void updatePosition(double time) {
    setPos(x + speedX * time, y + speedY * time);
  }


  /*
  * This method checks if 2 balls are intersecting each other by using the distance formula
  * @return true if 2 balls touch each other
  */
  public boolean intersect(Ball otherBall) {
    double deltaXSquare = Math.pow((otherBall.getXPos() - x), 2);
    double deltaYSquare = Math.pow((otherBall.getYPos() - y), 2);
    double distance = Math.sqrt(deltaXSquare + deltaYSquare);
    if (distance <= (2 * getRadius())) {
      return true;
    }
    else {
      return false;
    }
  }

  /*
  * This method swaps and sets the ball's velocity with each other as they collide together
  */
  public void collide(Ball otherBall) {
    if (intersect(otherBall) == true) {
      double deltaXSquare = Math.pow((otherBall.getXPos() - getXPos()), 2);
      double deltaYSquare = Math.pow((otherBall.getYPos() - getYPos()), 2);
      double distance     = Math.sqrt(deltaXSquare + deltaYSquare);
      double deltaX       = (getXPos() - otherBall.getXPos()) / distance;
      double deltaY       = (getYPos() - otherBall.getYPos()) / distance;

      double newColVelocity  = (otherBall.getSpeedX() * deltaX) + (otherBall.getSpeedY() * deltaY);
      double newNormVelocity = -1 * getSpeedX() * deltaY + getSpeedY() * deltaX;

      double newColVelocityOtherBall  = (getSpeedX() * deltaX) + (getSpeedY() * deltaY);
      double newNormVelocityOtherBall = -1 * otherBall.getSpeedX() * deltaY + otherBall.getSpeedY() * deltaX;

      double finalXVelocity = newColVelocity * deltaX - newNormVelocity * deltaY;
      double finalYVelocity = newColVelocity * deltaY + newNormVelocity * deltaX;
      double finalXVelocityOtherBall = newColVelocityOtherBall * deltaX - newNormVelocityOtherBall * deltaY;
      double finalYVelocityOtherBall = newColVelocityOtherBall * deltaY + newNormVelocityOtherBall * deltaX;


      setSpeedX(finalXVelocity);
      setSpeedY(finalYVelocity);
      otherBall.setSpeedX(finalXVelocityOtherBall);
      otherBall.setSpeedY(finalYVelocityOtherBall);
    }
  }
}
