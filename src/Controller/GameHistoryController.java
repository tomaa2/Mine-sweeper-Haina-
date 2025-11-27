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

public class GameHistoryController {

    @FXML
    private Label backLabel;   // מחובר ל-fx:id="backLabel" ב-FXML

    @FXML
    private void initialize() {
        // להפוך את הלייבל ל"כפתור" – לחיצה תחזיר לתפריט הראשי
        backLabel.setOnMouseClicked(this::handleBackToMenu);
    }

    private void handleBackToMenu(MouseEvent event) {
        switchScene((Node) event.getSource(), "/View/MainWindow.fxml");
    }

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
