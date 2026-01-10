package minesweepertest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Model.Question;
import Model.SysData;

public class QuestionTest {
	//test if deleting a questions works good
	@Test
		void testDeleteQuestion() {
			SysData sys = SysData.getInstance();
		    sys.deleteAllQuestions();
		    sys.loadQuestionsFromCsv();

		    int sizeBefore = sys.getQuestions().size();
		    String quesText = sys.getQuestions().get(0).getQuestion();

		    // Act
		    boolean deleted = sys.deleteQuestion(quesText);

		   
		    // Assert
		    assertTrue(deleted);
		    assertEquals(sizeBefore - 1, sys.getQuestions().size());
		    
		    
		    assertFalse(
		            sys.getQuestions().stream()
		                .anyMatch(q -> q.getQuestion().equalsIgnoreCase(quesText))
		        );
		}
}
