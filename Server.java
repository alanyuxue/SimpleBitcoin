import java.net.*;
import java.io.*;
import javax.net.ssl.*;
public class Server extends Thread{
    private ServerSocket serverSocket;
    //private SSLServerSocket serverSocket;

    private Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        //serverSocket.setSoTimeout(10000);
        //SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        //serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port);
    }

    public void run() {
        System.out.println("Server started");
        while(true) {
            try {
                //System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
                Socket server = serverSocket.accept();
                //SSLSocket server = (SSLSocket) serverSocket.accept();

                //System.out.println("Just connected to " + server.getRemoteSocketAddress());
                DataInputStream in = new DataInputStream(server.getInputStream());

                System.out.println(in.readUTF());
                calculateProofOfWork();

                DataOutputStream out = new DataOutputStream(server.getOutputStream());
                out.writeUTF("Approved! Alice has sent Bob $50.");
                server.close();

            } /*catch(SocketTimeoutException s) {
                System.out.println("Socket timed out!");
                break;
            } */catch(IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private void calculateProofOfWork() {
        System.out.println("Calculating proof of work ...");
        System.out.println("Approved! Sending message to Alice and Bob.");
    }

    public static void main(String[] args) {
        int portA = 1234; //Integer.parseInt(args[0]);
        try {
            Thread t1 = new Server(portA);
            t1.start();
        } catch(IOException e) {
            e.printStackTrace();
        }

        int portB = 5678;
        try {
            Thread t2 = new Server(portB);
            t2.start();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}

