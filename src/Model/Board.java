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

	
	//
	private void initializeCells() {
		for(int i=0; i<rows; i++) {
			for(int j=0; j<columns; j++) {
				grid[i][j]= new Cell(i,j);
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
	
	
	//revealing all the empty neighbors of an empty cell when revealed!
	public void floodReveal(int row, int col) {
		if(grid[row][col].getCellType() != CellType.EMPTY) {
			return;
		}
		for(int dr=-1; dr<=1; dr++) {
			for(int dc=-1; dc<=1; dc++) {
				int newRow = row + dr;
				int newCol = col + dc;
				
				if(!isInBounds(newRow, newCol))
					continue;
				
				Cell neighbor = grid[newRow][newCol];
				if(!neighbor.isRevealed() && !neighbor.isFlagged()) {
					neighbor.setRevealed(true);
					
					if (neighbor.getCellType() == CellType.EMPTY) {
	                    floodReveal(newRow, newCol);
	                }
				}
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
