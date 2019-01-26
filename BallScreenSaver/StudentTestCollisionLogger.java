import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Random;

import static org.junit.Assert.*;

public class StudentTestCollisionLogger {


    @Rule
    public Timeout timeout = Timeout.seconds(5);

    private CollisionLogger logger;
    private final int WIDTH = 100;
    private final int HEIGHT = 150;
    private final int BUCKET_WIDTH = 10;

    @Before
    public void setUp() {
        logger = new CollisionLogger(WIDTH, HEIGHT, BUCKET_WIDTH);
    }

    @Test
    public void TestPrivateAccessModifiers() {
        // wooo reflection!
        Class<CollisionLogger> c = CollisionLogger.class;
        Field[] memberVariables = c.getDeclaredFields();
        for (Field memberVariable : memberVariables) {
            assertTrue("Has non-private member variables", Modifier.isPrivate(memberVariable.getModifiers()));
        }
    }

    @Test
    public void TestEmptyMap() {
        try {
            int[][] actual = logger.getNormalizedHeatMap();
            for (int i = 0; i < actual.length; i++) {
                for (int j = 0; j < actual[i].length; j++) {
                    assertEquals(0, actual[i][j]);
                }
            }
        } catch (ArithmeticException e) {
            // For some reason, if the numerator is a double, then division by 0 gives you NaN/Infinity and not an
            // exception... And then casting NaN/Infinity to an int gives you 0? Java is weird... or broken...
            // yeah, definitely broken.
            fail("Divided an integer 0 by an integer 0");
        }
    }

    @Test
    public void TestDimensions() {
        int[][] actual = logger.getNormalizedHeatMap();
        try {
            assertEquals("Logger did not discretize the screen saver", WIDTH / BUCKET_WIDTH, actual[0].length);
            assertEquals("Logger did not discretize the screen saver", HEIGHT / BUCKET_WIDTH, actual.length);
        } catch (AssertionError e) {
            // Handle flip flops
            assertEquals("Logger did not discretize the screen saver", HEIGHT / BUCKET_WIDTH, actual[0].length);
            assertEquals("Logger did not discretize the screen saver", WIDTH / BUCKET_WIDTH, actual.length);
        }
    }

    @Test
    public void TestDimensionsRound() {
        int bucket_width = 7;
        logger = new CollisionLogger(WIDTH, HEIGHT, bucket_width);
        int[][] actual = logger.getNormalizedHeatMap();
        try {
            assertEquals("Logger did not discretize the screen saver (rounding)", WIDTH / bucket_width, actual[0].length);
            assertEquals("Logger did not discretize the screen saver (rounding)", HEIGHT / bucket_width, actual.length);
        } catch (AssertionError e) {
            // Handle flip flops
            assertEquals("Logger did not discretize the screen saver (rounding)", HEIGHT / bucket_width, actual[0].length);
            assertEquals("Logger did not discretize the screen saver (rounding)", WIDTH / bucket_width, actual.length);
        }
    }


    @Test
    public void TestMaxValue() {
        Random rand = new Random();
        int numCollisions = 1000000;

        // Have a bunch of random collisions
        while (numCollisions > 0) {
            Ball collider = new Ball(rand.nextInt(WIDTH), rand.nextInt(HEIGHT), rand.nextInt(WIDTH), Color.BLUE);
            Ball collidee = new Ball(rand.nextInt(WIDTH), rand.nextInt(HEIGHT), rand.nextInt(WIDTH), Color.BLUE);
            logger.collide(collider, collidee);
            numCollisions--;
        }

        // Make sure that none of the values are > 255
        int[][] actual = logger.getNormalizedHeatMap();
        for (int i = 0; i < actual.length; i++) {
            for (int j = 0; j < actual[i].length; j++) {
                assertTrue("Normalized heat map has a value > 255", actual[i][j] <= 255);
            }
        }
    }

    @Test
    public void TestCollisionEverywhereEqual() {
        int tolerance = 1;

        // Put one collision in each spot
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                Ball collider = new Ball(j, i, 2, Color.BLUE);
                Ball collidee = new Ball(j + 1, i + 1, 2, Color.BLUE);
                logger.collide(collider, collidee);
            }
        }

        int[][] actual = logger.getNormalizedHeatMap();
        // Should all normalize to 255
        for (int i = 0; i < actual.length; i++) {
            for (int j = 0; j < actual[i].length; j++) {
                assertEquals("Logger did not normalize correctly when collisions occurred everywhere, equally", 255, actual[i][j], tolerance);
            }
        }
    }

    @Test
    public void TestCollisionEverywhereUnequal() {

        int tolerance = 5;
        // Coordinates where x and y are odd will have three collisions
        // Coordinates where x xor y is odd will have two collisions
        // Coordinates where x and y are even will have one collision
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                Ball collider = new Ball(j, i, 2, Color.BLUE);
                if ((i / BUCKET_WIDTH) % 2 == 1 && (j / BUCKET_WIDTH) % 2 == 1) {
                    logger.collide(collider, collider);
                }
                if ((i / BUCKET_WIDTH) % 2 == 1 || (j / BUCKET_WIDTH) % 2 == 1) {
                    logger.collide(collider, collider);
                }
                logger.collide(collider, collider);
            }
        }

        int[][] actual = logger.getNormalizedHeatMap();
        for (int i = 0; i < actual.length; i++) {
            for (int j = 0; j < actual[i].length; j++) {
                int expected;
                if (i % 2 == 1 && j % 2 == 1) {
                    expected = 255;
                } else if (i % 2 == 1 || j % 2 == 1) {
                    expected = 170;
                } else {
                    expected = 85;
                }
                assertEquals("Logger did not normalize correctly when collisions occurred everywhere, unequally", expected, actual[i][j], tolerance);
            }
        }
    }

    @Test
    public void TestCollisionHalfMap() {
        int tolerance = 1;

        // Collisions only occur in the top half of the screen
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                Ball collider = new Ball(j, i, 2, Color.BLUE);
                if (i / BUCKET_WIDTH < (HEIGHT / (2 * BUCKET_WIDTH))) {
                    logger.collide(collider, collider);
                }
            }
        }

        int[][] actual = logger.getNormalizedHeatMap();
        for (int i = 0; i < actual.length; i++) {
            for (int j = 0; j < actual[i].length; j++) {
                int expected;
                if (i < actual.length / 2) {
                    expected = 255;
                } else {
                    expected = 0;
                }
                assertEquals("Logger did not normalize correctly when collisions only occurred in a part of the screen saver", expected, actual[i][j], tolerance);
            }
        }
    }

    @Test
    public void TestCollisionOne() {
        // Put one collision in the upper left hand corner
        logger.collide(new Ball(0, 0, 1, Color.BLUE), new Ball(1, 1, 1, Color.BLUE));
        int[][] actual = logger.getNormalizedHeatMap();

        assertEquals("Logger did not normalize correctly when there was only one collision", 255, actual[0][0]);
        for (int i = 0; i < actual.length; i++) {
            for (int j = 0; j < actual[i].length; j++) {
                if (i > 0 || j > 0) {
                    // Everywhere else should be 0
                    assertEquals("Logger did not normalize correctly when there was only one collision, but not at this spot", 0, actual[i][j]);
                }
            }
        }
    }

    @Test
    public void TestCollisionMultiple() {
        // Put three collisions in the upper left hand corner
        logger.collide(new Ball(0, 0, 1, Color.BLUE), new Ball(1, 1, 1, Color.BLUE));
        logger.collide(new Ball(0, 0, 1, Color.BLUE), new Ball(1, 1, 1, Color.BLUE));
        logger.collide(new Ball(0, 0, 1, Color.BLUE), new Ball(1, 1, 1, Color.BLUE));
        int[][] actual = logger.getNormalizedHeatMap();

        assertEquals("Logger did not normalize correctly when there were multiple collisions in the same spot", 255, actual[0][0]);
        for (int i = 0; i < actual.length; i++) {
            for (int j = 0; j < actual[i].length; j++) {
                if (i > 0 || j > 0) {
                    // Everywhere else should be 0
                    assertEquals("Logger did not normalize correctly when there were multiple collisions in the same spot", 0, actual[i][j]);
                }
            }
        }
    }
}
