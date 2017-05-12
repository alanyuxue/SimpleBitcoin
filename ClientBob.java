import java.io.*;
import java.net.*;
import javax.net.ssl.*;

public class ClientBob {
    public static void main(String [] args) {
        String serverName = "localhost"; //args[0];
        int port = 5678; //Integer.parseInt(args[1]);
        try {
            System.out.println("Connecting to Server");
            Socket client = new Socket(serverName, port);
            //SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            //SSLSocket client = (SSLSocket) sslSocketFactory.createSocket(serverName, port);

            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);
            out.writeUTF("");

            System.out.println("Receiving message from Server.");
            InputStream inFromServer = client.getInputStream();
            DataInputStream in = new DataInputStream(inFromServer);

            System.out.println(in.readUTF());
            client.close();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
}
