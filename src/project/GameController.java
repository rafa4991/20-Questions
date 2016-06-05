package project;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Iterator;

//Controller Class that handles a single Game of 20 Questions
public class GameController extends Thread{
	private Game game;
	private ClientPlayer player1;
	private ClientPlayer player2;
	private ArrayList<ClientPlayer> players = new ArrayList<ClientPlayer>();
	
	GameController(ServerSocket server) throws Exception{
		
		player1 = new ClientPlayer(server.accept());
        player2 = new ClientPlayer(server.accept());
        /*If we have two players connected then 
          we start the game*/
        if(player1.isConnected() && player2.isConnected()) {
        	player1.setOpponent(player2);
            player2.setOpponent(player1);
        	players.add(player1);
            players.add(player2);
            game.addPlayer(player1);
            game.addPlayer(player2);
            player1.send("You have been connected with: " + player2.getName());
            player2.send("You have been connected with: " + player1.getName());
        } 
        else{
        	System.out.println("Wasnt able to connect clients.");
        }
	}
	
	public void askForBet(){
		for(ClientPlayer player : players){
			player.reset();
			player.send("Waiting on opponent to place bet.");
			try{
				int bet = Integer.parseInt(player.askUser("How much would you like to bet?"));
				if(bet > player.getMoney())
					player.setBet(player.getMoney());
				else
					player.setBet(bet);
				
				player.setMoney(player.getMoney() - player.getBet());

			}catch(Exception e){
				player.send("Opponent provided invalid bet.");
			}
		}
		player1.send(player2.getName() + " bets " + player2.getBet());
		player2.send(player1.getName() + " bets " + player1.getBet());
	}
 
	public boolean isFinished(){
		return true;
	}
    
	public void run() {
		
	}
}
