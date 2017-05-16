import java.io.*;
import java.net.*;
//import javax.net.ssl.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class Server{

    private ArrayList<ClientThread> clientList;
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

                ClientThread newClient = new ClientThread(clientSocket, this);
                newClient.setDaemon(true);
                newClient.start();
                addToClientList(newClient);
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private synchronized void addToClientList(ClientThread client) {
        clientList.add(client);
    }
    private synchronized void removeFromClientList (ClientThread client) {
        clientList.remove(client);
    }
    private synchronized void broadcast(String message) {
        for(ClientThread client: clientList) {
            client.out.println(message);
            //client.out.flush();
        }
    }

    private class ClientThread extends Thread{
        private InputStream in;    //used to store input stream from socket
        private BufferedReader readIn;    //used to read input stream
        private PrintWriter out;    //used to output stream
        private Server server;
        private Socket clientSocket;
        //SSLSocket clientSocket;

        private ClientThread (Socket socket, Server server) {
        //public clientThread (SSLSocket socket, Server server) {
            super("ClientThread");
            this.server = server;
            clientSocket = socket;
        }

        public void run() {
            try {
                System.out.println("\nNew client connected.");
                in = clientSocket.getInputStream();
                readIn = new BufferedReader(new InputStreamReader(in));
                out = new PrintWriter(clientSocket.getOutputStream(), true);
            } catch(IOException e) {
                e.printStackTrace();
                return;
            }

            String messageReceived;
            while (true) {
                try {
                    messageReceived = readIn.readLine();
                    System.out.println("\nNew message received: " + messageReceived);
                    if (messageReceived.equalsIgnoreCase("QUIT")) {
                        out.println("connection closed");
                        out.flush();
                        System.out.println("Connection is closed.");
                        clientSocket.close();
                        removeFromClientList(this);
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
                            server.broadcast("Transaction approved! " + messageReceived);
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