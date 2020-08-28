package checkers.bot.gray;

import checkers.CheckersBoard;
import checkers.CheckersMove;
import checkers.CheckersPlayer;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GrayRandomBot implements CheckersPlayer {

	@Override
	public CheckersMove play(CheckersBoard board) {
		List<CheckersMove> possibleCaptures = board.possibleCaptures();
		if (possibleCaptures.isEmpty()) {
			List<CheckersMove> possibleMoves = board.possibleMoves();
			return possibleMoves.get(ThreadLocalRandom.current().nextInt(possibleMoves.size()));
		}
		return possibleCaptures.get(ThreadLocalRandom.current().nextInt(possibleCaptures.size()));
	}

}
