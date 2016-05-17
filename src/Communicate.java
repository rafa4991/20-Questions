import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

//Communication class that holds the sockets
public class Communicate {
	private Socket socket;
	private BufferedReader input;
	private PrintWriter output;
	
	Communicate(Socket socket) throws Exception{
		this.socket = socket;
		input = new BufferedReader( new InputStreamReader(socket.getInputStream()));
		output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
	}
	
	public String readline() throws IOException{
		String line = "";
		
		try{
			line = input.readLine().replace('+', '\n');
		}
		catch(Exception e){
			close();
		}
		return line;
	}
	
	public void writeline(String line){
		output.write(line.replace('\n', '+') + "\n");
		output.flush();
	}
	
	public void close() throws IOException{
		socket.close();
		input.close();
		output.close();
	}
}
