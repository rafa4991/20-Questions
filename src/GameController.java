import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Iterator;

//Controller Class that handles a single thread game of 20 Questions
public class GameController extends Thread {
	boolean finished = false;
	boolean stop = false;
	private Game game;
	private ClientPlayer player1;
	private ClientPlayer player2;
	private ClientPlayer winner;
	private ClientPlayer loser;
	private int rounds = 0;
	//private boolean tie = false;
	private boolean rematch = false;
	private boolean dilemma = false;
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
				player.send("Your current allowance: " + player.getMoney() + "\n");
				int bet = Integer.parseInt(player.askUser("How much would you like to bet?"));
				player.send("\n");
				if(bet > player.getMoney()){
					player.send("Exceeded allowance. Defaulting to allowance total.");
					player.setBet(player.getMoney());
				}
				else
					player.setBet(bet);
				
				//player.setMoney(player.getMoney() - player.getBet());
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
		player2.send("Waiting on opponent to input their mystery word...\n");
		for(ClientPlayer player : players){
			try{
				String answer = player.askUser("What is the word you would like your opponent to guess?");
				player.setAnswer(answer);
				
				if(player.isTurn()){
					player.send("Waiting on opponent to input their mystery word...\n");
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
		//System.out.println("TEST start of playGame function");
		while(!finished){
			for(ClientPlayer player : players){
				if(player.isQuestioner()){
					if(rounds == 1){
						player.send("First round finished. Next round!");
						player.send("Your turn to guess your opponent's word!\n");
					}
					
					if(player.isTurn()){
						//System.out.println("TEST before askOptions for " + player.getName());
						askOptions(player);
						if(rounds >= 2){
							//System.out.println("TEST Game finished");
							finished = true;
						}
					}
					else{
						player.send("Still waiting for opponent to answer question...\n");
					}
				}
				else{
					if(player.isTurn()){
						//System.out.println("TEST before askQuestion for " + player.getName());
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
		//System.out.println("TEST start of askOptions method");
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
				player.send("\n");
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
						player.send("Waiting on opponent to ask question or make a guess...\n");
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
	//(COMPLETE)Method that determines the winner and loser and does specific logic off of it.
	public void determineWinner(){
		String border = "***********************";
		player1.send(border);
		player2.send(border);
		
		int player1Score = player1.getScore();
		int player2Score = player2.getScore();
		
		String message = "With the final scores being-\n"
				+ player1.getName() + ": " + player1.getScore() + "\n"
				+ player2.getName() + ": " + player2.getScore() + "\n\n";
		
		player1.send(message);
		player2.send(message);
		
		if(player1Score > player2Score){
			winner = player1;
			loser = player2;
			
		}
		else if(player1Score < player2Score){
			winner = player2;
			loser = player1;
		}
		else{
			//tie = true;
			message = "You are tied! This is awkward.\n";
			player1.send(message);
			player2.send(message);
			gameTied();
			return;
		}
		
		winner.send("You have won the game! Good job, champ! ");
		int earnings = winner.getBet()*2;
		winner.setMoney(earnings + winner.getMoney()); 
		winner.send("You have won your bet at " + winner.getBet());
		winner.send("You now have an allowance of " + winner.getMoney() + "\n\n");
		
		loser.send("You have lost the game. Sorry, maybe next time! ");
		int loses = loser.getBet();
		loser.setMoney(loser.getMoney() - loses);
		loser.send("You have lost your bet of " + loser.getBet());
		loser.send("You now have an allowance of " + loser.getMoney() + "\n\n");
	}
	//In the case of a tie, this method gives the players the decision to settle with the tie or play a sudden death game
	public void gameTied(){
		for(ClientPlayer player : players){
			player.send("Since the game is tied you have the option of playing a game of Prisoner's Dilemma.\n");
			player.send("#################################################");
			player.send("------------The rules of Prisoner's Dilemma-------------");
			player.send("(1)Each player will bet half the lowest bet amongst each other.");
			player.send("     a.For instance, if Player 1 betted 100 and Player 2 betted 50, then both will bet 25.");
			player.send("(2)Both players will be asked if they want to STEAL or SPLIT their bets. ");
			player.send("(3)If both players pick SPLIT, then both WIN a reward of DOUBLE their bets.");
			player.send("(4)If one player picks SPLIT and the other picks STEAL:");
			player.send("     a.The stealer wins QUADRUPLE their bet.");
			player.send("     b.The spliter loses their bet.");
			player.send("(3)If both players pick STEAL, then both LOSE DOUBLE their bets. ");
			player.send("#################################################\n\n");
		}
		String message = "For there to be a game of Prisoner's Dilemma, both players must agree.\n";
		player1.send(message);
		player2.send(message);
		
		player2.send("Waiting on opponent to make choice...");
		String player1Choice = player1.askUser("Would you like to play Prisoner's Dilemma(yes/no)?");
		player1.send("Waiting on opponent to make choice...");
		String player2Choice = player2.askUser("Would you like to play Prisoner's Dilemma(yes/no)?");
		if(player1Choice.equalsIgnoreCase("yes") && player2Choice.equalsIgnoreCase("yes")){
			dilemma = true;
			message = "You both agreed to play Prisoner's Dilemma!";
			player1.send(message);
			player2.send(message);
		}else{
			dilemma = false;
			message = "Sorry, someone didn't want to play Prisoner's Dilemma. Maybe next time!";
			player1.send(message);
			player2.send(message);
		}
		
	}
	//
	public void playPrisonerDilemma(){
		
		int newBet = 0;
		if(player1.getBet() >= player2.getBet())
			newBet = player2.getBet()/2;
		else
			newBet = player1.getBet()/2;
		player1.setBet(newBet);
		player2.setBet(newBet);
		
		String message = "According to the bets made before, each player will be betting: " + newBet + "\n";
		player1.send(message);
		player2.send(message);
		
		player2.send("Waiting on opponent to make decision...\n");
		String player1Answer = player1.askUser("Would you like to SPLIT or STEAL?");
		player1.send("Waiting on opponent to make decision...\n");
		String player2Answer = player2.askUser("Would you like to SPLIT or STEAL?");
		
		message = "\nBoth answers are in!\n";
		player1.send(message);
		player2.send(message);
		
		//Game logic for player choice combinations
		if(player1Answer.equalsIgnoreCase("SPLIT") && player2Answer.equalsIgnoreCase("SPLIT")){
			message = "You both picked SPLIT!\n";
			for(ClientPlayer player : players){
				player.send(message);
				int earnings = player.getBet() * 2;
				player.setMoney(player.getMoney() + earnings);
				player.send("You both won double your bets with a total of: " + earnings);
				player.send("You now have an allowance of: " + player.getMoney() + "\n\n");
			}
		}else if(player1Answer.equalsIgnoreCase("STEAL") && player2Answer.equalsIgnoreCase("STEAL")){
			message = "You both picked STEAL!\n";
			for(ClientPlayer player : players){
				player.send(message);
				int loses = player.getBet();
				player.setMoney(player.getMoney() - loses);
				player.send("You both lost your bets of: " + loses);
				player.send("You now have an allowance of: " + player.getMoney() + "\n\n");
			}
		}else if(player1Answer.equalsIgnoreCase("SPLIT") && player2Answer.equalsIgnoreCase("STEAL")){
			player1.send("You picked SPLIT!\nYour opponent picked STEAL!\n");
			player1.send("Sorry, but that means you lose your bet of: " + player1.getBet());
			player1.setMoney(player1.getMoney() - player1.getBet());
			player1.send("You now have an allowance of: " + player1.getMoney() + "\n\n");
			
			player2.send("You picked STEAL!\nYour opponent picked SPLIT!\n");
			int earnings = player2.getBet()*4;
			player2.send("That means you won 4 times your bet of: " + player2.getBet());
			player2.send("You won " + earnings + " in total!");
			player2.setMoney(player2.getMoney() + earnings);
			player2.send("You now have an allowance of: " + player2.getMoney() + "\n\n");
			
		}else if(player2Answer.equalsIgnoreCase("SPLIT") && player1Answer.equalsIgnoreCase("STEAL")){
			player2.send("You picked SPLIT!\nYour opponent picked STEAL!\n");
			player2.send("Sorry, but that means you lose your bet of: " + player2.getBet());
			player2.setMoney(player2.getMoney() - player2.getBet());
			player2.send("You now have an allowance of: " + player2.getMoney() + "\n\n");
			
			player1.send("You picked STEAL!\nYour opponent picked SPLIT!\n");
			int earnings = player2.getBet()*4;
			player1.send("That means you won 4 times your bet of: " + player1.getBet());
			player1.send("You won " + earnings + " in total!");
			player1.setMoney(player1.getMoney() + earnings);
			player1.send("You now have an allowance of: " + player1.getMoney() + "\n\n");
		}else{
			for(ClientPlayer player : players){
				player.send("Sorry. Someone put invalid input.");
				player.send("Game has been cancelled and no money was lost or gained.\n");
			}
		}
		
		for(ClientPlayer player : players){
			player.send("Thanks for playing Prisoner's Dilemma! Come again.\n");
			player.send("#################################################\n\n");
		}
		
	}
	
	//(COMPLETE)Function that asks players after the match if they would like a re-match.
	public void askForRematch(){
		String message = "For there to be a rematch, both players must agree.";
		player2.send("Waiting on opponent to make choice...");
		String player1Rematch = player1.askUser("Would you like a rematch(yes/no)?");
		player1.send("Waiting on opponent to make choice...");
		String player2Rematch = player2.askUser("Would you like a rematch(yes/no)?");
		
		if(player1Rematch.equalsIgnoreCase("yes") && player2Rematch.equalsIgnoreCase("yes")){
			rematch = true;
			message = "You both agreed to a rematch!";
			player1.send(message);
			player2.send(message);
		}else{
			rematch = false;
			message = "Sorry, someone didn't want a rematch. Maybe next time!";
			player1.send(message);
			player2.send(message);
			finished = true;
		}
	}
	public void goodbye(){
		for(ClientPlayer player : players){
			player.send("Thank for playing 20 Questions!\n");
			player.send("See you next time!\n");
		}
	}
	
    @Override
	public void run() {
    	while(!stop){
    		rules();
    		askForBet();
    		askForAnswer();
    		playGame();
    		player1.send("Game Complete!");
    		player2.send("Game Complete!");
    		player1.send("-------------\n");
    		player2.send("-------------\n");
    		determineWinner();
    		if(dilemma){
    			playPrisonerDilemma();
    		}
    		askForRematch();
    		if(rematch) {
    			player1.reset();
    			player2.reset();
    			player1.setTurn(true);
    			player1.setQuestioner(true);
    			player2.setTurn(false);
    			player2.setQuestioner(false);
    			finished = false;
    			dilemma = false;
    			continue;
    		}
    		stop = true;
    		goodbye();
    		player1.close();
    		player2.close();
    	}

	}
}
