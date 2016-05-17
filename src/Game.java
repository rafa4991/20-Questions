//Class that handles individual games and their components

import java.util.*;

public class Game {
	private String id = "";
	private ArrayList<Player> players = new ArrayList<Player>();
	private ArrayList<String> history = new ArrayList<String>();
	
	//Constructor
	public Game(String id){
		this.id = id;
	}
	
	//GET Methods
	public String getId(){
		return id;
	}
	public ArrayList<Player> getPlayers(){
		return players;
	}
	public ArrayList<String> getHistory(){
		return history;
	}
	
	//SET Methods
	public void addPlayer(Player player){
		if(players.size()< 2){
			players.add(player);
		}
	}
	public void addHistory(String history){
		this.history.add(history);
	}
	

}
