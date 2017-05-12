import java.net.Socket;
import java.net.ServerSocket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.DataOutputStream;
import java.security.KeyPairGenerator;

public class server {
  	public static void main(String[] args) throws IOException {
/*		KeyPair pair = keyGen.generateKeyPair();
		Private pri = pair.getPrivate();
		Public pub = pair.getPublic();        
*/
		if (args.length != 1) {
		    System.err.println("Usage: java server <port number>");
		    System.exit(1);
		}
		
		int portNumber = Integer.parseInt(args[0]);
		ServerSocket serverSocket = null;
		Socket clientSocket = null;
		
		try {            
			serverSocket = new ServerSocket(Integer.parseInt(args[0]));
		} catch (IOException e) {
			e.printStackTrace();
		}
		while (true) {
			try {
				clientSocket = serverSocket.accept();     
			} catch (IOException e) {
				e.printStackTrace();
			}
			new clientThread(clientSocket).start();
		}
    	} 
}

class clientThread extends Thread {
	Socket socket;
	
	public clientThread (Socket clientSocket) {
		this.socket = clientSocket;
	}
	
	public void run() {
		InputStream in = null;//used to store input stream from socket
		BufferedReader readIn = null;//used to read input stream
		PrintWriter out = null;//used to output stream
		try {
			in = socket.getInputStream();
			readIn = new BufferedReader(new InputStreamReader(in));
			out = new PrintWriter(socket.getOutputStream(), true);
		} catch(IOException e) {
			e.printStackTrace();
			return;
		}

		String line;
		while (true) {
		System.out.println("in while ");
			try {
				line = readIn.readLine();
				if (line.equalsIgnoreCase("QUIT")) {
					out.println("connection closed");
					out.flush();
					socket.close();
					return;
				}
				// true for a number
				else if (checkStr(line)==true) {
					//wallet operations
					System.out.println("checked the number ");
					out.println("You entered a number");
					out.flush();
				}
				else {
					out.println("Please enter a number");
					out.flush();
				}
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
	}

	// return false if it is a string
	public boolean checkStr(String str) {
		return str.matches("[-+]?\\d*\\.?\\d+");
	}
}

