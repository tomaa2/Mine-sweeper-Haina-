package Controller;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import Model.Question;
import Model.SysData;

public class QuestionController {
	private String csvFilePath;
	
	public QuestionController() {
		this.csvFilePath = "src/questions.csv";
	}
	
	//load the questions from the questions file into the (SysData.questions)
	public void loadQuestion(String filePath) {
		this.csvFilePath = filePath;
		List<Question> loadedQuestions = new ArrayList<>();
		
		try(CSVReader reader = new CSVReader(new FileReader(filePath))) {
			String[] line;
			reader.readNext(); //skip header row
			
			while((line = reader.readNext()) != null) {
				String questionText = line[1];
	            String difficulty    = line[2];
	            List<String> answers = new ArrayList<>();
	            answers.add(line[3]); // A
	            answers.add(line[4]); // B
	            answers.add(line[5]); // C
	            answers.add(line[6]); // D

	            String correctAnswer = line[7]; //correct answer
	            
	            Question q = new Question (questionText, answers, correctAnswer, difficulty);
	            loadedQuestions.add(q);
			}
			
            SysData.getInstance().setQuestions(loadedQuestions);
            System.out.println("The Questions Loaded Successfully!");
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("Failed to load the questions!" + e.getMessage());
		}
	}
	
	//saves the questions from the system (the game) into the file (questions.csv)
	public void saveQuestions() {
		String filePath = this.csvFilePath;
		List<Question> questions = SysData.getInstance().getQuestions();
		
		try(CSVWriter writer = new CSVWriter(new FileWriter(filePath))){
			writer.writeNext(new String[]{
		            "ID", "Question", "Difficulty",
		            "A", "B", "C", "D", "Correct Answer"
		        });
			int id = 1;
			
			for(Question q: questions) {
				writer.writeNext(new String[] {
		                String.valueOf(id++),
		                q.getQuestion(),
		                q.getDifficulty(),
		                q.getAnswers().get(0),
		                q.getAnswers().get(1),
		                q.getAnswers().get(2),
		                q.getAnswers().get(3),
		                q.getCorrectAnswer()
				});
			}
				
			System.out.println("The Questions Saved Successfully!");
			
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("Failed to save the questions!" + e.getMessage());
		}
	}
	//
	//the loadQuestions method and the saveQuestions, each one does the opposite operation
	//of the second. loadQuestions gets the question from "Questions.csv" file into the system
	//saveQuestions take the questions from the system and saves them into the "Questions.csv" file.
	//
	
	//to display the questions into the question bank interface
	public void displayQuestions() {
		List<Question> questions = SysData.getInstance().getQuestions();
		if(questions.isEmpty()) {
			System.out.println("No questions available.");
		}else {
			questions.forEach(System.out::println);
		}
	}
	
	public void addQuestion(Scanner scanner) {
		System.out.println("Enter The Question");
		String questText = scanner.nextLine().trim();
		if(questText.isEmpty()) {
			 System.out.println("Question text cannot be empty.");
	            return;
		}
		
		System.out.println("Enter Answers (comma-separted):");
		List<String> answers = Arrays.asList(scanner.nextLine().split(","));
		if (answers.isEmpty() || answers.size() < 4) {
            System.out.println("There must be 4 answers answers.");
            return;
        }
		
		System.out.println("Enter the correct Answer:");
		String correctAnswer = scanner.nextLine().trim();
        if (!answers.contains(correctAnswer)) {
            System.out.println("The correct answer must be one of the provided answers.");
            return;
        }
        
        System.out.print("Enter difficulty (Easy(1), Medium(2), Hard(3), Expert(4): ");
        String difficulty = scanner.nextLine().trim().toUpperCase();
        if (!Arrays.asList("1", "2", "3", "4").contains(difficulty)) { // Replace List.of with Arrays.asList
            System.out.println("Invalid difficulty level.");
            return;
        }
        
        Question newQues = new Question (questText, answers, correctAnswer, difficulty);
        
        if (SysData.getInstance().addQuestion(newQues)) {
            System.out.println("Question added successfully!");
        } else {
            System.out.println("Failed to add question (duplicate or invalid).");
        }   
	}
	
	//to edit any existing question
	public void editQuestion(Scanner scanner) {
		 System.out.print("Enter the text of the question to edit: ");
	        String oldQuestionText = scanner.nextLine().trim();

	        if (SysData.getInstance().getQuestions().stream().noneMatch(q -> q.getQuestion().equalsIgnoreCase(oldQuestionText))) {
	            System.out.println("Question not found.");
	            return;
	        }

	        System.out.print("Enter new question text: ");
	        String newQuestionText = scanner.nextLine().trim();

	        System.out.print("Enter new answers (comma-separated): ");
	        List<String> newAnswers = Arrays.asList(scanner.nextLine().split(","));

	        System.out.print("Enter new correct answer: ");
	        String newCorrectAnswer = scanner.nextLine().trim();

	        System.out.print("Enter new difficulty (Easy, Medium, Hard): ");
	        String newDifficulty = scanner.nextLine().trim().toUpperCase();

	        Question updatedQuestion = new Question(newQuestionText, newAnswers, newCorrectAnswer, newDifficulty);

	        if (SysData.getInstance().editQuestion(oldQuestionText, updatedQuestion)) {
	            System.out.println("Question updated successfully!");
	        } else {
	            System.out.println("Failed to update question (invalid data).");
	        }
	}
	
	//to delete any one of the questions
	public void deleteQuestion(Scanner scanner) {
        System.out.print("Enter the text of the question to delete: ");
        String questionText = scanner.nextLine().trim();

        if (SysData.getInstance().deleteQuestion(questionText)) {
            System.out.println("Question deleted successfully!");
        } else {
            System.out.println("Question not found.");
        }
    }
	
}
