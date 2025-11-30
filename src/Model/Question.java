package Model;

import java.util.*;

/**
 * Represents a game question with:
 * - questionDifficulty: EASY / MEDIUM / HARD / EXPERT
 * - gameDifficulty: Easy / Medium / Hard
 */
public class Question {

    private String question;
    private List<String> answers;
    private String correctAnswer;

    // New fields
    private String gameDifficulty;        // Game difficulty level
    private String questionDifficulty;    // Question difficulty level

    // Full constructor
    public Question(String question, List<String> answers, String correctAnswer,
                    String gameDifficulty, String questionDifficulty) {

        this.question = question;
        this.answers = new ArrayList<>(answers);
        this.correctAnswer = correctAnswer;

        this.gameDifficulty = gameDifficulty;
        this.questionDifficulty = questionDifficulty;
    }

    // Backward-compatible constructor (old code still works)
    public Question(String question, List<String> answers,
                    String correctAnswer, String difficulty) {

        this(question, answers, correctAnswer, "Easy", difficulty);
    }

    // ===== Getters & Setters =====

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = new ArrayList<>(answers);
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getGameDifficulty() {
        return gameDifficulty;
    }

    public void setGameDifficulty(String gameDifficulty) {
        this.gameDifficulty = gameDifficulty;
    }

    public String getQuestionDifficulty() {
        return questionDifficulty;
    }

    public void setQuestionDifficulty(String questionDifficulty) {
        this.questionDifficulty = questionDifficulty;
    }

    // Backward compatibility (old getDifficulty() method)
    public String getDifficulty() {
        return questionDifficulty;
    }

    public void setDifficulty(String difficulty) {
        this.questionDifficulty = difficulty;
    }

    @Override
    public String toString() {
        return "Question{" +
                "question='" + question + '\'' +
                ", answers=" + answers +
                ", correctAnswer='" + correctAnswer + '\'' +
                ", gameDifficulty='" + gameDifficulty + '\'' +
                ", questionDifficulty='" + questionDifficulty + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(question);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Question)) return false;
        Question q = (Question) o;
        return Objects.equals(question, q.question);
    }
}
