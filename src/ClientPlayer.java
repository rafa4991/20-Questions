
import java.net.ServerSocket;
import java.net.Socket;


//Class that works as a player object on the server

public class ClientPlayer extends Player{
	private ClientPlayer opponent;
	private HelperSocket helper;
	private boolean isTurn = false;
	private boolean isQuestioner = false;
	
	public ClientPlayer(int port) throws Exception{
		ServerSocket server = new ServerSocket(port);
		helper = new HelperSocket(server.accept());
		server.close();
		send("\n");
		//Initialize Player object
		String name = askUser("What is your username?");
		send("\n");
		int money = Integer.parseInt(askUser("How much money would you like to use?"));
		send("\n");
		this.name = name;
		this.money = money;
		
		
	}
	public boolean isTurn(){
		return isTurn;
	}
	public void setTurn(boolean isTurn){
		this.isTurn = isTurn;
	}
	public boolean isQuestioner(){
		return isQuestioner;
	}
	public void setQuestioner(boolean isQuestioner){
		this.isQuestioner = isQuestioner;
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
	
	public void close(){
		helper.close();
	}
	
	
}
