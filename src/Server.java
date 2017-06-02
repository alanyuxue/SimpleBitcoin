import java.io.*;
import javax.net.ssl.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class Server{

    private ArrayList<ClientThread> clientList;
    private SSLServerSocket serverSocket;

    private Server(int portNumber) {
        System.setProperty("javax.net.ssl.keyStore", "serverkeystore");
        System.setProperty("javax.net.ssl.keyStorePassword","123456");

        try {
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
        }
    }

    private class ClientThread extends Thread{
        private InputStream in;    //used to store input stream from socket
        private BufferedReader readIn;    //used to read input stream
        private PrintWriter out;    //used to output stream
        private Server server;
        private SSLSocket clientSocket;

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
            boolean transferRequest = true;
	        int transferAmount = 0;
	        boolean initialise = true;
            String tHash = "0";
            while (true){
		        try {
		            messageReceived = readIn.readLine();
			        System.out.println("\nNew message received: " + messageReceived);
			        String[] splitMessage = messageReceived.split(" timestamp: ");
                    if (messageReceived.equalsIgnoreCase("QUIT")) {
                        out.println("connection closed");
                        out.flush();
                        System.out.println("Connection is closed.");
                        clientSocket.close();
                        removeFromClientList(this);
                        return;
                    } else {
                        if (transferRequest){
                            messageReceived = readIn.readLine();
                            String senderName = messageReceived + "Miner";

                            // set initial balance for sender,
                            // the amount must match with the digital wallet, otherwise the verification will fail
                            // use boolean initialise to make sure the balance will only be initialised once.
                            if (initialise) {
                                int initialBalance = 1000;
                                Miner.saveClientAccounts(senderName, Miner.readFinalBalance(senderName)+initialBalance,tHash);
                                initialise = false;
                            }
                            messageReceived = readIn.readLine();
                            String receiverName = messageReceived + "Miner";

                            System.out.printf("Transfer from: "+senderName + " to " + receiverName + "\n");
                            System.out.println("Transfer request received, processing...");
                            out.println("Processing your transaction. Please wait...");
                            out.flush();

                            //message must be a number, checked in client's code
                            String message = splitMessage[0];
                            String timestamp = splitMessage[1];
                            Miner miner = new Miner(message, timestamp);
                            transferAmount = Integer.parseInt(message);

                            try{
                                Miner.getPreviousHash(senderName);  // gets the previous hash of the sender's ledger from the server (just in case if there are more than 2 accounts transfering money). ***because we aren't doing a real block chain we may not need to have this***
                                tHash = miner.proof();
                                Miner.saveClientAccounts(senderName, (Miner.readFinalBalance(senderName) - transferAmount), tHash);
                                Miner.saveClientAccounts(receiverName, (Miner.readFinalBalance(receiverName) + transferAmount), tHash);
                                System.out.println("\nTransaction Successful amount: " + transferAmount);
                                server.broadcast("Transaction Successful amount: " + transferAmount);
                                transferRequest = false;
                            } catch (NoSuchAlgorithmException e){
                                e.printStackTrace();
                            }
                        }
                        if (!transferRequest) {
                            messageReceived = readIn.readLine();
                            int allegedBalance = Integer.parseInt(messageReceived);
                            System.out.printf("Verification request received of %d\n", allegedBalance);
                            messageReceived = readIn.readLine();
                            System.out.printf("Verification for account: %s\n",messageReceived);
                            String fullAccountName = messageReceived + "Miner";
                            if(Miner.accountBalanceVerification(fullAccountName, allegedBalance)){
                                System.out.printf("Verification of %d balance is correct\n", allegedBalance);
                                server.broadcast("Verification Complete");
                            } else {
                                System.out.printf("Verification Failure, balance has been altered\n");
                                server.broadcast("Verification Failed");
                            }
                            transferRequest = true;
                        }
                    }
		        } catch (IOException e) {
		            e.printStackTrace();
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
