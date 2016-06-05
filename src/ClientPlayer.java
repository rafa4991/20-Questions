
import java.net.Socket;


//Class that works as a player object on the server

public class ClientPlayer extends Player{
	private ClientPlayer opponent;
	private HelperSocket helper;
	
	public ClientPlayer(Socket server) throws Exception{
		helper = new HelperSocket(server);
		server.close();
		
		//Initialize Player object
		String name = askUser("What is your username?");
		int money = Integer.parseInt(askUser("How much money would you like to use?"));
		
		this.name = name;
		this.money = money;
		
		
	}
	public void setOpponent(ClientPlayer opponent){
		this.opponent = opponent;
	}
	
	public boolean isConnected() {
		return (helper != null);
	}
	
	//Local method used to send messages through server
	public void send(String message) {
		if(helper != null)
			helper.writeln(message);
	}
	
	//Local method used to ask player questions and receive responses
	public String askUser(String question) {
		String response = new String();
		
		if(helper != null) {
			helper.writeln(question);
			response = helper.readln();
			
			if(response.isEmpty()) {
				System.out.println(getName() + " has disconnected");
				helper = null;
			}
		}
		return response;
	}
	
	
}
