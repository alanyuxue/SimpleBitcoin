import java.io.*;
//import java.net.*;
import javax.net.ssl.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class Server{

    private ArrayList<ClientThread> clientList;
    //private ServerSocket serverSocket;
    private SSLServerSocket serverSocket;

    private Server(int portNumber) {
        System.setProperty("javax.net.ssl.keyStore", "serverkeystore");
        System.setProperty("javax.net.ssl.keyStorePassword","123456");

        try {
            //serverSocket = new ServerSocket(portNumber);
            SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(portNumber);
            clientList = new ArrayList<ClientThread>();
            System.out.println("\nServer started.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //create a thread for every client and add it to clientList
        while(true) {
            try{
                //Socket clientSocket = serverSocket.accept();
                SSLSocket clientSocket = (SSLSocket) serverSocket.accept();

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
        //private Socket clientSocket;
        private SSLSocket clientSocket;

        //private ClientThread (Socket socket, Server server) {
        public ClientThread (SSLSocket socket, Server server) {
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
            //boolean transferRequest = true;
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
                    } else {//if (transferRequest){
                        System.out.println("Transfer request received, processing...");
                        out.println("Processing your transaction. Please wait...");
                        out.flush();

                        //miner operations
                        String[] splitMessage = messageReceived.split(" timestamp: ");
                        //message must be a number, checked in client's code
                        String message = splitMessage[0];
                        String timestamp = splitMessage[1];
                        Miner miner = new Miner(message, timestamp);
                        int transferAmount = Integer.parseInt(message);
                        try{
                            miner.proof();
                            Miner.saveClientAccounts("aliceAccountMiner", (Miner.readFinalBalance("aliceAccountMiner") - transferAmount));
                            Miner.saveClientAccounts("bobAccountMiner", (Miner.readFinalBalance("bobAccountMiner") + transferAmount));
                            System.out.println("\nTransaction Successful amount: " + transferAmount);
                            server.broadcast("Transaction Successful amount: " + transferAmount);

                            //transferRequest = false;
                        } catch (NoSuchAlgorithmException e){
                            e.printStackTrace();
                            return;
                        }
                    }
                    /*else {
                        System.out.print("Verification request received\n");
                        //int allegedBalance = Integer.parseInt(messageReceived);
                        if(Miner.accountBalanceVerification("aliceAccountMiner", transferAmount)){
                            System.out.printf("Verification of $%d balance is correct\n", allegedBalance);
                            server.broadcast("Verification Complete");
                        }
                        else{
                            System.out.printf("Verification Failure, balance has been altered\n");
                            server.broadcast("Verification Failed");
                        }
                        transferRequest = true;
                    }*/
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    public static void main(String[] args){

        if(args.length != 1) {
            System.err.println("Usage: java Server <port number>");
            System.exit(1);
        }
        Server s = new Server(Integer.parseInt(args[0]));
    }
}