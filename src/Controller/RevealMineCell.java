package Controller;

import Model.Board;
import Model.Cell;
import Model.SoundManager;

public class RevealMineCell extends RevealCellTemplate {

	public RevealMineCell(GameController gc, Board board, Cell cell, int row, int col) {
		// TODO Auto-generated constructor stub
		super(gc, board, cell, row, col);
	}
	
	@Override
	protected void applyRevealEffect() {
		// TODO Auto-generated method stub
		SoundManager.playMineExplode();
		gameController.getGame().modifyLives(-1);
	}

}
