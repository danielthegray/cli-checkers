package checkers;

import checkers.exception.BadMoveException;

import java.util.Scanner;
import java.util.stream.Stream;

public class CheckersBoard {
	public static final char RED_PLAIN = 'r';
	public static final char RED_CROWNED = 'R';
	public static final char BLACK_PLAIN = 'b';
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

	public Player otherPlayer() {
		if (currentTurn == Player.BLACK) {
			return Player.RED;
		}
		return Player.BLACK;
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
						startingBoard.board[i][j] = RED_PLAIN;
					} else if (i > 4) {
						startingBoard.board[i][j] = BLACK_PLAIN;
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

		System.out.println("     0   1   2   3   4   5   6   7  ");
		for (int i = 0; i < 8; i++) {
			System.out.println("   " + "+---".repeat(8) + "+");
			System.out.print(i +"  ");
			for (int j = 0; j < 8; j++) {
				System.out.print("| " + board[i][j] + " ");
			}
			System.out.println("|");
		}
		System.out.println("   " + "+---".repeat(8) + "+");
		System.out.println("The current player is: " + currentTurn);
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
		return (currentTurn == Player.BLACK && Character.toLowerCase(board[i][j]) == 'r') ||
			 (currentTurn == Player.RED && Character.toLowerCase(board[i][j]) == 'b');
	}

	public boolean isMovePossible() {
		for (int i=0; i<8; i++) {
			for (int j=0; j<8; j++) {
				if (currentTurn == Player.RED && Character.toLowerCase(board[i][j]) != 'r') {
					continue;
				}
				if (currentTurn == Player.BLACK && Character.toLowerCase(board[i][j]) != 'b') {
					continue;
				}
				if (i < 7 && j < 7 && board[i+1][j+1] == EMPTY && board[i][j] != BLACK_PLAIN) {
					return true;
				}
				if (i > 0 && j > 0 && board[i-1][j-1] == EMPTY && board[i][j] != RED_PLAIN) {
					return true;
				}
				if (i < 7 && j > 0 && board[i+1][j-1] == EMPTY && board[i][j] != BLACK_PLAIN) {
					return true;
				}
				if (i > 0 && j < 7 && board[i-1][j+1] == EMPTY && board[i][j] != RED_PLAIN) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isCapturePossible() {
		for (int i=0; i<8; i++) {
			for (int j=0; j<8; j++) {
				if (currentTurn == Player.RED && Character.toLowerCase(board[i][j]) != 'r') {
					continue;
				}
				if (currentTurn == Player.BLACK && Character.toLowerCase(board[i][j]) != 'b') {
					continue;
				}
				if (i < 6 && j < 6 && isEnemyPiece(i+1, j+1) && board[i+2][j+2] == EMPTY && board[i][j] != BLACK_PLAIN) {
					return true;
				}
				if (i > 1 && j > 1 && isEnemyPiece(i-1, j-1) && board[i-2][j-2] == EMPTY && board[i][j] != RED_PLAIN) {
					return true;
				}
				if (i < 6 && j > 1 && isEnemyPiece(i+1, j-1) && board[i+2][j-2] == EMPTY && board[i][j] != BLACK_PLAIN) {
					return true;
				}
				if (i > 1 && j < 6 && isEnemyPiece(i-1, j+1) && board[i-2][j+2] == EMPTY && board[i][j] != RED_PLAIN) {
					return true;
				}
			}
		}
		return false;
	}

	public void doMove(int startRow, int startCol, int endRow, int endCol) {
		if (Stream.of(startRow, startCol, endRow, endCol).anyMatch(coord -> coord < 0 || coord > 7)) {
			throw new BadMoveException("All coordinates must be between [0-7]!");
		}
		if (currentTurn == Player.BLACK && Character.toLowerCase(board[startRow][startCol]) != 'b') {
			throw new BadMoveException("You must move YOUR pieces!");
		}
		if (currentTurn == Player.RED && Character.toLowerCase(board[startRow][startCol]) != 'r') {
			throw new BadMoveException("You must move YOUR pieces!");
		}
		if (board[endRow][endCol] != EMPTY) {
			throw new BadMoveException("You can only move into empty spaces!");
		}
		if (board[startRow][startCol] == RED_PLAIN && startRow > endRow) {
			throw new BadMoveException("You cannot move/capture backwards!");
		}
		if (board[startRow][startCol] == BLACK_PLAIN && startRow < endRow) {
			throw new BadMoveException("You cannot move/capture backwards!");
		}
		// normal move
		if (Math.abs(endRow - startRow) == 1 && Math.abs(endCol-startCol) == 1) {
			if (isCapturePossible()) {
				throw new BadMoveException("A capture is possible, so you cannot move!");
			}
			board[endRow][endCol] = board[startRow][startCol];
			board[startRow][startCol] = EMPTY;
			switchTurn();
		} else if (Math.abs(endRow - startRow) == 2 && Math.abs(endCol-startCol) == 2) {
			// capture
			int middleRow = (startRow + endRow) / 2;
			int middleCol = (startCol + endCol) / 2;
			if (currentTurn == Player.RED && Character.toLowerCase(board[middleRow][middleCol]) != 'b') {
				throw new BadMoveException("You can only capture your opponent's pieces!");
			}
			if (currentTurn == Player.BLACK && Character.toLowerCase(board[middleRow][middleCol]) != 'r') {
				throw new BadMoveException("You can only capture your opponent's pieces!");
			}
			board[endRow][endCol] = board[startRow][startCol];
			board[middleRow][middleCol] = EMPTY;
			board[startRow][startCol] = EMPTY;

			if (!isCapturePossible()) {
				switchTurn();
			}
		} else {
			throw new BadMoveException("You can only move 1 space away, or capture 2 spaces away!");
		}

		crownPiecesOnBoard();
	}

	public void crownPiecesOnBoard() {
		for (int j=0;j<8;j++) {
			if (board[7][j] == RED_PLAIN) {
				board[7][j] = RED_CROWNED;
			}
			if (board[0][j] == BLACK_PLAIN) {
				board[0][j] = BLACK_CROWNED;
			}
		}
	}

	public void play() {
		do {
			// check if I lost
			int numMyPieces = countPiecesOfPlayer(currentTurn);
			if (numMyPieces == 0) {
				System.out.println("Player " + currentTurn + " lost!");
				break;
			}
			// check if I can move
			if (!isMovePossible() && !isCapturePossible()) {
				switchTurn();
				if (!isMovePossible() && !isCapturePossible()) {
					System.out.println("There is a tie!");
					break;
				}
				switchTurn();

				System.out.println("Player " + currentTurn + " lost!");
				break;
			}

			printBoard();
			Scanner scanner = new Scanner(System.in);
			System.out.println("Move the piece from:");
			System.out.print("Row: ");
			int startRow = scanner.nextInt();
			System.out.print("Column: ");
			int startCol = scanner.nextInt();
			System.out.println("to:");
			System.out.print("Row: ");
			int endRow = scanner.nextInt();
			System.out.print("Column: ");
			int endCol = scanner.nextInt();

			try {
				doMove(startRow, startCol, endRow, endCol);
			} catch (BadMoveException ex) {
				System.err.println(ex.getMessage());
			}

		} while (true);
	}
	// TODO: revisar el empate

	public void switchTurn() {
		currentTurn = otherPlayer();
	}
}
