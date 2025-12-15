package minesweepertest;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import Controller.GameController;
import Model.GameConfig;
import Model.Board;
import Model.Cell;

class ArwaTest {
	//GameControllerEndByLivesTest

    @Test
    void checkGameEndByLives_whenLivesZero_returnsTrue_andRevealsAllCells() {
        // Arrange
        GameController gc = new GameController("P1", "P2", GameConfig.EASY);

        // make lives exactly 0 (avoid negative to not affect score bonus)
        int livesNow = gc.getLives();
        gc.getGame().modifyLives(-livesNow);  // <-- assumes modifyLives is public

        // Act
        boolean ended = gc.checkGameEndByLives();

        // Assert: returns true
        assertTrue(ended);

        // Assert: all cells revealed on both boards
        assertAllCellsRevealed(gc.getBoard1());
        assertAllCellsRevealed(gc.getBoard2());
    }

    private static void assertAllCellsRevealed(Board board) {
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getColumns(); c++) {
                Cell cell = board.getCell(r, c);
                assertTrue(cell.isRevealed(), "Cell (" + r + "," + c + ") should be revealed");
            }
        }
    }
}
