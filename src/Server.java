import java.net.ServerSocket;
import java.util.HashSet;

//Represents the mother server which launches GameController Threads
public class Server {
    private static HashSet<GameController> games = new HashSet<>();
    
    public static void main(String[] args) throws Exception {
    	System.out.println("20 Questions Server");
        System.out.println("Usage: java Server <number of games> <port> ");
        
        int numGames = 1;
        if(args.length > 0) 
        	numGames = Integer.parseInt(args[0]);
        else 
        	System.out.println("Number of games not specified, defaulting to 1");
        
        int port = 53000;
        if(args.length > 1) 
        	port = Integer.parseInt(args[1]);
        else 
        	System.out.println("Port not specified, defaulting to 53000");
        
        Server server = new Server(numGames ,port);
        
    }
    
    private Server(int numGames,int port) throws Exception {
		// TODO Auto-generated method stub
        System.out.println("20 Questions Server is Running");
        System.out.println("Listening on port " + port);
        
        for(int i = 0; i < numGames; ++i) {
	        try {
	          	
	          	ClientPlayer player1 = new ClientPlayer(port);
	          	System.out.println(player1.getName() + " connected to server.");
	          	player1.send("Waiting for an opponent...");
	            ClientPlayer player2 = new ClientPlayer(port);
	            System.out.println(player2.getName() + " connected to server.");
	                
	            GameController game = new GameController(player1, player2);
	            System.out.println("Game between " + player1.getName() + " and " + player2.getName() + " successfully created.");
	            games.add(game);
	            game.start();
	            
	          	
	         } catch(Exception e) {
	                System.out.println("An error occurred while starting game threads");
	         }
        }
	}

	
}
   