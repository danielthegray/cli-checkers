package checkers;

import checkers.exception.BadMoveException;

public class CheckersBoard {
	public static final char RED = 'r';
	public static final char RED_CROWNED = 'R';
	public static final char BLACK = 'b';
	public static final char BLACK_CROWNED = 'B';
	public static final char INVALID = '*';
	public static final char EMPTY = ' ';

	private char[][] board;
	private Player currentTurn;

	public CheckersBoard() {
		board = new char[8][8];
	}

	public enum Player {
		RED, BLACK;
	}

	public static CheckersBoard initBoard() {
		CheckersBoard startingBoard = new CheckersBoard();
		startingBoard.currentTurn = Player.BLACK;
		boolean invalidSquare = true;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (invalidSquare) {
					startingBoard.board[i][j] = INVALID;
				} else {
					if (i < 3) {
						startingBoard.board[i][j] = RED;
					} else if (i > 4) {
						startingBoard.board[i][j] = BLACK;
					} else {
						startingBoard.board[i][j] = EMPTY;
					}
				}
				invalidSquare = !invalidSquare;
			}
			invalidSquare = !invalidSquare;
		}
		return startingBoard;
	}

	public void printBoard() {
		for (int i = 0; i < 8; i++) {
			System.out.println("+---".repeat(8) + "+");
			for (int j = 0; j < 8; j++) {
				System.out.print("| " + board[i][j] + " ");
			}
			System.out.println("|");
		}
		System.out.println("+---".repeat(8) + "+");
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

	public void doMove(int startRow, int startCol, int endRow, int endCol) {
		if (currentTurn == Player.BLACK && board[startRow][startCol] != 'B' && board[startRow][startCol] != 'b') {
			throw new BadMoveException("You must move YOUR pieces!");
		}
		if (currentTurn == Player.RED && board[startRow][startCol] != 'R' && board[startRow][startCol] != 'r') {
			throw new BadMoveException("You must move YOUR pieces!");
		}
		if (board[endRow][endCol] != EMPTY) {
			throw new BadMoveException("You can only move into empty spaces!");
		}
		// normal move
		if (Math.abs(endRow - startRow) == 1 && Math.abs(endCol-startCol) == 1) {
			if (board[startRow][startCol] == 'r' && startRow > endRow) {
				throw new BadMoveException("You cannot move backwards!");
			}
			if (board[startRow][startCol] == 'b' && startRow < endRow) {
				throw new BadMoveException("You cannot move backwards!");
			}
			board[endRow][endCol] = board[startRow][startCol];
			board[startRow][startCol] = EMPTY;
			return;
		}

		// capture

	}
}
