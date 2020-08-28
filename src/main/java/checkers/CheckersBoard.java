package checkers;

import checkers.exception.BadMoveException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

public class CheckersBoard {
	public static final char RED_PLAIN = 'r';
	public static final char RED_CROWNED = 'R';
	public static final char BLACK_PLAIN = 'b';
	public static final char BLACK_CROWNED = 'B';
	public static final char INVALID = '*';
	public static final char EMPTY = ' ';

	private char[][] board;
	private Player currentPlayer;

	public CheckersBoard() {
		board = new char[8][8];
	}

	public enum Player {
		RED, BLACK;
	}

	public Player otherPlayer() {
		if (currentPlayer == Player.BLACK) {
			return Player.RED;
		}
		return Player.BLACK;
	}

	public CheckersBoard clone() {
		CheckersBoard clone = new CheckersBoard();
		for (int i=0;i<8;i++) {
			for (int j=0;j<8;j++) {
				clone.board[i][j] = this.board[i][j];
			}
		}
		clone.currentPlayer = this.currentPlayer;
		return clone;
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

	public void printBoard() {
		System.out.println("     0   1   2   3   4   5   6   7  ");
		for (int i = 0; i < 8; i++) {
			System.out.println("   " + "+---".repeat(8) + "+");
			System.out.print(i + "  ");
			for (int j = 0; j < 8; j++) {
				System.out.print("| " + board[i][j] + " ");
			}
			System.out.println("|");
		}
		System.out.println("   " + "+---".repeat(8) + "+");
		System.out.println("The current player is: " + currentPlayer);
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
		return (currentPlayer == Player.BLACK && Character.toLowerCase(board[i][j]) == 'r')//
				|| (currentPlayer == Player.RED && Character.toLowerCase(board[i][j]) == 'b');
	}

	public boolean isMovePossible() {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (isNotMyPiece(i, j)) {
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

	private boolean isUpRightMovePossible(int i, int j) {
		return i > 0 && j < 7 && board[i - 1][j + 1] == EMPTY
				// we exclude the non-crowned piece that cannot move in this direction
				&& board[i][j] != RED_PLAIN;
	}

	private boolean isDownLeftMovePossible(int i, int j) {
		return i < 7 && j > 0 && board[i + 1][j - 1] == EMPTY
				// we exclude the non-crowned piece that cannot move in this direction
				&& board[i][j] != BLACK_PLAIN;
	}

	private boolean isUpLeftMovePossible(int i, int j) {
		return i > 0 && j > 0 && board[i - 1][j - 1] == EMPTY
				// we exclude the non-crowned piece that cannot move in this direction
				&& board[i][j] != RED_PLAIN;
	}

	private boolean isDownRightMovePossible(int i, int j) {
		return i < 7 && j < 7 && board[i + 1][j + 1] == EMPTY
				// we exclude the non-crowned piece that cannot move in this direction
				&& board[i][j] != BLACK_PLAIN;
	}

	public boolean isCapturePossible() {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (isNotMyPiece(i, j)) {
					continue;
				}
				if (isDownRightCapturePossible(i, j)//
						|| isUpLeftCapturePossible(i, j)//
						|| isDownLeftCapturePossible(i, j)//
						|| isUpRightCapturePossible(i, j)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isUpRightCapturePossible(int i, int j) {
		return i > 1 && j < 6 && isEnemyPiece(i - 1, j + 1) && board[i - 2][j + 2] == EMPTY && board[i][j] != RED_PLAIN;
	}

	private boolean isDownLeftCapturePossible(int i, int j) {
		return i < 6 && j > 1 && isEnemyPiece(i + 1, j - 1) && board[i + 2][j - 2] == EMPTY && board[i][j] != BLACK_PLAIN;
	}

	private boolean isUpLeftCapturePossible(int i, int j) {
		return i > 1 && j > 1 && isEnemyPiece(i - 1, j - 1) && board[i - 2][j - 2] == EMPTY && board[i][j] != RED_PLAIN;
	}

	private boolean isDownRightCapturePossible(int i, int j) {
		return i < 6 && j < 6 && isEnemyPiece(i + 1, j + 1) && board[i + 2][j + 2] == EMPTY && board[i][j] != BLACK_PLAIN;
	}

	private boolean isNotMyPiece(int i, int j) {
		return (currentPlayer == Player.RED && Character.toLowerCase(board[i][j]) != 'r') || (currentPlayer == Player.BLACK
				&& Character.toLowerCase(board[i][j]) != 'b');
	}

	private void explodeIfMoveIsInvalid(CheckersMove move) throws BadMoveException {
		explodeIfMoveIsOutsideOfBoard(move);
		explodeIfNotMovingOwnPiece(move);
		explodeIfNotMovingIntoEmptySpace(move);
		explodeIfNormalPieceMovesBackwards(move);
	}

	private void explodeIfNotMovingIntoEmptySpace(CheckersMove move) throws BadMoveException {
		if (board[move.getEndRow()][move.getEndCol()] != EMPTY) {
			throw new BadMoveException("You can only move into empty spaces!");
		}
	}

	private void explodeIfNormalPieceMovesBackwards(CheckersMove move) throws BadMoveException {
		if (board[move.getStartRow()][move.getStartCol()] == RED_PLAIN && move.getStartRow() > move.getEndRow()) {
			throw new BadMoveException("You cannot move/capture backwards!");
		}
		if (board[move.getStartRow()][move.getStartCol()] == BLACK_PLAIN && move.getStartRow() < move.getEndRow()) {
			throw new BadMoveException("You cannot move/capture backwards!");
		}
	}

	private void explodeIfNotMovingOwnPiece(CheckersMove move) throws BadMoveException {
		if (currentPlayer == Player.BLACK && Character.toLowerCase(board[move.getStartRow()][move.getStartCol()]) != 'b') {
			throw new BadMoveException("You must move YOUR pieces!");
		}
		if (currentPlayer == Player.RED && Character.toLowerCase(board[move.getStartRow()][move.getStartCol()]) != 'r') {
			throw new BadMoveException("You must move YOUR pieces!");
		}
	}

	private void explodeIfMoveIsOutsideOfBoard(CheckersMove move) throws BadMoveException {
		if (Stream.of(move.getStartRow(), move.getStartCol(), move.getEndRow(), move.getEndCol()).anyMatch(coord -> coord < 0 || coord > 7)) {
			throw new BadMoveException("All coordinates must be between [0-7]!");
		}
	}

	private void explodeIfNotCapturingEnemyPiece(CheckersMove capture) throws BadMoveException {
		CheckersMove.Position midPoint = capture.getMiddlePosition();
		int middleRow = midPoint.getRow();
		int middleCol = midPoint.getCol();
		if (!isEnemyPiece(middleRow, middleCol)) {
			throw new BadMoveException("You can only capture your opponent's pieces!");
		}
	}

	public void processMove(CheckersMove move) throws BadMoveException {
		explodeIfMoveIsInvalid(move);

		if (isNormalMove(move)) {
			if (isCapturePossible()) {
				throw new BadMoveException("A capture is possible, so you cannot move!");
			}
			performMove(move);
			switchTurn();
		} else if (isCaptureMove(move)) {
			explodeIfNotCapturingEnemyPiece(move);
			performCapture(move);
			if (!isCapturePossible()) {
				switchTurn();
			}
		} else {
			throw new BadMoveException("You can only move 1 space away, or capture 2 spaces away!");
		}

		crownPiecesOnBoard();
	}

	private void performMove(CheckersMove move) {
		board[move.getEndRow()][move.getEndCol()] = board[move.getStartRow()][move.getStartCol()];
		board[move.getStartRow()][move.getStartCol()] = EMPTY;
	}

	private void performCapture(CheckersMove capture) {
		CheckersMove.Position midPoint = capture.getMiddlePosition();
		board[capture.getEndRow()][capture.getEndCol()] = board[capture.getStartRow()][capture.getStartCol()];
		board[midPoint.getRow()][midPoint.getCol()] = EMPTY;
		board[capture.getStartRow()][capture.getStartCol()] = EMPTY;
	}

	private boolean isCaptureMove(CheckersMove move) {
		return Math.abs(move.getEndRow() - move.getStartRow()) == 2 && Math.abs(move.getEndCol() - move.getStartCol()) == 2;
	}

	private boolean isNormalMove(CheckersMove move) {
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
		} while (true);
	}

	private boolean enemyCannotMove() {
		// quickly switch over to the other player
		// to check their possible moves/captures
		switchTurn();
		boolean enemyCannotMove = false;
		if (!isMovePossible() && !isCapturePossible()) {
			enemyCannotMove = true;
		}
		// switch the turn back to the real "current player"
		switchTurn();
		return enemyCannotMove;
	}

	public void switchTurn() {
		currentPlayer = otherPlayer();
	}
}
