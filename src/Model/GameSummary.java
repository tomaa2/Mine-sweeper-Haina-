package Model;


import java.time.*;

public class GameSummary {
	private String player1;
	private String player2;
	private String difficulty;
	private String Score;
	private long durationSeconds;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private String gameResult;
	private String screenshotPath;
	
	
	public GameSummary() {
		
	}


	public GameSummary(String player1, String player2, String difficulty, String score, long durationSeconds,LocalDateTime startTime,
			LocalDateTime endTime, String gameResult,String screenshotPath) {
		super();
		this.player1 = player1;
		this.player2 = player2;
		this.difficulty = difficulty;
		Score = score;
		this.durationSeconds = durationSeconds;
		this.startTime = startTime;
		this.endTime = endTime;
		this.gameResult = gameResult;
		this.screenshotPath = screenshotPath;
	}


	public String getPlayer1() {
		return player1;
	}
	public String getGameresult() {
		return gameResult;
	}

	public void setPlayer1(String player1) {
		this.player1 = player1;
	}


	public String getPlayer2() {
		return player2;
	}


	public void setPlayer2(String player2) {
		this.player2 = player2;
	}


	public String getDifficulty() {
		return difficulty;
	}


	public void setDifficulty(String difficulty) {
		this.difficulty = difficulty;
	}


	public String getScore() {
		return Score;
	}


	public void setScore(String score) {
		Score = score;
	}


	public long getDurationSeconds() {
		return durationSeconds;
	}
	
	public String getScreenshotPath() { return screenshotPath; }
	
	public void setScreenshotPath(String screenshotPath) {
        this.screenshotPath = screenshotPath;
    }


	public void setDurationSeconds(long durationSeconds) {
		this.durationSeconds = durationSeconds;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}


	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}
	
	public void setgameresult(String gameresult) {
		this.gameResult = gameresult;
	}

	@Override
	public String toString() {
		return "GameSummary [player1=" + player1 + ", player2=" + player2 + ", difficulty=" + difficulty + ", Score="
				+ Score + ", durationSeconds=" + durationSeconds + ", endTime=" + endTime + ", gameResult=" + gameResult + "]";
	}
	
	
}
