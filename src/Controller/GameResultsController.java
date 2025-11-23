package Controller;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import Model.Game;
import Model.GameSummary;
import Model.Question;
import Model.SysData;

public class GameResultsController {
	private static final String RESULTS_FILE_PATH = "src/gamehistory.csv";
	
	
	
	public GameResultsController() {
		
	}
	
	
	
	// Load game history
    public void loadGameHistory() {
    	String filePath =this.RESULTS_FILE_PATH; 
		List<GameSummary> loadedGames = new ArrayList<>();
		
		try(CSVReader reader = new CSVReader(new FileReader(filePath))) {
			String[] line;
			reader.readNext(); //skip header row
			
			while((line = reader.readNext()) != null) {
				String player1 = line[1];
	            String player2 = line[2];
	            String difficulty = line[3];
	            String score = line[4];
	            long durationSeconds = Long.parseLong(line[5]);
	            LocalDateTime startTime = LocalDateTime.parse(line[6]);
	            LocalDateTime endtTime = LocalDateTime.parse(line[7]);

	            
	            GameSummary g = new GameSummary (player1, player2, difficulty, score, durationSeconds,startTime, endtTime);
	            loadedGames.add(g);
			}
			
			SysData.getInstance().deleteAllGames();
			SysData.getInstance().addGames(loadedGames);
			System.out.println("The Questions Loaded Successfully!");
			
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("Failed to load games history!" + e.getMessage());
		}
    }
	
	// Save game history
	    public void saveGameHistory() {
	    	String filePath = this.RESULTS_FILE_PATH;
	    	List<GameSummary> games = SysData.getInstance().getAllGames();
	    	
	    	try(CSVWriter writer = new CSVWriter(new FileWriter(filePath))){
				writer.writeNext(new String[]{
			            "ID", "Question", "Difficulty",
			            "A", "B", "C", "D", "Correct Answer"
			        });
				int id = 1;
				
				for(GameSummary g: games) {
					writer.writeNext(new String[] {
			                String.valueOf(id++),
			                g.getPlayer1(),
			                g.getPlayer2(),
			                g.getDifficulty(),
	                        g.getScore(),
	                        String.valueOf(g.getDurationSeconds()),
	                        g.getStartTime().toString(),
	                        g.getEndTime().toString()
					});
				}
					
				System.out.println("Game History Saved Successfully!");
				
			}catch(Exception e) {
				e.printStackTrace();
				System.out.println("Failed to save the Games!" + e.getMessage());
			}
	    }
	    
	 //add a game result to the history
	    public void addGameHistory(GameSummary summary) {	        
	        SysData.getInstance().addGame(summary);
	        saveGameHistory();
	        System.out.println("Game added to history successfully.");
	        
	    }
	    
	    //to display the all the games
	    public void displayGameHistory() {
	    	loadGameHistory();
	    	List<GameSummary> games = SysData.getInstance().getAllGames();
	    	if(games.isEmpty()) {
	    		 System.out.println("No game history available.");
	    	}else {
	    		System.out.println("\n=== Game History ===");
	            games.forEach(System.out::println);
	    	}
	    }
	    
	    public void resetGameHistory() {
	    	String filePath = this.RESULTS_FILE_PATH;
	    	// 1. Clear in-memory list
	        SysData.getInstance().deleteAllGames(); 
	        SysData.getInstance().addGames(new ArrayList<>());

	        //Overwrite CSV file with only header
	        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {

	            writer.writeNext(new String[]{
	                    "Player1", "Player2", "Difficulty", "Score",
	                    "DurationSeconds", "StartTime", "EndTime"
	            });

	            System.out.println("Game history reset successfully.");

	        } catch (Exception e) {
	            System.out.println("Failed to reset game history: " + e.getMessage());
	        }
	    }
	    
	    
	    

}
