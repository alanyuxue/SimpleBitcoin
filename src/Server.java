import java.io.*;
import java.net.*;
//import javax.net.ssl.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class Server{

    ArrayList<ClientThread> clientList;
    private ServerSocket serverSocket;
    //SSLServerSocket serverSocket;

    private Server(int portNumber) {
        try {
            serverSocket = new ServerSocket(portNumber);
            //SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            //serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(portNumber);
            clientList = new ArrayList<ClientThread>();
            System.out.println("\nServer started.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //create a thread for every client and add it to clientList
        while(true) {
            try{
                Socket clientSocket = serverSocket.accept();
                //SSLSocket clientSocket = (SSLSocket) serverSocket.accept();

                ClientThread newClient = new ClientThread(clientSocket);
                newClient.start();
                clientList.add(newClient);
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private void broadcast(String message) {
        for(ClientThread client: clientList) {
            client.out.println(message);
            client.out.flush();
        }
    }

    private class ClientThread extends Thread{
        private InputStream in;    //used to store input stream from socket
        private BufferedReader readIn;    //used to read input stream
        private PrintWriter out;    //used to output stream
        private Socket clientSocket;
        //SSLSocket clientSocket;

        private ClientThread (Socket socket) {
            //public clientThread (SSLSocket socket) {
            clientSocket = socket;
            System.out.println("\nNew client connected.");
        }

        public void run() {
            try {
                in = clientSocket.getInputStream();
                readIn = new BufferedReader(new InputStreamReader(in));
                out = new PrintWriter(clientSocket.getOutputStream(), true);
            } catch(IOException e) {
                e.printStackTrace();
                return;
            }

            String messageReceived;
            while (true) {
                //System.out.println("in while loop");
                try {
                    messageReceived = readIn.readLine();
                    System.out.println("\nNew message received: " + messageReceived);
                    if (messageReceived.equalsIgnoreCase("QUIT")) {
                        out.println("connection closed");
                        out.flush();
                        System.out.println("Connection is closed.");
                        clientSocket.close();
                        return;
                    } else {
                        System.out.println("Calculating proof of work...");
                        out.println("Processing your transaction. Please wait...");
                        out.flush();

                        //miner operations
                        String[] splitMessage = messageReceived.split(" timestamp: ");
                        String message = splitMessage[0];
                        String timestamp = splitMessage[1];
                        Miner miner = new Miner(message, timestamp);
                        try{
                            miner.proof();
                        } catch (NoSuchAlgorithmException e){
                            e.printStackTrace();
                            return;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    public static void main(String[] args){
        /*
        if(args.length != 1) {
            System.err.println("Usage: java Server <port number>");
            System.exit(1);
        } */
        Server s = new Server(1234);
    }
}