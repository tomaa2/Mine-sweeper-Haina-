package minesweepertest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import Controller.GameController;
import Model.Board;
import Model.GameConfig;
import Model.Player;
//Toma
class BoardTest {

	@Test
	void testBoardInitializationEasy() {
		Board board = new Board(9, 9);

	    assertEquals(9, board.getRows());
	    assertEquals(9, board.getColumns());
	    assertNotNull(board.getGrid());
	    assertEquals(9, board.getGrid().length);
	    assertEquals(9, board.getGrid()[0].length);
	}

	@Test
	void testBoardInitializationMedium() {
		Board board = new Board(13, 13);

	    assertEquals(13, board.getRows());
	    assertEquals(13, board.getColumns());
	    assertNotNull(board.getGrid());
	    assertEquals(13, board.getGrid().length);
	    assertEquals(13, board.getGrid()[0].length);
	}

	@Test
	void testBoardInitializationHard() {
		Board board = new Board(16, 16);

	    assertEquals(16, board.getRows());
	    assertEquals(16, board.getColumns());
	    assertNotNull(board.getGrid());
	    assertEquals(16, board.getGrid().length);
	    assertEquals(16, board.getGrid()[0].length);
	}
	

	    // ==================== Test 1: testSwitchTurn ====================
	    // Developer: Toma
	    @Test
	    void testSwitchTurn() {
	        // Arrange
	        GameController gc = new GameController("Toma", "sharbel", GameConfig.EASY);
	        Player initialPlayer = gc.getCurrentPlayer();
	        
	        // Assert initial state
	        assertEquals("Toma", initialPlayer.getName(), "Initial player should be Toma (Player1)");
	        assertTrue(initialPlayer.isTurn(), "Initial player should have turn = true");
	        
	        // Act
	        gc.switchTurn();
	        
	        // Assert after switch
	        Player newPlayer = gc.getCurrentPlayer();
	        assertEquals("sharbel", newPlayer.getName(), "After switch, current player should be sharbel (Player2)");
	        assertTrue(newPlayer.isTurn(), "New player should have turn = true");
	        assertFalse(initialPlayer.isTurn(), "Previous player should have turn = false");
	    }

	    

	


}
