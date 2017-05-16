import java.io.*;
import java.net.*;
//import javax.net.ssl.*;

public class Bob {

    /**
     * Bob receives messages and update his ledger.
     * @param args server host name and port number
     * @throws IOException
     */
    public static void main(String[] args) throws IOException{
        /*
        if (args.length != 2) {
            System.err.println("Usage: java Bob <host name> <port number>");
            System.exit(1);
        }*/
        String hostName = "localhost"; //args[0];
        int portNumber = 1234; //Integer.parseInt(args[1]);

        try {
            Socket socket = new Socket(hostName, portNumber);
            //Bob never sends any message to server.
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Server connected. Please wait for new transactions.");
            while(true) {
                System.out.println("Server: " + in.readLine());
            }

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        }
    }
}
