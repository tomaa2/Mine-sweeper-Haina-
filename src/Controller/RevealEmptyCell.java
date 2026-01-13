package Controller;

import Model.Board;
import Model.Cell;
import Model.SoundManager;

public class RevealEmptyCell extends RevealCellTemplate{

	public RevealEmptyCell(GameController gc, Board board, Cell cell, int row, int col) {
		// TODO Auto-generated constructor stub
		super(gc, board, cell, row, col);
	}

	@Override
	protected void revealCell() {
        board.floodReveal(row, col);
    }
	
	@Override
	protected void applyRevealEffect() {
		// TODO Auto-generated method stu
		SoundManager.playRevealEmpty();
		gameController.getGame().modifyScore(1);
	}
	

}
