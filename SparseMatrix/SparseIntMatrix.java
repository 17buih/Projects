import java.util.Scanner;
import java.util.Arrays;
import java.io.File;

/**
 * Author: Xiaoxuan Zhang 5375317
 * Author: Wiley Bui 5368469
*/

 public class SparseIntMatrix {
    public MatrixEntry[] colHeader, rowHeader;
    private int numRows, numCols;

    public SparseIntMatrix(int numRows, int numCols) {
        this.numRows = numRows;
        this.numCols = numCols;

        colHeader = new MatrixEntry[numCols];
        rowHeader = new MatrixEntry[numRows];
    }

    public SparseIntMatrix(int numRows, int numCols, String inputFile) {
        this.numRows = numRows;
        this.numCols = numCols;
        File myFileInput = new File(inputFile);

        Scanner myScanner = null;
        String line;
        String[] splitter;
        int row, col, data;

        colHeader = new MatrixEntry[numCols];
        rowHeader = new MatrixEntry[numRows];

        try {
            myScanner = new Scanner(myFileInput);
        } catch (Exception e) {
            System.out.println("There was an error opening the file");
        }

        while (myScanner.hasNextLine()) {
            line = myScanner.nextLine();
            splitter = line.split(","); //splitting the string into 3 parts based on commas
            row = Integer.parseInt(splitter[0]);
            col = Integer.parseInt(splitter[1]);
            data = Integer.parseInt(splitter[2]);

            if (data != 0) {
                setElement(row, col, data);
                //passing current Row and Col into Linked List
            }
        }
    } //end SparseIntMatrix constructor

    public static void main(String args[]) {
        SparseIntMatrix lovelyGopher = new SparseIntMatrix(800,800,"matrix1_data.txt");
        MatrixViewer.show(lovelyGopher);

        SparseIntMatrix matrix2 = new SparseIntMatrix(800, 800, "matrix2_data.txt");
        SparseIntMatrix matrix2Noise = new SparseIntMatrix(800, 800, "matrix2_noise.txt");
        matrix2.minus(matrix2Noise);
        MatrixViewer.show(matrix2);
    }

    /**
     * This method gets the data back from a request based on its row and column
     *
     * @param row integer representing the specific row
     * @param col integer representing the specific column
     * @return a specific integer of data from Linked list
     */
    public int getElement(int row, int col) {
        if (row < 0 || col < 0 || row >= numRows || col >= numCols) {
            //check if out of bound
            return 0;
        } else {
            MatrixEntry current = rowHeader[row];
            if (current == null) {
                return 0;
            } else {
                while (current != null && rowHeader[row].getColumn() <= col) {
                    //stops when end of the linked list or already passed the specific column
                    if (current.getColumn() == col) {
                        return current.getData();
                    } else {
                        current = current.getNextCol();
                        //next column if couldn't find the specific column
                    }
                }
                return 0;
            }
        }
    }

    /**
     * This method changes the given row and column's data.
     *
     * @param row integer corresponding to row
     * @param col integer corresponding to column
     * @param data integer corresponding to data of its row and column
     * @return true if data can be placed in specific row and column
     */
    public boolean setElement(int row, int col, int data) {
        if (row < 0 || col < 0 || row >= numRows || col >= numCols || data == 0) {
            return false;
        } else if (getElement(row, col) != 0) {
            MatrixEntry current = rowHeader[row];
            while (current != null && current.getColumn() <= col) {
                //get data by looping until rowHeader matches the column
                if (current.getColumn() == col) {
                    current.setData(data);
                    return true;
                } else {
                    current = current.getNextCol();
                }
            }
            return false;
        } else {
            if (rowHeader[row] == null) {
                //nothing in this row
                rowHeader[row] = new MatrixEntry(row, col, data);
            } else if (rowHeader[row].getColumn() > col) {
                //before the rowHeader
                MatrixEntry tem = rowHeader[row];
                rowHeader[row] = new MatrixEntry(row, col, data);
                rowHeader[row].setNextCol(tem);
            } else {
                MatrixEntry lastColElement = rowHeader[row];
                while (lastColElement.getNextCol() != null && lastColElement.getColumn() < col) {
                    //stops when end of the linked list or already passed the specific column
                    lastColElement = lastColElement.getNextCol();
                }

                if (lastColElement.getNextCol() == null) {
                    //reaches at the end of linked list
                    lastColElement.setNextCol(new MatrixEntry(row, col, data));
                } else {
                    //insert in the middle
                    MatrixEntry newMatrix   = new MatrixEntry(row, col, data);
                    MatrixEntry beforeOne   = lastColElement;
                    MatrixEntry afterOne    = lastColElement.getNextCol();

                    beforeOne.setNextCol(newMatrix);
                    newMatrix.setNextCol(afterOne);
                }
            } //end if

            if (colHeader[col] == null) {
                //nothing in this column
                colHeader[col] = new MatrixEntry(row, col, data);
            } else if (colHeader[col].getRow() > row) {
                //before the header
                MatrixEntry tem = colHeader[col];
                colHeader[col] = new MatrixEntry(row, col, data);
                colHeader[col].setNextRow(tem);
            } else {
                MatrixEntry lastRowElement = colHeader[col];
                while (lastRowElement.getNextRow() != null && lastRowElement.getRow() < row) {
                    //stops when end of the linked list or already passed the specific column
                    lastRowElement = lastRowElement.getNextRow();
                }
                if (lastRowElement.getNextRow() == null) {
                    //at the end
                    lastRowElement.setNextRow(new MatrixEntry(row, col, data));
                } else {
                    //insert in the middle
                    MatrixEntry newMatrix = new MatrixEntry(row, col, data);
                    MatrixEntry beforeOne = lastRowElement;
                    MatrixEntry afterOne = lastRowElement.getNextRow();
                    beforeOne.setNextRow(newMatrix);
                    newMatrix.setNextRow(afterOne);
                }
            }
            return true;
        }
    } //end removeElement


    /**
     * This method removes an element based on its row and column.
     * Will be removed when new data matches old data or iff data == 0 
     *
     * @param row integer representing from its specific row
     * @param col integer representing from its specific column
     * @param data integer representing data given
     * @return true if element is already removed
     */
    public boolean removeElement(int row, int col, int data) {
        if (row < 0 || col < 0 || row >= numRows || col >= numCols || getElement(row, col) == 0) {
            //out of bound
            return false;
        } else if (data == 0 || data == getElement(row, col)) {
            if (row == colHeader[col].getRow()) {
                //delete header
                colHeader[col] = colHeader[col].getNextRow();
            } else {
                MatrixEntry lastRowElement = colHeader[col];
                while (lastRowElement.getNextRow().getNextRow() != null && lastRowElement.getNextRow().getRow() != row) {
                    lastRowElement = lastRowElement.getNextRow();
                }

                if (lastRowElement.getNextRow().getNextRow() == null) {
                    //at the end
                    lastRowElement.setNextRow(null);
                } else {
                    //insert
                    MatrixEntry beforeOne = lastRowElement;
                    MatrixEntry afterOne = lastRowElement.getNextRow().getNextRow();
                    beforeOne.setNextRow(afterOne);
                }
            }

            if (col == rowHeader[row].getColumn()) {
                //delete rowHeader
                rowHeader[row] = rowHeader[row].getNextCol();
            } else {
                MatrixEntry lastColElement = rowHeader[row];
                while (lastColElement.getNextCol().getNextCol() != null && lastColElement.getNextCol().getColumn() != col) {
                    lastColElement = lastColElement.getNextCol();
                }

                if (lastColElement.getNextCol().getNextCol() == null) {
                    //at the end
                    lastColElement.setNextCol(null);
                } else {
                    //insert
                    MatrixEntry beforeOne = lastColElement;
                    MatrixEntry afterOne = lastColElement.getNextCol().getNextCol();
                    beforeOne.setNextCol(afterOne);
                }
            }
            return true;
        }
        return false;
    }

    public int getNumCols() {
        return numCols;
    }

    public int getNumRows() {
        return numRows;
    }

    /**
     * This method adds each number from new Matrix to the current matrix
     *
     * @param otherMat another matrix to be added to each element
     * @return true if they can/are be properly added
     */
    public boolean plus(SparseIntMatrix otherMat) {
        if (numRows == otherMat.getNumRows() && numCols == otherMat.getNumCols()) {
            //2 matrices match properly
            int i, j, added;

            for (i = 0; i < numRows; i++) {
                for (j = 0; j < numCols; j++) {
                    added = getElement(i, j) + otherMat.getElement(i, j);
                    //add current element with other matrix element
                    setElement(i, j, added);
                }
            }
            return true;
        }
        return false;
    }


    /**
     * This method subtracts each number from new Matrix to the current matrix
     *
     * @param otherMat another matrix to be subtracted to each element
     * @return true if they can/are be properly subtracted
     */
    public boolean minus(SparseIntMatrix otherMat) {
        if (numRows == otherMat.getNumRows() && numCols == otherMat.getNumCols()) {
            int i, j, added;

            for (i = 0; i < numRows; i++) {
                for (j = 0; j < numCols; j++) {
                    added = getElement(i, j) - otherMat.getElement(i, j);
                    //minus current element with other matrix element
                    if (added != 0){
                        setElement(i, j, added);
                    } else{
                        removeElement(i,j,added);
                        //remove if added is 0
                    }
                }
            }
            return true;
        }
        return false;
    }
}
