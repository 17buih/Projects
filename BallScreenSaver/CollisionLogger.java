
import java.util.*;
public class CollisionLogger {
  private int screenWidth, screenHeight, bucketWidth;
  private int[][] loggerArray;

  public CollisionLogger(){
  } //end default constructor

  public CollisionLogger(int screenWidth, int screenHeight, int bucketWidth) {
    this.screenWidth = screenWidth;
    this.screenHeight = screenHeight;
    this.bucketWidth = bucketWidth;
    loggerArray = new int[screenHeight / bucketWidth][screenWidth / bucketWidth];
  }

  /**
  * This method records the collision event between two balls. Specifically, in increments the bucket corresponding to
  * their x and y positions to reflect that a collision occurred in that bucket.
  */
  public void collide(Ball one, Ball two) {
    int matrixY = (int)(((one.getXPos() + two.getXPos()) / 2) / bucketWidth);
    int matrixX = (int)(((one.getYPos() + two.getYPos()) / 2) / bucketWidth);
    try {
      loggerArray[matrixX][matrixY] += 1;
    }
    catch (ArrayIndexOutOfBoundsException exception) {
    } //prevents ERRORs from occuring if the index is out of loggerArray
  }


  /**
  * Returns the heat map scaled such that the bucket with the largest number of collisions has value 255,
  * and buckets without any collisions have value 0.
  */
  public int[][] getNormalizedHeatMap() {
    int[][] heatMap = new int[loggerArray.length][loggerArray[0].length];
    int biggestNumber = 0;

    //get a single largest number from the entire screen array - loggerArray
    for (int i= 0; i < loggerArray.length; i++) {
      for (int e = 0; e < loggerArray[i].length; e++) {
        if (loggerArray[i][e] > biggestNumber) {
          biggestNumber = loggerArray[i][e];
        }
      }
    }

    //rescaling the numbers with the max of 255 and min of 0
    if (biggestNumber != 0) {
      for (int i = 0; i < heatMap.length; i++) {
        for (int e = 0; e < heatMap[i].length; e++) {
          heatMap[i][e] = (255 * (loggerArray[i][e]) / biggestNumber);
        }
      }
    }

    return heatMap;
  }
}
