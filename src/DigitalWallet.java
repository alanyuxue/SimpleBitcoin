import java.io.*;
import java.net.*;
import javax.net.ssl.*;

public class DigitalWallet {

    public static void main(String[] args) throws IOException{
        boolean isSender = false;
        String senderName = "";
        String receiverName = "";

        if(args.length == 4) {
            senderName = args[2];
            receiverName = args[3];
            isSender = true;
        }
        else if(args.length == 3) {
            senderName = "";
            receiverName = args[2];
        }
        else {
            System.err.println("Usage if Sender: java DigitalWallet <host name> <port number> <sender's name> <receiver's name>");
            System.err.println("Usage if Receiver: java DigitalWallet <host name> <port number> <receiver's name>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        System.setProperty("javax.net.ssl.trustStore", "serverkeystore");
        System.setProperty("javax.net.ssl.trustStorePassword", "123456");

        if(isSender){
            runSender(hostName, portNumber, senderName, receiverName);
        }
        else{
            runReceiver(hostName, portNumber, receiverName);
        }
    }

    private static void runSender(String hostName, int portNumber, String senderName, String receiverName){

        Account senderAccount = new Account(senderName);

        // set initial balance for sender,
        // the amount must match with the server, otherwise the verification will fail

        int initialBalance = 1000;
        senderAccount.receiveMoney(initialBalance);

        try {
            //Socket socket = new Socket(hostName, portNumber);
            SSLSocketFactory sslSocket = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket socket = (SSLSocket) sslSocket.createSocket(hostName, portNumber);
            socket.setEnabledProtocols(new String[]{"SSLv3", "TLSv1"});
            socket.startHandshake();

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Server connected. Please enter the amount to transfer or QUIT to disconnect from server. \n");
            int balance;

            String userInput;
            balance = senderAccount.readFinalBalance();
            System.out.println("Your current balance is "+ balance);

            while((userInput = stdIn.readLine()) != null) {

                if (userInput.equalsIgnoreCase("QUIT")){
                    out.println("QUIT");
                    System.out.println("Server: " + in.readLine());
                }
                //check whether the input is a number
                else if (userInput.matches("[-+]?\\d*\\.?\\d+")) {
                    //check balance
                    int amountSent = Integer.parseInt(userInput);
                    if (amountSent <= balance && amountSent >=0) {
                        System.out.println("You are sending " + userInput + " Bitcoins to Bob.");

                        //add unix timestamp to message
                        out.println(userInput +" timestamp: "+ System.currentTimeMillis() / 1000L);

                        out.println(senderName);
                        out.println(receiverName);

                        //wait for server responding to the message
                        System.out.println("Server: " + in.readLine());
                        //wait for transaction result from server
                        String receivedMessage = in.readLine();
                        String isSuccessful = receivedMessage.split(" amount: ")[0];
                        //remove all the Unicode characters in the string to make sure .equals() method works
                        isSuccessful = isSuccessful.replaceAll("\\P{Print}","");

                        System.out.println("Server: " + receivedMessage);
                        if(isSuccessful.equals("Transaction Successful")){

                            senderAccount.sendMoney(Integer.parseInt(userInput));
                            System.out.println("Your current balance is "+senderAccount.readFinalBalance());

                        }
                        //verification
                        System.out.println("Verifying Transaction. Please wait...\n");
                        out.println(senderAccount.readFinalBalance());
                        out.println(senderName);
                        String verificationMessage = in.readLine();
                        System.out.println(verificationMessage);
                    } else {
                        System.out.println("Error: input is less than 0 or not enough balance");
                    }
                    balance = senderAccount.readFinalBalance();
                    System.out.println("Your current balance is "+ balance);
                }
                else {
                    System.out.println("Invalid input. Please enter a number or QUIT to disconnect from server.");
                }
            }
        }
        catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        }
        catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        }
    }

    private static void runReceiver(String hostName, int portNumber, String receiverName){

        Account receiverAccount = new Account(receiverName);
        try {
            SSLSocketFactory sslSocket = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket socket = (SSLSocket) sslSocket.createSocket(hostName, portNumber);
            socket.setEnabledProtocols(new String[]{"SSLv3", "TLSv1"});
            socket.startHandshake();
            //Bob never sends any message to server.
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Server connected. Please wait for new transactions.");

            while(true) {
                System.out.println("Your current balance is "+receiverAccount.readFinalBalance());
                String receivedMessage = in.readLine();

                String isSuccessful = receivedMessage.split(" amount: ")[0];
                //remove all the Unicode characters in the string to make sure .equals() method works
                isSuccessful = isSuccessful.replaceAll("\\P{Print}","");

                int transferAmount = Integer.parseInt(receivedMessage.split(" amount: ")[1]);
                System.out.println("Server: " + receivedMessage);

                if (isSuccessful.equals("Transaction Successful")) {
                    receiverAccount.receiveMoney(transferAmount);
                }
                String verificationMessage = in.readLine();
                System.out.println(verificationMessage);
            }
        }
        catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        }
        catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        }

    }
}