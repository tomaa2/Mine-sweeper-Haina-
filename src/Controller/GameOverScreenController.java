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

    // נקרא מה-GameScreenController אחרי טעינת ה-FXML
    public void initData(String p1, String p2, String difficulty,
                         int finalScore, int remainingLives) {

        this.player1Name = p1;
        this.player2Name = p2;
        this.difficulty = difficulty;
        this.finalScore = finalScore;
        this.remainingLives = remainingLives;

        updateUI();
    }

    @FXML
    private void initialize() {
        backToMenuButton.setOnAction(e -> handleBackToMenu((Node) e.getSource()));
    }

    private void updateUI() {
        boolean win = remainingLives > 0;

        // כותרת לפי מצב
        titleLabel.setText(win ? "YOU WIN 🏆" : "GAME OVER 💣");

        // מלל
        playersLabel.setText("Players: " + player1Name + " & " + player2Name);
        difficultyLabel.setText("Difficulty: " + difficulty);
        scoreLabel.setText("Final Score: " + finalScore);
        livesLabel.setText("Remaining Lives: " + remainingLives);

        // צבע רקע – אם תרצי הבדל בין WIN ל-LOSE
        if (win) {
            root.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #001331, #003366);"  // כחול ניצחון
            );
        } else {
            root.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #2b0000, #660000);"  // אדום הפסד
            );
        }
    }

    private void handleBackToMenu(Node source) {
        switchScene(source, "/View/MainWindow.fxml");
    }

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
