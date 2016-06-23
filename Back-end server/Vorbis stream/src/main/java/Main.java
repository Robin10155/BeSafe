import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 * Created by robin on 22/5/16.
 */
public class Main {
    public static void main(String[] args){
        new Main().run();
    }

    private void run() {
        String property = System.getProperty("java.library.path");
        StringTokenizer parser = new StringTokenizer(property, ";");
        while (parser.hasMoreTokens()) {
            System.err.println(parser.nextToken());
        }
        GatewayConnection gatewayConnection=new GatewayConnection();
        new Thread(gatewayConnection).start();
        ServerSocket welcomeSocket = null;
        try {
            welcomeSocket = new ServerSocket(3490);
            long count=0;
            while(true){
                Socket connectionSocket = welcomeSocket.accept();
                ConnectionHandler connectionHandler=new ConnectionHandler(connectionSocket,"/home/robin/audioData.fifo");
                count++;
                new Thread(connectionHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
