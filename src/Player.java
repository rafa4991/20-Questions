import java.util.ArrayList;

public class Player{
	
	protected int money = 0;
	protected int bet = 0;
	protected int score = 100;
	protected String name = "";
	protected String answer = "";
	protected ArrayList<String> questions = new ArrayList<String>();
	protected ArrayList<String> responses = new ArrayList<String>();
	protected ArrayList<String> guesses = new ArrayList<String>();
	private ArrayList<String> history = new ArrayList<String>();
	
	/*Constructor
	public Player(String name, int money){
		this.name = name;
		this.money = money;
	}
	*/
	//GET Methods
	public int getScore(){
		return score;
	}
	public int getMoney(){
		return money;
	}
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
	public ArrayList<String> getHistory(){
		return history;
	}
	public String getLatestQuestion(){
		return questions.get(questions.size() - 1);
	}
	public String getLatestResponse(){
		return responses.get(responses.size() - 1);
	}
	public String getLatestGuess(){
		return guesses.get(guesses.size() - 1);
	}
	
	//SET Methods
	public void setScore(int score){
		this.score = score;
	}
	public void setMoney(int money){
		this.money = money;
	}
	public void setBet(int bet){
		this.bet = bet;
	}
	public void setAnswer(String answer){
		this.answer = answer;
	}
	public void addQuestion(String question){
		questions.add(question);
	}
	public void addResponse(String response){
		responses.add(response);
	}
	public void addGuess(String guess){
		guesses.add(guess);
	}
	public void addHistory(String log){
		history.add(log);
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

