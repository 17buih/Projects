import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.util.Random;
import static org.junit.Assert.*;


public class MatrixEntryTest {

    @Rule
    public Timeout timeout = Timeout.seconds(5);
    private MatrixEntry matrix;
    private Random rand = new Random();
    private final int ROW = 20;
    private final int COL = 22;
    private final int DATA = 27;

    @Before
    public void setUp() {
        matrix = new MatrixEntry(ROW, COL, DATA);
    }

    @Test
    public void TestMatrixEntry() {
        assertEquals("Row: Either assigned to a local variable or redefined inherited member variables", ROW, matrix.getRow());
        assertEquals("Column: Either assigned to a local variable or redefined inherited member variables", COL, matrix.getColumn());
        assertEquals("Data: Either assigned to a local variable or redefined inherited member variables", DATA, matrix.getData());
    }

    @Test
    public void Testsetcheck() {

        int expectedCol = rand.nextInt(COL);
        int expectedRow = rand.nextInt(ROW);
        int expectedData = rand.nextInt(DATA);
        matrix.setColumn(expectedCol);
        matrix.setRow(expectedRow);
        matrix.setData(expectedData);

        assertEquals("Column did not change correctly", expectedCol, matrix.getColumn());
        assertEquals("Row did not change correctly", expectedRow, matrix.getRow());
        assertEquals("Data did not change correctly", expectedData, matrix.getData());
    }

    @Test
    public void TestNextCol() {
        MatrixEntry colTest = new MatrixEntry(rand.nextInt(ROW), rand.nextInt(COL), rand.nextInt(DATA));
        matrix.setNextCol(colTest);
        assertEquals("Cannot get NextRow or access wrong MatrixEntry of  next column ",colTest,matrix.getNextCol());
    }

    @Test
    public void TestNextRow() {
        MatrixEntry rowTest = new MatrixEntry(rand.nextInt(ROW), rand.nextInt(COL), rand.nextInt(DATA));
        matrix.setNextRow(rowTest);
        assertEquals("Cannot get NextRow or access wrong MatrixEntry of  next row ", rowTest, matrix.getNextRow());
    }
}
