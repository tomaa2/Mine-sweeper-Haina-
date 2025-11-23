package Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SysData {
	
	//singleton instance
	private static volatile SysData instance;
	
	private final List<Question> questions;
	private final List<GameSummary> games;
	
	
	private SysData() {
		this.questions= new ArrayList<>();
		this.games=new ArrayList<>();
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
    public boolean addQuestion(Question question) {
    	if(question == null)
    		return false;
    	
    	//avoiding duplicates
    	for(Question q : questions) {
    		if(q.getQuestion().equals(question.getQuestion()))
    			return false;
    	}
    	questions.add(question);
    	return true;
    		
    }
    
    //add more than one question
    public void addQuestions(List<Question> questions) {
    	if(questions!=null)
    		for(Question q : questions)
    			addQuestion(q);
    }
    
    
    public void setQuestions(List<Question> questions) {
    	questions.clear();
    	questions.addAll(questions);
    }
    
    //remove a question
    public boolean deleteQuestion(String ques) {
    	if(ques!=null) {
    		return questions.removeIf(q -> q.getQuestion().equalsIgnoreCase(ques));
    	}
    	return false;
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
    public Question getRandomQuestion() {
    	if(questions.isEmpty())
    		return null;
    	
    	Random rand = new Random();
    	return questions.get(rand.nextInt(questions.size()));
    }
    /*---------------------------------------------game methods------------------------------------------*/
    
    //add new game
    public boolean addGame(GameSummary game) {
    	if(game == null)
    		return false;
    	
    	games.add(game);
    	return true;
    }
    
    public void addGames(List<GameSummary> games) {
    	if(games == null)
    		return;
    	for(GameSummary g : games) {
    		games.add(g);
    	}
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
    
    
	
}
