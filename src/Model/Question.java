package Model;

import java.util.*;

public class Question {
	
	
	private String question;
	private List<String> answers;
	private String correctAnswer;
	private String difficulty;
	
	public Question(String question, List<String> answers, String correctAnswer, String difficulty) {
		this.question=question;
		this.answers=answers;
		this.correctAnswer=correctAnswer;
		this.difficulty=difficulty;
		
	}

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
		this.answers = answers;
	}

	public String getCorrectAnswer() {
		return correctAnswer;
	}

	public void setCorrectAnswer(String correctAnswer) {
		this.correctAnswer = correctAnswer;
	}

	public String getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(String difficulty) {
		this.difficulty = difficulty;
	}

	@Override
	public String toString() {
		return "Question [question=" + question + ", answers=" + answers + ", correctAnswer=" + correctAnswer
				+ ", difficulty=" + difficulty + "]";
	}
	
	
}
