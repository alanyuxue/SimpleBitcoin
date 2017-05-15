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
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Server connected. Please wait for new transactions.");

            String userInput;
            while((userInput = stdIn.readLine()) != null) {

                if (!userInput.equalsIgnoreCase("QUIT")){
                    System.out.println("Server: " + in.readLine());
                } else {
                    out.println("QUIT");
                    System.out.println("Server: " + in.readLine());
                }
                /*
                //check whether the input is a number
                else if (userInput.matches("[-+]?\\d*\\.?\\d+")) {
                    System.out.println("You are sending " + userInput + " chriscoins to Bob.");
                    //add unix timestamp to message
                    out.println("Alice is sending "+ userInput +" chriscoins to Bob timestamp: "+ System.currentTimeMillis() / 1000L);

                } else {
                    System.out.println("Invalid input. Please enter a number or QUIT to disconnect from server.");
                }*/
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
