package project;

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
        if(args.length > 0) 
        	port = Integer.parseInt(args[0]);
        else 
        	System.out.println("Port not specified, defaulting to 53000");
        
        ServerSocket server = new ServerSocket(port);
        System.out.println("20 Questions Server is Running");
        System.out.println("Listening on port " + port);
        
        //for(int i = 0; i < numGames; ++i) {
        try {
          	while(true){
            	GameController game = new GameController(server);
            	games.add(game);
            	game.start();
          	}
         } catch(Exception e) {
                System.out.println("An error occurred while starting game threads");
                System.out.println("Closing Server...");
                server.close();
         }
    }
    
    private Server(int port) throws Exception {
		// TODO Auto-generated method stub
    	ServerSocket server = new ServerSocket(port);
        System.out.println("20 Questions Server is Running");
        System.out.println("Listening on port " + port);
        
        //for(int i = 0; i < numGames; ++i) {
        try {
          	while(true){
            	GameController game = new GameController(server);
            	games.add(game);
            	game.start();
          	}
         } catch(Exception e) {
                System.out.println("An error occurred while starting game threads");
                System.out.println("Closing Server...");
                server.close();
         }
	}

	
}
   