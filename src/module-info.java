module MineSweeper_1 {
	requires com.opencsv;
	requires javafx.fxml;
	requires javafx.graphics;
	requires javafx.controls;
	requires java.desktop;

opens Controller to javafx.fxml, javafx.graphics;
opens View to javafx.fxml;
opens View.iconss to javafx.fxml;

exports Controller;
exports Model;
}