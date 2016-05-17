//Master Controller class that rules over all of the games and players. It functions like a server.

import java.util.HashSet;

public class Controller {
	private HashSet<Player> players = new HashSet<>();
	private HashSet<Game> games = new HashSet<>();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("20 Questions Server");
        System.out.println("Usage: java Controller <port> ");
        
        int port = 53000;
        if(args.length > 0) 
        	port = Integer.parseInt(args[0]);
        else 
        	System.out.println("Port not specified, defaulting to 53000");
        
        Controller controller = new Controller(port);
	}
	
	Controller(int port){
		System.out.println("Listening on port " + port);
	}

}
