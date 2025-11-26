package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class GameSetupController {

    // ----- FXML fields (must match fx:id in Gamesetup.fxml) -----

    @FXML
    private Label backToMenuLabel;

    @FXML
    private Label modeTitleLabel;

    @FXML
    private TextField player1Field;

    @FXML
    private TextField player2Field;

    @FXML
    private Button easyButton;

    @FXML
    private Button mediumButton;

    @FXML
    private Button hardButton;

    @FXML
    private Label rulesTitleLabel;

    @FXML
    private Label rulesBodyLabel;

    @FXML
    private Button startGameButton;

    @FXML
    private Button helpButton;

    // ----- Internal state -----

    private enum Difficulty {
        EASY, MEDIUM, HARD
    }

    private Difficulty currentDifficulty = Difficulty.EASY;

    // ----- Initialization -----

    @FXML
    private void initialize() {
        // Default difficulty when screen opens
        setDifficulty(Difficulty.EASY);

        // Make "Back to Menu" label clickable
        backToMenuLabel.setOnMouseClicked(this::handleBackToMenu);

        // Help button action
        helpButton.setOnAction(e ->
                switchScene((Node) e.getSource(), "/View/Help.fxml")
        );

        // Start Game button action
        startGameButton.setOnAction(e -> handleStartGame((Node) e.getSource()));
    }

    // ----- Difficulty button handlers (called from FXML onAction) -----

    @FXML
    private void selectEasy() {
        setDifficulty(Difficulty.EASY);
    }

    @FXML
    private void selectMedium() {
        setDifficulty(Difficulty.MEDIUM);
    }

    @FXML
    private void selectHard() {
        setDifficulty(Difficulty.HARD);
    }

    // ----- Navigation handlers -----

    private void handleBackToMenu(MouseEvent event) {
        switchScene((Node) event.getSource(), "/View/MainWindow.fxml");
    }

    private void handleStartGame(Node source) {
        // TODO: here you can pass player names & difficulty to the game logic
        // For now we just navigate to the game screen.
        switchScene(source, "/View/gamescreen.fxml");
    }

    // ----- Core logic -----

    private void setDifficulty(Difficulty difficulty) {
        this.currentDifficulty = difficulty;

        // Update rules text
        switch (difficulty) {
            case EASY -> {
                rulesTitleLabel.setText("Game Rules – Easy:");
                rulesBodyLabel.setText(
                        "• Each player has their own board to clear\n" +
                        "• You share 10 lives – hitting a mine costs 1 life\n" +
                        "• Both players must clear their boards to win\n" +
                        "• Work together and strategize!"
                );
            }
            case MEDIUM -> {
                rulesTitleLabel.setText("Game Rules – Medium:");
                rulesBodyLabel.setText(
                        "• Larger boards with more mines\n" +
                        "• You share 8 lives instead of 10\n" +
                        "• Mistakes are more expensive – think twice\n" +
                        "• Coordination between players is critical"
                );
            }
            case HARD -> {
                rulesTitleLabel.setText("Game Rules – Hard:");
                rulesBodyLabel.setText(
                        "• Biggest boards with many mines\n" +
                        "• You share only 5 lives\n" +
                        "• Every click is risky – plan carefully\n" +
                        "• Only perfect team play will win the game!"
                );
            }
        }

        // Optional: you can also visually highlight the selected button
        updateDifficultyButtonStyles();
    }

    private void updateDifficultyButtonStyles() {
        // Simple style toggle – you can replace with your own CSS later
        String selectedStyle = "-fx-background-color: #cce4ff;";
        String normalStyle   = "";

        easyButton.setStyle(currentDifficulty == Difficulty.EASY ? selectedStyle : normalStyle);
        mediumButton.setStyle(currentDifficulty == Difficulty.MEDIUM ? selectedStyle : normalStyle);
        hardButton.setStyle(currentDifficulty == Difficulty.HARD ? selectedStyle : normalStyle);
    }

    // ----- Utility: scene switching -----

    private void switchScene(Node source, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) source.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();   // later you can show an alert instead
        }
    }
}
