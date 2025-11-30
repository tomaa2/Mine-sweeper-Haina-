module MineSweeper_1 {
	requires com.opencsv;
	requires javafx.fxml;
	requires javafx.graphics;
	requires javafx.controls;
	requires java.desktop;

opens Controller to javafx.fxml, javafx.graphics;
opens View to javafx.fxml;
requires org.apache.commons.lang3;

exports Controller;
exports Model;
}