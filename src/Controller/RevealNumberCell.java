package Controller;

import Model.Board;
import Model.Cell;
import Model.SoundManager;

public class RevealNumberCell extends RevealCellTemplate{

	public RevealNumberCell(GameController gc, Board board, Cell cell, int row, int col) {
		// TODO Auto-generated constructor stub
		super(gc, board, cell, row, col);
	}

	@Override
	protected void applyRevealEffect() {
		// TODO Auto-generated method stub
		SoundManager.playReveal();
		gameController.getGame().modifyScore(1);
	}

}
