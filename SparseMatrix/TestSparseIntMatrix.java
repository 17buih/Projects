import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.util.Random;

import static org.junit.Assert.*;

/**
 * These tests provide *some* coverage of the requirements of the SparseIntMatrix class.
 * Note that testing all test cases in this file does not indicate a fully functional
 * SparseIntMatrix class.
 */
public class TestSparseIntMatrix {

    @Rule
    public Timeout timeout = Timeout.seconds(5);

    private SparseIntMatrix matrix;
    private Random rand = new Random();
    private final int ROW = 19;
    private final int COL = 33;

    @Before
    public void setUp() {
        matrix = new SparseIntMatrix(ROW, COL);
    }

    @Test
    public void TestGetNumRowsAndCols() {
        assertEquals(ROW, matrix.getNumRows());
        assertEquals(COL, matrix.getNumCols());
    }

    @Test
    public void TestGetElementEmpty() {
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                assertEquals("Returned a non-zero value on an empty matrix", Integer.valueOf(0), Integer.valueOf(matrix.getElement(i, j)));
            }
        }
    }

    @Test
    public void TestSetOutOfRange() {
        assertFalse("Did not check row", matrix.setElement(ROW + 10, 0, 1));
        assertFalse("Did not exclude row", matrix.setElement(ROW, 0, 1));
        assertFalse("Did not check column", matrix.setElement(0, COL + 10, 1));
        assertFalse("Did not exclude column", matrix.setElement(0, COL, 1));
        assertFalse("Did not check negatives", matrix.setElement(-1, -1, 1));
    }

    @Test
    public void TestSetInRange() {
        assertTrue(matrix.setElement(rand.nextInt(ROW), rand.nextInt(COL), 1));
    }

    @Test
    public void TestPlusMatchingDimensions() {
        SparseIntMatrix other = new SparseIntMatrix(ROW, COL);
        assertTrue(matrix.plus(other));
    }

    @Test
    public void TestMinusMatchingDimensions() {
        SparseIntMatrix other = new SparseIntMatrix(ROW, COL);
        assertTrue(matrix.minus(other));
    }

    @Test
    public void TestPlusMismatchingDimensions() {
        SparseIntMatrix other = new SparseIntMatrix(COL, ROW);
        assertFalse(matrix.plus(other));
    }

    @Test
    public void TestMinusMismatchingDimensions() {
        SparseIntMatrix other = new SparseIntMatrix(COL, ROW);
        assertFalse(matrix.minus(other));
    }
}