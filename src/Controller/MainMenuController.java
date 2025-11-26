package Controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MainMenuController {

    // --- Buttons from MainWindow.fxml  ---

    @FXML
    private Button newGameButton;        // fx:id="newGameButton"

    @FXML
    private Button gameHistoryButton;    // fx:id="gameHistoryButton"   

    @FXML
    private Button questionBankButton;   // fx:id="questionBankButton"  


    // === Handlers ===

    /**
     * Called when user clicks "Enter" under "New Game".
     * Navigates to Gamesetup.fxml.
     */
    @FXML
    private void handleNewGame(ActionEvent event) {
        switchScene(event, "/View/Gamesetup.fxml");
    }

    /**
     * Called when user clicks "Enter" under "Game History".
     * Navigates to Gamehistory.fxml.
     */
    @FXML
    private void handleGameHistory(ActionEvent event) {
        switchScene(event, "/View/Gamehistory.fxml");
    }

    /**
     * Called when user clicks "Enter" under "Question Bank".
     * Navigates to Question.fxml.
     */
    @FXML
    private void handleQuestionBank(ActionEvent event) {
        switchScene(event, "/View/Question.fxml");
    }
    
   


    // === Helper method to change scenes ===

    private void switchScene(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();   // later you can replace with an alert dialog
        }
    }
}
