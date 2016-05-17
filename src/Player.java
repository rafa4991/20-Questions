import java.util.*;

public class Player {
	private int bet = 0;
	private String name = "";
	private String answer = "";
	private ArrayList<String> questions = new ArrayList<String>();
	private ArrayList<String> responses = new ArrayList<String>();
	private ArrayList<String> guesses = new ArrayList<String>();
	
	//Constructor
	public Player(String name, int bet, String answer){
		this.name = name;
		this.bet = bet;
		this.answer = answer;
	}
	
	//GET Methods
	public int getBet(){
		return bet;
	}
	public String getName(){
		return name;
	}
	public String getAnswer(){
		 return answer;
	} 
	public ArrayList<String> getQuestions(){
		 return questions;
	}
	public ArrayList<String> getResponses(){
		 return responses;
	}
	public ArrayList<String> getGuesses(){
		 return guesses;
	}
	
	//SET Methods
	public void setBet(int bet){
		this.bet = bet;
	}
	public void setAnswer(String answer){
		this.answer = answer;
	}
	public void question(String question){
		questions.add(question);
	}
	public void respond(String response){
		responses.add(response);
	}
	public void guess(String guess){
		guesses.add(guess);
	}
	
	public int getEarnings(){
		return this.bet * 2;
	}
	
	//Resets players 
	public void reset(){
		this.bet = 0;
		this.answer = "";
		this.questions.clear();
		this.responses.clear();
		this.guesses.clear();
	}
	
	
	

}
