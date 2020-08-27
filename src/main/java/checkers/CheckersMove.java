package checkers;

public class CheckersMove {
	private int startRow;
	private int startCol;
	private int endRow;
	private int endCol;

	private CheckersMove() {}

	public static Builder builder() {
		return new Builder();
	}

	public int getStartRow() {
		return startRow;
	}

	public int getStartCol() {
		return startCol;
	}

	public int getEndRow() {
		return endRow;
	}

	public int getEndCol() {
		return endCol;
	}

	public Position getMiddlePosition() {
		int middleRow = (this.getStartRow() + this.getEndRow()) / 2;
		int middleCol = (this.getStartCol() + this.getEndCol()) / 2;
		return new Position(middleRow, middleCol);
	}

	public static class Position {
		final int row;
		final int col;

		public Position(int row, int col) {
			this.row = row;
			this.col = col;
		}

		public int getRow() {
			return row;
		}

		public int getCol() {
			return col;
		}
	}

	public static class Builder {

		private final CheckersMove move;

		private Builder() {
			move = new CheckersMove();
		}

		public Builder fromPosition(int startRow, int startCol) {
			move.startRow = startRow;
			move.startCol = startCol;
			return this;
		}

		public Builder toPosition(int endRow, int endCol) {
			move.endRow = endRow;
			move.endCol = endCol;
			return this;
		}

		public CheckersMove build() {
			return move;
		}

	}
}
