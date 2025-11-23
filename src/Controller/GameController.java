package Controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Model.*;

public class GameController {
	private final Game game; //the game instance
	private Player currentPlayer;   //current player turn
	
	private LocalDateTime StartTime; //variable for game start time
	private LocalDateTime EndTime;   //variable for gameend time
	private final GameResultsController gameResultsController; //variable for handeling and saving game result
	private final SysData sysData; //Access to the questions database
	
	
	public GameController(String name1, String name2, Difficulty difficulty) {
		Player player1 = new Player(name1);
		Player player2 = new Player(name2);
		this.game= new Game(player1, player2, difficulty);
		this.gameResultsController = new GameResultsController();
		this.sysData = SysData.getInstance();
		
		//Start the game with player1 turn
		player1.setTurn(true);
		player2.setTurn(false);
		this.currentPlayer = game.getPlayer1();
	}
	
	//turn management between players
	public void switchTurn() {
		if(currentPlayer == game.getPlayer1()) {
			game.getPlayer1().setTurn(false);
			currentPlayer = game.getPlayer2();
			game.getPlayer2().setTurn(true);
		}
		else {
			currentPlayer.setTurn(false);
			currentPlayer = game.getPlayer1();
			currentPlayer.setTurn(true);
		}
	}
	
	public Player getCurrentPlayer() {
		return currentPlayer;
	}
	
	//------------------------------------ Revealing a cell-----------------------------------------------//
	//revealing cells considering each type of cells
	public void revealCell(int boardIndex, int row, int col) {

        Board board = game.getBoard(boardIndex);
        Cell cell = board.getCell(row, col);
        
        //cant reveal already revealed cell
        if (cell.isRevealed())
            return;
      //cant reveal already flagged cell
        if(cell.isFlagged())
        	return;
        
        cell.setRevealed(true);

        switch (cell.getCellType()) {

            case MINE -> handleMineCell();

            case EMPTY -> handleEmptyCell(board, row, col);

            case NUMBER -> handleNumberCell(cell);

            case QUESTION -> handleQuestionCell(cell);

            case SURPRISE -> handleSurpriseCell(cell);
        }

        checkGameEnd();
    }
	
	
	//handeling different kind of cells when revealing
	private void handleMineCell() {
        game.loseLife();
        checkGameEndLifes();
        switchTurn();
    }
	
	private void handleEmptyCell(Board board, int row, int col) {
		game.addScore(1);
		board.floodReveal(row,col);
		switchTurn();
	}
	
	private void handleNumberCell(Cell cell) {
		game.addScore(1);
		switchTurn();
	}
	
	private void handleQuestionCell(Cell cell) {
		if(cell.isUsed())
			return;
		
		switchTurn();
	}
	
	
	private void handleSurpriseCell(Cell cell) {
		if(cell.isUsed())
			return;
		
		switchTurn();
	}

	//------------------------------------ flagging a cell-----------------------------------------------//
	public void flagCell(int boardIndex, int row, int col) {

	    Cell cell = game.getBoard(boardIndex).getCell(row, col);

	    // cant flag revealed cell
	    if (cell.isRevealed())
	        return;

	    // cell is already falgged
	    if(cell.isFlagged())
	    	return;
	    //flag the cell
	    cell.setFlagged(true);
	    
	    handleFlaggedCell(cell);
	}
	
	private void handleFlaggedCell(Cell cell) {
		switch(cell.getCellType()) {
			case MINE -> {
				game.addScore(1);
				cell.setRevealed(true);
			}
			
			default -> {
				game.addScore(-3);
			}
		}
	}
	
	//------------------------------------ checks if a game ended-----------------------------------------------//
	public void checkGameEnd() {
		//checking if the players lost the game
		checkGameEndLifes();
		
		//checks if game ended by winning
		boolean allRevealed = true;
		
		for(Board board : game.getBoards()) {
			for(int r=0; r<board.getRows(); r++) {
				for(int c=0; c<board.getColumns(); c++) {
					
					Cell cell = board.getCell(r, c);
					
					if(cell.getCellType() != CellType.MINE && !cell.isRevealed()) {
						allRevealed = false;
						break;
					}
				}
				if(!allRevealed)
					break;
			}
		}
		if(allRevealed) {
			EndTime = LocalDateTime.now();
			saveGameResult();
			return;
		}
	}
	
	//check if game ends by losing all lifes (the players lost the game)
	public void checkGameEndLifes() {
		if(game.getLives()<=0) {
			EndTime = LocalDateTime.now();
			saveGameResult();
			return;
		}
	}
	
	//---------------------------------------activating cells-----------------------------------------//
	public Question activateQuestionCell(int boardIndex, int row, int col) {
		
		Cell cell = game.getBoard(boardIndex).getCell(row, col);
		
		if(cell.isUsed() || cell.getCellType()!=CellType.QUESTION)
			return null;
		
		cell.setUsed(true);
		if(game.getDifficulty() == Difficulty.EASY) {
			//cost of activation in easy game mode
			game.addScore(-5);
		}
		if(game.getDifficulty() == Difficulty.MEDIUM) {
			//cost of activation in medium game mode
			game.addScore(-8);
		}
		if(game.getDifficulty() == Difficulty.HARD) {
			//cost of activation in hard game mode
			game.addScore(-12);
		}
		
		Question q = sysData.getRandomQuestion();
		return q; 
	}
	
	//checks all the conditions of answering a question
	public void applyAnswer(Question question, String playerAnswer) {
		boolean correct = playerAnswer.equalsIgnoreCase(question.getCorrectAnswer());
		//according to the condition of the player's answer, he is granted the outcome
		if(game.getDifficulty() == Difficulty.EASY) {
			if(question.getDifficulty().equals("1")) {
				if(!correct) {
					double randomNum = Math.random();
					if(randomNum<0.5) {
						game.addScore(-3);
					}
				}
				if(correct) {
					game.addScore(3);
					game.addLifes(1);
				}
			}
			if(question.getDifficulty().equals("2")) {
				if(!correct) {
					double randomNum = Math.random();
					if(randomNum<0.5) {
						game.addScore(-6);
					}
				}
				if(correct) {
					game.addScore(6);
					//reveal a random bomb cell
					int boardIndex = getCurrentPlayerBoardIndex();
					revealRandomBomb(boardIndex);
				}
			}
			if(question.getDifficulty().equals("3")) {
				if(!correct) {
					game.addScore(-10);
				}
				if(correct) {
					game.addScore(10);
					//reveal 3x3 randomly here	
					int boardIndex = getCurrentPlayerBoardIndex();
					revealRandomArea3x3(boardIndex);
				}
			}
			if(question.getDifficulty().equals("4")) {
				if(!correct) {
					game.addScore(-15);
					game.addLifes(-1);
				}
				if(correct) {
					game.addScore(15);
					game.addLifes(2);
				}
			}
		}
		if(game.getDifficulty()== Difficulty.MEDIUM) {
			if(question.getDifficulty().equals("1")) {
				if(!correct) {					
					game.addScore(-8);
				}
				if(correct) {
					game.addScore(8);
					game.addLifes(1);
				}
			}
			if(question.getDifficulty().equals("2")) {
				if(!correct) {
					double randomNum = Math.random();
					if(randomNum<0.5) {
						game.addScore(-10);
						game.addLifes(-1);
					}
				}
				if(correct) {
					game.addScore(10);
					game.addLifes(1);
				}
			}
			if(question.getDifficulty().equals("3")) {
				if(!correct) {
					game.addScore(-15);
					game.addLifes(-1);
				}
				if(correct) {
					game.addScore(15);
					game.addLifes(1);
				}
			}
			if(question.getDifficulty().equals("4")) {
				if(!correct) {
					double randomNum = Math.random();
					if(randomNum<0.5) {
						game.addScore(-20);
						game.addLifes(-1);
					}
					if(randomNum>0.5) {
						game.addScore(-20);
						game.addLifes(-2);
					}
				}
				if(correct) {
					game.addScore(20);
					game.addLifes(2);
				}
			}
		}
		if(game.getDifficulty()== Difficulty.HARD) {
			if(question.getDifficulty().equals("1")) {
				if(!correct) {					
					game.addScore(-10);
					game.addLifes(-1);
				}
				if(correct) {
					game.addScore(10);
					game.addLifes(1);
				}
			}
			if(question.getDifficulty().equals("2")) {
				if(!correct) {
					double randomNum = Math.random();
					if(randomNum<0.5) {
						game.addScore(-15);
						game.addLifes(-1);
					}
					if(randomNum>0.5) {
						game.addScore(-15);
						game.addLifes(-2);
					}
				}
				if(correct) {
					double randomNum = Math.random();
					if(randomNum<0.5) {
						game.addScore(15);
						game.addLifes(1);
					}
					if(randomNum>0.5) {
						game.addScore(15);
						game.addLifes(2);
					}
				}
			}
			if(question.getDifficulty().equals("3")) {
				if(!correct) {
					game.addScore(-20);
					game.addLifes(-2);
				}
				if(correct) {
					game.addScore(20);
					game.addLifes(2);
				}
			}
			if(question.getDifficulty().equals("4")) {
				if(!correct) {
					game.addScore(-40);
					game.addLifes(-3);
				}
				if(correct) {
					game.addScore(40);
					game.addLifes(3);
				}
			}
		}
		checkGameEnd();
		switchTurn();
	}
	
	
	
	public void activateSurpriseCell(int boardIndex, int row, int col) {
		
		Cell cell = game.getBoard(boardIndex).getCell(row, col);
		
		if(cell.isUsed() || cell.getCellType()!=CellType.SURPRISE)
			return;
		
		cell.setUsed(true);
		double randomNum = Math.random();
		if(game.getDifficulty() == Difficulty.EASY) {
			//cost of activation in easy game mode
			game.addScore(-5);
			
			if(randomNum<0.5) {
				game.addScore(8);
				game.addLifes(1);
			}else {
				game.addScore(-8);
				game.addLifes(-1);
			}
		}
		if(game.getDifficulty() == Difficulty.MEDIUM) {
			//cost of activation in medium game mode
			game.addScore(-8);
			
			if(randomNum<0.5) {
				game.addScore(12);
				game.addLifes(1);
			}else {
				game.addScore(-12);
				game.addLifes(-1);
			}
		}
		if(game.getDifficulty() == Difficulty.HARD) {
			//cost of activation in hard game mode
			game.addScore(-12);
			
			if(randomNum<0.5) {
				game.addScore(16);
				game.addLifes(1);
			}else {
				game.addScore(-16);
				game.addLifes(-1);
			}
		}
	}
	
	//revealing random Bomb
	public void revealRandomBomb(int boardIndex) {

	    Board board = game.getBoard(boardIndex);
	    List<Cell> bombCells = new ArrayList<>();

	    // Collect all UNREVEALED bomb cells
	    for (int r = 0; r < board.getRows(); r++) {
	        for (int c = 0; c < board.getColumns(); c++) {
	            Cell cell = board.getCell(r, c);

	            if (cell.getCellType() == CellType.MINE && !cell.isRevealed()) {
	                bombCells.add(cell);
	            }
	        }
	    }

	    // No bombs left to reveal
	    if (bombCells.isEmpty()) {
	        return;
	    }

	    // Choose a random bomb
	    Random rand = new Random();
	    Cell chosen = bombCells.get(rand.nextInt(bombCells.size()));
	  

	    // Reveal the bomb by switching the boolean Reveal variable to true
	    chosen.setRevealed(true);
	}

	//function for revealing random 3x3 area
	public void revealRandomArea3x3(int boardIndex) {

	    Random rand = new Random();
	    // 1. Choose a random board
	    Board board = game.getBoard(boardIndex);

	    // 2. Choose a random center cell
	    int row = rand.nextInt(board.getRows());
	    int col = rand.nextInt(board.getColumns());

	    // 3. Loop through the 3x3 area centered on (row, col)
	    for (int r = row - 1; r <= row + 1; r++) {
	        for (int c = col - 1; c <= col + 1; c++) {
	        	
	            // Boundary check
	            if (r < 0 || r >= board.getRows() || c < 0 || c >= board.getColumns())
	                continue;

	            // Reveal using your normal reveal logic
	            Cell cell = board.getCell(r, c);
	            if(!cell.isRevealed()) {
	            	cell.setRevealed(true);
	            }
	        }
	    }
	}
	
	
	
	private int getCurrentPlayerBoardIndex() {
	    if (currentPlayer == game.getPlayer1()) {
	        return 0; // board of player 1
	    } else {
	        return 1; // board of player 2
	    }
	}

	
	//Saving the game results
	private void saveGameResult() {
		long gameDuration = java.time.Duration.between(StartTime, EndTime).getSeconds();
		GameSummary gameSummary = new GameSummary(game.getPlayer1().getName(),
												  game.getPlayer2().getName(),
												  game.getDifficulty().toString(),
												  String.valueOf(game.getScore()),
												  gameDuration,
												  StartTime,
												  EndTime);
		gameResultsController.addGameHistory(gameSummary);
        System.out.println("Game result saved successfully.");
	}
	
	

//	public void startGame() {
//		StartTime = LocalDateTime.now();
//		boolean gameOver = false;
//		
//	}
}
