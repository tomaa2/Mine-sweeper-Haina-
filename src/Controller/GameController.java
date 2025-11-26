package Controller;

import Model.Game;
import Model.GameConfig;
import Model.GameSummary;
import Model.Player;
import Model.Question;
import Model.SysData;
import Model.Board;
import Model.Cell;
import Model.CellType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameController {
	private final Game game; // the game instance
	private Player currentPlayer; // current player turn
	private LocalDateTime StartTime; // variable for game start time
	private LocalDateTime EndTime; // variable for gameend time
	private final GameResultsController gameResultsController; // variable for handeling and saving game result
	private final SysData sysData; // Access to the questions database

	public GameController(String name1, String name2, GameConfig config) {
		Player player1 = new Player(name1);
		Player player2 = new Player(name2);
		this.game = new Game(player1, player2, config);
		this.gameResultsController = new GameResultsController();
		this.sysData = SysData.getInstance();

		// Start the game with player1 turn
		player1.setTurn(true);
		player2.setTurn(false);
		this.currentPlayer = game.getPlayer1();
	}

	// switch turn between players
	public void switchTurn() {
		if (currentPlayer == game.getPlayer1()) {
			game.getPlayer1().setTurn(false);
			game.getPlayer2().setTurn(true);
			currentPlayer = game.getPlayer2();
		} else {
			game.getPlayer2().setTurn(false);
			game.getPlayer1().setTurn(true);
			currentPlayer = game.getPlayer1();
		}
		game.switchTurn();
	}

	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	public Game getGame() {
		return game;
	}

	// helper to get the board for a given player index (1 or 2)
	private Board getBoardByIndex(int playerIndex) {
		return (playerIndex == 1) ? game.getBoard1() : game.getBoard2();
	}

	// get current player's board index
	private int getCurrentPlayerBoardIndex() {
		return (currentPlayer == game.getPlayer1()) ? 1 : 2;
	}

	// ------------------------------------ Revealing a
	// cell-----------------------------------------------//
	// revealing cells considering each type of cells
	public void revealCell(int playerIndex, int row, int col) {
		Board board = getBoardByIndex(playerIndex);
		Cell cell = board.getCell(row, col);

		if (cell == null || cell.isRevealed() || cell.isFlagged()) {
			return;
		}

		cell.setRevealed(true);

		switch (cell.getCellType()) {
		case MINE -> handleMineCell();
		case EMPTY -> handleEmptyCell(board, row, col);
		case NUMBER -> handleNumberCell();
		case QUESTION -> handleQuestionCell();
		case SURPRISE -> handleSurpriseCell();
		}

		checkGameEnd();
	}

	private void handleMineCell() {
		game.modifyLives(-1);
		checkGameEndByLives();
		switchTurn();
	}

	private void handleEmptyCell(Board board, int row, int col) {
		game.modifyScore(1);
		board.floodReveal(row, col);
		switchTurn();
	}

	private void handleNumberCell() {
		game.modifyScore(1);
		switchTurn();
	}

	private void handleQuestionCell() {
		// revealing question cell just reveals it, activation is separate
		// player can activate it later for a cost
		switchTurn();
	}

	private void handleSurpriseCell() {
		// revealing surprise cell just reveals it, activation is separate
		switchTurn();
	}

	// ------------------------------------ flagging a
	// cell-----------------------------------------------//
	public void flagCell(int playerIndex, int row, int col) {
		Board board = getBoardByIndex(playerIndex);
		Cell cell = board.getCell(row, col);

		if (cell == null || cell.isRevealed() || cell.isFlagged()) {
			return;
		}

		cell.setFlagged(true);

		if (cell.getCellType() == CellType.MINE) {
			// correctly flagged a mine
			game.modifyScore(1);
			cell.setRevealed(true);
		} else {
			// wrongly flagged a non-mine cell
			game.modifyScore(-3);
		}
	}

	// unflag a cell if needed
	public void unflagCell(int playerIndex, int row, int col) {
		Board board = getBoardByIndex(playerIndex);
		Cell cell = board.getCell(row, col);

		if (cell != null && cell.isFlagged() && !cell.isRevealed()) {
			cell.setFlagged(false);
		}
	}

	// ------------------------------------ checks if a game
	// ended-----------------------------------------------//
	public boolean checkGameEnd() {
		//check if lost all lives
		if (checkGameEndByLives()) {
			return true;
		}

		//check if all non mine cells are revealed on at least one board
		if (checkBoardCleared(game.getBoard1()) || checkBoardCleared(game.getBoard2())) {
			endGame();
			return true;
		}

		return false;
	}

	private boolean checkBoardCleared(Board board) {
		for (int r = 0; r < board.getRows(); r++) {
			for (int c = 0; c < board.getColumns(); c++) {
				Cell cell = board.getCell(r, c);
				//if theres a nonmine cell that isnt revealed, board is not cleared
				if (cell.getCellType() != CellType.MINE && !cell.isRevealed()) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean checkGameEndByLives() {
		if (game.getLives() <= 0) {
			endGame();
			return true;
		}
		return false;
	}

	private void endGame() {
		EndTime = LocalDateTime.now();

		//convert remaining lives to points
		int remainingLives = game.getLives();
		int activationCost = game.getConfig().getActivationCost();
		int bonusPoints = remainingLives * activationCost;
		game.modifyScore(bonusPoints);

		// reveal all cells on both boards
		revealAllCells(game.getBoard1());
		revealAllCells(game.getBoard2());

		saveGameResult();
	}

	private void revealAllCells(Board board) {
		for (int r = 0; r < board.getRows(); r++) {
			for (int c = 0; c < board.getColumns(); c++) {
				board.getCell(r, c).setRevealed(true);
			}
		}
	}

	// ---------------------------------------activating
	// cells-----------------------------------------//
	public Question activateQuestionCell(int playerIndex, int row, int col) {
		Board board = getBoardByIndex(playerIndex);
		Cell cell = board.getCell(row, col);
		
		if (cell == null || !cell.isRevealed() || cell.isUsed() || cell.getCellType() != CellType.QUESTION) {
			return null;
		}
		
		cell.setUsed(true);
		
		// pay the activation cost
		int activationCost = game.getConfig().getActivationCost();
		game.modifyScore(-activationCost);
		
		return sysData.getRandomQuestion();
	}
	
	public void applyAnswer(Question question, String playerAnswer) {
		boolean correct = playerAnswer.equalsIgnoreCase(question.getCorrectAnswer());
		GameConfig config = game.getConfig();
		String qLevel = question.getDifficulty();
		Random rand = new Random();
		
		// determine game difficulty level
		int gameLevel = getGameDifficultyLevel(config);
		
		// apply rewards/penalties based on game difficulty and question level
		applyQuestionOutcome(gameLevel, qLevel, correct, rand);
		
		checkGameEnd();
		switchTurn();
	}
	
	private int getGameDifficultyLevel(GameConfig config) {
		// Easy = 1, Medium = 2, Hard = 3
		return switch (config) {
			case EASY -> 1;
			case MEDIUM -> 2;
			case HARD -> 3;
		};
	}
	private void applyQuestionOutcome(int gameLevel, String qLevel, boolean correct, Random rand) {
		// based on the PDF table for question outcomes
		switch (gameLevel) {
			case 1 -> applyEasyGameOutcome(qLevel, correct, rand);
			case 2 -> applyMediumGameOutcome(qLevel, correct, rand);
			case 3 -> applyHardGameOutcome(qLevel, correct, rand);
		}
	}
	
	private void applyEasyGameOutcome(String qLevel, boolean correct, Random rand) {
		switch (qLevel) {
			case "1" -> { // easy question
				if (correct) {
					game.modifyScore(3);
					addLifeWithOverflowCheck(1);
				} else {
					// 50% chance to lose 3 pts, 50% nothing
					if (rand.nextDouble() < 0.5) {
						game.modifyScore(-3);
					}
				}
			}
			case "2" -> { // medium question
				if (correct) {
					game.modifyScore(6);
					revealRandomBomb(getCurrentPlayerBoardIndex());
				} else {
					if (rand.nextDouble() < 0.5) {
						game.modifyScore(-6);
					}
				}
			}
			case "3" -> { // hard question
				if (correct) {
					game.modifyScore(10);
					revealRandomArea3x3(getCurrentPlayerBoardIndex());
				} else {
					game.modifyScore(-10);
				}
			}
			case "4" -> { // expert question
				if (correct) {
					game.modifyScore(15);
					addLifeWithOverflowCheck(2);
				} else {
					game.modifyScore(-15);
					game.modifyLives(-1);
				}
			}
		}
	}
	
	private void applyMediumGameOutcome(String qLevel, boolean correct, Random rand) {
		switch (qLevel) {
			case "1" -> {
				if (correct) {
					game.modifyScore(8);
					addLifeWithOverflowCheck(1);
				} else {
					game.modifyScore(-8);
				}
			}
			case "2" -> {
				if (correct) {
					game.modifyScore(10);
					addLifeWithOverflowCheck(1);
				} else {
					if (rand.nextDouble() < 0.5) {
						game.modifyScore(-10);
						game.modifyLives(-1);
					}
					// else nothing happens
				}
			}
			case "3" -> {
				if (correct) {
					game.modifyScore(15);
					addLifeWithOverflowCheck(1);
				} else {
					game.modifyScore(-15);
					game.modifyLives(-1);
				}
			}
			case "4" -> {
				if (correct) {
					game.modifyScore(20);
					addLifeWithOverflowCheck(2);
				} else {
					if (rand.nextDouble() < 0.5) {
						game.modifyScore(-20);
						game.modifyLives(-1);
					} else {
						game.modifyScore(-20);
						game.modifyLives(-2);
					}
				}
			}
		}
	}

	private void applyHardGameOutcome(String qLevel, boolean correct, Random rand) {
		switch (qLevel) {
			case "1" -> {
				if (correct) {
					game.modifyScore(10);
					addLifeWithOverflowCheck(1);
				} else {
					game.modifyScore(-10);
					game.modifyLives(-1);
				}
			}
			case "2" -> {
				if (correct) {
					if (rand.nextDouble() < 0.5) {
						game.modifyScore(15);
						addLifeWithOverflowCheck(1);
					} else {
						game.modifyScore(15);
						addLifeWithOverflowCheck(2);
					}
				} else {
					if (rand.nextDouble() < 0.5) {
						game.modifyScore(-15);
						game.modifyLives(-1);
					} else {
						game.modifyScore(-15);
						game.modifyLives(-2);
					}
				}
			}
			case "3" -> {
				if (correct) {
					game.modifyScore(20);
					addLifeWithOverflowCheck(2);
				} else {
					game.modifyScore(-20);
					game.modifyLives(-2);
				}
			}
			case "4" -> {
				if (correct) {
					game.modifyScore(40);
					addLifeWithOverflowCheck(3);
				} else {
					game.modifyScore(-40);
					game.modifyLives(-3);
				}
			}
		}
	}

	// handles the max 10 lives rule - converts overflow to points
	private void addLifeWithOverflowCheck(int livesToAdd) {
		int currentLives = game.getLives();
		int maxLives = 10;
		int activationCost = game.getConfig().getActivationCost();
		
		int newTotal = currentLives + livesToAdd;
		
		if (newTotal > maxLives) {
			int overflow = newTotal - maxLives;
			game.modifyLives(maxLives - currentLives); // cap at 10
			game.modifyScore(overflow * activationCost); // convert extra lives to points
		} else {
			game.modifyLives(livesToAdd);
		}
	}
	

	public void activateSurpriseCell(int playerIndex, int row, int col) {
		Board board = getBoardByIndex(playerIndex);
		Cell cell = board.getCell(row, col);
		
		if (cell == null || !cell.isRevealed() || cell.isUsed() || cell.getCellType() != CellType.SURPRISE) {
			return;
		}
		
		cell.setUsed(true);
		
		GameConfig config = game.getConfig();
		int activationCost = config.getActivationCost();
		int bonus = config.getSurpriseBonus();
		int penalty = config.getSurprisePenalty();
		
		// pay activation cost
		game.modifyScore(-activationCost);
		
		// 50-50 good or bad surprise
		Random rand = new Random();
		if (rand.nextDouble() < 0.5) {
			// good surprise
			game.modifyScore(bonus);
			addLifeWithOverflowCheck(1);
		} else {
			// bad surprise
			game.modifyScore(penalty); // penalty is already negative in config
			game.modifyLives(-1);
		}
		
		checkGameEnd();
	}
	
	//////////////////	/more actions
	
	public void revealRandomBomb(int playerIndex) {
		Board board = getBoardByIndex(playerIndex);
		List<Cell> unrevealedMines = new ArrayList<>();
		
		for (int r = 0; r < board.getRows(); r++) {
			for (int c = 0; c < board.getColumns(); c++) {
				Cell cell = board.getCell(r, c);
				if (cell.getCellType() == CellType.MINE && !cell.isRevealed()) {
					unrevealedMines.add(cell);
				}
			}
		}
		
		if (unrevealedMines.isEmpty()) {
			return;
		}
		
		Random rand = new Random();
		Cell chosen = unrevealedMines.get(rand.nextInt(unrevealedMines.size()));
		chosen.setRevealed(true);
		// note: no points awarded for this reveal per PDF rules
	}
	
	public void revealRandomArea3x3(int playerIndex) {
		Board board = getBoardByIndex(playerIndex);
		Random rand = new Random();
		
		// pick a random center point
		int centerRow = rand.nextInt(board.getRows());
		int centerCol = rand.nextInt(board.getColumns());
		
		// reveal 3x3 area around center
		for (int r = centerRow - 1; r <= centerRow + 1; r++) {
			for (int c = centerCol - 1; c <= centerCol + 1; c++) {
				if (r >= 0 && r < board.getRows() && c >= 0 && c < board.getColumns()) {
					Cell cell = board.getCell(r, c);
					if (!cell.isRevealed()) {
						cell.setRevealed(true);
					}
				}
			}
		}
	}
	//------------------------------------ Save Results -------------------------------------------------//
	private void saveGameResult() {
		if (StartTime == null) {
			StartTime = LocalDateTime.now();
		}
		if (EndTime == null) {
			EndTime = LocalDateTime.now();
		}
		
		long durationSecs = Duration.between(StartTime, EndTime).getSeconds();
		
		String difficultyName = game.getConfig().name();
		
		GameSummary summary = new GameSummary(
			game.getPlayer1().getName(),
			game.getPlayer2().getName(),
			difficultyName,
			String.valueOf(game.getScore()),
			durationSecs,
			StartTime,
			EndTime
		);
		
		gameResultsController.addGameHistory(summary);
		System.out.println("Game result saved successfully.");
	}
	
	////////////////getters for view
	public int getScore() {
		return game.getScore();
	}
	
	public int getLives() {
		return game.getLives();
	}
	
	public Board getBoard1() {
		return game.getBoard1();
	}
	
	public Board getBoard2() {
		return game.getBoard2();
	}
	
	public GameConfig getConfig() {
		return game.getConfig();
	}

//	public void startGame() {
//		StartTime = LocalDateTime.now();
//		boolean gameOver = false;
//		
//	}
}
