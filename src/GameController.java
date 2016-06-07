import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Iterator;

//Controller Class that handles a single thread game of 20 Questions
public class GameController extends Thread {
	volatile boolean finished = false;
	private Game game;
	private ClientPlayer player1;
	private ClientPlayer player2;
	private ClientPlayer winner;
	private ClientPlayer loser;
	private int rounds = 0;
	private ArrayList<ClientPlayer> players = new ArrayList<ClientPlayer>();
	
	//(COMPLETE) Constructor of GameController
	GameController(ClientPlayer p1 , ClientPlayer p2) throws Exception{
		player1 = p1;
		player2 = p2;
		player1.setTurn(true);
		player1.setQuestioner(true);
        /*If we have two players connected then 
          we start the game*/
        if(player1.isConnected() && player2.isConnected()) {
        	
        	player1.setOpponent(player2);
            player2.setOpponent(player1);
        	players.add(player1);
            players.add(player2);
            //game.addPlayer(player1);
            //game.addPlayer(player2);
            player1.send("You have been connected with: " + player2.getName() + "\n");
            player2.send("You have been connected with: " + player1.getName() + "\n");
            System.out.println("Connected " + player1.getName() + " and " + player2.getName() + ".\n");
        } 
        else{
        	System.out.println("Wasnt able to connect clients.");
        }
	}
	
	//(COMPLETE) Method that displays the rules of the game to both players
	public void rules(){
		for(ClientPlayer player : players){
			player.send("-------------------------------------------------");
			player.send("------------The rules of 20Questions-------------");
			player.send("(1)Play two games for each player");
			player.send("   a.Play a game as guesser.");
			player.send("   b.Play a game as answerer.");
			player.send("(2)Players start with 100 points each.");
			player.send("(3)Each question asked is -2 points.");
			player.send("(4)Each wrong guess is -2 points.");
			player.send("(3)Player with most points after both games wins.");
			player.send("-------------------------------------------------\n\n");
		}
	}
	
	//(COMPLETE) Method that asks player how much they would like to bet.
	public void askForBet(){
		player2.send("Waiting on opponent to make bet...\n");
		for(ClientPlayer player : players){
			player.reset();
			try{
				int bet = Integer.parseInt(player.askUser("How much would you like to bet?"));
				player.send("\n");
				if(bet > player.getMoney()){
					player.send("Exceeded allowance. Defaulting to allowance total.");
					player.setBet(player.getMoney());
				}
				else
					player.setBet(bet);
				
				player.setMoney(player.getMoney() - player.getBet());
				if(player.isTurn()){
					player.send("Waiting on opponent to make bet...");
				}
			}catch(Exception e){
				player.send("Invalid bet.");
			}
			
			
		}
		player1.send(player2.getName() + " bets " + player2.getBet() + ".\n");
		player2.send(player1.getName() + " bets " + player1.getBet() + ".\n");
	}
	
	//(COMPLETE) Method that asks for the answer that players want their opponent to guess
	public void askForAnswer(){
		player2.send("Waiting on opponent to input their mystery word...");
		for(ClientPlayer player : players){
			try{
				String answer = player.askUser("What is the word you would like your opponent to guess?");
				player.setAnswer(answer);
				
				if(player.isTurn()){
					player.send("Waiting on opponent to input their mystery word...");
				}
			}catch(Exception e){
				player.send("Invalid word.");
			}
			
			
		}
		player1.send("Both answers are in.\n");
		player2.send("Both answers are in.\n");
	}
	
	//Starts game loop that runs until a winner is decided
	public void playGame(){
		player1.send("GAME START!\n");
		player2.send("GAME START!\n");
		player2.send("Waiting on opponent to ask question or make a guess...\n");
		System.out.println("TEST start of playGame function");
		while(!finished){
			for(ClientPlayer player : players){
				if(player.isQuestioner()){
					if(rounds == 1){
						player.send("First round finished. Next round!");
						player.send("Your turn to guess your opponent's word!");
					}
					
					if(player.isTurn()){
						System.out.println("TEST before askOptions for " + player.getName());
						askOptions(player);
						if(rounds >= 2){
							System.out.println("TEST Game finished");
							finished = true;
						}
					}
					else{
						player.send("Still waiting for opponent to answer question...\n");
					}
				}
				else{
					if(player.isTurn()){
						System.out.println("TEST before askQuestion for " + player.getName());
						askQuestion(player);
					}
					else{
						player.send("Still waiting on opponent to ask question or make a guess...\n");
					}
				}
			}
		}
	}
	
	//Method that asks player what option they would like to use and does desired functionality.
	public void askOptions(ClientPlayer player){
		//boolean stillSelecting = true;
		System.out.println("TEST start of askOptions method");
		while(true){
			if(!getOpponentResponse().equals("")){
				player.send("********");
				player.send("Last Question and Response:");
				player.send(player.getLatestQuestion() + " " + getOpponentResponse());
				player.send("********");
				player.send("\n");
				String historyLine = "Response: " + getOpponentResponse();
				boolean recorded = false;
				for(String line : player.getHistory()){
					if(line.equalsIgnoreCase(historyLine))
						recorded = true;
				}
				if(!recorded)
					player.addHistory(historyLine);
			}
			player.send("Current Score: " + player.getScore());
			player.send("[a] Make a guess.");
			player.send("[b] Ask a question.");
			player.send("[c] Check history.\n");
			String option =  player.askUser("What option do you choose(type a, b or c)?");
			player.send("\n");
			if(option.equalsIgnoreCase("a")){
				String guess = player.askUser("What is your guess?");
				player.addGuess(guess);
				player.addHistory("Made guess: " + guess);
				if(isGuessCorrect(guess)){
					//stillSelecting = false;
					player.send("You guessed correct! Great job!");
					player.send("You finished with a score of " + player.getScore() + ".\n");
					rounds = rounds + 1;
					if(rounds < 2){
						player.send("First round finished. Next round!");
						player.send("Your opponent will now try to figure out your word!\n");
						player2.send("Waiting on opponent to ask question or make a guess...\n");
						nextTurn();
						nextGame();
					}
					break;
				}
				player.setScore(player.getScore() - 2);
				player.send("Sorry. Your guess was wrong.");
				player.send("2 points have been deducted from your score.\n");
				
			}else if(option.equalsIgnoreCase("b")){
				String question = player.askUser("What is your question(must end in '?')?");
				player.setScore(player.getScore() - 2);
				player.addQuestion(question);
				player.addHistory("Asked question: '" + question + "'");
				player.send("Waiting for opponent to answer question...\n");
				nextTurn();
				break;
				
			}else if(option.equalsIgnoreCase("c")) {
				player.send("Here is your history.");
				for(String line : player.getHistory()){
					player.send(">" + line);
				}
				player.send("End of history.\n");
			}else{
				player.send("Invalid choice. Try again.");
			}
		}
	}
	
	//(COMPLETE) Method that asks the non-questioner user the question the other user gave him.
	public void askQuestion(ClientPlayer player){
		player.send("Here is the question your opponent asked.");
		player.send("Please answer with 'yes' or 'no'.");
		String response = player.askUser(getOpponentQuestion());
		player.addResponse(response);
		player.send("Waiting on opponent to ask question or make a guess...\n");
		nextTurn();
	}
	
	//(COMPLETE) Method that return the latest response the uesr's opponent made.
	public String getOpponentResponse(){
		String response = "";
		for(ClientPlayer player : players){
			if(!player.isQuestioner()){
				if(!player.getResponses().isEmpty())
					response = player.getLatestResponse();
			}
		}
		return response;
	}
	
	//(COMPLETE) Method that returns the latest question the user's opponent asked.
	public String getOpponentQuestion(){
		String question = "";
		for(ClientPlayer player : players){
			if(player.isQuestioner()){
				question = player.getLatestQuestion();
			}
		}
		return question;
	}
	
	//(COMPLETE) Method that checks if user's guess is correct 
	public boolean isGuessCorrect(String guess){
		boolean isCorrect = false;
		for(ClientPlayer player : players){
			if(!player.isQuestioner()){
				isCorrect = guess.equalsIgnoreCase(player.getAnswer());
			}
		}
		return isCorrect;
	}
	
	//(COMPLETE)Method that shifts game to the next turn.
	public void nextTurn(){
		player1.setTurn(!player1.isTurn());
		player2.setTurn(!player2.isTurn());
	}
	//(COMPLETE)Method that shifts game to the next game.
		public void nextGame(){
			player1.setQuestioner(!player1.isQuestioner());
			player2.setQuestioner(!player2.isQuestioner());
		}
 
	//(COMPLETE)Method that returns in the game is finished or not
	public boolean isFinished(){
		return finished;
	}
	
    @Override
	public void run() {
    	while(!finished){
    		rules();
    		askForBet();
    		askForAnswer();
    		playGame();
    		player1.send("Game Complete!");
    		player2.send("Game Complete!");
    		player1.send("-------------\n");
    		player2.send("-------------\n");
    		player1.close();
    		player2.close();
    		//finished = true;
    		System.out.println("TESTING");
    	}

	}
}
