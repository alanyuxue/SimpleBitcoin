import java.io.*;
import java.net.*;
import javax.net.ssl.*;

public class Alice {

    /**
     * Alice sends the amount from user input and update her ledger.
     * @param args server host name and port number
     * @throws IOException cannot find host name or port number
     */
    public static void main(String[] args) throws IOException{

        if (args.length != 2) {
            System.err.println("Usage: java Alice <host name> <port number>");
            System.exit(1);
        }
        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        System.setProperty("javax.net.ssl.trustStore", "serverkeystore");
        System.setProperty("javax.net.ssl.trustStorePassword", "123456");
        Account aliceAccount = new Account("AliceAccount",100);

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
            System.out.println("Your current balance is "+aliceAccount.readFinalBalance());
            String userInput;
            while((userInput = stdIn.readLine()) != null) {

                if (userInput.equalsIgnoreCase("QUIT")){
                    out.println("QUIT");
                    System.out.println("Server: " + in.readLine());
                }
                //check whether the input is a number
                else if (userInput.matches("[-+]?\\d*\\.?\\d+")) {
                    System.out.println("You are sending " + userInput + " Bitcoins to Bob.");
                    //add unix timestamp to message
                    out.println(userInput +" timestamp: "+ System.currentTimeMillis() / 1000L);
                    //wait for server responding to the message
                    System.out.println("Server: " + in.readLine());
                    //wait for transaction result from server
                    String receivedMessage = in.readLine();
                    String isSuccessful = receivedMessage.split(" amount: ")[0];
                    //remove all the Unicode characters in the string to make sure .equals() method works
                    isSuccessful = isSuccessful.replaceAll("\\P{Print}","");

                    System.out.println("Server: " + receivedMessage);
                    if(isSuccessful.equals("Transaction Successful")){
                        //DigitalWallet.transferFunds(DigitalWallet.getAliceAccount(), DigitalWallet.getBobAccount(), Integer.parseInt(userInput));
                        aliceAccount.sendMoney(Integer.parseInt(userInput));
                        System.out.println("Your current balance is "+aliceAccount.readFinalBalance());
                    }

                    /*
                    System.out.println("Beginning Verification Process\n");
                    out.println(aliceAccount.readFinalBalance());
                    System.out.println(in.readLine());
                    */
                } else {
                    System.out.println("Invalid input. Please enter a number or QUIT to disconnect from server.");
                }
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
