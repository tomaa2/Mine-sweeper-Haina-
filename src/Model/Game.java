package Model;

import java.util.Random;

public class Game {
	private final Player player1;
	private final Player player2;
	private Board[] boards = new Board[2];
	//private Board board2;
	private final Difficulty difficulty;
	private int score;
	private int lives;
	
	
	public Game(Player player1, Player player2, Difficulty difficulty) {
		this.player1 = player1;
		this.player2 = player2;
		this.difficulty = difficulty;
		this.score = 0;
		initializeGame();
		initializeBombs();
		initializeSpecialCells();
	}
	
	
	//initialize the game with boards' size according to difficulty
	private void initializeGame() {
		int size = switch (difficulty) {
        case EASY -> 9;
        case MEDIUM -> 13;
        case HARD -> 16;
		};
		
    	int livesNum = switch (difficulty) {
    	case EASY -> 10;
        case MEDIUM -> 8;
        case HARD -> 6;
    	};
    	
    this.boards[0] = new Board(size, size);
    this.boards[1] = new Board(size, size);
    this.lives = livesNum;
	}
	

	//initializing and spreading bombs across the two boards according to difficulty
	private void initializeBombs() {
		int numOfBombs=0;
		
		switch(difficulty) {
			case EASY:
				numOfBombs=10;
				break;
			case MEDIUM:
				numOfBombs=26;
				break;
			case HARD:
				numOfBombs=44;
				break;
			default:
                throw new IllegalStateException("Unknown difficulty: " + difficulty);
		}
		this.boards[0].placeBombs(numOfBombs);
		this.boards[1].placeBombs(numOfBombs);
	}
	
	private void initializeSpecialCells() {
		int numOfQues = 0;
		int numOfSur = 0;
		switch(difficulty) {
			case EASY:
				numOfQues = 6;
				numOfSur = 2;
				break;
			case MEDIUM:
				numOfQues = 7;
				numOfSur = 3;
				break;
			case HARD:
				numOfQues = 11;
				numOfSur = 4;
				break;
		}
		this.boards[0].placeSpecials(numOfQues, numOfSur);
		this.boards[1].placeSpecials(numOfQues, numOfSur);
	}
	
	
	public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }
    
    public Board[] getBoards() {
    	return boards;
    }
    
    //get one of the board
    public Board getBoard(int index) {
    	if(index>1 || index<0)
    		System.out.println("ERROR!!!!!!!!! the index should be 0 or 1 only!!!");
    	return boards[index];
    }
   
    
//    public Board getBoard2() {
//    	return board2;
//    }

    public Difficulty getDifficulty() {
        return difficulty;
    }
    
    
    
    public int getScore() {
    	return score;
    }
    
    public int getLives() {
    	return lives;
    }
    
    //add score
    public void addScore(int points) {
    	this.score+=points;
    }
    
    public void addLifes(int life) {
    	this.lives+= life;
    }
    //lose life
    public void loseLife() {
    	this.lives--;
    }
    
    
}
