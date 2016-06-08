import java.net.Socket;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class Client {
	private HelperSocket helper = null;
	private BufferedReader stdin;
	
	public static void main(String[] args){
		System.out.println("20 Questions Client");
		System.out.println("Usage: java Client <host> <port>");
		
		String host = "127.0.0.1";
		if(args.length > 0){ 
			host = args[0];
		}		
		else{
			System.out.println("Host address not specified, defaulting to localhost");
		}
		
		int port = 53000;
		if(args.length > 1){
			port = Integer.parseInt(args[1]); 
		}
		else{
			System.out.println("Port not specified, defaulting to port 53000");
		}
		
		//while(true){
			//System.out.println("TEST");
			try{
				Client client = new Client(host, port);
				client.start();
				
			} catch(Exception e){
				System.out.println("Connection to server failed. Retrying...");
			}
			try {
				Thread.sleep(2000);
			} catch (Exception e) {
				
			}
		//}
	}
	//Create the client socket and the input/output gateways
	Client(String host, int port) throws Exception{
		helper = new HelperSocket(new Socket(host,port));
		stdin = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Connected to 20 Questions Server");
	}
	
	void start() throws Exception{
		while(true){
			String response = helper.readln();
			System.out.println(response);
			
			if(response.endsWith("?")){
				helper.writeln(stdin.readLine());
			}
			if(response.equalsIgnoreCase("See you next time!")){
				break;
			}
		}
	}
	

}
