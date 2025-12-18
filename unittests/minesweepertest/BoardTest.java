package minesweepertest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import Controller.GameController;
import Model.Board;
import Model.GameConfig;
import Model.Player;

class BoardTest {

	@Test
	void testBoardInitializationEasy() {
		GameController gc = new GameController("arwa", "sharbel", GameConfig.EASY);
		Board board2 = gc.getBoard2();
		Board board1 = gc.getBoard1();
	    assertEquals(9, board1.getRows());
	    assertEquals(9, board1.getColumns());
	    assertEquals(9, board2.getRows());
	    assertEquals(9, board2.getColumns());
	    assertNotNull(board1.getGrid());
	    assertNotNull(board2.getGrid());


	}

	@Test
	void testBoardInitializationMedium() {
		GameController gc = new GameController("Toma", "bshara", GameConfig.MEDIUM);
        Board board1 = gc.getBoard1();
		Board board2 = gc.getBoard2();

	    assertEquals(13, board2.getRows());
	    assertEquals(13, board2.getColumns());
	    assertEquals(13, board1.getColumns());
	    assertEquals(13, board1.getRows());
	    assertNotNull(board2.getGrid());
	    assertNotNull(board1.getGrid());

	   
	}

	@Test
	void testBoardInitializationHard() {
		GameController gc = new GameController("sharbel", "bshara", GameConfig.HARD);
		Board board2 = gc.getBoard2();
        Board board1 = gc.getBoard1();

	    assertEquals(16, board2.getRows());
	    assertEquals(16, board2.getColumns());
	    assertEquals(16, board1.getColumns());
	    assertEquals(16, board1.getRows());
	    assertNotNull(board1.getGrid());
	    assertNotNull(board2.getGrid());

	    
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
