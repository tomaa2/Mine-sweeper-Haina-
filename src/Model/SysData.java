package Model;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.opencsv.CSVReader;

public class SysData {
	
	//singleton instance
	private static volatile SysData instance;
	
	private final List<Question> questions;
	private final List<GameSummary> games;
	
	
	
	private SysData() {
		this.questions= new ArrayList<>();
		this.games=new ArrayList<>();
		loadQuestionsFromCsv();
	}
	
	// Get Singleton instance
    public static SysData getInstance() {
        if (instance == null) {
            synchronized (SysData.class) {
                if (instance == null) {
                    instance = new SysData();
                }
            }
        }
        return instance;
    }
    
    /*---------------------------------------------question methods------------------------------------------*/
    
    //adding questions avoiding duplicates
//    public boolean addQuestion(Question question) {
//    	if(question == null)
//    		return false;
//    	
//    	//avoiding duplicates
//    	for(Question q : questions) {
//    		if(q.getQuestion().equals(question.getQuestion()))
//    			return false;
//    	}
//    	questions.add(question);
//    	return true;
//    		
//    }
//    
    /**
     * Adds a new question to the system.
     * The function checks the CSV file itself to avoid duplicates.
     *
     * @return true if added successfully, false if the question already exists.
     */
    
    public void loadQuestionsFromCsv() {
    	questions.clear();
    	
    	
    	
    	try (CSVReader reader = new CSVReader(new FileReader("questions.csv"))) {
    		String[] line;

            // Skip header
            reader.readNext();
            while ((line = reader.readNext()) != null) {
            	if (line.length < 8)
                    continue; // Skip invalid row

                String questionText = line[1].trim();
                String difficulty = line[2].trim();

                List<String> answers = new ArrayList<>();
                answers.add(line[3].trim());
                answers.add(line[4].trim());
                answers.add(line[5].trim());
                answers.add(line[6].trim());

                String correctAnswer = line[7].trim();

                // Create Question object
                Question q = new Question(questionText, answers, correctAnswer, difficulty);

                questions.add(q);
            }
            System.out.println("Loaded " + questions.size() + " questions into SysData.");

    	}catch(Exception e) {
    		e.printStackTrace();
    		System.out.println("ERROR: Could not load questions from CSV.");

    	}
    }
    
    
    public boolean addQuestion(Question question) {
        if (question == null) {
            return false;
        }

        String newText = question.getQuestion().trim().toLowerCase();

        // === 1. Load all existing questions from CSV ===
        List<String[]> csvRows = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader("questions.csv"))) {

            String[] line;
            reader.readNext(); // skip header

            while ((line = reader.readNext()) != null) {
                csvRows.add(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // === 2. Check duplicates inside the CSV file ===
        for (String[] row : csvRows) {
            if (row.length < 2) continue;

            String existingText = row[1].trim().toLowerCase(); // column 1 = question text

            if (existingText.equals(newText)) {
                System.out.println("Duplicate found in CSV, not adding: " + newText);
                return false;
            }
        }

        // === 3. If no duplicate → add to SysData list ===
        questions.add(question);
        return true;
    }

    //add more than one question
    public void addQuestions(List<Question> questions) {
    	if(questions!=null)
    		for(Question q : questions)
    			addQuestion(q);
    }
    
    
    public void setQuestions(List<Question> newQuestions) {
    	this.questions.clear();
    	if(newQuestions != null) {
    		this.questions.addAll(newQuestions);
    	}
    }
    
    //remove a question
    public boolean deleteQuestion(String ques) {
    	if (ques == null) return false;

        return questions.removeIf(q -> {
            return q.getQuestion().trim().equalsIgnoreCase(ques.trim());
        });
    }
    
    
    //edit question
    public boolean editQuestion(String oldQues, Question newQues) {
    	if(oldQues == null || newQues == null)
    		return false;
    	
    	for(int i=0; i<questions.size(); i++) {
    		if(questions.get(i).getQuestion().equalsIgnoreCase(oldQues)) {
    			questions.set(i, newQues);
    			return true;
    		}
    	}
    	return false;
    }
    
    //delete all questions from record
    public void deleteAllQuestions() {
    	questions.clear();
    }
    
    //get all questions
    public List<Question> getQuestions(){
    	return Collections.unmodifiableList(questions);
    }
    
    
    //get Random question for question cell
    public Question getRandomQuestion(Set<String> usedQuestions) {	
    	List<Question> available = new ArrayList<>();
    	
    	if(questions.isEmpty()) {
    		System.out.println("SysData.getRandomQuestion(): QUESTIONS LIST EMPTY!");
    		return null;
    	}
    	
    	for (Question q : questions) {
            if (!usedQuestions.contains(q.getQuestion())) {
                available.add(q);
            }
        }

        if (available.isEmpty()) {
            System.out.println("No unused questions available!");
            return null;
        }
    	
    	//System.out.println("SysData.getRandomQuestion(): size = " + available.size());
    	Random rand = new Random();
    	return available.get(rand.nextInt(available.size()));
    }
    
    
    /*---------------------------------------------game methods------------------------------------------*/
    
    //add new game
    public boolean addGame(GameSummary game) {
    	if(game == null)
    		return false;
    	
    	games.add(game);
    	return true;
    }
    
    public void addGames(List<GameSummary> newGames) {
    	if(games == null)
    		return;
    	 games.clear();      
    	    games.addAll(newGames);  
    }
    
    //delete a Game from history
    public boolean deleteGame(Game g) {
    	if(g!=null) 
    		return games.remove(g);
    	return false;
    }
    
    //delete all games from history
    public void deleteAllGames() {
    	games.clear();
    }
    
    //get all games record
    public List<GameSummary> getAllGames() {
    	return Collections.unmodifiableList(games);
    }
    
    public void debugPrintQuestions() {
        System.out.println("===== SysData QUESTIONS =====");
        for (Question q : questions) {
            System.out.println("[" + q.getQuestion() + "]");
        }
        System.out.println("===== END =====");
    }
	
}
