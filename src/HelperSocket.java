import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

//A simple connection class that can be used to send/receive strings
public class HelperSocket {
	private Socket socket;
	private BufferedReader input;
	private PrintWriter output;
	
	HelperSocket(Socket socket) throws Exception {
		this.socket = socket;
		input = new BufferedReader( new InputStreamReader(socket.getInputStream()));
		output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
	}
	
	String readln() {
		String line = new String();
		try {
			line = input.readLine().replace('+', '\n');
		} catch(Exception e) {
			close();
		}
		
		return line;
	}
	
	void writeln(String line)
	{
		output.write(line.replace('\n', '+') + "\n");
		output.flush();
	}
	
	public void close() {
		try {
			socket.close();
			output.close();
			input.close();
		} catch(Exception e) {

		}
	}
}
