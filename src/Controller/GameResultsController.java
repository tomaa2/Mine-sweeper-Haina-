package Controller;

import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

import Model.GameSummary;
import Model.SysData;

public class GameResultsController {

	private static final String RESULTS_FILE_PATH = "gamehistory.csv";

	// Time format: HH:MM:SS (for duration and times in CSV)
//	private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
	private static final DateTimeFormatter DATETIME_FORMATTER =
	        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

	private static final DateTimeFormatter TIME_ONLY_FORMATTER =
	        DateTimeFormatter.ofPattern("HH:mm:ss");
	public GameResultsController() {
	}

	/* ================== Helpers ================== */

	/**
	 * Format duration (in seconds) as HH:MM:SS string.
	 */
	private String formatDuration(long totalSeconds) {
		if (totalSeconds < 0) {
			totalSeconds = 0;
		}
		long hours = totalSeconds / 3600;
		long minutes = (totalSeconds % 3600) / 60;
		long seconds = totalSeconds % 60;
		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}

	/**
	 * Parse duration stored in CSV. Supports both "HH:MM:SS" and raw seconds (old
	 * files).
	 */
	private long parseDuration(String raw) {
		if (raw == null || raw.isEmpty()) {
			return 0;
		}
		try {
			if (raw.contains(":")) {
				// new format HH:MM:SS
				String[] parts = raw.split(":");
				long h = Long.parseLong(parts[0]);
				long m = Long.parseLong(parts[1]);
				long s = Long.parseLong(parts[2]);
				return h * 3600 + m * 60 + s;
			} else {
				// old format: seconds as number
				return Long.parseLong(raw);
			}
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * Parse time from CSV. Supports both LocalDateTime ISO string (old) and
	 * "HH:MM:SS" (new).
	 */
//	private LocalDateTime parseDateTimeOrTime(String raw) {
//		if (raw == null || raw.isEmpty()) {
//			return LocalDateTime.now();
//		}
//		try {
//			if (raw.contains("T")) {
//				// old format: full LocalDateTime
//				return LocalDateTime.parse(raw);
//			} else {
//				// new format: only time HH:MM:SS -> attach today's date
//				LocalTime t = LocalTime.parse(raw, DATETIME_FORMATTER);
//				return LocalDate.now().atTime(t);
//			}
//		} catch (Exception e) {
//			return LocalDateTime.now();
//		}
//	}


	private LocalDateTime parseDateTimeOrTime(String raw) {
	    if (raw == null || raw.isBlank()) return null;

	    raw = raw.trim();
	    try {
	        if (raw.contains("T")) {
	            // old format: ISO LocalDateTime (e.g., 2025-12-30T11:57:26)
	            return LocalDateTime.parse(raw);
	        }
	        if (raw.contains("/")) {
	            // new format: dd/MM/yyyy HH:mm:ss  (what you currently write)
	            return LocalDateTime.parse(raw, DATETIME_FORMATTER);
	        }
	        // very old format: HH:mm:ss only
	        LocalTime t = LocalTime.parse(raw, TIME_ONLY_FORMATTER);
	        return LocalDate.now().atTime(t);
	    } catch (Exception e) {
	        // better than "now": return null so you notice bad data
	        return null;
	    }
	}

	/* ================== Load game history ================== */

	public void loadGameHistory() {
		String filePath = RESULTS_FILE_PATH;
		List<GameSummary> loadedGames = new ArrayList<>();
		try (CSVReader reader = new CSVReaderBuilder(new FileReader(filePath))
	            .withCSVParser(new CSVParserBuilder()
	                .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
	                .withQuoteChar(CSVWriter.NO_QUOTE_CHARACTER)
	                .withEscapeChar(CSVWriter.NO_ESCAPE_CHARACTER)
	                .build())
	            .build()) {
			String[] line;
			reader.readNext(); // skip header row

			while ((line = reader.readNext()) != null) {

				if (line.length < 8) {
					continue; // skip malformed rows
				}

				String player1 = line[1];
				String player2 = line[2];
				String difficulty = line[3];
				String score = line[4];

				long durationSeconds = parseDuration(line[5]);
				LocalDateTime startTime = parseDateTimeOrTime(line[6]);
				LocalDateTime endTime = parseDateTimeOrTime(line[7]);
				if (startTime == null || endTime == null) {
				    continue; 
				}
				String gameResult = line.length > 8 ? line[8] : "";
				String screenshotPath = line.length > 9 ? line[9] : "";
	            System.out.println("Loaded screenshot path: " + screenshotPath);

				GameSummary g = new GameSummary(player1, player2, difficulty, score, durationSeconds, startTime,
						endTime, gameResult,screenshotPath);
				loadedGames.add(g);
			}

			SysData.getInstance().deleteAllGames();
			SysData.getInstance().addGames(loadedGames);
			System.out.println("Game history loaded successfully!");

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed to load games history! " + e.getMessage());
		}
	}

	/* ================== Save game history ================== */

	public void saveGameHistory() {
		String filePath = RESULTS_FILE_PATH;
		List<GameSummary> games = SysData.getInstance().getAllGames();

		try (CSVWriter writer = new CSVWriter(new FileWriter(filePath),
	            CSVWriter.DEFAULT_SEPARATOR,
	            CSVWriter.NO_QUOTE_CHARACTER,  // Don't quote fields
	            CSVWriter.NO_ESCAPE_CHARACTER, // Don't escape anything
	            CSVWriter.DEFAULT_LINE_END)) {

			
			
			// Header row – consistent format
			writer.writeNext(new String[] { "ID", "Player1", "Player2", "Difficulty", "Score", "Duration", "StartTime",
					"EndTime", "GameResult", "ScreenshotPath" });

			int id = 1;

			for (GameSummary g : games) {
				String durationStr = formatDuration(g.getDurationSeconds());
				String startTimeStr = g.getStartTime().format(DATETIME_FORMATTER);
				String endTimeStr = g.getEndTime().format(DATETIME_FORMATTER);

				String screenshotPath = g.getScreenshotPath();
	            if (screenshotPath == null) {
	                screenshotPath = ""; // Empty string for old games without screenshots
	            }
	            
				writer.writeNext(new String[] { String.valueOf(id++), g.getPlayer1(), g.getPlayer2(), g.getDifficulty(),
						g.getScore(), durationStr, startTimeStr, endTimeStr, g.getGameresult(),screenshotPath });
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed to save the games! " + e.getMessage());
		}
	}

	/* ================== Public API ================== */

	// Add a game result to the history
	public void addGameHistory(GameSummary summary) {
		loadGameHistory(); // load all the history's from csv file
		SysData.getInstance().addGame(summary); // add the current game history
		saveGameHistory();
		System.out.println("Game added to history successfully.");
	}

	// Display all games in console
	public void displayGameHistory() {
		loadGameHistory();
		List<GameSummary> games = SysData.getInstance().getAllGames();
		if (games.isEmpty()) {
			System.out.println("No game history available.");
		} else {
			System.out.println("\n=== Game History ===");
			games.forEach(System.out::println);
		}
	}

	// Reset CSV + in-memory games
	public void resetGameHistory() {
		String filePath = RESULTS_FILE_PATH;

		SysData.getInstance().deleteAllGames();
		SysData.getInstance().addGames(new ArrayList<>());

		try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
			writer.writeNext(new String[] { "ID", "Player1", "Player2", "Difficulty", "Score", "Duration", "StartTime",
					"EndTime" });

			System.out.println("Game history reset successfully.");

		} catch (Exception e) {
			System.out.println("Failed to reset game history: " + e.getMessage());
		}
	}
}
