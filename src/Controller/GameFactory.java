package Controller;

import Model.Board;
import Model.Cell;
import Model.Player;

public class GameFactory {
	//function for creating new Player
	public Player createPlayer(String name) {
		System.out.println("Creating Player: " + name);
		return new Player(name);
	}
	
	//func for creating new Cell
	public Cell createCell(int row, int col) {
		System.out.println("Creating Cells");
		return new Cell(row,col);
	}
	
	//func for creating new Board
	public Board createBoard(int rows, int cols) {
		System.out.println("Creating Boards");
		return new Board(rows, cols);
	}
	
}
