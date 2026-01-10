package Controller;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

import java.io.FileInputStream;
import Model.GameSummary;
import Model.SysData;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controller for the Game History screen.
 * Loads game history from CSV, updates summary statistics,
 * and displays recent games dynamically in the UI.
 */
public class GameHistoryController {

    // Top bar
    @FXML
    private Label backLabel;

    @FXML
    private Button clearHistoryButton;

    // Summary cards
    @FXML
    private Label totalGamesValueLabel;

    @FXML
    private Label victoriesValueLabel;

    @FXML
    private Label winRateValueLabel;

    @FXML
    private Label bestTimeValueLabel;

    // Container for dynamically created game rows
    @FXML
    private VBox gamesListContainer;

    private final GameResultsController resultsController = new GameResultsController();

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss");

    /**
     * Called automatically when the screen loads.
     * Initializes UI event handlers, loads game history,
     * updates summary statistics, and builds the recent games list.
     */
    @FXML
    private void initialize() {
        // Back to menu button
        backLabel.setOnMouseClicked(this::handleBackToMenu);

        // Clear history button
        clearHistoryButton.setOnAction(e -> handleClearHistory());

        // 1. Load history from CSV into SysData
        resultsController.loadGameHistory();

        // 2. Get all loaded games
        List<GameSummary> games = SysData.getInstance().getAllGames();

        // 3. Update the top summary cards
        updateSummaryCards(games);

        // 4. Fill the scroll list with game rows
        populateGamesList(games);
    }

    /* ========= SUMMARY CARDS ========= */

    /**
     * Updates total games, victory count, win rate,
     * and best time from the loaded game history.
     */
    private void updateSummaryCards(List<GameSummary> games) {
        int total = games.size();
        totalGamesValueLabel.setText(String.valueOf(total));

        // Count victories — assumed: positive score = win
//        long victories = games.stream()
//                .filter(g -> {
//                    try {
//                        return Integer.parseInt(g.getScore()) > 0;
//                    } catch (Exception e) {
//                        return false;
//                    }
//                })
//                .count();
//
//        victoriesValueLabel.setText(String.valueOf(victories));
     // Count victories — based on GameResult (Victory/Defeat)
        long victories = games.stream()
                .filter(g -> {
                    String r = g.getGameresult();
                    return r != null && r.trim().equalsIgnoreCase("Victory");
                })
                .count();

        victoriesValueLabel.setText(String.valueOf(victories));

        String winRateStr = total == 0
                ? "0.0%"
                : String.format("%.1f%%", (victories * 100.0) / total);
        winRateValueLabel.setText(winRateStr);

        // Best time = shortest duration in seconds
        if (total == 0) {
            bestTimeValueLabel.setText("-");
        } else {
            long bestSeconds = games.stream()
                    .mapToLong(GameSummary::getDurationSeconds)
                    .filter(s -> s > 0)
                    .min()
                    .orElse(0);

            if (bestSeconds <= 0) {
                bestTimeValueLabel.setText("-");
            } else {
                bestTimeValueLabel.setText(formatDuration(bestSeconds));
            }
        }
    }

    /**
     * Converts seconds into "HH:mm:ss" or "mm:ss" format.
     */
    private String formatDuration(long totalSeconds) {
        long h = totalSeconds / 3600;
        long m = (totalSeconds % 3600) / 60;
        long s = totalSeconds % 60;

        if (h > 0) {
            return String.format("%02d:%02d:%02d", h, m, s);
        } else {
            return String.format("%02d:%02d", m, s);
        }
    }

    /* ========= RECENT GAMES LIST ========= */

    /**
     * Sorts the game list and creates a UI row for each game.
     */
    private void populateGamesList(List<GameSummary> games) {
        gamesListContainer.getChildren().clear();

        // Create mutable copy (SysData may return unmodifiable list)
        List<GameSummary> sortedGames = new ArrayList<>(games);

        // Sort by start time (newest first)
        sortedGames.sort(
                Comparator.comparing(GameSummary::getStartTime)
                          .reversed()
        );

        // Build rows
        for (GameSummary g : sortedGames) {
            HBox row = buildGameRow(g);
            gamesListContainer.getChildren().add(row);
        }

        // If no games exist
        if (sortedGames.isEmpty()) {
            Label emptyLabel = new Label("No games played yet.");
            emptyLabel.setStyle("-fx-text-fill: #9ba3c7;");
            gamesListContainer.getChildren().add(emptyLabel);
        }
    }

    /**
     * Creates an HBox representing a single game entry in the history list.
     */
    private HBox buildGameRow(GameSummary game) {
        HBox row = new HBox(10);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        row.setPrefHeight(60);
        row.setStyle("-fx-background-color: #151b2b; -fx-background-radius: 12;");
        row.setPadding(new Insets(10, 16, 10, 16));

        // Optional icon
        ImageView icon = new ImageView();
        icon.setFitHeight(24);
        icon.setFitWidth(24);
        icon.setPreserveRatio(true);

        try {
            Image img = new Image(getClass().getResourceAsStream("/Images/bomb.png"));
            icon.setImage(img);
        } catch (Exception ignored) {}

        // Determine result: Victory / Defeat
        System.out.println("Game result: " + game.getGameresult());
//        String resultText = game.getGameresult().equalsIgnoreCase("Victory")
//				? "Victory 🏆"
//				: "Defeat 💣";
        String resultText;

        if (game.getGameresult().equalsIgnoreCase("Quit")) {
            resultText = "Quit 🚪";
        } else if (game.getGameresult().equalsIgnoreCase("Victory")) {
            resultText = "Victory 🏆";
        } else {
            resultText = "Defeat 💣";
        }

        Label resultLabel = new Label(resultText);
        resultLabel.setStyle("-fx-text-fill: #FFFFFF;");
        resultLabel.setFont(new javafx.scene.text.Font(14));

        // Date and time
        String dateTimeStr = game.getStartTime().format(DATE_TIME_FORMATTER);
        Label dateLabel = new Label(dateTimeStr);
        dateLabel.setStyle("-fx-text-fill: #9ba3c7;");

        VBox leftTextBox = new VBox(2, resultLabel, dateLabel);

        // Spacer pushes right elements to the far right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Difficulty label
        Label difficultyLabel = new Label(game.getDifficulty());
        difficultyLabel.setStyle(
                "-fx-text-fill: #9ba3c7; -fx-padding: 4 8 4 8; " +
                "-fx-background-color: #222b44; -fx-background-radius: 10;");
        difficultyLabel.setFont(new javafx.scene.text.Font(12));

        // Duration label
        String durationStr = formatDuration(game.getDurationSeconds());
        Label durationLabel = new Label(durationStr);
        durationLabel.setStyle("-fx-text-fill: #9ba3c7;");
        durationLabel.setFont(new javafx.scene.text.Font(12));

        Button viewButton = new Button("View Game");
        viewButton.setStyle(
                "-fx-background-color: #3c465a; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 6; " +
                "-fx-padding: 6 12 6 12; " +
                "-fx-cursor: hand; " +
                "-fx-font-size: 12px; " +
                "-fx-font-weight: bold;"
        );

        // Hover effect
        viewButton.setOnMouseEntered(e ->
                viewButton.setStyle(
                        "-fx-background-color: #505a78; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 6; " +
                        "-fx-padding: 6 12 6 12; " +
                        "-fx-cursor: hand; " +
                        "-fx-font-size: 12px; " +
                        "-fx-font-weight: bold;"
                )
        );

        viewButton.setOnMouseExited(e ->
                viewButton.setStyle(
                        "-fx-background-color: #3c465a; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 6; " +
                        "-fx-padding: 6 12 6 12; " +
                        "-fx-cursor: hand; " +
                        "-fx-font-size: 12px; " +
                        "-fx-font-weight: bold;"
                )
        );

        // Click handler - show screenshot
        viewButton.setOnAction(e -> showGameScreenshot(game));
        
        HBox rightBox = new HBox(10, difficultyLabel, durationLabel, viewButton);
        rightBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        row.getChildren().addAll(icon, leftTextBox, spacer, rightBox);
        return row;
    }
    
    
    // show screenshot
    //Opens a modal window displaying the game screenshot
    private void showGameScreenshot(GameSummary game) {
        try {
            String screenshotPath = game.getScreenshotPath();

            // Validate screenshot path
            if (screenshotPath == null || screenshotPath.isEmpty()) {
                showAlert("Screenshot Not Available", 
                         "No screenshot was saved for this game.");
                return;
            }
            System.out.println("=== DEBUG showGameScreenshot ===");
            System.out.println("Raw path from CSV: " + screenshotPath);

            File screenshotFile = new File(screenshotPath);
            
            System.out.println("Absolute path: " + screenshotFile.getAbsolutePath());
            System.out.println("File exists: " + screenshotFile.exists());
            System.out.println("Is file: " + screenshotFile.isFile());
            System.out.println("Can read: " + screenshotFile.canRead());
            
            if (!screenshotFile.exists()) {
                showAlert("Screenshot Not Found",
                         "Screenshot file not found:\n" + screenshotPath);
                return;
            }

            // Create new window for screenshot
            Stage screenshotStage = new Stage();
            screenshotStage.setTitle("Game Screenshot - " + 
                    game.getStartTime().format(DATE_TIME_FORMATTER));
            screenshotStage.initModality(Modality.APPLICATION_MODAL);

            // Load image
            Image screenshot = new Image(new FileInputStream(screenshotFile));
            ImageView imageView = new ImageView(screenshot);
            imageView.setPreserveRatio(true);

            // Create info panel at the top
            VBox infoPanel = new VBox(10);
            infoPanel.setStyle("-fx-background-color: #1b2235; -fx-padding: 15;");
            infoPanel.setAlignment(Pos.CENTER_LEFT);

            Label infoLabel = new Label(String.format(
                "Players: %s vs %s | Result: %s | Difficulty: %s | Time: %s | Score: %s",
                game.getPlayer1(),
                game.getPlayer2(),
                game.getGameresult(),
                game.getDifficulty(),
                formatDuration(game.getDurationSeconds()),
                game.getScore()
            ));
            infoLabel.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 14px;");
            infoPanel.getChildren().add(infoLabel);

            // Add to scroll pane
            ScrollPane scrollPane = new ScrollPane(imageView);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            scrollPane.setStyle("-fx-background: #060811;");

            // Layout
            VBox layout = new VBox();
            layout.getChildren().addAll(infoPanel, scrollPane);
            VBox.setVgrow(scrollPane, Priority.ALWAYS);

            Scene scene = new Scene(layout, 1200, 750);
            screenshotStage.setScene(scene);
            screenshotStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Error loading screenshot:\n" + e.getMessage());
        }
    }
    
    
    //Shows an alert dialog
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /* ========= CLEAR HISTORY ========= */

    /**
     * Clears the CSV file and UI content for history.
     */
    private void handleClearHistory() {
        resultsController.resetGameHistory();
        gamesListContainer.getChildren().clear();

        totalGamesValueLabel.setText("0");
        victoriesValueLabel.setText("0");
        winRateValueLabel.setText("0.0%");
        bestTimeValueLabel.setText("-");
    }

    /* ========= NAVIGATION ========= */

    /**
     * Returns user to the main menu screen.
     */
    private void handleBackToMenu(MouseEvent event) {
        switchScene((Node) event.getSource(), "/View/MainWindow.fxml");
    }

    /**
     * Switches between FXML scenes.
     */
    private void switchScene(Node source, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) source.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
