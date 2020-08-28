package checkers.bot.gray;

import checkers.CheckersBoard;
import checkers.CheckersMove;
import checkers.CheckersPlayer;

public class GrayRandomBot implements CheckersPlayer {

	@Override
	public CheckersMove play(CheckersBoard board) {
		return CheckersMove.builder().fromPosition(0,0).toPosition(0,0).build();
	}

}
