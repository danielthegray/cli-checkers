package checkers;

import java.util.Scanner;

public class KeyboardPlayer implements CheckersPlayer {

	private final Scanner scanner;

	public KeyboardPlayer() {
		scanner = new Scanner(System.in);
	}

	@Override
	public CheckersMove play(CheckersBoard board) {
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
		return CheckersMove.builder()
				.fromPosition(startRow, startCol)
				.toPosition(endRow, endCol)
				.build();
	}
}
