package Controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * Controller for the Game History screen.
 * 
 * Handles the "Back" label, which sends the user
 * back to the main menu when clicked.
 */
public class GameHistoryController {

    // Label that works like a Back button
    @FXML
    private Label backLabel;

    /**
     * Called automatically when the screen loads.
     * Sets the Back label to respond to mouse clicks.
     */
    @FXML
    private void initialize() {
        backLabel.setOnMouseClicked(this::handleBackToMenu);
    }

    /**
     * Returns the user to the main menu screen.
     */
    private void handleBackToMenu(MouseEvent event) {
        switchScene((Node) event.getSource(), "/View/MainWindow.fxml");
    }

    /**
     * Switches from the current screen to another FXML screen.
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