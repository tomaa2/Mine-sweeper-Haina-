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

	
	private void initializeCells() {
		for(int i=0; i<rows; i++) {
			for(int j=0; j<columns; j++) {
				grid[i][j]= new Cell();
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
	

	public int getRows() {
		return rows;
	}


	public int getColumns() {
		return columns;
	}
	
	
	public Cell[][] getGrid(){
		return grid;
	}
	
}
