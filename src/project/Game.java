package project;

//Class that handles individual games and their components

import java.util.*;

public class Game {
	private String id = "";
	private ArrayList<ClientPlayer> players = new ArrayList<ClientPlayer>();
	private ArrayList<String> history = new ArrayList<String>();
	
	//Constructor
	public Game(){
		//this.id = id;
	}
	
	//GET Methods
	public String getId(){
		return id;
	}
	public ArrayList<ClientPlayer> getPlayers(){
		return players;
	}
	public ArrayList<String> getHistory(){
		return history;
	}
	
	//SET Methods
	public void setID(String id){
		this.id = id;
	}
	public void addPlayer(ClientPlayer player){
		if(players.size()< 2){
			players.add(player);
		}
	}
	public void addHistory(String history){
		this.history.add(history);
	}
	
	//Game Methods
	public boolean ended(){
		Player player1 = players.get(0);
		Player player2 = players.get(1);
		boolean player1Won = player1.getLatestGuess().equalsIgnoreCase(player2.getAnswer());
		boolean player2Won = player2.getLatestGuess().equalsIgnoreCase(player1.getAnswer());
		if(player1Won || player2Won)
			return true;
		else
			return false;
	}
	
	public ClientPlayer getWinner(){
		Player player1 = players.get(0);
		Player player2 = players.get(1);
		
		if(player1.getLatestGuess().equalsIgnoreCase(player2.getAnswer())){
			return players.get(0);
		}
		else{
			return players.get(1);
		}
		
		
	}
	

}
