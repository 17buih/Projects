public class MatrixEntry {

	private int row, col, data;
	private MatrixEntry dataRight, dataDown;

	public MatrixEntry(int row, int col, int data) {
		this.row = row;
		this.col = col;
		this.data = data;
		dataRight = null;
		dataDown = null;
	}

	public int getColumn() {
		return col;
	}

	public void setColumn(int col) {
		this.col = col;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getData() {
		return data;
	}

	public void setData(int data) {
		this.data = data;
	}

	public MatrixEntry getNextRow() {
		return dataDown;
	}

	public void setNextRow(MatrixEntry el) {
		dataDown = el;
	}

	public MatrixEntry getNextCol() {
		return dataRight;
	}

	public void setNextCol(MatrixEntry el) {
		dataRight = el;
	}
}