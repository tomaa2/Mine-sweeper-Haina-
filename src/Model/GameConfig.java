package Model;
//enum holding all game configurations for different difficulty levels

public enum GameConfig {

	EASY(9, 10, 6, 2, 10, 5, 8, -8), MEDIUM(13, 26, 7, 3, 8, 8, 12, -12), HARD(16, 44, 11, 4, 6, 12, 16, -16);

	private final int gridSize;
	private final int totalMines;
	private final int totalQuestions;
	private final int totalSurprises;
	private final int startingLives;
	private final int activationCost;
	private final int surpriseBonus;
	private final int surprisePenalty;

	GameConfig(int gridSize, int totalMines, int totalQuestions, int totalSurprises, int startingLives,
			int activationCost, int surpriseBonus, int surprisePenalty) {
		this.gridSize = gridSize;
		this.totalMines = totalMines;
		this.totalQuestions = totalQuestions;
		this.totalSurprises = totalSurprises;
		this.startingLives = startingLives;
		this.activationCost = activationCost;
		this.surpriseBonus = surpriseBonus;
		this.surprisePenalty = surprisePenalty;
	}

	
	public int getGridSize() {
		return gridSize;
	}

	public int getTotalMines() {
		return totalMines;
	}

	public int getTotalQuestions() {
		return totalQuestions;
	}

	public int getTotalSurprises() {
		return totalSurprises;
	}

	public int getStartingLives() {
		return startingLives;
	}

	public int getActivationCost() {
		return activationCost;
	}

	public int getSurpriseBonus() {
		return surpriseBonus;
	}

	public int getSurprisePenalty() {
		return surprisePenalty;
	}
}
