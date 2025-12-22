package Controller;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import Model.Question;
import Model.SysData;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class QuestionController {

    // ===== CSV file path =====
    private String csvFilePath;

    // ===== Top bar elements (injected from FXML) =====
    @FXML private Button backToMenuButton;
    @FXML private Button addQuestionButton;

    // ===== Counters (injected from FXML) =====
    @FXML private Label totalQuestionsValueLabel;
    @FXML private Label easyQuestionsValueLabel;
    @FXML private Label mediumQuestionsValueLabel;
    @FXML private Label hardQuestionsValueLabel;
    @FXML private Label expertQuestionsValueLabel;

    // ===== Filters (injected from FXML) =====
    @FXML private TextField  searchField;
    @FXML private ComboBox<String> categoryFilterCombo;
    @FXML private ComboBox<String> difficultyFilterCombo; // Question difficulty ONLY

    // ===== Table (injected from FXML) =====
    @FXML private TableView<Question> questionsTable;
    @FXML private TableColumn<Question, String> difficultyColumn;      // question difficulty
    @FXML private TableColumn<Question, String> questionTextColumn;
    @FXML private TableColumn<Question, String> correctAnswerColumn;
    @FXML private TableColumn<Question, Void>   actionsColumn;

    // ===== In-memory data structures =====
    private final ObservableList<Question> masterData = FXCollections.observableArrayList();
    private FilteredList<Question> filteredData;

    /**
     * Default constructor.
     * Sets the default CSV file path.
     */
    public QuestionController() {
        this.csvFilePath = "questions.csv";
    }

    /**
     * Called automatically by JavaFX after the FXML is loaded.
     */
    @FXML
    private void initialize() {
        System.out.println("=== QuestionController.initialize() ===");

        // 0. Populate filter combo boxes
        if (difficultyFilterCombo != null) {
            difficultyFilterCombo.getItems().setAll(
                    "All Question Difficulties", "EASY", "MEDIUM", "HARD", "EXPERT"
            );
            difficultyFilterCombo.getSelectionModel().select("All Question Difficulties");
        }

        if (categoryFilterCombo != null) {
            categoryFilterCombo.getItems().setAll(
                    "All Categories", "General"
            );
            categoryFilterCombo.getSelectionModel().select("All Categories");
        }

        // 1. ALWAYS load questions from CSV into masterData + SysData
        loadQuestions(csvFilePath);
        System.out.println("MasterData size after initialize = " + masterData.size());

        // 2. Configure table columns
        if (difficultyColumn != null) {
            difficultyColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getQuestionDifficulty())
            );
        }

        if (questionTextColumn != null) {
            questionTextColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getQuestion())
            );
        }

        if (correctAnswerColumn != null) {
            correctAnswerColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getCorrectAnswer())
            );
        }

        // 3. Wrap data in FilteredList and SortedList
        filteredData = new FilteredList<>(masterData, q -> true);
        SortedList<Question> sortedData = new SortedList<>(filteredData);
        if (questionsTable != null) {
            sortedData.comparatorProperty().bind(questionsTable.comparatorProperty());
            questionsTable.setItems(sortedData);
            System.out.println("Table items size after bind = " + questionsTable.getItems().size());
        }

        // 4. Setup actions column (edit/delete buttons per row)
        if (actionsColumn != null) {
            setupActionsColumn();
        }

        // 5. Listeners for filters
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        }
        if (difficultyFilterCombo != null) {
            difficultyFilterCombo.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        }

        // 6. Initial counters
        updateCounters();
    }

    /**
     * Creates the "Actions" column (edit & delete buttons for each question row).
     */
    private void setupActionsColumn() {
        actionsColumn.setCellFactory(col -> new TableCell<>() {

            private final Button editButton   = new Button("✏");
            private final Button deleteButton = new Button("🗑");
            private final HBox   container    = new HBox(8, editButton, deleteButton);

            {
                container.setAlignment(Pos.CENTER);

                // Simple inline styling for action buttons
                editButton.setStyle("-fx-background-color: #e0f2fe; " +
                        "-fx-text-fill: #1d4ed8; " +
                        "-fx-background-radius: 6; " +
                        "-fx-font-size: 11px; " +
                        "-fx-padding: 3 7;");
                deleteButton.setStyle("-fx-background-color: #fee2e2; " +
                        "-fx-text-fill: #b91c1c; " +
                        "-fx-background-radius: 6; " +
                        "-fx-font-size: 11px; " +
                        "-fx-padding: 3 7;");

                editButton.setOnAction(e -> {
                    Question question = getTableView().getItems().get(getIndex());
                    handleEditQuestion(question);
                });

                deleteButton.setOnAction(e -> {
                    Question question = getTableView().getItems().get(getIndex());
                    handleDeleteQuestion(question);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(container);
                }
            }
        });
    }

    /**
     * Loads questions from a CSV file into masterData and SysData.
     */
    public void loadQuestions(String filePath) {
        this.csvFilePath = filePath;

        masterData.clear();  // ננקה קודם
        List<Question> loadedQuestions = new ArrayList<>();

        try {
            File f = new File(filePath);
            System.out.println("Loading questions from: " + f.getAbsolutePath());

            if (!f.exists()) {
                System.out.println("CSV file not found, no questions loaded.");
                return;
            }

            try (CSVReader reader = new CSVReader(new FileReader(f))) {
                String[] line;
                // skip header row
                reader.readNext();

                while ((line = reader.readNext()) != null) {
                    System.out.println("CSV LINE: " + Arrays.toString(line));
                    System.out.println("Line length = " + line.length);

                    if (line.length < 8) {
                        System.out.println("Skipping line, not enough columns.");
                        continue;
                    }

                    String questionText       = line[1].trim();
                    String questionDifficulty = line[2].trim();

                    List<String> answers = new ArrayList<>();
                    answers.add(line[3].trim());
                    answers.add(line[4].trim());
                    answers.add(line[5].trim());
                    answers.add(line[6].trim());

                    String correctAnswer = line[7];

                    Question q = new Question(
                            questionText,
                            answers,
                            correctAnswer,
                            questionDifficulty
                    );

                    loadedQuestions.add(q);
                    masterData.add(q);  // נוסיף גם ל-masterData
                }
            }

            SysData.getInstance().setQuestions(new ArrayList<>(masterData));
            System.out.println("Questions loaded successfully from CSV. Count = " + loadedQuestions.size());
            System.out.println("MasterData size after load = " + masterData.size());

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to load questions: " + e.getMessage());
        }
    }

    /**
     * Saves the current questions from masterData to the CSV file.
     */
    public void saveQuestions() {
        String filePath = this.csvFilePath;

        // Save based on what is actually in the table/masterData
        List<Question> questions = new ArrayList<>(masterData);

        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            // Header
            writer.writeNext(new String[]{
                    "ID", "Question", "Difficulty",
                    "A", "B", "C", "D", "Correct Answer"
            });

            int id = 1;
            for (Question q : questions) {
                writer.writeNext(new String[]{
                        String.valueOf(id++),
                        q.getQuestion(),
                        q.getQuestionDifficulty(),
                        q.getAnswers().get(0),
                        q.getAnswers().get(1),
                        q.getAnswers().get(2),
                        q.getAnswers().get(3),
                        q.getCorrectAnswer()
                });
            }

            System.out.println("Questions saved successfully to CSV. Count = " + questions.size());

            // Keep SysData in sync as well
            SysData.getInstance().setQuestions(questions);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to save questions: " + e.getMessage());
        }
    }

    /**
     * Applies search + question difficulty filters.
     */
    private void applyFilters() {
        String searchText = (searchField != null) ? searchField.getText() : "";

        String questionDiffFilter = (difficultyFilterCombo != null)
                ? difficultyFilterCombo.getValue()
                : "All Question Difficulties";

        if (filteredData == null) {
            return;
        }

        filteredData.setPredicate(q -> {
            boolean matchesText = (searchText == null || searchText.isBlank())
                    || q.getQuestion().toLowerCase().contains(searchText.toLowerCase());
            
            String mappedDifficulty = mapDifficultyToNumber(questionDiffFilter);
            boolean matchesDifficulty = (mappedDifficulty == null)  // means "All Question Difficulties"
                    || q.getQuestionDifficulty().equals(mappedDifficulty);
                    

            return matchesText && matchesDifficulty;
        });

        updateCounters();
    }

    
    //function for mapping difficulty text into numbers (EASY->1 , MEDUIM->2, HARD->3, EXPERT->4)
    private String mapDifficultyToNumber(String diff) {
        if (diff == null) return null;

        return switch (diff.toUpperCase()) {
            case "EASY"   -> "1";
            case "MEDIUM" -> "2";
            case "HARD"   -> "3";
            case "EXPERT" -> "4";
            default -> null; // "All Question Difficulties"
        };
    }

    /**
     * Updates the difficulty counters at the top of the screen
     * using the currently filtered data.
     */
    private void updateCounters() {
        if (filteredData == null) {
            return;
        }

        int total = filteredData.size();

        long easy   = filteredData.stream()
                .filter(q -> q.getQuestionDifficulty().equalsIgnoreCase("1")).count();
        long medium = filteredData.stream()
                .filter(q -> q.getQuestionDifficulty().equalsIgnoreCase("2")).count();
        long hard   = filteredData.stream()
                .filter(q -> q.getQuestionDifficulty().equalsIgnoreCase("3")).count();
        long expert = filteredData.stream()
                .filter(q -> q.getQuestionDifficulty().equalsIgnoreCase("4")).count();

        if (totalQuestionsValueLabel != null) {
            totalQuestionsValueLabel.setText(String.valueOf(total));
        }
        if (easyQuestionsValueLabel != null) {
            easyQuestionsValueLabel.setText(String.valueOf(easy));
        }
        if (mediumQuestionsValueLabel != null) {
            mediumQuestionsValueLabel.setText(String.valueOf(medium));
        }
        if (hardQuestionsValueLabel != null) {
            hardQuestionsValueLabel.setText(String.valueOf(hard));
        }
        if (expertQuestionsValueLabel != null) {
            expertQuestionsValueLabel.setText(String.valueOf(expert));
        }
    }

    /**
     * Clears all filters (search text, difficulties, category).
     */
    @FXML
    private void handleClearFilters() {
        if (searchField != null) {
            searchField.clear();
        }
        if (difficultyFilterCombo != null) {
            difficultyFilterCombo.getSelectionModel().select("All Question Difficulties");
        }
        if (categoryFilterCombo != null) {
            categoryFilterCombo.getSelectionModel().select("All Categories");
        }
        applyFilters();
    }

    /**
     * Handles the "Add Question" button.
     */
    @FXML
    private void handleAddQuestion() {
        Question newQuestion = showQuestionDialog(null);
        if (newQuestion != null) {
            boolean added = SysData.getInstance().addQuestion(newQuestion);
            if (added) {
                masterData.add(newQuestion);
                saveQuestions();
                applyFilters();
            } else {
                showError("Failed to add question. It may already exist.");
            }
        }
    }

    /**
     * Handles editing of an existing question.
     */
    private void handleEditQuestion(Question question) {
        if (question == null) {
            return;
        }

        String oldQuestionText = question.getQuestion();

        Question updated = showQuestionDialog(question);
        if (updated != null) {
            boolean success = SysData.getInstance().editQuestion(oldQuestionText, updated);
            if (success) {
                int index = masterData.indexOf(question);
                if (index >= 0) {
                    masterData.set(index, updated);
                }
                saveQuestions();
                applyFilters();
            } else {
                showError("Failed to update question. Please check the data.");
            }
        }
    }

    /**
     * Handles deletion of a question.
     */
    private void handleDeleteQuestion(Question question) {
        if (question == null) {
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Question");
        alert.setHeaderText("Are you sure you want to delete this question?");
        alert.setContentText(question.getQuestion());

        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                boolean success = SysData.getInstance().deleteQuestion(question.getQuestion());
                if (success) {
                    masterData.remove(question);
                    saveQuestions();
                    applyFilters();
                } else {
                    showError("Failed to delete question. It may not exist.");
                }
            }
        });
    }

    /**
     * Handles navigation back to the main menu.
     */
    @FXML
    private void handleBackToMenu() { 
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/MainWindow.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) backToMenuButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to navigate back to the main menu.\n" + e.getMessage());
        }
    }

    /**
     * Dialog for creating or editing a question (WITHOUT gameDifficulty).
     */
    private Question showQuestionDialog(Question existingQuestion) {
        boolean isEditMode = (existingQuestion != null);

        Dialog<Question> dialog = new Dialog<>();
        dialog.setTitle(isEditMode ? "Edit Question" : "Add Question");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Form fields
        TextField questionField      = new TextField();
        TextField answerAField       = new TextField();
        TextField answerBField       = new TextField();
        TextField answerCField       = new TextField();
        TextField answerDField       = new TextField();
        TextField correctAnswerField = new TextField();

        ComboBox<String> questionDifficultyCombo = new ComboBox<>();
        questionDifficultyCombo.getItems().addAll("EASY", "MEDIUM", "HARD", "EXPERT");

        if (isEditMode) {
            questionField.setText(existingQuestion.getQuestion());
            if (existingQuestion.getAnswers() != null && existingQuestion.getAnswers().size() == 4) {
                answerAField.setText(existingQuestion.getAnswers().get(0));
                answerBField.setText(existingQuestion.getAnswers().get(1));
                answerCField.setText(existingQuestion.getAnswers().get(2));
                answerDField.setText(existingQuestion.getAnswers().get(3));
            }

            // Try to recover the correct letter (A/B/C/D) from the existing correct answer
            String existingCorrect = existingQuestion.getCorrectAnswer();
            String correctLetter = "";
            if (existingCorrect != null) {
                if (existingCorrect.equals(answerAField.getText())) correctLetter = "A";
                else if (existingCorrect.equals(answerBField.getText())) correctLetter = "B";
                else if (existingCorrect.equals(answerCField.getText())) correctLetter = "C";
                else if (existingCorrect.equals(answerDField.getText())) correctLetter = "D";
                else correctLetter = existingCorrect; // fallback
            }
            correctAnswerField.setText(correctLetter);

            questionDifficultyCombo.getSelectionModel()
                    .select(existingQuestion.getQuestionDifficulty());

        } else {
            questionDifficultyCombo.getSelectionModel().select("EASY");
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(20, 10, 10, 10));

        int row = 0;
        grid.add(new Label("Question:"), 0, row);
        grid.add(questionField,         1, row++);

        grid.add(new Label("Answer A:"), 0, row);
        grid.add(answerAField,          1, row++);

        grid.add(new Label("Answer B:"), 0, row);
        grid.add(answerBField,          1, row++);

        grid.add(new Label("Answer C:"), 0, row);
        grid.add(answerCField,          1, row++);

        grid.add(new Label("Answer D:"), 0, row);
        grid.add(answerDField,          1, row++);

        grid.add(new Label("Correct Answer (letter A/B/C/D):"), 0, row);
        grid.add(correctAnswerField,                            1, row++);

        grid.add(new Label("Question Difficulty:"), 0, row);
        grid.add(questionDifficultyCombo,        1, row++);

        dialog.getDialogPane().setContent(grid);

        // Disable Save button when any required field is empty
        dialog.getDialogPane().lookupButton(saveButtonType).disableProperty().bind(
                questionField.textProperty().isEmpty()
                        .or(answerAField.textProperty().isEmpty())
                        .or(answerBField.textProperty().isEmpty())
                        .or(answerCField.textProperty().isEmpty())
                        .or(answerDField.textProperty().isEmpty())
                        .or(correctAnswerField.textProperty().isEmpty())
        );

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String qText   = questionField.getText().trim();
                String aA      = answerAField.getText().trim();
                String aB      = answerBField.getText().trim();
                String aC      = answerCField.getText().trim();
                String aD      = answerDField.getText().trim();
                String correct = correctAnswerField.getText().trim();

                // User enters only the letter A/B/C/D, we map to the full text
                String correctLetter = correct.toUpperCase();
                String correctAnswer;

                switch (correctLetter) {
                    case "A":
                        correctAnswer = aA;
                        break;
                    case "B":
                        correctAnswer = aB;
                        break;
                    case "C":
                        correctAnswer = aC;
                        break;
                    case "D":
                        correctAnswer = aD;
                        break;
                    default:
                        showError("Correct answer must be the letter A, B, C, or D.");
                        return null;
                }

                String questionDiff = questionDifficultyCombo.getValue();
                if (questionDiff == null || questionDiff.isBlank()) {
                    questionDiff = "EASY";
                }
                String difficultyNumber = mapDifficultyToNumber(questionDiff);

                List<String> answers = List.of(aA, aB, aC, aD);
                return new Question(qText, answers, correctAnswer, difficultyNumber);
            }
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }

    /**
     * Utility method to show an error alert with the given message.
     */
    private void showError(String message) {
        Alert error = new Alert(Alert.AlertType.ERROR);
        error.setTitle("Error");
        error.setHeaderText("Operation failed");
        error.setContentText(message);
        error.showAndWait();
    }
    
    
}
