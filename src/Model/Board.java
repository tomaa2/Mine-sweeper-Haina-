package Model;

import java.util.*;

public class Board {
	private final int rows;
	private final int columns;
	private Cell[][] grid;
	
	
	public Board(int rows, int columns) {
		this.rows=rows;
		this.columns=columns;
		grid = new Cell[rows][columns];
		initializeCells();
	}

	
	//function for creating an empty cell
	private Cell createCell(int row, int col, CellType type) {
		Cell cell = new Cell(row,col);
		cell.setCellType(type);
		return cell;
	}
	
	//initializing the two boards with empty cells
	private void initializeCells() {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				grid[i][j] = createCell(i,j,CellType.EMPTY);
			}
		}
	}
	
	//spreading the bombs randomly across the board randomly
	public void placeBombs(int bombsToPlace) {
	    int placed = 0;
	    Random rand = new Random();

	    while (placed < bombsToPlace) {
	        int r = rand.nextInt(rows);
	        int c = rand.nextInt(columns);

	        Cell cell = grid[r][c];

	        // place bomb only if empty
	        if (cell.getCellType() == CellType.EMPTY) {
	            cell.setCellType(CellType.MINE);
	            placed++;
	        }
	    }
	}
	
	//spreading the questions and the surprise cells across the board randomly 
	public void placeSpecials(int numOfQues, int numOfSur) {
		int ques = 0, sur=0;
		Random rand = new Random();
		
		while(ques<numOfQues) {
			int r = rand.nextInt(rows);
	        int c = rand.nextInt(columns);

	        Cell cell = grid[r][c];
	     // place question only if empty
	        if (cell.getCellType() == CellType.EMPTY) {
	            cell.setCellType(CellType.QUESTION);
	            ques++;
	        }
		}
		
		while(sur<numOfSur) {
			int r = rand.nextInt(rows);
	        int c = rand.nextInt(columns);

	        Cell cell = grid[r][c];
	     // place surprise only if empty
	        if (cell.getCellType() == CellType.EMPTY) {
	            cell.setCellType(CellType.SURPRISE);
	            sur++;
	        }
		}
	}
	
	
	// revealing all connected empty cells when an empty cell is revealed (cascade)
	public void floodReveal(int row, int col) {
		// boundary check
		if (!isInBounds(row, col)) {
			return;
		}
		Cell cell = grid[row][col];
		
		// stop if already revealed or flagged
		if (cell.isRevealed() || cell.isFlagged()) {
			return;
		}
		
		// reveal the current cell
		cell.setRevealed(true);
	    //if its a NUMBER cell then reveal it but dont continue flood
	    if (cell.getCellType() == CellType.NUMBER) {
	        return;
	    }

		// recursively reveal all 8 neighbors
		for (int dr = -1; dr <= 1; dr++) {
			for (int dc = -1; dc <= 1; dc++) {
				if (dr == 0 && dc == 0) {
					continue; // skip the center cell
				}
				floodReveal(row + dr, col + dc);
			}
		}
	}
	private boolean isInBounds(int row, int col) {
	    return row >= 0 && row < rows && col >= 0 && col < columns;
	}


	public int getRows() {
		return rows;
	}


	public int getColumns() {
		return columns;
	}
	
	
	public Cell[][] getGrid(){
		return grid;
	}
	
	public Cell getCell(int row, int col) {
		if (row < 0 || row >= rows || col < 0 || col >= columns)
	        return null;
		return this.grid[row][col];
	}
	
	public void setCell(int row, int col, Cell cell) {
		if(row>=0 && row<rows && col>=0 && col<columns)
			grid[row][col] = cell;
	}
}
