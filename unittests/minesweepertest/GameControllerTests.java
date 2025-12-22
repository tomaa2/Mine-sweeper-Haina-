package minesweepertest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import Controller.GameController;
import Model.Board;
import Model.Cell;
import Model.CellType;
import Model.GameConfig;
import Model.Question;


class GameControllerTests {

	
	
	//A test function for checking if activating a question cell works as expected
	@Test
	void testActivateQuestionCell() {
		GameController gc = new GameController("Test1", "Test2", GameConfig.EASY);
		Board b1 = gc.getBoard1();
		Cell cell = b1.getCell(0, 0);
		cell.setCellType(CellType.QUESTION);
		gc.revealCell(1, 0, 0);
		
		
		Question q = gc.activateQuestionCell(cell);
		assertNotNull(q);
		assertTrue(cell.isUsed());
		
		
	}
	
	
	
	
		
	//Checking if activating a surprise chnages the score (positive outcome/ negative outcome)
	@Test	
	void testActivateSurpriseCell() {
		GameController gc = new GameController("Test1", "Test2", GameConfig.EASY);
		Board b1 = gc.getBoard1();
		Cell cell = b1.getCell(0, 1);
		cell.setCellType(CellType.SURPRISE);
		gc.revealCell(1, 0, 1);
		
		gc.getGame().modifyScore(50);
		int scoreBefore = gc.getScore();
		
		gc.activateSurpriseCell(cell);
		
		int scoreAfter = gc.getScore();
		
		//Asserts
		assertNotEquals(scoreBefore, scoreAfter);
	}
	
	
	
}
