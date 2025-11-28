package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class GameOverScreenController {

    @FXML private VBox root;

    @FXML private Label titleLabel;
    @FXML private Label playersLabel;
    @FXML private Label difficultyLabel;
    @FXML private Label scoreLabel;
    @FXML private Label livesLabel;
    @FXML private Button backToMenuButton;

    private String player1Name;
    private String player2Name;
    private String difficulty;
    private int finalScore;
    private int remainingLives;

    /**
     * Initializes all game-over data.
     * Called from GameScreenController after the FXML is loaded.
     *
     * @param p1 Player 1 name
     * @param p2 Player 2 name
     * @param difficulty The selected difficulty level
     * @param finalScore The final score achieved by the players
     * @param remainingLives Number of lives left at the end of the game
     */
    public void initData(String p1, String p2, String difficulty,
                         int finalScore, int remainingLives) {

        this.player1Name = p1;
        this.player2Name = p2;
        this.difficulty = difficulty;
        this.finalScore = finalScore;
        this.remainingLives = remainingLives;

        updateUI();
    }

    /**
     * Called automatically when FXML is loaded.
     * Sets up the "Back to Menu" button action.
     */
    @FXML
    private void initialize() {
        backToMenuButton.setOnAction(e -> handleBackToMenu((Node) e.getSource()));
    }

    /**
     * Updates all UI elements based on game results.
     * Displays win/lose status, score, difficulty, players, and styling.
     */
    private void updateUI() {
        boolean win = remainingLives > 0;

        // Title based on result
        titleLabel.setText(win ? "YOU WIN 🏆" : "GAME OVER 💣");

        // Details
        playersLabel.setText("Players: " + player1Name + " & " + player2Name);
        difficultyLabel.setText("Difficulty: " + difficulty);
        scoreLabel.setText("Final Score: " + finalScore);
        livesLabel.setText("Remaining Lives: " + remainingLives);

        // Background color style
        if (win) {
            root.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #001331, #003366);"  // Blue (victory)
            );
        } else {
            root.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #2b0000, #660000);"  // Red (defeat)
            );
        }
    }

    /**
     * Handles navigation back to the main menu.
     */
    private void handleBackToMenu(Node source) {
        switchScene(source, "/View/MainWindow.fxml");
    }

    /**
     * Replaces the current scene with a new FXML view.
     *
     * @param source UI node used to retrieve the current stage
     * @param fxmlPath Path to the target FXML file
     */
    private void switchScene(Node source, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) source.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
