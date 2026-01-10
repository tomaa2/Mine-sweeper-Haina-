package Controller;

import Model.Board;
import Model.Cell;

public class RevealSurpriseCell extends RevealCellTemplate{

	public RevealSurpriseCell(GameController gc, Board board, Cell cell, int row, int col) {
		// TODO Auto-generated constructor stub
		super(gc, board, cell, row, col);
	}

	@Override
	protected void applyRevealEffect() {
		// TODO Auto-generated method stub
		gameController.getGame().modifyScore(1);
	}

	@Override
    protected boolean canReveal() {
        // Surprise cell can be revealed only if not revealed and not used
        return cell != null && !cell.isRevealed() && !cell.isUsed() && !cell.isFlagged();
    }
}
