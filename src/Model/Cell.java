package Model;

public class Cell {
	private CellType cellType;
	private boolean isRevealed; 		//if the cell is already revealed
	private boolean isUsed;			//for question and surprise cells
	private int adjacentBombs; 	  //only for number cells
	private int row;
    private int col;
    private boolean flagged;
    
    
    public Cell() {
    	
    }
    
    public Cell(int row, int col) {
    	this.row=row;
    	this.col=col;
    	this.isRevealed=false;
    	this.isUsed=false;
    	this.flagged=false;
		this.cellType = CellType.EMPTY;  // default to empty

    }


	public CellType getCellType() {
		return cellType;
	}


	public void setCellType(CellType cellType) {
		this.cellType = cellType;
	}


	public boolean isRevealed() {
		return isRevealed;
	}


	public void setRevealed(boolean isReveald) {
		this.isRevealed = isReveald;
	}


	public boolean isUsed() {
		return isUsed;
	}


	public void setUsed(boolean isUsed) {
		this.isUsed = isUsed;
	}


	public int getNumber() {
		return adjacentBombs;
	}


	public void setNumber(int number) {
		this.adjacentBombs = number;
	}


	public int getRow() {
		return row;
	}


	public void setRow(int row) {
		this.row = row;
	}


	public int getCol() {
		return col;
	}


	public void setCol(int col) {
		this.col = col;
	}
	
	public boolean isFlagged() {
		return flagged;
	}
    
	public void setFlagged(boolean flagged) {
		this.flagged=flagged;
	}
    
}
