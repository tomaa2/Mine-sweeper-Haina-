package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class HelpController {

    // If you used a Button:
    @FXML
    private Button backButton;

    // --- For a Button onAction="#handleBackToSetup" ---
    @FXML
    private void handleBack(MouseEvent event) {
        switchScene((Node) event.getSource(), "/View/Gamesetup.fxml");
    }

    // --- Utility method to switch scenes ---
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
