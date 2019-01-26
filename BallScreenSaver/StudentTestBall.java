import org.junit.*;
import org.junit.rules.Timeout;
import org.junit.runners.MethodSorters;

import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Random;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StudentTestBall {

    private final double TOLERANCE = 0.0000001;

    @Rule
    public Timeout timeout = Timeout.seconds(5);

    private Ball ball;

    private final double X = 5.1;
    private final double Y = 6.1;
    private final double RADIUS = 25.1;
    private final Color COLOR = new Color(125, 97, 2);

    @Before
    public void setUp() {
        ball = new Ball(X, Y, RADIUS, COLOR);
    }

    @Test
    public void TestExtendCircle() {
        assertTrue("Ball does not extend from Circle", Circle.class.isAssignableFrom(Ball.class));
    }

    @Test
    public void TestConstructor() {
        assertEquals("xPos: Either assigned to a local variable or redefined inherited member variables", X, ball.getXPos(), TOLERANCE);
        assertEquals("yPos: Either assigned to a local variable or redefined inherited member variables", Y, ball.getYPos(), TOLERANCE);
        assertEquals("radius: Either assigned to a local variable or redefined inherited member variables", RADIUS, ball.getRadius(), TOLERANCE);
        assertEquals("color: Either assigned to a local variable or redefined inherited member variables", COLOR, ball.getColor());
    }

    @Test
    public void TestPrivateAccessModifiers() {
        // wooo reflection!
        Class<Ball> c = Ball.class;
        Field[] memberVariables = c.getDeclaredFields();
        for (Field memberVariable : memberVariables) {
            assertTrue("Has non-private member variables", Modifier.isPrivate(memberVariable.getModifiers()));
        }
    }

    @Test
    public void TestStatic() {
        ball.setSpeedX(1);
        ball.setSpeedY(2);

        Ball other = new Ball(0, 0, 0, Color.ORANGE);
        other.setSpeedX(0);
        other.setSpeedY(0);

        assertNotEquals("Incorrect usage of static", 0, ball.getXPos());
        assertNotEquals("Incorrect usage of static", 0, ball.getYPos());
        assertNotEquals("Incorrect usage of static", 0, ball.getRadius());
        assertNotEquals("Incorrect usage of static", 0, ball.getSpeedX());
        assertNotEquals("Incorrect usage of static", 0, ball.getSpeedY());
        assertNotEquals("Incorrect usage of static", Color.ORANGE, ball.getColor());
    }

    @Test
    public void TestSetAndGetSpeedX() {
        double expected = 100.5;
        ball.setSpeedX(expected);
        double actual = ball.getSpeedX();
        assertEquals(expected, actual, TOLERANCE);
    }

    @Test
    public void TestSetAndGetSpeedY() {
        double expected = 100.5;
        ball.setSpeedY(expected);
        double actual = ball.getSpeedY();
        assertEquals(expected, actual, TOLERANCE);
    }

    @Test
    public void TestIntersectTrueX() {
        Ball other = new Ball(X + 10, Y, RADIUS * 10, COLOR);

        // Better be symmetric
        assertTrue("Horizontal intersection does not work", ball.intersect(other));
        assertTrue("Intersection code is not symmetric", other.intersect(ball));
    }

    @Test
    public void TestIntersectTrueY() {
        Ball other = new Ball(X, Y + 10, RADIUS * 10, COLOR);

        // Better be symmetric
        assertTrue("Vertical intersection does not work", ball.intersect(other));
        assertTrue("Intersection code is not symmetric", other.intersect(ball));
    }

    @Test
    public void TestIntersectTrueXAndY() {
        Ball other = new Ball(X + 10, Y + 10, RADIUS * 10, COLOR);

        // Better be symmetric
        assertTrue("2 dimensional intersection does not work", ball.intersect(other));
        assertTrue("Intersection code is not symmetric", other.intersect(ball));
    }

    @Test
    public void TestIntersectFalse() {
        Ball other = new Ball(X * 100, Y * 100, RADIUS, COLOR);

        // Better be symmetric
        assertFalse(ball.intersect(other));
        assertFalse("Intersection code is not symmetric", other.intersect(ball));
    }

    @Test
    public void TestIntersectFalseNoChange() {
        int expectedSpeedX = 10;
        int expectedSpeedY = 20;
        Color expectedColor = new Color(120, 140, 200);

        ball.setSpeedX(10);
        ball.setSpeedY(20);
        ball.setColor(expectedColor);

        Ball other = new Ball(X * 100, Y * 100, RADIUS, COLOR);

        ball.intersect(other);

        assertEquals("Member variables are not supposed to be modified w/ intersect call", X, ball.getXPos(), TOLERANCE);
        assertEquals("Member variables are not supposed to be modified w/ intersect call", Y, ball.getYPos(), TOLERANCE);
        assertEquals("Member variables are not supposed to be modified w/ intersect call", RADIUS, ball.getRadius(), TOLERANCE);
        assertEquals("Member variables are not supposed to be modified w/ intersect call", expectedSpeedX, ball.getSpeedX(), TOLERANCE);
        assertEquals("Member variables are not supposed to be modified w/ intersect call", expectedSpeedY, ball.getSpeedY(), TOLERANCE);
        assertEquals("Member variables are not supposed to be modified w/ intersect call", expectedColor, ball.getColor());
    }

    @Test
    public void TestIntersectTrueNoChange() {
        int expectedSpeedX = 10;
        int expectedSpeedY = 20;
        Color expectedColor = new Color(120, 140, 200);

        ball.setSpeedX(10);
        ball.setSpeedY(20);
        ball.setColor(expectedColor);

        Ball other = new Ball(X + 10, Y + 10, RADIUS * 10, COLOR);

        ball.intersect(other);

        assertEquals("Member variables are not supposed to be modified w/ intersect call", X, ball.getXPos(), TOLERANCE);
        assertEquals("Member variables are not supposed to be modified w/ intersect call", Y, ball.getYPos(), TOLERANCE);
        assertEquals("Member variables are not supposed to be modified w/ intersect call", RADIUS, ball.getRadius(), TOLERANCE);
        assertEquals("Member variables are not supposed to be modified w/ intersect call", expectedSpeedX, ball.getSpeedX(), TOLERANCE);
        assertEquals("Member variables are not supposed to be modified w/ intersect call", expectedSpeedY, ball.getSpeedY(), TOLERANCE);
        assertEquals("Member variables are not supposed to be modified w/ intersect call", expectedColor, ball.getColor());
    }

    @Test
    public void TestCollideX() {
        Random rand = new Random();
        int offset = rand.nextInt(1000);

        ball = new Ball(50 + offset, 50 + offset, 10 + offset, Color.BLUE);
        ball.setSpeedX(10 + offset);
        ball.setSpeedY(offset);

        Ball other = new Ball(100 + offset, 50 + offset, 41.0 + offset, Color.BLUE);
        other.setSpeedX(-10 + offset);
        other.setSpeedY(offset);
        ball.collide(other);

        assertEquals("Horizontal collision does not properly change X-speed", -10 + offset, ball.getSpeedX(), TOLERANCE);
        assertEquals("Horizontal collision does not properly change Y-speed", offset, ball.getSpeedY(), TOLERANCE);
    }

    @Test
    public void TestCollideY() {
        Random rand = new Random();
        int offset = rand.nextInt(100);

        ball = new Ball(50 + offset, 50 + offset, 10 + offset, Color.BLUE);
        ball.setSpeedX(offset);
        ball.setSpeedY(10 + offset);

        Ball other = new Ball(50 + offset, 100 + offset, 41.0 + offset, Color.BLUE);
        other.setSpeedX(offset);
        other.setSpeedY(-10 + offset);
        ball.collide(other);

        assertEquals("Vertical collision does not properly change X-speed", offset, ball.getSpeedX(), TOLERANCE);
        assertEquals("Vertical collision does not properly change Y-speed", -10 + offset, ball.getSpeedY(), TOLERANCE);
    }

    @Test
    public void TestCollideXAndYAndColor() {
        Random rand = new Random();
        int offset = rand.nextInt(100);

        ball = new Ball(397 + offset, 536 + offset, 10 + offset, Color.RED);
        ball.setSpeedX(99.9 + offset);
        ball.setSpeedY(3.865 + offset);

        Ball other = new Ball(401.567 + offset, 524.256 + offset, 10.0 + offset, Color.RED);
        other.setSpeedX(96.564 + offset);
        other.setSpeedY(25.9876 + offset);
        ball.collide(other);

        assertEquals("Two dimensional collision does not properly change X-speed", 92.3 + offset, ball.getSpeedX(), 1);
        assertEquals("Two dimensional collision does not properly change Y-speed", 24.4 + offset, ball.getSpeedY(), 1);
        assertEquals("Ball did not change color", Color.RED, other.getColor());
    }

    @Test
    public void TestCollideNoIntersection() {
        ball = new Ball(50, 50, 10, Color.BLUE);
        ball.setSpeedX(10);
        ball.setSpeedY(0);

        Ball other = new Ball(100, 50, 1, Color.BLUE);
        other.setSpeedX(-10);
        other.setSpeedY(0);
        ball.collide(other);

        assertEquals("X-speed of collider changed even though there was no intersection", 10, ball.getSpeedX(), TOLERANCE);
        assertEquals("X-speed of colidee changed even though there was no intersection", -10, other.getSpeedX(), TOLERANCE);
        assertEquals("Y-speed collider changed even though there was no intersection", 0, ball.getSpeedY(), TOLERANCE);
        assertEquals("Y-speed collidee changed even though there was no intersection", 0, other.getSpeedY(), TOLERANCE);
    }

    @Test
    public void TestCollideChangeCollide() {
        Random rand = new Random();
        int offset = rand.nextInt(1000);

        ball = new Ball(397 + offset, 536 + offset, 10 + offset, Color.RED);
        ball.setSpeedX(99.9 + offset);
        ball.setSpeedY(3.865 + offset);

        Ball other = new Ball(401.567 + offset, 524.256 + offset, 10.0 + offset, Color.GREEN);
        other.setSpeedX(96.564 + offset);
        other.setSpeedY(25.9876 + offset);
        ball.collide(other);

        assertEquals("Did not change the X-speed of the other ball", 104.2 + offset, other.getSpeedX(), 1);
        assertEquals("Did not change the Y-speed of the other ball", 5.45 + offset, other.getSpeedY(), 1);
    }

    @Test
    public void TestUpdatePosition() {
        double timeunit = 2.5;
        double speedX = 5.0;
        double speedY = 10.0;

        ball.setSpeedX(speedX);
        ball.setSpeedY(speedY);
        ball.updatePosition(timeunit);
        assertEquals("X-Position of the ball did not change correctly", timeunit * speedX + X, ball.getXPos(), TOLERANCE);
        assertEquals("Y-Position of the ball did not change correctly", timeunit * speedY + Y, ball.getYPos(), TOLERANCE);
    }
}
