import java.io.*;
import java.net.*;
import javax.net.ssl.*;

public class Bob {

    /**
     * Bob receives messages and update his ledger.
     * @param args server host name and port number
     * @throws IOException cannot find host name or port number
     */
    public static void main(String[] args) throws IOException{

        if (args.length != 2) {
            System.err.println("Usage: java Bob <host name> <port number>");
            System.exit(1);
        }
        String hostName = "localhost"; //args[0];
        int portNumber = 1234; //Integer.parseInt(args[1]);

        System.setProperty("javax.net.ssl.trustStore", "serverkeystore");
        System.setProperty("javax.net.ssl.trustStorePassword", "123456");

        Account bobAccount = new Account("BobAccount", 0);
        try {
            SSLSocketFactory sslSocket = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket socket = (SSLSocket) sslSocket.createSocket(hostName, portNumber);
            socket.setEnabledProtocols(new String[]{"SSLv3", "TLSv1"});
            socket.startHandshake();
            //Bob never sends any message to server.
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Server connected. Please wait for new transactions.");
            while(true) {
                String receivedMessage = in.readLine();
                String isSuccessful = receivedMessage.split(" amount: ")[0];
                int transferAmount = Integer.parseInt(receivedMessage.split(" amount: ")[1]);
                System.out.println("Server: " + receivedMessage);
                if (isSuccessful.equals("Transfer Successful")) {
                    bobAccount.receiveMoney(transferAmount);
                }
                /*
                System.out.println("Beginning Verification Process for Alice\n");
                System.out.println(in.readLine());
                */
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
