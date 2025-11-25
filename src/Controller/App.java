package Controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the first screen: MainWindow.fxml from the View package
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/View/MainWindow.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setTitle("Mine Sweeper");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            // If something is wrong with the FXML, you will see it here
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);   // Starts the JavaFX application
    }
}
