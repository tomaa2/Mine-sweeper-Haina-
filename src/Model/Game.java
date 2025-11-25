package Model;

//Main game class that manages the overall game state

public class Game {
	private final Player player1;
	private final Player player2;
	private Board board1;
	private Board board2;
	private final GameConfig config;
	private int score;
	private int lives;
	private boolean gameStarted;
	private int currentPlayerIndex; // 1,2

	public Game(Player player1, Player player2, GameConfig config) {
		this.player1 = player1;
		this.player2 = player2;
		this.config = config;
		this.score = 0;
		this.lives = config.getStartingLives();
		this.currentPlayerIndex = 1;
		this.gameStarted = false;
		// Initialize boards
		initializeBoards();
	}

	private void initializeBoards() {
		int gridSize = config.getGridSize();

		// create boards for both players
		board1 = new Board(gridSize, gridSize);
		board2 = new Board(gridSize, gridSize);

		// place mines on both boards (same configuration for fairness)
		board1.placeBombs(config.getTotalMines());
		board2.placeBombs(config.getTotalMines());

		// calculate numbers for cells adjacent to mines
		calculateNumbers(board1);
		calculateNumbers(board2);

		// place special cells (questions and surprises)
		board1.placeSpecials(config.getTotalQuestions(), config.getTotalSurprises());
		board2.placeSpecials(config.getTotalQuestions(), config.getTotalSurprises());
	}

	private void calculateNumbers(Board board) {
		for (int row = 0; row < board.getRows(); row++) {
			for (int col = 0; col < board.getColumns(); col++) {
				Cell cell = board.getCell(row, col);

				// Only calculate for non-mine cells
				if (cell.getCellType() != CellType.MINE) {
					int adjacentMines = countAdjacentMines(board, row, col);
					if (adjacentMines > 0 && cell.getCellType() == CellType.EMPTY) {
						cell.setCellType(CellType.NUMBER);
						cell.setNumber(adjacentMines);
					}
				}
			}
		}
	}

	private int countAdjacentMines(Board board, int row, int col) {
		int count = 0;

		// Check all 8 adjacent cells
		for (int dr = -1; dr <= 1; dr++) {
			for (int dc = -1; dc <= 1; dc++) {
				if (dr == 0 && dc == 0)
					continue; // Skip center cell
				int newRow = row + dr;
				int newCol = col + dc;
				Cell neighbor = board.getCell(newRow, newCol);
				if (neighbor != null && neighbor.getCellType() == CellType.MINE) {
					count++;
				}
			}
		}
		return count;
	}

	public void startGame() {
		gameStarted = true;
	}

	public void switchTurn() {
		currentPlayerIndex = (currentPlayerIndex == 1) ? 2 : 1;
	}

	// handles score
	public void modifyScore(int points) {
		score = Math.max(0, score + points);
	}

	// handles life
	public void modifyLives(int change) {
		lives = Math.max(0, lives + change);
	}

	public Player getPlayer1() {
		return player1;
	}

	public Player getPlayer2() {
		return player2;
	}

	public Board getBoard1() {
		return board1;
	}

	public Board getBoard2() {
		return board2;
	}

	public Board getCurrentBoard() {
		return currentPlayerIndex == 1 ? board1 : board2;
	}

	public GameConfig getConfig() {
		return config;
	}

	public boolean isGameStarted() {
		return gameStarted;
	}

	public int getCurrentPlayerIndex() {
		return currentPlayerIndex;
	}

	public int getScore() {
		return score;
	}

	public int getLives() {
		return lives;
	}

}
