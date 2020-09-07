package checkers;

import checkers.exception.BadMoveException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CheckersBoard {
	public static final char RED_PLAIN = 'r';
	public static final char RED_CROWNED = 'R';
	public static final char BLACK_PLAIN = 'b';
	public static final char BLACK_CROWNED = 'B';
	public static final char INVALID = '*';
	public static final char EMPTY = ' ';

	protected char[][] board;
	protected Player currentPlayer;

	protected boolean captureLock;
	protected int captureStartRow;
	protected int captureStartCol;

	protected CheckersBoard() {
		board = new char[8][8];
		captureLock = false;
	}

	public char[][] getBoard() {
		return board;
	}

	public enum Player {
		RED, BLACK;
	}

	public Player otherPlayer(Player player) {
		if (player == Player.BLACK) {
			return Player.RED;
		}
		return Player.BLACK;
	}

	public Player otherPlayer() {
		return otherPlayer(currentPlayer);
	}

	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	public boolean isCaptureLock() {
		return captureLock;
	}

	public int getCaptureStartRow() {
		return captureStartRow;
	}

	public int getCaptureStartCol() {
		return captureStartCol;
	}

	public CheckersBoard clone() {
		CheckersBoard clone = new CheckersBoard();
		for (int i=0;i<8;i++) {
			System.arraycopy(this.board[i], 0, clone.board[i],0, this.board[i].length);
		}
		clone.currentPlayer = this.currentPlayer;
		clone.captureLock = this.captureLock;
		clone.captureStartCol = this.captureStartCol;
		clone.captureStartRow = this.captureStartRow;
		return clone;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		CheckersBoard that = (CheckersBoard) o;
		return IntStream.range(0, board.length).allMatch(i -> Arrays.equals(board[i], that.board[i])) && currentPlayer == that.currentPlayer;
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(currentPlayer);
		result = 31 * result + Arrays.hashCode(board);
		return result;
	}

	public static CheckersBoard initBoard() {
		CheckersBoard startingBoard = new CheckersBoard();
		startingBoard.currentPlayer = Player.BLACK;
		boolean invalidSquare = true;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (invalidSquare) {
					startingBoard.board[i][j] = INVALID;
				} else {
					if (i < 3) {
						startingBoard.board[i][j] = RED_PLAIN;
					} else if (i > 4) {
						startingBoard.board[i][j] = BLACK_PLAIN;
					} else {
						startingBoard.board[i][j] = EMPTY;
					}
				}
				invalidSquare = !invalidSquare;
			}
			// the color of the first square of a new row is
			// the same color as the last square of the previous row
			// so we "revert" the color flip
			// otherwise, we end up with a vertically-striped board
			invalidSquare = !invalidSquare;
		}
		return startingBoard;
	}

	public String representation(char internal) {
		if (internal == '*') {
			return "\u2588\u2588\u2588";
		}
		return " " + internal + " ";
	}

	private void printTopOfBoard() {
		System.out.println("   \u2554\u2550\u2550\u2550\u2564\u2550\u2550\u2550\u2564\u2550\u2550\u2550\u2564\u2550\u2550\u2550\u2564\u2550\u2550\u2550\u2564\u2550\u2550\u2550\u2564\u2550\u2550\u2550\u2564\u2550\u2550\u2550\u2557");
	}
	private void printBottomOfBoard() {
		System.out.println("   \u255A\u2550\u2550\u2550\u2567\u2550\u2550\u2550\u2567\u2550\u2550\u2550\u2567\u2550\u2550\u2550\u2567\u2550\u2550\u2550\u2567\u2550\u2550\u2550\u2567\u2550\u2550\u2550\u2567\u2550\u2550\u2550\u255D");
	}
	private void printMiddleOfBoard() {
		System.out.println("   \u255F\u2500\u2500\u2500\u253C\u2500\u2500\u2500\u253C\u2500\u2500\u2500\u253C\u2500\u2500\u2500\u253C\u2500\u2500\u2500\u253C\u2500\u2500\u2500\u253C\u2500\u2500\u2500\u253C\u2500\u2500\u2500\u2562");
	}

	public void printBoard() {
		System.out.println("     0   1   2   3   4   5   6   7  ");
		for (int i = 0; i < 8; i++) {
			if (i==0) {
				printTopOfBoard();
			} else {
				printMiddleOfBoard();
			}
			//System.out.println("   " + "+---".repeat(8) + "+");
			System.out.print(i + "  ");
			for (int j = 0; j < 8; j++) {
				System.out.print((j == 0 ? "\u2551" : "\u2502") + representation(board[i][j]));
			}
			System.out.println("\u2551");
		}
		printBottomOfBoard();
		System.out.println("The current player is: " + currentPlayer + " " + countPiecesOfPlayer(currentPlayer) + " pcs vs. " + countPiecesOfPlayer(otherPlayer()) + " pcs.");
	}

	public int countPiecesOfPlayer(Player player) {
		int numPieces = 0;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if ((Player.RED.equals(player) && board[i][j] == 'r' || board[i][j] == 'R')//
						|| (Player.BLACK.equals(player) && board[i][j] == 'b' || board[i][j] == 'B')) {
					numPieces++;
				}
			}
		}
		return numPieces;
	}

	private boolean isEnemyPiece(int i, int j) {
		return isEnemyPiece(currentPlayer, i, j);
	}

	private boolean isEnemyPiece(Player player, int i, int j) {
		return (player == Player.BLACK && Character.toLowerCase(board[i][j]) == 'r')//
				|| (player == Player.RED && Character.toLowerCase(board[i][j]) == 'b');
	}

	public boolean isMovePossible() {
		return isMovePossible(currentPlayer);
	}

	public boolean isMovePossible(Player player) {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				Optional<Player> ownerOfPosition = ownerOf(i,j);
				if (ownerOfPosition.isEmpty() || ownerOfPosition.get() != player) {
					continue;
				}
				if (isDownRightMovePossible(i, j)//
						|| isUpLeftMovePossible(i, j)//
						|| isDownLeftMovePossible(i, j)//
						|| isUpRightMovePossible(i, j)) {
					return true;
				}
			}
		}
		return false;
	}

	public List<CheckersMove> possibleMoves(Player player) {
		List<CheckersMove> possibleMoves = new ArrayList<>();
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				Optional<Player> ownerOfPosition = ownerOf(i,j);
				if (ownerOfPosition.isEmpty() || ownerOfPosition.get() != player) {
					continue;
				}
				if (isDownRightMovePossible(i, j)) {
					possibleMoves.add(CheckersMove.builder().fromPosition(i,j).toPosition(i+1, j+1).build());
				}
				if (isUpLeftMovePossible(i, j)) {
					possibleMoves.add(CheckersMove.builder().fromPosition(i,j).toPosition(i-1, j-1).build());
				}
				if (isDownLeftMovePossible(i, j)) {
					possibleMoves.add(CheckersMove.builder().fromPosition(i,j).toPosition(i+1, j-1).build());
				}
				if (isUpRightMovePossible(i, j)) {
					possibleMoves.add(CheckersMove.builder().fromPosition(i,j).toPosition(i-1, j+1).build());
				}
			}
		}
		return possibleMoves;
	}

	public List<CheckersMove> possibleMoves() {
		return possibleMoves(currentPlayer);
	}

	public boolean isUpRightMovePossible(int i, int j) {
		return i > 0 && j < 7 && board[i - 1][j + 1] == EMPTY
				// we exclude the non-crowned piece that cannot move in this direction
				&& board[i][j] != RED_PLAIN;
	}

	public boolean isDownLeftMovePossible(int i, int j) {
		return i < 7 && j > 0 && board[i + 1][j - 1] == EMPTY
				// we exclude the non-crowned piece that cannot move in this direction
				&& board[i][j] != BLACK_PLAIN;
	}

	public boolean isUpLeftMovePossible(int i, int j) {
		return i > 0 && j > 0 && board[i - 1][j - 1] == EMPTY
				// we exclude the non-crowned piece that cannot move in this direction
				&& board[i][j] != RED_PLAIN;
	}

	public boolean isDownRightMovePossible(int i, int j) {
		return i < 7 && j < 7 && board[i + 1][j + 1] == EMPTY
				// we exclude the non-crowned piece that cannot move in this direction
				&& board[i][j] != BLACK_PLAIN;
	}

	public List<CheckersMove> possibleCaptures() {
		return possibleCaptures(currentPlayer);
	}

	public List<CheckersMove> possibleCaptures(Player player) {
		List<CheckersMove> captures = new ArrayList<>();
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				Optional<Player> ownerOfPosition = ownerOf(i,j);
				if (ownerOfPosition.isEmpty() || ownerOfPosition.get() != player) {
					continue;
				}
				if (captureLock && (i != captureStartRow || j != captureStartCol)) {
					continue;
				}
				if (isDownRightCapturePossible(player, i, j)) {
					captures.add(CheckersMove.builder().fromPosition(i,j).toPosition(i+2, j+2).build());
				}
				if (isUpLeftCapturePossible(player, i, j)) {
					captures.add(CheckersMove.builder().fromPosition(i,j).toPosition(i-2, j-2).build());
				}
				if (isDownLeftCapturePossible(player, i, j)) {
					captures.add(CheckersMove.builder().fromPosition(i,j).toPosition(i+2, j-2).build());
				}
				if (isUpRightCapturePossible(player, i, j)) {
					captures.add(CheckersMove.builder().fromPosition(i,j).toPosition(i-2, j+2).build());
				}
			}
		}
		return captures;
	}

	public boolean isCapturePossible() {
		return isCapturePossible(currentPlayer);
	}

	public boolean isCapturePossible(Player player) {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (isNotMyPiece(i, j)) {
					continue;
				}
				if (isCapturePossibleAtPosition(player, i, j)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isCapturePossibleAtPosition(Player player, int i, int j) {
		return isDownRightCapturePossible(player, i, j)//
				|| isUpLeftCapturePossible(player, i, j)//
				|| isDownLeftCapturePossible(player, i, j)//
				|| isUpRightCapturePossible(player, i, j);
	}

	public boolean isUpRightCapturePossible(Player player, int i, int j) {
		return i > 1 && j < 6 && isEnemyPiece(player, i - 1, j + 1) && board[i - 2][j + 2] == EMPTY && board[i][j] != RED_PLAIN;
	}

	public boolean isDownLeftCapturePossible(Player player, int i, int j) {
		return i < 6 && j > 1 && isEnemyPiece(player, i + 1, j - 1) && board[i + 2][j - 2] == EMPTY && board[i][j] != BLACK_PLAIN;
	}

	public boolean isUpLeftCapturePossible(Player player, int i, int j) {
		return i > 1 && j > 1 && isEnemyPiece(player, i - 1, j - 1) && board[i - 2][j - 2] == EMPTY && board[i][j] != RED_PLAIN;
	}

	public boolean isDownRightCapturePossible(Player player, int i, int j) {
		return i < 6 && j < 6 && isEnemyPiece(player, i + 1, j + 1) && board[i + 2][j + 2] == EMPTY && board[i][j] != BLACK_PLAIN;
	}

	public boolean isNotMyPiece(int i, int j) {
		return ownerOf(i, j).map(owner -> owner != currentPlayer).orElse(true);
	}

	public Optional<Player> ownerOf(int i, int j) {
		if (board[i][j] == RED_CROWNED || board[i][j] == RED_PLAIN) {
			return Optional.of(Player.RED);
		}
		if (board[i][j] == BLACK_CROWNED || board[i][j] == BLACK_PLAIN) {
			return Optional.of(Player.BLACK);
		}
		return Optional.empty();
	}

	protected void explodeIfMoveIsInvalid(CheckersMove move) throws BadMoveException {
		explodeIfMoveIsOutsideOfBoard(move);
		explodeIfNotMovingOwnPiece(move);
		explodeIfNotMovingIntoEmptySpace(move);
		explodeIfNormalPieceMovesBackwards(move);
	}

	protected void explodeIfNotMovingIntoEmptySpace(CheckersMove move) throws BadMoveException {
		if (board[move.getEndRow()][move.getEndCol()] != EMPTY) {
			throw new BadMoveException("You can only move into empty spaces!");
		}
	}

	protected void explodeIfNormalPieceMovesBackwards(CheckersMove move) throws BadMoveException {
		if (board[move.getStartRow()][move.getStartCol()] == RED_PLAIN && move.getStartRow() > move.getEndRow()) {
			throw new BadMoveException("You cannot move/capture backwards!");
		}
		if (board[move.getStartRow()][move.getStartCol()] == BLACK_PLAIN && move.getStartRow() < move.getEndRow()) {
			throw new BadMoveException("You cannot move/capture backwards!");
		}
	}

	protected void explodeIfNotMovingOwnPiece(CheckersMove move) throws BadMoveException {
		if (currentPlayer == Player.BLACK && Character.toLowerCase(board[move.getStartRow()][move.getStartCol()]) != 'b') {
			throw new BadMoveException("You must move YOUR pieces!");
		}
		if (currentPlayer == Player.RED && Character.toLowerCase(board[move.getStartRow()][move.getStartCol()]) != 'r') {
			throw new BadMoveException("You must move YOUR pieces!");
		}
	}

	protected void explodeIfMoveIsOutsideOfBoard(CheckersMove move) throws BadMoveException {
		if (Stream.of(move.getStartRow(), move.getStartCol(), move.getEndRow(), move.getEndCol()).anyMatch(coord -> coord < 0 || coord > 7)) {
			throw new BadMoveException("All coordinates must be between [0-7]!");
		}
	}

	protected void explodeIfNotCapturingEnemyPiece(CheckersMove capture) throws BadMoveException {
		CheckersMove.Position midPoint = capture.getMiddlePosition();
		int middleRow = midPoint.getRow();
		int middleCol = midPoint.getCol();
		if (!isEnemyPiece(middleRow, middleCol)) {
			throw new BadMoveException("You can only capture your opponent's pieces!");
		}
	}

	public void processMove(CheckersMove move) throws BadMoveException {
		explodeIfMoveIsInvalid(move);

		if (captureLock && (move.getStartCol() != captureStartCol || move.getStartRow() != captureStartRow)) {
			throw new BadMoveException("You must play the chained capture!");
		}

		if (isNormalMove(move)) {
			if (captureLock) {
				throw new BadMoveException("You must play the chained capture!");
			}
			if (isCapturePossible(currentPlayer)) {
				throw new BadMoveException("A capture is possible, so you cannot move!");
			}
			captureLock = false;
			performMove(move);
			switchTurn();
		} else if (isCaptureMove(move)) {
			explodeIfNotCapturingEnemyPiece(move);
			performCapture(move);
			if (isCapturePossibleAtPosition(currentPlayer, move.getEndRow(), move.getEndCol())) {
				captureLock = true;
				captureStartCol = move.getEndCol();
				captureStartRow = move.getEndRow();
			} else {
				switchTurn();
				captureLock = false;
			}
		} else {
			throw new BadMoveException("You can only move 1 space away, or capture 2 spaces away!");
		}

		crownPiecesOnBoard();
	}

	protected void performMove(CheckersMove move) {
		board[move.getEndRow()][move.getEndCol()] = board[move.getStartRow()][move.getStartCol()];
		board[move.getStartRow()][move.getStartCol()] = EMPTY;
	}

	protected void performCapture(CheckersMove capture) {
		CheckersMove.Position midPoint = capture.getMiddlePosition();
		board[capture.getEndRow()][capture.getEndCol()] = board[capture.getStartRow()][capture.getStartCol()];
		board[midPoint.getRow()][midPoint.getCol()] = EMPTY;
		board[capture.getStartRow()][capture.getStartCol()] = EMPTY;
	}

	protected boolean isCaptureMove(CheckersMove move) {
		return Math.abs(move.getEndRow() - move.getStartRow()) == 2 && Math.abs(move.getEndCol() - move.getStartCol()) == 2;
	}

	protected boolean isNormalMove(CheckersMove move) {
		return Math.abs(move.getEndRow() - move.getStartRow()) == 1 && Math.abs(move.getEndCol() - move.getStartCol()) == 1;
	}

	public void crownPiecesOnBoard() {
		for (int j = 0; j < 8; j++) {
			if (board[7][j] == RED_PLAIN) {
				board[7][j] = RED_CROWNED;
			}
			if (board[0][j] == BLACK_PLAIN) {
				board[0][j] = BLACK_CROWNED;
			}
		}
	}

	/**
	 * Executes a Checkers and returns the loser.
	 * @param player1 A Checkers-playing agent.
	 * @param player2 A Checkers-playing agent.
	 * @return The loser, or {@code Optional.empty()} if there is a tie.
	 */
	public Optional<CheckersPlayer> play(CheckersPlayer player1, CheckersPlayer player2) {
		Map<Player, CheckersPlayer> playerMap = Map.of(//
				Player.BLACK, player1,//
				Player.RED, player2
		);
		Runnable displayLossMessage = () -> {
			System.out.println("Player " + currentPlayer + "/" + playerMap.get(currentPlayer).getClass().getName()+" lost!");
		};
		int numberOfMovesSinceLastCapture = 0;
		do {
			CheckersPlayer playerAgent = playerMap.get(currentPlayer);
			// check if I lost
			int numMyPieces = countPiecesOfPlayer(currentPlayer);
			if (numMyPieces == 0) {
				displayLossMessage.run();
				return Optional.of(playerAgent);
			}
			// check if I can move
			if (!isMovePossible() && !isCapturePossible()) {
				if (enemyCannotMove()) {
					System.out.println("There is a tie!");
					return Optional.empty();
				}
				displayLossMessage.run();
				return Optional.of(playerAgent);
			}
			printBoard();
			CheckersMove moveFromPlayer = playerAgent.play(this.clone());
			try {
				processMove(moveFromPlayer);
			} catch (BadMoveException ex) {
				if (playerAgent instanceof KeyboardPlayer) {
					System.err.println(ex.getMessage());
				} else {
					System.err.println("Invalid move!! This agent has now lost!");
					displayLossMessage.run();
					return Optional.of(playerAgent);
				}
			}
			if (isNormalMove(moveFromPlayer)) {
				numberOfMovesSinceLastCapture++;
			} else {
				numberOfMovesSinceLastCapture = 0;
			}
			if (numberOfMovesSinceLastCapture > 25) {
				System.out.println("DRAW!");
				return Optional.empty();
			}
		} while (true);
	}

	protected boolean enemyCannotMove() {
		// quickly switch over to the other player
		// to check their possible moves/captures
		Player otherPlayer = otherPlayer(currentPlayer);
		return !isMovePossible(otherPlayer) && !isCapturePossible(otherPlayer);
	}

	public void switchTurn() {
		currentPlayer = otherPlayer();
	}
}
