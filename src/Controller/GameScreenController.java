package Controller;

import Model.Board;
import Model.Cell;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.io.IOException;

public class GameScreenController {

    // FXML fields matching gamescreen.fxml
    @FXML private Label backToMenuLabel;
    @FXML private Button newGameButton;
    @FXML private Label turnLabel;
    @FXML private Label livesLabel;
    @FXML private Label currentPlayerLabel;
    @FXML private Button modeButton;
    @FXML private Label scoreLabel;
    @FXML private Label player1NameLabel;
    @FXML private Label player1FlagsLabel;
    @FXML private GridPane player1Grid;
    @FXML private Label player2NameLabel;
    @FXML private Label player2FlagsLabel;
    @FXML private GridPane player2Grid;

    // game logic controller
    private final GameController gameController;
    
    // flag mode or reveal mode
    private boolean flagMode = false;

    public GameScreenController(GameController gameController) {
        this.gameController = gameController;
    }

    // called manually after FXML loads
    public void initializeAfterLoad() {
        setupEventHandlers();
        setupBoards();
        updateUI();
    }

    private void setupEventHandlers() {
        backToMenuLabel.setOnMouseClicked(this::handleBackToMenu);
        newGameButton.setOnAction(e -> handleNewGame((Node) e.getSource()));
        modeButton.setOnAction(e -> toggleMode());
    }

    private void setupBoards() {
        int gridSize = gameController.getConfig().getGridSize();

        setupGrid(player1Grid, gridSize, 1);
        setupGrid(player2Grid, gridSize, 2);

        player1NameLabel.setText(gameController.getGame().getPlayer1().getName());
        player2NameLabel.setText(gameController.getGame().getPlayer2().getName());
    }

    private void setupGrid(GridPane grid, int size, int playerIndex) {
        grid.getChildren().clear();
        grid.getColumnConstraints().clear();
        grid.getRowConstraints().clear();

        for (int i = 0; i < size; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setHgrow(Priority.ALWAYS);
            col.setPercentWidth(100.0 / size);
            grid.getColumnConstraints().add(col);

            RowConstraints row = new RowConstraints();
            row.setVgrow(Priority.ALWAYS);
            row.setPercentHeight(100.0 / size);
            grid.getRowConstraints().add(row);
        }

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Button cellButton = createCellButton(playerIndex, row, col);
                grid.add(cellButton, col, row);
            }
        }
    }

    private Button createCellButton(int playerIndex, int row, int col) {
        Button btn = new Button();
        btn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btn.setMinSize(20, 20);
        btn.setStyle("-fx-background-color: #cccccc; -fx-border-color: #999999;");

        btn.setOnMouseClicked(e -> handleCellClick(e, playerIndex, row, col));

        return btn;
    }

    private void handleCellClick(MouseEvent event, int playerIndex, int row, int col) {
        // check if its this players turn
        int currentPlayerIndex = gameController.getGame().getCurrentPlayerIndex();
        if (playerIndex != currentPlayerIndex) {
            showAlert("Wrong Board", "It's not your turn!");
            return;
        }

        // right click or flag mode = flag
        if (event.getButton() == MouseButton.SECONDARY || flagMode) {
            gameController.flagCell(playerIndex, row, col);
        } else {
            gameController.revealCell(playerIndex, row, col);
        }

        updateUI();
        
        if (gameController.checkGameEnd()) {
            showGameOver();
        }
    }

    private void updateUI() {
        scoreLabel.setText("Score: " + gameController.getScore());
        livesLabel.setText(String.valueOf(gameController.getLives()));

        String currentName = gameController.getCurrentPlayer().getName();
        currentPlayerLabel.setText("Current: " + currentName);

        updateGrid(player1Grid, gameController.getBoard1());
        updateGrid(player2Grid, gameController.getBoard2());

        updateFlagCount(player1FlagsLabel, gameController.getBoard1());
        updateFlagCount(player2FlagsLabel, gameController.getBoard2());

        highlightCurrentPlayer();
    }

    private void updateGrid(GridPane grid, Board board) {
        int size = board.getRows();

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Cell cell = board.getCell(row, col);
                Button btn = (Button) getNodeFromGrid(grid, col, row);

                if (btn != null) {
                    updateCellButton(btn, cell);
                }
            }
        }
    }

    private void updateCellButton(Button btn, Cell cell) {
        if (cell.isRevealed()) {
            switch (cell.getCellType()) {
                case MINE -> {
                    btn.setText("💣");
                    btn.setStyle("-fx-background-color: #ff6666;");
                }
                case NUMBER -> {
                    btn.setText(String.valueOf(cell.getNumber()));
                    btn.setStyle("-fx-background-color: #ffffff;");
                }
                case EMPTY -> {
                    btn.setText("");
                    btn.setStyle("-fx-background-color: #e0e0e0;");
                }
                case QUESTION -> {
                    btn.setText(cell.isUsed() ? "✓" : "❓");
                    btn.setStyle("-fx-background-color: #ffff99;");
                }
                case SURPRISE -> {
                    btn.setText(cell.isUsed() ? "✓" : "🎁");
                    btn.setStyle("-fx-background-color: #ff99ff;");
                }
            }
        } else if (cell.isFlagged()) {
            btn.setText("🚩");
            btn.setStyle("-fx-background-color: #ffcc00;");
        } else {
            btn.setText("");
            btn.setStyle("-fx-background-color: #cccccc; -fx-border-color: #999999;");
        }
    }

    private Node getNodeFromGrid(GridPane grid, int col, int row) {
        for (Node node : grid.getChildren()) {
            Integer nodeCol = GridPane.getColumnIndex(node);
            Integer nodeRow = GridPane.getRowIndex(node);
            
            if (nodeCol == null) nodeCol = 0;
            if (nodeRow == null) nodeRow = 0;
            
            if (nodeCol == col && nodeRow == row) {
                return node;
            }
        }
        return null;
    }

    private void updateFlagCount(Label label, Board board) {
        int flagged = 0;
        int totalMines = gameController.getConfig().getTotalMines();

        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getColumns(); c++) {
                if (board.getCell(r, c).isFlagged()) {
                    flagged++;
                }
            }
        }

        label.setText(flagged + "/" + totalMines);
    }

    private void highlightCurrentPlayer() {
        int current = gameController.getGame().getCurrentPlayerIndex();
        
        String activeStyle = "-fx-border-color: #00cc00; -fx-border-width: 3;";
        String inactiveStyle = "-fx-border-color: transparent;";

        player1Grid.setStyle(current == 1 ? activeStyle : inactiveStyle);
        player2Grid.setStyle(current == 2 ? activeStyle : inactiveStyle);
    }

    private void toggleMode() {
        flagMode = !flagMode;
        modeButton.setText(flagMode ? "Flag Mode 🚩" : "Reveal Mode 👆");
    }

    private void showGameOver() {
        int finalScore = gameController.getScore();
        int remainingLives = gameController.getLives();
        
        String result = remainingLives > 0 ? "You Won!" : "Game Over!";
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(result);
        alert.setContentText("Final Score: " + finalScore + "\nRemaining Lives: " + remainingLives);
        alert.showAndWait();
    }

    private void handleBackToMenu(MouseEvent event) {
        switchScene((Node) event.getSource(), "/View/MainWindow.fxml");
    }

    private void handleNewGame(Node source) {
        switchScene(source, "/View/Gamesetup.fxml");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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