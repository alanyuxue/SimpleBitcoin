import java.io.*;
import java.net.*;
import javax.net.ssl.*;

public class ClientAlice {
    public static void main(String [] args) {
        String serverName = "localhost"; //args[0];
        int port = 1234; //Integer.parseInt(args[1]);
        try {
            System.out.println("Connecting to Server");
            Socket client = new Socket(serverName, port);
            //SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            //SSLSocket client = (SSLSocket) sslSocketFactory.createSocket(serverName, port);

            System.out.println("Sending message to Server");
            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);

            out.writeUTF("Message from Alice: Alice is sending Bob $50.");
            InputStream inFromServer = client.getInputStream();
            DataInputStream in = new DataInputStream(inFromServer);

            System.out.println(in.readUTF());
            client.close();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
}
