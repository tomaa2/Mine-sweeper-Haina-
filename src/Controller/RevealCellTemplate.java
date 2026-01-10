package Controller;

import Model.Board;
import Model.Cell;

public abstract class RevealCellTemplate {
	protected final GameController gameController;
    protected final Board board;
    protected final Cell cell;
    protected final int row;
    protected final int col;
    
    public RevealCellTemplate(GameController gc, Board board, Cell cell, int row, int col) {
        this.gameController = gc;
        this.board = board;
        this.cell = cell;
        this.row = row;
        this.col = col;
    }
    
    //Template method that defines the step of revealing a cell
    public final void reveal() {
    	if(!canReveal())
    		return;
    	
    	applySound();
    	revealCell();
    	applyRevealEffect();
        afterReveal();
    }
    
    // steps
    protected boolean canReveal() {
        return cell != null && !cell.isRevealed() && !cell.isFlagged();
    }

    protected void revealCell() {
        cell.setRevealed(true);
    }

    //applying the sound of revealing a cell
    private void applySound() {
    	//System.out.print("Applying the sound of revealing a cell");
    	//rest of the logic of applying a sound
    }
    
    //abstract method to be implemented in the subclasses
    protected abstract void applyRevealEffect();

    protected void afterReveal() {
        gameController.checkGameEnd();
        gameController.switchTurn();
    }
    
    
}
