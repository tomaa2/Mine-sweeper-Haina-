package Controller;

import Model.Board;
import Model.Cell;
import Model.CellType;
import Model.Question;
import Model.SoundManager;
//import Controller.GameObserver;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;

import java.io.IOException;

public class GameScreenController implements GameObserver{

	// FXML fields matching gamescreen.fxml
	@FXML
	private Label backToMenuLabel;
	@FXML
	private Button newGameButton;
	@FXML
	private Label turnLabel;
	@FXML
	private Label livesLabel;
	@FXML
	private Label currentPlayerLabel;
	@FXML
	private Button modeButton;
	@FXML
	private Label scoreLabel;
	@FXML
	private Label player1NameLabel;
	@FXML
	private Label player1FlagsLabel;
	@FXML
	private GridPane player1Grid;
	@FXML
	private Label player2NameLabel;
	@FXML
	private Label player2FlagsLabel;
	@FXML
	private GridPane player2Grid;
	@FXML
	private javafx.scene.layout.BorderPane gameRootPane;
	// timer for each turn
	private AnimationTimer gameTimer;
	private long startTimeNanos;
	private int elapsedSeconds = 0;
	// track if game has ended
	private boolean gameOver = false;

	// game logic controller
	private final GameController gameController;

	// flag mode or reveal mode
	private boolean flagMode = false;

	public GameScreenController(GameController gameController) {
		this.gameController = gameController;
	}

	// called manually after FXML loads
	public void initializeAfterLoad() {
		gameController.addObserver(this);
		setupEventHandlers();
		setupBoards();
		setupTimer();
		updateUI();
		
		gameController.setGameSceneNode(gameRootPane);
	    System.out.println("Game scene node set for screenshot capture");
	}

	private void setupEventHandlers() {
		backToMenuLabel.setOnMouseClicked(this::handleBackToMenu);
		newGameButton.setOnAction(e -> handleNewGame((Node) e.getSource()));
		modeButton.setOnAction(e -> toggleMode());
		modeButton.setText("🚩 Flag OFF");
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
		btn.setMinSize(25, 25);
		btn.setPrefSize(30, 30);
		btn.setStyle("-fx-background-color: #cccccc; -fx-border-color: #999999; -fx-font-size: 12px; -fx-padding: 0;");

		btn.setOnMouseClicked(e -> handleCellClick(e, playerIndex, row, col));

		return btn;
	}

	private void handleCellClick2(MouseEvent event, int playerIndex, int row, int col) {
		// ignores any click if game is over
		if (gameOver) {
			// SoundManager.playGameOver();
			return;
		}
		// check if its this players turn
		int currentPlayerIndex = gameController.getGame().getCurrentPlayerIndex();
		if (playerIndex != currentPlayerIndex) {
			showAlert("Wrong Board", "It's not your turn!");
			return;
		}

		// get the cell to check if action is valid
		Board board = (playerIndex == 1) ? gameController.getBoard1() : gameController.getBoard2();
		Cell cell = board.getCell(row, col);

		// ignore if cell is already revealed or flagged
		if (cell == null || (cell.isRevealed() && cell.getCellType() != CellType.QUESTION
				&& cell.getCellType() != CellType.SURPRISE) || cell.isUsed() || cell.isFlagged()) {
			return;
		}
		SoundManager.playClick();

		// right click or flag mode = flag
		if (event.getButton() == MouseButton.SECONDARY || flagMode) {
			gameController.flagCell(playerIndex, row, col);
			gameController.switchTurn();
			// turn off flag mode after using it
			if (flagMode) {
				flagMode = false;
				modeButton.setText("🚩 Flag OFF");
				modeButton.setStyle("");
			}

		}
		if (cell.getCellType() == CellType.QUESTION && cell.isRevealed() && !cell.isUsed()) {
			Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
			confirm.setTitle("Activate Question");
			confirm.setHeaderText("Do you want to activate this question cell?");
			confirm.setContentText("This will cost points.");
			confirm.showAndWait().ifPresent(result -> {
				if (result == ButtonType.OK) {
					updateUI();
					// Activate the question → marks used + deduct cost + returns random question
					Question q = gameController.activateQuestionCell(cell);
					// System.out.println(q);

					if (q != null) {
						showQuestionPopup(cell, q); // Show the MCQ popup
					}

					updateUI();

					if (gameController.checkGameEnd()) {
						showGameOver();
					}
				}
			});
		}
		if (cell.getCellType() == CellType.SURPRISE && cell.isRevealed() && !cell.isUsed()) {
			Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
			confirm.setTitle("Activate Surprise");
			confirm.setHeaderText("Do you want to activate this surprise cell?");
			confirm.setContentText("This will cost points.");
			confirm.showAndWait().ifPresent(result -> {
				if (result == ButtonType.OK) {
					updateUI();
					gameController.activateSurpriseCell(cell);
					updateUI();

					if (gameController.checkGameEnd()) {
						showGameOver();
					}

				}
			});
		}

		else {
			gameController.revealCell(playerIndex, row, col);
		}

		updateUI();

		if (gameController.checkGameEnd()) {
			updateUI();
			showGameOver();
		}
	}
	
	private void handleCellClick(MouseEvent event, int playerIndex, int row, int col) {

	    // ignore clicks if game is over
	    if (gameOver) {
	        return;
	    }

	    // check turn
	    int currentPlayerIndex = gameController.getGame().getCurrentPlayerIndex();
	    if (playerIndex != currentPlayerIndex) {
	        showAlert("Wrong Board", "It's not your turn!");
	        return;
	    }

	    // get board & cell
	    Board board = (playerIndex == 1)
	            ? gameController.getBoard1()
	            : gameController.getBoard2();

	    Cell cell = board.getCell(row, col);

	    // invalid cell or invalid state
	    if (cell == null ||
	        (cell.isRevealed() && cell.getCellType() != CellType.QUESTION
	                && cell.getCellType() != CellType.SURPRISE) ||
	        cell.isUsed() ||
	        cell.isFlagged()) {
	        return;
	    }

	    SoundManager.playClick();

	    // flag mode or right-click → flag cell
	    if (event.getButton() == MouseButton.SECONDARY || flagMode) {
	        gameController.flagCell(playerIndex, row, col);
	        gameController.switchTurn();

	        // UI-only logic (safe here)
	        if (flagMode) {
	            flagMode = false;
	            modeButton.setText("🚩 Flag OFF");
	            modeButton.setStyle("");
	        }
	        return; // important: stop further processing
	    }

	    // QUESTION activation
	    if (cell.getCellType() == CellType.QUESTION && cell.isRevealed() && !cell.isUsed()) {
	        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
	        confirm.setTitle("Activate Question");
	        confirm.setHeaderText("Do you want to activate this question cell?");
	        confirm.setContentText("This will cost points.");

	        confirm.showAndWait().ifPresent(result -> {
	            if (result == ButtonType.OK) {

	                Question q = gameController.activateQuestionCell(cell);

	                if (q != null) {
	                    showQuestionPopup(cell, q);
	                }
	                
	                gameController.notifyObservers();

	                if (gameController.checkGameEnd()) {
	                    showGameOver();
	                }
	            }
	        });
	        return;
	    }

	    // SURPRISE activation
	    if (cell.getCellType() == CellType.SURPRISE && cell.isRevealed() && !cell.isUsed()) {
	        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
	        confirm.setTitle("Activate Surprise");
	        confirm.setHeaderText("Do you want to activate this surprise cell?");
	        confirm.setContentText("This will cost points.");

	        confirm.showAndWait().ifPresent(result -> {
	            if (result == ButtonType.OK) {

	                gameController.activateSurpriseCell(cell);
	                gameController.notifyObservers();

	                if (gameController.checkGameEnd()) {
	                    showGameOver();
	                }
	            }
	        });
	        return;
	    }

	    // normal reveal
	    gameController.revealCell(playerIndex, row, col);

	    // game over check (UI decision only)
	    if (gameController.checkGameEnd()) {
	        showGameOver();
	    }
	}


	private void showQuestionPopup(Cell cell, Question q) {
		// Question q = gameController.activateQuestionCell(cell);
		if (q == null) {
			showError("Could not load question.");
			return; // invalid or already used
		}

		Alert alert = new Alert(Alert.AlertType.NONE);
		alert.setTitle("Question Cell");
		alert.setHeaderText(q.getQuestion());

		ButtonType aBtn = new ButtonType(q.getAnswers().get(0));
		ButtonType bBtn = new ButtonType(q.getAnswers().get(1));
		ButtonType cBtn = new ButtonType(q.getAnswers().get(2));
		ButtonType dBtn = new ButtonType(q.getAnswers().get(3));

		alert.getButtonTypes().setAll(aBtn, bBtn, cBtn, dBtn);

		alert.showAndWait().ifPresent(choice -> {
			String answer = null;
			if (choice == aBtn)
				answer = "A";
			if (choice == bBtn)
				answer = "B";
			if (choice == cBtn)
				answer = "C";
			if (choice == dBtn)
				answer = "D";
			gameController.applyAnswer(q, answer);
			//updateUI();

			if (gameController.checkGameEnd()) {
				showGameOver();
			}
		});
	}

	private void setupTimer() {
		startTimeNanos = System.nanoTime();

		gameTimer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				long elapsedNanos = now - startTimeNanos;
				int seconds = (int) (elapsedNanos / 1_000_000_000);

				if (seconds != elapsedSeconds) {
					elapsedSeconds = seconds;
					updateTimerLabel();
				}
			}
		};
		gameTimer.start();
	}

	private void updateTimerLabel() {
		int minutes = elapsedSeconds / 60;
		int secs = elapsedSeconds % 60;

		if (minutes > 0) {
			turnLabel.setText("Time: " + minutes + "m " + secs + "s");
		} else {
			turnLabel.setText("Time: " + secs + "s");
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

		String baseStyle = "-fx-font-size: 12px; -fx-padding: 0; -fx-border-color: #999999;";
		if (cell.isRevealed()) {
			btn.getStyleClass().add("cell-revealed");

			switch (cell.getCellType()) {
			case MINE -> {
				btn.setText("💣");
				btn.setStyle(baseStyle + "-fx-background-color: #ff6666;");
			}
			case NUMBER -> {
				btn.setText(String.valueOf(cell.getNumber()));
				btn.setStyle(baseStyle + "-fx-background-color: #ffffff;");
			}
			case EMPTY -> {
				btn.setText("");
				btn.setStyle(baseStyle + "-fx-background-color: #ffffff;");
			}
			case QUESTION -> {
				btn.setText(cell.isUsed() ? "✓" : "❓");
				btn.setStyle(baseStyle + "-fx-background-color: #ffff99;");
			}
			case SURPRISE -> {
				btn.setText(cell.isUsed() ? "✓" : "🎁");
				btn.setStyle(baseStyle + "-fx-background-color: #ff99ff;");
			}
			}
		} else if (cell.isFlagged()) {
			btn.setText("🚩");
			btn.setStyle(baseStyle + "-fx-background-color: #ffcc00;");
		} else {
			btn.setText("");
			btn.setStyle(baseStyle + "-fx-background-color: #cccccc; -fx-border-color: #999999;");
		}
	}

	private Node getNodeFromGrid(GridPane grid, int col, int row) {
		for (Node node : grid.getChildren()) {
			Integer nodeCol = GridPane.getColumnIndex(node);
			Integer nodeRow = GridPane.getRowIndex(node);

			if (nodeCol == null)
				nodeCol = 0;
			if (nodeRow == null)
				nodeRow = 0;

			if (nodeCol == col && nodeRow == row) {
				return node;
			}
		}
		return null;
	}

	private void updateFlagCount(Label label, Board board) {
		int foundOrFlagged = 0;
		int totalMines = gameController.getConfig().getTotalMines();

		for (int r = 0; r < board.getRows(); r++) {
			for (int c = 0; c < board.getColumns(); c++) {
				Cell cell = board.getCell(r, c);
				// count only actual mines that are revealed or flagged
				if (cell.getCellType() == Model.CellType.MINE && (cell.isRevealed() || cell.isFlagged())) {
					foundOrFlagged++;
				}
			}
		}

		label.setText(foundOrFlagged + "/" + totalMines);
	}

	private void highlightCurrentPlayer() {
		int current = gameController.getGame().getCurrentPlayerIndex();

		String activeStyle = "-fx-border-color: #00cc00; -fx-border-width: 3; -fx-border-style: solid;";
		String inactiveStyle = "-fx-border-color: transparent; -fx-border-width: 3; -fx-border-style: solid;";
		player1Grid.setStyle(current == 1 ? activeStyle : inactiveStyle);
		player2Grid.setStyle(current == 2 ? activeStyle : inactiveStyle);
	}

	private void toggleMode() {
		flagMode = !flagMode;
		if (flagMode) {
			modeButton.setText("🚩 Flag ON");
			modeButton.setStyle("-fx-background-color: #ffcc00");
		} else {
			modeButton.setText("🚩 Flag OFF");
			modeButton.setStyle("");
		}

	}

	/**
	 * Displays the Game Over screen by loading GameOverScreen.fxml. Stops the
	 * timer, collects final game data, and switches the scene.
	 */
	private void showGameOver() {
		if (gameTimer != null) {
			gameTimer.stop();
		}
		gameOver = true;

		// Collect end-game data
		String p1 = gameController.getGame().getPlayer1().getName();
		String p2 = gameController.getGame().getPlayer2().getName();
		String difficulty = gameController.getConfig().name();
		int finalScore = gameController.getScore();
		int remainingLives = gameController.getLives();

		// create result(game over) screen
		String result = remainingLives > 0 ? "🎉 YOU WIN!" : "💣 GAME OVER";
		// custom dialog
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Game Over");
		// create custom VBox content
		VBox content = new VBox(15);
		content.setAlignment(javafx.geometry.Pos.CENTER);
		content.setPadding(new javafx.geometry.Insets(20));
		content.setStyle("-fx-background-color: linear-gradient(to bottom, #2b0000, #660000);");
		// details labels
		Label titleLabel = new Label(result);
		titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 32px; -fx-font-weight: bold;");
		Label playersLabel = new Label(p1 + " & " + p2);
		playersLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
		Label difficultyLabel = new Label("Difficulty: " + difficulty);
		difficultyLabel.setStyle("-fx-text-fill: #dddddd; -fx-font-size: 16px;");
		Label scoreLabel = new Label("Final Score: " + finalScore);
		scoreLabel.setStyle("-fx-text-fill: #ffeb3b; -fx-font-size: 20px;");
		Label livesLabel = new Label("Remaining Lives: " + remainingLives);
		livesLabel.setStyle("-fx-text-fill: #ff8a80; -fx-font-size: 16px;");
		Label timeLabel = new Label("Time: " + elapsedSeconds + "s");
		timeLabel.setStyle("-fx-text-fill: #80d4ff; -fx-font-size: 16px;");

		content.getChildren().addAll(titleLabel, playersLabel, difficultyLabel, scoreLabel, livesLabel, timeLabel);
		dialog.getDialogPane().setContent(content);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
		dialog.getDialogPane().setStyle("-fx-background-color: #660000;");
		// style the OK button
		Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
		okButton.setText("Back to Menu");
		okButton.setStyle(
				"-fx-background-color: #444444; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 20;");
		dialog.showAndWait();

		// go back to menu after closing
		switchScene(backToMenuLabel, "/View/MainWindow.fxml");
	}

	private void handleBackToMenu(MouseEvent event) {
		Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
		confirm.setTitle("Back to Menu");
		confirm.setHeaderText("Are you sure you want to quit?");
		confirm.setContentText("Your game progress will be saved.");
		confirm.showAndWait().ifPresent(response -> {
			if (response == javafx.scene.control.ButtonType.OK) {
	            gameController.setGameQuit(true);
				// stop timer and save game
				if (gameTimer != null)
					gameTimer.stop();
				gameController.saveAndEndGame();
				switchScene((Node) event.getSource(), "/View/MainWindow.fxml");

			}

		});
	}

	private void handleNewGame(Node source) {
		Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
		confirm.setTitle("New Game");
		confirm.setHeaderText("Are you sure you want to start a new game?");
		confirm.setContentText("Your current game progress will be saved.");

		confirm.showAndWait().ifPresent(response -> {
			if (response == javafx.scene.control.ButtonType.OK) {
				// stop timer
				if (gameTimer != null) {
					gameTimer.stop();
				}
				// save the game
				gameController.saveAndEndGame();
				switchScene(source, "/View/Gamesetup.fxml");
			}
		});
	}

	private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	private void switchScene(Node source, String fxmlPath) {
		// stop timer before switching scene
		if (gameTimer != null) {
			gameTimer.stop();
		}

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

	private void showError(String message) {
		Alert error = new Alert(Alert.AlertType.ERROR);
		error.setTitle("Error");
		error.setHeaderText("Operation failed");
		error.setContentText(message);
		error.showAndWait();
	}

	@Override
	public void onGameStateChanged() {
		// TODO Auto-generated method stub
		System.out.println("Observer notified"); //for testing
		updateUI();
	}

	
	
}