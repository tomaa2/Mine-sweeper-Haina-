package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

import Model.GameConfig;

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
    private GameConfig currentConfig = GameConfig.EASY;

    // max length for player name
    private static final int MAX_NAME_LENGTH = 15;

    // ----- Initialization -----

    @FXML
    private void initialize() {
        // Default difficulty when screen opens
        setDifficulty(GameConfig.EASY);

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
        setDifficulty(GameConfig.EASY);
    }

    @FXML
    private void selectMedium() {
        setDifficulty(GameConfig.MEDIUM);
    }

    @FXML
    private void selectHard() {
        setDifficulty(GameConfig.HARD);
    }

    // ----- Navigation handlers -----

    private void handleBackToMenu(MouseEvent event) {
        switchScene((Node) event.getSource(), "/View/MainWindow.fxml");
    }

    private void handleStartGame(Node source) {
        String player1Name = player1Field.getText().trim();
        String player2Name = player2Field.getText().trim();

        // validate player 1 name
        if (!validatePlayerName(player1Name, "Player 1", player1Field)) {
            return;
        }

        // validate player 2 name
        if (!validatePlayerName(player2Name, "Player 2", player2Field)) {
            return;
        }

        // check duplicates (same names)
        if (player1Name.equalsIgnoreCase(player2Name)) {
            showAlert("Same Names", "Players must have different names.");
            player2Field.requestFocus();
            return;
        }

        // if we got here – both names are valid
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/gamescreen.fxml"));

            GameController gameController = new GameController(player1Name, player2Name, currentConfig);
            GameScreenController screenController = new GameScreenController(gameController);
            loader.setController(screenController);

            Parent root = loader.load();
            screenController.initializeAfterLoad();

            Stage stage = (Stage) source.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load game screen.");
        }
    }

    // ----- Name validation helpers -----

    private boolean validatePlayerName(String name, String playerLabel, TextField field) {
        // empty
        if (name.isEmpty()) {
            showAlert("Missing Name", "Please enter a name for " + playerLabel + ".");
            field.requestFocus();
            return false;
        }

        // too long
        if (name.length() > MAX_NAME_LENGTH) {
            showAlert("Name Too Long",
                    "Name for " + playerLabel + " must be at most " + MAX_NAME_LENGTH + " characters.");
            field.requestFocus();
            return false;
        }

        // invalid characters – only letters, digits and spaces
        if (!name.matches("[A-Za-z0-9 ]+")) {
            showAlert("Invalid Characters",
                    "Name for " + playerLabel + " can contain only letters, digits and spaces.");
            field.requestFocus();
            return false;
        }

        return true;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ----- Core logic -----

    private void setDifficulty(GameConfig config) {
        this.currentConfig = config;

        // Update rules text
        switch (config) {
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
                        "• You share only 6 lives\n" +
                        "• Every click is risky – plan carefully\n" +
                        "• Only perfect team play will win the game!"
                );
            }
        }
        updateDifficultyButtonStyles();
    }

    private void updateDifficultyButtonStyles() {
        String selectedStyle = "-fx-background-color: #cce4ff;";
        String normalStyle = "";

        easyButton.setStyle(currentConfig == GameConfig.EASY ? selectedStyle : normalStyle);
        mediumButton.setStyle(currentConfig == GameConfig.MEDIUM ? selectedStyle : normalStyle);
        hardButton.setStyle(currentConfig == GameConfig.HARD ? selectedStyle : normalStyle);
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
